package page_objects.youtube_studio;

import common.Common;
import common.helpers.DateTimeHelper;
import common.helpers.ElementHelper;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import page_objects.BasePage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StudioVideoDetailsPage extends BasePage {
    By btnMonetization = By.id("menu-item-5");
    By btnManageMidRoll = By.id("place-manually-button");
    By txtTimeBreak = By.xpath("//div[@id='placeholder']/following-sibling::input");
    By btnDelete = By.cssSelector(".delete-button");
    By btnAddAdsBreak = By.id("add-ad-break");
    By btnPlaceAuto = By.id("place-automatically");
    By btnConfirm = By.id("confirm-button");
    By btnContinue = By.id("save-button");
    By btnDiscard = By.id("discard-button");
    By btnSave = By.xpath("//ytcp-button[@id='save']");

    public void addAdsBreak(int bufferTimeInMins) {
        waitAndClick(btnManageMidRoll);
        waitForElement(txtTimeBreak);
        if (!ElementHelper.doesElementVisible(txtTimeBreak)) {
            clickElement(btnPlaceAuto);
            waitForElement(btnConfirm);
            clickElement(btnConfirm);
        }
        List<WebElement> timeslotElements = getElements(txtTimeBreak);
        String lastBreak = timeslotElements.get(timeslotElements.size() - 1).getAttribute("value");
        List<String> breakTimeline = calculateBreakTimes(lastBreak, bufferTimeInMins);
        //number of break needed lesser or equals to number of existing slot
        if (breakTimeline.size() <= timeslotElements.size() + 2) {
            return;
        }
        removeAllBreak();
        for (String breakSlot : breakTimeline) {
            clickElement(btnAddAdsBreak);
            Common.sleep(1);
            ElementHelper.typeByActions(txtTimeBreak, breakSlot);
        }
        clickElement(btnContinue);
        clickElement(btnSave);
        Common.sleep(1);
    }

    private List<String> calculateBreakTimes(String lastSpotTime, int bufferTimeInSeconds) {
        String dateFormat = StringUtils.countMatches(lastSpotTime, ":") >= 3 ? "H:mm:ss:SS" : "mm:ss:SS";
        String firstSlot = StringUtils.countMatches(lastSpotTime, ":") >= 3 ? "0:03:00:00" : "03:00:00";

        String thresholdDate = DateTimeHelper.adjustTime(lastSpotTime, dateFormat, Calendar.MINUTE, -bufferTimeInSeconds);
        List<String> breakTimes = new ArrayList<>();
        String currentSlot = firstSlot;
        while (!DateTimeHelper.isAfterDate(currentSlot, thresholdDate, dateFormat)) {
            breakTimes.add(currentSlot);
            currentSlot = DateTimeHelper.adjustTime(currentSlot, dateFormat, Calendar.MINUTE, bufferTimeInSeconds);
        }
        return breakTimes;
    }

    public void removeAllBreak() {
        waitForElement(btnDelete);
        while (ElementHelper.doesElementVisible(btnDelete)) {
            clickElement(btnDelete);
        }
    }

}
