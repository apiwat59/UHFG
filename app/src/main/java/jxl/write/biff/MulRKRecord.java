package jxl.write.biff;

import java.util.List;
import jxl.biff.IntegerHelper;
import jxl.biff.Type;
import jxl.biff.WritableRecordData;
import jxl.write.Number;

/* loaded from: classes.dex */
class MulRKRecord extends WritableRecordData {
    private int colFirst;
    private int colLast;
    private int[] rknumbers;
    private int row;
    private int[] xfIndices;

    public MulRKRecord(List numbers) {
        super(Type.MULRK);
        this.row = ((Number) numbers.get(0)).getRow();
        this.colFirst = ((Number) numbers.get(0)).getColumn();
        this.colLast = (r0 + numbers.size()) - 1;
        this.rknumbers = new int[numbers.size()];
        this.xfIndices = new int[numbers.size()];
        for (int i = 0; i < numbers.size(); i++) {
            this.rknumbers[i] = (int) ((Number) numbers.get(i)).getValue();
            this.xfIndices[i] = ((CellValue) numbers.get(i)).getXFIndex();
        }
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        byte[] data = new byte[(this.rknumbers.length * 6) + 6];
        IntegerHelper.getTwoBytes(this.row, data, 0);
        IntegerHelper.getTwoBytes(this.colFirst, data, 2);
        int pos = 4;
        byte[] bArr = new byte[4];
        for (int i = 0; i < this.rknumbers.length; i++) {
            IntegerHelper.getTwoBytes(this.xfIndices[i], data, pos);
            int rkValue = this.rknumbers[i] << 2;
            IntegerHelper.getFourBytes(rkValue | 2, data, pos + 2);
            pos += 6;
        }
        IntegerHelper.getTwoBytes(this.colLast, data, pos);
        return data;
    }
}
