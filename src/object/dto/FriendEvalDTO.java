package object.dto;

public class FriendEvalDTO extends BaseDTO {

    Long friendEvalId;
    String relationship;
    Integer follow;
    Integer friend;
    Integer posts;
    String postsTime;
    Integer likes;
    Integer comments;
    String lifeStory;
    String caption;
    Integer checkin;
    Integer photos;

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getRelationship() {
        return relationship;
    }

    public Integer getFollow() {
        return follow;
    }

    public void setFollow(Integer follow) {
        this.follow = follow;
    }

    public Integer getFriend() {
        return friend;
    }

    public void setFriend(Integer friend) {
        this.friend = friend;
    }

    public Integer getPosts() {
        return posts;
    }

    public void setPosts(Integer posts) {
        this.posts = posts;
    }

    public String getPostsTime() {
        return postsTime;
    }

    public void setPostsTime(String postsTime) {
        this.postsTime = postsTime;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getComments() {
        return comments;
    }

    public void setComments(Integer comments) {
        this.comments = comments;
    }

    public String getLifeStory() {
        return lifeStory;
    }

    public void setLifeStory(String lifeStory) {
        this.lifeStory = lifeStory;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Integer getCheckin() {
        return checkin;
    }

    public void setCheckin(Integer checkin) {
        this.checkin = checkin;
    }

    public Integer getPhotos() {
        return photos;
    }

    public void setPhotos(Integer photos) {
        this.photos = photos;
    }

    public Long getFriendEvalId() {
        return friendEvalId;
    }

    public void setFriendEvalId(Long friendEvalId) {
        this.friendEvalId = friendEvalId;
    }
}
