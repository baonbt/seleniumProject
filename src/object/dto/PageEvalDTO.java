package object.dto;

public class PageEvalDTO extends BaseDTO {

    Integer likesPage;
    Integer followsPage;
    Integer posts;
    Integer likes;
    Integer comments;

    public Integer getLikesPage() {
        return likesPage;
    }

    public void setLikesPage(Integer likesPage) {
        this.likesPage = likesPage;
    }

    public Integer getFollowsPage() {
        return followsPage;
    }

    public void setFollowsPage(Integer followsPage) {
        this.followsPage = followsPage;
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
