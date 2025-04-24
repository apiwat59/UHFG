package jxl.read.biff;

import jxl.biff.RecordData;
import jxl.common.Logger;

/* loaded from: classes.dex */
class Excel9FileRecord extends RecordData {
    private static Logger logger = Logger.getLogger(Excel9FileRecord.class);
    private boolean excel9file;

    public Excel9FileRecord(Record t) {
        super(t);
        this.excel9file = true;
    }

    public boolean getExcel9File() {
        return this.excel9file;
    }
}
