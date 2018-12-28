/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import object.dto.ChatbotDTO;
import object.log.BaseLog;
import object.log.ConnectionLog;
import util.common.Constant;
import util.common.DataUtil;
import static util.common.Constant.SERVER.SIMSIMI;
import static util.common.DataUtil.random;

/**
 *
 * @author Thai Tuan Anh
 */
public class ChatbotService extends BaseService {

    public String chatWithSimsimi(ChatbotDTO dto) {
        try {
            ConnectionLog result = (ConnectionLog) DataUtil.jsonStringToObject(
                    sendPostRequest(SIMSIMI, DataUtil.objectToJsonString(dto)).getMessage(),
                    ConnectionLog.class);

            if (result != null && !Constant.HTTP.ERROR_CODE_FAIL.equals(result.getErrorCode())) {
                return URLDecoder.decode(result.getMessage(), "utf-8");
            }

            return "";
        } catch (Exception ex) {
            Logger.getLogger(ChatbotService.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public String reply(String content, long accountId) {
        ChatbotDTO dto = new ChatbotDTO();
        dto.setContent(content);
        dto.setAccountId(accountId);
        dto.setCount(0);

        String result = chatWithSimsimi(dto);

        if (result != null) {
            return result;
        } else {
            return "null";
        }
    }

    public String wishGenerator(int i, String name) {
        switch (i) {
            case 1:
                return "Happy birthday to you, " + name + "!";
            case 2:
                return "Hey" + name + ", I hope your special day will bring you lots of happiness, love and fun. You deserve them a lot. Enjoy!";
            case 3:
                return name + ", Have a wonderful birthday. I wish your every day to be filled with lots of love, laughter, happiness and the warmth of sunshine.";
            case 4:
                return "May your coming year surprise you with the happiness of smiles, the feeling of love and so on. I hope you will find plenty of sweet memories to cherish forever. Happy birthday " + name + "!";
            case 5:
                return name + ", on your special day, I wish you good luck. I hope this wonderful day will fill up your heart with joy and blessings. Have a fantastic birthday, celebrate the happiness on every day of your life. Happy Birthday!!";
            case 6:
                return "May this birthday be filled with lots of happy hours and also your life with many happy birthdays, that are yet to come. Happy birthday " + name + "!";
            case 7:
                return "Let’s light the candles and celebrate this special day of your life. Happy birthday " + name + "!";
            case 8:
                return "You are very special and that’s why you need to float with lots of smiles on your lovely face. Happy birthday " + name + "!";
            case 9:
                return "You are very special and that’s why you need to float with lots of smiles on your lovely face. Happy birthday " + name + "!";
            case 10:
                return "Special day, special person and special celebration. May all your dreams and desires come true in this coming year. Happy birthday " + name + "!";
        }
        return "HappyBirthday!";
    }

    public static String randomEmotion() {
        List<String> emotion = new ArrayList<>();
        emotion.add(":)");
        emotion.add(":)");
        emotion.add(":) :)");
        emotion.add(":) :) :)");
        emotion.add(":D");
        emotion.add(":D :D");
        emotion.add(":D :D :D");
        emotion.add(":(");
        emotion.add(":( :(");
        emotion.add(":( :( :(");
        emotion.add(":\'(");
        emotion.add(":\'( :\'(");
        emotion.add(":\'( :\'( :\'(");
        emotion.add(":P");
        emotion.add(":P :P");
        emotion.add(":P :P :P");
        emotion.add("O:)");
        emotion.add("O:) O:)");
        emotion.add("O:) O:) O:)");
        emotion.add("3:)");
        emotion.add("3:) 3:)");
        emotion.add("3:) 3:) 3:)");
        emotion.add("o.O");
        emotion.add(";)");
        emotion.add(";) ;)");
        emotion.add(";) ;) ;)");
        emotion.add(":O");
        emotion.add(":O :O");
        emotion.add(":O :O :O");
        emotion.add("-_-");
        emotion.add(">.O");
        emotion.add(">.O >.O");
        emotion.add(">.O >.O >.O");
        emotion.add(":*");
        emotion.add(":* :*");
        emotion.add(":* :* :*");
        emotion.add(":* :* :* :*");
        emotion.add(":* :* :* :* :*");
        emotion.add("<3");
        emotion.add("<3 <3");
        emotion.add("<3 <3 <3");
        emotion.add("<3 <3 <3 <3");
        emotion.add("<3 <3 <3 <3 <3 ");
        emotion.add("^.^");
        emotion.add("8-)");
        emotion.add("8|");
        emotion.add("(^^^)");
        emotion.add(":|]");
        emotion.add(">:(");
        emotion.add(":v");
        emotion.add(":/");
        emotion.add(":3");
        emotion.add("(y) (y)");
        emotion.add("(y) (y) (y)");
        emotion.add("(y)");
        emotion.add(":poop:");
        emotion.add("<(\'\')");
        return emotion.get(random(0, emotion.size() - 1));
    }
}
