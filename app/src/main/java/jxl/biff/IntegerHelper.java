package jxl.biff;

/* loaded from: classes.dex */
public final class IntegerHelper {
    private IntegerHelper() {
    }

    public static int getInt(byte b1, byte b2) {
        int i1 = b1 & 255;
        int i2 = b2 & 255;
        int val = (i2 << 8) | i1;
        return val;
    }

    public static short getShort(byte b1, byte b2) {
        short i1 = (short) (b1 & 255);
        short i2 = (short) (b2 & 255);
        short val = (short) ((i2 << 8) | i1);
        return val;
    }

    public static int getInt(byte b1, byte b2, byte b3, byte b4) {
        int i1 = getInt(b1, b2);
        int i2 = getInt(b3, b4);
        int val = (i2 << 16) | i1;
        return val;
    }

    public static byte[] getTwoBytes(int i) {
        byte[] bytes = {(byte) (i & 255), (byte) ((65280 & i) >> 8)};
        return bytes;
    }

    public static byte[] getFourBytes(int i) {
        byte[] bytes = new byte[4];
        int i1 = 65535 & i;
        int i2 = ((-65536) & i) >> 16;
        getTwoBytes(i1, bytes, 0);
        getTwoBytes(i2, bytes, 2);
        return bytes;
    }

    public static void getTwoBytes(int i, byte[] target, int pos) {
        target[pos] = (byte) (i & 255);
        target[pos + 1] = (byte) ((65280 & i) >> 8);
    }

    public static void getFourBytes(int i, byte[] target, int pos) {
        byte[] bytes = getFourBytes(i);
        target[pos] = bytes[0];
        target[pos + 1] = bytes[1];
        target[pos + 2] = bytes[2];
        target[pos + 3] = bytes[3];
    }
}
