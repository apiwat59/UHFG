package com.gg.reader.api.utils;

import androidx.core.internal.view.SupportMenu;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import com.gg.reader.api.protocol.gx.EnumG;

/* loaded from: classes.dex */
public class HexUtils {
    public static boolean isHexDigit(char c) {
        if (c >= '0' && c <= '9') {
            return true;
        }
        if (c >= 'A' && c <= 'F') {
            return true;
        }
        if (c >= 'a' && c <= 'f') {
            return true;
        }
        return false;
    }

    public static boolean isHexString(String str) {
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!isHexDigit(c)) {
                return false;
            }
        }
        return true;
    }

    public static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(Character.toUpperCase(c));
    }

    public static byte hex2Byte(String hex) {
        if (hex.length() < 2) {
            hex = "00" + hex;
        }
        if (hex.length() > 2) {
            hex = hex.substring(0, 2);
        }
        return (byte) ((charToByte(hex.charAt(0)) << 4) | charToByte(hex.charAt(1)));
    }

    public static short hex2Short(String hex) {
        if (hex.length() < 4) {
            hex = EnumG.MSG_TYPE_BIT_ERROR + hex;
        }
        if (hex.length() > 4) {
            hex = hex.substring(0, 4);
        }
        return (short) ((charToByte(hex.charAt(0)) << 12) | (charToByte(hex.charAt(1)) << 8) | (charToByte(hex.charAt(2)) << 4) | charToByte(hex.charAt(3)));
    }

    public static int hex2Int(String hex) {
        if (hex.length() < 8) {
            hex = "00000000" + hex;
        }
        if (hex.length() > 8) {
            hex = hex.substring(0, 8);
        }
        return (charToByte(hex.charAt(0)) << 28) | (charToByte(hex.charAt(1)) << 24) | (charToByte(hex.charAt(2)) << 20) | (charToByte(hex.charAt(3)) << 16) | (charToByte(hex.charAt(4)) << 12) | (charToByte(hex.charAt(5)) << 8) | (charToByte(hex.charAt(6)) << 4) | charToByte(hex.charAt(7));
    }

    public static long hex2Long(String hex) {
        if (hex.length() < 16) {
            hex = "0000000000000000" + hex;
        }
        if (hex.length() > 16) {
            hex = hex.substring(0, 16);
        }
        long value = (charToByte(hex.charAt(0)) << 28) | (charToByte(hex.charAt(1)) << 24) | (charToByte(hex.charAt(2)) << 20) | (charToByte(hex.charAt(3)) << 16) | (charToByte(hex.charAt(4)) << 12) | (charToByte(hex.charAt(5)) << 8) | (charToByte(hex.charAt(6)) << 4) | charToByte(hex.charAt(7));
        return (((value << 16) | ((((charToByte(hex.charAt(8)) << 12) | (charToByte(hex.charAt(9)) << 8)) | (charToByte(hex.charAt(10)) << 4)) | charToByte(hex.charAt(11)))) << 16) | (charToByte(hex.charAt(14)) << 4) | (charToByte(hex.charAt(12)) << 12) | (charToByte(hex.charAt(13)) << 8) | charToByte(hex.charAt(15));
    }

    public static String byte2Hex(byte b) {
        try {
            String rt = Integer.toHexString(b & 255);
            if (rt.length() == 1) {
                rt = '0' + rt;
            }
            return rt.toUpperCase();
        } catch (Exception e) {
            return "";
        }
    }

    public static String short2Hex(short value) {
        String rt = byte2Hex((byte) ((value >> 8) & 255));
        return rt + byte2Hex((byte) (value & 255));
    }

    public static String int2Hex(int value) {
        String rt = short2Hex((short) ((value >> 16) & SupportMenu.USER_MASK));
        return rt + short2Hex((short) (65535 & value));
    }

    public static String long2Hex(long value) {
        String rt = int2Hex((int) (value >> 32));
        return rt + int2Hex((int) value);
    }

    public static byte[] hexString2Bytes(String hexString) {
        if (hexString == null || hexString.isEmpty()) {
            throw new IllegalArgumentException();
        }
        String newString = "";
        for (int i = 0; i < hexString.length(); i++) {
            char c = hexString.charAt(i);
            if (isHexDigit(c)) {
                newString = newString + c;
            }
        }
        int i2 = newString.length();
        if (i2 % 2 != 0) {
            newString = newString.substring(0, newString.length() - 1);
        }
        int byteLength = newString.length() / 2;
        byte[] bytes = new byte[byteLength];
        int j = 0;
        for (int i3 = 0; i3 < bytes.length; i3++) {
            String hex = newString.substring(j, j + 2);
            bytes[i3] = hex2Byte(hex);
            j += 2;
        }
        return bytes;
    }

    public static String bytes2HexString(byte[] bArray, int offset, int length) {
        if (bArray == null || bArray.length <= 0) {
            throw new IllegalArgumentException();
        }
        if (offset + length > bArray.length) {
            throw new IllegalArgumentException();
        }
        if (offset < 0 || length < 0) {
            throw new IllegalArgumentException();
        }
        StringBuffer temp = new StringBuffer();
        for (int i = offset; i < offset + length; i++) {
            temp.append(byte2Hex(bArray[i]));
        }
        return temp.toString();
    }

    public static String bytes2HexString(byte[] bArray) {
        if (bArray == null || bArray.length <= 0) {
            throw new IllegalArgumentException();
        }
        return bytes2HexString(bArray, 0, bArray.length);
    }

    public static short bytes2Short(byte[] bArray, int offset) {
        if (bArray == null || bArray.length < offset + 2) {
            throw new IllegalArgumentException();
        }
        return (short) (((bArray[offset] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (bArray[offset + 1] & 255));
    }

    public static short bytes2Short(byte[] bArray) {
        return bytes2Short(bArray, 0);
    }

    public static byte[] short2Bytes(short value) {
        byte[] result = {(byte) ((value >> 8) & 255), (byte) (value & 255)};
        return result;
    }

    public static int bytes2Int(byte[] bArray, int offset) {
        if (bArray == null || bArray.length < offset + 4) {
            throw new IllegalArgumentException();
        }
        return ((bArray[offset] << 24) & ViewCompat.MEASURED_STATE_MASK) | ((bArray[offset + 1] << 16) & 16711680) | ((bArray[offset + 2] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | (bArray[offset + 3] & 255);
    }

    public static int bytes2Int(byte[] bArray) {
        return bytes2Int(bArray, 0);
    }

    public static byte[] int2Bytes(int value) {
        byte[] result = {(byte) ((value >> 24) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 8) & 255), (byte) (value & 255)};
        return result;
    }

    public static byte[] long2Bytes(long value) {
        byte[] result = {(byte) ((value >> 56) & 255), (byte) ((value >> 48) & 255), (byte) ((value >> 40) & 255), (byte) ((value >> 32) & 255), (byte) ((value >> 24) & 255), (byte) ((value >> 16) & 255), (byte) ((value >> 8) & 255), (byte) (value & 255)};
        return result;
    }
}
