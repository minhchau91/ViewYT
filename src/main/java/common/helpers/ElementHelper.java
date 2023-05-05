package common.helpers;

import common.Common;
import common.Constant;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class ElementHelper {
    public static WebElement getElement(By locator) {
        return BrowserHelper.getDriver().findElement(locator);
    }

    public static void waitElementExist(By locator, int seconds) {
        try {
            WebDriverWait wait = new WebDriverWait(BrowserHelper.getDriver(), seconds);
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (Exception e) {
            LogHelper.debug(String.format("Wait for element with locator: %s visible unsuccessfully.\nError: %s", locator.toString(), e.getMessage()));
        }
    }

    public static void waitElementClickable(By locator, int seconds) {
        try {
            WebDriverWait wait = new WebDriverWait(BrowserHelper.getDriver(), seconds);
            wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (Exception e) {
            LogHelper.debug(String.format("Wait for element with locator: %s clickable unsuccessfully.\nError: %s", locator.toString(), e.getMessage()));
        }
    }

    public static void waitElementClickable(By locator) {
        waitElementClickable(locator, Constant.DEFAULT_TIMEWAIT);
    }

    public static void waitElementExist(By locator) {
        waitElementExist(locator, Constant.DEFAULT_TIMEWAIT);
    }


    public static void waitElementDisappear(By locator, int seconds) {
        try {
            WebDriverWait wait = new WebDriverWait(BrowserHelper.getDriver(), seconds);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (Exception e) {
            LogHelper.debug(String.format("Wait for element with locator: %s not visible unsuccessfully.\nError: %s", locator.toString(), e.getMessage()));
        }
    }

    public static void selectDropdownOptionByText(WebElement element, String text) {
        Select dropdown = new Select(element);
        dropdown.selectByVisibleText(text);
    }

    public static void selectDropdownOptionByIndex(WebElement element, int index) {
        Select dropdown = new Select(element);
        dropdown.selectByIndex(index);
    }

    public static void scrollToView(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) BrowserHelper.getDriver();
        js.executeScript("arguments[0].scrollIntoView();", element);
    }

    public static void scrollDown(int scrollTime) {
        while (scrollTime-- > 0) {
            JavascriptExecutor js = (JavascriptExecutor) BrowserHelper.getDriver();
            js.executeScript(String.format("window.scrollBy(0,%s)", Common.getRandomNumber(200, 250)), "");
            Common.sleep(Common.getRandomNumber(2, 5));
        }
    }

    public static void scrollUpWithoutWait(int offset) {
        JavascriptExecutor js = (JavascriptExecutor) BrowserHelper.getDriver();
        js.executeScript(String.format("window.scrollBy(0,-%s)", offset), "");
    }

    public static void scrollUp(int scrollTime) {
        while (scrollTime-- > 0) {
            JavascriptExecutor js = (JavascriptExecutor) BrowserHelper.getDriver();
            js.executeScript(String.format("window.scrollBy(0,-%s)", Common.getRandomNumber(200, 250)), "");
            Common.sleep(Common.getRandomNumber(2, 5));
        }
    }

    public static void setAttribute(WebElement element, String attributeName, String value) {
        JavascriptExecutor js = (JavascriptExecutor) BrowserHelper.getDriver();
        js.executeScript(String.format("arguments[0].setAttribute('%s','%s');", attributeName, value), element);
    }

    public static boolean doesElementVisible(By locator) {
        try {
            return BrowserHelper.getDriver().findElement(locator).isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    public static String getDropDownSelectedOption(WebElement dropdown) {
        Select select = new Select(dropdown);
        return select.getFirstSelectedOption().getText();
    }

    public static void typeByActions(By locator, CharSequence... keys) {
        Actions actions = new Actions(BrowserHelper.getDriver());
        actions.click(getElement(locator))
                .keyDown(Keys.CONTROL)
                .sendKeys("a")
                .keyUp(Keys.CONTROL)
                .sendKeys(Keys.BACK_SPACE)
                .build()
                .perform();
        actions.sendKeys(keys).build().perform();
    }

    public static void hoverOn(By locator) {
        WebElement element = getElement(locator);
        Actions action = new Actions(BrowserHelper.getDriver());
        action.moveToElement(element).perform();
    }

    public static void clickByAction(By locator) {
        WebElement element = getElement(locator);
        clickByAction(element);
    }

    public static void clickByAction(WebElement element) {
        Actions action = new Actions(BrowserHelper.getDriver());
        action.moveToElement(element).click().perform();
    }

    public static String getAttribute(By locator, String attributeName) {
        return getElement(locator).getAttribute(attributeName);
    }
}
