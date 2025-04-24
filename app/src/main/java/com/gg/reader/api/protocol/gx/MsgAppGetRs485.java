package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgAppGetRs485 extends Message {
    private int address;
    private int baudRate;

    public MsgAppGetRs485() {
        this.baudRate = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 22;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetRs485(byte[] data) {
        this();
        if (data == null) {
            return;
        }
        try {
            if (data.length <= 0) {
                return;
            }
            BitBuffer buffer = BitBuffer.wrap(data);
            buffer.position(0);
            this.address = buffer.getIntUnsigned(8);
            this.baudRate = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public int getAddress() {
        return this.address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getBaudRate() {
        return this.baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putInt(this.address, 8);
            int i = this.baudRate;
            if (Integer.MAX_VALUE != i) {
                buffer.putLong(i, 8);
            }
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        if (this.cData != null && this.cData.length > 0) {
            BitBuffer buffer = BitBuffer.wrap(this.cData);
            buffer.position(0);
            this.address = buffer.getIntUnsigned(8);
            this.baudRate = buffer.getIntUnsigned(8);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgAppGetRs485{address=" + this.address + ", baudRate=" + this.baudRate + '}';
    }
}
