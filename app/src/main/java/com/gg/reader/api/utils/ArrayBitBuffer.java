package com.gg.reader.api.utils;

/* loaded from: classes.dex */
public class ArrayBitBuffer extends SimpleBitBuffer {
    private byte[] bytes;

    protected ArrayBitBuffer(byte[] bytes, int limit, int offset) {
        this.bytes = bytes;
        this.limit = limit;
        this.offset = offset;
        this.size = limit;
    }

    protected ArrayBitBuffer(int bits) {
        this.bytes = new byte[(int) ((bits + (8 - (bits % 8))) / 8)];
        this.limit = bits;
        this.size = this.limit;
    }

    protected ArrayBitBuffer(byte[] bytes) {
        this.bytes = bytes;
        this.limit = bytes.length * 8;
        this.size = this.limit;
    }

    @Override // com.gg.reader.api.utils.SimpleBitBuffer
    protected byte rawGet(int index) {
        return this.bytes[index];
    }

    @Override // com.gg.reader.api.utils.SimpleBitBuffer
    protected void rawSet(int index, byte value) {
        this.bytes[index] = value;
    }

    @Override // com.gg.reader.api.utils.SimpleBitBuffer
    protected int rawLength() {
        return this.limit;
    }

    @Override // com.gg.reader.api.utils.SimpleBitBuffer, com.gg.reader.api.utils.BitBuffer
    public int limit() {
        return this.limit;
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public BitBuffer slice() {
        return new ArrayBitBuffer(this.bytes, size() - position(), this.offset + position());
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public BitBuffer slice(int start, int length) {
        return new ArrayBitBuffer(this.bytes, Math.min(length, size() - start), this.offset + start);
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public BitBuffer slice(int start) {
        return slice(start, size() - start);
    }
}
