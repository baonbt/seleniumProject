package service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;

import static util.common.Constant.DRIVER.CHROME_ORIGIN_ZIP;
import static util.common.Constant.DRIVER.CHROME_PROFILE_FOLDER;

public class ChromeProfileService {

    public byte[] getOriginChromeProfileFromServer() {
        try {
            Path path = Paths.get(CHROME_ORIGIN_ZIP);
            byte[] zipFileBytes = Files.readAllBytes(path);
            return zipFileBytes;
        } catch (Exception ignored) {
            return null;
        }
    }

    public String cloneChromeProfile() throws IOException, InterruptedException {
        byte[] originProfileBytes = getOriginChromeProfileFromServer();
        File profileDirectory = new File(CHROME_PROFILE_FOLDER + getCurrentTime());
        while (true) {
            while (profileDirectory.exists()) {
                profileDirectory = new File(CHROME_PROFILE_FOLDER + getCurrentTime());
                Thread.sleep(1000);
            }
            if (profileDirectory.mkdir()) {
                String sourcePath = profileDirectory.getPath() + "//origin_chrome_profile.zip";
                FileUtils.writeByteArrayToFile(new File(sourcePath), originProfileBytes);
                try {
                    ZipFile zipFile = new ZipFile(sourcePath);
                    zipFile.extractAll(profileDirectory.getPath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return profileDirectory.getPath();
    }

    private String getCurrentTime() {
        String dateInString = new Date().toString();
        return dateInString.replaceAll(" ", "_").replaceAll(":", "_");
    }
}
