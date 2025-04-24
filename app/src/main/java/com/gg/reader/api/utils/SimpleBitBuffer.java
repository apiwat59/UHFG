package com.gg.reader.api.utils;

import androidx.core.internal.view.SupportMenu;
import androidx.core.view.MotionEventCompat;

/* loaded from: classes.dex */
public abstract class SimpleBitBuffer extends BitBuffer {
    protected int limit;
    protected int offset;
    private int position;
    protected int size;

    protected abstract byte rawGet(int i);

    protected abstract int rawLength();

    protected abstract void rawSet(int i, byte b);

    protected void advance(int p, boolean write) {
        int i = this.position + p;
        this.position = i;
        if (write && i > this.size) {
            this.size = i;
        }
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public BitBuffer putBoolean(boolean b) {
        int pos = this.offset + this.position;
        advance(1, true);
        rawSet(pos / 8, (byte) ((rawGet(pos / 8) & ((128 >>> (pos % 8)) ^ (-1))) + ((b ? 128 : 0) >>> (pos % 8))));
        return this;
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public BitBuffer putByte(byte b) {
        int pos = this.offset + this.position;
        advance(8, true);
        byte old = (byte) (rawGet(pos / 8) & ((byte) ((255 >>> (pos % 8)) ^ (-1))));
        rawSet(pos / 8, (byte) (((byte) ((b & 255) >>> (pos % 8))) | old));
        if (pos % 8 > 0) {
            rawSet((pos / 8) + 1, (byte) ((b & 255) << (8 - (pos % 8))));
        }
        return this;
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public BitBuffer putByte(byte b, int bits) {
        int pos = this.offset + this.position;
        advance(bits, true);
        byte b2 = (byte) ((((255 >>> (8 - bits)) & b) << (8 - bits)) & 255);
        rawSet(pos / 8, (byte) (((rawGet(pos / 8) & (255 << (8 - (pos % 8)))) | ((b2 & 255) >>> (pos % 8))) & 255));
        if (8 - (pos % 8) < bits) {
            rawSet((pos / 8) + 1, (byte) (((b2 & 255) << (8 - (pos % 8))) & 255));
        }
        return this;
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public boolean getBoolean() {
        int pos = this.offset + this.position;
        advance(1, false);
        return (rawGet(pos / 8) & (128 >>> (pos % 8))) > 0;
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public byte getByte() {
        int pos = this.offset + this.position;
        advance(8, false);
        byte b = (byte) ((rawGet(pos / 8) & (255 >>> (pos % 8))) << (pos % 8));
        return pos % 8 > 0 ? (byte) (((rawGet((pos / 8) + 1) & 255) >>> (8 - (pos % 8))) | b) : b;
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public byte getByte(int bits) {
        int pos = this.offset + this.position;
        advance(bits, false);
        boolean sign = (rawGet(pos / 8) & (128 >>> (pos % 8))) > 0;
        int pos2 = pos + 1;
        int bits2 = bits - 1;
        short mask = (short) (((MotionEventCompat.ACTION_POINTER_INDEX_MASK << (8 - bits2)) & SupportMenu.USER_MASK) >>> (pos2 % 8));
        byte b = (byte) ((((65280 & mask) >>> 8) & rawGet(pos2 / 8)) << (pos2 % 8));
        if (8 - (pos2 % 8) < bits2) {
            b = (byte) ((((rawGet((pos2 / 8) + 1) & (mask & 255)) & 255) >>> (bits2 - (((pos2 % 8) + bits2) - 8))) | b);
        }
        byte b2 = (byte) ((b & 255) >>> (8 - bits2));
        return (byte) (sign ? ((255 << bits2) & 255) | b2 : b2);
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public byte getByteUnsigned(int bits) {
        int pos = this.offset + this.position;
        advance(bits, false);
        short mask = (short) (((MotionEventCompat.ACTION_POINTER_INDEX_MASK << (8 - bits)) & SupportMenu.USER_MASK) >>> (pos % 8));
        byte b = (byte) ((((65280 & mask) >>> 8) & rawGet(pos / 8)) << (pos % 8));
        if (8 - (pos % 8) < bits) {
            b = (byte) ((((rawGet((pos / 8) + 1) & (mask & 255)) & 255) >>> (bits - (((pos % 8) + bits) - 8))) | b);
        }
        return (byte) ((b & 255) >>> (8 - bits));
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public int size() {
        return this.size;
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public BitBuffer size(int size) {
        this.size = size;
        return this;
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public int limit() {
        return rawLength();
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public int position() {
        return this.position;
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public int hashCode() {
        int result = (1 * 31) + size();
        return result;
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof BitBuffer)) {
            return false;
        }
        BitBuffer other = (BitBuffer) obj;
        int size = size();
        if (size() != other.size()) {
            return false;
        }
        int mark = position();
        int otherMark = other.position();
        int i = 0;
        while (i < size) {
            if (size - i > 7) {
                try {
                    byte a = getByte();
                    byte b = other.getByte();
                    if (a != b) {
                        return false;
                    }
                    i += 7;
                } finally {
                    position(mark);
                    other.position(otherMark);
                }
            } else {
                i++;
                if (getBoolean() != other.getBoolean()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override // com.gg.reader.api.utils.BitBuffer
    public BitBuffer position(int newPosition) {
        this.position = newPosition;
        return this;
    }

    public int offset() {
        return this.offset;
    }
}
