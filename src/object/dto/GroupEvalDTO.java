package object.dto;

public class GroupEvalDTO extends BaseDTO {

    String description;
    Integer postsToday;
    Integer posts30Day;
    Integer members30Day;
    Integer members;
    String created;
    Integer posts;
    Integer likes;
    Integer comments;

    String groupPrivacy;
    String joiningStatus;
    String evaluationPostLink;

    public void setEvaluationPostLink(String evaluationPostLink) {
        this.evaluationPostLink = evaluationPostLink;
    }

    public String getEvaluationPostLink() {
        return evaluationPostLink;
    }

    public void setMembers(Integer members) {
        this.members = members;
    }

    public Integer getMembers() {
        return members;
    }

    public String getJoiningStatus() {
        return joiningStatus;
    }

    public void setJoiningStatus(String joiningStatus) {
        this.joiningStatus = joiningStatus;
    }

    public String getGroupPrivacy() {
        return groupPrivacy;
    }

    public void setGroupPrivacy(String groupPrivacy) {
        this.groupPrivacy = groupPrivacy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPostsToday() {
        return postsToday;
    }

    public void setPostsToday(Integer postsToday) {
        this.postsToday = postsToday;
    }

    public Integer getPosts30Day() {
        return posts30Day;
    }

    public void setPosts30Day(Integer posts30Day) {
        this.posts30Day = posts30Day;
    }

    public Integer getMembers30Day() {
        return members30Day;
    }

    public void setMembers30Day(Integer members30Day) {
        this.members30Day = members30Day;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Integer getPosts() {
        return posts;
    }

    public void setPosts(Integer posts) {
        this.posts = posts;
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
}
