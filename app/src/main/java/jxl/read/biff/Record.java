package jxl.read.biff;

import java.util.ArrayList;
import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.common.Logger;

/* loaded from: classes.dex */
public final class Record {
    private static final Logger logger = Logger.getLogger(Record.class);
    private int code;
    private ArrayList continueRecords;
    private byte[] data;
    private int dataPos;
    private File file;
    private int length;
    private Type type;

    Record(byte[] d, int offset, File f) {
        this.code = IntegerHelper.getInt(d[offset], d[offset + 1]);
        this.length = IntegerHelper.getInt(d[offset + 2], d[offset + 3]);
        this.file = f;
        f.skip(4);
        this.dataPos = f.getPos();
        this.file.skip(this.length);
        this.type = Type.getType(this.code);
    }

    public Type getType() {
        return this.type;
    }

    public int getLength() {
        return this.length;
    }

    public byte[] getData() {
        if (this.data == null) {
            this.data = this.file.read(this.dataPos, this.length);
        }
        ArrayList arrayList = this.continueRecords;
        if (arrayList != null) {
            int size = 0;
            byte[][] contData = new byte[arrayList.size()][];
            for (int i = 0; i < this.continueRecords.size(); i++) {
                Record r = (Record) this.continueRecords.get(i);
                contData[i] = r.getData();
                size += contData[i].length;
            }
            byte[] bArr = this.data;
            byte[] d3 = new byte[bArr.length + size];
            System.arraycopy(bArr, 0, d3, 0, bArr.length);
            int pos = this.data.length;
            for (byte[] d2 : contData) {
                System.arraycopy(d2, 0, d3, pos, d2.length);
                pos += d2.length;
            }
            this.data = d3;
        }
        return this.data;
    }

    public int getCode() {
        return this.code;
    }

    void setType(Type t) {
        this.type = t;
    }

    public void addContinueRecord(Record d) {
        if (this.continueRecords == null) {
            this.continueRecords = new ArrayList();
        }
        this.continueRecords.add(d);
    }
}
