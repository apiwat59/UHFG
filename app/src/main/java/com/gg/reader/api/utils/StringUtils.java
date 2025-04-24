package com.gg.reader.api.utils;

/* loaded from: classes.dex */
public class StringUtils {
    public static boolean isNullOfEmpty(String str) {
        if (str == null) {
            return true;
        }
        return str.isEmpty();
    }

    public static String genPad(char pad, int len) {
        StringBuffer temp = new StringBuffer();
        for (int i = 0; i < len; i++) {
            temp.append(pad);
        }
        return temp.toString();
    }

    public static String padRight(String src, char pad, int len) {
        int srcLen = src.length();
        if (srcLen < len) {
            String str = genPad(pad, len - srcLen);
            return src + str;
        }
        return src;
    }

    public static String padLeft(String src, char pad, int len) {
        int srcLen = src.length();
        if (srcLen < len) {
            String str = genPad(pad, len - srcLen);
            return str + src;
        }
        return src;
    }

    public static String trimStart(String src, String trim) {
        if (src.startsWith(trim)) {
            return src.substring(trim.length());
        }
        return src;
    }

    public static String trimEnd(String src, String trim) {
        if (src.endsWith(trim)) {
            return src.substring(0, src.length() - trim.length());
        }
        return src;
    }
}
