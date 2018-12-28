/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import static util.common.DataUtil.isNullOrEmptyStr;

/**
 *
 * @author Thai Tuan Anh
 */
public class TestString {

    public static void main(String[] args) throws URISyntaxException, UnsupportedFlavorException, IOException {
//        String result = "https://www.facebook.com/Kho-S%E1%BB%89-D%C3%BAi-0931585960-1874504709507332/?fref=pb&hc_location=profile_browser";
//        System.out.println(getUidFromProfileLink(result));
//
//
//        String s = "4903834100023825824209";
//        System.out.println(s.substring(s.length() - 15, s.length()));
//
//        String a = "THAI tuan anh";
//        System.out.println(a.toUpperCase());
//        System.out.println(a.toLowerCase());

        String liveViewsCount = "abc 123";
        if (isNullOrEmptyStr(liveViewsCount)) {
            liveViewsCount = "0";
        }
        int a = Integer.valueOf(liveViewsCount.replaceAll("[^0-9]", ""));
        System.out.println(a);
    }

    private static String getUidFromProfileLink(String profileLink) throws URISyntaxException {
        URI uri = new URI(profileLink);

        if (!profileLink.contains("profile.php")) {
            String[] segments = uri.getPath().split("/");

            int i = 0;
            for (String segment : segments) {
                if (segment.contains("facebook.com")) {
                    i++;
                    break;
                }
            }

            return segments[i + 1];
        } else {
            List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");

            for (NameValuePair param : params) {
                if ("id".equals(param.getName())) {
                    return param.getValue();
                }
            }

            return "";
        }
    }

    private static String handleFileType(String fileType) {
        if (fileType.indexOf('?') > 0) {
            return fileType.split("\\?")[0];
        } else {
            return fileType;
        }
    }
}
