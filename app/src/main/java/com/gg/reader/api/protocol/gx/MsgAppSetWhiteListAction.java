package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgAppSetWhiteListAction extends Message {
    private int relay;
    private int relayCloseTime;

    public MsgAppSetWhiteListAction() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 35;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppSetWhiteListAction(byte[] data) {
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
            this.relay = buffer.getIntUnsigned(8);
            this.relayCloseTime = buffer.getIntUnsigned(16);
        } catch (Exception e) {
        }
    }

    public int getRelay() {
        return this.relay;
    }

    public void setRelay(int relay) {
        this.relay = relay;
    }

    public int getRelayCloseTime() {
        return this.relayCloseTime;
    }

    public void setRelayCloseTime(int relayCloseTime) {
        this.relayCloseTime = relayCloseTime;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putInt(this.relay, 8);
        buffer.putInt(this.relayCloseTime, 16);
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppSetWhiteListAction.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Set Fail.");
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
