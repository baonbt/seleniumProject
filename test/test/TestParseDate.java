/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Thai Tuan Anh
 */
public class TestParseDate {

    public static void main(String[] args) {
        String format = "MMM d, yyyy";
        String dateString = "October 9, 1994";
        DateFormat sdf = new SimpleDateFormat(format);

        try {
            Date date = sdf.parse(dateString);
            System.out.println(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
