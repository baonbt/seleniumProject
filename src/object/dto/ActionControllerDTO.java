package object.dto;

public class ActionControllerDTO extends BaseDTO {
    int actionCode;
    int commandCode;
    String interactId;
    String sessionId;

    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }

    public void setCommandCode(int commandCode) {
        this.commandCode = commandCode;
    }

    public int getActionCode() {
        return actionCode;
    }

    public int getCommandCode() {
        return commandCode;
    }

    public String getInteractId() {
        return interactId;
    }

    public void setInteractId(String interactId) {
        this.interactId = interactId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
