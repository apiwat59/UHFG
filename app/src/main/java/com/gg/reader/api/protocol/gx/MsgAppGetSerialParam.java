package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgAppGetSerialParam extends Message {
    private int serialBaudrate;

    public MsgAppGetSerialParam() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 3;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetSerialParam(byte[] data) {
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
            this.serialBaudrate = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public int getSerialBaudrate() {
        return this.serialBaudrate;
    }

    public void setSerialBaudrate(int serialBaudrate) {
        this.serialBaudrate = serialBaudrate;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putLong(this.serialBaudrate, 8);
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
            this.serialBaudrate = buffer.getIntUnsigned(8);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgAppGetSerialParam{serialBaudrate=" + this.serialBaudrate + '}';
    }
}
