package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgBaseGetTagLog extends Message {
    private int repeatedTime;
    private int rssiTV;

    public MsgBaseGetTagLog() {
        this.repeatedTime = Integer.MAX_VALUE;
        this.rssiTV = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 10;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseGetTagLog(byte[] data) {
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
            this.repeatedTime = buffer.getIntUnsigned(16);
            this.rssiTV = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public int getRepeatedTime() {
        return this.repeatedTime;
    }

    public void setRepeatedTime(int repeatedTime) {
        this.repeatedTime = repeatedTime;
    }

    public int getRssiTV() {
        return this.rssiTV;
    }

    public void setRssiTV(int rssiTV) {
        this.rssiTV = rssiTV;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            int i = this.repeatedTime;
            if (Integer.MAX_VALUE != i) {
                buffer.putLong(i, 16);
            }
            int i2 = this.rssiTV;
            if (Integer.MAX_VALUE != i2) {
                buffer.putLong(i2, 8);
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
            this.repeatedTime = buffer.getIntUnsigned(16);
            this.rssiTV = buffer.getIntUnsigned(8);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgBaseGetTagLog{repeatedTime=" + this.repeatedTime + ", rssiTV=" + this.rssiTV + '}';
    }
}
