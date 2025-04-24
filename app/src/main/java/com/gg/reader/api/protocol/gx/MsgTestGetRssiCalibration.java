package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgTestGetRssiCalibration extends Message {
    private int rssiBaseValue;

    public MsgTestGetRssiCalibration() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_TEST;
            this.msgType.msgId = (byte) 9;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgTestGetRssiCalibration(byte[] data) {
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
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putInt(this.rssiBaseValue, 16);
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
            this.rssiBaseValue = buffer.getInt(16);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgTestRssiCalibrationGet{rssiBaseValue=" + this.rssiBaseValue + '}';
    }
}
