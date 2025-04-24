package jxl.biff.formula;

import jxl.biff.IntegerHelper;
import jxl.common.Logger;

/* loaded from: classes.dex */
class IntegerValue extends NumberValue implements ParsedThing {
    private static Logger logger = Logger.getLogger(IntegerValue.class);
    private boolean outOfRange;
    private double value;

    public IntegerValue() {
        this.outOfRange = false;
    }

    public IntegerValue(String s) {
        try {
            this.value = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            logger.warn(e, e);
            this.value = 0.0d;
        }
        double d = this.value;
        short v = (short) d;
        this.outOfRange = d != ((double) v);
    }

    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        this.value = IntegerHelper.getInt(data[pos], data[pos + 1]);
        return 2;
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        byte[] data = {Token.INTEGER.getCode(), 0, 0};
        IntegerHelper.getTwoBytes((int) this.value, data, 1);
        return data;
    }

    @Override // jxl.biff.formula.NumberValue
    public double getValue() {
        return this.value;
    }

    boolean isOutOfRange() {
        return this.outOfRange;
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
    }
}
