package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgAppGetWhiteListSwitch extends Message {
    private int filterArea;
    private int onOrOff;

    public MsgAppGetWhiteListSwitch() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 38;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetWhiteListSwitch(byte[] data) {
        this();
        if (data == null) {
            return;
        }
        try {
            if (data.length <= 0) {
                return;
            }
            BitBuffer buffer = BitBuffer.wrap(data);
            this.onOrOff = buffer.getIntUnsigned(8);
            this.filterArea = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public int getOnOrOff() {
        return this.onOrOff;
    }

    public void setOnOrOff(int onOrOff) {
        this.onOrOff = onOrOff;
    }

    public int getFilterArea() {
        return this.filterArea;
    }

    public void setFilterArea(int filterArea) {
        this.filterArea = filterArea;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putInt(this.onOrOff, 8);
            buffer.putInt(this.filterArea, 8);
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
            this.onOrOff = buffer.getIntUnsigned(8);
            this.filterArea = buffer.getIntUnsigned(8);
            setRtCode((byte) 0);
        }
    }
}
