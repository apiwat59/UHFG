package com.gg.reader.api.utils;

/* loaded from: classes.dex */
public class DynamicBitBuffer extends SimpleBitBuffer {
    private static final int DEFAULT_CAPACITY = 256;
    private byte[] bytes;

    protected DynamicBitBuffer() {
        this.bytes = new byte[256];
    }

    protected DynamicBitBuffer(int initialCapacity) {
        this.bytes = new byte[toBytes(initialCapacity)];
    }

    private static int toBytes(int bits) {
        return ((8 - (bits % 8)) + bits) / 8;
    }

    @Override // com.gg.reader.api.utils.SimpleBitBuffer
    protected byte rawGet(int index) {
        if (index >= this.bytes.length) {
            ensureCapacity(index + 1);
        }
        return this.bytes[index];
    }

    private void ensureCapacity(int toBytes) {
        byte[] newBytes = new byte[toBytes];
        byte[] bArr = this.bytes;
        System.arraycopy(bArr, 0, newBytes, 0, bArr.length);
        this.bytes = newBytes;
    }

    @Override // com.gg.reader.api.utils.SimpleBitBuffer
    protected void rawSet(int index, byte value) {
        if (index >= this.bytes.length) {
            ensureCapacity(index + 1);
        }
        this.bytes[index] = value;
    }

    @Override // com.gg.reader.api.utils.SimpleBitBuffer
    protected int rawLength() {
        return this.bytes.length * 8;
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public BitBuffer slice() {
        return new ArrayBitBuffer(this.bytes, size() - position(), position());
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public BitBuffer slice(int start, int length) {
        return new ArrayBitBuffer(this.bytes, Math.min(length, size() - start), start);
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public BitBuffer slice(int start) {
        return slice(start, size() - start);
    }
}
