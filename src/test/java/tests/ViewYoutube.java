package tests;

import common.Common;
import common.helpers.*;
import common.model.Email;
import org.testng.ITestContext;
import org.testng.annotations.*;
import page_objects.google.LanguagePage;
import page_objects.google.LoginPage;
import page_objects.google.MyActivitiesPage;
import page_objects.youtube.HomePage;
import page_objects.youtube.MembershipPage;
import page_objects.youtube.MultiPageAction;

import java.util.List;

public class ViewYoutube extends BaseTest {
    MyActivitiesPage myActivitiesPage = new MyActivitiesPage();
    LoginPage loginPage = new LoginPage();
    HomePage ytbHome = new HomePage();
    MembershipPage membershipPage = new MembershipPage();
    LanguagePage languagePage = new LanguagePage();
    private boolean enableActivity;

    @Test(dataProvider = "emailList")
    @Parameters({"enableActivity"})
    public void TC001(Email email) {

        while (true) {
            MultiPageAction multiPageAction = new MultiPageAction();
            LogHelper.info("Turn on data captive for: " + email.getUsername());
            Common.setRunningEmail(email);
            BrowserHelper.startBrowser(browserType, "Profile 1", true);
            myActivitiesPage.open();
//            if (true)
//                throw new RuntimeException("Debug error");
            boolean isLoggedIn = loginPage.login(email);
            if (!isLoggedIn) {
                LogHelper.error("Login failed. Skip email");
                BrowserHelper.captureScreenshot(email.getUsername() + ".png", Common.failedFolderPath, false);
                return;
            }
            myActivitiesPage.setDataCaptiveStatus(enableActivity);
            languagePage.open();
            languagePage.changeToEnglish();
            ytbHome.openHomePage();
            loginPage.login(email);
            if (Common.getRunPropertyAsBoolean("checkExpire") && membershipPage.isMembershipExpired(email.getId())) {
                LogHelper.error(String.format("Account: %s is expired. Skip running", email.getUsername()));
                BrowserHelper.captureScreenshot(email.getUsername() + ".png", Common.failedFolderPath, false);
                return;
            }
            ytbHome.openHomePage();
            multiPageAction.watchHomePage();
            multiPageAction.watchYtbVideo();
            BrowserHelper.quitBrowser();
            GoogleSheetHelper.cleanUp();
            Common.sleep(Common.getRunPropertyAsNumber("sessionDelay") * 60);
        }
    }

    @BeforeClass
    @Parameters({"enableActivity"})
    public void setUp(@Optional("") String enableActivity) {
        enableActivity = getParametersValue("enableActivity", enableActivity);
        LogHelper.info("Enable activity: " + enableActivity);
        this.enableActivity = Boolean.parseBoolean(enableActivity);
    }

    @DataProvider(name = "emailList", parallel = true)
    public Object[][] emailList(ITestContext context) {
        String vpsId = XMLHelper.getPropertyValue("vpsId", context.getCurrentXmlTest());
        if (vpsId == null) {
            vpsId = DataHelper.getProperty("vpsId");
        }
        List<Email> emailList = GoogleSheetHelper.getEmailList(vpsId);
        return emailList.stream()
                .map(email -> new Object[]{email})
                .toArray(Object[][]::new);
    }
}
