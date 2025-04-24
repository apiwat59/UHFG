package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class HideobjRecord extends WritableRecordData {
    private byte[] data;
    private int hidemode;

    public HideobjRecord(int newHideMode) {
        super(Type.HIDEOBJ);
        this.hidemode = newHideMode;
        byte[] bArr = new byte[2];
        this.data = bArr;
        IntegerHelper.getTwoBytes(newHideMode, bArr, 0);
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
