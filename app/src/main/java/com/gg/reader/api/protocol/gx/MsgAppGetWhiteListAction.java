package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgAppGetWhiteListAction extends Message {
    private int relay;
    private int relayCloseTime;

    public MsgAppGetWhiteListAction() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 36;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetWhiteListAction(byte[] data) {
        this();
        if (data == null) {
            return;
        }
        try {
            if (data.length <= 0) {
                return;
            }
            BitBuffer buffer = BitBuffer.wrap(data);
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
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putInt(this.relay, 8);
            buffer.putInt(this.relayCloseTime, 16);
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
            this.relay = buffer.getIntUnsigned(8);
            this.relayCloseTime = buffer.getIntUnsigned(16);
            setRtCode((byte) 0);
        }
    }
}
