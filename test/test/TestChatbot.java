/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import object.dto.ChatbotDTO;
import service.ChatbotService;

/**
 * 
 * @author Thai Tuan Anh
 */
public class TestChatbot {
    public static void main(String[] args) {
        ChatbotService chatbot = new ChatbotService();
        ChatbotDTO dto = new ChatbotDTO();
        dto.setAccountId(1);
        dto.setCount(0);
        dto.setContent("dis mẹ");
        System.out.println(chatbot.chatWithSimsimi(dto));
    }
}
