package jxl.write.biff;

import jxl.biff.DoubleHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class DeltaRecord extends WritableRecordData {
    private byte[] data;
    private double iterationValue;

    public DeltaRecord(double itval) {
        super(Type.DELTA);
        this.iterationValue = itval;
        this.data = new byte[8];
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        DoubleHelper.getIEEEBytes(this.iterationValue, this.data, 0);
        return this.data;
    }
}
