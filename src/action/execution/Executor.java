/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package action.execution;

import action.handler.AccountHandler;
import action.handler.BoostHandler;
import action.handler.CustomerHandler;
import action.handler.InteractionHandler;
import bot.base.Driver;

import com.fasterxml.jackson.core.JsonProcessingException;
import object.dto.*;
import object.log.BaseLog;
import object.log.ConnectionLog;
import service.BaseService;
import service.ServerService;
import util.common.Constant;

import static util.common.Constant.ACTION.*;
import static util.common.Constant.SERVER.*;

import util.common.DataUtil;

/**
 * @author baonb
 */
public class Executor {

    //    SigmaCommandDTO sigma;
    Driver driver;

    //base class
    public Executor(Driver driver) {
        this.driver = driver;
    }

    public void execute(Integer code) {
        switch (code) {
            case WATCH_LIVE_STREAM:
                liveStreamWatcher(code);
                break;
            case BOOST_POST:
                boostPost(code);
                break;
            case AUTOTYM:
                autoTym(code);
                break;
            case INTERTACTION:
                interaction(code);
                break;
            case CUSTOMER_FOLLOW:
                followCustomer(code);
                break;
            case CUSTOMER_SEE_FIRST:
                followAndSeeFirstCustomer(code);
                break;
            case CUSTOMER_UN_FOLLOW:
                unFollowCustomer(code);
                break;
            case CUSTOMER_UN_SEE_FIRST:
                unSeeFirstCustomer(code);
                break;
            case POST_AND_LIVE_STREAM_LISTENER:
                postAndLiveStreamListener(code);
                break;
            case INITIAL_SETUP:
                initialSetup(code);
                break;
            case UPDATE_COOKIES:
                updateCookies();
                break;
        }
    }

    public void modernMobileAutoTym(int actionCode) {
        BaseLog baseLog = new BaseLog();
        InteractionHandler interactionHandler = new InteractionHandler(driver);
        try {
            InteractNewsfeedDTO interactNewsfeedDTO = (InteractNewsfeedDTO) DataUtil.jsonStringToObject(driver.getSigma().getData(), InteractNewsfeedDTO.class);

            baseLog.setAccountId(driver.getSigma().getAccount().getAccountId());
            baseLog.setActionCode(actionCode);

            interactionHandler.modernMobileAutoTym(
                    interactNewsfeedDTO.getSessionId(),
                    interactNewsfeedDTO.getInteractTimes(),
                    interactNewsfeedDTO.getLikeRate(),
                    interactNewsfeedDTO.getCommentRate(),
                    interactNewsfeedDTO.isInteractProfileOnly(),
                    interactNewsfeedDTO.getInteractId(),
                    interactNewsfeedDTO.isNewsfeedOnly(),
                    interactNewsfeedDTO.getReplyInboxRate(),
                    0,
                    interactNewsfeedDTO.getTimeDelay());
        } catch (Exception e) {
            e.printStackTrace();
            baseLog.setErrorMessage(e.getStackTrace().toString());
        } finally {
            sendLogToServer(driver.getSigma().getBaseLogUrl(), baseLog);
        }
    }
    public void oldMobileAutoTym(int actionCode) {
        BaseLog baseLog = new BaseLog();
        InteractionHandler interactionHandler = new InteractionHandler(driver);
        try {
            InteractNewsfeedDTO interactNewsfeedDTO = (InteractNewsfeedDTO) DataUtil.jsonStringToObject(driver.getSigma().getData(), InteractNewsfeedDTO.class);

            baseLog.setAccountId(driver.getSigma().getAccount().getAccountId());
            baseLog.setActionCode(actionCode);

            interactionHandler.oldMobileAutoTym(
                    interactNewsfeedDTO.getSessionId(),
                    interactNewsfeedDTO.getInteractTimes(),
                    interactNewsfeedDTO.getLikeRate(),
                    interactNewsfeedDTO.getCommentRate(),
                    interactNewsfeedDTO.isInteractProfileOnly(),
                    interactNewsfeedDTO.getInteractId(),
                    interactNewsfeedDTO.isNewsfeedOnly(),
                    interactNewsfeedDTO.getReplyInboxRate(),
                    0,
                    interactNewsfeedDTO.getTimeDelay());
        } catch (Exception e) {
            e.printStackTrace();
            baseLog.setErrorMessage(e.getStackTrace().toString());
        } finally {
            sendLogToServer(driver.getSigma().getBaseLogUrl(), baseLog);
        }
    }

    private void followCustomer(int actionCode) {
        BaseLog baseLog = new BaseLog();
        CustomerHandler customerHandler = new CustomerHandler(driver);
        try {
            baseLog.setAccountId(driver.getSigma().getAccount().getAccountId());
            baseLog.setActionCode(actionCode);
//            baseLog.setActionMessage(customerHandler.follow(customerId));
        } catch (Exception e) {
            baseLog.setErrorMessage(e.getStackTrace().toString());
        } finally {
//            sendLogToServer(BASELOG_FOLLOW_CUSTOMER, baseLog);
        }
    }

    private void unFollowCustomer(int actionCode) {
        BaseLog baseLog = new BaseLog();
        CustomerHandler customerHandler = new CustomerHandler(driver);
        try {
            baseLog.setAccountId(driver.getSigma().getAccount().getAccountId());
            baseLog.setActionCode(actionCode);
//            baseLog.setActionMessage(customerHandler.unfollow(customerId));
        } catch (Exception e) {
            baseLog.setErrorMessage(e.getStackTrace().toString());
        } finally {
//            sendLogToServer(BASELOG_UN_FOLLOW_CUSTOMER, baseLog);
        }
    }

    private void followAndSeeFirstCustomer(int actionCode) {
        BaseLog baseLog = new BaseLog();
        SetupListenerResultDTO resultDTO = new SetupListenerResultDTO();
        CustomerHandler customerHandler = new CustomerHandler(driver);

        try {
            FollowAndSeeFirstDTO infoDTO = (FollowAndSeeFirstDTO) DataUtil.jsonStringToObject(driver.getSigma().getData(), FollowAndSeeFirstDTO.class);

            baseLog.setAccountId(driver.getSigma().getAccount().getAccountId());
            baseLog.setActionCode(actionCode);
            baseLog.setActionMessage(customerHandler.followAndSeeFirst(infoDTO.getCustomerId()));

            resultDTO.setTargetAccountId(infoDTO.getTargetAccountId());
            resultDTO.setResult(baseLog.getActionMessage());
        } catch (Exception e) {
            baseLog.setErrorMessage(e.getStackTrace().toString());
            resultDTO.setResult("error");
        } finally {
            ServerService.sendDTOToServer(driver.getSigma().getActionResultUrl(), resultDTO);
            sendLogToServer(driver.getSigma().getBaseLogUrl(), baseLog);
        }
    }

    private void unSeeFirstCustomer(int actionCode) {
        BaseLog baseLog = new BaseLog();
        CustomerHandler customerHandler = new CustomerHandler(driver);
        try {
            baseLog.setAccountId(driver.getSigma().getAccount().getAccountId());
            baseLog.setActionCode(actionCode);
//            baseLog.setActionMessage(customerHandler.unSeeFirst(customerId));
        } catch (Exception e) {
            baseLog.setErrorMessage(e.getStackTrace().toString());
        } finally {
//            sendLogToServer(BASELOG_UN_SEE_FIRST_CUSTOMER, baseLog);
        }
    }

    private void postAndLiveStreamListener(int actionCode) {
        BaseLog baseLog = new BaseLog();
        BoostHandler boostHandler = new BoostHandler(driver);
        try {
            CustomerInfoDTO customerInfoDTO = (CustomerInfoDTO) DataUtil.jsonStringToObject(driver.getSigma().getData(), CustomerInfoDTO.class);

            baseLog.setAccountId(driver.getSigma().getAccount().getAccountId());
            baseLog.setActionCode(actionCode);
            for (String uid : customerInfoDTO.getCustomerIds()) {
                boostHandler.profileLookup(uid);
            }

            boostHandler.listenerExecutor(customerInfoDTO.getCustomerIds());
        } catch (Exception e) {
            baseLog.setErrorMessage(e.getStackTrace().toString());
        } finally {
            sendLogToServer(driver.getSigma().getBaseLogUrl(), baseLog);
        }
    }

    private void liveStreamWatcher(int actionCode) {
        BaseLog baseLog = new BaseLog();
        BoostHandler boostHandler = new BoostHandler(driver);
        try {
            baseLog.setAccountId(driver.getSigma().getAccount().getAccountId());
            baseLog.setActionCode(actionCode);
            boostHandler.liveStreamExecutor();
        } catch (Exception e) {
            baseLog.setErrorMessage(e.getStackTrace().toString());
        } finally {
            sendLogToServer(driver.getSigma().getBaseLogUrl(), baseLog);
        }
    }

    private void boostPost(int actionCode) {
        BaseLog baseLog = new BaseLog();
        BoostHandler boostHandler = new BoostHandler(driver);
        InteractionDTO interactionDTO = new InteractionDTO();
        try {
            PostBoosterSTCDTO postBoosterSTCDTO = (PostBoosterSTCDTO) DataUtil.jsonStringToObject(driver.getSigma().getData(), PostBoosterSTCDTO.class);

            baseLog.setAccountId(driver.getSigma().getAccount().getAccountId());
            baseLog.setActionCode(actionCode);

            interactionDTO = boostHandler.boostAPost(postBoosterSTCDTO);
            interactionDTO.setBuffScheduleId(postBoosterSTCDTO.getBuffScheduleId());
            interactionDTO.setAccountId(driver.getSigma().getAccount().getAccountId());

        } catch (Exception e) {
            baseLog.setErrorMessage(e.getStackTrace().toString());
        } finally {
            sendLogToServer(driver.getSigma().getBaseLogUrl(), baseLog);
            ServerService.sendDTOToServer(driver.getSigma().getActionResultUrl(), interactionDTO);
        }
    }

    private void initialSetup(int actionCode) {
        BaseLog baseLog = new BaseLog();
        AccountHandler accountHandler = new AccountHandler(driver);
        AccountInfoDTO accountInfoDTO = new AccountInfoDTO();
        FriendRawDTO friendRawDTO = new FriendRawDTO();
        try {
            baseLog.setAccountId(driver.getSigma().getAccount().getAccountId());
            accountInfoDTO.setAccountId(driver.getSigma().getAccount().getAccountId());
            friendRawDTO.setAccountId(driver.getSigma().getAccount().getAccountId());
            baseLog.setActionCode(actionCode);

            baseLog.setActionMessage(accountHandler.initialSetup(accountInfoDTO, friendRawDTO));
        } catch (Exception e) {
            e.printStackTrace();
            baseLog.setErrorMessage(e.getStackTrace().toString());
        } finally {
            sendLogToServer(driver.getSigma().getBaseLogUrl(), baseLog);
            ServerService.sendDTOToServer(driver.getSigma().getActionResultUrl(), accountInfoDTO);
            ServerService.sendDTOToServer(driver.getSigma().getActionResult2Url(), friendRawDTO);
        }
    }

    public void updateCookies() {
        try {
            BaseService baseService = new BaseService();

            LoggedAccountDTO loggedAccountDTO = new LoggedAccountDTO();
            loggedAccountDTO.setLoggedAccount(driver.getSigma().getAccount().getUid());
            loggedAccountDTO.setProxyId(driver.getSigma().getAccount().getProxyId());
            String cookie = driver.getCookies();
            loggedAccountDTO.setCookies(cookie);

            baseService.sendPostRequest(SERVER_ACCOUNT_REPORT_V2, DataUtil.objectToJsonString(loggedAccountDTO));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("ERROR UPDATE COOKIES");
        }
    }

    public void autoTym(int actionCode) {
        BaseLog baseLog = new BaseLog();
        InteractionHandler interactionHandler = new InteractionHandler(driver);
        try {
            InteractNewsfeedDTO interactNewsfeedDTO = (InteractNewsfeedDTO) DataUtil.jsonStringToObject(driver.getSigma().getData(), InteractNewsfeedDTO.class);

            baseLog.setAccountId(driver.getSigma().getAccount().getAccountId());
            baseLog.setActionCode(actionCode);

            interactionHandler.autoTym(
                    interactNewsfeedDTO.getSessionId(),
                    interactNewsfeedDTO.getInteractTimes(),
                    interactNewsfeedDTO.getLikeRate(),
                    interactNewsfeedDTO.getCommentRate(),
                    interactNewsfeedDTO.isInteractProfileOnly(),
                    interactNewsfeedDTO.getInteractId(),
                    0,
                    interactNewsfeedDTO.getTimeDelay());
        } catch (Exception e) {
            e.printStackTrace();
            baseLog.setErrorMessage(e.getStackTrace().toString());
        } finally {
            sendLogToServer(driver.getSigma().getBaseLogUrl(), baseLog);
        }
    }


    public void interaction(int actionCode) {
        BaseLog baseLog = new BaseLog();
        InteractionHandler interactionHandler = new InteractionHandler(driver);
        try {
            InteractNewsfeedDTO interactNewsfeedDTO = (InteractNewsfeedDTO) DataUtil.jsonStringToObject(driver.getSigma().getData(), InteractNewsfeedDTO.class);

            baseLog.setAccountId(driver.getSigma().getAccount().getAccountId());
            baseLog.setActionCode(actionCode);

            interactionHandler.interacting(
                    interactNewsfeedDTO.getSessionId(),
                    interactNewsfeedDTO.getInteractTimes(),
                    interactNewsfeedDTO.getLikeRate(),
                    interactNewsfeedDTO.getCommentRate(),
                    interactNewsfeedDTO.isInteractProfileOnly(),
                    interactNewsfeedDTO.getInteractId(),
                    interactNewsfeedDTO.isNewsfeedOnly(),
                    interactNewsfeedDTO.getReplyInboxRate(),
                    0,
                    interactNewsfeedDTO.getTimeDelay());
        } catch (Exception e) {
            e.printStackTrace();
            baseLog.setErrorMessage(e.getStackTrace().toString());
        } finally {
            sendLogToServer(driver.getSigma().getBaseLogUrl(), baseLog);
        }
    }

    public void reportErrorLoggin(String loginResult) {
        BaseLog baseLog = new BaseLog();
        try {
            baseLog.setAccountId(driver.getSigma().getAccount().getAccountId());
            baseLog.setErrorMessage(loginResult);
        } catch (Exception e) {
            baseLog.setErrorMessage(">>>>>>>>>>>>>>>>>>>" + loginResult + "<<<<<<<<<<<<<<<<"
                    + e.getMessage());
        } finally {
            sendLogToServer(SERVER_CHECKPOINT, baseLog);
        }
    }

    public String sendLogToServer(String serverUrl, BaseLog log) {
        try {
            BaseService ws = new BaseService();
            ws.sendPostRequest(serverUrl, DataUtil.objectToJsonString(log));
            return Constant.HTTP.ERROR_CODE_SUCCESS;
        } catch (Exception ex) {
            ex.printStackTrace();
            return Constant.HTTP.ERROR_CODE_FAIL;
        }
    }
}
