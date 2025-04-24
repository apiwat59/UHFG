package com.gg.reader.api.utils;

/* loaded from: classes.dex */
public class RingBuffer {
    private byte[] Buffer = null;
    private int DataCount = 0;
    private int DataStart = 0;
    private int DataEnd = 0;

    public byte[] getBuffer() {
        return this.Buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.Buffer = buffer;
    }

    public int getDataCount() {
        return this.DataCount;
    }

    public void setDataCount(int dataCount) {
        this.DataCount = dataCount;
    }

    public int getDataStart() {
        return this.DataStart;
    }

    public void setDataStart(int dataStart) {
        this.DataStart = dataStart;
    }

    public int getDataEnd() {
        return this.DataEnd;
    }

    public void setDataEnd(int dataEnd) {
        this.DataEnd = dataEnd;
    }

    public RingBuffer(int bufferSize) {
        setDataCount(0);
        setDataStart(0);
        setDataEnd(0);
        setBuffer(new byte[bufferSize]);
    }

    public byte Index(int index) throws Exception {
        if (index >= this.DataCount) {
            throw new Exception("环形缓冲区异常，索引溢出");
        }
        int i = this.DataStart;
        int i2 = i + index;
        byte[] bArr = this.Buffer;
        if (i2 < bArr.length) {
            return bArr[i + index];
        }
        return bArr[(i + index) - bArr.length];
    }

    public int GetReserveCount() {
        return this.Buffer.length - this.DataCount;
    }

    public void Clear() {
        this.DataCount = 0;
        this.DataStart = 0;
        this.DataEnd = 0;
    }

    public void Clear(int count) {
        int i = this.DataCount;
        if (count >= i) {
            this.DataCount = 0;
            this.DataStart = 0;
            this.DataEnd = 0;
            return;
        }
        int i2 = this.DataStart;
        int i3 = i2 + count;
        byte[] bArr = this.Buffer;
        if (i3 >= bArr.length) {
            this.DataStart = (i2 + count) - bArr.length;
        } else {
            this.DataStart = i2 + count;
        }
        this.DataCount = i - count;
    }

    public void WriteBuffer(byte[] buffer, int offset, int count) throws Exception {
        byte[] bArr = this.Buffer;
        int reserveCount = bArr.length - this.DataCount;
        if (reserveCount >= count) {
            int i = this.DataEnd;
            if (i + count < bArr.length) {
                System.arraycopy(buffer, offset, bArr, i, count);
                this.DataEnd += count;
                this.DataCount += count;
                return;
            }
            int overflowIndexLength = (i + count) - bArr.length;
            int endPushIndexLength = count - overflowIndexLength;
            System.arraycopy(buffer, offset, bArr, i, endPushIndexLength);
            this.DataEnd = 0;
            int offset2 = offset + endPushIndexLength;
            this.DataCount += endPushIndexLength;
            if (overflowIndexLength != 0) {
                System.arraycopy(buffer, offset2, this.Buffer, 0, overflowIndexLength);
            }
            this.DataEnd += overflowIndexLength;
            this.DataCount += overflowIndexLength;
        }
    }

    public void WriteBuffer(byte[] buffer) throws Exception {
        WriteBuffer(buffer, 0, buffer.length);
    }

    public void ReadBuffer(byte[] targetBytes, int offset, int count) throws Exception {
        if (count > this.DataCount) {
            throw new Exception("环形缓冲区异常，读取长度大于数据长度");
        }
        int i = this.DataStart;
        int i2 = this.DataStart;
        int i3 = i2 + count;
        byte[] bArr = this.Buffer;
        if (i3 < bArr.length) {
            System.arraycopy(bArr, i2, targetBytes, offset, count);
            return;
        }
        int overflowIndexLength = (i2 + count) - bArr.length;
        int endPushIndexLength = count - overflowIndexLength;
        System.arraycopy(bArr, i2, targetBytes, offset, endPushIndexLength);
        int offset2 = offset + endPushIndexLength;
        if (overflowIndexLength != 0) {
            System.arraycopy(this.Buffer, 0, targetBytes, offset2, overflowIndexLength);
        }
    }
}
