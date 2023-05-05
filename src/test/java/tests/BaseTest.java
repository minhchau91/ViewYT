package tests;

import common.Common;
import common.helpers.BrowserHelper;
import common.helpers.DataHelper;
import common.helpers.LogHelper;
import listener.RetryAnalyzer;
import org.testng.ITestContext;
import org.testng.annotations.*;

import java.util.Arrays;
import java.util.List;


public class BaseTest {
    BrowserHelper.DriverType browserType;

    @DataProvider(name = "profileList", parallel = true)
    public Object[][] profileList() {
        List<String> profileList = Arrays.asList(DataHelper.getProperty("profile").split(","));
        return profileList.stream()
                .map(profile -> new Object[]{"Profile " + profile})
                .toArray(Object[][]::new);
    }

    @BeforeMethod
    @Parameters({"browser"})
    public void beforeMethod(@Optional("firefox") String browser) {
        browserType = BrowserHelper.DriverType.valueOf(browser.toUpperCase());
    }

    @BeforeClass
    @Parameters({"retryCount", "rerunFailed", "threadCount"})
    public void beforeClass(ITestContext context, @Optional("") String retryCount, @Optional("") String rerunFailed, @Optional("") String threadCount) {
        retryCount = getParametersValue("retryCount", retryCount);
        LogHelper.info("Retry count: " + retryCount);
        RetryAnalyzer.setGlobalRetryCount(Integer.parseInt(retryCount));
        if (Boolean.parseBoolean(getParametersValue("rerunFailed", rerunFailed))) {
            DataHelper.overwriteEmailList();
        }
        threadCount = getParametersValue("threadCount", threadCount);
        LogHelper.info("Thread Count: " + threadCount);
        int numOfThreads = Integer.parseInt(threadCount);
        if (numOfThreads > 0) {
            context.getCurrentXmlTest().getSuite().setDataProviderThreadCount(numOfThreads);
        }
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        LogHelper.info("Post-condition");
        BrowserHelper.quitBrowser();
//        BrowserHelper.removeCurrentProfile();
    }

    protected static String getParametersValue(String parameterName, String parameterValue) {
        return Common.getRunProperty(parameterName, parameterValue);
    }
}
