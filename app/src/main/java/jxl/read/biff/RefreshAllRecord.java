package jxl.read.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.common.Logger;

/* loaded from: classes.dex */
class RefreshAllRecord extends RecordData {
    private static Logger logger = Logger.getLogger(RefreshAllRecord.class);
    private boolean refreshAll;

    public RefreshAllRecord(Record t) {
        super(t);
        byte[] data = t.getData();
        int mode = IntegerHelper.getInt(data[0], data[1]);
        this.refreshAll = mode == 1;
    }

    public boolean getRefreshAll() {
        return this.refreshAll;
    }
}
