package jxl.read.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.common.Logger;

/* loaded from: classes.dex */
class WindowProtectedRecord extends RecordData {
    private static Logger logger = Logger.getLogger(WindowProtectedRecord.class);
    private boolean windowProtected;

    public WindowProtectedRecord(Record t) {
        super(t);
        byte[] data = t.getData();
        int mode = IntegerHelper.getInt(data[0], data[1]);
        this.windowProtected = mode == 1;
    }

    public boolean getWindowProtected() {
        return this.windowProtected;
    }
}
