/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.IOException;
import object.common.Account;
import util.common.DataUtil;

/**
 *
 * @author Thai Tuan Anh
 */
public class TestJson {
    public static void main(String[] args) throws JsonProcessingException, IOException {
        Account acc = new Account();
//        acc.setRealname("Tuan Anh");
        acc.setAddress("Dinh Hoa Thai Nguyen");
        
        System.out.println(DataUtil.objectToJsonString(acc));
        
        String json = "{\"username\":null,\"password\":null,\"cookies\":null,\"birthday\":null,\"realname\":\"Tuan Anh\",\"address\":\"Dinh Hoa Thai Nguyen\"}";
        Account parsed = (Account) DataUtil.jsonStringToObject(json, Account.class);
//        System.out.println(parsed.getRealname());
    }
}
