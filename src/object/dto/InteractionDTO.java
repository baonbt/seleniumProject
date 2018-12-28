package object.dto;

public class InteractionDTO extends BaseDTO {

    String hostProfileLink;
    String hostName;
    String replyContent;
    int reaction;
    Long buffScheduleId;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getHostProfileLink() {
        return hostProfileLink;
    }

    public int getReaction() {
        return reaction;
    }

    public void setHostProfileLink(String hostProfileLink) {
        this.hostProfileLink = hostProfileLink;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }

    public Long getBuffScheduleId() {
        return buffScheduleId;
    }

    public void setBuffScheduleId(Long buffScheduleId) {
        this.buffScheduleId = buffScheduleId;
    }
}
