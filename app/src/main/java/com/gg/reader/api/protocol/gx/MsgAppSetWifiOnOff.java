package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgAppSetWifiOnOff extends Message {
    private int wifiSwitch;

    public MsgAppSetWifiOnOff() {
        this.wifiSwitch = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 55;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppSetWifiOnOff(byte[] data) {
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
        BitBuffer buffer = BitBuffer.allocateDynamic();
        int i = this.wifiSwitch;
        if (Integer.MAX_VALUE != i) {
            buffer.putInt(i, 8);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppSetWifiOnOff.1
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
