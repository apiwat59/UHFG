package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgTestVSWRcheck extends Message {
    private int preValue;
    private int sufValue;

    public MsgTestVSWRcheck() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_TEST;
            this.msgType.msgId = (byte) 5;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgTestVSWRcheck(byte[] data) {
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
            this.preValue = buffer.getIntUnsigned(8);
            this.sufValue = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public int getPreValue() {
        return this.preValue;
    }

    public void setPreValue(int preValue) {
        this.preValue = preValue;
    }

    public int getSufValue() {
        return this.sufValue;
    }

    public void setSufValue(int sufValue) {
        this.sufValue = sufValue;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        super.pack();
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        if (this.cData != null && this.cData.length > 0) {
            BitBuffer buffer = BitBuffer.wrap(this.cData);
            buffer.position(0);
            this.preValue = buffer.getIntUnsigned(8);
            this.sufValue = buffer.getIntUnsigned(8);
            setRtCode((byte) 0);
        }
    }
}
