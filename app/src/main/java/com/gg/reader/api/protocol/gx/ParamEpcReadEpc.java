package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class ParamEpcReadEpc extends Parameter {
    private int len;
    private int start;

    public ParamEpcReadEpc() {
    }

    public ParamEpcReadEpc(int start, int len) {
        this.start = start;
        this.len = len;
    }

    public int getStart() {
        return this.start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLen() {
        return this.len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public ParamEpcReadEpc(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        BitBuffer buffer = BitBuffer.wrap(data);
        buffer.position(0);
        this.start = buffer.getIntUnsigned(16);
        this.len = buffer.getIntUnsigned(8);
    }

    @Override // com.gg.reader.api.protocol.gx.Parameter
    public byte[] toBytes() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.start, 16);
        buffer.putLong(this.len, 8);
        return buffer.asByteArray();
    }
}
