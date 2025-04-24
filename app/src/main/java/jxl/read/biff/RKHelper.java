package jxl.read.biff;

/* loaded from: classes.dex */
final class RKHelper {
    private RKHelper() {
    }

    public static double getDouble(int rk) {
        if ((rk & 2) != 0) {
            int intval = rk >> 2;
            double value = intval;
            if ((rk & 1) != 0) {
                Double.isNaN(value);
                return value / 100.0d;
            }
            return value;
        }
        int intval2 = rk & (-4);
        long valbits = intval2;
        double value2 = Double.longBitsToDouble(valbits << 32);
        if ((rk & 1) != 0) {
            return value2 / 100.0d;
        }
        return value2;
    }
}
