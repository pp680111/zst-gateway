package com.zst.utils;

public class StringUtils {
    public static boolean hasLength(String str) {
        return (str != null && !str.isEmpty());
    }

    public static boolean isEmpty(String str) {
        return !hasLength(str);
    }

    public static boolean isNotEmpty(String str) {
        return hasLength(str);
    }
}
