package common.helpers;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileHelper {
    @SneakyThrows
    public static List<String> readFile(String fileName) {
        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        List<String> textLines = new ArrayList<>();
        BufferedReader reader;
        reader = new BufferedReader(new InputStreamReader(input));
        String line = reader.readLine();
        while (line != null) {
            textLines.add(line);
            // read next line
            line = reader.readLine();
        }
        reader.close();

        return textLines;
    }

    @SneakyThrows
    public static void copyFolder(String from, String to) {
        File srcDir = new File(from);
        File destDir = new File(to);
        FileUtils.copyDirectory(srcDir, destDir);
    }

    @SneakyThrows
    public static void copyFile(String from, String to) {
        File srcDir = new File(from);
        File destDir = new File(to);
        FileUtils.copyFile(srcDir, destDir);
    }

    @SneakyThrows
    public static void removeFolder(String path) {
        FileUtils.deleteDirectory(new File(path));
    }

    @SneakyThrows
    public static void writeToFile(List<String> lines, String filePath) {
        FileWriter writer = new FileWriter(filePath);
        for (String str : lines) {
            writer.write(str + System.lineSeparator());
        }
        writer.close();
    }

    @SneakyThrows
    public static String addFilesToZip(String folderPath) {
        String zipPath = folderPath + "/proxy.zip";
        File folder = new File(folderPath);
        File[] fileList = folder.listFiles();
        try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipPath))) {
            for (File fileToZip : fileList) {
                zipOut.putNextEntry(new ZipEntry(fileToZip.getName()));
                Files.copy(fileToZip.toPath(), zipOut);
            }
        }
        removeOnExit(folder);
        return zipPath;
    }

    public static void removeOnExit(File fileOrDir) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> FileUtils.deleteQuietly(fileOrDir)));
    }

    public static boolean doesFileExist(String filePath) {
        return new File(filePath).exists();
    }
}
