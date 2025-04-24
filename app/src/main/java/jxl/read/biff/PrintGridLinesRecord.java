package jxl.read.biff;

import jxl.biff.RecordData;

/* loaded from: classes.dex */
class PrintGridLinesRecord extends RecordData {
    private boolean printGridLines;

    public PrintGridLinesRecord(Record pgl) {
        super(pgl);
        byte[] data = pgl.getData();
        this.printGridLines = data[0] == 1;
    }

    public boolean getPrintGridLines() {
        return this.printGridLines;
    }
}
