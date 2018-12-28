/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import action.handler.BaseAction;
import java.io.IOException;

import object.dto.StatusDTO;
import service.ServerService;

/**
 *
 * @author Thai Tuan Anh
 */
public class TestImage {

    public static void main(String[] args) throws IOException {
        //nạp ảnh vào clipboard
        ServerService service = new ServerService();
        StatusDTO imageDTO = service.getImageFromServer("sexy");
        System.out.println("");
        BaseAction baseAction = new BaseAction(null);
        baseAction.copyImageToClipboard(imageDTO.getBytes());
        //
    }
}
