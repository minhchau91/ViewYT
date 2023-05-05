package page_objects.youtube;

import common.Common;
import common.helpers.BrowserHelper;
import common.helpers.ElementHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import page_objects.BasePage;

import java.util.List;

public class HomePage extends BasePage {
    private static final String HOME_URL = "https://www.youtube.com/";

    private final By videosImg = By.xpath("//ytd-rich-grid-media");
    private final By btnSignIn = By.xpath("//a[contains(@href, 'ServiceLogin?service=youtube')]");

    public void openHomePage() {
        BrowserHelper.navigateToUrl(HOME_URL);
    }

    public void signIn() {
        waitForElement(btnSignIn, 5);
        if (ElementHelper.doesElementVisible(btnSignIn)) {
            clickElement(btnSignIn);
            Common.sleep(3);
        }
    }

    public void openRandomVideo() {
        waitForElement(videosImg);
        List<WebElement> elements = getElements(videosImg);
        if (elements.size() == 0) {
            BrowserHelper.getDriver().navigate().refresh();
            waitForElement(videosImg);
            elements = getElements(videosImg);
        }
        WebElement randomVideo = elements.get(Common.getRandomNumber(elements.size() - 1));
        ElementHelper.scrollToView(randomVideo);
        randomVideo.click();
    }
}
