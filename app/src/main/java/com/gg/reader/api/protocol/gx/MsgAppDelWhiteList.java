package com.gg.reader.api.protocol.gx;

import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgAppDelWhiteList extends Message {
    public MsgAppDelWhiteList() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 34;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppDelWhiteList(byte[] data) {
        this();
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        super.pack();
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppDelWhiteList.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Delete Failure.");
            }
        };
        if (this.cData != null && this.cData.length == 1) {
            setRtCode(this.cData[0]);
            if (dicErrorMsg.containsKey(Byte.valueOf(this.cData[0]))) {
                setRtMsg(dicErrorMsg.get(Byte.valueOf(this.cData[0])));
            }
        }
    }
}
