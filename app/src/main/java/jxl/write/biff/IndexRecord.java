package jxl.write.biff;

import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;

/* loaded from: classes.dex */
class IndexRecord extends WritableRecordData {
    private int blocks;
    private int bofPosition;
    private byte[] data;
    private int dataPos;
    private int rows;

    public IndexRecord(int pos, int r, int bl) {
        super(Type.INDEX);
        this.bofPosition = pos;
        this.rows = r;
        this.blocks = bl;
        this.data = new byte[(bl * 4) + 16];
        this.dataPos = 16;
    }

    @Override // jxl.biff.WritableRecordData
    protected byte[] getData() {
        IntegerHelper.getFourBytes(this.rows, this.data, 8);
        return this.data;
    }

    void addBlockPosition(int pos) {
        IntegerHelper.getFourBytes(pos - this.bofPosition, this.data, this.dataPos);
        this.dataPos += 4;
    }

    void setDataStartPosition(int pos) {
        IntegerHelper.getFourBytes(pos - this.bofPosition, this.data, 12);
    }
}
