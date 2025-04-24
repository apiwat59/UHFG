package jxl.biff.drawing;

/* loaded from: classes.dex */
class Chunk {
    private byte[] data;
    private int length;
    private int pos;
    private ChunkType type;

    public Chunk(int p, int l, ChunkType ct, byte[] d) {
        this.pos = p;
        this.length = l;
        this.type = ct;
        byte[] bArr = new byte[l];
        this.data = bArr;
        System.arraycopy(d, p, bArr, 0, l);
    }

    public byte[] getData() {
        return this.data;
    }
}
