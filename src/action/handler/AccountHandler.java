package action.handler;

import action.execution.Executor;
import bot.base.Driver;
import object.dto.AccountInfoDTO;
import object.dto.FriendRawDTO;
import object.dto.LoggedAccountDTO;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import service.BaseService;
import util.common.DataUtil;

import java.util.List;

import static util.common.Constant.SERVER.SERVER_ACCOUNT_REPORT_V2;

public class AccountHandler extends BaseAction {

    public AccountHandler(Driver driver) {
        super(driver);
    }

    public String mobileLogInStatus(){
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 60);
        String currentUrl = driver.getDriver().getCurrentUrl();
        if (currentUrl.contains("checkpoint")) {
            return "checkpoint";
        } else {
            List<WebElement> registerElementList = driver.getDriver().findElements(By.xpath(".//a[@data-sigil='m_reg_button']"));
            List<WebElement> loginButtonElementList = driver.getDriver().findElements(By.xpath(".//button[@data-sigil='m_login_button']"));
            List<WebElement> signUpButtonElementList = driver.getDriver().findElements(By.id("signup-button"));
            if(registerElementList.isEmpty() && loginButtonElementList.isEmpty() && signUpButtonElementList.isEmpty()){
                try{
                    waitForPageLoaded();
                    Thread.sleep(10000);
                    List<WebElement> facebookLogoElements = driver.getDriver().findElements(By.xpath(".//img[@alt='Facebook logo']"));
                    List<WebElement> topBlueBarElements = driver.getDriver().findElements(By.xpath(".//div[@data-sigil='MTopBlueBarHeader']"));
                    if(facebookLogoElements.isEmpty() && !topBlueBarElements.isEmpty()){
                        return "mobile-modern-loggedIn";
                    }
                    if(topBlueBarElements.isEmpty() && !facebookLogoElements.isEmpty()){
                        return "mobile-old-loggedIn";
                    }
                    return "mobile-unknown-loggedIn";

                } catch (Exception e){
                    e.printStackTrace();
                    return "proxyError";
                }
            } else {
                return "mobile-loggedOut";
            }
        }
    }

    public String logInStatus() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 60);
        String currentUrl = driver.getDriver().getCurrentUrl();
        if (currentUrl.contains("checkpoint")) {
            return "checkpoint";
        } else {
            //TODO
            return "loggedIn";
//            List<WebElement> loggedOutElementList = driver.getDriver().findElements(By.className("UIPage_LoggedOut"));
//            if (!loggedOutElementList.isEmpty()) {
//                List<WebElement> dataPolicyElementList = driver.getDriver().findElements(By.xpath(".//a[contains(@href, '/about/privacy/update')]"));
//                List<WebElement> cookiPolicyElementList = driver.getDriver().findElements(By.xpath(".//a[contains(@href, '/policies/cookies')]"));
//                if (!dataPolicyElementList.isEmpty() && !cookiPolicyElementList.isEmpty()) {
//                    return relogin();
//                }
//            }
//            try {
//                wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fbNotificationsJewel")));
//                delay();
//
//                new Executor(driver).updateCookies();
//                return "loggedIn";
//            } catch (Exception e) {
//                e.printStackTrace();
//                return "proxyError";
//            }
        }
    }

    public String relogin() {
        List<WebElement> reloginList = driver.getDriver().findElements(By.xpath(".//a[contains(@href, '/login/?cuid')]"));
        if (!reloginList.isEmpty()) {
            reloginList.get(0).click();
            List<WebElement> pwInsertFameList = driver.getDriver().findElements(By.xpath(".//form[@action='/login/device-based/regular/login/']"));
            if (!pwInsertFameList.isEmpty()) {
                List<WebElement> inputPwBox = pwInsertFameList.get(0).findElements(By.id("pass"));
                if (!inputPwBox.isEmpty()) {
                    if (!DataUtil.isNullOrEmptyStr(driver.getSigma().getAccount().getPassword())) {
                        inputPwBox.get(0).sendKeys(driver.getSigma().getAccount().getPassword());
                    }

//                    inputPwBox.get(0).sendKeys(Keys.ENTER);
                    List<WebElement> loginButtonList = pwInsertFameList.get(0).findElements(By.className("selected"));
                    for (WebElement loginButton : loginButtonList) {
                        try {
//                            click(loginButton);
                            loginButton.click();
                        } catch (Exception ignored) {
                        }
                    }
                }
                try {
                    delay();
                    waitForPageLoaded();
                    delay();
                } catch (Exception e) {
                    return "loggedOut";
                }
            }
        } else {
            List<WebElement> uidInputElementList = driver.getDriver().findElements(By.xpath(".//input[@data-testid='royal_email']"));
            if (!uidInputElementList.isEmpty()) {
                uidInputElementList.get(0).sendKeys(driver.getSigma().getAccount().getUid());
            }
            List<WebElement> pwInputElementList = driver.getDriver().findElements(By.xpath(".//input[@data-testid='royal_pass']"));
            if (!pwInputElementList.isEmpty()) {
                if (!DataUtil.isNullOrEmptyStr(driver.getSigma().getAccount().getPassword())) {
                    pwInputElementList.get(0).sendKeys(driver.getSigma().getAccount().getPassword());
                }
            }
            List<WebElement> loginButtonList = driver.getDriver().findElements(By.className("uiButtonConfirm"));
            for (WebElement loginButton : loginButtonList) {
                try {
                    click(loginButton);
                } catch (Exception ignored) {
                }
            }
        }
        try {
            WebDriverWait wait = new WebDriverWait(driver.getDriver(), 60);
            wait.until(ExpectedConditions.presenceOfElementLocated(By.id("fbNotificationsJewel")));
            delay();
            for (int i = 0; i < 3; i++) {
                try {
                    WebElement ufiLikeLink = driver.getDriver().findElement(By.className("UFILikeLink"));
                    new Executor(driver).updateCookies();
                    if (driver.getDriver().getCurrentUrl().contains("login")) {
                        return "wrongPwd";
                    }
                    return "loggedIn";
                } catch (Exception e) {
                    returnToNewsFeed();
                    scrollDown();
                }
            }
            if (driver.getDriver().getCurrentUrl().contains("login")) {
                return "wrongPwd";
            }

            new Executor(driver).updateCookies();
            return "loggedIn-Lagging";
        } catch (Exception e) {
        }
        if (driver.getDriver().getCurrentUrl().contains("login")) {
            return "wrongPwd";
        }
        return "loggedOut";
    }


    public String initialSetup(AccountInfoDTO accountInfoDTO, FriendRawDTO friendRawDTO) throws Exception {
        String result = "initialSetup";
        DataHandler dataHandler = new DataHandler(driver);
        result = result + "-" + englishChanger();
        result = result + "-" + turnOffChatAndVoiceCall();
        result = result + "-" + turnOffLiveNotification();
        result = result + "-" + dataHandler.crawlSelfAccountInfo(accountInfoDTO, friendRawDTO, driver.getSigma().getAccount().getAccountId());
        return result;
    }

    private String englishChanger() throws InterruptedException {
        String result = "language_unknown";
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 60);
        driver.goToPage("https://www.facebook.com/settings?tab=language");
        List<WebElement> sampleSentenceList = driver.getDriver().findElements(By.xpath(".//h3[text()='What language do you want to use Facebook in?']"));
        if (sampleSentenceList.isEmpty()) {
            List<WebElement> englishElementList = driver.getDriver().findElements(By.xpath(".//strong[text()='English (US)']"));
            if (englishElementList.isEmpty()) {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.className("fbSettingsListItemContent")));
                delay();
                List<WebElement> fbSettingsListItemContentElementList = driver.getDriver().findElements(By.className("fbSettingsListItemContent"));
                click(fbSettingsListItemContentElementList.get(0));
                delay();
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//select[@name='new_language']")));
                Select dropdown = new Select(driver.getDriver().findElement(By.xpath("//select[@name='new_language']")));
                try {
                    dropdown.selectByValue("en_US");
                } catch (Exception ignored) {
                }
                List<WebElement> saveElementList = driver.getDriver().findElements(By.className("uiButtonConfirm"));
                for (WebElement saveElement : saveElementList) {
                    if (saveElement.isDisplayed() && saveElement.isEnabled()) {
                        try {
                            while (true) {
                                click(saveElement);
                                delay();
                                if (!saveElement.isEnabled() || !saveElement.isDisplayed()) {
                                    break;
                                }
                            }
                        } catch (Exception ignored) {

                        }
                    }
                }
                delay();
                result = "language_eng";
            }
        } else {
            result = "language_eng";
        }
        return result;
    }

    private String turnOffChatAndVoiceCall() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 30);
        List<WebElement> fbChatTypeaheadElementList = driver.getDriver().findElements(By.className("fbChatTypeahead"));
        for (WebElement fbChatTypeaheadElement : fbChatTypeaheadElementList) {
            if (fbChatTypeaheadElement.isDisplayed()) {
                WebElement optionButtonElement = fbChatTypeaheadElement.findElement(By.className("button"));
                click(optionButtonElement);
                List<WebElement> turnOffChatElementList = driver.getDriver().findElements(By.xpath("//span[text()='Turn Off Chat']"));
                if (!turnOffChatElementList.isEmpty()) {
                    turnOffChatElementList.get(0).click();
                    delay();
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("offlineSection")));
                    delay();
                    WebElement offlineSection = driver.getDriver().findElement(By.className("offlineSection"));
                    offlineSection.click();
                    delay();
                    WebElement layerConfirm = driver.getDriver().findElement(By.className("layerConfirm"));
                    layerConfirm.click();
                    delay();
                    optionButtonElement = fbChatTypeaheadElement.findElement(By.className("button"));
                    click(optionButtonElement);
                }
                List<WebElement> turnOffVideoElementList = driver.getDriver().findElements(By.xpath("//span[text()='Turn Off Video/Voice Calls']"));
                if (!turnOffVideoElementList.isEmpty()) {
                    turnOffVideoElementList.get(0).click();
                    delay();
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.className("uiInputLabelLabel")));
                    delay();
                    List<WebElement> disableChoiceList = driver.getDriver().findElements(By.className("uiInputLabelLabel"));
                    for (WebElement disableChoice : disableChoiceList) {
                        if (disableChoice.getText().contains("Until I turn it back on")) {
                            disableChoice.click();
                            delay();
                            WebElement layerConfirm = driver.getDriver().findElement(By.className("layerConfirm"));
                            layerConfirm.click();
                            delay();
                        }
                    }
                }
            }
        }
        return "chatAndVoice_off";
    }

    private String turnOffLiveNotification() throws InterruptedException {
        driver.goToPage("https://www.facebook.com/settings?tab=notifications&section=on_facebook&view");
        waitForPageLoaded();
        List<WebElement> liveNotificationSettingSectionElementList = driver.getDriver().findElements(By.xpath("//li[@id='live_video_notification']"));
        if (!liveNotificationSettingSectionElementList.isEmpty()) {
            WebElement onOffButtonElement = liveNotificationSettingSectionElementList.get(0).findElement(By.xpath(".//a[@role='button']"));
            if (onOffButtonElement.getText().contains("On")) {
                scrollElementToMiddle(onOffButtonElement);
                click(onOffButtonElement);
                List<WebElement> selectionElementList = driver.getDriver().findElements(By.className("uiContextualLayerBelowLeft"));
                if (!selectionElementList.isEmpty()) {
                    List<WebElement> onOffSelectionElementList = driver.getDriver().findElements(By.xpath(".//a[@role='menuitemcheckbox']"));
                    if (!onOffSelectionElementList.isEmpty()) {
                        onOffSelectionElementList.get(2).click();
                    }
                }
            }
        }
        return "liveNotification_off";
    }
}
