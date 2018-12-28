package object.dto;

public class PostBoosterCTSDTO extends BaseDTO {
    String postLink;
    String hashTag;

    /*
    0 = comment
    1 = reply comment
     */
    int statement;

    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    public void setHashTag(String hashTag) {
        this.hashTag = hashTag;
    }

    public void setStatement(int statement) {
        this.statement = statement;
    }

    public String getPostLink() {
        return postLink;
    }

    public int getStatement() {
        return statement;
    }

    public String getHashTag() {
        return hashTag;
    }
}
