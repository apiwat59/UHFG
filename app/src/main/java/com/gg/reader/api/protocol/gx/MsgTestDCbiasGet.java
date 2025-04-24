package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgTestDCbiasGet extends Message {
    private int param;

    public MsgTestDCbiasGet() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_TEST;
            this.msgType.msgId = (byte) 2;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgTestDCbiasGet(byte[] data) {
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
            this.param = buffer.getInt(8);
        } catch (Exception e) {
        }
    }

    public int getParam() {
        return this.param;
    }

    public void setParam(int param) {
        this.param = param;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.put(this.param, 8);
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
            this.param = buffer.getInt(8);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgTestDCbiasGet{param=" + this.param + '}';
    }
}
