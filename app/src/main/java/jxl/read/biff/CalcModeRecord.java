package jxl.read.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.common.Logger;

/* loaded from: classes.dex */
class CalcModeRecord extends RecordData {
    private static Logger logger = Logger.getLogger(CalcModeRecord.class);
    private boolean automatic;

    public CalcModeRecord(Record t) {
        super(t);
        byte[] data = t.getData();
        int mode = IntegerHelper.getInt(data[0], data[1]);
        this.automatic = mode == 1;
    }

    public boolean isAutomatic() {
        return this.automatic;
    }
}
