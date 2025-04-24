package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class GridSetRecord extends WritableRecordData {
    private byte[] data;
    private boolean gridSet;

    public GridSetRecord(boolean gs) {
        super(Type.GRIDSET);
        this.gridSet = gs;
        byte[] bArr = new byte[2];
        this.data = bArr;
        if (gs) {
            bArr[0] = 1;
        }
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
