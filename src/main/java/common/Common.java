package common;

import common.helpers.DataHelper;
import common.helpers.GoogleSheetHelper;
import common.model.Email;
import org.apache.commons.lang3.time.StopWatch;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Common {
    public static String failedFolderPath;
    public static String passedFolderPath;
    private static final ThreadLocal<Email> runningEmail = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<String> runningVideo = ThreadLocal.withInitial(() -> null);
    private static final ThreadLocal<List<String>> playedVideo = ThreadLocal.withInitial(ArrayList::new);

    public static String plusDayFromNow(long daysToAdd, String dateFormat) {
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern(dateFormat);
        return LocalDate.now().plusDays(daysToAdd).format(myFormatObj);
    }

    public static File readFile(String fileName) {
        String path = new File(fileName).getAbsolutePath();
        return new File(path);
    }

    public static String changeDateFormat(String date, String newFormat) {
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern(newFormat);
        return LocalDate.parse(date).format(myFormatObj);
    }

    public static String plusDaysFromDate(String date, long daysToAdd) {
        return LocalDate.parse(date).plusDays(daysToAdd).toString();
    }

    public static void sleep(int timeInSeconds) {
        if (timeInSeconds <= 0) {
            return;
        }
        try {
            Thread.sleep(timeInSeconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getUniqueId() {
        return UUID.randomUUID().toString().substring(0, 10);
    }

    public static int getRandomNumber(int maxNumber) {
        if (maxNumber == 0) {
            return 0;
        }
        Random rand = new Random();
        return rand.nextInt(maxNumber);
    }

    public static int getRandomNumber(int min, int max) {
        if (max == 0) {
            return 0;
        }
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static Email getRunningEmail() {
        return runningEmail.get();
    }

    public static void setRunningEmail(Email email) {
        runningEmail.set(email);
    }

    public static String getRunningVideo() {
        return runningVideo.get();
    }

    public static void setRunningVideo(String videoUrl) {
        runningVideo.set(videoUrl);
    }

    public static String generateDateAsString() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        return LocalDateTime.now().format(dateTimeFormatter);
    }

    public static boolean getRandomBoolean(int percentageToBeTrue) {
        return getRandomNumber(100) <= percentageToBeTrue;
    }

    /**
     * Get match group
     *
     * @param input    Input text
     * @param regex    Regex
     * @param groupIdx 0 mean the whole test, 1 is first group, 2 is second group and so on
     * @return matched group
     */
    public static String getMatchGroup(String input, String regex, int groupIdx) {
        String result = null;
        Pattern r = Pattern.compile(regex);
        Matcher m = r.matcher(input);
        if (m.find()) {
            result = m.group(groupIdx);
        }
        return result;
    }

    public static String getVideoId(String videoUrl) {
        return getMatchGroup(videoUrl, "[\\?|\\&]v=([\\w|-]+)", 1);
    }

    public static boolean isVideoPlayed(String videoUrl) {
        return playedVideo.get().contains(getVideoId(videoUrl));
    }

    public static void addVideoToPlayed(String videoUrl) {
        if (!isVideoPlayed(videoUrl)) {
            String videoId = getVideoId(videoUrl);
            playedVideo.get().add(videoId);
            GoogleSheetHelper.getYoutubeVideoList().removeIf(fullUrl -> fullUrl.contains(videoId));
        }
    }

    public static int getTimeLeft(int watchTimeInSeconds, StopWatch stopWatch) {
        int timeLeft = watchTimeInSeconds - (int) stopWatch.getTime(TimeUnit.SECONDS);
//        LogHelper.info(String.format("Playing time left: %s seconds", timeLeft));
        return timeLeft;
    }

    public static String getRunProperty(String parameterName) {
        return getRunProperty(parameterName, "");
    }

    public static int getRunPropertyAsNumber(String parameterName) {
        return Integer.parseInt(getRunProperty(parameterName, ""));
    }

    public static boolean getRunPropertyAsBoolean(String parameterName) {
        return Boolean.parseBoolean(getRunProperty(parameterName, ""));
    }

    public static String getRunProperty(String parameterName, String parameterValue) {
        if (!DataHelper.getEnvVariable(parameterName).equals("")) {
            return DataHelper.getEnvVariable(parameterName);
        }
        if (DataHelper.getEnvVariable("debugMode").equals("false") || DataHelper.getProperty("debugMode").equals("false")) {
            if (!GoogleSheetHelper.getRunProperty(parameterName).equals("")) {
                return GoogleSheetHelper.getRunProperty(parameterName);
            }
        }
        return !parameterValue.equals("") ? parameterValue : DataHelper.getProperty(parameterName);
    }
}
