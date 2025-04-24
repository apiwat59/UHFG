package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class ParamFastId extends Parameter {
    private int fastId;
    private int tagFoucs;

    public ParamFastId() {
    }

    public ParamFastId(int fastId, int tagFoucs) {
        this.fastId = fastId;
        this.tagFoucs = tagFoucs;
    }

    public ParamFastId(byte[] data) {
        if (data == null || data.length == 0) {
            return;
        }
        this.fastId = data[0];
        this.tagFoucs = data[1];
    }

    public int getFastId() {
        return this.fastId;
    }

    public void setFastId(int fastId) {
        this.fastId = fastId;
    }

    public int getTagFoucs() {
        return this.tagFoucs;
    }

    public void setTagFoucs(int tagFoucs) {
        this.tagFoucs = tagFoucs;
    }

    @Override // com.gg.reader.api.protocol.gx.Parameter
    public byte[] toBytes() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putInt(this.fastId, 8);
        buffer.putInt(this.tagFoucs, 8);
        return buffer.asByteArray();
    }
}
