package page_objects.youtube;

import common.Common;
import common.helpers.BrowserHelper;
import common.helpers.GoogleSheetHelper;
import common.helpers.LogHelper;
import common.helpers.RunnerHelper;
import org.apache.commons.lang3.time.StopWatch;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MultiPageAction {
    public void watchHomePage() {
        int MIN_WATCH_TIME = 5;
        int MAX_WATCH_TIME = 20;

        int timeToWatchHomePage = Common.getRunPropertyAsNumber("watchHomePageTime");
        if (timeToWatchHomePage == 0) {
            return;
        }

        LogHelper.info(String.format("Watching homepage for: %s", timeToWatchHomePage));
        HomePage homePage = new HomePage();
        VideoPlayPage videoPlayPage = new VideoPlayPage();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        RunnerHelper.retryIfClickIntercepted(homePage::openRandomVideo);
        Common.sleep(Common.getRandomNumber(MIN_WATCH_TIME, MAX_WATCH_TIME));
        boolean isAtShortVideo = videoPlayPage.isAtShortVideo();
        while (stopWatch.getTime(TimeUnit.SECONDS) <= timeToWatchHomePage) {
            videoPlayPage.openRandomRecommendedVideo(isAtShortVideo);
            if (!isAtShortVideo) {
                videoPlayPage.waitForVideoPlay(Common.getRandomNumber(MIN_WATCH_TIME, MAX_WATCH_TIME));
            } else {
                Common.sleep(Common.getRandomNumber(MIN_WATCH_TIME, MAX_WATCH_TIME));
            }
        }
    }

    public void watchYtbVideo() {
        VideoPlayPage videoPlayPage = new VideoPlayPage();
        int minWatchTime = Common.getRunPropertyAsNumber("minTime") * 60;
        int maxWatchTime = Common.getRunPropertyAsNumber("maxTime") * 60;
        int watchTime = Common.getRandomNumber(minWatchTime, maxWatchTime);
        if (Common.getRunPropertyAsBoolean("viewBySearch")) {
            if (!videoPlayPage.playVideoByKeyword()) {
                LogHelper.info("Can't play any video using any keyword");
                return;
            }
        } else {
            playVideoFromLink();
        }

        videoPlayPage.setVideoQuality(Common.getRunProperty("quality"));

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        videoPlayPage.waitForVideoPlay(watchTime);
        while (Common.getTimeLeft(watchTime, stopWatch) > 0) {
            LogHelper.info(String.format("Total play time left: %s seconds", Common.getTimeLeft(watchTime, stopWatch)));
            if (Common.getRunPropertyAsBoolean("viewBySearch")) {
                if (!videoPlayPage.playVideoByKeyword()) {
                    LogHelper.info("Can't play any video using any keyword");
                    return;
                }
            } else if (!videoPlayPage.playVideoInList() && !playVideoFromLink()) {
                LogHelper.info("No more videos found. Finish session");
                return;
            }

            if (BrowserHelper.getWindowsList().size() > 1) {
                BrowserHelper.closeOtherWindows();
                BrowserHelper.refresh();
            }
            videoPlayPage.waitForVideoPlay(Common.getTimeLeft(watchTime, stopWatch));
        }
    }

    private boolean playVideoFromLink() {
        List<String> videoLists = GoogleSheetHelper.getYoutubeVideoList();
        if (videoLists.size() == 0) {
            LogHelper.info("No more video in list. Stop playing");
            return false;
        }
        String videoUrl = videoLists.get(Common.getRandomNumber(videoLists.size() - 1));
        Common.addVideoToPlayed(videoUrl);
        LogHelper.info("Play video from url: " + videoUrl);
        BrowserHelper.navigateToUrl(videoUrl);
        return true;
    }
}
