package com.gg.reader.api.protocol.gx;

import java.util.HashMap;

/* loaded from: classes.dex */
public class MsgTestDCAutobias extends Message {
    public MsgTestDCAutobias() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_TEST;
            this.msgType.msgId = (byte) 6;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgTestDCAutobias(byte[] data) {
        this();
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        super.pack();
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        HashMap<Byte, String> dicErrorMsg = new HashMap<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgTestDCAutobias.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "failure.");
            }
        };
        if (this.cData != null && this.cData.length > 0) {
            setRtCode(this.cData[0]);
            if (dicErrorMsg.containsKey(Byte.valueOf(this.cData[0]))) {
                setRtMsg(dicErrorMsg.get(Byte.valueOf(this.cData[0])));
            }
        }
    }
}
