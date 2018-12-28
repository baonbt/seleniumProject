package action.handler;

import bot.base.Driver;
import object.dto.StatusDTO;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import java.io.IOException;
import java.util.List;

public class PostHandler extends BaseAction {
    public PostHandler(Driver driver) {
        super(driver);
    }

    public void statusPoster(StatusDTO statusDTO) throws InterruptedException, IOException {
        _returnToNewsFeed();
        List<WebElement> navigationFocusElements = driver.getDriver().findElements(By.className("navigationFocus"));
        for (WebElement navigationFocusElement: navigationFocusElements){
            List<WebElement> dataTestIdElements = navigationFocusElement.findElements(By.xpath(".//input[@data-testid='search_input']"));
            if(!dataTestIdElements.isEmpty()){
                continue;
            } else {
                List<WebElement> statusInputBoxElements = driver.getDriver().findElements(By.className("notranslate"));
                if(!statusInputBoxElements.isEmpty()){
                    statusInputBoxElements.get(0).click();
                    Thread.sleep(5000);

                    if(statusDTO.getUrl()!=null){
                        for(int i = 0; i<600; i++){
                            if(isClipboardReady()){
                                copyStringtoClipBoard(statusDTO.getUrl());
                                //TODO CHECK CTRL + V ac.keyDown(Keys.CONTROL).keyDown("v").keyUp(Keys.CONTROL).keyUp("v").build().perform();
                                statusInputBoxElements.get(0).sendKeys(Keys.CONTROL+"v");
                                Thread.sleep(10000);
                                setClipboardReady();
                                statusInputBoxElements.get(0).sendKeys(Keys.CONTROL+"a");
                                Thread.sleep(1000);
                                statusInputBoxElements.get(0).sendKeys(Keys.BACK_SPACE);
                                Thread.sleep(1000);
                                break;
                            }
                            Thread.sleep(500);
                        }
                    }

                    if(statusDTO.getCaption() != null){
                        statusInputBoxElements.get(0).sendKeys(statusDTO.getCaption());
                    }

                    if(statusDTO.getBytes()!=null){
                        for(int i = 0; i<600; i++){
                            if(isClipboardReady()){
                                copyImageToClipboard(statusDTO.getBytes());
                                //TODO CHECK CTRL + V ac.keyDown(Keys.CONTROL).keyDown("v").keyUp(Keys.CONTROL).keyUp("v").build().perform();
                                statusInputBoxElements.get(0).sendKeys(Keys.CONTROL+"v");
                                Thread.sleep(10000);
                                setClipboardReady();
                                break;
                            }
                            Thread.sleep(500);
                        }
                    }

                    List<WebElement> checkBoxElements = driver.getDriver().findElements(By.xpath(".//div[@role='checkbox' and @aria-checked='false']"));
                    for(WebElement checkBox: checkBoxElements){
                        List<WebElement> storiesCheckBoxElements = checkBox.findElements(By.xpath(".//li[@data-destination='STORIES']"));
                        if(!storiesCheckBoxElements.isEmpty()){
                            storiesCheckBoxElements.get(0).click();
                            Thread.sleep(1000);
                        }
                    }

                    List<WebElement> submitButtonElements = driver.getDriver().findElements(By.xpath(".//button[@data-testid='react-composer-post-button']"));
                    for(WebElement submitButtonElement: submitButtonElements){
                        if(submitButtonElement.isDisplayed() && submitButtonElement.isEnabled()){
                            submitButtonElement.click();
                            Thread.sleep(3000);
                        }
                    }
                }
            }
        }
    }

    public void groupPoster(){

    }
}
