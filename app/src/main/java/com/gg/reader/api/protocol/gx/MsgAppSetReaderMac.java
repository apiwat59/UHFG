package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.StringUtils;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgAppSetReaderMac extends Message {
    private String mac;

    public MsgAppSetReaderMac() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 19;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppSetReaderMac(byte[] data) {
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
        BitBuffer buffer = BitBuffer.allocateDynamic();
        if (!StringUtils.isNullOfEmpty(this.mac)) {
            String[] macs = this.mac.split("-");
            for (String s : macs) {
                buffer.putInt(Integer.parseInt(s, 16), 8);
            }
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppSetReaderMac.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Reader MAC parameter error.");
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
