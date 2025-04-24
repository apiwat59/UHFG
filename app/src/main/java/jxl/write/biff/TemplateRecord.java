package jxl.write.biff;

import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class TemplateRecord extends WritableRecordData {
    public TemplateRecord() {
        super(Type.TEMPLATE);
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return new byte[0];
    }
}
