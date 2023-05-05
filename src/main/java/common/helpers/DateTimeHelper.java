package common.helpers;

import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateTimeHelper {

    public static String getCurrentTime(String datePattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        Date date = new Date();
        return sdf.format(date);
    }

    public static String getCurrentTimeInVietnam(String datePattern) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(datePattern);
        return df.format(LocalDateTime.now(ZoneId.of("Asia/Saigon")));
    }

    @SneakyThrows
    public static String adjustTime(final String oldDate, final String dateFormat, int calendarUnit,
                                    int timeToAdd) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Calendar c = Calendar.getInstance();
        c.setTime(sdf.parse(oldDate));
        c.add(calendarUnit, timeToAdd);
        return new SimpleDateFormat(dateFormat).format(c.getTime());
    }

    public static String getTimeFormat(String time) {
        return StringUtils.countMatches(time, ":") >= 2 ? "H:mm:ss" : "m:ss";
    }

    @SneakyThrows
    public static int getSecondPassed(String fromTime, String toTime) {
        String fromTimeDF = getTimeFormat(fromTime);
        String toTimeDF = getTimeFormat(toTime);
        Date fromTimeDate = new SimpleDateFormat(fromTimeDF).parse(fromTime);
        Date toTimeDate = new SimpleDateFormat(toTimeDF).parse(toTime);

        long diffInMillies = Math.abs(toTimeDate.getTime() - fromTimeDate.getTime());
        return (int) TimeUnit.SECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static int getMinPassed(Date fromTime, Date toTime) {
        long diffInMillies = Math.abs(toTime.getTime() - fromTime.getTime());
        return (int) TimeUnit.MINUTES.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    @SneakyThrows
    public static boolean isAfterDate(String inputDate, String dateToCompare, String formatPattern) {
        DateFormat dfm = new SimpleDateFormat(formatPattern);
        Date inputDateTime = dfm.parse(inputDate);
        Date compareTo = dfm.parse(dateToCompare);
        return inputDateTime.equals(dateToCompare) || inputDateTime.after(compareTo);
    }

    @SneakyThrows
    public static boolean isDatePassed(String date, String formatPattern) {
        Date today = new Date();
        DateFormat dfm = new SimpleDateFormat(formatPattern);
        String dateWithFormat = dfm.format(today);
        Date inputDateTime = dfm.parse(date);
        Date formattedToday = dfm.parse(dateWithFormat);
        return inputDateTime.equals(formattedToday) || inputDateTime.before(formattedToday);
    }

    public static String getCurrentTimeZone() {
        TimeZone tz = TimeZone.getDefault();
        return tz.getDisplayName();
    }
}
