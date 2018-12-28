/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

/**
 *
 * @author Thai Tuan Anh
 */
public class TestAvatarUrl {

    public static void main(String[] args) {
        String url = "https://scontent.fhan5-4.fna.fbcdn.net/v/t1.0-1/p160x160/23319360_1472450326208324_1696005655984921361_n.jpg?_nc_cat=0&oh=5c4929b77ba3d96a837168bc686ddb9c&oe=5B2D3E56";
        System.out.println(getAvatarNameFromUrl(url));
    }

    private static String getAvatarNameFromUrl(String url) {
        if (url == null) {
            return "";
        } else {
            try {
                String[] segments = url.split("/");
                String nameAndParam = segments[segments.length - 1];

                if (nameAndParam.indexOf('?') > 0) {
                    return nameAndParam.split("\\?")[0];
                } else {
                    return "";
                }
            } catch (Exception e) {
                return "";
            }
        }
    }
}
