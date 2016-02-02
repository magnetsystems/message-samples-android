package com.magnet.messagingsample.helpers;

import java.util.StringTokenizer;

public class TextHelper {

    public static String getInitials(String name) {
        String result = "";
        StringTokenizer stringTokenizer = new StringTokenizer(name);
        while (stringTokenizer.hasMoreTokens()) {
            result += stringTokenizer.nextToken().charAt(0);
        }
        return result.toUpperCase();
    }

}
