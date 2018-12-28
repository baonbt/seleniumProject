package object.dto;

public class ListenerReportCTSDTO extends BaseDTO {
    String postLink;
    String hashTag;
    String profileLink;
    /**
     * 0 = normal
     * 1 = live stream
     */
    int postType;
    String content;

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public String getPostLink() {
        return postLink;
    }

    public String getHashTag() {
        return hashTag;
    }

    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public void setPostType(int postType) {
        this.postType = postType;
    }

    public int getPostType() {
        return postType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
