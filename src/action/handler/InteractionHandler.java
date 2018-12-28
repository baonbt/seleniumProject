package action.handler;

import bot.base.Driver;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import object.dto.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import service.BaseService;
import service.ServerService;
import util.common.DataUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static service.ChatbotService.randomEmotion;
import static util.common.Constant.ACTION.DEFAULT_REACTION_RATE;
import static util.common.Constant.ACTION.INTERTACTION;
import static util.common.Constant.SERVER.SERVER_ACCOUNT_REPORT_V2;
import static util.common.Constant.SERVER.SERVER_COMMENT;
import static util.common.DataUtil.isNullOrEmptyStr;
import static util.common.DataUtil.random;
import static util.common.DataUtil.randomByPercent;

import object.dto.LiveStreamDTO.LiveStreamLinkInfo;

public class InteractionHandler extends BaseAction {
    public InteractionHandler(Driver driver) {
        super(driver);
    }

    public void autoTym(String sessionId, int interactTimes, String likeRate, String commentRate, boolean interactProfileOnly, String interactId, int notLikeGroupPostsPercent, int timeDelay) throws Exception {
        while (true){
            try{
                _returnToNewsFeed();
                ActionControllerDTO actionControllerDTO = new ActionControllerDTO();
                BaseService baseService = new BaseService();
                for (int i = 0; i < interactTimes; i++) {
                    _newsFeed(likeRate, commentRate, interactProfileOnly, notLikeGroupPostsPercent);
                    _scrollDown();
                    Thread.sleep(timeDelay * 1000);
                }

                actionControllerDTO.setActionCode(INTERTACTION);
                actionControllerDTO.setInteractId(interactId);
                actionControllerDTO.setAccountId(driver.getSigma().getAccount().getAccountId());
                actionControllerDTO.setSessionId(sessionId);

                actionControllerDTO = (ActionControllerDTO) DataUtil.jsonStringToObject(
                        baseService.sendPostRequest(driver.getSigma().getBreakLoopUrl(), DataUtil.objectToJsonString(actionControllerDTO)).getMessage(),
                        ActionControllerDTO.class);

                if (actionControllerDTO.getCommandCode() != 0) {
                    break;
                }

                //update cookie
//                int hour = new Date().getHours();
////
////                if (hour % 2 == 0) {
////                    LoggedAccountDTO loggedAccountDTO = new LoggedAccountDTO();
////                    loggedAccountDTO.setLoggedAccount(driver.getSigma().getAccount().getUid());
////                    loggedAccountDTO.setProxyId(driver.getSigma().getAccount().getProxyId());
////                    String cookie = driver.getCookies();
////                    loggedAccountDTO.setCookies(cookie);
////
////                    baseService.sendPostRequest(SERVER_ACCOUNT_REPORT_V2, DataUtil.objectToJsonString(loggedAccountDTO));
////                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void interacting(String sessionId, int interactTimes, String likeRate, String commentRate, boolean interactProfileOnly, String interactId, boolean newsfeedOnly, int replyInboxRate, int notLikeGroupPostsPercent, int timeDelay) throws Exception {
        while (true) {
            try {
                returnToNewsFeed();
                closeAllChatTabs();
                ActionControllerDTO actionControllerDTO = new ActionControllerDTO();
                BaseService baseService = new BaseService();

                for (int i = 0; i < interactTimes; i++) {
                    int randomNewOrMessOrNoti = random(1, 100);
                    if (randomNewOrMessOrNoti < 60 || newsfeedOnly) {
                        int randomPageFeed = random(1, 10);
                        String currentUrl = driver.getDriver().getCurrentUrl();
                        if (randomPageFeed > 9 && !currentUrl.contains("pages/feed") && !interactProfileOnly) {
                            List<WebElement> pagesFeedElementList = driver.getDriver().findElements(By.xpath(".//a[@data-testid='left_nav_item_Pages Feed']"));
                            boolean isInPagesFeed = false;
                            for (WebElement pagesFeedElement : pagesFeedElementList) {
                                if (pagesFeedElement.isDisplayed()) {
                                    click(pagesFeedElement);
                                    waitForPageLoaded();
                                    isInPagesFeed = true;
                                }
                            }
                            if (!isInPagesFeed) {
                                driver.goToPage("https://www.facebook.com/pages/feed?ref=bookmarks");
                            }
                        } else if (randomPageFeed < 9 && currentUrl.contains("pages/feed") && !interactProfileOnly) {
                            returnToNewsFeed();
                        }
                        newsFeed(likeRate, commentRate, interactProfileOnly, notLikeGroupPostsPercent);
                        scrollDown();
                    } else if (randomNewOrMessOrNoti > 80 && !newsfeedOnly) {
                        if (randomByPhanTram(replyInboxRate)) {
                            message();
                        }
                    } else if (!newsfeedOnly) {
                        notification(likeRate, commentRate, interactProfileOnly);
                        returnToNewsFeed();
                    }

                    Thread.sleep(timeDelay * 1000);
                }

                actionControllerDTO.setActionCode(INTERTACTION);
                actionControllerDTO.setInteractId(interactId);
                actionControllerDTO.setAccountId(driver.getSigma().getAccount().getAccountId());
                actionControllerDTO.setSessionId(sessionId);

                actionControllerDTO = (ActionControllerDTO) DataUtil.jsonStringToObject(
                        baseService.sendPostRequest(driver.getSigma().getBreakLoopUrl(), DataUtil.objectToJsonString(actionControllerDTO)).getMessage(),
                        ActionControllerDTO.class);

                if (actionControllerDTO.getCommandCode() != 0) {
                    break;
                }

//                //update cookie
//                int hour = new Date().getHours();
//
//                if (hour % 2 == 0) {
//                    LoggedAccountDTO loggedAccountDTO = new LoggedAccountDTO();
//                    loggedAccountDTO.setLoggedAccount(driver.getSigma().getAccount().getUid());
//                    loggedAccountDTO.setProxyId(driver.getSigma().getAccount().getProxyId());
//                    String cookie = driver.getCookies();
//                    loggedAccountDTO.setCookies(cookie);
//
//                    baseService.sendPostRequest(SERVER_ACCOUNT_REPORT_V2, DataUtil.objectToJsonString(loggedAccountDTO));
//                }
            } catch (Exception e) {
            }
        }
    }


    private void closeAllChatTabs() {
        int breakOut = 0;
        List<WebElement> fbNubFlyoutTitlebarList = driver.getDriver().findElements(By.className("fbNubFlyoutTitlebar"));
        for (WebElement fbNubFlyoutTitlebar : fbNubFlyoutTitlebarList) {
            breakOut = 0;
            List<WebElement> closeButtonList = fbNubFlyoutTitlebar.findElements(By.className("close"));
            while (breakOut < 5 && !closeButtonList.isEmpty()) {
                breakOut = breakOut + 1;
                try {
                    closeButtonList = fbNubFlyoutTitlebar.findElements(By.className("close"));
                    for (WebElement closeButton : closeButtonList) {
                        String closeButtonClassName = closeButton.getAttribute("class");
                        if (closeButtonClassName.contains("close button")) {
                            try {
                                move(closeButton);
                                fbNubFlyoutTitlebar.findElement(By.className("close")).click();
                            } catch (Exception ignored) {
                                ignored.printStackTrace();
                            }
                        }
                    }
                } catch (Exception ignored) {

                }
            }
        }
        fbNubFlyoutTitlebarList = driver.getDriver().findElements(By.className("fbNubFlyoutTitlebar"));
        if (fbNubFlyoutTitlebarList.size() > 3) {
            closeAllChatTabs();
        }
    }

    private String postTypeDetection(WebElement wrapper){
        String result = "type";
        List<WebElement> timeStampElementList = wrapper.findElements(By.className("timestamp"));
        if(timeStampElementList.isEmpty()){
            result = result + "-sponsored";
        }
        List<WebElement> pageHoverCards = wrapper.findElements(By.xpath(".//*[contains(@data-hovercard, 'page.php')]"));
        if(!pageHoverCards.isEmpty()){
            result = result + "-page";
        }
        List<WebElement> applicationHoverCards = wrapper.findElements(By.xpath(".//*[contains(@data-hovercard, 'application.php')]"));
        if(!applicationHoverCards.isEmpty()){
            result = result + "-application";
        }
        List<WebElement> groupHrefs = wrapper.findElements(By.xpath(".//*[contains(@href, '/groups/')]"));
        if(!groupHrefs.isEmpty()){
            result = result + "-group";
        }
        return result;
    }

    private void _newsFeed(String likeRate, String commentRate, boolean interactProfileOnly, int notLikeGroupPostsPercent) throws Exception {
        WebElement contentAreaElement = driver.getDriver().findElement(By.id("contentArea"));
        List<WebElement> userContentWrapperList = contentAreaElement.findElements(By.className("userContentWrapper"));
        if (!userContentWrapperList.isEmpty()) {
            for(WebElement userContentWrapper : userContentWrapperList){
                String postType = postTypeDetection(userContentWrapper);
                if(interactProfileOnly){
                    if(postType.contains("group") || postType.contains("page") || postType.contains("application")  || postType.contains("sponsored")){
                        continue;
                    }
                }

                _scrollElementToMiddle(userContentWrapper);
                _postHandler(userContentWrapper, likeRate, commentRate, interactProfileOnly);
            }
        }
    }

    private void newsFeed(String likeRate, String commentRate, boolean interactProfileOnly, int notLikeGroupPostsPercent) throws Exception {
        WebElement contentAreaElement = driver.getDriver().findElement(By.id("contentArea"));
        List<WebElement> userContentWrapperList = contentAreaElement.findElements(By.className("userContentWrapper"));
        if (!userContentWrapperList.isEmpty()) {
            int randomPost = 0;
            if (userContentWrapperList.size() > 1) {
                randomPost = random(0, userContentWrapperList.size() - 1);
            }
            List<Integer> randomedList = new ArrayList<>();
            randomedList.add(9999);
            for (int i = 0; i < 10; i++) {
                while (randomedList.indexOf(randomPost) != -1) {
                    randomPost = random(0, userContentWrapperList.size() - 1);
                }
                randomedList.add(randomPost);
                if (randomedList.size() == userContentWrapperList.size()) {
                    return;
                }
                List<WebElement> likeButtonList = userContentWrapperList.get(randomPost).findElements(By.className("UFILikeLink"));
                String likeButtonPressedString = "na";
                if (!likeButtonList.isEmpty()) {
                    likeButtonPressedString = likeButtonList.get(0).getAttribute("aria-pressed");
                }
                List<WebElement> groupsPostHrefList = userContentWrapperList.get(randomPost).findElements(By.xpath(".//a[contains(@href, '/groups/')]"));

                boolean notLikeGroupPosts = randomByPhanTram(notLikeGroupPostsPercent);

                if (notLikeGroupPosts && !groupsPostHrefList.isEmpty()) {
                    continue;
                }
                if (interactProfileOnly) {
                    List<WebElement> pagePostList = userContentWrapperList.get(randomPost).findElements(By.xpath(".//a[contains(@data-hovercard, 'page.php')]"));
                    List<WebElement> groupPostList = userContentWrapperList.get(randomPost).findElements(By.xpath(".//a[contains(@data-hovercard, 'group.php')]"));
                    if ((pagePostList.isEmpty() && groupPostList.isEmpty()) && likeButtonPressedString.equals("false")){
                        break;
                    } else if (i == 9 && (!pagePostList.isEmpty() || !groupPostList.isEmpty())) {
                        return;
                    }
                    if (likeButtonPressedString.equals("true")) {
                        return;
                    }
                } else if (likeButtonPressedString.equals("false")) {
                    break;
                }
            }
            scrollElementToMiddle(userContentWrapperList.get(randomPost));
            List<WebElement> isLiveNowList = userContentWrapperList.get(randomPost).findElements(By.xpath(".//span[@class='fcg' and contains(text(), ' is live now.')]"));
            if (!isLiveNowList.isEmpty()) {
                //live
            } else {
                postHandler(userContentWrapperList.get(randomPost), likeRate, commentRate, interactProfileOnly);
            }
        }
    }

    private Boolean closeAllRemainChatTabs() throws Exception {
        List<WebElement> fbChatTypeaheadElementList = driver.getDriver().findElements(By.className("fbChatTypeahead"));
        for (WebElement fbChatTypeaheadElement : fbChatTypeaheadElementList) {
            if (fbChatTypeaheadElement.isDisplayed()) {
                WebElement optionButtonElement = fbChatTypeaheadElement.findElement(By.className("button"));
                click(optionButtonElement);
            }
        }
        try {
            List<WebElement> menuItemElementList = driver.getDriver().findElements(By.xpath("//a[@role='menuitem']"));
            for (WebElement menuItemElement : menuItemElementList) {
                String menuItemElementText = menuItemElement.getText();
                if (menuItemElementText.contains("Close All Chat Tabs")) {
                    menuItemElement.click();
                    delay();
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkNewMessage(String author) {
        for (int i = 1; i < 99; i++) {
            if (author.contains("(" + i + ")")) {
                return true;
            }
        }
        return false;
    }

    private void message() throws Exception {
        List<WebElement> fbDockChatTabFlyoutElementList = driver.getDriver().findElements(By.className("fbDockChatTabFlyout"));
        if (fbDockChatTabFlyoutElementList.size() > 0) {
            if (!closeAllRemainChatTabs()) {
                if (!closeAllRemainChatTabs()) {
                    closeAllRemainChatTabs();
                }
            }
        }
        List<WebElement> mercuryMessagesElementList = driver.getDriver().findElements(By.xpath("//a[@name='mercurymessages']"));
        if (mercuryMessagesElementList.size() > 0) {
            click(mercuryMessagesElementList.get(0));
            delay();
            delay();
            for (int i = 0; i < 6; i++) {
                List<WebElement> seeAllInMessengerElementList = driver.getDriver().findElements(By.xpath("//a[@name='mercurymessages']"));
                if (seeAllInMessengerElementList.size() > 0) {
                    List<WebElement> messageContentsElementList = driver.getDriver().findElements(By.className("messagesContent"));
                    List<WebElement> newMessageContentsElementList = new ArrayList<WebElement>();
                    for (int n = 0; n < messageContentsElementList.size() - 1; n++) {
                        String author = messageContentsElementList.get(n).getText();
                        if (checkNewMessage(author)) {
                            newMessageContentsElementList.add(messageContentsElementList.get(n));
                        }
                        if (newMessageContentsElementList.size() > 5 || n >= 8) {
                            break;
                        }
                    }
                    if (newMessageContentsElementList.size() > 0) {
                        int randomOrder = random(0, newMessageContentsElementList.size() - 1);

                        click(newMessageContentsElementList.get(randomOrder));
                        delay();
                        List<WebElement> focusedTabElementList = driver.getDriver().findElements(By.className("fbDockChatTabFlyout"));
                        if (focusedTabElementList.isEmpty()) {
                            click(newMessageContentsElementList.get(randomOrder));
                            focusedTabElementList = driver.getDriver().findElements(By.className("fbDockChatTabFlyout"));
                            if (focusedTabElementList.isEmpty()) {
                                click(newMessageContentsElementList.get(randomOrder));
                                focusedTabElementList = driver.getDriver().findElements(By.className("fbDockChatTabFlyout"));
                                if (focusedTabElementList.isEmpty()) {
                                    click(newMessageContentsElementList.get(randomOrder));
                                }
                            }
                        }
                        if (!focusedTabElementList.isEmpty()) {
                            String receiver = focusedTabElementList.get(0).findElement(By.className("titlebarText")).getText();
                            List<WebElement> receivedMessageElementList = focusedTabElementList.get(0).findElements(By.className("direction_ltr"));
                            String receivedMessageString = "";
                            String replyContent = "";
                            for (int k = 0; k < receivedMessageElementList.size(); k++) {
                                receivedMessageString = receivedMessageElementList.get(receivedMessageElementList.size() - k - 1).getText();
                                if (!isNullOrEmptyStr(receivedMessageString)) {
                                    receivedMessageString = receivedMessageString.replaceAll("\n", "");
                                    if (!isNullOrEmptyStr(receivedMessageString)) {
                                        if (receivedMessageString.length() > 5) {
                                            replyContent = chatbot.reply(receivedMessageString, driver.getSigma().getAccount().getAccountId());
                                            if ("null".equals(replyContent)) {
                                                int emojiOrSticker = random(0, 99);
                                                if (emojiOrSticker < 49) {
                                                    replyContent = randomEmotion();
                                                } else {
                                                    replyContent = "STICKER";
                                                }
                                            }
                                        }
                                    }
                                }
                                if (!isNullOrEmptyStr(replyContent)) {
                                    break;
                                }
                            }
                            if (DataUtil.isNullOrEmptyStr(replyContent)) {
                                replyContent = randomEmotion();
                            }
                            if (replyContent.equals("STICKER")) {
                                List<WebElement> flyoutButtonElementList = focusedTabElementList.get(0).findElements(By.xpath(".//a[@label='flyout button']"));
                                for (WebElement flyoutButtonElement : flyoutButtonElementList) {
                                    if (flyoutButtonElement.isDisplayed()) {
                                        move(flyoutButtonElement);
                                    }
                                }
                                List<WebElement> chooseAStickerElementList = focusedTabElementList.get(0).findElements(By.xpath(".//a[@title='Choose a sticker']"));
                                if (!chooseAStickerElementList.isEmpty()) {
                                    click(chooseAStickerElementList.get(0));
                                    waitForPageLoaded();
                                    commentBySticker();
                                    delay();
                                    List<WebElement> messageInputBoxElementList = focusedTabElementList.get(0).findElements(By.className("notranslate"));
                                    try {
                                        messageInputBoxElementList.get(0).sendKeys(Keys.ESCAPE);
                                        messageInputBoxElementList.get(0).sendKeys(Keys.ESCAPE);
                                        messageInputBoxElementList.get(0).sendKeys(Keys.ESCAPE);
                                    } catch (Exception ignored) {
                                    }
                                }
                            } else {
                                List<WebElement> messageInputBoxElementList = focusedTabElementList.get(0).findElements(By.className("notranslate"));
                                if (!messageInputBoxElementList.isEmpty()) {
                                    MessageDTO messageDTO = new MessageDTO();
                                    messageInputBoxElementList.get(0).sendKeys(replyContent);
                                    delay();
                                    messageInputBoxElementList.get(0).sendKeys(Keys.ENTER);
                                    delay();
                                    try {
                                        messageInputBoxElementList.get(0).sendKeys(Keys.ESCAPE);
                                        messageInputBoxElementList.get(0).sendKeys(Keys.ESCAPE);
                                        messageInputBoxElementList.get(0).sendKeys(Keys.ESCAPE);
                                    } catch (Exception ignored) {
                                    }
                                    messageDTO.setAccountId(driver.getSigma().getAccount().getAccountId());
                                    messageDTO.setReceiver(receiver);
                                    messageDTO.setReplyContent(replyContent);
                                }
                            }
                        }
                    }
                    break;
                } else {
                    delay();
                    WebElement mercuryMessagesElement = driver.getDriver().findElement(By.xpath("//a[@name='mercurymessages']"));
                    click(mercuryMessagesElement);
                }
            }
        }
    }

    private void _postHandler(WebElement postFrameWebElement, String likeRate, String commentRate, boolean interactProfileOnly) throws Exception {
        //reaction
        InteractionDTO interactionDTO = new InteractionDTO();
        List<WebElement> profileLinkElementList = postFrameWebElement.findElements(By.xpath(".//a[contains(@href, 'www.facebook.com') and contains(@href, '&hc_ref=')]"));
        if (!profileLinkElementList.isEmpty()) {
            interactionDTO.setHostProfileLink(profileLinkElementList.get(0).getAttribute("href"));
        }
        List<WebElement> likeButtonList = postFrameWebElement.findElements(By.className("UFILikeLink"));
        if (!likeButtonList.isEmpty()) {
            String likeButtonPressedString = likeButtonList.get(0).getAttribute("aria-pressed");
            if (likeButtonPressedString.equals("true")) {
                return;
            }
        }
        int reactionCode = randomByPercent(likeRate);
        if (reactionCode != 0) {
            _reaction(reactionCode, likeButtonList);
        }
        interactionDTO.setReaction(reactionCode);
//        // comment
//        int commentCode = randomByPercent(commentRate);
//        String replyContent = "<3";
//        if (commentCode != 0) {
//            switch (commentCode) {
//                case 1:
//                    //chatbot
//                    replyContent = "CHATBOT";
//                    break;
//                case 2:
//                    //EMOTICON
//                    replyContent = "EMOS";
//                    break;
//                case 3:
//                    replyContent = "STICKER";
//                    break;
//            }
//            comment(interactionDTO, postFrameWebElement, replyContent);
//        }
        if (interactionDTO.getReaction() != -1 || interactionDTO.getReplyContent() != null) {
            ServerService.sendDTOToServer(SERVER_COMMENT, interactionDTO);
        }
    }

    private void postHandler(WebElement postFrameWebElement, String likeRate, String commentRate, boolean interactProfileOnly) throws Exception {
        //reaction
        InteractionDTO interactionDTO = new InteractionDTO();
        List<WebElement> profileLinkElementList = postFrameWebElement.findElements(By.xpath(".//a[contains(@href, 'www.facebook.com') and contains(@href, '&hc_ref=')]"));
        if (!profileLinkElementList.isEmpty()) {
            interactionDTO.setHostProfileLink(profileLinkElementList.get(0).getAttribute("href"));
        }
        List<WebElement> likeButtonList = postFrameWebElement.findElements(By.className("UFILikeLink"));
        if (!likeButtonList.isEmpty()) {
            String likeButtonPressedString = likeButtonList.get(0).getAttribute("aria-pressed");
            if (likeButtonPressedString.equals("true")) {
                return;
            }
        }
        int reactionCode = randomByPercent(likeRate);
        if (reactionCode != 0) {
            reaction(reactionCode, likeButtonList);
        }
        interactionDTO.setReaction(reactionCode);
        // comment
        int commentCode = randomByPercent(commentRate);
        String replyContent = "<3";
        if (commentCode != 0) {
            switch (commentCode) {
                case 1:
                    //chatbot
                    replyContent = "CHATBOT";
                    break;
                case 2:
                    //EMOTICON
                    replyContent = "EMOS";
                    break;
                case 3:
                    replyContent = "STICKER";
                    break;
            }
            comment(interactionDTO, postFrameWebElement, replyContent);
        }
        if (interactionDTO.getReaction() != -1 || interactionDTO.getReplyContent() != null) {
            ServerService.sendDTOToServer(SERVER_COMMENT, interactionDTO);
        }
    }

    public void notification(String likeRate, String commentRate, boolean interactProfileOnly) throws Exception {
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 30);
        List<WebElement> notificationIconElementList = driver.getDriver().findElements(By.id("fbNotificationsJewel"));
        if (!notificationIconElementList.isEmpty()) {
            click(notificationIconElementList.get(0));
            delay();
            wait.until(ExpectedConditions.presenceOfElementLocated(By.className("jewelItemNew")));
            delay();
            delay();
            delay();
            List<WebElement> notificationNewItemList = driver.getDriver().findElements(By.className("jewelItemNew"));
            int breakOut = 0;
            for (WebElement notificationNewItem : notificationNewItemList) {
                breakOut = breakOut + 1;
                String stringDataGt = notificationNewItem.getAttribute("data-gt");
                if (stringDataGt != null) {
                    if (stringDataGt.contains("\"unread\":1")) {
                        String notificationContent = notificationNewItem.getText();
                        if (notificationContent.contains("added a photo in")
                                || notificationContent.contains("added new photo")
                                || notificationContent.contains("updated his status")
                                || notificationContent.contains("tagged you in a post")
                                || notificationContent.contains("posted on your timeline")) {
                            if (notificationNewItem.isDisplayed()) {
                                click(notificationNewItem);
                                delay();
                                waitForPageLoaded();
                                break;
                            }
                        } else if (notificationContent.contains("commented on")
                                || notificationContent.contains("also commented")
                                || notificationContent.contains("mentioned you in a comment")) {
                            if (notificationNewItem.isDisplayed()) {
                                click(notificationNewItem);
                                delay();
                                waitForPageLoaded();
                                break;
                            }
                        } else if (notificationContent.contains("added")
                                && notificationContent.contains("photos in")) {
                            if (notificationNewItem.isDisplayed()) {
                                click(notificationNewItem);
                                delay();
                                waitForPageLoaded();
                                break;
                            }
                        }
                    }
                }
                if (breakOut > 6) {
                    return;
                }
            }
            List<WebElement> hightLightedCommentElement = driver.getDriver().findElements(By.xpath(".//div[@data-testid='ufi_highlighted_comment']"));
            if (!hightLightedCommentElement.isEmpty()) {
                replyComment(hightLightedCommentElement.get(0));
            } else {
                List<WebElement> postFromNotificationElementList = driver.getDriver().findElements(By.xpath(".//span[text()='FROM NOTIFICATIONS']"));
                if (!postFromNotificationElementList.isEmpty()) {
                    WebElement postFromNotificationElement = postFromNotificationElementList.get(0).findElement(By.xpath("./following::div[contains(@class, 'userContentWrapper')]"));
                    postHandler(postFromNotificationElement, likeRate, commentRate, interactProfileOnly);
                } else {
                    List<WebElement> userContentWrapperElementList = driver.getDriver().findElements(By.className("userContentWrapper"));
                    if (!userContentWrapperElementList.isEmpty()) {
                        postHandler(userContentWrapperElementList.get(0), likeRate, commentRate, interactProfileOnly);
                    }
                }
            }
        }
    }

    public void _reaction(int reactionCode, List<WebElement> likeButtonList) throws Exception {
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 60);
        for (WebElement likeButton : likeButtonList) {
            String likeButtonPressedString = likeButton.getAttribute("aria-pressed");
            if (likeButton.isDisplayed() && likeButtonPressedString.equals("false")) {
                _scrollElementToMiddle(likeButton);
                _move(likeButton);

                boolean loopLoop = true;
                int count = 0;
                while (loopLoop) {
                    List<WebElement> reactionButtonList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[@data-testid='reaction_2']")));
                    for (WebElement reactionButton : reactionButtonList) {
                        if (reactionButton.isDisplayed()) {
                            loopLoop = false;
                        }
                    }
                    Thread.sleep(500);
                    count++;

                    if (count > 20) {
                        break;
                    }
                }
                List<WebElement> reactionButtonList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[@data-testid='reaction_2']")));
                Thread.sleep(300);
                switch (reactionCode) {
                    case 1:
                        reactionButtonList = driver.getDriver().findElements(By.xpath(".//span[@data-testid='reaction_1']"));
                        break;
                    case 2:
                        reactionButtonList = driver.getDriver().findElements(By.xpath(".//span[@data-testid='reaction_2']"));
                        break;
                    case 3:
                        reactionButtonList = driver.getDriver().findElements(By.xpath(".//span[@data-testid='reaction_4']"));
                        break;
                    case 4:
                        reactionButtonList = driver.getDriver().findElements(By.xpath(".//span[@data-testid='reaction_3']"));
                        break;
                    case 5:
                        reactionButtonList = driver.getDriver().findElements(By.xpath(".//span[@data-testid='reaction_7']"));
                        break;
                    case 6:
                        reactionButtonList = driver.getDriver().findElements(By.xpath(".//span[@data-testid='reaction_8']"));
                        break;
                }
                Thread.sleep(1000);
                for (WebElement reactionButton : reactionButtonList) {
                    if (reactionButton.isDisplayed()) {
                        int loopOut = 0;
                        while (true) {
                            reactionButton.click();
                            Thread.sleep(1000);
                            if (!reactionButton.isDisplayed() || loopOut > 5) {
                                break;
                            }
                            loopOut = loopOut + 1;
                        }
                        break;
                    }
                }
            }
            break;
        }
    }

    public void reaction(int reactionCode, List<WebElement> likeButtonList) throws Exception {
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 60);
        for (WebElement likeButton : likeButtonList) {
            String likeButtonPressedString = likeButton.getAttribute("aria-pressed");
            if (likeButton.isDisplayed() && likeButtonPressedString.equals("false")) {
                scrollElementToMiddle(likeButton);
                move(likeButton);

                boolean loopLoop = true;
                int count = 0;
                while (loopLoop) {
                    List<WebElement> reactionButtonList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[@data-testid='reaction_2']")));
                    for (WebElement reactionButton : reactionButtonList) {
                        if (reactionButton.isDisplayed()) {
                            loopLoop = false;
                        }
                    }
                    Thread.sleep(200);
                    count++;

                    if (count > 20) {
                        break;
                    }
                }
                List<WebElement> reactionButtonList = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//span[@data-testid='reaction_2']")));
                Thread.sleep(300);
                switch (reactionCode) {
                    case 1:
                        reactionButtonList = driver.getDriver().findElements(By.xpath(".//span[@data-testid='reaction_1']"));
                        break;
                    case 2:
                        reactionButtonList = driver.getDriver().findElements(By.xpath(".//span[@data-testid='reaction_2']"));
                        break;
                    case 3:
                        reactionButtonList = driver.getDriver().findElements(By.xpath(".//span[@data-testid='reaction_4']"));
                        break;
                    case 4:
                        reactionButtonList = driver.getDriver().findElements(By.xpath(".//span[@data-testid='reaction_3']"));
                        break;
                    case 5:
                        reactionButtonList = driver.getDriver().findElements(By.xpath(".//span[@data-testid='reaction_7']"));
                        break;
                    case 6:
                        reactionButtonList = driver.getDriver().findElements(By.xpath(".//span[@data-testid='reaction_8']"));
                        break;
                }
                delay();
                for (WebElement reactionButton : reactionButtonList) {
                    if (reactionButton.isDisplayed()) {
                        int loopOut = 0;
                        while (true) {
                            click(reactionButton);
                            Thread.sleep(1000);
                            if (!reactionButton.isDisplayed() || loopOut > 5) {
                                break;
                            }
                            loopOut = loopOut + 1;
                        }
                        break;
                    }
                }
            }
            break;
        }
    }

    public void comment(InteractionDTO interactionDTO, WebElement frameElement, String replyContent) throws Exception {
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 60);
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(frameElement, By.className("userContent")));
        delay();
        String postContent = frameElement.findElement(By.className("userContent")).getText();
        if (replyContent.equals("CHATBOT")) {
            replyContent = answer(postContent);
        } else if (replyContent.equals("EMOS")) {
            replyContent = randomEmotion();
        }
        wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(frameElement, By.className("comment_link")));
        delay();
        List<WebElement> commentButtonList = frameElement.findElements(By.className("comment_link"));
        if (commentButtonList.size() > 0) {
            scrollElementToMiddle(commentButtonList.get(0));
            click(commentButtonList.get(0));
            delay();
            WebElement commentContainerElement = frameElement.findElement(By.className("UFICommentContainer"));
            doingComment(commentContainerElement, replyContent, interactionDTO);
        }
    }

    public void replyComment(WebElement frameElement) throws Exception {
        BaseService baseService = new BaseService();
        WebElement profileLinkElement = frameElement.findElement(By.xpath(".//a[contains(@href, 'www.facebook.com') and contains(@href, 'fref=ufi')]"));

        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 20);

        InteractionDTO interactionDTO = new InteractionDTO();
        interactionDTO.setHostProfileLink(profileLinkElement.getAttribute("href"));
        int reaction = -1;
        int randomReaction = random(1, 100);
        if (randomReaction > 40) {
            reaction = randomByPercent(DEFAULT_REACTION_RATE);
            List<WebElement> likeButtonList = frameElement.findElements(By.className("UFILikeLink"));
            reaction(reaction, likeButtonList);
        }
        interactionDTO.setReaction(reaction);
        // comment
        int randomComment = random(1, 100);
        if (randomComment > 50) {
            String commentContent = frameElement.findElement(By.className("UFICommentBody")).getText();
            String replyContent = answer(commentContent);
            wait.until(ExpectedConditions.presenceOfNestedElementLocatedBy(frameElement, By.className("UFIReplyLink")));
            delay();
            List<WebElement> replyButtonList = frameElement.findElements(By.className("UFIReplyLink"));
            if (replyButtonList.size() > 0) {
                scrollElementToMiddle(replyButtonList.get(0));
                delay();
                click(replyButtonList.get(0));
                delay();

                List<WebElement> replyInputBoxElementList = frameElement.findElements(By.xpath(".//following::div[@data-testid='ufi_reply_composer']"));
                for (WebElement replyInputBoxElement : replyInputBoxElementList) {
                    if (replyInputBoxElement.isDisplayed()) {
                        WebElement commentContainerElement = replyInputBoxElementList.get(0).findElement(By.xpath("../../../../../../../..//div[@class='UFICommentContainer']"));
//                        scrollElementToMiddle(commentContainerElement);
                        if (replyContent.equals("STICKER")) {
                            WebElement stickerButtonElement = commentContainerElement.findElement(By.className("UFICommentStickerIcon"));
                            click(stickerButtonElement);
                            waitForPageLoaded();
                            commentBySticker();
                        } else {
                            insertTextToCommentInputBox(replyInputBoxElement, replyContent, interactionDTO);
                            ServerService.sendDTOToServer(SERVER_COMMENT, interactionDTO);
                        }
                    }
                }
            }
        }
    }

    public String answer(String commentContent) {
        String replyContent = "";
        if (commentContent.length() > 10) {
            replyContent = chatbot.reply(commentContent, driver.getSigma().getAccount().getAccountId());
        }
        if (isNullOrEmptyStr(replyContent) || "null".equals(replyContent)) {
            int emojOrSticker = random(0, 100);
            if (emojOrSticker < 50) {
                replyContent = randomEmotion();
            } else {
                replyContent = "STICKER";
            }
        }
        return replyContent;
    }

    private void doingComment(WebElement commentContainerElement, String replyContent, InteractionDTO interactionDTO) throws Exception {
        scrollElementToMiddle(commentContainerElement);
        if (replyContent.equals("STICKER")) {
            WebElement stickerButtonElement = commentContainerElement.findElement(By.className("UFICommentStickerIcon"));
            click(stickerButtonElement);
            waitForPageLoaded();
            commentBySticker();
        } else {
            List<WebElement> commentInputBoxElementList = commentContainerElement.findElements(By.xpath(".//div[@data-testid='ufi_comment_composer']"));
            for (WebElement commentInputBoxElement : commentInputBoxElementList) {
                if (commentInputBoxElement.isDisplayed()) {
                    insertTextToCommentInputBox(commentInputBoxElementList.get(0), replyContent, interactionDTO);
                    break;
                }
            }
        }
    }

    public void insertTextToCommentInputBox(WebElement commentInputBoxElement, String replyContent, InteractionDTO interactionDTO) throws Exception {
        scrollElementToMiddle(commentInputBoxElement);
        commentInputBoxElement.sendKeys(replyContent);
        delay();
        commentInputBoxElement.sendKeys(Keys.RETURN);
        delay();
        interactionDTO.setReplyContent(replyContent);
        interactionDTO.setAccountId(driver.getSigma().getAccount().getAccountId());
    }

    public void commentBySticker() throws Exception {
        WebElement stickerframeElement = driver.getDriver().findElement(By.xpath("//div[@aria-label='Stickers']"));
        List<WebElement> stickerTabElementList = stickerframeElement.findElements(By.xpath(".//a[@aria-label='Recent']/following-sibling::a[@data-tooltip-content]"));
        if (!stickerTabElementList.isEmpty()) {
            int randomStickerTab = random(0, stickerTabElementList.size() - 1);
            if (randomStickerTab > 2) {
                randomStickerTab = 2;
            }
            scrollElementToMiddle(stickerTabElementList.get(randomStickerTab));
            click(stickerTabElementList.get(randomStickerTab));
            delay();
            WebElement chooseStickerAreaElement = stickerframeElement.findElement(By.className("uiScrollableAreaContent"));
            List<WebElement> stickerElementList = chooseStickerAreaElement.findElements(By.xpath(".//div[@role='presentation']"));
            if (!stickerElementList.isEmpty()) {
                int randomStcker = random(0, stickerElementList.size() - 1);
                click(stickerElementList.get(randomStcker));
                delay();
            }
        }
    }

    public void modernMobileAutoTym(String sessionId, int interactTimes, String likeRate, String commentRate, boolean interactProfileOnly, String interactId, boolean newsfeedOnly, int replyInboxRate, int notLikeGroupPostsPercent, int timeDelay){

        modernMobileReturnToNewsFeed();
        int failCount = 0;
        while (true){
            try{
                ActionControllerDTO actionControllerDTO = new ActionControllerDTO();
                BaseService baseService = new BaseService();
                for (int i = 0; i < interactTimes; i++) {
                    modernMobileFbNewsFeed(likeRate, commentRate, interactProfileOnly, notLikeGroupPostsPercent);
                    _scrollDown();
                    Thread.sleep(timeDelay * 1000);
                }

                actionControllerDTO.setActionCode(INTERTACTION);
                actionControllerDTO.setInteractId(interactId);
                actionControllerDTO.setAccountId(driver.getSigma().getAccount().getAccountId());
                actionControllerDTO.setSessionId(sessionId);

                actionControllerDTO = (ActionControllerDTO) DataUtil.jsonStringToObject(
                        baseService.sendPostRequest(driver.getSigma().getBreakLoopUrl(), DataUtil.objectToJsonString(actionControllerDTO)).getMessage(),
                        ActionControllerDTO.class);

                if (actionControllerDTO.getCommandCode() != 0) {
                    break;
                }

                //update cookie
                int hour = new Date().getHours();

                if (hour % 2 == 0) {
                    LoggedAccountDTO loggedAccountDTO = new LoggedAccountDTO();
                    loggedAccountDTO.setLoggedAccount(driver.getSigma().getAccount().getUid());
                    loggedAccountDTO.setProxyId(driver.getSigma().getAccount().getProxyId());
                    String cookie = driver.getCookies();
                    loggedAccountDTO.setCookies(cookie);

                    baseService.sendPostRequest(SERVER_ACCOUNT_REPORT_V2, DataUtil.objectToJsonString(loggedAccountDTO));
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private boolean modernMobileFbNewsFeed(String likeRate, String commentRate, boolean interactProfileOnly, int notLikeGroupPostsPercent) throws Exception {

        List<WebElement> contentAreaElements = driver.getDriver().findElements(By.id("m_newsfeed_stream"));
        if(contentAreaElements.isEmpty()){
            return false;
        } else {
            List<WebElement> userContentWrapperList = contentAreaElements.get(0).findElements(By.xpath(".//article[contains(@data-store, 'actor_name')]"));
            for(WebElement userContentWrapper : userContentWrapperList){
                String postType = modernMobileFbPostTypeDetection(userContentWrapper);
                if(interactProfileOnly){
                    if(postType.contains("group") || postType.contains("page") || postType.contains("application")  || postType.contains("sponsored")){
                        continue;
                    }
                }

                _scrollElementToMiddle(userContentWrapper);
                if(modernMobileFbPostHandler(userContentWrapper, likeRate, commentRate, interactProfileOnly)){
                    return true;
                }
            }
            return false;
        }

    }
    private String modernMobileFbPostTypeDetection(WebElement webElement){
        String dataFtContent = webElement.getAttribute("data-store");
        if(dataFtContent.contains("page_id") || dataFtContent.contains("page_insights")){
            return "page";
        }
        List<WebElement> hrefElements = webElement.findElements(By.xpath(".//*[@href]"));
        for(WebElement hrefElement: hrefElements){
            if(hrefElement.getAttribute("href").contains("groups")){
                return "group";
            }
        }
        return "normal";
    }

    private boolean modernMobileFbPostHandler(WebElement postFrameWebElement, String likeRate, String commentRate, boolean interactProfileOnly) throws InterruptedException {

        //reaction
        InteractionDTO interactionDTO = new InteractionDTO();
        List<WebElement> likeButtons = postFrameWebElement.findElements(By.xpath(".//a[contains(@data-uri, 'https://m.facebook.com/ufi/reaction/?ft_ent_identifier')]"));
        if (likeButtons.isEmpty()) {

                return false;
        }
        Actions ac = new Actions(driver.getDriver());
        ac.moveToElement(likeButtons.get(0), 1, 1).build().perform();
        Thread.sleep(1000);
        ac.moveToElement(likeButtons.get(0)).clickAndHold().build().perform();
//        ac.moveToElement(likeButtons.get(0)).clickAndHold().contextClick().build().perform();
        Thread.sleep(3000);
//        ac.moveToElement(likeButtons.get(0)).release().build().perform();
        

        Thread.sleep(5000);
        List<WebElement> reactionTypes = driver.getDriver().findElements(By.xpath(".//div[@data-store='{\"reaction\":1}']"));
        int reactionCode = randomByPercent(likeRate);
        switch (reactionCode){
            case 0:
                return false;
            case 1:
                break;
            case 2:
                reactionTypes = driver.getDriver().findElements(By.xpath(".//div[@data-store='{\"reaction\":2}']"));
                break;
            case 3:
                reactionTypes = driver.getDriver().findElements(By.xpath(".//div[@data-store='{\"reaction\":4}']"));
                break;
            case 4:
                reactionTypes = driver.getDriver().findElements(By.xpath(".//div[@data-store='{\"reaction\":3}']"));
                break;
            case 5:
                reactionTypes = driver.getDriver().findElements(By.xpath(".//div[@data-store='{\"reaction\":7}']"));
                break;
            case 6:
                reactionTypes = driver.getDriver().findElements(By.xpath(".//div[@data-store='{\"reaction\":8}']"));
                break;
        }
        if(reactionTypes.isEmpty()){
            reactionTypes = driver.getDriver().findElements(By.xpath(".//div[@data-store='{\"reaction\":1}']"));
        }
        if(!reactionTypes.isEmpty()){
            try{
                for(WebElement reactionType: reactionTypes){
                    if(reactionType.isDisplayed()){
                        reactionType.click();
                    }
                }
                interactionDTO.setReaction(reactionCode);
                if (interactionDTO.getReaction() != -1 || interactionDTO.getReplyContent() != null) {
                    ServerService.sendDTOToServer(SERVER_COMMENT, interactionDTO);
                }
            } catch (Exception ignore){ }
            return true;
        }
        return true;
    }


    public void oldMobileAutoTym(String sessionId, int interactTimes, String likeRate, String commentRate, boolean interactProfileOnly, String interactId, boolean newsfeedOnly, int replyInboxRate, int notLikeGroupPostsPercent, int timeDelay){

        oldMobileReturnToNewsFeed();
        int failCount = 0;
        while (true){
            try{
                ActionControllerDTO actionControllerDTO = new ActionControllerDTO();
                BaseService baseService = new BaseService();
                if(oldMobileFbNewsFeed(likeRate, commentRate, interactProfileOnly, notLikeGroupPostsPercent)){
                    oldMobileReturnToNewsFeed();
                    failCount = 0;
                } else {
                    failCount = failCount + 1;
                }
                if(failCount >=5){
                    oldMobileReturnToNewsFeed();
                }

                actionControllerDTO.setActionCode(INTERTACTION);
                actionControllerDTO.setInteractId(interactId);
                actionControllerDTO.setAccountId(driver.getSigma().getAccount().getAccountId());
                actionControllerDTO.setSessionId(sessionId);

                actionControllerDTO = (ActionControllerDTO) DataUtil.jsonStringToObject(
                        baseService.sendPostRequest(driver.getSigma().getBreakLoopUrl(), DataUtil.objectToJsonString(actionControllerDTO)).getMessage(),
                        ActionControllerDTO.class);

                if (actionControllerDTO.getCommandCode() != 0) {
                    break;
                }

                //update cookie
                int hour = new Date().getHours();

                if (hour % 2 == 0) {
                    LoggedAccountDTO loggedAccountDTO = new LoggedAccountDTO();
                    loggedAccountDTO.setLoggedAccount(driver.getSigma().getAccount().getUid());
                    loggedAccountDTO.setProxyId(driver.getSigma().getAccount().getProxyId());
                    String cookie = driver.getCookies();
                    loggedAccountDTO.setCookies(cookie);

                    baseService.sendPostRequest(SERVER_ACCOUNT_REPORT_V2, DataUtil.objectToJsonString(loggedAccountDTO));
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    private boolean oldMobileFbNewsFeed(String likeRate, String commentRate, boolean interactProfileOnly, int notLikeGroupPostsPercent) throws Exception {

        WebElement contentAreaElement = driver.getDriver().findElement(By.id("m_newsfeed_stream"));
        List<WebElement> userContentWrapperList = contentAreaElement.findElements(By.xpath(".//div[@role='article']"));
        for(WebElement userContentWrapper : userContentWrapperList){
            String postType = oldMobileFbPostTypeDetection(userContentWrapper);
            if(interactProfileOnly){
                if(postType.contains("group") || postType.contains("page") || postType.contains("application")  || postType.contains("sponsored")){
                    continue;
                }
            }

            _scrollElementToMiddle(userContentWrapper);
            if(oldMobileFbPostHandler(userContentWrapper, likeRate, commentRate, interactProfileOnly)){
                return true;
            }
        }
        return false;
    }
    private String oldMobileFbPostTypeDetection(WebElement webElement){
        String dataFtContent = webElement.getAttribute("data-ft");
        if(dataFtContent.contains("page_id") || dataFtContent.contains("page_insights")){
            return "page";
        }
        List<WebElement> hrefElements = webElement.findElements(By.xpath(".//*[@href]"));
        for(WebElement hrefElement: hrefElements){
            if(hrefElement.getAttribute("href").contains("groups")){
                return "group";
            }
        }
        return "normal";
    }
    private boolean oldMobileFbPostHandler(WebElement postFrameWebElement, String likeRate, String commentRate, boolean interactProfileOnly) throws InterruptedException {
        //reaction
        InteractionDTO interactionDTO = new InteractionDTO();
        List<WebElement> reactionPickers = postFrameWebElement.findElements(By.xpath(".//a[contains(@href, '/reactions/picker/')]"));
        List<WebElement> likeButtons = postFrameWebElement.findElements(By.xpath(".//a[contains(@href, '/a/like.php?')]"));

        if (reactionPickers.isEmpty() || likeButtons.isEmpty()) {
            return false;
        }
        for(WebElement reactionPicker: reactionPickers){
            try{
                reactionPicker.click();
                Thread.sleep(500);
            } catch (Exception ignored){};
        }
        Thread.sleep(5000);
        List<WebElement> reactionTypes = driver.getDriver().findElements(By.xpath(".//a[contains(@href, '&reaction_type=1&')]"));
        int reactionCode = randomByPercent(likeRate);
        switch (reactionCode){
            case 0:
                return true;
            case 1:
                break;
            case 2:
                reactionTypes = driver.getDriver().findElements(By.xpath(".//a[contains(@href, '&reaction_type=2&')]"));
                break;
            case 3:
                reactionTypes = driver.getDriver().findElements(By.xpath(".//a[contains(@href, '&reaction_type=4&')]"));
                break;
            case 4:
                reactionTypes = driver.getDriver().findElements(By.xpath(".//a[contains(@href, '&reaction_type=3&')]"));
                break;
            case 5:
                reactionTypes = driver.getDriver().findElements(By.xpath(".//a[contains(@href, '&reaction_type=7&')]"));
                break;
            case 6:
                reactionTypes = driver.getDriver().findElements(By.xpath(".//a[contains(@href, '&reaction_type=8&')]"));
                break;
        }
        if(reactionTypes.isEmpty()){
            reactionTypes = driver.getDriver().findElements(By.xpath(".//a[contains(@href, '&reaction_type=1&')]"));
        }
        if(!reactionTypes.isEmpty()){
            try{
                for(WebElement reactionType: reactionTypes){
                    if(reactionType.isDisplayed()){
                        reactionType.click();
                    }
                }
                interactionDTO.setReaction(reactionCode);
                if (interactionDTO.getReaction() != -1 || interactionDTO.getReplyContent() != null) {
                    ServerService.sendDTOToServer(SERVER_COMMENT, interactionDTO);
                }
            } catch (Exception ignore){ }
        }
        Thread.sleep(10000);
        return true;
    }
}
