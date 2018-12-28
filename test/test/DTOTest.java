/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.List;

import object.dto.GroupRawDTO;
import service.ServerService;

import static util.common.Constant.SERVER.OFFLINE_WATCHER;

/**
 *
 * @author baonb
 */
public class DTOTest {
    public static void main(String[] args) {
        GroupRawDTO dto = new GroupRawDTO();//cai dto nay de gui len sv
        List<GroupRawDTO.GroupInfo> groups = new ArrayList<>();
        dto.setGroups(groups);
        
        for (int i = 0; i < 10; i++) {
            GroupRawDTO.GroupInfo info = new GroupRawDTO.GroupInfo();
            groups.add(info);
            info.setGid("a"+i);
            info.setKey("b"+i);
            
        }
        System.out.println(dto.getGroups().get(0).getGid());

        System.out.println((int)Math.round(1.4));

        System.out.println(ServerService.sendObjectToServer(OFFLINE_WATCHER, 10L));
    }
}
