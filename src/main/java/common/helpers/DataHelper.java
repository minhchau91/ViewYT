package common.helpers;

import com.github.javafaker.Faker;
import common.Common;
import common.Constant;
import common.model.Email;
import common.model.Proxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DataHelper {
    private static List<Proxy> proxyList;
    private static Faker faker = new Faker();
    private static Properties prop = new Properties();

    public static String getRandomEmail() {
        return faker.internet().emailAddress();
    }

    public static String getRandomText() {
        return faker.letterify("?????????");
    }

    public static String getRandomPID() {
        return faker.numerify("##########");
    }

    public static String getEnvVariable(String envName) {
        return System.getProperty(envName) != null ? System.getProperty(envName) : "";
    }

    public static String getProperty(String propertyName) {
        return prop.getProperty(propertyName) != null ? prop.getProperty(propertyName) : "";
    }

    static {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties")) {
            //load a properties file from class path, inside static method
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static List<Email> getEmailList() {
        List<String> rawEmails = FileHelper.readFile(Constant.EMAILS_PATH);
        List<Email> emails = new ArrayList<>();
        int idx = 0;
        for (String rawEmail : rawEmails) {
            String[] emailParts = rawEmail.split("\\s+");
            if (emailParts.length < 2) {
                LogHelper.info(String.format("Email: %s isn't in correct format. Should be in '<Email> <Password> <Recovery Email>' format. Skip this!", rawEmail));
            } else {
                emails.add(new Email(idx++, emailParts[0], emailParts[1], emailParts.length > 2 ? emailParts[2] : null));
            }
        }
        return emails;
    }

    public static String getRandomUserAgent() {
        List<String> userAgents = FileHelper.readFile(Constant.USER_AGENT_PATH);
        return userAgents.get(Common.getRandomNumber(userAgents.size() - 1));
    }

    public static List<Proxy> getProxyList() {
        List<String> rawProxies = FileHelper.readFile("proxy/proxylist.txt");
        List<Proxy> proxies = new ArrayList<>();
        for (String rawProxy : rawProxies) {
            proxies.add(buildProxy(rawProxy));
        }
        return proxies;
    }

    public static Proxy buildProxy(String rawProxy) {
        Proxy proxy;
        String[] proxyPart = rawProxy.split(":");
        if (proxyPart.length < 2) {
            LogHelper.info(String.format("Proxy: %s isn't in incorrect format. Should be in '<host>:<post>:<username>:<password>' format. Skip this!", rawProxy));
        }
        if (proxyPart.length == 2) {
            proxy = new Proxy(proxyPart[0], proxyPart[1]);
        } else {
            proxy = new Proxy(proxyPart[0], proxyPart[1], proxyPart[2], proxyPart[3]);
        }
        return proxy;
    }

    public static synchronized Proxy getRandomProxy() {
        if (proxyList == null) {
            proxyList = getProxyList();
        }
        int proxyIndex = Common.getRandomNumber(proxyList.size() - 1);
        return proxyList.get(proxyIndex);
    }

    public static synchronized Proxy getProxy() {
        if (proxyList == null || proxyList.size() == 0) {
            LogHelper.info("Generate new proxy list");
            proxyList = getProxyList();
        }
        //get proxy idx by email id
//        int proxyIndex = Common.getRunningEmail().getId();
//        if (proxyIndex >= proxyList.size()) {
//            proxyIndex = Common.getRandomNumber(proxyList.size());
//        }
        int proxyIndex = Common.getRandomNumber(proxyList.size());
        Proxy result = proxyList.get(proxyIndex);
        proxyList.remove(result);
        LogHelper.info(String.format("Running on Proxy: %s:%s", result.getHost(), result.getPort()));
        return result;
    }

    public static String getResourcePath(String fileName) {
        return Thread.currentThread().getContextClassLoader().getResource(fileName).getPath();
    }

    public static void overwriteEmailList() {
        if (!FileHelper.doesFileExist(Constant.FAILED_EMAILS_PATH)) {
            throw new RuntimeException("Failed email list doesn't exist. Need to run one time first");
        }
        FileHelper.copyFile(Constant.FAILED_EMAILS_PATH, getResourcePath(Constant.EMAILS_PATH));
    }
}
