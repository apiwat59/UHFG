package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class HorizontalPageBreaksRecord extends WritableRecordData {
    private int[] rowBreaks;

    public HorizontalPageBreaksRecord(int[] breaks) {
        super(Type.HORIZONTALPAGEBREAKS);
        this.rowBreaks = breaks;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        int[] iArr = this.rowBreaks;
        byte[] data = new byte[(iArr.length * 6) + 2];
        IntegerHelper.getTwoBytes(iArr.length, data, 0);
        int pos = 2;
        int i = 0;
        while (true) {
            int[] iArr2 = this.rowBreaks;
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
