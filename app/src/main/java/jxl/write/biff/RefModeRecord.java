package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class RefModeRecord extends WritableRecordData {
    public RefModeRecord() {
        super(Type.REFMODE);
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] data = {1, 0};
        return data;
    }
}
