package jxl.write.biff;

import jxl.biff.StringHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;
import jxl.common.Logger;

/* loaded from: classes.dex */
class ExternalNameRecord extends WritableRecordData {
    Logger logger;
    private String name;

    public ExternalNameRecord(String n) {
        super(Type.EXTERNNAME);
        this.logger = Logger.getLogger(ExternalNameRecord.class);
        this.name = n;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] data = new byte[(this.name.length() * 2) + 12];
        data[6] = (byte) this.name.length();
        data[7] = 1;
        StringHelper.getUnicodeBytes(this.name, data, 8);
        int pos = (this.name.length() * 2) + 8;
        data[pos] = 2;
        data[pos + 1] = 0;
        data[pos + 2] = 28;
        data[pos + 3] = 23;
        return data;
    }
}
