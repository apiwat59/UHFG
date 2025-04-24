package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class ParamGbAntiCollision extends Parameter {
    int CCN;
    int CIN;

    public ParamGbAntiCollision() {
    }

    public ParamGbAntiCollision(int CIN, int CCN) {
        this.CIN = CIN;
        this.CCN = CCN;
    }

    public int getCIN() {
        return this.CIN;
    }

    public void setCIN(int CIN) {
        this.CIN = CIN;
    }

    public int getCCN() {
        return this.CCN;
    }

    public void setCCN(int CCN) {
        this.CCN = CCN;
    }

    public ParamGbAntiCollision(byte[] data) {
        if (data == null) {
            return;
        }
        try {
            BitBuffer buffer = BitBuffer.wrap(data);
            buffer.position(0);
            this.CIN = buffer.getIntUnsigned(4);
            this.CCN = buffer.getIntUnsigned(4);
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Parameter
    public byte[] toBytes() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.CIN, 4);
        buffer.putLong(this.CCN, 4);
        return buffer.asByteArray();
    }

    public String toString() {
        return "ParamGbAntiCollision{CIN=" + this.CIN + ", CCN=" + this.CCN + '}';
    }
}
