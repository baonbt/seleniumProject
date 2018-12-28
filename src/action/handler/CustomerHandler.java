package action.handler;

import bot.base.Driver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

public class CustomerHandler extends BaseAction {
    public CustomerHandler(Driver driver) {
        super(driver);
    }

    public String follow(String customerId) throws Exception {
        String result = "followCustomer";
        String subcriberProfileLink = "https://www.facebook.com/" + customerId;
        driver.goToPage(subcriberProfileLink);
        WebElement pageletTimelineProfileActions = driver.getDriver().findElement(By.id("pagelet_timeline_profile_actions"));
        //chua co ban be
        List<WebElement> followButtonList = pageletTimelineProfileActions.findElements(By.xpath(".//a[@role='button' and contains(@ajaxify,'show_followee_on_follow')]"));
        //da co ban be
        List<WebElement> seeFirstElementList = pageletTimelineProfileActions.findElements(By.xpath(".//button[@data-status='see_first']"));
        List<WebElement> followElementList = pageletTimelineProfileActions.findElements(By.xpath(".//button[@data-status='follow']"));
        if (!seeFirstElementList.isEmpty()) {
            if (seeFirstElementList.get(0).isDisplayed()) {
                result = result + "-seeFirst";
                return result;
            }
        }
        if (!followElementList.isEmpty()) {
            if (followElementList.get(0).isDisplayed()) {
                try {
                    String followElementText = followElementList.get(0).getText();
                    if(!followElementText.equals("Following")){
                        click(followElementList.get(0));
                        WebElement facebookBlueBarLogo = driver.getDriver().findElement(By.xpath(".//*[@data-click='bluebar_logo']"));
                        move(facebookBlueBarLogo);
                    }
                    return result + "-following";
                } catch (Exception ignored) {
                }
            }
        }

        if (followButtonList.isEmpty()) {
            result = result + "-followButtonDisabled";
        } else {
            try {
                String followButtonText = followButtonList.get(0).getText();
                if(!followButtonText.equals("Following")){
                    click(followButtonList.get(0));
                    WebElement facebookBlueBarLogo = driver.getDriver().findElement(By.xpath(".//*[@data-click='bluebar_logo']"));
                    move(facebookBlueBarLogo);
                }
                return result + "-following";
            } catch (Exception ignored) {
                result = result + "-clickFollowFailed";
            }
        }
        return result;
    }

    public String followAndSeeFirst(String customerId) throws Exception {
        String result = "followAndSeeFirstCustomer";
        result = result + follow(customerId);
        if (result.contains("-seeFirst")) {
            return result;
        }
        WebElement pageletTimelineProfileActions = driver.getDriver().findElement(By.id("pagelet_timeline_profile_actions"));
        //chua co ban be
        List<WebElement> followButtonList = pageletTimelineProfileActions.findElements(By.xpath(".//a[@role='button' and contains(@ajaxify,'show_followee_on_follow')]"));
        //da co ban be
        List<WebElement> followElementList = pageletTimelineProfileActions.findElements(By.xpath(".//button[@data-status='follow']"));

        if (!followElementList.isEmpty()) {
            if (followElementList.get(0).isDisplayed()) {
                try {
                    move(followElementList.get(0));
                    WebElement uiContextualLayerBelowRight = driver.getDriver().findElement(By.className("uiContextualLayerBelowRight"));
                    WebElement seeFirstSelection = uiContextualLayerBelowRight.findElement(By.xpath(".//div[text()='See First']"));
                    seeFirstSelection.click();
                    delay();
                    click(seeFirstSelection);
                    if (uiContextualLayerBelowRight.getText().contains("more friends and Pages and") || pageletTimelineProfileActions.getText().contains("See First")) {
                        WebElement facebookBlueBarLogo = driver.getDriver().findElement(By.xpath(".//*[@data-click='bluebar_logo']"));
                        move(facebookBlueBarLogo);
                        return result + "-seeFirst";
                    }
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                    return result + "-followedButSeeFirstFailed";
                }
            }
        }
        if (followButtonList.isEmpty()) {
            result = result + "-followButtonDisabled";
        } else {
            try {
                click(followButtonList.get(0));
                move(followButtonList.get(0));
                WebElement uiContextualLayerBelowRight = driver.getDriver().findElement(By.className("uiContextualLayerBelowRight"));
                WebElement seeFirstSelection = uiContextualLayerBelowRight.findElement(By.xpath(".//div[text()='See First']"));
                seeFirstSelection.click();
                delay();
                click(seeFirstSelection);
                if (uiContextualLayerBelowRight.getText().contains("more friends and Pages and") || pageletTimelineProfileActions.getText().contains("See First")) {
                    result = result + "-seeFirst";
                    WebElement facebookBlueBarLogo = driver.getDriver().findElement(By.xpath(".//*[@data-click='bluebar_logo']"));
                    move(facebookBlueBarLogo);
                }
            } catch (Exception ignored) {
                result = result + "-clickFollowFailed";
            }
        }
        returnToNewsFeed();
        return result;
    }

    public String unSeeFirst(String customerId) throws Exception {
        String result = "unSeeFirstCustomer";
        String subcriberProfileLink = "https://www.facebook.com/" + customerId;
        driver.goToPage(subcriberProfileLink);
        WebElement pageletTimelineProfileActions = driver.getDriver().findElement(By.id("pagelet_timeline_profile_actions"));
        //da co ban be
        List<WebElement> seeFirstElementList = pageletTimelineProfileActions.findElements(By.xpath(".//button[@data-status='see_first']"));
        List<WebElement> followElementList = pageletTimelineProfileActions.findElements(By.xpath(".//button[@data-status='follow']"));
        if (!seeFirstElementList.isEmpty()) {
            if (seeFirstElementList.get(0).isDisplayed()) {
                move(seeFirstElementList.get(0));
                WebElement uiContextualLayerBelowRight = driver.getDriver().findElement(By.className("uiContextualLayerBelowRight"));
                WebElement defaultSelection = uiContextualLayerBelowRight.findElement(By.xpath(".//div[text()='Default']"));
                defaultSelection.click();
                delay();
                click(defaultSelection);
                return result + "-defaultFollow";
            }
        }
        if (!followElementList.isEmpty()) {
            if (followElementList.get(0).isDisplayed()) {
                return result + "-defaultFollow";
            }
        }
        return result + "-unFollowed";
    }

    public String unfollow(String profileLink) throws Exception {
        String result = "unFollowCustomer";
        driver.goToPage(profileLink);
        WebElement pageletTimelineProfileActions = driver.getDriver().findElement(By.id("pagelet_timeline_profile_actions"));
        //da co ban be
        List<WebElement> seeFirstElementList = pageletTimelineProfileActions.findElements(By.xpath(".//button[@data-status='see_first']"));
        List<WebElement> followElementList = pageletTimelineProfileActions.findElements(By.xpath(".//button[@data-status='follow']"));
        if (!seeFirstElementList.isEmpty()) {
            if (seeFirstElementList.get(0).isDisplayed()) {
                move(seeFirstElementList.get(0));
            }
        }
        if (!followElementList.isEmpty()) {
            if (followElementList.get(0).isDisplayed()) {
                move(followElementList.get(0));
            }
        }
        List<WebElement> uiContextualLayerBelowRightList = driver.getDriver().findElements(By.className("uiContextualLayerBelowRight"));
        if (!uiContextualLayerBelowRightList.isEmpty()) {
            WebElement unFollowSelection = uiContextualLayerBelowRightList.get(0).findElement(By.xpath(".//span[contains(text(), 'Unfollow')]"));
            move(unFollowSelection);
            delay();
            unFollowSelection.click();
        }
        return result + "-unFollowed";
    }
}
