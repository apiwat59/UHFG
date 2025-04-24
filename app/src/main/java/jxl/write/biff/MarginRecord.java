package jxl.write.biff;

import jxl.biff.DoubleHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
abstract class MarginRecord extends WritableRecordData {
    private double margin;

    public MarginRecord(Type t, double v) {
        super(t);
        this.margin = v;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] data = new byte[8];
        DoubleHelper.getIEEEBytes(this.margin, data, 0);
        return data;
    }
}
