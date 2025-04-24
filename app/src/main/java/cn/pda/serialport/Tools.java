package cn.pda.serialport;

import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import java.text.SimpleDateFormat;
import java.util.Date;

/* loaded from: classes.dex */
public class Tools {
    public static String Bytes2HexString(byte[] b, int size) {
        String ret = "";
        for (int i = 0; i < size; i++) {
            String hex = Integer.toHexString(b[i] & 255);
            if (hex.length() == 1) {
                hex = "0" + hex;
            }
            ret = ret + hex.toUpperCase();
        }
        return ret;
    }

    public static byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0})).byteValue();
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1})).byteValue();
        byte ret = (byte) (((byte) (_b0 << 4)) ^ _b1);
        return ret;
    }

    public static byte[] HexString2Bytes(String src) {
        int len = src.length() / 2;
        byte[] ret = new byte[len];
        byte[] tmp = src.getBytes();
        for (int i = 0; i < len; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[(i * 2) + 1]);
        }
        return ret;
    }

    public static int bytesToInt(byte[] bytes) {
        int addr = bytes[0] & 255;
        return addr | ((bytes[1] << 8) & MotionEventCompat.ACTION_POINTER_INDEX_MASK) | ((bytes[2] << 16) & 16711680) | ((bytes[3] << 25) & ViewCompat.MEASURED_STATE_MASK);
    }

    public static byte[] intToByte(int i) {
        byte[] abyte0 = {(byte) (i & 255), (byte) ((65280 & i) >> 8), (byte) ((16711680 & i) >> 16), (byte) (((-16777216) & i) >> 24)};
        return abyte0;
    }

    public static String getmyTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sDateFormat.format(new Date());
        return date;
    }
}
