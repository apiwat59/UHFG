package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class VerticalCentreRecord extends WritableRecordData {
    private boolean centre;
    private byte[] data;

    public VerticalCentreRecord(boolean ce) {
        super(Type.VCENTER);
        this.centre = ce;
        byte[] bArr = new byte[2];
        this.data = bArr;
        if (ce) {
            bArr[0] = 1;
        }
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
