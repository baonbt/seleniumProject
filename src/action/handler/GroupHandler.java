package action.handler;

import bot.base.Driver;
import object.dto.StatusDTO;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import service.ServerService;

import java.util.List;

import static service.ChatbotService.randomEmotion;
import static util.common.Constant.ACTION.MAXIMUM_SELL_GROUP_POST;
import static util.common.DataUtil.isNullOrEmptyStr;
import static util.common.DataUtil.random;

public class GroupHandler extends BaseAction {
    public GroupHandler(Driver driver) {
        super(driver);
    }
    public void createGroup(String groupName){
//TODO CREATE GROUP
    }
    public String joinGroup(String groupId, long accountId) throws Exception {
        String result = "joinGroup";
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 20);
        List<WebElement> follow_unfollow_groups = driver.getDriver().findElements(By.id("follow_unfollow_group"));
        for (WebElement follow_unfollow_group : follow_unfollow_groups) {
            if (follow_unfollow_group.getText().contains("Joined")) {
                result += "-joined-" + groupId;
                return result;
            }
        }
        List<WebElement> joinGroupButtons = driver.getDriver().findElements(By.className("selected"));
        for (WebElement joinGroupButton : joinGroupButtons) {
            String joinGroupButtonText = joinGroupButton.getText();
            if (joinGroupButtonText.contains("Join Group")) {
                click(joinGroupButton);
                waitForPageLoaded();
                List<WebElement> questionsElementList = driver.getDriver().findElements(By.className("pam"));
                List<WebElement> answerBoxsElementList = driver.getDriver().findElements(By.className("uiTextareaAutogrow"));
                for (int i = 0; i < questionsElementList.size() + 1; i++) {
                    String question = questionsElementList.get(i).getText();
                    String answerFromChatBot = chatbot.reply(question, accountId);
                    if (!isNullOrEmptyStr(answerFromChatBot)) {
                        answerFromChatBot = "I don't know";
                    }
                    answerBoxsElementList.get(i).sendKeys(answerFromChatBot);
                    delay();
                }
                WebElement confirmButton = driver.getDriver().findElement(By.className("layerConfirm"));
                click(confirmButton);
                delay();
                wait.until(ExpectedConditions.presenceOfElementLocated(By.className("layerCancel")));
                WebElement layerCancel = driver.getDriver().findElement(By.className("layerCancel"));
                click(layerCancel);
                delay();
                result += "-requested-" + groupId;
                return result;
            }
        }
        return result;
    }

    public String postImage(String groupId, long accountId) throws Exception {
        String result = "postImage";
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 100);
        List<WebElement> writePostElementList = driver.getDriver().findElements(By.className("fbReactComposerAttachmentSelector_STATUS"));
        if (!writePostElementList.isEmpty()) {
            result = result + "-allowedToPostStatus";
            click(writePostElementList.get(0));
            delay();
            WebElement inputTextBoxElement = driver.getDriver().findElement(By.xpath("//div[@data-testid='status-attachment-mentions-input']"));

            ServerService service = new ServerService();
            StatusDTO statusDTO = service.getStatusDTOFromServer(accountId, "status");
            String caption = randomEmotion();
            if (statusDTO.getCaption().length() > 5) {
                caption = statusDTO.getCaption();
            }
            inputTextBoxElement.sendKeys(caption);
            delay();
            if (statusDTO.getBytes().length > 10) {
                result = result + "-imgStatus";
                int timeOut = 0;
                while (!isClipboardReady()) {
                    Thread.sleep(100);
                    timeOut = timeOut + 1;
                    if (timeOut > 1000) {
                        break;
                    }
                }
                //nạp ảnh vào clipboard
                copyImageToClipboard(statusDTO.getBytes());
                inputTextBoxElement.sendKeys(Keys.CONTROL + "v");
                delay();
                setClipboardReady();
            } else {
                result = result + "-textStatus";
            }

            ////
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@data-testid='media-attachment-photo']")));
            } catch (Exception e) {
                result = result + "-uploadImgFailed";
            }
            WebElement postButtonElement = driver.getDriver().findElement(By.xpath("//button[@data-testid='react-composer-post-button']"));
            click(postButtonElement);
            delay();
            try {
                WebElement adminApprovalElement = driver.getDriver().findElement(By.className("uiBoxYellow"));
                if (adminApprovalElement.getText().contains("by an admin")) {
                    result = result + "-requireAdminProve";
                }
            } catch (Exception e) {
            }
            WebElement timestampContentElement = driver.getDriver().findElement(By.className("timestampContent"));
            if (timestampContentElement.getText().contains("Just now")) {
                click(timestampContentElement);
                waitForPageLoaded();
                result = result + "-postedImgTo-" + groupId + "-" + driver.getDriver().getCurrentUrl();
            }
        } else {
            result = result + "-NOTAllowedToPostStatus";
        }
        return result;
    }

    public String postLink(String groupId, String content, String linkToPost) throws Exception {
        String result = "postLink";
        WebElement postBoxElement = driver.getDriver().findElement(By.xpath("//div[@data-testid='react-composer-root']"));
        List<WebElement> statusPostElementList = postBoxElement.findElements(By.className("fbReactComposerAttachmentSelector_STATUS"));
        List<WebElement> sellPostElementList = postBoxElement.findElements(By.className("fbReactComposerAttachmentSelector_SELL"));

        if (!sellPostElementList.isEmpty()) {
            click(sellPostElementList.get(0));
            waitForPageLoaded();
            WebElement whatAreYouSellingElement = postBoxElement.findElement(By.xpath(".//input[@placeholder='What are you selling?']"));
            click(whatAreYouSellingElement);
            delay();
            WebElement inputElement = postBoxElement.findElement(By.xpath(".//input[@placeholder='What are you selling?']"));
            String shortenContent = StringUtils.abbreviate(content, 90);
            inputElement.sendKeys(shortenContent);
            delay();
            inputElement = postBoxElement.findElement(By.xpath(".//input[@placeholder='Price']"));
            int pirce = random(0, 9999);
            inputElement.sendKeys(Integer.toString(pirce));
            delay();

            inputElement = postBoxElement.findElement(By.xpath("//div[@data-testid='status-attachment-mentions-input']"));
            inputElement.sendKeys(linkToPost);
            //click next
            WebElement postButtonElement = driver.getDriver().findElement(By.xpath("//button[@data-testid='react-composer-post-button']"));
            click(postButtonElement);
            delay();
            delay();
            delay();
            //post to more group
            List<WebElement> moreGroupElementList = driver.getDriver().findElements(By.xpath("//div[@aria-checked='false'][@aria-disabled='false'][@role='checkbox']"));
            int postTime = 1;
            for (int i = 0; i < MAXIMUM_SELL_GROUP_POST; i++) {
                scrollElementToMiddle(moreGroupElementList.get(i));
                click(moreGroupElementList.get(i));
                delay();
                postTime = i + 1;
            }
            //click post
            postButtonElement = driver.getDriver().findElement(By.xpath("//button[@data-testid='react-composer-post-button']"));
            click(postButtonElement);
            delay();
            delay();
            delay();
            result = result + "-postedToGroup-" + groupId;
            try {
                WebElement adminApprovalElement = driver.getDriver().findElement(By.className("uiBoxYellow"));
                if (adminApprovalElement.getText().contains("by an admin")) {
                    result = result + "-requireAdminProve";
                }
            } catch (Exception e) {
            }
            WebElement timestampContentElement = driver.getDriver().findElement(By.className("timestampContent"));
            if (timestampContentElement.getText().contains("Just now")) {
                click(timestampContentElement);
                waitForPageLoaded();
                result = result + postTime + "-sellGroup-" + driver.getDriver().getCurrentUrl();
            }
        } else if (!statusPostElementList.isEmpty()) {
            click(statusPostElementList.get(0));
            delay();
            WebElement inputTextBoxElement = driver.getDriver().findElement(By.xpath("//div[@data-testid='status-attachment-mentions-input']"));
            inputTextBoxElement.sendKeys(content);
            inputTextBoxElement.sendKeys(Keys.RETURN);
            delay();
            inputTextBoxElement.sendKeys(linkToPost);
            delay();
            delay();
            delay();
            delay();
            delay();
            delay();
            WebElement postButtonElement = driver.getDriver().findElement(By.xpath("//button[@data-testid='react-composer-post-button']"));
            click(postButtonElement);
            delay();
            delay();
            delay();
            result = result + "-postedToGroup-" + groupId;
            try {
                WebElement adminApprovalElement = driver.getDriver().findElement(By.className("uiBoxYellow"));
                if (adminApprovalElement.getText().contains("by an admin")) {
                    result = result + "-requireAdminProve";
                }
            } catch (Exception e) {
            }
            WebElement timestampContentElement = driver.getDriver().findElement(By.className("timestampContent"));
            if (timestampContentElement.getText().contains("Just now")) {
                click(timestampContentElement);
                waitForPageLoaded();
                result = result + "-normalGroup-" + driver.getDriver().getCurrentUrl();
            }
        } else {
            result = result + "-groupNotAllowToPost";
        }
        return result;
    }

    public String inviteSuggestFriendToGroup(String groupId, Integer maxInvitation) throws Exception {
        String result = "inviteSuggestFriendToGroup";
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 60);
        String groupLink = "https://www.facebook.com/groups/" + groupId + "/?ref=group_browse_new";
        driver.goToPage(groupLink);
        waitForPageLoaded();
        result = result + "-navigatedToGroup";
        try {
            WebElement seeMoreButtonElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.linkText("See More")));
            scrollElementToMiddle(seeMoreButtonElement);
            click(seeMoreButtonElement);
        } catch (Exception e) {
        }
        List<WebElement> suggestFriendElementList = driver.getDriver().findElements(By.xpath("//li[contains(@id,'suggested_member')]"));
        int invitations = 0;
        for (int i = 0; i < maxInvitation; i++) {
            if (i == suggestFriendElementList.size()) {
                break;
            }
            WebElement addButtonElement = suggestFriendElementList.get(i).findElement(By.xpath("./button[@type='submit']"));
            scrollElementToMiddle(addButtonElement);
            click(addButtonElement);
            invitations = i;
        }
        result = result + "-invited-" + invitations;
        return result;
    }

    public String inviteTargetFriendToGroup(String groupId, String targetName) throws Exception {
        String result = "inviteTargetFriendToGroup";
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 60);
        String groupLink = "https://www.facebook.com/groups/" + groupId + "/?ref=group_browse_new";
        driver.goToPage(groupLink);
        waitForPageLoaded();
        result = result + "-navigatedToGroup";
        WebElement addMemberInputBoxElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@data-testid='GROUP_ADD_MEMBER_TYPEAHEAD_INPUT']")));
        scrollElementToMiddle(addMemberInputBoxElement);
        click(addMemberInputBoxElement);
        addMemberInputBoxElement = driver.getDriver().findElement(By.xpath("//input[@data-testid='GROUP_ADD_MEMBER_TYPEAHEAD_INPUT']"));
        addMemberInputBoxElement.sendKeys(targetName);
        addMemberInputBoxElement.sendKeys(Keys.ENTER);

        result = result + "-invited-" + targetName;
        return result;
    }
}
