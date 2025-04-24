package jxl.read.biff;

import jxl.WorkbookSettings;
import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.biff.StringHelper;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class ExternalNameRecord extends RecordData {
    private static Logger logger = Logger.getLogger(ExternalNameRecord.class);
    private boolean addInFunction;
    private String name;

    ExternalNameRecord(Record t, WorkbookSettings ws) {
        super(t);
        byte[] data = getRecord().getData();
        int options = IntegerHelper.getInt(data[0], data[1]);
        if (options == 0) {
            this.addInFunction = true;
        }
        if (!this.addInFunction) {
            return;
        }
        int length = data[6];
        boolean unicode = data[7] != 0;
        if (unicode) {
            this.name = StringHelper.getUnicodeString(data, length, 8);
        } else {
            this.name = StringHelper.getString(data, length, 8, ws);
        }
    }

    public boolean isAddInFunction() {
        return this.addInFunction;
    }

    public String getName() {
        return this.name;
    }
}
