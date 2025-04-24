package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgTestPowerCalibrationGet extends Message {
    private int childFreqRange;
    private int power;
    private int powerParam;

    public MsgTestPowerCalibrationGet() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_TEST;
            this.msgType.msgId = (byte) 4;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgTestPowerCalibrationGet(byte[] data) {
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
            this.childFreqRange = buffer.getIntUnsigned(8);
            this.power = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public int getChildFreqRange() {
        return this.childFreqRange;
    }

    public void setChildFreqRange(int childFreqRange) {
        this.childFreqRange = childFreqRange;
    }

    public int getPower() {
        return this.power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getPowerParam() {
        return this.powerParam;
    }

    public void setPowerParam(int powerParam) {
        this.powerParam = powerParam;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putLong(this.childFreqRange, 8);
            buffer.putLong(this.power, 8);
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putLong(this.childFreqRange, 8);
            buffer.putLong(this.power, 8);
            buffer.putLong(this.powerParam, 8);
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        if (this.cData != null && this.cData.length > 0) {
            BitBuffer buffer = BitBuffer.wrap(this.cData);
            buffer.position(0);
            this.childFreqRange = buffer.getIntUnsigned(8);
            this.power = buffer.getIntUnsigned(8);
            this.powerParam = buffer.getIntUnsigned(8);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgTestPowerCalibrationGet{childFreqRange=" + this.childFreqRange + ", power=" + this.power + ", powerParam=" + this.powerParam + '}';
    }
}
