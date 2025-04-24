package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgBaseSetAutoDormancy extends Message {
    private int freeTime;
    private int onOff;

    public MsgBaseSetAutoDormancy() {
        this.freeTime = Integer.MIN_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 13;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseSetAutoDormancy(byte[] data) {
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
            this.onOff = buffer.getIntUnsigned(8);
            while (buffer.position() / 8 < data.length) {
                byte pid = buffer.getByte();
                if (pid == 1) {
                    this.freeTime = buffer.getIntUnsigned(16);
                }
            }
        } catch (Exception e) {
        }
    }

    public int getOnOff() {
        return this.onOff;
    }

    public void setOnOff(int onOff) {
        this.onOff = onOff;
    }

    public int getFreeTime() {
        return this.freeTime;
    }

    public void setFreeTime(int freeTime) {
        this.freeTime = freeTime;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.onOff, 8);
        if (Integer.MIN_VALUE != this.freeTime) {
            buffer.putInt(1, 8);
            buffer.putInt(this.freeTime, 16);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseSetAutoDormancy.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Parameter error.");
                put((byte) 2, "Other error.");
                put((byte) 3, "Save failure.");
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
