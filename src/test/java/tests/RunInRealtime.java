package tests;

import org.testng.TestNG;
import org.testng.collections.Lists;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class RunInRealtime {
    public static void main(String[] args) {
        TestNG testng = new TestNG();
        List<String> suites = Lists.newArrayList();
        String suiteName = args.length == 0 ? "raceHorse.xml" : args[0];
        suites.add(String.format("src/test/resources/%s", suiteName));
        testng.setTestSuites(suites);
        String outputDir = "target/custom-reports";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy_hh_mm_ss");

        while (true) {
            testng.setOutputDirectory(outputDir + File.separator + formatter.format(calendar.getTime()));
            testng.run();
        }
    }
}
