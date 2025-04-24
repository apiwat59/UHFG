package jxl.read.biff;

import jxl.biff.RecordData;

/* loaded from: classes.dex */
class PrintHeadersRecord extends RecordData {
    private boolean printHeaders;

    public PrintHeadersRecord(Record ph) {
        super(ph);
        byte[] data = ph.getData();
        this.printHeaders = data[0] == 1;
    }

    public boolean getPrintHeaders() {
        return this.printHeaders;
    }
}
