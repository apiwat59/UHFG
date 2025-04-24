package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class ActionParamFail extends Parameter {
    private int gpo1;
    private int gpo2;
    private int gpo3;
    private int gpo4;
    private int keepTime;

    public ActionParamFail() {
        this.keepTime = 0;
        this.gpo1 = Integer.MAX_VALUE;
        this.gpo2 = Integer.MAX_VALUE;
        this.gpo3 = Integer.MAX_VALUE;
        this.gpo4 = Integer.MAX_VALUE;
    }

    public ActionParamFail(byte[] data) {
        this.keepTime = 0;
        this.gpo1 = Integer.MAX_VALUE;
        this.gpo2 = Integer.MAX_VALUE;
        this.gpo3 = Integer.MAX_VALUE;
        this.gpo4 = Integer.MAX_VALUE;
        if (data == null || data.length == 0) {
            return;
        }
        BitBuffer buffer = BitBuffer.wrap(data);
        buffer.position(0);
        this.keepTime = buffer.getIntUnsigned(16);
        while (buffer.position() / 8 < data.length) {
            int pid = buffer.getIntUnsigned(8);
            if (pid == 1) {
                this.gpo1 = buffer.getIntUnsigned(8);
            } else if (pid == 2) {
                this.gpo2 = buffer.getIntUnsigned(8);
            } else if (pid == 3) {
                this.gpo3 = buffer.getIntUnsigned(8);
            } else if (pid == 4) {
                this.gpo4 = buffer.getIntUnsigned(8);
            }
        }
    }

    public int getKeepTime() {
        return this.keepTime;
    }

    public void setKeepTime(int keepTime) {
        this.keepTime = keepTime;
    }

    public int getGpo1() {
        return this.gpo1;
    }

    public void setGpo1(int gpo1) {
        this.gpo1 = gpo1;
    }

    public int getGpo2() {
        return this.gpo2;
    }

    public void setGpo2(int gpo2) {
        this.gpo2 = gpo2;
    }

    public int getGpo3() {
        return this.gpo3;
    }

    public void setGpo3(int gpo3) {
        this.gpo3 = gpo3;
    }

    public int getGpo4() {
        return this.gpo4;
    }

    public void setGpo4(int gpo4) {
        this.gpo4 = gpo4;
    }

    @Override // com.gg.reader.api.protocol.gx.Parameter
    public byte[] toBytes() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.keepTime, 16);
        if (Integer.MAX_VALUE != this.gpo1) {
            buffer.putInt(1, 8);
            buffer.putInt(this.gpo1, 8);
        }
        if (Integer.MAX_VALUE != this.gpo2) {
            buffer.putInt(2, 8);
            buffer.putInt(this.gpo2, 8);
        }
        if (Integer.MAX_VALUE != this.gpo3) {
            buffer.putInt(3, 8);
            buffer.putInt(this.gpo3, 8);
        }
        if (Integer.MAX_VALUE != this.gpo4) {
            buffer.putInt(4, 8);
            buffer.putInt(this.gpo4, 8);
        }
        return buffer.asByteArray();
    }

    public String toString() {
        return "ActionParamFail{keepTime=" + this.keepTime + ", gpo1=" + this.gpo1 + ", gpo2=" + this.gpo2 + ", gpo3=" + this.gpo3 + ", gpo4=" + this.gpo4 + '}';
    }
}
