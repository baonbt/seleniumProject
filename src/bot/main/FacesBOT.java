/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bot.main;

import action.handler.BaseAction;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import bot.base.ResourceCleaner;
import bot.base.ResourceReportThread;
import bot.base.TaskProducer;
import service.ServerService;

import static util.common.Constant.COMMON.*;
import static util.common.Constant.RESOURCE.RESOURCE_AVAILABLE;
import static util.common.Constant.SERVER.RESOURCE_REPORT;

/**
 * @author Thai Tuan Anh
 */
public class FacesBOT {

    //    private static final String MODE = MODE_NORMAL;
//    private static final String MODE = MODE_BUFF;
//    private static final String MODE = MODE_TYM;
    private static final String MODE = MODE_TYM_TEST;
//    private static final String MODE = MODE_LISTENER;

    public static void main(String[] args) {

        new BaseAction().setClipboardReady();

        BlockingQueue<TaskProducer.Session> runningQueue = new ArrayBlockingQueue<>(INIT_QUEUE_SIZE);
        TaskProducer producer;

        if (MODE_NORMAL.equals(MODE)) {
            producer = new TaskProducer(runningQueue, ACTION_TYPE_NORMAL);
        } else if (MODE_BUFF.equals(MODE)) {
            producer = new TaskProducer(runningQueue, ACTION_TYPE_BUFF);
        } else if (MODE_TYM.equals(MODE)) {
            producer = new TaskProducer(runningQueue, ACTION_TYPE_TYM);
        } else if (MODE_TYM_TEST.equals(MODE)) {
            producer = new TaskProducer(runningQueue, ACTION_TYPE_TYM_TEST);
        } else if (MODE_LISTENER.equals(MODE)) {
            producer = new TaskProducer(runningQueue, ACTION_TYPE_LISTENER);
        } else {
            producer = new TaskProducer(runningQueue, ACTION_TYPE_NORMAL);
        }

        //start producer thread
        producer.start();

        //start cleaner thread
        new ResourceCleaner(runningQueue).start();

        //start resource reporting thread
        ResourceReportThread resourceReportThread = new ResourceReportThread();
        resourceReportThread.start();
    }

    public static boolean canIncreaseThread() {
        String result = ServerService.sendToServer(RESOURCE_REPORT);

        if (result.equals(RESOURCE_AVAILABLE)) {
            return true;
        }

        return false;
    }
}
