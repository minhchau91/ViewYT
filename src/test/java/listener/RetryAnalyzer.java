package listener;

import common.Common;
import common.helpers.LogHelper;
import lombok.extern.slf4j.Slf4j;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

@Slf4j
public class RetryAnalyzer implements IRetryAnalyzer {
    private static int globalRetryCount;
    private static ThreadLocal<Integer> retryCount;


    public synchronized int getRetryCount() {
        LogHelper.info("Get retry count for test name: " + Common.getRunningEmail().getUsername());
        if (retryCount == null) {
            retryCount = new ThreadLocal<>();
        }
        if (retryCount.get() == null) {
            retryCount.set(globalRetryCount);
        }
        return retryCount.get();
    }

    public static void setGlobalRetryCount(int retryCount) {
        globalRetryCount = retryCount;
    }

    public static void setRetryCount(int retryTime) {
        if (retryCount == null) {
            retryCount = new ThreadLocal<>();
        }
        retryCount.set(retryTime);
    }

    /*
     * (non-Javadoc)
     * @see org.testng.IRetryAnalyzer#retry(org.testng.ITestResult)
     *
     * This method decides how many times a test needs to be rerun.
     * TestNg will call this method every time a test fails. So we
     * can put some code in here to decide when to rerun the test.
     *
     * Note: This method will return true if a tests needs to be retried
     * and false if it doesn't.
     *
     */
    @Override
    public boolean retry(final ITestResult testResult) {
        if (globalRetryCount == 0) {
            return false;
        }
        boolean needRetry = this.getRetryCount() > 0;
        if (!needRetry) {
            this.retryCount.remove();
        } else {
            this.retryCount.set(this.retryCount.get() - 1);
        }
        if (needRetry) {
            LogHelper.info("Retry test case, retry time left: " + this.getRetryCount());
        } else {
            LogHelper.info("Retry no longer needed. Stop playing.");
        }
        return needRetry;
    }
}
