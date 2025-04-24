package jxl.read.biff;

import jxl.WorkbookSettings;
import jxl.biff.RecordData;
import jxl.biff.StringHelper;
import jxl.biff.Type;

/* loaded from: classes.dex */
class WriteAccessRecord extends RecordData {
    private String wauser;

    public WriteAccessRecord(Record t, boolean isBiff8, WorkbookSettings ws) {
        super(Type.WRITEACCESS);
        byte[] data = t.getData();
        if (isBiff8) {
            this.wauser = StringHelper.getUnicodeString(data, 56, 0);
        } else {
            int length = data[1];
            this.wauser = StringHelper.getString(data, length, 1, ws);
        }
    }

    public String getWriteAccess() {
        return this.wauser;
    }
}
