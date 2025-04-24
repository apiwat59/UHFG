package jxl.read.biff;

import jxl.WorkbookSettings;
import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.biff.StringHelper;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class HeaderRecord extends RecordData {
    private String header;
    private static Logger logger = Logger.getLogger(HeaderRecord.class);
    public static Biff7 biff7 = new Biff7();

    /* JADX INFO: Access modifiers changed from: private */
    static class Biff7 {
        private Biff7() {
        }
    }

    HeaderRecord(Record t, WorkbookSettings ws) {
        super(t);
        byte[] data = getRecord().getData();
        if (data.length == 0) {
            return;
        }
        int chars = IntegerHelper.getInt(data[0], data[1]);
        boolean unicode = data[2] == 1;
        if (unicode) {
            this.header = StringHelper.getUnicodeString(data, chars, 3);
        } else {
            this.header = StringHelper.getString(data, chars, 3, ws);
        }
    }

    HeaderRecord(Record t, WorkbookSettings ws, Biff7 dummy) {
        super(t);
        byte[] data = getRecord().getData();
        if (data.length == 0) {
            return;
        }
        int chars = data[0];
        this.header = StringHelper.getString(data, chars, 1, ws);
    }

    String getHeader() {
        return this.header;
    }
}
