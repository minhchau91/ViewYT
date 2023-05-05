package page_objects.google;

import common.Constant;
import common.helpers.BrowserHelper;
import common.helpers.ElementHelper;
import common.helpers.LogHelper;
import org.openqa.selenium.By;
import page_objects.BasePage;

public class LanguagePage extends BasePage {
    private By btnEditLanguage = By.cssSelector("c-wiz[jsdata='deferred-i2'] svg");
    private By txtSearchLanguage = By.id("c1");
    private By btnEnglish = By.cssSelector("li[aria-label=English]");
    private By btnUnitedState = By.cssSelector("li[aria-label='United States']");
    private By btnSelect = By.cssSelector("button[data-mdc-dialog-action=ok]");
    private By lblStatusUpdate = By.cssSelector("div[role=status]");
    private By lblEnglishLanguage = By.cssSelector("span[lang=en]");

    public void open() {
        BrowserHelper.navigateToUrl("https://myaccount.google.com/language");
    }

    public void changeToEnglish() {
        waitForElement(lblEnglishLanguage, Constant.VERY_SHORT_WAIT);
        if (ElementHelper.doesElementVisible(lblEnglishLanguage)) {
            LogHelper.info("English selected.");
            return;
        }
        waitAndClick(btnEditLanguage);
        waitForElement(txtSearchLanguage);
        getElement(txtSearchLanguage).sendKeys("English");
        waitAndClick(btnEnglish);
        waitAndClick(btnUnitedState);
        waitAndClick(btnSelect);
        waitForElement(lblStatusUpdate);
        assert ElementHelper.doesElementVisible(lblStatusUpdate);
    }
}
