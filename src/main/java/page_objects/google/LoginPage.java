package page_objects.google;

import common.Constant;
import common.helpers.ElementHelper;
import common.helpers.LogHelper;
import common.model.Email;
import org.openqa.selenium.By;
import page_objects.BasePage;

public class LoginPage extends BasePage {
    private final By btnSignIn = By.xpath("//a[contains(@href, 'https://accounts.google.com/ServiceLogin')]");
    private final By txtEmail = By.xpath("//input[@type='email']");
    private final By txtPassword = By.xpath("//input[@type='password']");
    private final By btnPasswordNext = By.cssSelector("#passwordNext button");
    private final By btnIdNext = By.cssSelector("#identifierNext button");
    private final By btnConfirmRecoveryEmail = By.xpath("//div[@data-challengeindex=\"2\"]");
    private final By btnNext = By.xpath("//div[@data-is-primary-action-disabled=\"false\"]//button");
    private final By txtPhoneNumber = By.id("phoneNumberId");

    public boolean login(String username, String pwd, String recoveryEmail) {
        ElementHelper.waitElementExist(btnSignIn, Constant.SHORT_WAIT);
        if (!ElementHelper.doesElementVisible(btnSignIn)) {
            LogHelper.info("Logged in.");
            return true;
        }

        getElement(btnSignIn).click();
        ElementHelper.waitElementExist(txtEmail);

        if (!ElementHelper.doesElementVisible(txtEmail)) {
            LogHelper.info("Logged in.");
            return true;
        }
        getElement(txtEmail).sendKeys(username);
        getElement(btnIdNext).click();
        ElementHelper.waitElementExist(txtPassword);
        getElement(txtPassword).sendKeys(pwd);
        getElement(btnPasswordNext).click();
        ElementHelper.waitElementExist(btnConfirmRecoveryEmail, Constant.SHORT_WAIT);
        if (!ElementHelper.doesElementVisible(btnConfirmRecoveryEmail)) {
            if (ElementHelper.doesElementVisible(txtPhoneNumber)) {
                return false;
            }
            LogHelper.info("Logged in.");
            return true;
        }

        getElement(btnConfirmRecoveryEmail).click();
        ElementHelper.waitElementExist(txtEmail);
        getElement(txtEmail).sendKeys(recoveryEmail);
        getElement(btnNext).click();
        return true;
    }

    public boolean login(Email email) {
        return login(email.getUsername(), email.getPassword(), email.getRecoveryEmail());
    }
}
