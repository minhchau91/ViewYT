package page_objects.youtube_studio;

import common.helpers.BrowserHelper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import page_objects.BasePage;

import java.util.ArrayList;
import java.util.List;

public class StudioContentPage extends BasePage {
    String baseUrl = "https://studio.youtube.com/";
    By btnContent = By.xpath("//a[@id='menu-item-1']");
    By videoTab = By.id("video-list-uploads-tab");
    By liveTab = By.id("video-list-live-tab");
    By videos = By.cssSelector("#video-list #video-title");

    public void clickContentButton() {
        waitAndClick(btnContent);
    }

    public List<String> getVideoList() {
        List<String> videoList = new ArrayList<>();
        waitForElement(videoTab);
        clickElement(videoTab);
        waitForElement(videos);
        for (WebElement element : getElements(videos)) {
            videoList.add(element.getAttribute("href"));
        }
        clickElement(liveTab);
        waitForElement(videos);
        for (WebElement element : getElements(videos)) {
            videoList.add(element.getAttribute("href"));
        }
        return videoList;
    }

    public void openVideo(String href) {
        BrowserHelper.getDriver().navigate().to(href);
    }

    public void openChannel(String channelId) {
        BrowserHelper.getDriver().navigate().to(String.format("%s/channel/%s", baseUrl, channelId));
    }
}
