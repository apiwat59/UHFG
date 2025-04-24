package jxl.read.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.RecordData;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class BOFRecord extends RecordData {
    private static final int Biff7 = 1280;
    private static final int Biff8 = 1536;
    private static final int Chart = 32;
    private static final int MacroSheet = 64;
    private static final int WorkbookGlobals = 5;
    private static final int Worksheet = 16;
    private static Logger logger = Logger.getLogger(BOFRecord.class);
    private int substreamType;
    private int version;

    BOFRecord(Record t) {
        super(t);
        byte[] data = getRecord().getData();
        this.version = IntegerHelper.getInt(data[0], data[1]);
        this.substreamType = IntegerHelper.getInt(data[2], data[3]);
    }

    public boolean isBiff8() {
        return this.version == Biff8;
    }

    public boolean isBiff7() {
        return this.version == Biff7;
    }

    boolean isWorkbookGlobals() {
        return this.substreamType == 5;
    }

    public boolean isWorksheet() {
        return this.substreamType == 16;
    }

    public boolean isMacroSheet() {
        return this.substreamType == 64;
    }

    public boolean isChart() {
        return this.substreamType == 32;
    }

    int getLength() {
        return getRecord().getLength();
    }
}
