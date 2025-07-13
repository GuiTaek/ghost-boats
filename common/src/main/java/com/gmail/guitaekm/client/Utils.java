package com.gmail.guitaekm.client;

public class Utils {
    public static int min(int...a) {
        if (a.length == 0) {
            return Integer.MAX_VALUE;
        }
        int result = a[0];
        for (int i = 1; i < a.length; i++) {
            result = Math.min(result, a[i]);
        }
        return result;
    }
    public static int max(int...a) {
        if (a.length == 0) {
            return Integer.MAX_VALUE;
        }
        int result = a[0];
        for (int i = 1; i < a.length; i++) {
            result = Math.max(result, a[i]);
        }
        return result;
    }
}
