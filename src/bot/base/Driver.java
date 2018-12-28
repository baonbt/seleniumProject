/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bot.base;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import object.dto.SigmaCommandDTO;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import util.common.DataUtil;
import util.common.StringUtil;

import static util.common.Constant.ACTION.LOAD_TIME;
import static util.common.Constant.DRIVER.*;

/**
 * @author Thai Tuan Anh
 */
public class Driver {

    SigmaCommandDTO sigma;
    WebDriver driver;
    String extFolder;

    public Driver(SigmaCommandDTO sigma) {
        this.sigma = sigma;
    }

    void mobileDriver() throws Exception {
        System.setProperty("webdriver.chrome.driver", CHROME_FOLDER);
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(sigma.getAccount().getProxy().getAddress());
        proxy.setSslProxy(sigma.getAccount().getProxy().getAddress());
        proxy.setHttpProxy(sigma.getAccount().getProxy().getAddress());

        String extFolder = initExtFolder();

        changeProxyAuth(extFolder, sigma.getAccount().getProxy().getUsername(), sigma.getAccount().getProxy().getPassword());

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability("proxy", proxy);

        List<String> args = new ArrayList<>();
        args.add("start-maximized");
        args.add("disable-infobars");
        args.add("load-extension=" + extFolder);
        args.add("disable-gpu");
        args.add("no-sandbox");
        args.add("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");

        Map<String, String> mobileEmulation = new HashMap<>();

        mobileEmulation.put("deviceName", "iPhone X");

        //block notification
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.managed_default_content_settings.images", 2);

        Map<String, Object> chromeOptions = new HashMap<String, Object>();
        chromeOptions.put("mobileEmulation", mobileEmulation);
        chromeOptions.put("prefs", prefs);
        chromeOptions.put("args", args);

        capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);

        driver = new ChromeDriver(capabilities);
        driver.manage().timeouts().pageLoadTimeout(LOAD_TIME, TimeUnit.SECONDS);

        goToPage(FACEBOOK_HOME);

        try {
            loadCookies(sigma.getAccount().getCookies());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("loadCookies error");
            throw e;
        }

        goToPage(FACEBOOK_HOME);
    }

    void actionDriver() throws Exception {
        System.setProperty("webdriver.chrome.driver", CHROME_FOLDER);
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(sigma.getAccount().getProxy().getAddress());
        proxy.setSslProxy(sigma.getAccount().getProxy().getAddress());
        proxy.setHttpProxy(sigma.getAccount().getProxy().getAddress());

        String extFolder = initExtFolder();

        changeProxyAuth(extFolder, sigma.getAccount().getProxy().getUsername(), sigma.getAccount().getProxy().getPassword());

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability("proxy", proxy);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("load-extension=" + extFolder);
        options.addArguments("disable-gpu");
        options.addArguments("no-sandbox");

        //block notification
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.managed_default_content_settings.images", 2);
        options.setExperimentalOption("prefs", prefs);

        capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        driver = new ChromeDriver(capabilities);
        driver.manage().timeouts().pageLoadTimeout(LOAD_TIME, TimeUnit.SECONDS);

        goToPage(FACEBOOK_HOME);

        try {
            loadCookies(sigma.getAccount().getCookies());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("loadCookies error");
            throw e;
        }

        goToPage(FACEBOOK_HOME);
    }


    void websiteCrawlerDriver() throws Exception {
        ChromeOptions options = new ChromeOptions();
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
//        options.addArguments("--user-data-dir=" + newChromeProfilePath);
        capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        driver = new ChromeDriver(capabilities);
    }

    void watchLiveStreamDriver() throws Exception {
        System.setProperty("webdriver.chrome.driver", CHROME_FOLDER);
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(sigma.getAccount().getProxy().getAddress());
        proxy.setSslProxy(sigma.getAccount().getProxy().getAddress());
        proxy.setHttpProxy(sigma.getAccount().getProxy().getAddress());

        String extFolder = initExtFolder();
        this.extFolder = extFolder;

        changeProxyAuth(extFolder, sigma.getAccount().getProxy().getUsername(), sigma.getAccount().getProxy().getPassword());

        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        capabilities.setCapability("proxy", proxy);

        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("load-extension=" + extFolder + "," + PLAY_LIVE_STREAM_EXT);
//        options.addArguments("--user-data-dir=" + newChromeProfilePath);

        //block notification
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("profile.default_content_setting_values.notifications", 2);
        options.setExperimentalOption("prefs", prefs);

        capabilities.setCapability(ChromeOptions.CAPABILITY, options);

        driver = new ChromeDriver(capabilities);
        driver.manage().timeouts().pageLoadTimeout(LOAD_TIME, TimeUnit.SECONDS);

        goToPage(FACEBOOK_HOME);

        try {
            loadCookies(sigma.getAccount().getCookies());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("loadCookies error");
            throw e;
        }

        goToPage(FACEBOOK_HOME);
    }

    public boolean goToPage(String url) {
        try {
            try {
                driver.switchTo().alert().accept();
            } catch (Exception ignored) {
            }
            driver.get(url);
            try {
                driver.switchTo().alert().accept();
            } catch (Exception ignored) {
            }

            return true;
        } catch (Exception e) {
            System.out.println("proxy Lagging, loading exceed " + LOAD_TIME + "s: " + sigma.getAccount().getProxyId());

            return false;
        }
    }

    public String getCookies() {
        StringBuilder sbd = new StringBuilder();
        for (Cookie cookie : driver.manage().getCookies()) {
            sbd.append(cookie.getName() + ":");
            sbd.append(cookie.getValue());
            sbd.append("\n");
        }
        return sbd.toString();
    }

    private void loadCookies(String cookiesStr) throws Exception {
        cookiesStr = cookiesStr.replaceAll("=", ":").replaceAll(";", "\n");

        String[] cookies = cookiesStr.split("\n");
        for (String cookie : cookies) {
            try {
                driver.manage().addCookie(new Cookie(cookie.split(":")[0], cookie.split(":")[1], ".facebook.com", "/", null));
            } catch (Exception e) {
                int a = 0;
            }
        }
    }

    private void changeProxyAuth(String extFolder, String username, String password) throws IOException {
        List<String> newLines = new ArrayList<>();
        String jsFilePath = extFolder + "\\js\\extension.js";
        for (String line : Files.readAllLines(Paths.get(jsFilePath), StandardCharsets.UTF_8)) {
            if (line.contains("/*username*/")) {
                line = replaceString(line, username == null ? "" : username);
            }
            if (line.contains("/*password*/")) {
                line = replaceString(line, password == null ? "" : password);
            }
            newLines.add(line);
        }
        Files.write(Paths.get(jsFilePath), newLines, StandardCharsets.UTF_8);
    }

    private String initExtFolder() {
        try {
            String destFolder = MAIN_FOLDER + "\\" + EXTENSIONS_FOLDER + "\\" + DataUtil.generateUniqueRandomString();
            FileUtils.copyDirectory(new File(MAIN_FOLDER + "\\" + PROTOTYPE_EXT_FOLDER), new File(destFolder));

            return destFolder;
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    private String replaceString(String line, String input) {
        String s = line.substring(line.indexOf("'") + 1, line.indexOf("'", line.indexOf("'") + 1));
        return line.replaceAll(s, input);
    }

    public WebDriver getDriver() {
        return driver;
    }

    public void setSigma(SigmaCommandDTO sigma) {
        this.sigma = sigma;
    }

    public SigmaCommandDTO getSigma() {
        return sigma;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public String getExtFolder() {
        return extFolder;
    }

    public void setExtFolder(String extFolder) {
        this.extFolder = extFolder;
    }
}
