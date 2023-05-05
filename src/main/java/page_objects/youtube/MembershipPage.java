package page_objects.youtube;

import common.helpers.BrowserHelper;
import common.helpers.DateTimeHelper;
import common.helpers.GoogleSheetHelper;
import org.openqa.selenium.By;
import page_objects.BasePage;

public class MembershipPage extends BasePage {
    By membershipEndDate = By.cssSelector("#container #card-item-text-collection-renderer #card-text span:last-child");

    public void open() {
        BrowserHelper.getDriver().navigate().to("https://www.youtube.com/paid_memberships");
    }

    public boolean isMembershipExpired(int mailIdx) {
        open();
        waitForElement(membershipEndDate);
        String memberShipDate = getElement(membershipEndDate).getText();
        GoogleSheetHelper.updateExpiredDate(mailIdx, memberShipDate);
        return DateTimeHelper.isDatePassed(memberShipDate, "MMM d");
    }
}
