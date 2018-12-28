/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Thai Tuan Anh
 */
public class TestRandom {

    public static void main(String[] args) {
        System.out.println(randomByPercent("1=0;2=0;3=5;4=0;5=0;6=10"));
    }

    private static int randomByPercent(String input) {

        List<Integer> list = new ArrayList<>();

        String[] items = input.split(";");
        for (String item : items) {
            String numStr = item.split("=")[0];
            String percentStr = item.split("=")[1];

            int num = Integer.valueOf(numStr);
            int percent = Integer.valueOf(percentStr);

            if (percent == 0) {
                continue;
            }

            for (int i = 0; i < percent; i++) {
                list.add(num);
            }
        }

        if (list.size() < 1) {
            return -1;
        }

        return list.get(randomInRange(0, list.size() - 1));
    }

    public static int randomInRange(int min, int max) {

        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}
