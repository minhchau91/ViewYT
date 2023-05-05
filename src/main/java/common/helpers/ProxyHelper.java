package common.helpers;

import common.Common;
import common.model.Proxy;

import java.io.File;
import java.util.List;

public class ProxyHelper {

    public static String buildProxy() {
        return buildProxy(Common.getRunningEmail().getProxy());
    }

    public static String buildProxy(Proxy proxy) {
        String proxyTemplatePath = DataHelper.getResourcePath("proxy");
        LogHelper.info(String.format("Running on proxy with host: %s:%s", proxy.getHost(), proxy.getPort()));
        String proxyOutputPath = String.format("%s%s_%s_%s_%s", proxyTemplatePath, proxy.getHost(),
                proxy.getPort(), proxy.getUsername(), proxy.getPassword());
        if (FileHelper.doesFileExist(proxyOutputPath)) {
            return proxyOutputPath + "/proxy.zip";
        }
        new File(proxyOutputPath).mkdirs();
        String outputBackground = proxyOutputPath + "/" + "background.js";
        String outputManifest = proxyOutputPath + "/" + "manifest.json";
        List<String> background_js = FileHelper.readFile("proxy/background.js");

        for (int i = 0; i < background_js.size(); i++) {
            if (background_js.get(i).contains("HOST")) {
                background_js.set(i, background_js.get(i).replace("HOST", proxy.getHost()));
                continue;
            }
            if (background_js.get(i).contains("PORT")) {
                background_js.set(i, background_js.get(i).replace("PORT", proxy.getPort()));
                continue;
            }
            if (background_js.get(i).contains("USERNAME")) {
                background_js.set(i, background_js.get(i).replace("USERNAME", proxy.getUsername()));
                continue;
            }
            if (background_js.get(i).contains("PASSWORD")) {
                background_js.set(i, background_js.get(i).replace("PASSWORD", proxy.getPassword()));
                break;
            }

        }
        FileHelper.writeToFile(background_js, outputBackground);
        FileHelper.copyFile(DataHelper.getResourcePath("proxy/manifest.json"), outputManifest);

        return FileHelper.addFilesToZip(proxyOutputPath);
    }
}
