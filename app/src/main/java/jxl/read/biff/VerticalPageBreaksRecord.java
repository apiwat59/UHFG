package jxl.read.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.common.Logger;

/* loaded from: classes.dex */
class VerticalPageBreaksRecord extends RecordData {
    public static Biff7 biff7 = new Biff7();
    private int[] columnBreaks;
    private final Logger logger;

    /* JADX INFO: Access modifiers changed from: private */
    static class Biff7 {
        private Biff7() {
        }
    }

    public VerticalPageBreaksRecord(Record t) {
        super(t);
        this.logger = Logger.getLogger(VerticalPageBreaksRecord.class);
        byte[] data = t.getData();
        int numbreaks = IntegerHelper.getInt(data[0], data[1]);
        int pos = 2;
        this.columnBreaks = new int[numbreaks];
        for (int i = 0; i < numbreaks; i++) {
            this.columnBreaks[i] = IntegerHelper.getInt(data[pos], data[pos + 1]);
            pos += 6;
        }
    }

    public VerticalPageBreaksRecord(Record t, Biff7 biff72) {
        super(t);
        this.logger = Logger.getLogger(VerticalPageBreaksRecord.class);
        byte[] data = t.getData();
        int numbreaks = IntegerHelper.getInt(data[0], data[1]);
        int pos = 2;
        this.columnBreaks = new int[numbreaks];
        for (int i = 0; i < numbreaks; i++) {
            this.columnBreaks[i] = IntegerHelper.getInt(data[pos], data[pos + 1]);
            pos += 2;
        }
    }

    public int[] getColumnBreaks() {
        return this.columnBreaks;
    }
}
