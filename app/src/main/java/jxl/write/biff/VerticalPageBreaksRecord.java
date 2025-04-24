package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class VerticalPageBreaksRecord extends WritableRecordData {
    private int[] columnBreaks;

    public VerticalPageBreaksRecord(int[] breaks) {
        super(Type.VERTICALPAGEBREAKS);
        this.columnBreaks = breaks;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        int[] iArr = this.columnBreaks;
        byte[] data = new byte[(iArr.length * 6) + 2];
        IntegerHelper.getTwoBytes(iArr.length, data, 0);
        int pos = 2;
        int i = 0;
        while (true) {
            int[] iArr2 = this.columnBreaks;
            if (i < iArr2.length) {
                IntegerHelper.getTwoBytes(iArr2[i], data, pos);
                IntegerHelper.getTwoBytes(255, data, pos + 4);
                pos += 6;
                i++;
            } else {
                return data;
            }
        }
    }
}
