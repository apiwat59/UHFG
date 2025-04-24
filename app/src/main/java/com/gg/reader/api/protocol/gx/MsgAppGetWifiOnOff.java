package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgAppGetWifiOnOff extends Message {
    private int wifiSwitch;

    public MsgAppGetWifiOnOff() {
        this.wifiSwitch = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 56;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetWifiOnOff(byte[] data) {
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
            this.wifiSwitch = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public int getWifiSwitch() {
        return this.wifiSwitch;
    }

    public void setWifiSwitch(int wifiSwitch) {
        this.wifiSwitch = wifiSwitch;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            int i = this.wifiSwitch;
            if (Integer.MAX_VALUE != i) {
                buffer.putInt(i, 8);
            }
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
            this.wifiSwitch = buffer.getIntUnsigned(8);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgAppGetWifiOnOff{wifiSwitch=" + this.wifiSwitch + '}';
    }
}
