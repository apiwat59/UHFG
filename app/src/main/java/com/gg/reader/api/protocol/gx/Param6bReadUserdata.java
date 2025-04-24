package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class Param6bReadUserdata extends Parameter {
    private int len;
    private int start;

    public Param6bReadUserdata() {
    }

    public Param6bReadUserdata(int start, int len) {
        this.start = start;
        this.len = len;
    }

    public Param6bReadUserdata(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        this.start = data[0];
        this.len = data[1];
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

    @Override // com.gg.reader.api.protocol.gx.Parameter
    public byte[] toBytes() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.start, 8);
        buffer.putLong(this.len, 8);
        return buffer.asByteArray();
    }
}
