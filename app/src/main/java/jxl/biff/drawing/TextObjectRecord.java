package jxl.biff.drawing;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;
import jxl.common.Logger;
import jxl.read.biff.Record;

/* loaded from: classes.dex */
public class TextObjectRecord extends WritableRecordData {
    private static Logger logger = Logger.getLogger(TextObjectRecord.class);
    private byte[] data;
    private int textLength;

    TextObjectRecord(String t) {
        super(Type.TXO);
        this.textLength = t.length();
    }

    public TextObjectRecord(Record t) {
        super(t);
        byte[] data = getRecord().getData();
        this.data = data;
        this.textLength = IntegerHelper.getInt(data[10], data[11]);
    }

    public TextObjectRecord(byte[] d) {
        super(Type.TXO);
        this.data = d;
    }

    public int getTextLength() {
        return this.textLength;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] bArr = this.data;
        if (bArr != null) {
            return bArr;
        }
        byte[] bArr2 = new byte[18];
        this.data = bArr2;
        int options = 0 | 2;
        IntegerHelper.getTwoBytes(options | 16 | 512, bArr2, 0);
        IntegerHelper.getTwoBytes(this.textLength, this.data, 10);
        IntegerHelper.getTwoBytes(16, this.data, 12);
        return this.data;
    }
}
