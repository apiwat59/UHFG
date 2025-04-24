package com.gg.reader.api.protocol.gx;

/* loaded from: classes.dex */
public class MsgAppReset extends Message {
    public MsgAppReset() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 15;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppReset(byte[] data) {
        this();
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        super.pack();
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        super.ackUnpack();
    }
}
