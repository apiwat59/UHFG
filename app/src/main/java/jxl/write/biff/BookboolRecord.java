package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class BookboolRecord extends WritableRecordData {
    private byte[] data;
    private boolean externalLink;

    public BookboolRecord(boolean extlink) {
        super(Type.BOOKBOOL);
        this.externalLink = extlink;
        byte[] bArr = new byte[2];
        this.data = bArr;
        if (!extlink) {
            IntegerHelper.getTwoBytes(1, bArr, 0);
        }
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
