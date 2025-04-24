package jxl.biff;

/* loaded from: classes.dex */
public class ByteArray {
    private static final int defaultGrowSize = 1024;
    private byte[] bytes;
    private int growSize;
    private int pos;

    public ByteArray() {
        this(1024);
    }

    public ByteArray(int gs) {
        this.growSize = gs;
        this.bytes = new byte[1024];
        this.pos = 0;
    }

    public void add(byte b) {
        checkSize(1);
        byte[] bArr = this.bytes;
        int i = this.pos;
        bArr[i] = b;
        this.pos = i + 1;
    }

    public void add(byte[] b) {
        checkSize(b.length);
        System.arraycopy(b, 0, this.bytes, this.pos, b.length);
        this.pos += b.length;
    }

    public byte[] getBytes() {
        int i = this.pos;
        byte[] returnArray = new byte[i];
        System.arraycopy(this.bytes, 0, returnArray, 0, i);
        return returnArray;
    }

    private void checkSize(int sz) {
        while (true) {
            int i = this.pos;
            int i2 = i + sz;
            byte[] bArr = this.bytes;
            if (i2 >= bArr.length) {
                byte[] newArray = new byte[bArr.length + this.growSize];
                System.arraycopy(bArr, 0, newArray, 0, i);
                this.bytes = newArray;
            } else {
                return;
            }
        }
    }
}
