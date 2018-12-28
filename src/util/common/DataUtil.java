/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.common;

import bot.base.TaskProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

/**
 * @author Thai Tuan Anh
 */
public class DataUtil {

    public boolean isNullOrEmpty(Object object) {
        return false;
    }

    public List<Object> convertArrayToList(Object[] objects) {
        return null;
    }

    public static String objectToJsonString(Object obj) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.writeValueAsString(obj);
    }

    public static Object jsonStringToObject(String json, Class c) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json, c);
    }

    public static int random(int from, int to) {
        if (from == to) {
            return from;
        } else {
            --from;
            Random rand = new Random();
            int n = rand.nextInt(to - from) + 1;
            return from + n;
        }
    }

    public static int randomByPercent(String input) {

        List<Integer> list = new ArrayList<>();

        String[] items = input.split(";");
        for (String item : items) {
            String numStr = item.split("=")[0];
            String percentStr = item.split("=")[1];

            int num = Integer.valueOf(numStr);
            int percent = Integer.valueOf(percentStr);

            for (int i = 0; i < percent; i++) {
                list.add(num);
            }
        }
        return list.get(randomInRange(0, list.size() - 1));
    }

    public static int randomInRange(int min, int max) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static String generateUniqueRandomString() {
        return java.util.UUID.randomUUID().toString();
    }

    public static boolean isNullOrEmpty(List list) {
        if (list != null && list.size() > 0) {
            return false;
        }

        return true;
    }

    public static boolean isNullOrEmptyStr(String str) {
        if (str != null && str.length() > 0) {
            return false;
        }

        return true;
    }

    private static String getUidFromProfileLink(String profileLink) throws URISyntaxException {
        URI uri = new URI(profileLink);

        if (!profileLink.contains("profile.php")) {
            String[] segments = uri.getPath().split("/");

            int i = 0;
            for (String segment : segments) {
                if (segment.contains("facebook.com")) {
                    i++;
                    break;
                }
            }

            return segments[i + 1];
        } else {
            List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");

            for (NameValuePair param : params) {
                if ("id".equals(param.getName())) {
                    return param.getValue();
                }
            }

            return "";
        }
    }

    public static BlockingQueue increaseQueueSize(BlockingQueue<TaskProducer.Session> queue, int size) {
        if (queue.size() < size) {
            BlockingQueue<TaskProducer.Session> result = new ArrayBlockingQueue<>(size);

            queue.forEach(session -> {
                try {
                    result.put(session);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });

            return result;
        } else {
            return queue;
        }
    }
}
