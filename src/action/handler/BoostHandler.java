package action.handler;

import bot.base.Driver;
import object.dto.*;
import org.apache.commons.lang3.ObjectUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import service.BaseService;
import service.ServerService;
import util.common.DataUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static util.common.Constant.SERVER.*;
import static util.common.DataUtil.isNullOrEmptyStr;
import static util.common.DataUtil.random;
import static util.common.DataUtil.randomByPercent;

public class BoostHandler extends BaseAction {
    public BoostHandler(Driver driver) {
        super(driver);
    }

    private static final int LIVE_ENDED = 6;
    private static final int WATCHING_LIVE_LINK = 7;

    public void listenerExecutor(List<String> customerUidList) throws Exception {
        ActionControllerDTO actionControllerDTO = new ActionControllerDTO();
        BaseService baseService = new BaseService();

        while (true) {
            actionControllerDTO.setAccountId(driver.getSigma().getAccount().getAccountId());
            actionControllerDTO.setActionCode(502);

            actionControllerDTO = (ActionControllerDTO) DataUtil.jsonStringToObject(
                    baseService.sendPostRequest(driver.getSigma().getBreakLoopUrl(), DataUtil.objectToJsonString(actionControllerDTO)).getMessage(),
                    ActionControllerDTO.class);

            if (actionControllerDTO.getCommandCode() == 1) {
                break;
            }

            listener(customerUidList);
            Thread.sleep(random(10000, 30000));
        }
    }

    public void liveStreamExecutor() throws Exception {
        ActionControllerDTO actionControllerDTO = new ActionControllerDTO();
        BaseService baseService = new BaseService();
        LiveStreamDTO liveStreamDTO = new LiveStreamDTO();

        while (true) {
            actionControllerDTO.setAccountId(driver.getSigma().getAccount().getAccountId());
            actionControllerDTO.setActionCode(501);

            actionControllerDTO = (ActionControllerDTO) DataUtil.jsonStringToObject(
                    baseService.sendPostRequest(driver.getSigma().getBreakLoopUrl(), DataUtil.objectToJsonString(actionControllerDTO)).getMessage(),
                    ActionControllerDTO.class);

            if (actionControllerDTO.getCommandCode() == 1) {
                ServerService.sendObjectToServer(driver.getSigma().getOfflineUrl(), driver.getSigma().getAccount().getAccountId());
                break;
            }

            liveStreamDTO.setLiveStreamLinkInfos(liveStreamChecker(liveStreamDTO));
            liveStreamDTO.setAccountId(driver.getSigma().getAccount().getAccountId());
            baseService.sendPostRequest(BUFF_LIVE, DataUtil.objectToJsonString(liveStreamDTO));
        }
    }


    public void profileLookup(String profileLinkToCrawl) throws Exception {
        BaseService baseService = new BaseService();
        if (!profileLinkToCrawl.contains("https://www.facebook.com/")) {
            profileLinkToCrawl = "https://www.facebook.com/" + profileLinkToCrawl;
        }
        int randomScroll = random(5, 10);
        for (int i = 0; i < randomScroll; i++) {
            scroll(500);
            waitForPageLoaded();
        }
        driver.goToPage(profileLinkToCrawl);
        List<WebElement> userContentWrapperList = driver.getDriver().findElements(By.className("userContentWrapper"));
        int i = 0;
        for (WebElement userContentWrapper : userContentWrapperList) {
            ListenerReportCTSDTO boosterReportCtsDTO = new ListenerReportCTSDTO();
            String hashTag = "||";
            WebElement profileLinkContainerElement = userContentWrapper.findElement(By.xpath(".//span[contains(@class,'fwn fcg')]"));
            WebElement profileLinkElement = profileLinkContainerElement.findElement(By.xpath(".//a[@href]"));
            boosterReportCtsDTO.setProfileLink(profileLinkElement.getAttribute("href"));

            List<WebElement> isLiveNowList = userContentWrapper.findElements(By.xpath(".//span[@class='fcg' and contains(text(), ' is live now.')]"));
            if (!isLiveNowList.isEmpty()) {
                //live
                List<WebElement> hrefElementList = userContentWrapper.findElements(By.xpath(".//a[@data-channel-caller='channel_view_from_newsfeed']"));
                if (!hrefElementList.isEmpty()) {
                    String liveStreamLink = hrefElementList.get(0).getAttribute("href");
                    List<WebElement> hashTagElementList = userContentWrapper.findElements(By.xpath(".//a[contains(@href, '/hashtag/')]"));
                    for (WebElement hashTagElement : hashTagElementList) {
                        hashTag = hashTag + "AZZB" + hashTagElement.getText();
                    }
                    boosterReportCtsDTO.setPostLink(liveStreamLink);
                    boosterReportCtsDTO.setHashTag(hashTag);
                    boosterReportCtsDTO.setPostType(1);
                    baseService.sendPostRequest(driver.getSigma().getLoopReportUrl(), DataUtil.objectToJsonString(boosterReportCtsDTO));
                }
            } else {
                //normal post
                WebElement livetimestampElement = userContentWrapper.findElement(By.className("livetimestamp"));

                List<WebElement> userContentElement = userContentWrapper.findElements(By.className("userContent"));
                if (!userContentElement.isEmpty()) {
                    String postContent = userContentElement.get(0).getText();
                    boosterReportCtsDTO.setContent(postContent);
                }

                WebElement postLinkElement = livetimestampElement.findElement(By.xpath("./parent::a"));
                String postLink = postLinkElement.getAttribute("href");
                List<WebElement> hashTagElementList = userContentWrapper.findElements(By.xpath(".//a[contains(@href, '/hashtag/')]"));
                for (WebElement hashTagElement : hashTagElementList) {
                    hashTag = hashTag + "AZZB" + hashTagElement.getText();
                }
                boosterReportCtsDTO.setPostLink(postLink);
                boosterReportCtsDTO.setHashTag(hashTag);
                boosterReportCtsDTO.setPostType(0);
                baseService.sendPostRequest(driver.getSigma().getLoopReportUrl(), DataUtil.objectToJsonString(boosterReportCtsDTO));
            }
            i = i + 1;
            if(i>=5){
                break;
            }
        }
    }

    private void listener(List<String> customerPROFILeLINkList) throws Exception {
        BaseService baseService = new BaseService();
        returnToNewsFeed();
        WebElement contentAreaElement = driver.getDriver().findElement(By.id("contentArea"));
        List<WebElement> userContentWrapperList = contentAreaElement.findElements(By.className("userContentWrapper"));
        List<String> profileLinkToUnfollowList = new ArrayList<>();
        for (WebElement userContentWrapper : userContentWrapperList) {
            ListenerReportCTSDTO boosterReportCtsDTO = new ListenerReportCTSDTO();
            String hashTag = "||";
            List<WebElement> seeFirstStarList = userContentWrapper.findElements(By.xpath(".//i[contains(@data-tooltip-content, 'You chose to see')]"));
            if (!seeFirstStarList.isEmpty()) {
                WebElement profileLinkContainerElement = userContentWrapper.findElement(By.xpath(".//span[contains(@class,'fwn fcg')]"));
                WebElement profileLinkElement = profileLinkContainerElement.findElement(By.xpath(".//a[@href]"));
                String profileLinkString = profileLinkElement.getAttribute("href");
                boosterReportCtsDTO.setProfileLink(profileLinkString);
                boolean isNotCustomer = true;
                for(String customerPROFILeLINk:customerPROFILeLINkList){
                    if(profileLinkString.contains(customerPROFILeLINk)){
                        isNotCustomer = false;
                    }
                }
                if(isNotCustomer){
                    profileLinkToUnfollowList.add(profileLinkString);
                    continue;
                }
                List<WebElement> isLiveNowList = userContentWrapper.findElements(By.xpath(".//span[@class='fcg' and contains(text(), ' is live now.')]"));
                if (!isLiveNowList.isEmpty()) {
                    //live
                    List<WebElement> hrefElementList = userContentWrapper.findElements(By.xpath(".//a[@data-channel-caller='channel_view_from_newsfeed']"));
                    if (!hrefElementList.isEmpty()) {
                        String liveStreamLink = hrefElementList.get(0).getAttribute("href");
                        List<WebElement> hashTagElementList = userContentWrapper.findElements(By.xpath(".//a[contains(@href, '/hashtag/')]"));
                        for (WebElement hashTagElement : hashTagElementList) {
                            hashTag = hashTag + "AZZB" + hashTagElement.getText();
                        }
                        boosterReportCtsDTO.setPostLink(liveStreamLink);
                        boosterReportCtsDTO.setHashTag(hashTag);
                        boosterReportCtsDTO.setPostType(1);
                        baseService.sendPostRequest(driver.getSigma().getLoopReportUrl(), DataUtil.objectToJsonString(boosterReportCtsDTO));
                    }
                } else {
                    //normal post
                    WebElement livetimestampElement = userContentWrapper.findElement(By.className("livetimestamp"));

                    List<WebElement> userContentElement = userContentWrapper.findElements(By.className("userContent"));
                    if (!userContentElement.isEmpty()) {
                        String postContent = userContentElement.get(0).getText();
                        boosterReportCtsDTO.setContent(postContent);
                    }

                    WebElement postLinkElement = livetimestampElement.findElement(By.xpath("./parent::a"));
                    String postLink = postLinkElement.getAttribute("href");
                    List<WebElement> hashTagElementList = userContentWrapper.findElements(By.xpath(".//a[contains(@href, '/hashtag/')]"));
                    for (WebElement hashTagElement : hashTagElementList) {
                        hashTag = hashTag + "AZZB" + hashTagElement.getText();
                    }
                    boosterReportCtsDTO.setPostLink(postLink);
                    boosterReportCtsDTO.setHashTag(hashTag);
                    boosterReportCtsDTO.setPostType(0);
                    baseService.sendPostRequest(driver.getSigma().getLoopReportUrl(), DataUtil.objectToJsonString(boosterReportCtsDTO));
                }
            }
        }
        for(String profileLinkToUnfollow: profileLinkToUnfollowList){
            CustomerHandler customerHandler = new CustomerHandler(driver);
            customerHandler.unfollow(profileLinkToUnfollow);
        }
    }

    private void reportUnavailableBuff(Long buffScheduleId) {
        ReportBuffNADTO reportBuffNADTO = new ReportBuffNADTO();
        reportBuffNADTO.setBuffScheduleId(buffScheduleId);

        ServerService.sendDTOToServer(driver.getSigma().getBuffReportUrl(), reportBuffNADTO);
    }

    public InteractionDTO boostAPost(PostBoosterSTCDTO postBoosterSTCDTO) throws Exception {
        InteractionDTO interactionDTO = null;

        returnToNewsFeed();
        driver.goToPage(postBoosterSTCDTO.getPostLink());
        waitForPageLoaded();
        List<WebElement> notAvailabePostList = driver.getDriver().findElements(By.className("interstitialHeader"));
        for(WebElement notAvailabePost: notAvailabePostList){
            String notAvailableText = notAvailabePost.getText();
            if(notAvailableText.contains("nội dung này hiện không khả dụng") || notAvailableText.contains("this content isn't available right now")){
                reportUnavailableBuff(postBoosterSTCDTO.getBuffScheduleId());
            }
        }
        List<WebElement> pageTitleList = driver.getDriver().findElements(By.id("pageTitle"));
        for(WebElement pageTitle: pageTitleList){
            String pageTitleText = pageTitle.getText();
            if(pageTitleText.contains("Page Not Found")){
                reportUnavailableBuff(postBoosterSTCDTO.getBuffScheduleId());
            }
        }

        InteractionHandler interactionHandler = new InteractionHandler(driver);
        String replyContent = postBoosterSTCDTO.getReplyContent();
        String currentUrl = driver.getDriver().getCurrentUrl();
        if (currentUrl.contains("/videos/")) {
            //link video
            //reaction
            String[] linkSegment = currentUrl.split("videos");
            int reactionCode = -1;
            interactionDTO = new InteractionDTO();
            interactionDTO.setHostProfileLink(linkSegment[0]);

            WebElement sauBonBonNamElement = driver.getDriver().findElement(By.xpath(".//div[@class='_6445']"));
            WebElement profileLinkElement = sauBonBonNamElement.findElement(By.className("profileLink"));
            interactionDTO.setHostName(profileLinkElement.getText());
            if (replyContent.equals("CHATBOT_REPLY_COMMENT")) {
                List<WebElement> commentLinkElementList = driver.getDriver().findElements(By.className("comment_link"));
                if (commentLinkElementList.isEmpty()) {
                    interactionDTO.setReplyContent("commentDisabled");
                    interactionDTO.setAccountId(driver.getSigma().getAccount().getAccountId());
                }

                for (WebElement commentLinkElement : commentLinkElementList) {
                    if (commentLinkElement.isDisplayed()) {
                        click(commentLinkElement);
                        delay();
                        WebElement uifContainerElement = driver.getDriver().findElement(By.className("UFIContainer"));
                        List<WebElement> commentContentBlockElementList = uifContainerElement.findElements(By.className("UFICommentContentBlock"));
                        for (WebElement commentContentBlockElement : commentContentBlockElementList) {
                            WebElement commentBodyElement = commentContentBlockElement.findElement(By.className("UFICommentBody"));
                            String commentBodyText = commentBodyElement.getText();
                            if (!isNullOrEmptyStr(commentBodyText)) {
                                if (commentBodyText.length() > 5) {
                                    if (!postBoosterSTCDTO.getReactionRate().equals("na")) {
                                        List<WebElement> likeButtonElementList = commentContentBlockElement.findElements(By.xpath(".//a[@data-testid='ufi_comment_like_link']"));
                                        reactionCode = randomByPercent(postBoosterSTCDTO.getReactionRate());
                                        interactionHandler.reaction(reactionCode, likeButtonElementList);
                                    }
                                    interactionDTO.setReaction(reactionCode);
                                    if (!replyContent.equals("na")) {
                                        WebElement replyButtonElement = commentContentBlockElement.findElement(By.className("UFIReplyLink"));
                                        click(replyButtonElement);
                                        List<WebElement> UFIReplyListElementList = sauBonBonNamElement.findElements(By.className("UFIReplyList"));
                                        for (WebElement UFIReplyListElement : UFIReplyListElementList) {
                                            List<WebElement> UFIInputContainerElementList = UFIReplyListElement.findElements(By.className("UFIInputContainer"));
                                            if (!UFIInputContainerElementList.isEmpty()) {
                                                replyContent = interactionHandler.answer(commentBodyText);
                                                if (replyContent.equals("STICKER")) {
                                                    WebElement stickerButtonElement = UFIInputContainerElementList.get(0).findElement(By.className("UFICommentStickerIcon"));
                                                    click(stickerButtonElement);
                                                    delay();
                                                    interactionHandler.commentBySticker();
                                                    interactionDTO.setReplyContent("STICKER");
                                                } else {
                                                    WebElement commentInputBoxElement = UFIInputContainerElementList.get(0).findElement(By.xpath(".//div[@data-testid='ufi_comment_composer']"));
                                                    interactionHandler.insertTextToCommentInputBox(commentInputBoxElement, replyContent, interactionDTO);
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    }
                }
            } else {
                if (!postBoosterSTCDTO.getReactionRate().equals("na")) {
                    List<WebElement> likeButtonElementList = sauBonBonNamElement.findElements(By.xpath(".//a[@data-testid='fb-ufi-likelink']"));
                    reactionCode = randomByPercent(postBoosterSTCDTO.getReactionRate());
                    interactionHandler.reaction(reactionCode, likeButtonElementList);
                }
                interactionDTO.setReaction(reactionCode);
                if (!replyContent.equals("na")) {
                    List<WebElement> commentLinkElementList = driver.getDriver().findElements(By.className("comment_link"));
                    if (commentLinkElementList.isEmpty()) {
                        interactionDTO.setReplyContent("commentDisabled");
                        interactionDTO.setAccountId(driver.getSigma().getAccount().getAccountId());
                    }
                    for (WebElement commentLinkElement : commentLinkElementList) {
                        if (commentLinkElement.isDisplayed()) {
                            click(commentLinkElement);
                            delay();
                            if (replyContent.equals("CHATBOT")) {
                                List<WebElement> irgElementList = sauBonBonNamElement.findElements(By.xpath(".//div[@class='_1rg-']"));
                                for (WebElement irgElement : irgElementList) {
                                    if (irgElement.isDisplayed()) {
                                        replyContent = interactionHandler.answer(irgElement.getText());
                                    }
                                }
                            }
                            List<WebElement> UFICommentContainerElementList = driver.getDriver().findElements(By.className("UFICommentContainer"));
                            for (WebElement UFICommentContainerElement : UFICommentContainerElementList) {
                                if (UFICommentContainerElement.isDisplayed()) {
                                    if (replyContent.equals("STICKER")) {
                                        WebElement stickerButtonElement = UFICommentContainerElement.findElement(By.className("UFICommentStickerIcon"));
                                        click(stickerButtonElement);
                                        delay();
                                        interactionHandler.commentBySticker();
                                    } else {
                                        WebElement commentInputBoxElement = UFICommentContainerElement.findElement(By.xpath(".//div[@data-testid='ufi_comment_composer']"));
                                        interactionHandler.insertTextToCommentInputBox(commentInputBoxElement, replyContent, interactionDTO);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            if (interactionDTO.getReaction() != -1 || interactionDTO.getReplyContent() != null) {
                ServerService.sendDTOToServer(SERVER_COMMENT, interactionDTO);
            }
        } else {
            List<WebElement> fbPhotoSnowliftContainerElementList = driver.getDriver().findElements(By.className("fbPhotoSnowliftContainer"));
            if (!fbPhotoSnowliftContainerElementList.isEmpty()) {
                //photo lift
                WebElement fbPhotoSnowliftFeedbackElement = fbPhotoSnowliftContainerElementList.get(0).findElement(By.id("fbPhotoSnowliftFeedback"));
                List<WebElement> commentInputBoxElementList = fbPhotoSnowliftFeedbackElement.findElements(By.className("notranslate"));
                for (WebElement commentInputBoxElement : commentInputBoxElementList) {
                    if (commentInputBoxElement.isDisplayed()) {
                        commentInputBoxElement.sendKeys(Keys.ESCAPE);
                        delay();
                    }
                }
            }
            int reactionCode = -1;
            interactionDTO = new InteractionDTO();
            WebElement userContentWrapperElement = driver.getDriver().findElement(By.className("userContentWrapper"));
            WebElement profileLinkContainerElement = userContentWrapperElement.findElement(By.xpath(".//span[contains(@class,'fwn fcg')]"));
            WebElement profileLinkElement = profileLinkContainerElement.findElement(By.xpath(".//a[@href]"));
            interactionDTO.setHostProfileLink(profileLinkElement.getAttribute("href"));
            interactionDTO.setHostName(profileLinkContainerElement.getText());

            if (replyContent.equals("CHATBOT_REPLY_COMMENT")) {
                List<WebElement> commentContentBlockElementList = userContentWrapperElement.findElements(By.className("UFICommentContentBlock"));
                for (int i = commentContentBlockElementList.size() - 1; i < 0; i = i - 1) {
                    WebElement commentBodyElement = commentContentBlockElementList.get(i).findElement(By.className("UFICommentBody"));
                    String commentBodyText = commentBodyElement.getText();
                    if (!isNullOrEmptyStr(commentBodyText)) {
                        if (commentBodyText.length() > 5) {
                            WebElement replyButtonElement = commentContentBlockElementList.get(i).findElement(By.className("UFIReplyLink"));
                            scrollElementToMiddle(replyButtonElement);
                            if (postBoosterSTCDTO.getReactionRate().equals("na")) {
                                List<WebElement> likeButtonElementList = commentContentBlockElementList.get(i).findElements(By.xpath(".//a[@data-testid='ufi_comment_like_link']"));
                                reactionCode = randomByPercent(postBoosterSTCDTO.getReactionRate());
                                interactionHandler.reaction(reactionCode, likeButtonElementList);
                            }
                            interactionDTO.setReaction(reactionCode);
                            click(replyButtonElement);
                            delay();
                            List<WebElement> replyInputBoxElementList = commentContentBlockElementList.get(i).findElements(By.xpath(".//following::div[@data-testid='ufi_reply_composer']"));
                            for (WebElement replyInputBoxElement : replyInputBoxElementList) {
                                if (replyInputBoxElement.isDisplayed()) {
                                    WebElement commentContainerElement = replyInputBoxElementList.get(0).findElement(By.xpath("../../../../../../../..//div[@class='UFICommentContainer']"));
                                    if (replyContent.equals("STICKER")) {
                                        WebElement stickerButtonElement = commentContainerElement.findElement(By.className("UFICommentStickerIcon"));
                                        click(stickerButtonElement);
                                        waitForPageLoaded();
                                        interactionHandler.commentBySticker();
                                    } else {
                                        interactionHandler.insertTextToCommentInputBox(replyInputBoxElement, replyContent, interactionDTO);
                                        interactionDTO.setReplyContent(replyContent);
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            } else {
                if (!postBoosterSTCDTO.getReactionRate().equals("na")) {
                    List<WebElement> likeButtonElementList = userContentWrapperElement.findElements(By.xpath(".//a[@data-testid='fb-ufi-likelink']"));
                    reactionCode = randomByPercent(postBoosterSTCDTO.getReactionRate());
                    interactionHandler.reaction(reactionCode, likeButtonElementList);
                }
                interactionDTO.setReaction(reactionCode);
                if (!replyContent.equals("na")) {
                    List<WebElement> commentLinkElementList = userContentWrapperElement.findElements(By.className("comment_link"));
                    if (commentLinkElementList.isEmpty()) {
                        interactionDTO.setReplyContent("commentDisabled");
                        interactionDTO.setAccountId(driver.getSigma().getAccount().getAccountId());
                    }
                    for (WebElement commentLinkElement : commentLinkElementList) {
                        if (commentLinkElement.isDisplayed()) {
                            click(commentLinkElement);
                            delay();
                            if (replyContent.equals("CHATBOT")) {
                                WebElement userContentElement = userContentWrapperElement.findElement(By.className("userContent"));
                                replyContent = interactionHandler.answer(userContentElement.getText());
                            }
                            List<WebElement> commentContainerElementList = userContentWrapperElement.findElements(By.className("UFICommentContainer"));
                            for (WebElement commentContainerElement : commentContainerElementList) {
                                if (commentContainerElement.isDisplayed()) {
                                    if (replyContent.equals("STICKER")) {
                                        WebElement stickerButtonElement = commentContainerElement.findElement(By.className("UFICommentStickerIcon"));
                                        click(stickerButtonElement);
                                        delay();
                                        interactionHandler.commentBySticker();
                                    } else {
                                        WebElement commentInputBoxElement = commentContainerElement.findElement(By.xpath(".//div[@data-testid='ufi_comment_composer']"));
                                        interactionHandler.insertTextToCommentInputBox(commentInputBoxElement, replyContent, interactionDTO);
                                    }
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
            if (interactionDTO.getReaction() != -1 || interactionDTO.getReplyContent() != null) {
                ServerService.sendDTOToServer(SERVER_COMMENT, interactionDTO);
            }
        }

        return interactionDTO;
    }

    public String reviewFanpage(String fanpageLink, int point, String reviewContent) throws Exception {
        String result = "reviewFanpage";
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 60);
        driver.getDriver().navigate().to(fanpageLink);
        waitForPageLoaded();
        WebElement likeFanpagebuttonElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@data-testid='page_profile_like_button_test_id']")));
        scrollElementToMiddle(likeFanpagebuttonElement);
        click(likeFanpagebuttonElement);
        WebElement uiStarsRatableFlatStarsElement = driver.getDriver().findElement(By.className("uiStarsRatableFlatStars"));
        scrollElementToMiddle(likeFanpagebuttonElement);
        switch (point) {
            case 1:
                WebElement poor = uiStarsRatableFlatStarsElement.findElement(By.xpath("./a[@aria-label='Poor']"));
                click(poor);
                break;
            case 2:
                WebElement fair = uiStarsRatableFlatStarsElement.findElement(By.xpath("./a[@aria-label='Fair']"));
                click(fair);
                break;
            case 3:
                WebElement good = uiStarsRatableFlatStarsElement.findElement(By.xpath("./a[@aria-label='Good']"));
                click(good);
                break;
            case 4:
                WebElement veryGood = uiStarsRatableFlatStarsElement.findElement(By.xpath("./a[@aria-label='Very good']"));
                click(veryGood);
                break;
            case 5:
                WebElement excellent = uiStarsRatableFlatStarsElement.findElement(By.xpath("./a[@aria-label='Excellent']"));
                click(excellent);
                break;
        }
        delay();
        delay();
        WebElement textareaElement = driver.getDriver().findElement(By.xpath("//textarea[@name='note_message']"));
        click(textareaElement);
        textareaElement.sendKeys(reviewContent);
        WebElement doneButtonElement = driver.getDriver().findElement(By.xpath("//button[@name='done_button']"));
        click(doneButtonElement);
        waitForPageLoaded();
        delay();
        delay();
        delay();
        delay();
        delay();
        result = result + "-reviewed-" + fanpageLink;
        return result;
    }

    public String shareToWall(String linkOfPostToShare) throws Exception {
        driver.getDriver().navigate().to(linkOfPostToShare);
        waitForPageLoaded();
        WebElement share_action_linkElement = driver.getDriver().findElement(By.className("share_action_link "));
        click(share_action_linkElement);
        List<WebElement> menuItemElementList = driver.getDriver().findElements(By.xpath("//a[@role='menuitem']"));
        for (WebElement menuItemElement : menuItemElementList) {
            String menuItemText = menuItemElement.getText();
            if (menuItemText.equals("Share Now (Friends)")) {
                click(menuItemElement);
                delay();
                delay();
                return "postSharedToWall";
            }
        }
        return "na";
    }

    public String shareToGroup(String linkOfPostToShare, String groupName, String caption) throws Exception {
        driver.getDriver().navigate().to(linkOfPostToShare);
        waitForPageLoaded();
        WebElement share_action_linkElement = driver.getDriver().findElement(By.className("share_action_link "));
        click(share_action_linkElement);
        List<WebElement> menuItemElementList = driver.getDriver().findElements(By.xpath("//a[@role='menuitem']"));
        for (WebElement menuItemElement : menuItemElementList) {
            String menuItemText = menuItemElement.getText();
            if (menuItemText.equals("Share…")) {
                click(menuItemElement);
                delay();
                delay();
                WebElement shareChoiceElement = driver.getDriver().findElement(By.xpath("//span[@data-testid=['share_on_own']"));
                click(shareChoiceElement);
                delay();
                WebElement shareToGroupElement = driver.getDriver().findElement(By.xpath("//span[@data-testid=['share_to_group']"));
                click(shareToGroupElement);
                WebElement groupNameElement = driver.getDriver().findElement(By.xpath("//input[@placeholder=['Group Name']"));
                String groupNameSub = groupName.substring(0, Math.min(groupName.length(), 10));
                groupNameElement.sendKeys(groupNameSub);
                groupNameElement.sendKeys(Keys.ARROW_DOWN);
                delay();
                groupNameElement.sendKeys(Keys.ENTER);
                delay();
                WebElement shareDialogElement = driver.getDriver().findElement(By.xpath("//div[@data-testid='react_share_dialog_content']"));

                WebElement captionInputBoxElement = shareDialogElement.findElement(By.className("notranslate"));
                captionInputBoxElement.sendKeys(caption);
                WebElement shareDialogPostButtonElement = driver.getDriver().findElement(By.xpath("//div[@data-testid='react_share_dialog_post_button']"));
                click(shareDialogPostButtonElement);
                delay();
                return "postSharedToGroup" + groupName;
            }
        }
        return "na";
    }

    private List<LiveStreamDTO.LiveStreamLinkInfo> liveStreamChecker(LiveStreamDTO liveStreamDTO) throws Exception {
        List<LiveStreamDTO.LiveStreamLinkInfo> newliveStreamLinkInfoList = new ArrayList<>();

        List<String> liveStreamShouldWatchList = new ArrayList<>();
        List<String> currentUrlList = new ArrayList<>();
        for (LiveStreamDTO.LiveStreamLinkInfo liveStreamLinkInfo : liveStreamDTO.getLiveStreamLinkInfos()) {
            liveStreamShouldWatchList.add(liveStreamLinkInfo.getLiveStreamLink());
        }
        Set<String> chromeTabSet = driver.getDriver().getWindowHandles();
        for (String chromeTab : chromeTabSet) {
            driver.getDriver().switchTo().window(chromeTab);
            String currentUrl = driver.getDriver().getCurrentUrl();
            currentUrlList.add(currentUrl);
        }
        for (String liveStreamShouldWatch : liveStreamShouldWatchList) {
            if (currentUrlList.indexOf(liveStreamShouldWatch) == -1) {
                openUrlInNewTab(liveStreamShouldWatch);
            }
        }
        chromeTabSet = driver.getDriver().getWindowHandles();
        for (String chromeTab : chromeTabSet) {
            driver.getDriver().switchTo().window(chromeTab);
            waitForPageLoaded();
            String currentUrl = driver.getDriver().getCurrentUrl();
            if (liveStreamShouldWatchList.indexOf(currentUrl) == -1) {
                if (driver.getDriver().getWindowHandles().size() > 1) {
                    driver.getDriver().close();
                }
            } else {
                String jsScriptCode = "var vidElement;\n" +
                        "while (true) {\n" +
                        "\n" +
                        "vidElement = document.querySelector('video');\n" +
                        "if (vidElement != null) {\n" +
                        "if (vidElement.paused) {\n" +
                        "\tvidElement.play();\n" +
                        "}}\n" +
                        "break;\n" +
                        "}";
                ((JavascriptExecutor) driver.getDriver()).executeScript(jsScriptCode);
                waitForPageLoaded();
                List<WebElement> sau445 = driver.getDriver().findElements(By.xpath("//div[@class='_6445']"));
                List<WebElement> liveStatusList = new ArrayList<>();
                if (sau445.size() > 0) {
                    liveStatusList = sau445.get(0).findElements(By.xpath(".//span[text()=' was live.']"));
                }
                List<WebElement> contentNAList = driver.getDriver().findElements(By.xpath("//h2[contains(text(),'Sorry, this content') and contains(text(),'available right now')]"));
                if (liveStatusList.isEmpty() && contentNAList.isEmpty()) {
                    List<WebElement> sauBonBonBonList = driver.getDriver().findElements(By.xpath("//div[@class='_6444']"));
                    int liveViewInt = 0;
                    if (!sauBonBonBonList.isEmpty()) {
                        List<WebElement> redLiveElementList = sauBonBonBonList.get(0).findElements(By.xpath(".//span[text()='LIVE']"));
                        for (WebElement redLiveElement : redLiveElementList) {
                            if (redLiveElement.isDisplayed()) {
                                WebElement redLiveParentElement = redLiveElement.findElement(By.xpath("./parent::div"));
                                WebElement redLiveNextSiblingElement = redLiveParentElement.findElement(By.xpath("./following-sibling::div"));
                                String liveViewsCount = redLiveNextSiblingElement.getText();
                                liveViewsCount = liveViewsCount.replaceAll("[^0-9]", "");
                                if (isNullOrEmptyStr(liveViewsCount)) {
                                    liveViewsCount = "0";
                                }
                                liveViewInt = Integer.valueOf(liveViewsCount);
                            }
                        }
                    }
                    newliveStreamLinkInfoList.add(setLiveStreamLinkInfo(currentUrl, liveViewInt, WATCHING_LIVE_LINK));
                }
                if (!liveStatusList.isEmpty() || !contentNAList.isEmpty()) {
                    newliveStreamLinkInfoList.add(setLiveStreamLinkInfo(currentUrl, 0, LIVE_ENDED));
                    if (driver.getDriver().getWindowHandles().size() > 1) {
                        driver.getDriver().close();
                    }
                }

            }
        }
        return newliveStreamLinkInfoList;
    }

    private LiveStreamDTO.LiveStreamLinkInfo setLiveStreamLinkInfo(String liveStreamLink, int liveStreamViewerCount, int liveStreamStatusCode) throws Exception {
        LiveStreamDTO.LiveStreamLinkInfo liveStreamLinkInfo = new LiveStreamDTO.LiveStreamLinkInfo();
        liveStreamLinkInfo.setLiveStreamLink(liveStreamLink);
        liveStreamLinkInfo.setLiveStreamViewerCount(liveStreamViewerCount);
        liveStreamLinkInfo.setLiveStreamStatusCode(liveStreamStatusCode);
        return liveStreamLinkInfo;
    }
}
