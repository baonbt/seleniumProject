package object.dto;


public class MessageDTO extends BaseDTO {

    String replyContent;
    String receiver;

    public String getReplyContent() {
        return replyContent;
    }

    public void setReplyContent(String replyContent) {
        this.replyContent = replyContent;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String reveiver) {
        this.receiver = reveiver;
    }

}
