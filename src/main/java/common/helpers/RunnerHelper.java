package common.helpers;

import org.openqa.selenium.ElementNotInteractableException;

public class RunnerHelper {
    public static void retryIfClickIntercepted(Runnable runnable) {
        try {
            runnable.run();
        } catch (ElementNotInteractableException e) {
            LogHelper.info("Click failed, retry...");
            retryIfClickIntercepted(runnable);
        }
    }
}
