package common.helpers;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import common.Common;
import common.model.Email;
import common.model.VPSMatrix;
import common.model.VideoMap;
import lombok.SneakyThrows;
import org.apache.commons.lang3.time.StopWatch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class GoogleSheetHelper {
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final String PREMIUM_SPREAD_SHEET_ID = "1kKg7z3_BqZPEfkvAC9e9yL0x312Z_M4Fs_cruRe2-O4";
    private static StopWatch stopWatch;

    private static List<VPSMatrix> vpsMatrixList;
    private static List<Email> emailList;
    private static List<String> proxyList;
    private static Map<String, String> properties;
    private static List<VideoMap> videosDict;
    private static ThreadLocal<List<String>> videosUrlListThreadLocal = new ThreadLocal<>();
    private static ThreadLocal<Date> lastUpdated = new ThreadLocal<>();

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private static final List<String> SCOPES =
            Collections.singletonList(SheetsScopes.SPREADSHEETS);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT)
            throws IOException {
        // Load client secrets.
        InputStream in = ThreadLocal.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets =
                GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    @SneakyThrows
    public static List<List<Object>> getData(String spreadsheetId, String range) {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Sheets service =
                new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();
        ValueRange response = service.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();
        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            System.out.println("No data found.");
        }
        return values;
    }

    @SneakyThrows
    public static UpdateValuesResponse updateValues(String spreadsheetId,
                                                    String range,
                                                    String valueInputOption,
                                                    List<List<Object>> values) {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        // Create the sheets API client
        Sheets service = new Sheets.Builder(new NetHttpTransport(),
                JSON_FACTORY,
                getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();

        UpdateValuesResponse result = null;
        try {
            // Updates the values in the specified range.
            ValueRange body = new ValueRange()
                    .setValues(values);
            result = service.spreadsheets().values().update(spreadsheetId, range, body)
                    .setValueInputOption(valueInputOption)
                    .execute();
            System.out.printf("%d cells updated.", result.getUpdatedCells());
        } catch (GoogleJsonResponseException e) {
            // TODO(developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                System.out.printf("Spreadsheet not found with id '%s'.\n", spreadsheetId);
            } else if (error.getCode() == 429) {
                System.out.print(e.getMessage());
            } else {
                throw e;
            }
        }
        return result;
    }

    public static void updateExpiredDate(int mailId, String value) {
        String range = String.format("ViewYTB!E%s:E%s", mailId + 1, mailId + 1);
        updateValues(PREMIUM_SPREAD_SHEET_ID, range, "RAW", Collections.singletonList(Collections.singletonList(value)));
    }

    public static void updateVideoSearchKeywordStatus(int videoIdx, String value) {
        String range = String.format("Search video buff!D%s:D%s", videoIdx + 1, videoIdx + 1);
        updateValues(PREMIUM_SPREAD_SHEET_ID, range, "RAW", Collections.singletonList(Collections.singletonList(value)));
    }

    public static void updateViewStatus(int mailId, int viewDuration) {
        if (!isStatusExpired()) {
            LogHelper.info("View status is not expired. Skip update.");
            return;
        }
        String range = String.format("ViewYTB!F%s:F%s", mailId + 1, mailId + 1);
        String currentTime = DateTimeHelper.getCurrentTimeInVietnam("MM/dd HH:mm");
        String viewStatus = String.format("Start at: %s, waiting for %s seconds", currentTime, viewDuration);
        UpdateValuesResponse result = updateValues(PREMIUM_SPREAD_SHEET_ID, range, "RAW", Collections.singletonList(Collections.singletonList(viewStatus)));
        if (result != null)
            lastUpdated.set(new Date());
    }

    public static boolean isStatusExpired() {
        Date currentTime = new Date();
        int expireTime = Common.getRunPropertyAsNumber("updateStatusTime");
        if (lastUpdated.get() == null) {
            return true;
        }
        return expireTime > 0 && DateTimeHelper.getMinPassed(lastUpdated.get(), currentTime) > expireTime;
    }

    public static List<String> generateYoutubeVideoList() {
        LogHelper.info("Generate Youtube video list");
        String range = "List video buff!A2:B";
        List<String> ytbVideos = new ArrayList<>();
        for (List row : getData(PREMIUM_SPREAD_SHEET_ID, range)) {
            ytbVideos.add((String) row.get(1));
        }
        return ytbVideos;
    }

    public static List<String> generateProxyList() {
        String range = "ProxyList!A2:B";
        List<String> proxyList = new ArrayList<>();
        for (List row : getData(PREMIUM_SPREAD_SHEET_ID, range)) {
            proxyList.add((String) row.get(0));
        }
        return proxyList;
    }

    public static List<VPSMatrix> generateVPSMatrix() {
        final String RANGE = "RunMatrix!A2:C";
        final int VPS_ID_IDX = 0;
        final int FROM_IDX = 1;
        final int TO_IDX = 2;
        List<VPSMatrix> listVPSMatrix = new ArrayList<>();
        List<List<Object>> values = getData(PREMIUM_SPREAD_SHEET_ID, RANGE);
        for (List<Object> row : values) {
            listVPSMatrix.add(new VPSMatrix(row.get(VPS_ID_IDX), row.get(FROM_IDX), row.get(TO_IDX)));
        }
        return listVPSMatrix;
    }

    public static String getRandomProxy() {
        List<String> proxyList = getProxyList();
        String result = proxyList.get(Common.getRandomNumber(proxyList.size() - 1));
        proxyList.remove(result);
        return result;
    }

    public static String getProxyByEmailIdx() {
        return getProxyList().get(Common.getRunningEmail().getId() - 1);
    }

    public synchronized static List<String> getProxyList() {
        if (stopWatch == null) {
            stopWatch = new StopWatch();
            stopWatch.start();
        }

        if (proxyList == null || proxyList.size() == 0 || stopWatch.getTime(TimeUnit.MINUTES) > Common.getRunPropertyAsNumber("refreshProxyListTimeOutInMinutes")) {
            LogHelper.info("Generate new proxy list");
            proxyList = generateProxyList();
            stopWatch.reset();
            stopWatch.start();
        }
        return proxyList;
    }

    public synchronized static List<String> getYoutubeVideoList() {
//        LogHelper.info("getYoutubeVideoList");
        if (videosUrlListThreadLocal.get() == null) {
//            LogHelper.info("setYoutubeVideoList");
            videosUrlListThreadLocal.set(generateYoutubeVideoList());
        }
        if (videosUrlListThreadLocal.get() == null) {
//            LogHelper.info("youtubeVideo is null");
            assert videosUrlListThreadLocal.get() != null;
        }
        return videosUrlListThreadLocal.get();
    }

    public synchronized static List<VPSMatrix> getVPSMatrix() {
        if (vpsMatrixList == null) {
            vpsMatrixList = generateVPSMatrix();
        }
        return vpsMatrixList;
    }

    public static List<Email> generateEmailList() {
        final String RANGE = "ViewYTB!A2:E";
        final int STT_IDX = 0;
        final int EMAIL_IDX = 1;
        final int PASSWORD_IDX = 2;
        final int RECOVERY_EMAIL_IDX = 3;
        final int PROXY_IDX = 4;

        List<Email> emailList = new ArrayList<>();
        List<List<Object>> values = getData(PREMIUM_SPREAD_SHEET_ID, RANGE);
        for (List<Object> row : values) {
//            Proxy proxy = DataHelper.buildProxy(getRandomProxy());
            String recoveryEmail = row.size() >= 4 ? (String) row.get(RECOVERY_EMAIL_IDX) : null;
            Email email = new Email(Integer.parseInt((String) row.get(STT_IDX)), (String) row.get(EMAIL_IDX), (String) row.get(PASSWORD_IDX), recoveryEmail, null);
            emailList.add(email);
        }
        return emailList;
    }

    public synchronized static List<Email> getFullEmailList() {
        if (emailList == null) {
            emailList = generateEmailList();
        }
        return emailList;
    }

    public static List<Email> getEmailList(String vpsId) {
        List<VPSMatrix> vpsMatrixList = getVPSMatrix();
        VPSMatrix vpsInfo = vpsMatrixList.stream().filter(vpsMatrix -> vpsMatrix.getId().equals(vpsId.trim())).findFirst().orElse(null);
        if (vpsInfo == null) {
            throw new RuntimeException(String.format("Can't find VPS with id: %s. Please check again", vpsId));
        }
        if (vpsInfo.getFrom() > vpsInfo.getTo()) {
            throw new RuntimeException(String.format("VPS with id: %s is not valid, 'From' field can't be larger than 'To'", vpsId));
        }
        return getFullEmailList().stream().filter(email -> email.getId() >= vpsInfo.getFrom() && email.getId() <= vpsInfo.getTo()).collect(Collectors.toList());
    }

    public synchronized static void cleanUp() {
        proxyList = null;
        properties = null;
        videosUrlListThreadLocal.remove();
    }

    public static Map<String, String> generateRunProperties() {
        final String RANGE = "RunProperties!A2:B";
        final int PROPERTY_NAME_IDX = 0;
        final int PROPERTY_VALUE = 1;
        List<List<Object>> values = getData(PREMIUM_SPREAD_SHEET_ID, RANGE);
        Map<String, String> propertiesMap = new HashMap<>();
        for (List<Object> row : values) {
            propertiesMap.put((String) row.get(PROPERTY_NAME_IDX), (String) row.get(PROPERTY_VALUE));
        }
        return propertiesMap;
    }

    public static synchronized Map<String, String> getRunProperties() {
        if (properties == null) {
            properties = generateRunProperties();
        }
        return properties;
    }

    public static List<VideoMap> generateRunVideos() {
        final String RANGE = "Search video buff!A2:C";
        final int VIDEO_IDX = 0;
        final int VIDEO_ID_IDX = 1;
        final int KEYWORD_IDX = 2;
        List<List<Object>> values = getData(PREMIUM_SPREAD_SHEET_ID, RANGE);
        List<VideoMap> videosMap = new ArrayList<>();
        for (List<Object> row : values) {
            videosMap.add(new VideoMap((String) row.get(VIDEO_IDX), (String) row.get(VIDEO_ID_IDX), (String) row.get(KEYWORD_IDX)));
        }
        return videosMap;
    }

    public static synchronized VideoMap getRunVideo() {
        if (videosDict == null || videosDict.size() == 0) {
            videosDict = generateRunVideos();
        }
        int videoIdx = Common.getRandomNumber(videosDict.size() - 1);
        VideoMap ret = videosDict.get(videoIdx);
        videosDict.remove(videoIdx);
        return ret;
    }

    public static String getRunProperty(String propertyName) {
        return getRunProperties().getOrDefault(propertyName, "");
    }
}
