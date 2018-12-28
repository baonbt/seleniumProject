/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package action.handler;

import bot.base.Driver;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import service.ChatbotService;

import static util.common.Constant.ACTION.DELAY_FROM;
import static util.common.Constant.ACTION.DELAY_TO;

import util.common.DataUtil;
import util.common.ImageSelection;

import javax.imageio.ImageIO;

import static util.common.Constant.COMMON.SALE_KEY_WORDS;
import static util.common.DataUtil.random;

/**
 * @param <T>
 * @author Thai Tuan Anh
 */
public class BaseAction<T extends Driver> {
    String searchBoxXpath = "//input[@data-testid='search_input']";

    public Driver driver;
    public ChatbotService chatbot = new ChatbotService();

    public BaseAction(T driver) {
        this.driver = driver;
    }

    public BaseAction() {

    }

    public void delay() throws InterruptedException {
        Thread.sleep(DataUtil.random(DELAY_FROM, DELAY_TO));
    }

    public boolean _scrollDown() {
        try {
            int pixel = random(500, 800);
            Thread.sleep(1000);
            ((JavascriptExecutor) driver.getDriver()).executeScript("window.scrollBy(0," + pixel + ")");
            Thread.sleep(1000);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean scrollDown() {
        try {
            int pixel = random(300, 500);
            delay();
            ((JavascriptExecutor) driver.getDriver()).executeScript("window.scrollBy(0," + pixel + ")");
            delay();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean click(WebElement webElement) throws InterruptedException {
        try {
            Actions ac = new Actions(driver.getDriver());
            int x = random(0, 8);
            int y = random(0, 8);
//        ac.click(webElement).build().perform();
            ac.moveToElement(webElement, x, y).build().perform();
            Thread.sleep(500);
            ac.moveToElement(webElement, x, y).click().build().perform();
            delay();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean randomByPhanTram(int percent) {
        List<Integer> intList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (i < percent) {
                intList.add(1);
            } else {
                intList.add(0);
            }
        }
        int order = random(0, intList.size() - 1);
        if (intList.get(order) == 1) {
            return true;
        }
        return false;
    }

    public boolean _waitForPageLoaded() {
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 60);
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver webDriver) {
                return ((JavascriptExecutor) webDriver).executeScript("return document.readyState").toString().equals("complete");
            }
        };
        try {
            Thread.sleep(1000);
            wait.until(expectation);
            Thread.sleep(1000);
            return true;
        } catch (Throwable error) {
            Assert.fail("Timeout waiting for Page Load Request to complete.");
            return false;
        }
    }

    public boolean waitForPageLoaded() {
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 60);
        ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
            public Boolean apply(WebDriver webDriver) {
                return ((JavascriptExecutor) webDriver).executeScript("return document.readyState").toString().equals("complete");
            }
        };
        try {
            delay();
            wait.until(expectation);
            delay();
            return true;
        } catch (Throwable error) {
            Assert.fail("Timeout waiting for Page Load Request to complete.");
            return false;
        }
    }


    public boolean _returnToNewsFeed() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 15);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//*[@data-click='bluebar_logo']")));
            WebElement facebookBlueBarLogo = driver.getDriver().findElement(By.xpath(".//*[@data-click='bluebar_logo']"));
            Thread.sleep(1000);
            facebookBlueBarLogo.click();
            _waitForPageLoaded();
            List<WebElement> layerCancelElementList = driver.getDriver().findElements(By.className("layerCancel"));
            if (layerCancelElementList.size() > 0) {
                driver.goToPage("https://www.facebook.com/");
                _waitForPageLoaded();
            }
            return true;
        } catch (Exception e) {
            driver.goToPage("https://www.facebook.com/");
            return false;
        }
    }

    public boolean returnToNewsFeed() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 15);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(".//*[@data-click='bluebar_logo']")));
            WebElement facebookBlueBarLogo = driver.getDriver().findElement(By.xpath(".//*[@data-click='bluebar_logo']"));
            delay();
            click(facebookBlueBarLogo);
            waitForPageLoaded();
            List<WebElement> layerCancelElementList = driver.getDriver().findElements(By.className("layerCancel"));
            if (layerCancelElementList.size() > 0) {
                driver.goToPage("https://www.facebook.com/");
            }
            return true;
        } catch (Exception e) {
            driver.goToPage("https://www.facebook.com/");
            return false;
        }
    }


    public void _move(WebElement webElement) throws InterruptedException {
        Actions ac = new Actions(driver.getDriver());
        ac.moveToElement(webElement, 1, 1).build().perform();
        Thread.sleep(1000);
    }

    public void move(WebElement webElement) throws InterruptedException {
        Actions ac = new Actions(driver.getDriver());
            int x = random(0, 8);
            int y = random(0, 8);
        ac.moveToElement(webElement, x, y).build().perform();
        delay();
    }

    public Boolean isVisibleInViewport(WebElement element) {
        return (Boolean)((JavascriptExecutor) driver.getDriver()).executeScript(
                "var elem = arguments[0],                 " +
                        "  box = elem.getBoundingClientRect(),    " +
                        "  cx = box.left + box.width / 2,         " +
                        "  cy = box.top + box.height / 2,         " +
                        "  e = document.elementFromPoint(cx, cy); " +
                        "for (; e; e = e.parentElement) {         " +
                        "  if (e === elem)                        " +
                        "    return true;                         " +
                        "}                                        " +
                        "return false;                            "
                , element);
    }


    public boolean _scrollElementToMiddle(WebElement webElement) {
        try {
            String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
                    + "var elementTop = arguments[0].getBoundingClientRect().top;"
                    + "window.scrollBy(0, elementTop-(viewPortHeight/20));";
            ((JavascriptExecutor) driver.getDriver()).executeScript(scrollElementIntoMiddle, webElement);
            delay();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public boolean scrollElementToMiddle(WebElement webElement) {
        try {
            String scrollElementIntoMiddle = "var viewPortHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);"
                    + "var elementTop = arguments[0].getBoundingClientRect().top;"
                    + "window.scrollBy(0, elementTop-(viewPortHeight/2));";
            ((JavascriptExecutor) driver.getDriver()).executeScript(scrollElementIntoMiddle, webElement);
            delay();
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public void scrollElementToViewPort(){

    }

    public void copyImageToClipboard(byte[] images) throws IOException {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(images));
        ImageSelection imgSel = new ImageSelection(img);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(imgSel, null);
    }

    public void setClipboardReady() {
        StringSelection stringSelection = new StringSelection("ready");
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                stringSelection, null);
    }

    public void copyStringtoClipBoard(String string){
        StringSelection selection = new StringSelection(string);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                selection, null);
    }

    public boolean isClipboardReady() {
        try {
            String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            if (data.equals("ready")) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean scroll(int pixel) {
        try {
            delay();
            ((JavascriptExecutor) driver.getDriver()).executeScript("window.scrollBy(0," + pixel + ")");
            delay();
            return true;
        } catch (Exception e) {
            System.out.println("scroll-" + e.getMessage());
            return false;
        }
    }

    public boolean scrollToTop() {
        try {
            delay();
            ((JavascriptExecutor) driver.getDriver()).executeScript("window.scrollTo(0,0)");
            delay();
            return true;
        } catch (Exception e) {
            System.out.println("scroll-" + e.getMessage());
            return false;
        }
    }

    public boolean scrollToBottom() {
        try {
            delay();
            ((JavascriptExecutor) driver.getDriver()).executeScript("window.scrollTo(0, document.body.scrollHeight)");
            delay();
            return true;
        } catch (Exception e) {
            System.out.println("scroll-" + e.getMessage());
            return false;
        }
    }

    public int getNumberInString(String string) {
        String numberOnly = string.replaceAll("[^0-9]", "-");
        String[] segments = numberOnly.split("-");
        int maximum = 0;
        for (String segment : segments) {
            if (segment.length() > 0) {
                if (Integer.valueOf(segment) > maximum) {
                    maximum = Integer.valueOf(segment);
                }
            }
        }
        return maximum;
    }

    public boolean goProfileThroughSearch(String name, String profileLinkOrId) {
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 30);
        try {
            List<WebElement> searchInputElementList = driver.getDriver().findElements(By.xpath(searchBoxXpath));
            if (searchInputElementList.isEmpty()) {
                returnToNewsFeed();
                searchInputElementList = driver.getDriver().findElements(By.xpath(searchBoxXpath));
            }
            if (!searchInputElementList.isEmpty()) {
                searchInputElementList.get(0).sendKeys(name);
                Thread.sleep(random(200, 400));
                driver.getDriver().findElement(By.xpath(searchBoxXpath)).sendKeys(Keys.ENTER);
                waitForPageLoaded();
                WebElement groupsSearchTabElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href, 'search/people')]")));
                click(groupsSearchTabElement);
                waitForPageLoaded();
                WebElement browseResultsContainerElement = driver.getDriver().findElement(By.id("pagelet_loader_initial_browse_result"));
                List<WebElement> searchResultSectionElementList = browseResultsContainerElement.findElements(By.xpath(".//a[contains(@href, '?ref=br_rs')]"));
                return clickerByHref(searchResultSectionElementList, profileLinkOrId);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean clickerByHref(List<WebElement> webElementList, String checkKey) throws Exception {
        for (WebElement webElement : webElementList) {
            if (webElement.getAttribute("href").contains(checkKey)) {
                scrollElementToMiddle(webElement);
                click(webElement);
                waitForPageLoaded();
                return true;
            }
        }
        return false;
    }

    public void scrollClick(WebElement webElement) throws InterruptedException {
        while (true){
            try{

                int pixel = random(150, 170);
                ((JavascriptExecutor) driver.getDriver()).executeScript("window.scrollBy(0," + pixel + ")");
                Thread.sleep(random(300,500));
                Actions ac = new Actions(driver.getDriver());
                int elementHeight = webElement.getSize().getHeight();
                int elementWidth = webElement.getSize().getWidth();
                int x = elementWidth / 2;
                int y = elementHeight / 2;
                ac.moveToElement(webElement, x, y).build().perform();
                Thread.sleep(500);
                ac.moveToElement(webElement, x, y).click().build().perform();
                delay();
                break;
            }catch (Exception ignored){
                int pixel = random(80, 100);
                ((JavascriptExecutor) driver.getDriver()).executeScript("window.scrollBy(0," + pixel + ")");
                Thread.sleep(random(300,500));
            }
        }
    }

    public boolean isSaler(String statusCaption) {
        statusCaption = statusCaption.toLowerCase();
        for (String saleKeyWord : SALE_KEY_WORDS) {
            if (statusCaption.contains(saleKeyWord)) {
                return true;
            }
        }
        return false;
    }

    public boolean openUrlInNewTab(String urlToOpen) {
        try {
            //open new tab and goto Url
            String jsScriptCode = "window.open(\"" + urlToOpen + "\",\"_blank\");";
            ((JavascriptExecutor) driver.getDriver()).executeScript(jsScriptCode);
            delay();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean modernMobileReturnToNewsFeed(){
        try {
            if(driver.getDriver().getCurrentUrl().contains("m.facebook.com")){
                scrollToTop();
                if(driver.getDriver().findElements(By.xpath(".//a[contains(@href, '/home.php?refid')]")).isEmpty()){
                    driver.goToPage("https://m.facebook.com/");
                    return true;
                } else {
                    try {
                        driver.getDriver().findElement(By.xpath(".//a[contains(@href, '/home.php?refid')]")).click();
                        return true;
                    } catch (Exception ignored){
                        driver.goToPage("https://m.facebook.com/");
                        return true;
                    }

                }
            } else {
                driver.goToPage("https://m.facebook.com/");
                return true;
            }
        } catch (Exception ignored){
            return false;
        }
    }

    public boolean oldMobileReturnToNewsFeed(){
        try {
            if(driver.getDriver().getCurrentUrl().contains("m.facebook.com")){
                scrollToTop();
                if(driver.getDriver().findElements(By.xpath(".//img[@alt='Facebook logo']")).isEmpty()){
                    driver.goToPage("https://m.facebook.com/");
                    return true;
                } else {
                    try {
                        driver.getDriver().findElement(By.xpath(".//img[@alt='Facebook logo']")).click();
                        return true;
                    } catch (Exception ignored){
                        driver.goToPage("https://m.facebook.com/");
                        return true;
                    }

                }
            } else {
                driver.goToPage("https://m.facebook.com/");
                return true;
            }
        } catch (Exception ignored){
            return false;
        }
    }
}
