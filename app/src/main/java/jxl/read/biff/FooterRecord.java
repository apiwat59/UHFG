package jxl.read.biff;

import jxl.WorkbookSettings;
import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.biff.StringHelper;

/* loaded from: classes.dex */
public class FooterRecord extends RecordData {
    public static Biff7 biff7 = new Biff7();
    private String footer;

    /* JADX INFO: Access modifiers changed from: private */
    static class Biff7 {
        private Biff7() {
        }
    }

    FooterRecord(Record t, WorkbookSettings ws) {
        super(t);
        byte[] data = getRecord().getData();
        if (data.length == 0) {
            return;
        }
        int chars = IntegerHelper.getInt(data[0], data[1]);
        boolean unicode = data[2] == 1;
        if (unicode) {
            this.footer = StringHelper.getUnicodeString(data, chars, 3);
        } else {
            this.footer = StringHelper.getString(data, chars, 3, ws);
        }
    }

    FooterRecord(Record t, WorkbookSettings ws, Biff7 dummy) {
        super(t);
        byte[] data = getRecord().getData();
        if (data.length == 0) {
            return;
        }
        int chars = data[0];
        this.footer = StringHelper.getString(data, chars, 1, ws);
    }

    String getFooter() {
        return this.footer;
    }
}
