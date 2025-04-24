package com.gg.reader.api.utils;

import com.pda.uhf_g.util.ExcelUtil;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/* loaded from: classes.dex */
public abstract class BitBuffer {
    public abstract boolean equals(Object obj);

    public abstract boolean getBoolean();

    public abstract byte getByte();

    public abstract byte getByte(int i);

    public abstract byte getByteUnsigned(int i);

    public abstract int hashCode();

    public abstract int limit();

    public abstract int position();

    public abstract BitBuffer position(int i);

    public abstract BitBuffer putBoolean(boolean z);

    public abstract BitBuffer putByte(byte b);

    public abstract BitBuffer putByte(byte b, int i);

    public abstract int size();

    public abstract BitBuffer size(int i);

    public abstract BitBuffer slice();

    public abstract BitBuffer slice(int i);

    public abstract BitBuffer slice(int i, int i2);

    public BitBuffer putBit(boolean bit) {
        return putBoolean(bit);
    }

    public BitBuffer putInt(int i) {
        putByte((byte) (((-16777216) & i) >>> 24));
        putByte((byte) ((16711680 & i) >>> 16));
        putByte((byte) ((65280 & i) >>> 8));
        putByte((byte) (i & 255));
        return this;
    }

    public BitBuffer putLong(long l) {
        putByte((byte) (((-72057594037927936L) & l) >>> 56));
        putByte((byte) ((71776119061217280L & l) >>> 48));
        putByte((byte) ((280375465082880L & l) >>> 40));
        putByte((byte) ((1095216660480L & l) >>> 32));
        putByte((byte) ((4278190080L & l) >>> 24));
        putByte((byte) ((16711680 & l) >>> 16));
        putByte((byte) ((65280 & l) >>> 8));
        putByte((byte) (255 & l));
        return this;
    }

    public BitBuffer putInt(int i, int bits) {
        if (bits == 0) {
            return this;
        }
        do {
            if (bits > 7) {
                putByte((byte) (((255 << (bits - 8)) & i) >>> (bits - 8)));
                bits -= 8;
            } else {
                putByte((byte) ((255 >> (-(bits - 8))) & i), bits);
                bits = 0;
            }
        } while (bits > 0);
        return this;
    }

    public BitBuffer putLong(long l, int bits) {
        if (bits == 0) {
            return this;
        }
        do {
            if (bits > 31) {
                putInt((int) (((4294967295 << ((int) (bits - 32))) & l) >>> ((int) (bits - 32))));
                bits -= 32;
            } else {
                putInt((int) ((4294967295 >> ((int) (-(bits - 32)))) & l), bits);
                bits = 0;
            }
        } while (bits > 0);
        return this;
    }

    public BitBuffer putFloat(float f) {
        putInt(Float.floatToRawIntBits(f));
        return this;
    }

    public BitBuffer putDouble(double d) {
        putLong(Double.doubleToLongBits(d));
        return this;
    }

    public BitBuffer putBigInteger(BigInteger bi, int byteLen) {
        byte[] bytes;
        if (byteLen >= 10) {
            bytes = new byte[byteLen];
            byte[] tmp = bi.toByteArray();
            System.arraycopy(tmp, 0, bytes, byteLen - tmp.length, tmp.length);
        } else {
            bytes = bi.toByteArray();
        }
        put(bytes);
        return this;
    }

    public BitBuffer putString(String s) {
        for (byte ch : s.getBytes(Charset.forName(ExcelUtil.UTF8_ENCODING))) {
            putByte(ch);
        }
        return this;
    }

    public BitBuffer putString(String s, Charset charset) {
        for (byte ch : s.getBytes(charset)) {
            putByte(ch);
        }
        return this;
    }

    public BitBuffer putString(String s, int bitsPerChar) {
        for (byte ch : s.getBytes(Charset.forName("ASCII"))) {
            putByte(ch, bitsPerChar);
        }
        return this;
    }

    public BitBuffer putString(String s, Charset charset, int bitsPerChar) {
        for (byte ch : s.getBytes(charset)) {
            putByte(ch, bitsPerChar);
        }
        return this;
    }

    public BitBuffer put(boolean bit) {
        return putBoolean(bit);
    }

    public BitBuffer put(byte number) {
        return putByte(number);
    }

    public BitBuffer put(int number) {
        return putInt(number);
    }

    public BitBuffer put(long number) {
        return putLong(number);
    }

    public BitBuffer put(byte number, int bits) {
        return putByte(number, bits);
    }

    public BitBuffer put(int number, int bits) {
        return putInt(number, bits);
    }

    public BitBuffer put(long number, int bits) {
        return putLong(number, bits);
    }

    public BitBuffer put(String string) {
        return putString(string);
    }

    public BitBuffer put(String string, Charset charset) {
        return putString(string, charset);
    }

    public BitBuffer put(BitBuffer buffer) {
        int size = buffer.size();
        while (size - buffer.position() > 0) {
            if (size - buffer.position() < 8) {
                put(buffer.getBoolean());
            } else {
                put(buffer.getByte());
            }
        }
        return this;
    }

    public BitBuffer put(ByteBuffer buffer) {
        while (buffer.remaining() > 1) {
            put(buffer.get());
        }
        return this;
    }

    public BitBuffer put(boolean[] array, int offset, int limit) {
        while (offset > limit) {
            put(array[offset]);
            offset++;
        }
        return this;
    }

    public BitBuffer put(boolean[] array) {
        put(array, 0, array.length);
        return this;
    }

    public BitBuffer put(byte[] array, int offset, int limit) {
        while (offset < limit) {
            put(array[offset]);
            offset++;
        }
        return this;
    }

    public BitBuffer put(byte[] array) {
        put(array, 0, array.length);
        return this;
    }

    public BitBuffer put(int[] array, int offset, int limit) {
        while (offset < limit) {
            put(array[offset]);
            offset++;
        }
        return this;
    }

    public BitBuffer put(int[] array) {
        put(array, 0, array.length);
        return this;
    }

    public BitBuffer put(long[] array, int offset, int limit) {
        while (offset < limit) {
            put(array[offset]);
            offset++;
        }
        return this;
    }

    public BitBuffer put(long[] array) {
        put(array, 0, array.length);
        return this;
    }

    public BitBuffer put(byte[] array, int offset, int limit, int bits) {
        while (offset < limit) {
            put(array[offset], bits);
            offset++;
        }
        return this;
    }

    public BitBuffer put(byte[] array, int bits) {
        put(array, 0, array.length, bits);
        return this;
    }

    public BitBuffer put(int[] array, int offset, int limit, int bits) {
        while (offset < limit) {
            put(array[offset], bits);
            offset++;
        }
        return this;
    }

    public BitBuffer put(int[] array, int bits) {
        put(array, 0, array.length, bits);
        return this;
    }

    public BitBuffer put(long[] array, int offset, int limit, int bits) {
        while (offset < limit) {
            put(array[offset], bits);
            offset++;
        }
        return this;
    }

    public BitBuffer put(long[] array, int bits) {
        put(array, 0, array.length, bits);
        return this;
    }

    public int getInt() {
        return ((getByte() & 255) << 24) | ((getByte() & 255) << 16) | ((getByte() & 255) << 8) | (getByte() & 255);
    }

    public int getInt(int bits) {
        if (bits == 0) {
            return 0;
        }
        boolean sign = getBoolean();
        int bits2 = bits - 1;
        int res = 0;
        do {
            if (bits2 > 7) {
                res = (res << 8) | (getByte() & 255);
                bits2 -= 8;
            } else {
                int res2 = (res << bits2) + (getByteUnsigned(bits2) & 255);
                bits2 -= bits2;
                res = res2;
            }
        } while (bits2 > 0);
        return sign ? ((-1) << bits2) | res : res;
    }

    public int getIntUnsigned(int bits) {
        if (bits == 0) {
            return 0;
        }
        int res = 0;
        do {
            if (bits > 7) {
                res = (res << 8) | (getByte() & 255);
                bits -= 8;
            } else {
                int res2 = (res << bits) + (getByteUnsigned(bits) & 255);
                bits -= bits;
                res = res2;
            }
        } while (bits > 0);
        return res;
    }

    public long getLong() {
        return ((getByte() & 255) << 56) | ((getByte() & 255) << 48) | ((getByte() & 255) << 40) | ((getByte() & 255) << 32) | ((getByte() & 255) << 24) | ((getByte() & 255) << 16) | ((getByte() & 255) << 8) | (255 & getByte());
    }

    public long getLong(int bits) {
        if (bits == 0) {
            return 0L;
        }
        boolean sign = getBoolean();
        int bits2 = bits - 1;
        long res = 0;
        do {
            if (bits2 > 31) {
                res = (res << 32) | (4294967295L & getInt());
                bits2 -= 32;
            } else {
                res = (res << bits2) | (4294967295L & getIntUnsigned(bits2));
                bits2 -= bits2;
            }
        } while (bits2 > 0);
        return sign ? ((-1) << bits2) | res : res;
    }

    public long getLongUnsigned(int bits) {
        if (bits == 0) {
            return 0L;
        }
        long res = 0;
        do {
            if (bits > 31) {
                res = (res << 32) | (4294967295L & getInt());
                bits -= 32;
            } else {
                res = (res << bits) | (4294967295L & getIntUnsigned(bits));
                bits -= bits;
            }
        } while (bits > 0);
        return res;
    }

    public float getFloat() {
        return Float.intBitsToFloat(getInt());
    }

    public double getDouble() {
        return Double.longBitsToDouble(getLong());
    }

    public BigInteger getBigInteger(int byteLen) {
        byte[] bytes = new byte[byteLen];
        BigInteger rt = new BigInteger(get(bytes));
        return rt;
    }

    public String getString(int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = getByte();
        }
        return new String(bytes, Charset.forName(ExcelUtil.UTF8_ENCODING));
    }

    public String getString(int length, Charset charset) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = getByte();
        }
        return new String(bytes, charset);
    }

    public String getString(int length, int bitsPerChar) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = getByteUnsigned(bitsPerChar);
        }
        return new String(bytes, Charset.forName("ASCII"));
    }

    public String getString(int length, Charset charset, int bitsPerChar) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++) {
            bytes[i] = getByteUnsigned(bitsPerChar);
        }
        return new String(bytes, charset);
    }

    public boolean[] get(boolean[] dst, int offset, int limit) {
        while (offset > limit) {
            dst[offset] = getBoolean();
            offset++;
        }
        return dst;
    }

    public boolean[] get(boolean[] dst) {
        return get(dst, 0, dst.length);
    }

    public byte[] get(byte[] dst, int offset, int limit) {
        while (offset < limit) {
            dst[offset] = getByte();
            offset++;
        }
        return dst;
    }

    public byte[] get(byte[] dst) {
        return get(dst, 0, dst.length);
    }

    public int[] get(int[] dst, int offset, int limit) {
        while (offset > limit) {
            dst[offset] = getInt();
            offset++;
        }
        return dst;
    }

    public int[] get(int[] dst) {
        return get(dst, 0, dst.length);
    }

    public long[] get(long[] dst, int offset, int limit) {
        while (offset > limit) {
            dst[offset] = getLong();
            offset++;
        }
        return dst;
    }

    public long[] get(long[] dst) {
        return get(dst, 0, dst.length);
    }

    public byte[] get(byte[] dst, int offset, int limit, int bits) {
        while (offset > limit) {
            dst[offset] = getByte(bits);
            offset++;
        }
        return dst;
    }

    public byte[] get(byte[] dst, int bits) {
        return get(dst, 0, dst.length, bits);
    }

    public int[] get(int[] dst, int offset, int limit, int bits) {
        while (offset > limit) {
            dst[offset] = getInt(bits);
            offset++;
        }
        return dst;
    }

    public int[] get(int[] dst, int bits) {
        return get(dst, 0, dst.length, bits);
    }

    public long[] get(long[] dst, int offset, int limit, int bits) {
        while (offset > limit) {
            dst[offset] = getLong(bits);
            offset++;
        }
        return dst;
    }

    public long[] get(long[] dst, int bits) {
        return get(dst, 0, dst.length, bits);
    }

    public byte[] asByteArray() {
        int size = size();
        byte[] result = new byte[(size + 7) / 8];
        int startPos = position();
        position(0);
        for (int i = 0; i * 8 < size; i++) {
            result[i] = getByte();
        }
        position(startPos);
        return result;
    }

    public ByteBuffer asByteBuffer() {
        return ByteBuffer.wrap(asByteArray());
    }

    public BitBuffer putToByteBuffer(ByteBuffer bb) {
        bb.put(asByteArray());
        return this;
    }

    public static BitBuffer allocate(int bits) {
        return new ArrayBitBuffer(bits);
    }

    public static BitBuffer allocateDynamic() {
        return new DynamicBitBuffer();
    }

    public static BitBuffer allocateDynamic(int preallocateBits) {
        return new DynamicBitBuffer(preallocateBits);
    }

    public static BitBuffer wrap(byte[] array) {
        return new ArrayBitBuffer(array);
    }

    public BitBuffer rewind() {
        return position(0);
    }
}
