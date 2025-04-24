package com.handheld.uhfr;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;

/* loaded from: classes.dex */
public class PcUtils {
    public static int getValueLen(String value) {
        if (value.length() % 4 == 0) {
            int iLength = value.length() / 4;
            return iLength;
        }
        int iLength2 = value.length();
        return (iLength2 / 4) + 1;
    }

    public static int getValueLen(byte[] value) {
        if (value.length % 2 == 0) {
            int iLength = value.length / 2;
            return iLength;
        }
        int iLength2 = value.length;
        return (iLength2 / 2) + 1;
    }

    public static int getValueLen(int byteLen) {
        if (byteLen % 2 == 0) {
            int iLength = byteLen / 2;
            return iLength;
        }
        int iLength2 = (byteLen / 2) + 1;
        return iLength2;
    }

    public static String getPc(int pcLen) {
        int iPc = pcLen << 11;
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.put(iPc);
        buffer.position(16);
        byte[] bTmp = new byte[2];
        buffer.get(bTmp);
        return HexUtils.bytes2HexString(bTmp);
    }

    public static String getGJBPc(int pcLen) {
        int iPc = pcLen << 8;
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.put(iPc);
        buffer.position(16);
        byte[] bTmp = new byte[2];
        buffer.get(bTmp);
        return HexUtils.bytes2HexString(bTmp);
    }

    public static String padRight(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }
        char[] chars = new char[len];
        System.arraycopy(src.toCharArray(), 0, chars, 0, src.length());
        for (int i = src.length(); i < len; i++) {
            chars[i] = ch;
        }
        return new String(chars);
    }
}
