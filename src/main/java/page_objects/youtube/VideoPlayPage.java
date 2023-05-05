package page_objects.youtube;

import common.Common;
import common.Constant;
import common.helpers.*;
import common.model.VideoMap;
import org.apache.commons.lang3.time.StopWatch;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import page_objects.BasePage;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class VideoPlayPage extends BasePage {
    private static int MIN_WATCH_TIME = Common.getRunPropertyAsNumber("minTimeForOneVideo");
    private static int LIKE_PERCENTAGE = 20;
    private static int DISLIKE_PERCENTAGE = 10;
    private static int SUBSCRIBE_PERCENTAGE = 30;
    private static int SHOW_HIDE_CHAT_PERCENTAGE = 50;
    private static int SCROLL_UP_AND_DOWN_PERCENTAGE = 70;
    private static int PLAY_VIDEO_POOLING_TIME = 20;

    private static final String videoQualityOptionXpath = "//div[@class='ytp-menuitem-label' and contains(.,'%sp')]";
    private final By lblYtbTimeCurrent = By.cssSelector("#ytd-player .ytp-time-current");
    private final By lblYtbTimeDuration = By.cssSelector("#ytd-player .ytp-time-duration");
    private final By btnYtbSettings = By.cssSelector("#ytd-player .ytp-settings-button");
    private final By btnYtbVideoQuality = By.xpath("//div[@class='ytp-menuitem-label' and text() = 'Quality']/following-sibling::div");
    private final By youtubePlayer = By.id("ytd-player");
    private final By btnLikeAlt = By.id("segmented-like-button");
    private final By btnLike = By.xpath("//button[contains(@aria-label, 'like this video along with')]/..");
    private final By btnDislike = By.xpath("//button[contains(@aria-label, 'Dislike this video')]/..");
    private final By btnPlay = By.xpath("//div[@id='player-theater-container']//button[@data-title-no-tooltip='Play']");
    //    private final By btnSubscribe = By.cssSelector("#meta-contents #subscribe-button");
    private final By btnSubscribe = By.cssSelector("#subscribe-button");
    private final By lblSubscribed = By.xpath("//tp-yt-paper-button[@subscribed]/..");
    private final By btnShowHideChat = By.id("show-hide-button");
    private final By lnkPlaylistVideo = By.cssSelector("#playlist ytd-playlist-panel-video-renderer a");
    private final By lnkRecommendedVideo = By.cssSelector("#contents ytd-compact-video-renderer ytd-thumbnail");
    private final By lnkShortVideo = By.cssSelector("#shorts-inner-container #player-container");
    private final String dynCssVideoByHref = "a#video-title[href=\"/watch?v=%s\"]";
    private final By txtSearch = By.cssSelector("input#search");
    private final By btnSearchNarrow = By.cssSelector("#search-button-narrow");
    private final By btnSearchNormal = By.cssSelector("#search-icon-legacy");

    public void setVideoQuality(String videoQuality) {
        LogHelper.info(String.format("Set video quality to: %sp", videoQuality));
        waitForElement(youtubePlayer);
        ElementHelper.hoverOn(youtubePlayer);
        ElementHelper.clickByAction(btnYtbSettings);
        waitForElement(btnYtbVideoQuality);
        clickElementWithoutScroll(btnYtbVideoQuality);
        By btnVideoQuality = By.xpath(String.format(videoQualityOptionXpath, videoQuality));
        waitForElement(btnVideoQuality);
        clickElementWithoutScroll(btnVideoQuality);
    }

    public void likeVideoRandomly() {
        String pressed = "style-default-active";
        waitForElement(btnLike);
        if (ElementHelper.doesElementVisible(btnLikeAlt) || ElementHelper.getAttribute(btnLike, "class").contains(pressed)
                || ElementHelper.getAttribute(btnDislike, "class").contains(pressed)) {
            return;
        }
        if (Common.getRandomBoolean(LIKE_PERCENTAGE)) {
            LogHelper.info("Like Youtube video.");
            ElementHelper.clickByAction(btnLike);
            scrollToVideoPlayer();

        } else if (Common.getRandomBoolean(DISLIKE_PERCENTAGE)) {
            ElementHelper.clickByAction(btnDislike);
            scrollToVideoPlayer();
        }
    }

    public void subscribeRandomly() {
        waitForElement(btnSubscribe);
        if (!ElementHelper.doesElementVisible(lblSubscribed) && Common.getRandomBoolean(SUBSCRIBE_PERCENTAGE)) {
            LogHelper.info("Subscribe Youtube Channel.");
            for (WebElement element : getElements(btnSubscribe)) {
                if (element.isDisplayed()) {
                    ElementHelper.clickByAction(element);
                    scrollToVideoPlayer();
                }
            }
        }
    }

    public void showHideChatRandomly() {
        waitForElement(btnShowHideChat);
        if (ElementHelper.doesElementVisible(btnShowHideChat) && Common.getRandomBoolean(SHOW_HIDE_CHAT_PERCENTAGE)) {
            LogHelper.info("Hide/Show chat info.");
            clickElement(btnShowHideChat);
            scrollToVideoPlayer();
        }
    }

    public void scrollUpAndDownRandomly() {
        if (Common.getRandomBoolean(SCROLL_UP_AND_DOWN_PERCENTAGE)) {
            LogHelper.info("Scroll Page up and down.");
            int scrollTime = Common.getRandomNumber(2, 5);
            ElementHelper.scrollDown(scrollTime);
            ElementHelper.scrollUp(scrollTime);
            scrollToVideoPlayer();
        }
    }

    public void scrollToVideoPlayer() {
        ElementHelper.scrollToView(getElement(youtubePlayer));
    }

    /**
     * @param overwriteTimeInSeconds maximum time to play
     */
    public void waitForVideoPlay(int overwriteTimeInSeconds) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        waitForElement(lblYtbTimeCurrent);
        String currentTime = getElement(lblYtbTimeCurrent).getAttribute("innerText");
        String duration = getElement(lblYtbTimeDuration).getAttribute("innerText");
        int watchTimeLeft = DateTimeHelper.getSecondPassed(currentTime, duration);
        int waitTime = watchTimeLeft <= MIN_WATCH_TIME ? MIN_WATCH_TIME : Common.getRandomNumber(MIN_WATCH_TIME, watchTimeLeft);
        if (waitTime > overwriteTimeInSeconds && overwriteTimeInSeconds != 0) {
            waitTime = overwriteTimeInSeconds;
        }
        GoogleSheetHelper.updateViewStatus(Common.getRunningEmail().getId(), waitTime);
        //TODO: remove after debug
//        if (DataHelper.getProperty("debugMode").equals("true") && waitTime > 120) waitTime = 120;

        LogHelper.info(String.format("Wait for video playing in: %s (seconds)", waitTime));
        int firstTimePlayTime = waitTime <= MIN_WATCH_TIME ? waitTime : Common.getRandomNumber(MIN_WATCH_TIME, waitTime);
        LogHelper.info(String.format("Wait for video playing before interacting in: %s (seconds)", firstTimePlayTime));
        keepVideoPlaying(firstTimePlayTime, stopWatch);

        if (Common.getTimeLeft(waitTime, stopWatch) <= 0) {
            return;
        }
        showHideChatRandomly();
        if (Common.getTimeLeft(waitTime, stopWatch) <= 0) {
            return;
        }

        if (Common.getTimeLeft(waitTime, stopWatch) <= 0) {
            return;
        }
        likeVideoRandomly();
        if (Common.getTimeLeft(waitTime, stopWatch) <= 0) {
            return;
        }

        if (Common.getTimeLeft(waitTime, stopWatch) <= 0) {
            return;
        }
        subscribeRandomly();
        if (Common.getTimeLeft(waitTime, stopWatch) <= 0) {
            return;
        }

        if (Common.getTimeLeft(waitTime, stopWatch) <= 0) {
            return;
        }
        scrollUpAndDownRandomly();
        if (Common.getTimeLeft(waitTime, stopWatch) <= 0) {
            return;
        }
        keepVideoPlaying(waitTime, stopWatch);
    }

    public void keepVideoPlaying(int waitTime, StopWatch stopWatch) {
        int timeLeft = Common.getTimeLeft(waitTime, stopWatch);
        LogHelper.info("Keep video playing for: " + waitTime);
        WebDriver currentDriver = BrowserHelper.getDriver();
        new Thread(() -> {
            BrowserHelper.setDriver(currentDriver);
            StopWatch waitStopWatch = new StopWatch();
            while (waitStopWatch.getTime(TimeUnit.SECONDS) < timeLeft) {
                Common.sleep(PLAY_VIDEO_POOLING_TIME);
                if (BrowserHelper.getDriver() == null) {
                    LogHelper.info("Driver not found. Skip waiting.");
                    return;
                }
//                LogHelper.info("Check if video is stopped.");
                if (ElementHelper.doesElementVisible(btnPlay)) {
                    LogHelper.info("Video stopped. Play video.");
                    clickElement(btnPlay);
                } else if (!ElementHelper.doesElementVisible(youtubePlayer)) {
                    LogHelper.info("Video crashed. Refresh browser.");
                    BrowserHelper.refresh();
                    waitForElement(youtubePlayer);
                }
            }
//            LogHelper.info("End check video stopped.");
        }).start();
        Common.sleep(timeLeft);
    }

    public boolean playVideoInList() {
        waitForElement(lnkPlaylistVideo);
        List<WebElement> videosList = getElements(lnkPlaylistVideo);
        Collections.shuffle(videosList);
        for (WebElement element : videosList) {
            String videoUrl = element.getAttribute("href");
            if (!Common.isVideoPlayed(videoUrl)) {
                LogHelper.info("Play video in list with url: " + videoUrl);
                ElementHelper.clickByAction(element);
                Common.addVideoToPlayed(videoUrl);
                return true;
            }
        }
        return false;
    }

    public boolean playVideoByKeyword() {
        int retryCount = 50;
        while (retryCount > 0) {
            VideoMap videoMap = GoogleSheetHelper.getRunVideo();
            waitForElement(btnSearchNarrow);
            if (!ElementHelper.doesElementVisible(txtSearch)) {
                clickElement(btnSearchNarrow);
            }
            waitForElement(txtSearch);
            getElement(txtSearch).clear();
            getElement(txtSearch).sendKeys(videoMap.getKeyword());
            clickElement(btnSearchNormal);
            By lnkVideo = By.cssSelector(String.format(dynCssVideoByHref, videoMap.getVideoId()));
            waitForElement(lnkVideo);
            if (ElementHelper.doesElementVisible(lnkVideo)) {
                LogHelper.info("Play video with id: " + videoMap.getVideoId());
                clickElement(lnkVideo);
                GoogleSheetHelper.updateVideoSearchKeywordStatus(Integer.parseInt(videoMap.getIdx()), "Not Found");
                return true;
            } else {
                LogHelper.info(String.format("Video: %s not found by keyword: %s", videoMap.getVideoId(), videoMap.getKeyword()));
                --retryCount;
            }
        }
        return false;
    }

    public boolean isAtShortVideo() {
        waitForElement(lnkShortVideo, Constant.VERY_SHORT_WAIT);
        return ElementHelper.doesElementVisible(lnkShortVideo);
    }

    public void openRandomRecommendedVideo(boolean isAtShortVideo) {
        if (isAtShortVideo) {
            List<WebElement> shortVideos = getElements(lnkShortVideo);
            ElementHelper.scrollToView(shortVideos.get(Common.getRandomNumber(shortVideos.size() - 1)));
        } else {
            waitForElement(lnkRecommendedVideo);
            List<WebElement> elements = getElements(lnkRecommendedVideo);
            if (elements.size() == 0) {
                BrowserHelper.getDriver().navigate().refresh();
                waitForElement(lnkRecommendedVideo);
                elements = getElements(lnkRecommendedVideo);
            }
            WebElement element = elements.get(Common.getRandomNumber(elements.size() - 1));
            ElementHelper.clickByAction(element);
        }
    }
}
