package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class IterationRecord extends WritableRecordData {
    private byte[] data;
    private boolean iterate;

    public IterationRecord(boolean it) {
        super(Type.ITERATION);
        this.iterate = it;
        byte[] bArr = new byte[2];
        this.data = bArr;
        if (it) {
            bArr[0] = 1;
        }
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
