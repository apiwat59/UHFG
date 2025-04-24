package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgAppSetWiegand extends Message {
    private int negativePulseWidth;
    private int pulseInterval;
    private int wiegandContent;
    private int wiegandFormat;
    private int wiegandSwitch;

    public MsgAppSetWiegand() {
        this.negativePulseWidth = Integer.MAX_VALUE;
        this.pulseInterval = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 13;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppSetWiegand(byte[] data) {
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
            this.wiegandSwitch = buffer.getIntUnsigned(8);
            this.wiegandFormat = buffer.getIntUnsigned(8);
            this.wiegandContent = buffer.getIntUnsigned(8);
            while (buffer.position() / 8 < data.length) {
                int pid = buffer.getIntUnsigned(8);
                if (pid == 1) {
                    byte[] paramData = new byte[2];
                    buffer.get(paramData);
                    this.negativePulseWidth = HexUtils.bytes2Int(paramData);
                } else if (pid == 2) {
                    byte[] paramData2 = new byte[2];
                    buffer.get(paramData2);
                    this.pulseInterval = HexUtils.bytes2Int(paramData2);
                }
            }
        } catch (Exception e) {
        }
    }

    public int getWiegandSwitch() {
        return this.wiegandSwitch;
    }

    public void setWiegandSwitch(int wiegandSwitch) {
        this.wiegandSwitch = wiegandSwitch;
    }

    public int getWiegandFormat() {
        return this.wiegandFormat;
    }

    public void setWiegandFormat(int wiegandFormat) {
        this.wiegandFormat = wiegandFormat;
    }

    public int getWiegandContent() {
        return this.wiegandContent;
    }

    public void setWiegandContent(int wiegandContent) {
        this.wiegandContent = wiegandContent;
    }

    public int getNegativePulseWidth() {
        return this.negativePulseWidth;
    }

    public void setNegativePulseWidth(int negativePulseWidth) {
        this.negativePulseWidth = negativePulseWidth;
    }

    public int getPulseInterval() {
        return this.pulseInterval;
    }

    public void setPulseInterval(int pulseInterval) {
        this.pulseInterval = pulseInterval;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.wiegandSwitch, 8);
        buffer.putLong(this.wiegandFormat, 8);
        buffer.putLong(this.wiegandContent, 8);
        if (Integer.MAX_VALUE != this.negativePulseWidth) {
            buffer.putInt(1, 8);
            buffer.put(this.negativePulseWidth, 16);
        }
        if (Integer.MAX_VALUE != this.pulseInterval) {
            buffer.putInt(2, 8);
            buffer.put(this.pulseInterval, 16);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppSetWiegand.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Reader hardware is not supported Wigan port.");
                put((byte) 2, "Wiegand communication format not supported by reader .");
                put((byte) 3, "Data content not supported by the reader");
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
