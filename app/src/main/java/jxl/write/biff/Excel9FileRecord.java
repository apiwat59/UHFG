package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class Excel9FileRecord extends WritableRecordData {
    public Excel9FileRecord() {
        super(Type.EXCEL9FILE);
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return new byte[0];
    }
}
