package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class ParamGbBaseSpeed extends Parameter {
    int LS;
    int RLC;
    int RLF;
    int Tc;

    public ParamGbBaseSpeed() {
    }

    public ParamGbBaseSpeed(int tc, int LS, int RLF, int RLC) {
        this.Tc = tc;
        this.LS = LS;
        this.RLF = RLF;
        this.RLC = RLC;
    }

    public int getTc() {
        return this.Tc;
    }

    public void setTc(int tc) {
        this.Tc = tc;
    }

    public int getLS() {
        return this.LS;
    }

    public void setLS(int LS) {
        this.LS = LS;
    }

    public int getRLF() {
        return this.RLF;
    }

    public void setRLF(int RLF) {
        this.RLF = RLF;
    }

    public int getRLC() {
        return this.RLC;
    }

    public void setRLC(int RLC) {
        this.RLC = RLC;
    }

    public ParamGbBaseSpeed(byte[] data) {
        if (data == null) {
            return;
        }
        try {
            BitBuffer buffer = BitBuffer.wrap(data);
            buffer.position(0);
            this.Tc = buffer.getIntUnsigned(1);
            this.LS = buffer.getIntUnsigned(1);
            this.RLF = buffer.getIntUnsigned(4);
            this.RLC = buffer.getIntUnsigned(2);
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Parameter
    public byte[] toBytes() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.Tc, 1);
        buffer.putLong(this.LS, 1);
        buffer.putLong(this.RLF, 4);
        buffer.putLong(this.RLC, 2);
        return buffer.asByteArray();
    }

    public String toString() {
        return "ParamGbBaseSpeed{Tc=" + this.Tc + ", LS=" + this.LS + ", RLF=" + this.RLF + ", RLC=" + this.RLC + '}';
    }
}
