package object.dto;

public class PostBoosterSTCDTO {
    /**
     * "na" = không like
     */
    String reactionRate;
    /**
     * "na" = không cmt vào post
     * "CHATBOT" = tl tự động bằng chatbot
     * "STICKER" = tl bằng random sticker
     * "CHATBOT_REPLY_COMMENT" = tl comment tự động bằng chatbot
     */
    String replyContent;

    String postLink;
    Long buffScheduleId;

    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    public String getPostLink() {
        return postLink;
    }

    public void setReactionRate(String reactionRate) {
        this.reactionRate = reactionRate;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getReactionRate() {
        return reactionRate;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public Long getBuffScheduleId() {
        return buffScheduleId;
    }

    public void setBuffScheduleId(Long buffScheduleId) {
        this.buffScheduleId = buffScheduleId;
    }
}
