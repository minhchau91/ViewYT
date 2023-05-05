package tests;

import common.Common;
import common.Constant;
import common.helpers.BrowserHelper;
import common.helpers.FileHelper;
import common.helpers.LogHelper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import page_objects.youtube_studio.StudioContentPage;
import page_objects.youtube_studio.StudioVideoDetailsPage;

import java.util.List;

public class AddAdsBreaks extends BaseTest {
    StudioContentPage contentPage = new StudioContentPage();
    StudioVideoDetailsPage videoDetailsPage = new StudioVideoDetailsPage();

    @Test(dataProvider = "channelList")
    public void TC001(String channel) {
        LogHelper.info("Add ads breaks for channel id: " + channel);
        BrowserHelper.startBrowser(browserType, "9978259181");
        contentPage.openChannel(channel);
        contentPage.clickContentButton();
        List<String> videoList = contentPage.getVideoList();
        for (String video : videoList) {
            video = video.replace("/edit", "/monetization/ads");
            Common.setRunningVideo(video);
            LogHelper.info("Add ads breaks for video: " + video);
            contentPage.openVideo(video);
            videoDetailsPage.addAdsBreak(3);
        }
    }

    @DataProvider(name = "channelList")
    public Object[][] channelList() {
        List<String> channels = FileHelper.readFile(Constant.CHANNEL_PATH);
        return channels.stream()
                .map(channel -> new Object[]{channel})
                .toArray(Object[][]::new);
    }
}
