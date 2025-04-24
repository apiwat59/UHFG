package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class SCLRecord extends WritableRecordData {
    private int zoomFactor;

    public SCLRecord(int zf) {
        super(Type.SCL);
        this.zoomFactor = zf;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] data = new byte[4];
        int numerator = this.zoomFactor;
        IntegerHelper.getTwoBytes(numerator, data, 0);
        IntegerHelper.getTwoBytes(100, data, 2);
        return data;
    }
}
