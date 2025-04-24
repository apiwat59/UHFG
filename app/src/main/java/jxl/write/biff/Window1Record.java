package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class Window1Record extends WritableRecordData {
    private byte[] data;
    private int selectedSheet;

    public Window1Record(int selSheet) {
        super(Type.WINDOW1);
        this.selectedSheet = selSheet;
        byte[] bArr = {104, 1, 14, 1, 92, 58, -66, 35, 56, 0, 0, 0, 0, 0, 1, 0, 88, 2};
        this.data = bArr;
        IntegerHelper.getTwoBytes(selSheet, bArr, 10);
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        return this.data;
    }
}
