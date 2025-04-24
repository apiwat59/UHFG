package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class ParamGbReadUserdata extends Parameter {
    private int childArea;
    private int len;
    private int start;

    public ParamGbReadUserdata() {
    }

    public ParamGbReadUserdata(int childArea, int start, int len) {
        this.childArea = childArea;
        this.start = start;
        this.len = len;
    }

    public ParamGbReadUserdata(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        BitBuffer buffer = BitBuffer.wrap(data);
        buffer.position(0);
        this.childArea = buffer.getIntUnsigned(8);
        this.start = buffer.getIntUnsigned(16);
        this.len = buffer.getIntUnsigned(8);
    }

    public int getChildArea() {
        return this.childArea;
    }

    public void setChildArea(int childArea) {
        this.childArea = childArea;
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
        buffer.putInt(this.childArea, 8);
        buffer.putInt(this.start, 16);
        buffer.putInt(this.len, 8);
        return buffer.asByteArray();
    }
}
