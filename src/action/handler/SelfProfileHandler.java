package action.handler;

import bot.base.Driver;
import object.dto.StatusDTO;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import service.ServerService;

import java.util.List;

import static service.ChatbotService.randomEmotion;

public class SelfProfileHandler extends BaseAction {
    public SelfProfileHandler(Driver driver) {
        super(driver);
    }
    public String updateCoverPhoto(long accountId) throws Exception {
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 30);
        String result = "updateProfilePicture";
        if (postImage(accountId, "cover")) {
            result = result + "-imgPostedToWall";
            WebElement profileNavigationElement = driver.getDriver().findElement(By.xpath("//a[@title='Profile']"));
            click(profileNavigationElement);
            waitForPageLoaded();
            WebElement coverPhotoElement = driver.getDriver().findElement(By.className("fbCoverImageContainer"));
            move(coverPhotoElement);
            WebElement fbProfileCoverPhotoSelectorElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("fbProfileCoverPhotoSelector")));
            click(fbProfileCoverPhotoSelectorElement);
            WebElement selectPhotoElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(),'Select Photo')]")));
            click(selectPhotoElement);
            WebElement firstPhotoElement = driver.getDriver().findElement(By.className("vTop"));
            click(firstPhotoElement);
            delay();
            delay();
            delay();
            WebElement wait_1 = wait.until(ExpectedConditions.elementToBeClickable(By.className("saveButton")));
            List<WebElement> saveButtonElementList = driver.getDriver().findElements(By.className("saveButton"));
            for (WebElement saveButtonElement : saveButtonElementList) {
                if (saveButtonElement.getText().contains("Save Changes")) {
                    click(saveButtonElement);
                    delay();
                    delay();
                    result = result + "-COMPLETED";
                }
            }
            result = result + "-saveButtonNotFound";
        } else {
            result = result + "-failedToPostImg";
        }
        return result;
    }
    public String updateProfilePicture(long accountId) throws Exception {
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 30);
        String result = "updateProfilePicture";
        if (postImage(accountId, "avatar")) {
            result = result + "-imgPostedToWall";
            WebElement profileNavigationElement = driver.getDriver().findElement(By.xpath("//a[@title='Profile']"));
            click(profileNavigationElement);
            waitForPageLoaded();
            WebElement profilePictureElement = driver.getDriver().findElement(By.className("profilePic"));
            move(profilePictureElement);
            delay();
            WebElement fbTimelineProfilePicSelectorElement = driver.getDriver().findElement(By.className("fbTimelineProfilePicSelector"));
            click(fbTimelineProfilePicSelectorElement);
            delay();
            WebElement yourPhotosElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[contains(text(),'Your Photos')]/following-sibling::div//a[@rel='makeprofile']")));
            scrollElementToMiddle(yourPhotosElement);
            click(yourPhotosElement);
            WebElement postButtonElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//button[@data-testid='profilePicSaveButton']")));
            click(postButtonElement);
            result = result + "-COMPLETED";
        } else {
            result = result + "-failedToPostImg";
        }
        return result;
    }
    private boolean postImage(long accountId, String imgKey) {
        String content = randomEmotion();

        try {
            WebElement profileNavigationElement = driver.getDriver().findElement(By.xpath("//a[@title='Profile']"));
            click(profileNavigationElement);
            waitForPageLoaded();
            WebElement writePostElement = driver.getDriver().findElement(By.className("fbReactComposerAttachmentSelector_STATUS"));
            click(writePostElement);
            delay();
            WebElement inputTextBoxElement = driver.getDriver().findElement(By.xpath("//div[@data-testid='status-attachment-mentions-input']"));
            click(inputTextBoxElement);
            delay();
            inputTextBoxElement.sendKeys(content);
            delay();
            int timeOut = 0;
            while (!isClipboardReady()) {
                Thread.sleep(100);
                timeOut = timeOut + 1;
                if (timeOut > 1000) {
                    break;
                }
            }
            ServerService service = new ServerService();
            StatusDTO statusDTO = service.getStatusDTOFromServer(accountId, imgKey);
            copyImageToClipboard(statusDTO.getBytes());
            inputTextBoxElement.sendKeys(Keys.CONTROL + "v");
            delay();
            setClipboardReady();
            WebElement postButtonElement = driver.getDriver().findElement(By.xpath("//button[@data-testid='react-composer-post-button']"));
            click(postButtonElement);
            delay();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public String updateStatus(long accountId) throws Exception {
        String result = "updateStatus";
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 30);
        WebElement profileNavigationElement = driver.getDriver().findElement(By.xpath("//a[@title='Profile']"));
        click(profileNavigationElement);

        waitForPageLoaded();
        WebElement writePostElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.className("fbReactComposerAttachmentSelector_STATUS")));
        click(writePostElement);
        delay();
        WebElement inputTextBoxElement = driver.getDriver().findElement(By.xpath("//div[@data-testid='status-attachment-mentions-input']"));
        click(inputTextBoxElement);
        delay();

        ServerService service = new ServerService();
        StatusDTO statusDTO = service.getStatusDTOFromServer(accountId, "status");
        String caption = randomEmotion();
        if (statusDTO.getCaption() != null) {
            if (statusDTO.getCaption().length() > 5) {
                caption = statusDTO.getCaption();
            }
        }
        inputTextBoxElement.sendKeys(caption);
        delay();
        if (statusDTO.getBytes().length > 10) {
            result = result + "-imgStatus";
            int timeOut = 0;
            while (!isClipboardReady() && timeOut < 1000) {
                Thread.sleep(100);
                timeOut = timeOut + 1;
            }
            //nạp ảnh vào clipboard
            copyImageToClipboard(statusDTO.getBytes());
            inputTextBoxElement.sendKeys(Keys.CONTROL + "v");
            delay();
            setClipboardReady();
        } else {
            result = result + "-textStatus";
        }
        delay();
        WebElement postButtonElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@data-testid='react-composer-post-button']")));
        click(postButtonElement);
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@data-testid='react-composer-post-button']")));
        delay();
        delay();
        return result;
    }
}
