package action.handler;

import bot.base.Driver;
import com.beust.jcommander.internal.Lists;
import object.dto.*;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import service.ServerService;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import static service.ChatbotService.randomEmotion;
import static util.common.Constant.ACTION.MAXIMUM_CRAWL_POST;
import static util.common.Constant.ACTION.NOTHING;
import static util.common.Constant.SERVER.SERVER_BACKUP_IMAGE;
import static util.common.Constant.SERVER.SERVER_CRAWL_FRIEND_RAW;
import static util.common.Constant.SERVER.SERVER_POST_STATUS;
import static util.common.DataUtil.isNullOrEmptyStr;
import static util.common.DataUtil.random;

public class DataHandler extends BaseAction {
    public DataHandler(Driver driver) {
        super(driver);
    }

    public void newsFeedCrawler(int minLikes, int minComments){
        //TODO
    }

    private static String getUidFromProfileLink(String profileLink) throws URISyntaxException {
        URI uri = new URI(profileLink);

        if (!profileLink.contains("profile.php")) {
            String[] segments = uri.getPath().split("/");

            int i = 0;
            for (String segment : segments) {
                if (segment.contains("facebook.com")) {
                    i++;
                    break;
                }
            }

            return segments[i + 1];
        } else {
            return "";
        }
    }

    public String crawlSelfAccountInfo(AccountInfoDTO accountCrawledInfo, FriendRawDTO friendRawDTO, long accoungId) throws Exception {
        List<FriendRawDTO.FriendInfo> friendsList = new ArrayList<>();
        friendRawDTO.setFriends(friendsList);
        String result = "crawlSelfAccountInfo";
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 60);
        WebElement profile = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@data-click='profile_icon']")));
        scrollElementToMiddle(profile);
        click(profile);
        click(profile);
        waitForPageLoaded();
        List<WebElement> customInfoElementList = driver.getDriver().findElements(By.xpath("//li[@data-store='{\"event\":\"context_item_edit_click\"}']"));
        for (WebElement customInfoElement : customInfoElementList) {
            String customInfoText = customInfoElement.getText();
            if (customInfoText.contains("Lives in")) {
                accountCrawledInfo.setLiveIn(customInfoText);
            }
            if (customInfoText.contains("Joined")) {
                accountCrawledInfo.setJoined(customInfoText);
            }
            if (customInfoText.contains("Followed by")) {
                accountCrawledInfo.setFollows(customInfoText.replace("Followed by ", "").replace(" people", ""));
            }
            result = result + "crawledCustomInfo";
        }

        accountCrawledInfo.setAvatarUrl(driver.getDriver().findElement(By.className("profilePicThumb")).findElement(By.className("img")).getAttribute("src"));

        String profileLink = driver.getDriver().getCurrentUrl();
        accountCrawledInfo.setProfileLink(profileLink);
        WebElement about = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@data-tab-key='about']")));
        click(about);
        waitForPageLoaded();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='_4bl9 _2pis _2dbl']")));
        List<WebElement> birthdayElementList = driver.getDriver().findElements(By.xpath("//div[@class='_4bl9 _2pis _2dbl']"));
        for (WebElement birthdayElement : birthdayElementList) {
            if (birthdayElement.getText().contains("Birthday")) {
                accountCrawledInfo.setBirthday(birthdayElement.getText().replace("Birthday\n", ""));
                result = result + "birthdayCrawled";
            }
        }

        WebElement friends = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@data-tab-key='friends']")));
        String friendsString = friends.getText();
        scrollElementToMiddle(friends);
        click(friends);
        try {
            click(friends);
        }catch (Exception ignore){

        }
        accountCrawledInfo.setFriends(Integer.valueOf(friendsString.replaceAll("[^0-9]", "")));

        String moreAboutYouXpath = "//*[contains(text(),'More About You')]";
        List<WebElement> moreAboutYouElementList = driver.getDriver().findElements(By.xpath(moreAboutYouXpath));
        int friendLoopCount = 0;
        while (moreAboutYouElementList.isEmpty() && friendLoopCount < 10) {
            friendLoopCount = friendLoopCount + 1;
            scroll(random(500, 800));
            moreAboutYouElementList = driver.getDriver().findElements(By.xpath(moreAboutYouXpath));
        }


        List<WebElement> profileBlockElementList = driver.getDriver().findElements(By.className("uiProfileBlockContent"));
        for (WebElement profileBlockElement : profileBlockElementList) {
            FriendRawDTO.FriendInfo friendInfo = new FriendRawDTO.FriendInfo();
            friendsList.add(friendInfo);

            List<WebElement> profileLinkElementList = profileBlockElement.findElements(By.xpath(".//a[contains(@href,'hc_location=friends_tab')]"));
            if(!profileLinkElementList.isEmpty()){
                String friendName = profileLinkElementList.get(0).getText();
                String friendProfileLink = profileLinkElementList.get(0).getAttribute("href");

                friendInfo.setKey("" + accoungId);
                friendInfo.setType("friend");
                friendInfo.setName(friendName);
                friendInfo.setUid(getUidFromProfileLink(friendProfileLink));
            }
        }

        delay();
        driver.getDriver().navigate().to("https://www.facebook.com/settings");
        waitForPageLoaded();
        waitForPageLoaded();
        List<WebElement> generalSettingList = driver.getDriver().findElements(By.className("fbSettingsListItemLabeled"));

        for (WebElement generalSetting : generalSettingList) {
            String generalSettingText = generalSetting.getText();
            if (generalSettingText.contains("Name")) {
                accountCrawledInfo.setName(generalSettingText.replace("Name\nEdit\n", ""));
                result = result + "nameCrawled";
            }
            if (generalSettingText.contains("Contact") && generalSettingText.contains("Primary:")) {
                accountCrawledInfo.setEmail(generalSettingText.replace("Contact\nEdit\nPrimary: ", ""));
                result = result + "emailCrawled";
            }
        }
        return result;
    }

    public String crawlStatusFromProfileLink(Long accountIdToCrawl, String profileLinkToCrawl) throws Exception {
        ServerService service = new ServerService();
        String result = "crawlStatus";
        if (!profileLinkToCrawl.contains("https://www.facebook.com/")) {
            profileLinkToCrawl = "https://www.facebook.com/" + profileLinkToCrawl;
        }
        driver.getDriver().navigate().to(profileLinkToCrawl);
        waitForPageLoaded();
        int randomScroll = random(5, 15);
        for (int i = 0; i < randomScroll; i++) {
            scroll(500);
            waitForPageLoaded();
        }
        List<WebElement> userContentWrapperElementList = driver.getDriver().findElements(By.className("userContentWrapper"));
        List<WebElement> _userContentWrapperElementList = Lists.newArrayList(userContentWrapperElementList);
        userContentWrapperElementList.forEach((userContentWrapperElement) -> {
            String userContentWrapperElementText = userContentWrapperElement.getText();
            if (userContentWrapperElementText.contains("shared") || userContentWrapperElementText.contains("was live") || userContentWrapperElementText.contains("tagged")) {
                _userContentWrapperElementList.remove(userContentWrapperElement);
            }
        });
        int successTimes = 0;
        for (int i = 0; i < MAXIMUM_CRAWL_POST + 1; i++) {
            StatusDTO dto = new StatusDTO();
            dto.setAccountId(driver.getSigma().getAccount().getAccountId());

            if (i == _userContentWrapperElementList.size() - 1) {
                break;
            }

            String postId = _userContentWrapperElementList.get(i).findElement(By.xpath(".//div[contains(@id,'feed_subtitle_')]")).getAttribute("id");
            dto.setPostId(postId);

//            scrollElementToMiddle(_userContentWrapperElementList.get(i));
            String caption = _userContentWrapperElementList.get(i).findElement(By.className("userContent")).getText();

            dto.setCaption(caption);
            dto.setAccountId(accountIdToCrawl);
            dto.setFbProfileLink(profileLinkToCrawl);

            List<WebElement> avatarImageElementList = _userContentWrapperElementList.get(i).findElements(By.xpath(".//img[@style='width:364px;height:364px']"));
            List<WebElement> scaledImageFitHeightElementList = _userContentWrapperElementList.get(i).findElements(By.className("scaledImageFitHeight"));
            List<WebElement> scaledImageFitWidthElementList = _userContentWrapperElementList.get(i).findElements(By.className("scaledImageFitWidth"));
            List<WebElement> doubleImageElementList = _userContentWrapperElementList.get(i).findElements(By.xpath(".//img[@width='370' and @height='493']"));
            if (avatarImageElementList.size() > 0) {
                String imageURL = avatarImageElementList.get(0).getAttribute("src");
                dto.setBase64(service.getBase64FromUrl(dto, imageURL));
                dto.setType("avatar");
                result = "crawledImageStatus";
            } else if (scaledImageFitHeightElementList.size() > 0) {
                String imageURL = scaledImageFitHeightElementList.get(0).getAttribute("src");
                getImageBase(dto, scaledImageFitHeightElementList, service, imageURL);
                result = "crawledImageStatus";
            } else if (scaledImageFitWidthElementList.size() > 0) {
                String imageURL = scaledImageFitWidthElementList.get(0).getAttribute("src");
                getImageBase(dto, scaledImageFitWidthElementList, service, imageURL);
                result = "crawledImageStatus";
            } else if (doubleImageElementList.size() > 0) {
                String imageURL = doubleImageElementList.get(0).getAttribute("src");
                getImageBase(dto, doubleImageElementList, service, imageURL);
                result = "crawledImageStatus";
            } else {
                result = "crawledTextStatus";
            }
            if (isNullOrEmptyStr(dto.getType())) {
                dto.setType("status-textOnly");
            }
            successTimes = i;
            ServerService.sendDTOToServer(SERVER_POST_STATUS, dto);
        }
        //crawl cover
        List<WebElement> coverImgElementList = driver.getDriver().findElements(By.className("coverPhotoImg"));
        if (coverImgElementList.size() > 0) {
            StatusDTO dto = new StatusDTO();
            String imageURL = coverImgElementList.get(0).getAttribute("src");
            dto.setBase64(service.getBase64FromUrl(dto, imageURL));
            String imageType = "cover-" + coverImgElementList.get(0).getAttribute("alt");
            dto.setType(imageType);
            result = "crawledCoverImg";
            ServerService.sendDTOToServer(SERVER_POST_STATUS, dto);
        }
        result = result + "-postCrawled-" + successTimes;
        return result;
    }

    private void getImageBase(StatusDTO dto, List<WebElement> webElementList, ServerService service, String imageURL) {
        dto.setBase64(service.getBase64FromUrl(dto, imageURL));
        String imageType = "status-" + webElementList.get(0).getAttribute("alt");
        dto.setType(imageType);
    }

    public String crawlFromFriend(PageRawDTO pageRawDTO, GroupRawDTO groupRawDTO, FriendRawDTO friendRawDTO, String friendUid) throws Exception {

        List<PageRawDTO.PageRawInfo> pagesList = new ArrayList<>();
        pageRawDTO.setPages(pagesList);
        List<GroupRawDTO.GroupInfo> groupsList = new ArrayList<>();
        groupRawDTO.setGroups(groupsList);
        List<FriendRawDTO.FriendInfo> friendsList = new ArrayList<>();
        friendRawDTO.setFriends(friendsList);

        String result = "friendListAndGroups";
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 30);
        String profileLink = "https://www.facebook.com/" + friendUid;
        driver.goToPage(profileLink);
        WebElement friendsTabElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@data-tab-key='friends']")));
        click(friendsTabElement);
        WebElement friendsSectionElement = wait.until(ExpectedConditions.elementToBeClickable(By.id("pagelet_timeline_medley_friends")));
        delay();
        List<WebElement> allFriendsTabElementList = friendsSectionElement.findElements(By.xpath(".//a[@name='All Friends']"));
        if (allFriendsTabElementList.size() > 0) {
            result = result + "-friendsListIsPublic";
            for (int i = 0; i < 50; i++) {

                List<WebElement> photosSectionElementList = driver.getDriver().findElements(By.id("pagelet_timeline_medley_photos"));
                if (photosSectionElementList.size() > 0) {
                    break;
                }
                scroll(random(500, 600));
            }

            List<WebElement> profileBlockElementList = driver.getDriver().findElements(By.className("uiProfileBlockContent"));
            for (WebElement profileBlockElement : profileBlockElementList) {
                FriendRawDTO.FriendInfo friendInfo = new FriendRawDTO.FriendInfo();
                friendsList.add(friendInfo);

                WebElement profileLinkElement = profileBlockElement.findElement(By.xpath(".//a[contains(@href,'hc_location=friends_tab')]"));
                String friendName = profileLinkElement.getText();
                String friendProfileLink = profileLinkElement.getAttribute("href");

                friendInfo.setKey(friendUid);
                friendInfo.setType("friend");
                friendInfo.setName(friendName);
                friendInfo.setUid(getUidFromProfileLink(friendProfileLink));
            }
        } else {
            result = result + "-friendsListIsPrivate";
        }


        String followingTabLink = profileLink + "/following";
        driver.goToPage(followingTabLink);
        scroll(random(500, 600));
        List<WebElement> followingTabElementList = driver.getDriver().findElements(By.id("pagelet_timeline_medley_friends"));
        if (!followingTabElementList.isEmpty()) {
            result = result + "-followingListIsPublic";
            for (int i = 0; i < 20; i++) {
                scroll(random(500, 600));
            }

            WebElement followingTabElement = driver.getDriver().findElement(By.id("pagelet_timeline_medley_friends"));
            delay();
            List<WebElement> followingElementList = followingTabElement.findElements(By.className("followListItem"));
            followingElementList.forEach((followingElement) -> {
                FriendRawDTO.FriendInfo friendInfo = new FriendRawDTO.FriendInfo();
                friendsList.add(friendInfo);

                WebElement profileLinkElement = followingElement.findElement(By.xpath(".//a[contains(@href,'fref=st')]"));
                List<WebElement> alterNameElement = followingElement.findElements(By.className("alternate_name"));

                String friendName = profileLinkElement.getText();
                String friendProfileLink = profileLinkElement.getAttribute("href");
                if (!alterNameElement.isEmpty()) {
                    String alterName = alterNameElement.get(0).getText();
                    friendName = friendName.replace(alterName, "");
                }
                friendInfo.setKey(friendUid);
                friendInfo.setType("following");
                friendInfo.setName(friendName);
                friendInfo.setUid(friendProfileLink.replace("https://www.facebook.com/", "").replace("?fref=st", ""));
            });
        } else {
            result = result + "-followingListIsPrivate";
        }

        String groupsTabLink = profileLink + "/groups";
        driver.goToPage(groupsTabLink);
        scroll(random(500, 600));
        List<WebElement> groupsTabElementList = driver.getDriver().findElements(By.id("pagelet_timeline_medley_groups"));
        if (!groupsTabElementList.isEmpty()) {
            result = result + "-groupsListIsPublic";
            for (int i = 0; i < 20; i++) {
                scroll(random(500, 600));
            }

            WebElement groupsTabElement = driver.getDriver().findElement(By.id("pagelet_timeline_medley_groups"));
            List<WebElement> groupsElementList = groupsTabElement.findElements(By.xpath(".//div[contains(@data-collection-item, '1000')]"));
            groupsElementList.forEach((groupsElement) -> {
                GroupRawDTO.GroupInfo groupInfo = new GroupRawDTO.GroupInfo();
                groupsList.add(groupInfo);

                WebElement groupNameElement = groupsElement.findElement(By.xpath(".//div[@class='mbs fwb']"));
                WebElement groupMembersElement = groupsElement.findElement(By.xpath(".//div[@class='mbs fcg']"));
                WebElement groupIdElement = groupNameElement.findElement(By.xpath(".//a[contains(@href, '/groups/')]"));

                String groupName = groupNameElement.getText();
                String groupMembers = groupMembersElement.getText();
                String groupLink = groupIdElement.getAttribute("href");

                groupInfo.setKey(friendUid);
                groupInfo.setType("friend");
                groupInfo.setName(groupName);
                groupInfo.setMember(Integer.valueOf(groupMembers.replaceAll("[^0-9]", "")));
                groupInfo.setGid(groupLink.replace("https://www.facebook.com/groups/", "").replace("/", ""));
            });
        } else {
            result = result + "-groupsListIsPrivate";
        }
        String likesTabLink = profileLink + "/likes";
        driver.goToPage(likesTabLink);
        scroll(random(500, 600));
        List<WebElement> pagesElementListList = driver.getDriver().findElements(By.id("pagelet_timeline_medley_likes"));
        if (!pagesElementListList.isEmpty()) {
            result = result + "-pagesListIsPublic";
            for (int i = 0; i < 10; i++) {
                scroll(random(500, 600));
            }
            WebElement pagesTabElement = driver.getDriver().findElement(By.id("pagelet_timeline_medley_likes"));
            List<WebElement> pagesElementList = pagesTabElement.findElements(By.xpath(".//a[contains(@href, 'fref=pb&hc_location=profile_browser')]"));
            for (WebElement pagesElement : pagesElementList) {
                PageRawDTO.PageRawInfo pageRawInfo = new PageRawDTO.PageRawInfo();
                pagesList.add(pageRawInfo);

                WebElement imgTagElement = pagesElement.findElement(By.xpath(".//img[1]"));

                String pageName = imgTagElement.getAttribute("aria-label");
                String pageLink = pagesElement.getAttribute("href");

                pageRawInfo.setKey(friendUid);
                pageRawInfo.setType("friend");
                pageRawInfo.setName(pageName);
                pageRawInfo.setPid(getUidFromProfileLink(pageLink));
            }
        } else {
            result = result + "-pagesListIsPrivate";
        }

        return result;
    }

    public String crawlFriendsFromGroup(FriendRawDTO friendRawDTO, String groupIdToCrawl) throws Exception {
        List<FriendRawDTO.FriendInfo> friendsList = new ArrayList<>();
        friendRawDTO.setFriends(friendsList);
        String result = "friendsFromGroup";

        String groupLink = "https://www.facebook.com/groups/" + groupIdToCrawl + "/";
        driver.goToPage(groupLink);
        for (int i = 0; i < 3; i++) {
            scroll(random(300, 500));
        }
        List<WebElement> UFIPagerLinkElementList = driver.getDriver().findElements(By.className("UFIPagerLink"));
        int countOut = 0;
        while (UFIPagerLinkElementList.size() > 0) {
            UFIPagerLinkElementList = driver.getDriver().findElements(By.className("UFIPagerLink"));
            int randomOrder = random(0, UFIPagerLinkElementList.size() - 1);
            scrollElementToMiddle(UFIPagerLinkElementList.get(randomOrder));
            click(UFIPagerLinkElementList.get(randomOrder));
            countOut = countOut + 1;
            if (countOut > 10) {
                break;
            }
        }
        List<WebElement> UFICommentActorNameElementList = driver.getDriver().findElements(By.className("UFICommentActorName"));
        for (WebElement UFICommentActorNameElement : UFICommentActorNameElementList) {
            FriendRawDTO.FriendInfo friendInfo = new FriendRawDTO.FriendInfo();
            friendsList.add(friendInfo);

            String friendName = UFICommentActorNameElement.getText();
            String friendProfileLink = UFICommentActorNameElement.getAttribute("href");

            friendInfo.setKey(groupIdToCrawl);
            friendInfo.setType("group");
            friendInfo.setName(friendName);
            friendInfo.setUid(getUidFromProfileLink(friendProfileLink));
        }
        result = result + "-found-" + friendsList.size() + "-friendProfileLink";
        return result;
    }

    public String crawlPagesFromKey(PageRawDTO pageRawDTO, String keyToFindPages) throws Exception {
        List<PageRawDTO.PageRawInfo> pagesList = new ArrayList<>();
        pageRawDTO.setPages(pagesList);
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 30);
        String result = "pagesFromKey";

        List<WebElement> searchInputElementList = driver.getDriver().findElements(By.xpath("//input[@data-testid='search_input']"));
        if (searchInputElementList.size() > 0) {
            result = result + "searchBoxFound";
            searchInputElementList.get(0).sendKeys(keyToFindPages);
            Thread.sleep(random(200, 400));
            driver.getDriver().findElement(By.xpath("//input[@data-testid='search_input']")).sendKeys(Keys.ENTER);
            waitForPageLoaded();
            WebElement pagesSearchTabElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href, 'search/pages')]")));
            click(pagesSearchTabElement);
            waitForPageLoaded();
            for (int i = 0; i < 10; i++) {
                scroll(random(300, 500));
            }
            WebElement searchResultSectionElement = driver.getDriver().findElement(By.id("pagelet_loader_initial_browse_result"));
            List<WebElement> pageElementList = searchResultSectionElement.findElements(By.xpath(".//a[contains(@href, '/?ref=br_rs') and contains(@class, 'lfloat')]"));
            for (WebElement pageElement : pageElementList) {
                PageRawDTO.PageRawInfo pageRawInfo = new PageRawDTO.PageRawInfo();
                pagesList.add(pageRawInfo);

                WebElement groupNameElement = pageElement.findElement(By.xpath("./following::*[contains(@href, '/?ref=br_rs')]"));
                WebElement groupMembersElement = pageElement.findElement(By.xpath(".//following::*[contains(text(), 'like this')]"));

                String pageName = groupNameElement.getText();
                String pageLike = groupMembersElement.getText();
                String[] segments = pageLike.split("members");
                String pageLink = pageElement.getAttribute("href");

                pageRawInfo.setKey(keyToFindPages);
                pageRawInfo.setType("key");
                pageRawInfo.setName(pageName);
                pageRawInfo.setPid(getUidFromProfileLink(pageLink));
            }
        } else {
            result = result + "searchBoxNotFound";
        }
        result = result + "-found-" + pagesList.size() + "-pages";
        return result;
    }

    public String crawlPagesFromDiscover(PageRawDTO pageRawDTO) throws Exception {
        List<PageRawDTO.PageRawInfo> pagesList = new ArrayList<>();
        pageRawDTO.setPages(pagesList);
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 30);
        String result = "pagesFromDiscover";
        driver.goToPage("https://www.facebook.com/pages/?category=top");

        List<WebElement> endOfSuggestion = driver.getDriver().findElements(By.xpath("//*[contains(text(),'End of suggestions')]"));
        int loopCount = 0;
        while (endOfSuggestion.isEmpty() && loopCount < 10) {
            scroll(random(300, 500));
            endOfSuggestion = driver.getDriver().findElements(By.xpath("//*[contains(text(),'End of suggestions')]"));
            loopCount++;
        }

        List<WebElement> pageInfoElementList = driver.getDriver().findElements(By.className("stat_elem"));
        for (WebElement pageInfoElement : pageInfoElementList) {
            List<WebElement> pageNameElementList = pageInfoElement.findElements(By.xpath(".//a[@target='_self' and @id]"));
            if (!pageNameElementList.isEmpty()) {
                PageRawDTO.PageRawInfo pageInfo = new PageRawDTO.PageRawInfo();
                pagesList.add(pageInfo);


                String pageName = pageNameElementList.get(0).findElement(By.className("img")).getAttribute("aria-label");
                int pageLike = 0;
                String pageLink = pageNameElementList.get(0).getAttribute("href");

                pageInfo.setKey("topSuggestion");
                pageInfo.setType("key");
                pageInfo.setName(pageName);
                pageInfo.setPid(getUidFromProfileLink(pageLink));
            }
        }
        result = result + "-found-" + pagesList.size() + "-pages";
        return result;
    }

    public String crawlGroupsFromDiscover(GroupRawDTO groupRawDTO) throws Exception {
        List<GroupRawDTO.GroupInfo> groupsList = new ArrayList<>();
        groupRawDTO.setGroups(groupsList);
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 30);
        String result = "groupsFromDiscover";

        driver.goToPage("https://www.facebook.com/groups/?category=discover");
        if (driver.getDriver().getCurrentUrl().contains("category=discover")) {
            result = result + "-discoverIsUnlocked";
            for (int i = 0; i < 30; i++) {
                scroll(random(300, 500));
            }
            List<WebElement> GroupDiscoverCardElementList = driver.getDriver().findElements(By.xpath("//div[contains(@id, 'GroupDiscoverCard')]"));
            GroupDiscoverCardElementList.forEach((GroupDiscoverCardElement) -> {
                String cardName = GroupDiscoverCardElement.findElement(By.xpath(".//span[contains(@role,'heading')]")).getText();
                List<WebElement> groupElementList = GroupDiscoverCardElement.findElements(By.xpath(".//li[contains(@id, 'GroupDiscoverCard')]"));
                groupElementList.forEach((groupElement) -> {
                    GroupRawDTO.GroupInfo groupInfo = new GroupRawDTO.GroupInfo();
                    groupsList.add(groupInfo);
                    try {
                        WebElement groupNameElement = groupElement.findElement(By.xpath(".//a[contains(@href,'ref=category_discover_landing')]"));
                        WebElement groupMembersElement = groupElement.findElement(By.xpath("./following::div[contains(text(), 'members')]"));

                        String groupName = groupNameElement.getText();
                        String groupMembers = groupMembersElement.getText();
                        String groupLink = groupNameElement.getAttribute("href");
                        int member = 0;

                        if (groupMembers.contains("friends")) {
                            String[] segments = groupMembers.split("friends");
                            String[] _segments = segments[1].split("members");
                            member = Integer.valueOf(_segments[0].replaceAll("[^0-9]", ""));
                        } else {
                            String[] segments = groupMembers.split("members");
                            member = Integer.valueOf(segments[0].replaceAll("[^0-9]", ""));
                        }

                        groupInfo.setKey(cardName);
                        groupInfo.setType("discover");
                        groupInfo.setName(groupName);
                        groupInfo.setMember(Integer.valueOf(member));
                        groupInfo.setGid(groupLink.replace("https://www.facebook.com/groups/", "").replace("/?ref=category_discover_landing", ""));


                    } catch (Exception e) {
                    }
                });
            });
        } else {
            result = result + "-discoverIsLocked";
        }
        return result;
    }

    public String crawlGroupsFromKey(GroupRawDTO groupRawDTO, String keyToFindGroups) throws Exception {
        List<GroupRawDTO.GroupInfo> groupsList = new ArrayList<>();
        groupRawDTO.setGroups(groupsList);
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 30);
        String result = "groupsFromKey";

        List<WebElement> searchInputElementList = driver.getDriver().findElements(By.xpath("//input[@data-testid='search_input']"));
        if (searchInputElementList.size() > 0) {
            result = result + "-searchBoxFound";
            searchInputElementList.get(0).sendKeys(keyToFindGroups);
            Thread.sleep(random(200, 400));
            driver.getDriver().findElement(By.xpath("//input[@data-testid='search_input']")).sendKeys(Keys.ENTER);
            waitForPageLoaded();
            WebElement groupsSearchTabElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[contains(@href, 'search/groups')]")));
            click(groupsSearchTabElement);
            waitForPageLoaded();
            for (int i = 0; i < 10; i++) {
                scroll(random(300, 500));
            }
            WebElement searchResultSectionElement = driver.getDriver().findElement(By.id("pagelet_loader_initial_browse_result"));
            List<WebElement> groupElementList = searchResultSectionElement.findElements(By.xpath(".//a[contains(@href, '/groups/') and contains(@class, 'lfloat')]"));
            groupElementList.forEach((groupElement) -> {
                GroupRawDTO.GroupInfo groupInfo = new GroupRawDTO.GroupInfo();
                groupsList.add(groupInfo);

                WebElement groupNameElement = groupElement.findElement(By.xpath("./following::a[contains(@href, '/groups/')]"));
                WebElement groupMembersElement = groupElement.findElement(By.xpath("./following::div[contains(text(), 'members')]"));

                String groupName = groupNameElement.getText();
                String groupMembers = groupMembersElement.getText();
                String[] segments = groupMembers.split("members");
                String groupLink = groupElement.getAttribute("href");

                groupInfo.setKey(keyToFindGroups);
                groupInfo.setType("key");
                groupInfo.setName(groupName);
                groupInfo.setMember(Integer.valueOf(segments[0].replace("K", "000").replace("M", "000000").replace(".", "").replace(" ", "")));
                groupInfo.setGid(groupLink.replace("https://www.facebook.com/groups/", "").replace("/?ref=br_rs", ""));
            });
        } else {
            result = result + "-searchBoxNotFound";
        }
        result = result + "-found-" + groupsList.size() + "-groups";
        return result;
    }

    public String crawlFriendsFromFanpage(FriendRawDTO friendRawDTO, String fanpageLink) throws Exception {
        List<FriendRawDTO.FriendInfo> friendsList = new ArrayList<>();
        friendRawDTO.setFriends(friendsList);
        String result = "friendsFromFanpage";
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 30);

        driver.goToPage(fanpageLink);

        for (int i = 0; i < 10; i++) {
            scroll(random(300, 500));
        }
        List<WebElement> UFIReplySocialSentenceLinkTextElementList = driver.getDriver().findElements(By.className("UFIReplySocialSentenceLinkText"));
        List<WebElement> UFIPagerLinkElementList = driver.getDriver().findElements(By.className("UFIPagerLink"));
        int countOut = 0;
        while (UFIReplySocialSentenceLinkTextElementList.size() > 0 || UFIPagerLinkElementList.size() > 0) {
            UFIReplySocialSentenceLinkTextElementList = driver.getDriver().findElements(By.className("UFIReplySocialSentenceLinkText"));
            UFIPagerLinkElementList = driver.getDriver().findElements(By.className("UFIPagerLink"));
            if (UFIReplySocialSentenceLinkTextElementList.size() > 0) {
                int randomOrder = random(0, UFIReplySocialSentenceLinkTextElementList.size() - 1);
                scrollElementToMiddle(UFIPagerLinkElementList.get(randomOrder));
                click(UFIPagerLinkElementList.get(randomOrder));
            }
            if (UFIPagerLinkElementList.size() > 0) {
                int randomOrder = random(0, UFIPagerLinkElementList.size() - 1);
                scrollElementToMiddle(UFIPagerLinkElementList.get(randomOrder));
                click(UFIPagerLinkElementList.get(randomOrder));
            }
            if (countOut > 10) {
                break;
            }
        }
        List<WebElement> UFICommentActorNameElementList = driver.getDriver().findElements(By.className("UFICommentActorName"));
        for (WebElement UFICommentActorNameElement : UFICommentActorNameElementList) {
            FriendRawDTO.FriendInfo friendInfo = new FriendRawDTO.FriendInfo();
            friendsList.add(friendInfo);

            String friendName = UFICommentActorNameElement.getText();
            String friendProfileLink = UFICommentActorNameElement.getAttribute("href");

            friendInfo.setKey(fanpageLink);
            friendInfo.setType("fanpage");
            friendInfo.setName(friendName);
            friendInfo.setUid(getUidFromProfileLink(friendProfileLink));
        }
        result = result + "-found-" + friendsList.size() + "-friendProfileLink";
        return result;
    }

    public String friendsFromWebsite(FriendRawDTO friendRawDTO, String websiteAdress, int maximumLoop) throws Exception {
        String result = "friendsFromWebsite";
        String targetLink = "http://www." + websiteAdress + "/";
        driver.goToPage(targetLink);

        int loopCount = 0;
        List<String> hrefLink = new ArrayList<>();
        List<String> scannedHrefLink = new ArrayList<>();

        while (loopCount < maximumLoop) {
            List<FriendRawDTO.FriendInfo> friendsList = new ArrayList<>();
            friendRawDTO.setFriends(friendsList);
            List<WebElement> hrefExistElementList = driver.getDriver().findElements(By.xpath("//*[@href]"));
            hrefExistElementList.forEach((hrefExistElement) -> {
                if (hrefExistElement.getAttribute("href").contains(websiteAdress)) {
                    hrefLink.add(hrefExistElement.getAttribute("href"));
                }
            });
            int randomOrder = random(0, hrefLink.size() - 1);
            scrollToBottom();
            List<WebElement> facebookIframeElementList = driver.getDriver().findElements(By.xpath("//iframe[@title='Facebook Social Plugin']"));
            if (facebookIframeElementList.size() > 0) {
                driver.getDriver().switchTo().frame(facebookIframeElementList.get(0));
                List<WebElement> load10MoreComment = driver.getDriver().findElements(By.xpath("//button[@role='button']"));
                int loadCommentLoopCount = 0;
                while (load10MoreComment.size() > 0 && loadCommentLoopCount < 100) {
                    load10MoreComment = driver.getDriver().findElements(By.xpath("//button[@role='button']"));
                    if (!load10MoreComment.isEmpty()) {
                        scrollElementToMiddle(load10MoreComment.get(0));
                        load10MoreComment.get(0).click();
                        waitForPageLoaded();
                        loadCommentLoopCount = loadCommentLoopCount + 1;
                    }
                }
                List<WebElement> UFICommentActorNameElementList = driver.getDriver().findElements(By.className("UFICommentActorName"));
                for (WebElement UFICommentActorNameElement : UFICommentActorNameElementList) {

                    String friendName = UFICommentActorNameElement.getText();
                    String friendProfileLink = UFICommentActorNameElement.getAttribute("href");
                    if (!isNullOrEmptyStr(friendProfileLink)) {
                        FriendRawDTO.FriendInfo friendInfo = new FriendRawDTO.FriendInfo();
                        friendsList.add(friendInfo);

                        friendInfo.setKey(websiteAdress);
                        friendInfo.setType("website");
                        friendInfo.setName(friendName);
                        if (friendProfileLink.contains("https://www.facebook.com/people/")) {
                            friendInfo.setUid(friendProfileLink.replaceAll("[^0-9]", ""));
                        } else {
                            friendInfo.setUid(getUidFromProfileLink(friendProfileLink));
                        }
                    }
                }
                friendRawDTO.setAccountId(driver.getSigma().getAccount().getAccountId());
                ServerService.sendDTOToServer(SERVER_CRAWL_FRIEND_RAW, friendRawDTO);
            }
        }
        result = result + "-foundSomeProfile";
        return result;
    }

    public String crawlFriendsFromNewsFeed(FriendRawDTO friendRawDTO, long accountId) throws Exception {
        List<FriendRawDTO.FriendInfo> friendsList = new ArrayList<>();
        friendRawDTO.setFriends(friendsList);
        String result = "friendsFromNewsFeed";
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 30);
        for (int i = 0; i < 10; i++) {
            scroll(random(300, 500));
        }
        List<WebElement> UFIReplySocialSentenceLinkTextElementList = driver.getDriver().findElements(By.className("UFIReplySocialSentenceLinkText"));
        List<WebElement> UFIPagerLinkElementList = driver.getDriver().findElements(By.className("UFIPagerLink"));
        int countOut = 0;
        while (!UFIReplySocialSentenceLinkTextElementList.isEmpty() || !UFIPagerLinkElementList.isEmpty()) {
            UFIReplySocialSentenceLinkTextElementList = driver.getDriver().findElements(By.className("UFIReplySocialSentenceLinkText"));
            if (!UFIReplySocialSentenceLinkTextElementList.isEmpty()) {
                int randomOrder1 = random(0, UFIReplySocialSentenceLinkTextElementList.size() - 1);
                try {
                    scrollElementToMiddle(UFIPagerLinkElementList.get(randomOrder1));
                    click(UFIPagerLinkElementList.get(randomOrder1));
                } catch (Exception ignore) {
                }
            }
            UFIPagerLinkElementList = driver.getDriver().findElements(By.className("UFIPagerLink"));
            if (!UFIPagerLinkElementList.isEmpty()) {
                int randomOrder = random(0, UFIPagerLinkElementList.size() - 1);
                try {
                    scrollElementToMiddle(UFIPagerLinkElementList.get(randomOrder));
                    click(UFIPagerLinkElementList.get(randomOrder));
                } catch (Exception ignore) {
                }
            }
            countOut++;
            if (countOut > 10) {
                break;
            }
        }
        List<WebElement> UFICommentActorNameElementList = driver.getDriver().findElements(By.className("UFICommentActorName"));
        for (WebElement UFICommentActorNameElement : UFICommentActorNameElementList) {
            FriendRawDTO.FriendInfo friendInfo = new FriendRawDTO.FriendInfo();
            friendsList.add(friendInfo);

            String friendName = UFICommentActorNameElement.getText();
            String friendProfileLink = UFICommentActorNameElement.getAttribute("href");

            friendInfo.setKey("" + accountId);
            friendInfo.setType("newsfeed");
            friendInfo.setName(friendName);
            friendInfo.setUid(getUidFromProfileLink(friendProfileLink));
        }
        result = result + "-found-" + friendsList.size() + "-friendProfileLink";
        return result;
    }

    public String postsFromPage(String pageLinkToCrawl) throws Exception {
        ServerService service = new ServerService();
        String result = "crawlPostsFromPage";
        //goto [posts]
        WebElement postProgressBarElement = driver.getDriver().findElement(By.xpath("//a[contains(@data-endpoint,'posts/?ref=page_internal')]"));
        click(postProgressBarElement);
        waitForPageLoaded();
        int randomScroll = random(5, 10);
        //get List post
        List<WebElement> userContentWrapperElementList = driver.getDriver().findElements(By.className("userContentWrapper"));
        for (int i = 0; i < randomScroll; i++) {
            scroll(500);
            waitForPageLoaded();
            userContentWrapperElementList = driver.getDriver().findElements(By.className("userContentWrapper"));
            if (userContentWrapperElementList.size() > 5) {
                break;
            }
        }
        List<WebElement> acceptedPostElementList = new ArrayList<>();
        int successTime = 0;
        //classify post text-img-video-share-link
        for (WebElement userContentWrapperElement : userContentWrapperElementList) {
            WebElement postContentElement = userContentWrapperElement.findElement(By.xpath(".//div[1]"));
            List<WebElement> videoTagElementList = postContentElement.findElements(By.xpath(".//video[@height]"));
            if (videoTagElementList.size() > 0) {
                //post contain video
                if (!userContentWrapperElement.getText().contains("shared")) {
                    StatusDTO dto = new StatusDTO();
                    dto.setAccountId(driver.getSigma().getAccount().getAccountId());
                    String postId = userContentWrapperElement.findElement(By.xpath(".//div[contains(@id,'feed_subtitle_')]")).getAttribute("id");
                    dto.setPostId(postId);
                    dto.setType("video");
                    String postLink = userContentWrapperElement.findElement(By.className("timestampContent")).findElement(By.xpath("./parent::*/parent::*")).getAttribute("href");
                    dto.setCaption(postLink);
                    ServerService.sendDTOToServer(SERVER_POST_STATUS, dto);
                    successTime = successTime + 1;
                }
            }
            List<WebElement> uiScaledImageContainerElementList = postContentElement.findElements(By.className("uiScaledImageContainer"));
            if (uiScaledImageContainerElementList.size() > 0) {
                //post contain image
                if (!userContentWrapperElement.getText().contains("shared")) {
                    acceptedPostElementList.add(userContentWrapperElement);
                }
            }
            List<WebElement> hrefElementList = postContentElement.findElements(By.xpath(".//*[@href]"));
//            hrefElementList.forEach((hrefElement) -> {
//                if (!hrefElement.getAttribute("href").contains("https://www.facebook.com/")) {
//                    acceptedPostElementList.remove(userContentWrapperElement);
//                }
//            });
        }

        for (WebElement acceptedPostElement : acceptedPostElementList) {
            StatusDTO dto = new StatusDTO();
            dto.setAccountId(driver.getSigma().getAccount().getAccountId());

            String postId = acceptedPostElement.findElement(By.xpath(".//div[contains(@id,'feed_subtitle_')]")).getAttribute("id");
            dto.setPostId(postId);

//            scrollElementToMiddle(acceptedPostElement);
            String caption = acceptedPostElement.findElement(By.className("userContent")).getText();

            List<WebElement> scaledImageFitHeightElementList = acceptedPostElement.findElements(By.className("scaledImageFitHeight"));
            List<WebElement> scaledImageFitWidthElementList = acceptedPostElement.findElements(By.className("scaledImageFitWidth"));

            dto.setCaption(caption);
            dto.setFbProfileLink(pageLinkToCrawl);
            if (scaledImageFitHeightElementList.size() > 0) {
                String imageURL = scaledImageFitHeightElementList.get(0).getAttribute("src");
                getImageBase(dto, scaledImageFitHeightElementList, service, imageURL);
                result = "crawledImageStatus";
            } else if (scaledImageFitWidthElementList.size() > 0) {
                String imageURL = scaledImageFitWidthElementList.get(0).getAttribute("src");
                getImageBase(dto, scaledImageFitWidthElementList, service, imageURL);
                result = "crawledImageStatus";
            }

            ServerService.sendDTOToServer(SERVER_POST_STATUS, dto);
            successTime = successTime + 1;
        }
        result = result + successTime + "-COMPLETED";
        return result;
    }

    public String crawlProfileLink(ProfileLinkDTO dto, int likePerPost, int commentPerPost, int minPost, int count) throws Exception {
        String result = NOTHING;
        WebDriverWait wait = new WebDriverWait(driver.getDriver(), 30);
        returnToNewsFeed();
        int randomScroll = random(5, 10);
        for (int i = 0; i < randomScroll; i++) {
            scroll(500);
            waitForPageLoaded();
        }
        List<WebElement> profileLinkElementList = driver.getDriver().findElements(By.className("profileLink"));
        if (profileLinkElementList.size() > 0) {
            result = "foundAProfileLink";
            int randomOder = random(0, profileLinkElementList.size() - 1);
            scrollElementToMiddle(profileLinkElementList.get(randomOder));
            click(profileLinkElementList.get(randomOder));
            waitForPageLoaded();
            List<WebElement> friendButtonElementList = driver.getDriver().findElements(By.className("friendButton"));
            if (friendButtonElementList.size() == 0) {
                result = "foundAProfileLink-stranger";
                return crawlProfileLink(dto, likePerPost, commentPerPost, minPost, ++count);
            }
            for (int i = 0; i < 15; i++) {
                scroll(500);
                waitForPageLoaded();
            }

            List<WebElement> userContentWrapperElementList = driver.getDriver().findElements(By.className("userContentWrapper"));
            List<WebElement> UFICommentBodyElementList = driver.getDriver().findElements(By.className("UFICommentBody"));
            List<WebElement> UFILikeSentenceElementList = driver.getDriver().findElements(By.className("UFILikeSentence"));

            int posts = userContentWrapperElementList.size();
            int comments = UFICommentBodyElementList.size();
            int likes = 0;

            for (WebElement UFILikeSentenceElement : UFILikeSentenceElementList) {
                likes = likes + getNumberInString(UFILikeSentenceElement.getText());
            }

            List<WebElement> UFIReplySocialSentenceRowElementList = driver.getDriver().findElements(By.className("UFIReplySocialSentenceRow"));
            for (WebElement UFIReplySocialSentenceRowElement : UFIReplySocialSentenceRowElementList) {
                comments = comments + getNumberInString(UFIReplySocialSentenceRowElement.getText());
            }
            List<WebElement> UFIPagerRowElementList = driver.getDriver().findElements(By.className("UFIPagerRow"));
            for (WebElement UFIPagerRowElement : UFIPagerRowElementList) {
                comments = comments + getNumberInString(UFIPagerRowElement.getText());
            }

            int avgComments = 0;
            int avgLikes = 0;

            try {
                avgComments = comments / posts;
                avgLikes = likes / posts;
            } catch (Exception e) {
            }

            if (posts >= minPost && avgLikes >= likePerPost && avgComments >= commentPerPost) {
                List<WebElement> nameButtonElement = driver.getDriver().findElements(By.className("nameButton"));
                if (nameButtonElement.size() > 0) {
                    click(nameButtonElement.get(0));
                    waitForPageLoaded();
                    String profileLink = driver.getDriver().getCurrentUrl();
                    dto.setProfileLink(profileLink);

                    return "foundAEligibleProfileLink";
                }
            } else {
                if (count <= 5) {
                    return crawlProfileLink(dto, likePerPost, commentPerPost, minPost, ++count);
                } else {
                    return "couldNotFindEligibleProfileLink";
                }
            }
        }
        return "couldNotFindEligibleProfileLink";
    }
    public String evaluatePost() throws Exception {
        String result = "evaluatePost";
        //TODO
        return result;
    }
    public String evaluatePage(PageEvalDTO pageEvalDTO, String pageId) throws Exception {
        String result = "evaluatePage";
        String pageLink = "https://www.facebook.com/" + pageId + "&ref=br_rs";
        driver.goToPage(pageLink);
        /* Integer likesPage */
        WebElement likePageElementList = driver.getDriver().findElement(By.xpath("//div[text()='people like this']"));
        pageEvalDTO.setLikesPage(Integer.valueOf(likePageElementList.getText().replaceAll("[^0-9]", "")));
        /* Integer followsPage */
        WebElement followElementList = driver.getDriver().findElement(By.xpath("//div[text()='people follow this']"));
        pageEvalDTO.setFollowsPage(Integer.valueOf(followElementList.getText().replaceAll("[^0-9]", "")));
        /* Integer posts */
        String pagePostsLink = "https://www.facebook.com/" + pageId + "/posts/?ref=page_internal";
        driver.goToPage(pagePostsLink);
        for (int i = 0; i < 10; i++) {
            scroll(random(300, 500));
        }
        List<WebElement> userContentWrapperElementList = driver.getDriver().findElements(By.className("userContentWrapper"));
        List<WebElement> UFICommentBodyElementList = driver.getDriver().findElements(By.className("UFICommentBody"));
        List<WebElement> UFILikeSentenceElementList = driver.getDriver().findElements(By.className("UFILikeSentence"));

        int posts = userContentWrapperElementList.size();
        int comments = UFICommentBodyElementList.size();
        int likes = 0;
        String postTime = "";

        for (WebElement UFILikeSentenceElement : UFILikeSentenceElementList) {
            likes = likes + getNumberInString(UFILikeSentenceElement.getText());
        }

        List<WebElement> UFIReplySocialSentenceRowElementList = driver.getDriver().findElements(By.className("UFIReplySocialSentenceRow"));
        for (WebElement UFIReplySocialSentenceRowElement : UFIReplySocialSentenceRowElementList) {
            comments = comments + getNumberInString(UFIReplySocialSentenceRowElement.getText());
        }
        List<WebElement> UFIPagerRowElementList = driver.getDriver().findElements(By.className("UFIPagerRow"));
        for (WebElement UFIPagerRowElement : UFIPagerRowElementList) {
            comments = comments + getNumberInString(UFIPagerRowElement.getText());
        }
        List<WebElement> postTimeElementList = driver.getDriver().findElements(By.className("timestampContent"));
        for (WebElement postTimeElement : postTimeElementList) {
            postTime = postTime + postTimeElement.getText();
        }
        pageEvalDTO.setPosts(posts);
        /* Integer likes */
        pageEvalDTO.setLikes(likes);
        /* Integer comments */
        pageEvalDTO.setComments(comments);
        return result;
    }
    public String evaluateGroup(GroupEvalDTO groupEvalDTO, String groupId, long accountId) throws Exception {
        String result = "evaluateGroup";
        String groupLink = "https://www.facebook.com/groups/" + groupId + "/";
        String groupAbout = groupLink + "about/";
        driver.goToPage(groupAbout);
        WebElement groupAboutElementList = driver.getDriver().findElement(By.id("pagelet_group_about"));
        /* description */
        List<WebElement> seeMoreElementList = groupAboutElementList.findElements(By.linkText("See More"));
        if (!seeMoreElementList.isEmpty()) {
            scrollElementToMiddle(seeMoreElementList.get(0));
            click(seeMoreElementList.get(0));
        }
        StringBuilder groupDescription = new StringBuilder();
        List<WebElement> descriptionElementList = groupAboutElementList.findElements(By.xpath(".//div[@role='heading']"));
        for (WebElement descriptionElement : descriptionElementList) {
            groupDescription.append("|||").append(descriptionElement.getText());
        }
        groupEvalDTO.setDescription(groupDescription.toString());
        /* postsToday */
        WebElement postTodayElement = groupAboutElementList.findElement(By.xpath(".//div[text()='New posts today']/parent::*"));
        String postTodayText = postTodayElement.getText();
        String[] segments = postTodayText.split("\n");
        Integer postsToday = Integer.valueOf(segments[0].replaceAll("[^0-9]", ""));
        groupEvalDTO.setPostsToday(postsToday);
        /* posts30Day */
        WebElement postIn30DaysElement = groupAboutElementList.findElement(By.xpath(".//div[text()='New posts today']/following::div[text()='in the last 30 days']"));
        String post30daysText = postIn30DaysElement.getText().replace("in the last 30 days", "").replaceAll("[^0-9]", "");
        Integer post30days = Integer.valueOf(post30daysText);
        groupEvalDTO.setPosts30Day(post30days);
        /*members in group*/
        WebElement membersIn30DaysElement = groupAboutElementList.findElement(By.xpath(".//div[text()='Members']/following::div[text()='in the last 30 days']"));
        WebElement memberInGroupElement = membersIn30DaysElement.findElement(By.xpath("./parent::*"));
        String[] memberSegments = memberInGroupElement.getText().split("\n");
        Integer members = Integer.valueOf(memberSegments[0].replaceAll("[^0-9]", ""));
        Integer membersIn30Days = Integer.valueOf(membersIn30DaysElement.getText().replace("in the last 30 days", "").replaceAll("[^0-9]", ""));
        groupEvalDTO.setPosts30Day(membersIn30Days);
        groupEvalDTO.setMembers(members);
        /* created*/
        WebElement timeStampElement = groupAboutElementList.findElement(By.className("timestamp"));
        groupEvalDTO.setCreated(timeStampElement.getAttribute("title"));
        /*privacy*/
        String closedGroupPrivacyXpath = "//span[@data-tooltip-content='Anyone can find the group and see who's in it. Only members can see posts.']";
        String publicGroupPrivacyXpath = "//span[@data-tooltip-content='Anyone can find the group and see who's in it. Only members can see posts.']";
        List<WebElement> closedGroupPrivacyElementList = driver.getDriver().findElements(By.xpath(closedGroupPrivacyXpath));
        if (closedGroupPrivacyElementList.isEmpty()) {
            List<WebElement> publicGroupPrivacyElementList = driver.getDriver().findElements(By.xpath(publicGroupPrivacyXpath));
            if (publicGroupPrivacyElementList.isEmpty()) {
                result += "-unknownPrivacy";
                groupEvalDTO.setGroupPrivacy("unknown");
            } else {
                groupEvalDTO.setGroupPrivacy("public");
                result += "-publicGroup";
            }
        } else {
            groupEvalDTO.setGroupPrivacy("close");
            result += "-closedGroup";
        }
        GroupHandler groupHandler = new GroupHandler(driver);
        result += groupHandler.joinGroup(groupId, accountId);
        if (result.contains("requested")) {
            groupEvalDTO.setJoiningStatus("requested");
        } else if (result.contains("joined")) {
            groupEvalDTO.setJoiningStatus("joined");
        }
        /* posts */
        if (result.contains("publicGroup")) {

            for (int i = 0; i < 10; i++) {
                scroll(random(300, 500));
            }
            List<WebElement> userContentWrapperElementList = driver.getDriver().findElements(By.className("userContentWrapper"));
            List<WebElement> UFICommentBodyElementList = driver.getDriver().findElements(By.className("UFICommentBody"));
            List<WebElement> UFILikeSentenceElementList = driver.getDriver().findElements(By.className("UFILikeSentence"));

            int posts = userContentWrapperElementList.size();
            int comments = UFICommentBodyElementList.size();
            int likes = 0;
            String postTime = "";

            for (WebElement UFILikeSentenceElement : UFILikeSentenceElementList) {
                likes = likes + getNumberInString(UFILikeSentenceElement.getText());
            }

            List<WebElement> UFIReplySocialSentenceRowElementList = driver.getDriver().findElements(By.className("UFIReplySocialSentenceRow"));
            for (WebElement UFIReplySocialSentenceRowElement : UFIReplySocialSentenceRowElementList) {
                comments = comments + getNumberInString(UFIReplySocialSentenceRowElement.getText());
            }
            List<WebElement> UFIPagerRowElementList = driver.getDriver().findElements(By.className("UFIPagerRow"));
            for (WebElement UFIPagerRowElement : UFIPagerRowElementList) {
                comments = comments + getNumberInString(UFIPagerRowElement.getText());
            }
            List<WebElement> postTimeElementList = driver.getDriver().findElements(By.className("timestampContent"));
            for (WebElement postTimeElement : postTimeElementList) {
                postTime = postTime + postTimeElement.getText();
            }
            groupEvalDTO.setPosts(posts);
            /* likes */
            groupEvalDTO.setLikes(likes);
            /* comments */
            groupEvalDTO.setComments(comments);
        }
        if (result.contains("joined")) {
            result += "-postImage";
            WebDriverWait wait = new WebDriverWait(driver.getDriver(), 100);
            List<WebElement> writePostElementList = driver.getDriver().findElements(By.className("fbReactComposerAttachmentSelector_STATUS"));
            if (!writePostElementList.isEmpty()) {
                result = result + "-allowedToPostStatus";
                click(writePostElementList.get(0));
                delay();
                WebElement inputTextBoxElement = driver.getDriver().findElement(By.xpath("//div[@data-testid='status-attachment-mentions-input']"));

                ServerService service = new ServerService();
                StatusDTO statusDTO = service.getStatusDTOFromServer(accountId, "imageForEvaluationGroup");
                String caption = randomEmotion();
                if (statusDTO.getCaption().length() > 5) {
                    caption = statusDTO.getCaption();
                }
                inputTextBoxElement.sendKeys(caption);
                delay();
                if (statusDTO.getBytes().length > 10) {
                    result += "-imgStatus";
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
                    result += "-textStatus";
                }
                try {
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@data-testid='media-attachment-photo']")));
                } catch (Exception e) {
                    result += "-uploadImgFailed";
                }
                WebElement postButtonElement = driver.getDriver().findElement(By.xpath("//button[@data-testid='react-composer-post-button']"));
                click(postButtonElement);
                delay();
                try {
                    WebElement adminApprovalElement = driver.getDriver().findElement(By.className("uiBoxYellow"));
                    if (adminApprovalElement.getText().contains("by an admin")) {
                        result += "-requireAdminProve";
                        groupEvalDTO.setEvaluationPostLink("admin");
                    }
                } catch (Exception ignored) {
                }
                WebElement timestampContentElement = driver.getDriver().findElement(By.className("timestampContent"));
                if (timestampContentElement.getText().contains("Just now")) {
                    click(timestampContentElement);
                    waitForPageLoaded();
                    result += "-postedImgTo-" + groupId + "-" + driver.getDriver().getCurrentUrl();
                    groupEvalDTO.setEvaluationPostLink(driver.getDriver().getCurrentUrl());
                }
            } else {
                result += "-NOTAllowedToPostStatus";
            }
        }
        return result;
    }
    private void scrollDownUntilMoreAbout() throws Exception {
        int countLoop = 0;
        List<WebElement> moreAboutList = driver.getDriver().findElements(By.xpath("//h3[contains(text(), 'More About')]"));
        while (countLoop < 10 && moreAboutList.isEmpty()) {
            scroll(random(500, 800));
            moreAboutList = driver.getDriver().findElements(By.xpath("//h3[contains(text(), 'More About')]"));
            countLoop++;
        }
    }
    public String evaluateFriend(FriendEvalDTO friendEvalDTO, String friendName, String friendId) throws Exception {

        String result = "evaluateFriend";
        if (!goProfileThroughSearch(friendName, friendId)) {
            String friendProfileLink = "https://www.facebook.com/" + friendId;
            driver.goToPage(friendProfileLink);
        }
        //kiểm tra trạng thái kết bạn
        WebElement pageLetTimelineProfileActions = driver.getDriver().findElement(By.id("pagelet_timeline_profile_actions"));
        List<WebElement> friendButtonElementList = pageLetTimelineProfileActions.findElements(By.className("FriendButton"));
        if (friendButtonElementList.isEmpty()) {
            result = result + "-friendAddDisabled";
            //add friend disabled
            friendEvalDTO.setRelationship("disabled");
        } else {
            switch (friendButtonElementList.get(0).getText()) {
                case "Add Friend":
                    result = result + "-addFriend";
                    friendEvalDTO.setRelationship("addFriend");
                    break;
                case "Friend Request Sent":
                    result = result + "-friendRequestSent";
                    friendEvalDTO.setRelationship("friendRequestSent");
                    break;
                case "Friend\nFriends":
                    result = result + "-alreadyFriend";
                    friendEvalDTO.setRelationship("alreadyFriend");
                    break;
                case "Respond to Friend Request":
                    result = result + "-respondToFriendRequest";
                    friendEvalDTO.setRelationship("respondToFriendRequest");
                    break;
                default:
                    break;
            }
        }
        //get life story(intro)
        WebElement introContainerElement = driver.getDriver().findElement(By.id("intro_container_id"));
        List<WebElement> customInfoElementList = introContainerElement.findElements(By.xpath(".//li[@data-store='{\"event\":\"context_item_view_click\"}']"));
        String life_story = "";
        for (WebElement customInfoElement : customInfoElementList) {
            String customInfoText = customInfoElement.getText();
            life_story = life_story + "|||" + customInfoText;
            //get follow
            if (customInfoText.contains("Followed by")) {
                friendEvalDTO.setFollow(Integer.valueOf(customInfoText.replaceAll("[^0-9]", "")));
            }
        }
        friendEvalDTO.setLifeStory(life_story);
        //get posts-likes-comments
        for (int i = 0; i < 10; i++) {
            scroll(random(300, 500));
        }
        List<WebElement> userContentWrapperElementList = driver.getDriver().findElements(By.className("userContentWrapper"));
        List<WebElement> _userContentWrapperElementList = Lists.newArrayList(userContentWrapperElementList);
        for (WebElement userContentWrapperElement : userContentWrapperElementList) {
            String userContentWrapperElementText = userContentWrapperElement.getText();
            if (userContentWrapperElementText.contains("shared") || userContentWrapperElementText.contains("tagged")) {
                _userContentWrapperElementList.remove(userContentWrapperElement);
            }
        }
        int posts = _userContentWrapperElementList.size();
        int comments = 0;
        int likes = 0;
        String postTime = "";
        for (WebElement _userContentWrapperElement : _userContentWrapperElementList) {
            List<WebElement> UFICommentBodyElementList = _userContentWrapperElement.findElements(By.className("UFICommentBody"));
            List<WebElement> UFILikeSentenceElementList = _userContentWrapperElement.findElements(By.className("UFILikeSentence"));
            comments += UFICommentBodyElementList.size();

            for (WebElement UFILikeSentenceElement : UFILikeSentenceElementList) {
                likes = likes + getNumberInString(UFILikeSentenceElement.getText());
            }

            List<WebElement> UFIReplySocialSentenceRowElementList = _userContentWrapperElement.findElements(By.className("UFIReplySocialSentenceRow"));
            for (WebElement UFIReplySocialSentenceRowElement : UFIReplySocialSentenceRowElementList) {
                comments = comments + getNumberInString(UFIReplySocialSentenceRowElement.getText());
            }
            List<WebElement> UFIPagerRowElementList = _userContentWrapperElement.findElements(By.className("UFIPagerRow"));
            for (WebElement UFIPagerRowElement : UFIPagerRowElementList) {
                comments = comments + getNumberInString(UFIPagerRowElement.getText());
            }
            List<WebElement> postTimeElementList = _userContentWrapperElement.findElements(By.className("timestampContent"));
            for (WebElement postTimeElement : postTimeElementList) {
                postTime += "|||" + postTimeElement.getText();
            }
        }

        friendEvalDTO.setPosts(posts);
        friendEvalDTO.setLikes(likes);
        friendEvalDTO.setComments(comments);
        friendEvalDTO.setPostsTime(postTime);
        //get longest caption
        String longestCaption = "";
        String salePostLink = "";
        for (WebElement _userContentWrapperElement : _userContentWrapperElementList) {
            String caption = _userContentWrapperElement.findElement(By.className("userContent")).getText();
            if (caption.length() > longestCaption.length()) {
                longestCaption = caption;
            }
            if (isSaler(caption)) {
                WebElement postLinkElment = _userContentWrapperElement.findElement(By.xpath(".//span[@class='timestampContent']/parent::abbr/parent::a"));
                salePostLink += "|||" + postLinkElment.getAttribute("href");
            }
        }
        longestCaption += "|||" + salePostLink;
        friendEvalDTO.setCaption(longestCaption);
        //get friends number
        List<WebElement> friendNumberElementList = driver.getDriver().findElements(By.xpath("//a[contains(@href,'/friends') and not(contains(@href, 'mutual'))]"));
        for (WebElement friendNumberElement : friendNumberElementList) {
            String friendText = friendNumberElement.getText().replaceAll("[*a-zA-Z]", "").replaceAll(",", "");
            if (friendText.length() > 1) {
                //friend Number
                friendEvalDTO.setFriend(Integer.valueOf(friendText.replaceAll("[^0-9]", "")));
            }
        }
        //get photos number
        WebElement timelineNavTop = driver.getDriver().findElement(By.xpath("//ul[@data-referrer='timeline_light_nav_top']"));
        WebElement photoTabElement = timelineNavTop.findElement(By.xpath(".//a[@data-tab-key='photos']"));
        scrollElementToMiddle(photoTabElement);
        click(photoTabElement);
        waitForPageLoaded();
        scrollDownUntilMoreAbout();
        List<WebElement> photoThumbElementList = driver.getDriver().findElements(By.className("uiMediaThumbMedium"));
        friendEvalDTO.setPhotos(photoThumbElementList.size());
        //get check-in time
        List<WebElement> tabElementList = timelineNavTop.findElements(By.xpath(".//a[@role='button' and text()='More']"));
        if (!tabElementList.isEmpty()) {
            scrollElementToMiddle(tabElementList.get(0));
            move(tabElementList.get(0));
            waitForPageLoaded();
            List<WebElement> menuElementList = driver.getDriver().findElements(By.xpath("//a[@role='menuitem' and @data-tab-key='map']"));
            for (WebElement menuElement : menuElementList) {
                if (menuElement.getText().contains("Check-Ins")) {
                    click(menuElement);
                    waitForPageLoaded();
                }
            }
        }

        if (!driver.getDriver().getCurrentUrl().contains("/map?")) {
            String checkInLink = "https://www.facebook.com/" + friendId + "/map";
            driver.goToPage(checkInLink);
        }
        scrollDownUntilMoreAbout();
        List<WebElement> checkInSectionElementList = driver.getDriver().findElements(By.id("pagelet_timeline_medley_map"));
        if (!checkInSectionElementList.isEmpty()) {
            List<WebElement> checkInImageElementList = checkInSectionElementList.get(0).findElements(By.className("uiScaledImageContainer"));
            friendEvalDTO.setCheckin(checkInImageElementList.size());
        }
        //get videos number

        return result;
    }

    public void modernMobileFriendImageBackUp(int maxFriendCrawl){
        Actions ac = new Actions(driver.getDriver());
        modernMobileReturnToNewsFeed();
        try {
            ac.moveToElement(driver.getDriver().findElement(By.id("MComposer"))
            .findElement(By.xpath(".//a[contains(@href, 'm.facebook.com')]"))).click().build().perform();
            Thread.sleep(1000);
            waitForPageLoaded();
            List<WebElement> friendElements = driver.getDriver().findElements(By.xpath(".//a[contains(@href, '/friends?')]"));
            for(WebElement friendElement: friendElements){
                if(friendElement.getAttribute("data-store").contains("\"section_type\":\"friends\"")){
                    _move(friendElement);
                    ac.moveToElement(friendElement).click().build().perform();
                    Thread.sleep(1000);
                    waitForPageLoaded();
                    break;
                }
            }
            List<String> friendLinks = new ArrayList<>();
            for(int i=0;i<6;i++){
                List<WebElement> darkTouchElements = driver.getDriver().findElements(By.className("darkTouch"));
                for(WebElement darkTouchElement: darkTouchElements){
                    if(friendLinks.size() >= maxFriendCrawl){
                        break;
                    }
                    String hrefString = null;
                    try{
                        hrefString = darkTouchElement.getAttribute("href");
                    }catch (Exception ignored){}
                    if(hrefString !=null && hrefString.contains("facebook.com") && friendLinks.indexOf(hrefString)!=-1){
                        friendLinks.add(hrefString);
                    }
                }
                //TODO CLICK LOAD MORE FRIENDS
            }
            for(String friendLink: friendLinks){
                driver.goToPage(friendLink);
                waitForPageLoaded();

                WebElement coverSectionElement = driver.getDriver().findElement(By.id("m-timeline-cover-section"));
                List<WebElement> coverAndProfilePics = coverSectionElement.findElements(By.xpath(".//a[contains(@href, '/photo.php?')]"));
                for(WebElement coverAndProfilePic:coverAndProfilePics){
                    String coverAndProfilePicUrl = coverAndProfilePic.findElement(By.xpath(".//i[contains(@style, 'url')]")).getAttribute("style");

                    //TODO xu ly: background: url("https://scontent.fhan5-3.fna.fbcdn.net/v/t1.0-9/cp0/e15/q65/c19.0.2010.1128/42959082_1852184141568272_438030601983885312_o.jpg?_nc_cat=106&efg=eyJpIjoidCJ9&_nc_ht=scontent.fhan5-3.fna&oh=d46d231966358c2db1c03cbd9baf6fe5&oe=5C464AAE") center center / 100% 100% no-repeat rgb(255, 255, 255); padding-bottom: 56.111%;
                }

                WebElement introCard = driver.getDriver().findElement(By.id("profile_intro_card"));
                List<WebElement> photoViewers = introCard.findElements(By.xpath(".//a[contains(@href, '/photos/viewer/')]"));
                for(WebElement photoViewer:photoViewers){
                    String imgSource = photoViewer.findElement(By.xpath(".//i[contains(@data-store, 'imgsrc')]")).getAttribute("data-store");

                    //TODO xu ly: {"imgsrc":"https:\\/\\/scontent.fhan5-4.fna.fbcdn.net\\/v\\/t1.0-0\\/cp0\\/e15\\/q65\\/c0.8.526.348\\/p526x296\\/23316472_1472517836201573_4905425769037447739_n.jpg?_nc_cat=104&efg=eyJpIjoidCJ9&_nc_ht=scontent.fhan5-4.fna&oh=bbe0d0c0cd71ccf24424ebd878a619e3&oe=5C8189E7"}
                }

                WebElement photoOfAndBys = driver.getDriver().findElement(By.xpath(".//div[contains(@data-store, 'photos_of_and_by')]"));
                List<WebElement> imgElements = introCard.findElements(By.xpath(".//i[contains(@style, 'background-image: url')]"));
                for(WebElement imgElement:imgElements){
                    String imgSource = imgElement.getAttribute("style");

                    //TODO xu ly background-image: url("https://scontent.fhan5-6.fna.fbcdn.net/v/t1.0-0/cp0/e15/q65/p180x540/19510350_1088638267935595_1540347797861592945_n.jpg?_nc_cat=107&efg=eyJpIjoidCJ9&_nc_ht=scontent.fhan5-6.fna&oh=71e0e77bf5c113366f967c517cbb5262&oe=5C562939"); background-repeat: no-repeat; background-size: 100% 100%; width: 178px; height: 178px;
                }

            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void desktopFriendImageBackup(int maxFriendsCrawl, String fbUid, String mainId, long timeDelay) {
        Actions ac = new Actions(driver.getDriver());
        try {
            driver.goToPage("https://www.facebook.com/" + fbUid);
            _waitForPageLoaded();
            WebElement friendTabElement = driver.getDriver().findElement(By.xpath(".//a[@data-tab-key='friends']"));
            friendTabElement.click();
            _waitForPageLoaded();
            List<WebElement> friend_list_items = driver.getDriver().findElements(By.xpath(".//div[@data-testid='friend_list_item']"));
            int loopCount = 0;
            List<String> friendLinkList = new ArrayList<>();

            while (friend_list_items.size() < maxFriendsCrawl) {
                _scrollDown();
                friend_list_items = driver.getDriver().findElements(By.xpath(".//div[@data-testid='friend_list_item']"));
                loopCount = loopCount + 1;
                if (loopCount > 30) {
                    break;
                }
            }

            for (WebElement friend_list_item : friend_list_items) {
                BackupImageDTO backupImageDTO = new BackupImageDTO();
                backupImageDTO.setMainId(mainId);
                List<String> imgSrcList = new ArrayList<>();
                List<WebElement> friendHrefCheck = friend_list_item.findElements(By.xpath(".//a[contains(@href, 'hc_location=friends_tab')]"));
                if (!friendHrefCheck.isEmpty()) {
                    for (WebElement _friendHrefCheck : friendHrefCheck) {
                        String friendName = _friendHrefCheck.getText();
                        if (friendName.length() > 3) {
                            backupImageDTO.setFriendName(friendName);
                            backupImageDTO.setFriendProfileLink(_friendHrefCheck.getAttribute("href"));
                        }
                    }
                    friendLinkList.add(friendHrefCheck.get(0).getAttribute("href"));
                    _scrollElementToMiddle(friendHrefCheck.get(0));
                    ac.moveToElement(friendHrefCheck.get(0)).keyDown(Keys.LEFT_CONTROL).click().keyUp(Keys.LEFT_CONTROL).build().perform();
                    Thread.sleep(3000);
                    ArrayList<String> tabs = new ArrayList<String>(driver.getDriver().getWindowHandles());
                    driver.getDriver().switchTo().window(tabs.get(tabs.size() - 1));

                    List<WebElement> coverPhotoImgs = driver.getDriver().findElements(By.className("coverPhotoImg"));
                    if (!coverPhotoImgs.isEmpty()) {
                        //TODO LINK ẢNH COVER
                        String coverImgSrc = coverPhotoImgs.get(0).getAttribute("src");
                        imgSrcList.add(coverImgSrc);
                    }
                    List<WebElement> profilePicThumbs = driver.getDriver().findElements(By.className("profilePicThumb"));
                    if (!profilePicThumbs.isEmpty()) {
                        List<WebElement> profilePicThumbSrcs = profilePicThumbs.get(0).findElements(By.xpath(".//img[@src]"));
                        if (!profilePicThumbSrcs.isEmpty()) {
                            //todo link anh dai dien
                            String profilePicSrc = profilePicThumbSrcs.get(0).getAttribute("src");
                            imgSrcList.add(profilePicSrc);
                        }
                    }
                    List<WebElement> introContainerIds = driver.getDriver().findElements(By.id("intro_container_id"));
                    if (!introContainerIds.isEmpty()) {
                        List<WebElement> featureImgs = introContainerIds.get(0).findElements(By.xpath(".//img[@src]"));
                        for (WebElement featureImg : featureImgs) {
                            //ảnh feature
                            imgSrcList.add(featureImg.getAttribute("src"));
                        }
                    }
                    List<WebElement> profileTimelineTilesUnitPageletsPhotos = driver.getDriver().findElements(By.id("profile_timeline_tiles_unit_pagelets_photos"));
                    if (!profileTimelineTilesUnitPageletsPhotos.isEmpty()) {
                        List<WebElement> subImgs = profileTimelineTilesUnitPageletsPhotos.get(0).findElements(By.xpath(".//img[@src]"));
                        for (WebElement subImg : subImgs) {
                            //todo ảnh nho
                            imgSrcList.add(subImg.getAttribute("src"));
                        }
                    }

                    backupImageDTO.setUrls(imgSrcList);
                    //todo gui ve sv
                    ServerService.sendDTOToServer(SERVER_BACKUP_IMAGE, backupImageDTO);

                    driver.getDriver().close();
                    driver.getDriver().switchTo().window(tabs.get(0));
                }

                Thread.sleep(timeDelay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void desktopFriendBackupFromMessenger(int maxFriendsCrawl, String fbUid){
        Actions ac = new Actions(driver.getDriver());
        List<String> doneList = new ArrayList<>();
        List<WebElement> prepareList = new ArrayList<>();
        try{
            //MESSENGER
            driver.goToPage("https://www.facebook.com/messages/t");
            _waitForPageLoaded();
            List<WebElement> uiScrollableAreaWraps = driver.getDriver().findElements(By.className("uiScrollableAreaWrap"));
            if(!uiScrollableAreaWraps.isEmpty()){
                List<WebElement> messSections = uiScrollableAreaWraps.get(0).findElements(By.xpath(".//a[contains(@data-href, 'https://www.facebook.com/messages/t/')]"));
                if(!messSections.isEmpty()){
                    ac.moveToElement(messSections.get(0)).click().build().perform();
                    int count = 0;
                    while(messSections.size()<maxFriendsCrawl && count <30){
                        ac.keyDown(Keys.ARROW_DOWN).keyUp(Keys.ARROW_DOWN).build().perform();
                        Thread.sleep(1000);
                        count = count +1;
                        messSections = uiScrollableAreaWraps.get(0).findElements(By.xpath(".//a[contains(@data-href, 'https://www.facebook.com/messages/t/')]"));
                    }
                    for(WebElement messSection: messSections){
                        ac.moveToElement(messSection).click().build().perform();
                        List<WebElement> uidLinks = driver.getDriver().findElements(By.xpath(".//a[contains(@data-hovercard, '/ajax/hovercard/user.php?id=')]"));
                        if(!uidLinks.isEmpty()){
                            //todo friend name profileLinks.get(0).getText();
                            ac.moveToElement(uidLinks.get(0)).keyDown(Keys.LEFT_CONTROL).click().keyUp(Keys.LEFT_CONTROL).build().perform();
                            // todo lay anh
                            //todo add vao list da lay
                        }
                    }
                }
            }

            if(doneList.size() < maxFriendsCrawl){
                prepareList = new ArrayList<>();
                driver.goToPage("https://www.facebook.com/"+fbUid+"/allactivity");
                List<WebElement> uidLinks = driver.getDriver().findElements(By.xpath(".//a[@data-click='profile_icon']"));
                String ownProfileLink = fbUid;
                if(!uidLinks.isEmpty()){
                    if(!uidLinks.get(0).findElements(By.xpath(".//a[contains(@href, 'https://www.facebook.com/')]")).isEmpty()){
                        ownProfileLink = uidLinks.get(0).findElement(By.xpath(".//a[contains(@href, 'https://www.facebook.com/')]")).getAttribute("href");
                    }
                }
                List<WebElement> profileLinks = driver.getDriver().findElements(By.className("profileLink"));
                for(WebElement profileLink: profileLinks){
                    if(doneList.size() + prepareList.size() >maxFriendsCrawl){
                        break;
                    }
                    if(!profileLink.getAttribute("href").contains(ownProfileLink) && prepareList.indexOf(profileLink) == -1){
                        for(Object doneObj: doneList){
                            if(!doneObj.getName.equal(profileLink.getText())){
                                prepareList.add(profileLink);
                            }
                        }
                    }
                }
                for (WebElement profileLink: prepareList){
                    ac.moveToElement(profileLink).keyDown(Keys.LEFT_CONTROL).click().keyUp(Keys.LEFT_CONTROL).build().perform();
                    //todo lay anh
                    //todo add vao doneList da lay
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
