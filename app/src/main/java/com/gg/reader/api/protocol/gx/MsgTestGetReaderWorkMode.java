package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgTestGetReaderWorkMode extends Message {
    private int baudRate232;
    private int baudRate485;
    private int dataBit485;
    private int parityBit485;
    private int stopBit485;
    private int workMode;

    public MsgTestGetReaderWorkMode() {
        this.baudRate485 = Integer.MAX_VALUE;
        this.dataBit485 = Integer.MAX_VALUE;
        this.parityBit485 = Integer.MAX_VALUE;
        this.stopBit485 = Integer.MAX_VALUE;
        this.baudRate232 = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_TEST;
            this.msgType.msgId = (byte) 18;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgTestGetReaderWorkMode(byte[] data) {
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
            this.workMode = buffer.getInt(8);
            this.baudRate485 = buffer.getIntUnsigned(8);
            this.dataBit485 = buffer.getIntUnsigned(8);
            this.parityBit485 = buffer.getIntUnsigned(8);
            this.stopBit485 = buffer.getIntUnsigned(8);
            this.baudRate232 = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public int getWorkMode() {
        return this.workMode;
    }

    public void setWorkMode(int workMode) {
        this.workMode = workMode;
    }

    public int getBaudRate485() {
        return this.baudRate485;
    }

    public void setBaudRate485(int baudRate485) {
        this.baudRate485 = baudRate485;
    }

    public int getDataBit485() {
        return this.dataBit485;
    }

    public void setDataBit485(int dataBit485) {
        this.dataBit485 = dataBit485;
    }

    public int getParityBit485() {
        return this.parityBit485;
    }

    public void setParityBit485(int parityBit485) {
        this.parityBit485 = parityBit485;
    }

    public int getStopBit485() {
        return this.stopBit485;
    }

    public void setStopBit485(int stopBit485) {
        this.stopBit485 = stopBit485;
    }

    public int getBaudRate232() {
        return this.baudRate232;
    }

    public void setBaudRate232(int baudRate232) {
        this.baudRate232 = baudRate232;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putInt(this.workMode, 8);
            buffer.putInt(this.baudRate485, 8);
            buffer.putInt(this.dataBit485, 8);
            buffer.putInt(this.parityBit485, 8);
            buffer.putInt(this.stopBit485, 8);
            buffer.putInt(this.baudRate232, 8);
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
            this.workMode = buffer.getInt(8);
            this.baudRate485 = buffer.getIntUnsigned(8);
            this.dataBit485 = buffer.getIntUnsigned(8);
            this.parityBit485 = buffer.getIntUnsigned(8);
            this.stopBit485 = buffer.getIntUnsigned(8);
            this.baudRate232 = buffer.getIntUnsigned(8);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgTestGetReaderWorkMode{workMode=" + this.workMode + ", baudRate485=" + this.baudRate485 + ", dataBit485=" + this.dataBit485 + ", parityBit485=" + this.parityBit485 + ", stopBit485=" + this.stopBit485 + ", baudRate232=" + this.baudRate232 + '}';
    }
}
