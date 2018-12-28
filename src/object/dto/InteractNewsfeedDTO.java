package object.dto;

public class InteractNewsfeedDTO {
    int interactTimes;
    String likeRate;
    String commentRate;
    boolean interactProfileOnly;
    String interactId;
    boolean newsfeedOnly;
    int replyInboxRate;
    String sessionId;
    int timeDelay;

    public int getInteractTimes() {
        return interactTimes;
    }

    public void setInteractTimes(int interactTimes) {
        this.interactTimes = interactTimes;
    }

    public String getLikeRate() {
        return likeRate;
    }

    public void setLikeRate(String likeRate) {
        this.likeRate = likeRate;
    }

    public String getCommentRate() {
        return commentRate;
    }

    public void setCommentRate(String commentRate) {
        this.commentRate = commentRate;
    }

    public boolean isInteractProfileOnly() {
        return interactProfileOnly;
    }

    public void setInteractProfileOnly(boolean interactProfileOnly) {
        this.interactProfileOnly = interactProfileOnly;
    }

    public String getInteractId() {
        return interactId;
    }

    public void setInteractId(String interactId) {
        this.interactId = interactId;
    }

    public boolean isNewsfeedOnly() {
        return newsfeedOnly;
    }

    public void setNewsfeedOnly(boolean newsfeedOnly) {
        this.newsfeedOnly = newsfeedOnly;
    }

    public int getReplyInboxRate() {
        return replyInboxRate;
    }

    public void setReplyInboxRate(int replyInboxRate) {
        this.replyInboxRate = replyInboxRate;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    public int getTimeDelay() {
        return timeDelay;
    }

    public void setTimeDelay(int timeDelay) {
        this.timeDelay = timeDelay;
    }

}
