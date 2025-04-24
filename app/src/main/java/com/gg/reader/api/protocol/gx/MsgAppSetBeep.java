package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgAppSetBeep extends Message {
    private int beepMode;
    private int beepStatus;

    public MsgAppSetBeep() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 31;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppSetBeep(byte[] data) {
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
            this.beepStatus = buffer.getIntUnsigned(8);
            this.beepMode = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public int getBeepStatus() {
        return this.beepStatus;
    }

    public void setBeepStatus(int beepStatus) {
        this.beepStatus = beepStatus;
    }

    public int getBeepMode() {
        return this.beepMode;
    }

    public void setBeepMode(int beepMode) {
        this.beepMode = beepMode;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putInt(this.beepStatus, 8);
        buffer.putInt(this.beepMode, 8);
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppSetBeep.1
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
