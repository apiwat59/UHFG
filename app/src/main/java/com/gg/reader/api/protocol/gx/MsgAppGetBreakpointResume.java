package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgAppGetBreakpointResume extends Message {
    private int onOrOff;

    public MsgAppGetBreakpointResume() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 24;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetBreakpointResume(byte[] data) {
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
            this.onOrOff = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public int getOnOrOff() {
        return this.onOrOff;
    }

    public void setOnOrOff(int onOrOff) {
        this.onOrOff = onOrOff;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putInt(this.onOrOff, 8);
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
            this.onOrOff = buffer.getIntUnsigned(8);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgAppGetBreakpointResume{onOrOff=" + this.onOrOff + '}';
    }
}
