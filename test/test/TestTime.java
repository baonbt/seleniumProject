/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 *
 * @author Thai Tuan Anh
 */
public class TestTime {

    private static final String DATE_FORMAT = "EEE MMM dd HH:mm:ss z yyyy";

    public static void main(String[] args) {
        System.out.println(new Date().toString().replaceAll(" ", "_").replaceAll(":", "_"));
//        String dateInString = new Date().toString();
//        LocalDateTime ldt = LocalDateTime.parse(dateInString, DateTimeFormatter.ofPattern(DATE_FORMAT));

//        ZoneId singaporeZoneId = ZoneId.of("Asia/Singapore");
//        System.out.println("TimeZone : " + singaporeZoneId.getId());
//
//        //LocalDateTime + ZoneId = ZonedDateTime
//        ZonedDateTime asiaZonedDateTime = ldt.atZone(singaporeZoneId);
//        System.out.println("Date (Singapore) : " + asiaZonedDateTime);
//
//        ZoneId newYokZoneId = ZoneId.of("America/New_York");
//        System.out.println("TimeZone : " + newYokZoneId);
//
//        ZonedDateTime nyDateTime = asiaZonedDateTime.withZoneSameInstant(newYokZoneId);
//        System.out.println("Date (New York) : " + nyDateTime);
//
//        DateTimeFormatter format = DateTimeFormatter.ofPattern(DATE_FORMAT);
//        System.out.println("\n---DateTimeFormatter---");
//        System.out.println("Date (Singapore) : " + format.format(asiaZonedDateTime));
//        System.out.println("Date (New York) : " + format.format(nyDateTime));
//
//        Date d = Date.from(asiaZonedDateTime.toInstant());
//        System.out.println(d.toString());
    }
}
