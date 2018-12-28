/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Thai Tuan Anh
 */
public class TestParamUri {

    public static void main(String[] args) {
        String uri = "/groups/member_bio/bio_dialog/?group_id=337937363284185&member_id=100003462102575&ref=floc2";
        System.out.println(getQueryMap(uri).get("member_id"));
    }

    public static Map<String, String> getQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }
}
