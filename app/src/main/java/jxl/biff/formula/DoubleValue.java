package jxl.biff.formula;

import jxl.biff.DoubleHelper;
import jxl.common.Logger;

/* loaded from: classes.dex */
class DoubleValue extends NumberValue implements ParsedThing {
    private static Logger logger = Logger.getLogger(DoubleValue.class);
    private double value;

    public DoubleValue() {
    }

    DoubleValue(double v) {
        this.value = v;
    }

    public DoubleValue(String s) {
        try {
            this.value = Double.parseDouble(s);
        } catch (NumberFormatException e) {
            logger.warn(e, e);
            this.value = 0.0d;
        }
    }

    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) {
        this.value = DoubleHelper.getIEEEDouble(data, pos);
        return 8;
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        byte[] data = new byte[9];
        data[0] = Token.DOUBLE.getCode();
        DoubleHelper.getIEEEBytes(this.value, data, 1);
        return data;
    }

    @Override // jxl.biff.formula.NumberValue
    public double getValue() {
        return this.value;
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
    }
}
