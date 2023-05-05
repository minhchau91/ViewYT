package page_objects;

import common.helpers.BrowserHelper;
import common.helpers.ElementHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebElement;

import java.util.List;

public class BasePage {
    //work with element
    public WebElement getElement(By locator) {
        return BrowserHelper.getDriver().findElement(locator);
    }

    public List<WebElement> getElements(By locator) {
        return BrowserHelper.getDriver().findElements(locator);
    }

    public void waitForElement(By locator, int timeout) {
        ElementHelper.waitElementExist(locator, timeout);
    }

    public void waitForElement(By locator) {
        ElementHelper.waitElementExist(locator, 10);
    }

    public void clickElement(By locator) {
        ElementHelper.scrollToView(getElement(locator));
        try {
            getElement(locator).click();
        } catch (ElementNotInteractableException e) {
            ElementHelper.scrollUpWithoutWait(200);
            getElement(locator).click();
        }
    }

    public void clickElementWithoutScroll(By locator) {
        getElement(locator).click();
    }

    public void waitAndClick(By locator) {
        waitForElement(locator);
        clickElement(locator);
    }
}
