package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class SaveRecalcRecord extends WritableRecordData {
    private byte[] data;
    private boolean recalc;

    public SaveRecalcRecord(boolean r) {
        super(Type.SAVERECALC);
        this.recalc = r;
        byte[] bArr = new byte[2];
        this.data = bArr;
        if (r) {
            bArr[0] = 1;
        }
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
