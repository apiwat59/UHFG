package com.rscja.utility;

import android.util.Log;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import com.pda.uhf_g.util.ExcelUtil;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

/* loaded from: classes.dex */
public class StringUtility {
    public static String bytes2HexString(byte[] b, int size) {
        String str = "";
        for (int i = 0; i < size; i++) {
            try {
                String hexString = Integer.toHexString(b[i] & 255);
                if (hexString.length() == 1) {
                    hexString = "0" + hexString;
                }
                str = String.valueOf(str) + hexString.toUpperCase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public static String byte2HexString(byte b) {
        try {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            return String.valueOf("") + hexString.toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String chars2HexString(char[] c, int size) {
        String str = "";
        for (int i = 0; i < size; i++) {
            try {
                String hexString = Integer.toHexString(Integer.valueOf(c[i]).intValue());
                if (hexString.length() == 1) {
                    hexString = "0" + hexString;
                }
                str = String.valueOf(str) + hexString.toUpperCase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str;
    }

    public static long byteArrayTolong(byte[] byteArray) {
        byte[] bArr = new byte[8];
        int length = byteArray.length - 1;
        int i = 7;
        while (i >= 0) {
            if (length >= 0) {
                bArr[i] = byteArray[length];
            } else {
                bArr[i] = 0;
            }
            i--;
            length--;
        }
        long j = (bArr[0] & 255) << 56;
        long j2 = (bArr[1] & 255) << 48;
        long j3 = (bArr[2] & 255) << 40;
        long j4 = (bArr[3] & 255) << 32;
        long j5 = (bArr[4] & 255) << 24;
        long j6 = (bArr[5] & 255) << 16;
        long j7 = (bArr[6] & 255) << 8;
        long j8 = bArr[7] & 255;
        Log.i("StringUtility", j + "@" + j2 + "@" + j3 + "@" + j4 + "@" + j5 + "@" + j6 + "@" + j7 + "@" + j8);
        return j8 | j2 | j | j3 | j4 | j5 | j6 | j7;
    }

    public static int bytesToInt(byte[] bytes) {
        return ((bytes[3] << 24) & ViewCompat.MEASURED_STATE_MASK) | (bytes[0] & 255) | ((bytes[1] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | ((bytes[2] << 16) & 16711680);
    }

    public static long charArrayTolong(char[] array) {
        char[] cArr = new char[8];
        int length = array.length - 1;
        int i = 7;
        while (i >= 0) {
            if (length >= 0) {
                cArr[i] = array[length];
            } else {
                cArr[i] = 0;
            }
            i--;
            length--;
        }
        long j = (cArr[0] & 255) << 56;
        long j2 = (cArr[1] & 255) << 48;
        long j3 = (cArr[2] & 255) << 40;
        long j4 = (cArr[3] & 255) << 32;
        long j5 = (cArr[4] & 255) << 24;
        long j6 = (cArr[5] & 255) << 16;
        long j7 = (cArr[6] & 255) << 8;
        long j8 = cArr[7] & 255;
        Log.i("StringUtility", j + "@" + j2 + "@" + j3 + "@" + j4 + "@" + j5 + "@" + j6 + "@" + j7 + "@" + j8);
        return j8 | j2 | j | j3 | j4 | j5 | j6 | j7;
    }

    private static byte[] a(byte[] bArr) {
        byte[] bArr2 = new byte[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
            bArr2[i] = bArr[(bArr.length - 1) - i];
        }
        return bArr2;
    }

    public static final BigInteger readUnsignedInt64(byte[] readBuffer) throws IOException {
        if (readBuffer == null || readBuffer.length < 8) {
            return new BigInteger("0");
        }
        byte[] bArr = new byte[9];
        bArr[8] = 0;
        System.arraycopy(readBuffer, 0, bArr, 0, 8);
        return new BigInteger(a(bArr));
    }

    public static long chars2Long(char[] c, int i, int i2) {
        byte[] bytes = getBytes(c);
        for (byte b : bytes) {
            Log.i("StringUtility", "chars2Long bytes[i]:" + ((int) b));
        }
        return byteArrayTolong(bytes);
    }

    public static String char2HexString(char c) {
        return chars2HexString(new char[]{c}, 1);
    }

    public static boolean isOctNumber(String str) {
        int length = str.length();
        boolean z = false;
        for (int i = 0; i < length; i++) {
            char charAt = str.charAt(i);
            if ((charAt == '9') | (charAt == '0') | (charAt == '1') | (charAt == '2') | (charAt == '3') | (charAt == '4') | (charAt == '5') | (charAt == '6') | (charAt == '7') | (charAt == '8')) {
                z = true;
            }
        }
        return z;
    }

    @Deprecated
    public static boolean isHexNumber(String str) {
        boolean z = false;
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt == '0' || charAt == '1' || charAt == '2' || charAt == '3' || charAt == '4' || charAt == '5' || charAt == '6' || charAt == '7' || charAt == '8' || charAt == '9' || charAt == 'A' || charAt == 'B' || charAt == 'C' || charAt == 'D' || charAt == 'E' || charAt == 'F' || charAt == 'a' || charAt == 'b' || charAt == 'c' || charAt == 'c' || charAt == 'd' || charAt == 'e' || charAt == 'f') {
                z = true;
            }
        }
        return z;
    }

    public static boolean isOctNumberRex(String str) {
        return str.matches("\\d+");
    }

    public static boolean isHexNumberRex(String str) {
        return str.matches("(?i)[0-9a-f]+");
    }

    public static char[] hexString2Chars(String s) {
        String s2 = s.replace(" ", "");
        int length = s2.length() / 2;
        char[] cArr = new char[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            cArr[i] = (char) Integer.parseInt(s2.substring(i2, i2 + 2), 16);
        }
        return cArr;
    }

    public static byte[] getBytes(char[] chars) {
        Charset forName = Charset.forName(ExcelUtil.UTF8_ENCODING);
        CharBuffer allocate = CharBuffer.allocate(chars.length);
        allocate.put(chars);
        allocate.flip();
        return forName.encode(allocate).array();
    }

    public static char[] getChars(byte[] bytes) {
        Charset forName = Charset.forName(ExcelUtil.UTF8_ENCODING);
        ByteBuffer allocate = ByteBuffer.allocate(bytes.length);
        allocate.put(bytes);
        allocate.flip();
        return forName.decode(allocate).array();
    }

    public static boolean isDecimal(String decimal) {
        int length = decimal.length();
        int i = 0;
        while (i < length) {
            int i2 = i + 1;
            char charAt = decimal.charAt(i);
            if (charAt < '0' || charAt > '9') {
                return false;
            }
            i = i2;
        }
        return true;
    }

    public static byte[] hexString2Bytes(String s) {
        int length = s.length() / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) Integer.parseInt(s.substring(i2, i2 + 2), 16);
        }
        return bArr;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        String hexString2 = hexString.toUpperCase();
        int length = hexString2.length() / 2;
        char[] charArray = hexString2.toCharArray();
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i << 1;
            bArr[i] = (byte) (a(charArray[i2 + 1]) | (a(charArray[i2]) << 4));
        }
        return bArr;
    }

    private static byte a(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNum(String str) {
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    public static String int2HexString(int n) {
        String hexString = Integer.toHexString(n);
        int length = hexString.length();
        if (length == 1) {
            return "0" + hexString;
        }
        return hexString.substring(length - 2, length);
    }

    public static int string2Int(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
            return defValue;
        }
    }
}
