package com.gg.reader.api.protocol.gx;

import java.util.HashMap;

/* loaded from: classes.dex */
public class MsgAppGetCacheTagData extends Message {
    public MsgAppGetCacheTagData() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 27;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetCacheTagData(byte[] data) {
        this();
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        super.pack();
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        HashMap<Byte, String> dicErrorMsg = new HashMap<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppGetCacheTagData.1
            {
                put((byte) 0, "Have  data.");
                put((byte) 1, "No data.");
                put((byte) 2, "End of data return.");
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
