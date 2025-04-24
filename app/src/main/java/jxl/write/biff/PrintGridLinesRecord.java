package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class PrintGridLinesRecord extends WritableRecordData {
    private byte[] data;
    private boolean printGridLines;

    public PrintGridLinesRecord(boolean pgl) {
        super(Type.PRINTGRIDLINES);
        this.printGridLines = pgl;
        byte[] bArr = new byte[2];
        this.data = bArr;
        if (pgl) {
            bArr[0] = 1;
        }
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
