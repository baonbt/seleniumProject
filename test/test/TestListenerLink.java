package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import object.dto.ListenerReportCTSDTO;
import service.BaseService;
import util.common.DataUtil;

public class TestListenerLink {
    public static void main(String[] args) throws Exception {
        BaseService baseService = new BaseService();
        ListenerReportCTSDTO boosterReportCtsDTO = new ListenerReportCTSDTO();
        boosterReportCtsDTO.setPostLink("https://www.facebook.com/photo.php?fbid=1704830206303667&set=a.251720818281287.58934.100003301119032&type=3&theater");
        boosterReportCtsDTO.setHashTag("codat");
        boosterReportCtsDTO.setPostType(0);
        boosterReportCtsDTO.setProfileLink("https://www.facebook.com/vuhienhihi");
        baseService.sendPostRequest("http://localhost:8080/buff/listener", DataUtil.objectToJsonString(boosterReportCtsDTO));
    }
}