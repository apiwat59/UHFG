package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgTestGetSjc extends Message {
    private int cap_i;
    private int cap_q;
    private int cap_s;

    public MsgTestGetSjc() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_TEST;
            this.msgType.msgId = (byte) 23;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgTestGetSjc(byte[] data) {
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
            this.cap_i = buffer.getIntUnsigned(8);
            this.cap_q = buffer.getIntUnsigned(8);
            this.cap_s = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public int getCap_i() {
        return this.cap_i;
    }

    public void setCap_i(int cap_i) {
        this.cap_i = cap_i;
    }

    public int getCap_q() {
        return this.cap_q;
    }

    public void setCap_q(int cap_q) {
        this.cap_q = cap_q;
    }

    public int getCap_s() {
        return this.cap_s;
    }

    public void setCap_s(int cap_s) {
        this.cap_s = cap_s;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putInt(this.cap_i, 8);
            buffer.putInt(this.cap_q, 8);
            buffer.putInt(this.cap_s, 8);
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        if (this.cData != null && this.cData.length == 3) {
            BitBuffer buffer = BitBuffer.wrap(this.cData);
            buffer.position(0);
            this.cap_i = buffer.getIntUnsigned(8);
            this.cap_q = buffer.getIntUnsigned(8);
            this.cap_s = buffer.getIntUnsigned(8);
            setRtCode((byte) 0);
        }
    }
}
