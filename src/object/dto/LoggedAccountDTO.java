package object.dto;

public class LoggedAccountDTO {
    String loggedAccount;
    String cookies;
    Long proxyId;

    public String getLoggedAccount() {
        return loggedAccount;
    }

    public void setLoggedAccount(String loggedAccount) {
        this.loggedAccount = loggedAccount;
    }

    public String getCookies() {
        return cookies;
    }

    public void setCookies(String cookies) {
        this.cookies = cookies;
    }


    public Long getProxyId() {
        return proxyId;
    }

    public void setProxyId(Long proxyId) {
        this.proxyId = proxyId;
    }
}
