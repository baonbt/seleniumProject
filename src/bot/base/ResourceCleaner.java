package bot.base;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static util.common.Constant.DRIVER.JAVA_TEMP_FOLDER;

public class ResourceCleaner extends Thread {

    private BlockingQueue<TaskProducer.Session> runningQueue;

    public ResourceCleaner(BlockingQueue<TaskProducer.Session> runningQueue) {
        this.runningQueue = runningQueue;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Date date = new Date();
                Calendar calendar = GregorianCalendar.getInstance();
                calendar.setTime(date);
                int hour = calendar.get(Calendar.HOUR);

                if (hour % 2 == 0) {
                    for (TaskProducer.Session session : runningQueue) {
                        if (session.isAlive()) {
                            runningQueue.remove(session);
                            session.stop();
                        }
                    }

                    runningQueue.clear();

                    //kill chrome process
                    Runtime rt = Runtime.getRuntime();
                    rt.exec("TASKKILL /IM chrome.exe /F");
                    rt.exec("TASKKILL /IM chromedriver.exe /F");

                    //clean java temp folder
                    Thread.sleep(10000);
                    try {
                        FileUtils.deleteDirectory(new File(JAVA_TEMP_FOLDER));
                    } catch (Exception e) {
                        System.out.println("Cannot delete java temp folder!");
                    }

                    Thread.sleep(3600000);
                }
            } catch (Exception e) {
            } finally {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
