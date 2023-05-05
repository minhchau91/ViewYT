package listener;

import common.Common;
import common.Constant;
import common.helpers.BrowserHelper;
import common.helpers.DateTimeHelper;
import common.helpers.FileHelper;
import common.helpers.LogHelper;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.ArrayList;
import java.util.List;

public class TestListener implements ITestListener {
    private static List<String> failedEmails = new ArrayList<>();

    @Override
    public void onTestStart(ITestResult iTestResult) {
    }

    @Override
    public void onTestSuccess(ITestResult iTestResult) {
        LogHelper.info("onTestSuccess");
//        if (Common.getRunningEmail() == null) return;
//        String runningEmail = Common.getRunningEmail().getUsername();
//        BrowserHelper.captureScreenshot(runningEmail + ".png", passedSSFolderPath, false);
    }

    @Override
    public void onTestFailure(ITestResult iTestResult) {
        LogHelper.error("onTestFailure");
        if (Common.getRunningVideo() != null) {
            String runningVideo = Common.getRunningVideo();
            String errorFilePath = BrowserHelper.captureScreenshot(runningVideo.split("/")[-3]
                    + DateTimeHelper.getCurrentTime("HHmmss") + ".png", Common.failedFolderPath, false);
            LogHelper.error(String.format("Failed video: %s. Screenshot: %s", runningVideo, errorFilePath));
        } else {
            String runningEmail = Common.getRunningEmail().getUsername();
            String recoveryPwd = Common.getRunningEmail().getRecoveryEmail();
            String errorFilePath = BrowserHelper.captureScreenshot(runningEmail + DateTimeHelper.getCurrentTime("HHmmss") + ".png", Common.failedFolderPath, false);
            LogHelper.error(String.format("Failed email: %s. Screenshot: %s", runningEmail, errorFilePath));
            failedEmails.add(String.format("%s\t%s\t%s", runningEmail, Common.getRunningEmail().getPassword(),
                    recoveryPwd == null ? "" : recoveryPwd));
        }
    }

    @Override
    public void onTestSkipped(ITestResult iTestResult) {
        onTestFailure(iTestResult);
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {

    }

    @Override
    public void onStart(ITestContext iTestContext) {
        String folderName = Common.generateDateAsString();
        Common.failedFolderPath = Constant.TEST_RESULT_FOLDER + "/" + folderName + "/" + "Failed";
        Common.passedFolderPath = Constant.TEST_RESULT_FOLDER + "/" + folderName + "/" + "Passed";
    }

    @Override
    public void onFinish(ITestContext iTestContext) {
        if (!failedEmails.isEmpty())
            FileHelper.writeToFile(failedEmails, Constant.FAILED_EMAILS_PATH);
    }
}
