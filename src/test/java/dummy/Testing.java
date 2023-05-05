package dummy;

import common.helpers.DateTimeHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Testing {

    public static void main(String... args) throws Exception {
        SimpleDateFormat ddf = new SimpleDateFormat("H:mm:ss");
        Date startTime = ddf.parse("5:10:20");
        System.out.println(DateTimeHelper.getCurrentTimeInVietnam("MM/dd HH:mm"));
    }
}
