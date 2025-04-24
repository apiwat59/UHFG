package jxl.write.biff;

import java.io.IOException;
import java.io.OutputStream;
import jxl.common.Logger;

/* loaded from: classes.dex */
class MemoryDataOutput implements ExcelDataOutput {
    private static Logger logger = Logger.getLogger(MemoryDataOutput.class);
    private byte[] data;
    private int growSize;
    private int pos = 0;

    public MemoryDataOutput(int initialSize, int gs) {
        this.data = new byte[initialSize];
        this.growSize = gs;
    }

    @Override // jxl.write.biff.ExcelDataOutput
    public void write(byte[] bytes) {
        while (true) {
            int i = this.pos;
            int length = bytes.length + i;
            byte[] bArr = this.data;
            if (length > bArr.length) {
                byte[] newdata = new byte[bArr.length + this.growSize];
                System.arraycopy(bArr, 0, newdata, 0, i);
                this.data = newdata;
            } else {
                System.arraycopy(bytes, 0, bArr, i, bytes.length);
                this.pos += bytes.length;
                return;
            }
        }
    }

    @Override // jxl.write.biff.ExcelDataOutput
    public int getPosition() {
        return this.pos;
    }

    @Override // jxl.write.biff.ExcelDataOutput
    public void setData(byte[] newdata, int pos) {
        System.arraycopy(newdata, 0, this.data, pos, newdata.length);
    }

    @Override // jxl.write.biff.ExcelDataOutput
    public void writeData(OutputStream out) throws IOException {
        out.write(this.data, 0, this.pos);
    }

    @Override // jxl.write.biff.ExcelDataOutput
    public void close() throws IOException {
    }
}
