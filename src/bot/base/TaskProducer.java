/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bot.base;

import action.execution.Executor;
import action.handler.AccountHandler;
import object.dto.SigmaCommandDTO;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import object.dto.SingleCommandDTO;
import org.apache.commons.io.FileUtils;
import service.BaseService;

import static bot.main.FacesBOT.canIncreaseThread;
import static util.common.Constant.COMMON.*;
import static util.common.Constant.SERVER.*;

import util.common.DataUtil;

/**
 * @author Thai Tuan Anh
 */
public class TaskProducer extends Thread {

    private static final long PRODUCE_SLEEP_TIME = 10000;
    private BlockingQueue<Session> runningQueue;
    private int actionType;

    public TaskProducer(BlockingQueue<Session> runningQueue, int actionType) {
        this.runningQueue = runningQueue;
        this.actionType = actionType;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (canIncreaseThread()) {
                    Session session = createSession(runningQueue);

                    if (session != null) {
                        if (session.getSigma().getAccount().getAccountId() != 0) {
                            runningQueue.put(session);
                            session.start();

                            System.out.println("putting, queue size: " + runningQueue.size());
                        } else {
                            System.out.println("Account type " + actionType + " out of stock!");
                        }
                    }
                }

                Thread.sleep(PRODUCE_SLEEP_TIME);
            } catch (Exception ex) {
                try {
                    Logger.getLogger(TaskProducer.class.getName()).log(Level.SEVERE, null, ex);
                    Thread.sleep(PRODUCE_SLEEP_TIME);
                } catch (InterruptedException ex1) {
                    Logger.getLogger(TaskProducer.class.getName()).log(Level.SEVERE, null, ex1);
                }
            }
        }
    }

    private Session createSession(BlockingQueue<Session> runningQueue) {
        try {
            BaseService ws = new BaseService();
            SigmaCommandDTO sigmaCommand = null;

            if (actionType == ACTION_TYPE_NORMAL) {
                sigmaCommand = (SigmaCommandDTO) DataUtil.jsonStringToObject(ws.sendGetRequest(SERVER_COMMAND_URL).getMessage(), SigmaCommandDTO.class);
            } else if (actionType == ACTION_TYPE_BUFF) {
                sigmaCommand = (SigmaCommandDTO) DataUtil.jsonStringToObject(ws.sendGetRequest(SERVER_COMMAND_BUFF_URL).getMessage(), SigmaCommandDTO.class);
            } else if (actionType == ACTION_TYPE_TYM) {
                sigmaCommand = (SigmaCommandDTO) DataUtil.jsonStringToObject(ws.sendGetRequest(SERVER_COMMAND_TYM_URL).getMessage(), SigmaCommandDTO.class);
            } else if (actionType == ACTION_TYPE_LISTENER) {
                sigmaCommand = (SigmaCommandDTO) DataUtil.jsonStringToObject(ws.sendGetRequest(SERVER_COMMAND_LISTENER_URL).getMessage(), SigmaCommandDTO.class);
            } else if (actionType == ACTION_TYPE_TYM_TEST) {
                sigmaCommand = (SigmaCommandDTO) DataUtil.jsonStringToObject(ws.sendGetRequest(SERVER_COMMAND_TYM_TEST_URL).getMessage(), SigmaCommandDTO.class);
            }

            Session session = new Session(runningQueue, actionType);
            session.setSigma(sigmaCommand);
            return session;
        } catch (Exception e) {
            System.out.println("server offline");
        }

        return null;
    }

    public class Session extends Thread implements Runnable {

        SigmaCommandDTO sigma;

        private BlockingQueue<Session> runningQueue;
        private int actionType;

        public Session(BlockingQueue<Session> runningQueue, int actionType) {
            this.runningQueue = runningQueue;
            this.actionType = actionType;
        }

        @Override
        public void run() {
            Driver driver = new Driver(sigma);
            try {
                if (!DataUtil.isNullOrEmpty(sigma.getCommands())) {
                    System.out.println("running account: " + sigma.getAccount().getUid());

                    Executor execution = new Executor(driver);
                    if (!checkCommandContain(sigma.getCommands(), 501)) {
                        if (sigma.getAccount().getProxyId() == 0) {
                            //crawl from website
                            driver.websiteCrawlerDriver();
                            //TODO execution.;
                        } else {
                            if(checkCommandContain(sigma.getCommands(), 2991)){
                                driver.mobileDriver();
                                AccountHandler accountHandler = new AccountHandler(driver);
                                String loginResult = accountHandler.mobileLogInStatus();
                                if (loginResult.contains("loggedIn")) {
                                    if(loginResult.contains("modern")){
                                        execution.modernMobileAutoTym(2991);
                                    } else if(loginResult.contains("old")){
                                        execution.oldMobileAutoTym(2991);
                                    } else {
                                        execution.reportErrorLoggin(loginResult);
                                    }
                                } else {
                                    if ((actionType == ACTION_TYPE_NORMAL || actionType == ACTION_TYPE_TYM) && ("loggedOut".equals(loginResult) || "wrongPwd".equals(loginResult))) {
                                        execution.reportErrorLoggin(loginResult);
                                    }
                                }
                            } else {
                                driver.actionDriver();
                                AccountHandler accountHandler = new AccountHandler(driver);
                                String loginResult = accountHandler.logInStatus();
                                if (loginResult.contains("loggedIn")) {
                                    for (SingleCommandDTO command : sigma.getCommands()) {
                                        execution.execute(command.getActionCode());
                                    }
                                } else {
                                    if ((actionType == ACTION_TYPE_NORMAL || actionType == ACTION_TYPE_TYM) && ("loggedOut".equals(loginResult) || "wrongPwd".equals(loginResult))) {
                                        execution.reportErrorLoggin(loginResult);
                                    }
                                }
                            }
                        }
                    } else {
                        driver.watchLiveStreamDriver();
                        AccountHandler accountHandler = new AccountHandler(driver);
                        String loginResult = accountHandler.logInStatus();
                        if (loginResult.contains("loggedIn")) {
                            execution.execute(501);
                        } else {
                            if ((actionType == ACTION_TYPE_NORMAL || actionType == ACTION_TYPE_TYM) && ("loggedOut".equals(loginResult) || "wrongPwd".equals(loginResult))) {
                                execution.reportErrorLoggin(loginResult);
                            }
                        }
                    }
                } else {
                    System.out.println("skip by actionCodes null");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("unexpect error, exitting driver");
            } finally {
                runningQueue.remove(this);

                if (driver.getDriver() != null) {
                    driver.getDriver().quit();
                }

                try {
                    if (!DataUtil.isNullOrEmptyStr(driver.getExtFolder())) {
                        FileUtils.deleteDirectory(new File(driver.getExtFolder()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("removing " + driver.getSigma().getAccount().getAccountId() + ", queue size: " + runningQueue.size());
            }
        }

        public SigmaCommandDTO getSigma() {
            return sigma;
        }

        public void setSigma(SigmaCommandDTO sigma) {
            this.sigma = sigma;
        }
    }

    private boolean checkCommandContain(List<SingleCommandDTO> commands, int code) {
        for (SingleCommandDTO command : commands) {
            if (command.getActionCode() == code) {
                return true;
            }
        }

        return false;
    }

    public BlockingQueue<Session> getRunningQueue() {
        return runningQueue;
    }

    public void setRunningQueue(BlockingQueue<Session> runningQueue) {
        this.runningQueue = runningQueue;
    }
}
