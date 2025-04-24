package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class EOFRecord extends WritableRecordData {
    public EOFRecord() {
        super(Type.EOF);
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return new byte[0];
    }
}
