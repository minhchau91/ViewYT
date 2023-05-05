package page_objects.google;

import common.Constant;
import common.helpers.BrowserHelper;
import common.helpers.ElementHelper;
import common.helpers.LogHelper;
import org.openqa.selenium.By;
import page_objects.BasePage;

public class MyActivitiesPage extends BasePage {
    private final By btnTurnOnDataCaptive = By.xpath("//div[@data-captive=\"false\"]//button[@data-is-on=\"false\"]");
    private final By btnTurnOffDataCaptive = By.xpath("//div[@data-captive=\"false\"]//button[@data-is-on=\"true\"]");
    private final By btnTurnOnPopup = By.xpath("(//div[@jsaction=\"focus:.CLIENT\"]/..//button)[last()]");
    private final By btnClose = By.xpath("//button[@data-disable-idom=\"true\" and i]");
    private final By dataLoadingMsg = By.xpath("//div[@data-loadingmessage]");
    private final By bottomLink = By.xpath("(//a[contains(@href, '.google.com')])[last()]");

    public void open() {
        BrowserHelper.navigateToUrl(Constant.MY_ACTIVITY);
    }

    public void setDataCaptiveStatus(boolean isEnabled) {
        ElementHelper.waitElementExist(btnTurnOnDataCaptive, Constant.SHORT_WAIT);
        if (ElementHelper.doesElementVisible(btnTurnOffDataCaptive)) {
            if (isEnabled) {
                LogHelper.info("Data captive already turned on.");
                return;
            } else {
                LogHelper.info("Turn Off Data captive.");
                clickElement(btnTurnOffDataCaptive);
            }
        } else if (isEnabled) {
            LogHelper.info("Turn On Data captive.");
            getElement(btnTurnOnDataCaptive).click();
        } else {
            LogHelper.info("Data captive already turned off.");
            return;
        }
        ElementHelper.waitElementClickable(btnTurnOnPopup);
        ElementHelper.waitElementExist(dataLoadingMsg, 3);
        if (ElementHelper.doesElementVisible(dataLoadingMsg))
            ElementHelper.waitElementDisappear(dataLoadingMsg, 5);
        ElementHelper.scrollToView(getElement(bottomLink));
        getElement(btnTurnOnPopup).click();
        ElementHelper.waitElementExist(btnClose);
        ElementHelper.waitElementExist(dataLoadingMsg, 2);
        ElementHelper.waitElementDisappear(dataLoadingMsg, 5);
        getElement(btnClose).click();

    }
}
