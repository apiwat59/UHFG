package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.StringUtils;

/* loaded from: classes.dex */
public class MsgAppGetReaderMac extends Message {
    private String mac;

    public MsgAppGetReaderMac() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 6;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetReaderMac(byte[] data) {
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
            this.mac = String.format("%02x", Integer.valueOf(buffer.getIntUnsigned(8))).toUpperCase() + "-" + String.format("%02x", Integer.valueOf(buffer.getIntUnsigned(8))).toUpperCase() + "-" + String.format("%02x", Integer.valueOf(buffer.getIntUnsigned(8))).toUpperCase() + "-" + String.format("%02x", Integer.valueOf(buffer.getIntUnsigned(8))).toUpperCase() + "-" + String.format("%02x", Integer.valueOf(buffer.getIntUnsigned(8))).toUpperCase() + "-" + String.format("%02x", Integer.valueOf(buffer.getIntUnsigned(8))).toUpperCase();
        } catch (Exception e) {
        }
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            if (!StringUtils.isNullOfEmpty(this.mac)) {
                String[] macs = this.mac.split("-");
                for (String s : macs) {
                    buffer.putInt(Integer.parseInt(s, 16), 8);
                }
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
            this.mac = String.format("%02x", Integer.valueOf(buffer.getIntUnsigned(8))).toUpperCase() + "-" + String.format("%02x", Integer.valueOf(buffer.getIntUnsigned(8))).toUpperCase() + "-" + String.format("%02x", Integer.valueOf(buffer.getIntUnsigned(8))).toUpperCase() + "-" + String.format("%02x", Integer.valueOf(buffer.getIntUnsigned(8))).toUpperCase() + "-" + String.format("%02x", Integer.valueOf(buffer.getIntUnsigned(8))).toUpperCase() + "-" + String.format("%02x", Integer.valueOf(buffer.getIntUnsigned(8))).toUpperCase();
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgAppGetReaderMac{mac='" + this.mac + "'}";
    }
}
