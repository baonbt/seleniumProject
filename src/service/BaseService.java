/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import object.log.BaseLog;
import object.log.ConnectionLog;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import static util.common.Constant.HTTP.ACCEPTED_LANGUAGE;
import static util.common.Constant.HTTP.CONTENT_TYPE;
import static util.common.Constant.HTTP.ERROR_CODE_SUCCESS;
import static util.common.Constant.HTTP.USER_AGENT;

/**
 * @author Thai Tuan Anh
 */
public class BaseService {

    public ConnectionLog sendGetRequest(String url) throws MalformedURLException, IOException {
        ConnectionLog result = new ConnectionLog();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        result.setMessage(response.toString());
        result.setErrorCode(ERROR_CODE_SUCCESS);

        return result;
    }

    public ConnectionLog sendPostRequest(String url, String msgBody) throws Exception {
        ConnectionLog result = new ConnectionLog();
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(url);

        post.setHeader("User-Agent", USER_AGENT);
        post.setHeader("Accept-Language", ACCEPTED_LANGUAGE);
        post.setHeader("Content-Type", CONTENT_TYPE);

        StringEntity entity = new StringEntity(msgBody, "UTF-8");
        post.setEntity(entity);

        HttpResponse response = client.execute(post);
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        StringBuffer res = new StringBuffer();
        String line = "";
        while ((line = rd.readLine()) != null) {
            res.append(line);
        }

        result.setMessage(res.toString());
        result.setErrorCode(ERROR_CODE_SUCCESS);

        return result;
    }
}
