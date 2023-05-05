package common.helpers;

import common.Common;
import common.model.Email;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BrowserHelper {
    private static final int BROWSER_WIDTH = 600;
    private static final int BROWSER_HEIGHT = 500;
    private static final int DISTANT_WIDTH = 300;
    private static final int DISTANT_HEIGHT = 200;
    private static java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static Point currentPosition;
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final ThreadLocal<String> profileFolder = new ThreadLocal<>();
    //    private static WebDriver driver;
    private static final ThreadLocal<String> winHandleBefore = new ThreadLocal<>();
    private static boolean isFirstTime = true;

    public enum DriverType {CHROME, FIREFOX, EDGE}

    public static void navigateToUrl(String url) {
        driver.get().get(url);
    }

    public synchronized static void startBrowser(DriverType driverType, String profileName) {
        startBrowser(driverType, profileName, false);
    }

    private synchronized static Point getBrowserPosition() {
        if (currentPosition == null) {
            currentPosition = new Point(0, 0);
            return currentPosition;
        }
        int maxXAxis = screenSize.width - BROWSER_WIDTH;
        int maxYAxis = screenSize.height - BROWSER_HEIGHT;
        int xAxis = currentPosition.getX() + DISTANT_WIDTH;
        int yAxis = currentPosition.getY();
        if (xAxis > maxXAxis) {
            xAxis = 0;
            yAxis += DISTANT_HEIGHT;
            if (yAxis > maxYAxis) {
                yAxis = 0;
            }
        }
        currentPosition = new Point(xAxis, yAxis);
        return currentPosition;
    }


    public synchronized static void startBrowser(DriverType driverType, String profileName, boolean useProxy) {
        if (!isFirstTime) {
            Common.sleep(Common.getRunPropertyAsNumber("delayBetweenThread"));
        } else {
            isFirstTime = false;
        }
        String userProfile = Thread.currentThread().getContextClassLoader().getResource("profile").getPath().replace("/", "\\").replaceFirst("\\\\", "");
        Email runningEmail = Common.getRunningEmail();
        String newProfileName = runningEmail.getUsername().split("@")[0];
        String newProfileFolder = userProfile + "\\" + newProfileName;
        LogHelper.info("Running on profile folder: " + newProfileFolder);
        profileFolder.set(newProfileFolder);
        if (FileHelper.doesFileExist(newProfileFolder)) {
            FileHelper.copyFolder(userProfile + "\\" + profileName, newProfileFolder);
        }
        LogHelper.info(String.format("Start %s browser with Profile name: %s", driverType, newProfileName));
        switch (driverType) {
            case CHROME:
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments(String.format("user-data-dir=%s\\%s", userProfile, newProfileName));
//                options.addArguments(String.format("--profile-directory=%s", newProfileName));
                options.addArguments(String.format("user-agent=%s", DataHelper.getRandomUserAgent()));
                options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
                options.setExperimentalOption("useAutomationExtension", false);
                options.addArguments("--disable-blink-features=AutomationControlled");
                if (useProxy) {
                    String proxy = Boolean.parseBoolean(Common.getRunProperty("randomProxy"))
                            ? GoogleSheetHelper.getRandomProxy()
                            : GoogleSheetHelper.getProxyByEmailIdx();
                    String proxyFile = ProxyHelper.buildProxy(DataHelper.buildProxy(proxy));
                    options.addExtensions(new File(proxyFile));
                }
                driver.set(new ChromeDriver(options));
                break;
            case FIREFOX:
                WebDriverManager.firefoxdriver().setup();
                ProfilesIni profile = new ProfilesIni();
                FirefoxProfile myprofile = profile.getProfile("default-release");
                myprofile.setPreference("dom.webdriver.enabled", false);
                myprofile.setPreference("useAutomationExtension", false);
                FirefoxOptions fo = new FirefoxOptions();
                fo.setProfile(myprofile);
                driver.set(new FirefoxDriver(fo));
                break;
            case EDGE:
                WebDriverManager.iedriver().setup();
                driver.set(new EdgeDriver());
                break;
        }
//        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
//        driver.get().manage().window().maximize();
        driver.get().manage().window().setSize(new Dimension(BROWSER_WIDTH, BROWSER_HEIGHT));
        driver.get().manage().window().setPosition(getBrowserPosition());
        winHandleBefore.set(getWindowsList().get(0));
    }

    public static void setDriver(WebDriver webDriver) {
        driver.set(webDriver);
    }

    public static void quitBrowser() {
        if (driver.get() != null) {
            driver.get().quit();
            driver.set(null);
        }
    }

    public static void removeCurrentProfile() {
        FileHelper.removeFolder(profileFolder.get());
    }

    public static WebDriver getDriver() {
        return driver.get();
    }

    public static String getPageTitle() {
        return BrowserHelper.getDriver().getTitle().trim();
    }

    public static void switchToFrame(By locator) {
        getDriver().switchTo().frame(getDriver().findElement(locator));
    }

    public static void switchToDefaultFrame() {
        getDriver().switchTo().defaultContent();
    }

    public static void switchToNewWindows(int retry) {
        winHandleBefore.set(getDriver().getWindowHandle());
        List<String> winHandles = new ArrayList<>(getDriver().getWindowHandles());
        if (retry > 0 && winHandles.size() < 2) {
            Common.sleep(1);
            switchToNewWindows(--retry);
            return;
        }
        for (String winHandle : winHandles) {
            if (!winHandle.equals(winHandleBefore.get())) {
                driver.set(getDriver().switchTo().window(winHandle));
                return;
            }

        }
    }

    public static void waitForNewWindows(int timeout) {
        List<String> winHandles = new ArrayList<>(getDriver().getWindowHandles());
        while (winHandles.size() == 1 && timeout > 0) {
            Common.sleep(1);
            timeout--;
            winHandles = new ArrayList<>(getDriver().getWindowHandles());
        }
    }

    public static void switchToDefaultWindow() {
        if (winHandleBefore.get() != null)
            driver.set(getDriver().switchTo().window(winHandleBefore.get()));
    }

    public static void maximize() {
        getDriver().manage().window().maximize();
    }

    public static void closeOtherWindows() {
        List<String> winHandles = new ArrayList<>(getDriver().getWindowHandles());
        if (winHandleBefore.get() != null && winHandles.size() > 1) {
            for (String winHandle : winHandles) {
                if (!winHandle.equals(winHandleBefore.get())) {
                    driver.set(getDriver().switchTo().window(winHandle));
                    getDriver().close();
                    switchToDefaultWindow();
                }
            }
        }
    }

    public static void setAttribute(WebElement element, String attName, String attValue) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        LogHelper.info((String) js.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);",
                element, attName, attValue));
    }

    public static void setValue(WebElement element, String attValue) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        LogHelper.info((String) js.executeScript("(arguments[0])[arguments[1]] = arguments[2]",
                element, attValue));
    }

    @SneakyThrows
    public static String captureScreenshot(final String fileName, final String folderPath, final boolean deleteOnExit) {
        String path = folderPath + File.separator + fileName;
        LogHelper.info("Capturing screenshot and store into " + path);

        File objScreenCaptureFile = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.FILE);
        File dest = new File(path);
        FileUtils.moveFile(objScreenCaptureFile, dest);
        if (deleteOnExit) {
            dest.deleteOnExit();
        }
        return dest.getAbsolutePath();
    }

    public static void switchTo(final String windowHandle) {
        getDriver().switchTo().window(windowHandle);
    }

    public static List<String> getWindowsList() {
        return new ArrayList<>(getDriver().getWindowHandles());
    }

    public static String switchToNewWindow(final List<String> oldHandles) {
        List<String> latestHandles = getWindowsList();
        String newHandle = latestHandles.stream().filter(handle -> !oldHandles.contains(handle)).findFirst().get();
        switchTo(newHandle);
        return newHandle;
    }

    public static void closeOtherWindows(final List<String> oldHandles) {
        switchToNewWindow(oldHandles);
        getDriver().close();
        switchTo(oldHandles.get(0));
    }

    public static void refresh() {
        getDriver().navigate().refresh();
    }
}
