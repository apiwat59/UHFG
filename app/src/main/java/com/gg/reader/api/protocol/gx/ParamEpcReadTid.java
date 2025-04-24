package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class ParamEpcReadTid extends Parameter {
    private int len;
    private int mode;

    public ParamEpcReadTid() {
    }

    public ParamEpcReadTid(int mode, int len) {
        this.mode = mode;
        this.len = len;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getLen() {
        return this.len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public ParamEpcReadTid(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        this.mode = data[0];
        this.len = data[1];
    }

    @Override // com.gg.reader.api.protocol.gx.Parameter
    public byte[] toBytes() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.mode, 8);
        buffer.putLong(this.len, 8);
        return buffer.asByteArray();
    }
}
