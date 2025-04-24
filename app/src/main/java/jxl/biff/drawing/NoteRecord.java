package jxl.biff.drawing;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;
import jxl.common.Logger;
import jxl.read.biff.Record;

/* loaded from: classes.dex */
public class NoteRecord extends WritableRecordData {
    private static Logger logger = Logger.getLogger(NoteRecord.class);
    private int column;
    private byte[] data;
    private int objectId;
    private int row;

    public NoteRecord(Record t) {
        super(t);
        byte[] data = getRecord().getData();
        this.data = data;
        this.row = IntegerHelper.getInt(data[0], data[1]);
        byte[] bArr = this.data;
        this.column = IntegerHelper.getInt(bArr[2], bArr[3]);
        byte[] bArr2 = this.data;
        this.objectId = IntegerHelper.getInt(bArr2[6], bArr2[7]);
    }

    public NoteRecord(byte[] d) {
        super(Type.NOTE);
        this.data = d;
    }

    public NoteRecord(int c, int r, int id) {
        super(Type.NOTE);
        this.row = r;
        this.column = c;
        this.objectId = id;
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] bArr = this.data;
        if (bArr != null) {
            return bArr;
        }
        byte[] bArr2 = new byte["".length() + 8 + 4];
        this.data = bArr2;
        IntegerHelper.getTwoBytes(this.row, bArr2, 0);
        IntegerHelper.getTwoBytes(this.column, this.data, 2);
        IntegerHelper.getTwoBytes(this.objectId, this.data, 6);
        IntegerHelper.getTwoBytes("".length(), this.data, 8);
        return this.data;
    }

    int getRow() {
        return this.row;
    }

    int getColumn() {
        return this.column;
    }

    public int getObjectId() {
        return this.objectId;
    }
}
