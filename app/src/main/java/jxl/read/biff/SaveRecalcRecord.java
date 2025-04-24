package jxl.read.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.common.Logger;

/* loaded from: classes.dex */
class SaveRecalcRecord extends RecordData {
    private static Logger logger = Logger.getLogger(SaveRecalcRecord.class);
    private boolean recalculateOnSave;

    public SaveRecalcRecord(Record t) {
        super(t);
        byte[] data = t.getData();
        int mode = IntegerHelper.getInt(data[0], data[1]);
        this.recalculateOnSave = mode == 1;
    }

    public boolean getRecalculateOnSave() {
        return this.recalculateOnSave;
    }
}
