package com.gg.reader.api.protocol.gx;

import java.util.HashMap;

/* loaded from: classes.dex */
public class MsgBaseStop extends Message {
    public MsgBaseStop() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) -1;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseStop(byte[] data) {
        this();
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        super.pack();
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        HashMap<Byte, String> dicErrorMsg = new HashMap<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseStop.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Stop failure.");
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
