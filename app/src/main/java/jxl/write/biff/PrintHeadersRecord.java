package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class PrintHeadersRecord extends WritableRecordData {
    private byte[] data;
    private boolean printHeaders;

    public PrintHeadersRecord(boolean ph) {
        super(Type.PRINTHEADERS);
        this.printHeaders = ph;
        byte[] bArr = new byte[2];
        this.data = bArr;
        if (ph) {
            bArr[0] = 1;
        }
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
