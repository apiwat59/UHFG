package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgBaseGetAutoDormancy extends Message {
    private int freeTime;
    private int onOff;

    public MsgBaseGetAutoDormancy() {
        this.freeTime = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 14;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseGetAutoDormancy(byte[] data) {
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
            this.onOff = buffer.getIntUnsigned(8);
            this.freeTime = buffer.getIntUnsigned(16);
        } catch (Exception e) {
        }
    }

    public int getOnOff() {
        return this.onOff;
    }

    public void setOnOff(int onOff) {
        this.onOff = onOff;
    }

    public int getFreeTime() {
        return this.freeTime;
    }

    public void setFreeTime(int freeTime) {
        this.freeTime = freeTime;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putLong(this.onOff, 8);
            int i = this.freeTime;
            if (Integer.MAX_VALUE != i) {
                buffer.put(i, 16);
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
            this.onOff = buffer.getIntUnsigned(8);
            this.freeTime = buffer.getIntUnsigned(16);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgBaseGetAutoDormancy{onOff=" + this.onOff + ", freeTime=" + this.freeTime + '}';
    }
}
