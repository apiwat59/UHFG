package jxl.write.biff;

import jxl.Workbook;
import jxl.biff.StringHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class WriteAccessRecord extends WritableRecordData {
    private static final String authorString = "Java Excel API";
    private byte[] data;
    private String userName;

    public WriteAccessRecord(String userName) {
        super(Type.WRITEACCESS);
        String astring;
        this.data = new byte[112];
        if (userName != null) {
            astring = userName;
        } else {
            astring = "Java Excel API v" + Workbook.getVersion();
        }
        StringHelper.getBytes(astring, this.data, 0);
        int i = astring.length();
        while (true) {
            byte[] bArr = this.data;
            if (i < bArr.length) {
                bArr[i] = 32;
                i++;
            } else {
                return;
            }
        }
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
