package tests;

import common.Common;
import common.helpers.BrowserHelper;
import common.helpers.DataHelper;
import common.helpers.LogHelper;
import common.model.Email;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import page_objects.google.LoginPage;
import page_objects.google.MyActivitiesPage;

import java.util.List;

public class EnableDataCaptivate extends BaseTest {
    MyActivitiesPage myActivitiesPage = new MyActivitiesPage();
    LoginPage loginPage = new LoginPage();

    @Test(dataProvider = "emailList")
    public void TC001(Email email) {
        LogHelper.info("Turn on data captive for: " + email.getUsername());
        Common.setRunningEmail(email);
        BrowserHelper.startBrowser(browserType, "Profile 1", true);
        myActivitiesPage.open();
        loginPage.login(email);
        myActivitiesPage.setDataCaptiveStatus(true);
    }

    @DataProvider(name = "emailList", parallel = true)
    public Object[][] emailList() {
        List<Email> emailList = DataHelper.getEmailList();
        return emailList.stream()
                .map(email -> new Object[]{email})
                .toArray(Object[][]::new);
    }
}
