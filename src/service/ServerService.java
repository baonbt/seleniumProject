/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import action.execution.Executor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import object.dto.BaseDTO;
import object.dto.StatusDTO;
import object.log.BaseLog;
import object.log.ConnectionLog;
import org.apache.commons.io.IOUtils;
import util.common.DataUtil;
import util.common.Constant;

import static util.common.Constant.SERVER.SERVER_GET_STATUS;

/**
 * @author Thai Tuan Anh
 */
public class ServerService extends BaseService {

    public static String sendDTOToServer(String serverUrl, BaseDTO dto) {
        try {
            BaseService ws = new BaseService();
            ConnectionLog connectionLog = (ConnectionLog) DataUtil.jsonStringToObject(ws.sendPostRequest(serverUrl, DataUtil.objectToJsonString(dto)).getMessage(), ConnectionLog.class);

            return connectionLog.getErrorCode();
        } catch (Exception ex) {
            System.out.println("GET DTO FROM SERVER ERROR!");
            return Constant.HTTP.ERROR_CODE_FAIL;
        }
    }

    public static String sendToServer(String serverUrl) {
        try {
            BaseService ws = new BaseService();
            String actionResult = ws.sendGetRequest(serverUrl).getMessage();

            return actionResult;
        } catch (Exception ex) {
            System.out.println("GET INFO FROM SERVER ERROR!");
            return Constant.HTTP.ERROR_CODE_FAIL;
        }
    }

    public static String sendLogToServer(String serverUrl, BaseLog log) {
        try {
            BaseService ws = new BaseService();
            ConnectionLog connectionLog = (ConnectionLog) DataUtil.jsonStringToObject(ws.sendPostRequest(serverUrl, DataUtil.objectToJsonString(log)).getMessage(), ConnectionLog.class);

            return connectionLog.getErrorCode();
        } catch (Exception ex) {
            Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, null, ex);
            return Constant.HTTP.ERROR_CODE_FAIL;
        }
    }


    public StatusDTO getImageFromServer(String type) throws IOException {
        InputStream is = null;
        byte[] imageBytes = null;
        StatusDTO imageDTO = null;

        try {
            imageDTO = (StatusDTO) DataUtil.jsonStringToObject(sendGetRequest(SERVER_GET_STATUS + "/" + type).getMessage(), StatusDTO.class);
            if (imageDTO != null) {
                URL url = new URL(imageDTO.getUrl());
                is = url.openStream();
                imageBytes = IOUtils.toByteArray(is);
                imageDTO.setBytes(imageBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
            return imageDTO;
        }
    }

    public StatusDTO getStatusDTOFromServer(long accountId, String type) throws IOException {
        InputStream is = null;
        byte[] imageBytes = null;
        StatusDTO statusDTO = null;

        try {
            statusDTO = (StatusDTO) DataUtil.jsonStringToObject(sendGetRequest(SERVER_GET_STATUS + "/" + accountId + "/" + type).getMessage(), StatusDTO.class);
            statusDTO.setBytes(new byte[0]);

            if (statusDTO != null && statusDTO.getUrl() != null) {
                URL url = new URL(statusDTO.getUrl());
                is = url.openStream();
                imageBytes = IOUtils.toByteArray(is);
                statusDTO.setBytes(imageBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                is.close();
            }
            return statusDTO;
        }
    }

    public String getBase64FromUrl(StatusDTO dto, String link) {
        try {
            InputStream is = null;
            byte[] imageBytes = null;

            URL url = new URL(link);
            is = url.openStream();
            imageBytes = IOUtils.toByteArray(is);
            String extension = link.substring(link.lastIndexOf(".") + 1);
            dto.setFileType(extension);

            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (IOException ex) {
            Logger.getLogger(ServerService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    public static String sendObjectToServer(String serverUrl, Object obj) {
        try {
            BaseService ws = new BaseService();
            ConnectionLog connectionLog = (ConnectionLog) DataUtil.jsonStringToObject(ws.sendPostRequest(serverUrl, obj.toString()).getMessage(), ConnectionLog.class);

            return connectionLog.getErrorCode();
        } catch (Exception ex) {
            Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, null, ex);
            return Constant.HTTP.ERROR_CODE_FAIL;
        }
    }
}
