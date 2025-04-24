package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgAppTagDataReply extends Message {
    private long serialNumber;

    public MsgAppTagDataReply() {
        this.serialNumber = 0L;
        this.msgType = new MsgType();
        this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
        this.msgType.msgId = (byte) 29;
        this.dataLen = 0;
    }

    public MsgAppTagDataReply(byte[] data) {
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
            this.serialNumber = buffer.getLongUnsigned(32);
        } catch (Exception e) {
        }
    }

    public long getSerialNumber() {
        return this.serialNumber;
    }

    public void setSerialNumber(long serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.put(this.serialNumber, 32);
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.put(this.serialNumber, 32);
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        BitBuffer buffer = BitBuffer.wrap(this.cData);
        buffer.position(0);
        this.serialNumber = buffer.getLongUnsigned(32);
        setRtCode((byte) 0);
    }

    public String toString() {
        return "MsgAppTagDataReply{serialNumber=" + this.serialNumber + '}';
    }
}
