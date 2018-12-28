package test;

import service.ChromeProfileService;

import java.io.IOException;

public class TestCloneProfile {

    public static void main(String[] args) throws IOException, InterruptedException {
        ChromeProfileService cps = new ChromeProfileService();

        System.out.println(cps.cloneChromeProfile());

    }
}
