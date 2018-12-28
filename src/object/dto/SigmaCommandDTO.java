/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package object.dto;

import java.util.List;
import object.common.Account;

/**
 *
 * @author Thai Tuan Anh
 */
public class SigmaCommandDTO {

    Account account;
    List<SingleCommandDTO> commands;
    String data;
    String actionResultUrl;
    String actionResult2Url;
    String baseLogUrl;
    String breakLoopUrl;
    String loopReportUrl;
    String offlineUrl;
    String buffReportUrl;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getActionResultUrl() {
        return actionResultUrl;
    }

    public void setActionResultUrl(String actionResultUrl) {
        this.actionResultUrl = actionResultUrl;
    }

    public String getBaseLogUrl() {
        return baseLogUrl;
    }

    public void setBaseLogUrl(String baseLogUrl) {
        this.baseLogUrl = baseLogUrl;
    }

    public String getBreakLoopUrl() {
        return breakLoopUrl;
    }

    public void setBreakLoopUrl(String breakLoopUrl) {
        this.breakLoopUrl = breakLoopUrl;
    }

    public String getOfflineUrl() {
        return offlineUrl;
    }

    public void setOfflineUrl(String offlineUrl) {
        this.offlineUrl = offlineUrl;
    }

    public String getLoopReportUrl() {
        return loopReportUrl;
    }

    public void setLoopReportUrl(String loopReportUrl) {
        this.loopReportUrl = loopReportUrl;
    }

    public List<SingleCommandDTO> getCommands() {
        return commands;
    }

    public void setCommands(List<SingleCommandDTO> commands) {
        this.commands = commands;
    }

    public String getActionResult2Url() {
        return actionResult2Url;
    }

    public void setActionResult2Url(String actionResult2Url) {
        this.actionResult2Url = actionResult2Url;
    }

    public String getBuffReportUrl() {
        return buffReportUrl;
    }

    public void setBuffReportUrl(String buffReportUrl) {
        this.buffReportUrl = buffReportUrl;
    }
}
