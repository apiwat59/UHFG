package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgTestSetRssiCalibration extends Message {
    private int rssiBaseValue;

    public MsgTestSetRssiCalibration() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_TEST;
            this.msgType.msgId = (byte) 8;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgTestSetRssiCalibration(byte[] data) {
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
            this.rssiBaseValue = buffer.getInt(16);
        } catch (Exception e) {
        }
    }

    public int getRssiBaseValue() {
        return this.rssiBaseValue;
    }

    public void setRssiBaseValue(int rssiBaseValue) {
        this.rssiBaseValue = rssiBaseValue;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putInt(this.rssiBaseValue, 16);
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgTestSetRssiCalibration.1
            {
                put((byte) 0, "Set success.");
                put((byte) 1, "Other error.");
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
