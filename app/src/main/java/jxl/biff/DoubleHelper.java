package jxl.biff;

/* loaded from: classes.dex */
public class DoubleHelper {
    private DoubleHelper() {
    }

    public static double getIEEEDouble(byte[] data, int pos) {
        int num1 = IntegerHelper.getInt(data[pos], data[pos + 1], data[pos + 2], data[pos + 3]);
        int num2 = IntegerHelper.getInt(data[pos + 4], data[pos + 5], data[pos + 6], data[pos + 7]);
        boolean negative = (Integer.MIN_VALUE & num2) != 0;
        long j = (Integer.MAX_VALUE & num2) * 4294967296L;
        long j2 = num1;
        if (num1 < 0) {
            j2 += 4294967296L;
        }
        long val = j + j2;
        double value = Double.longBitsToDouble(val);
        if (negative) {
            return -value;
        }
        return value;
    }

    public static void getIEEEBytes(double d, byte[] target, int pos) {
        long val = Double.doubleToLongBits(d);
        target[pos] = (byte) (255 & val);
        target[pos + 1] = (byte) ((65280 & val) >> 8);
        target[pos + 2] = (byte) ((16711680 & val) >> 16);
        target[pos + 3] = (byte) (((-16777216) & val) >> 24);
        target[pos + 4] = (byte) ((1095216660480L & val) >> 32);
        target[pos + 5] = (byte) ((280375465082880L & val) >> 40);
        target[pos + 6] = (byte) ((71776119061217280L & val) >> 48);
        target[pos + 7] = (byte) (((-72057594037927936L) & val) >> 56);
    }
}
