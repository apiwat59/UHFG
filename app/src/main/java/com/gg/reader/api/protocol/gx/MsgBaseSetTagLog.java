package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgBaseSetTagLog extends Message {
    private int repeatedTime;
    private int rssiTV;

    public MsgBaseSetTagLog() {
        this.repeatedTime = Integer.MAX_VALUE;
        this.rssiTV = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 9;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseSetTagLog(byte[] data) {
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
            while (buffer.position() / 8 < data.length) {
                byte pid = buffer.getByte();
                if (pid == 1) {
                    this.repeatedTime = buffer.getIntUnsigned(16);
                } else if (pid == 2) {
                    this.rssiTV = buffer.getIntUnsigned(8);
                }
            }
        } catch (Exception e) {
        }
    }

    public int getRepeatedTime() {
        return this.repeatedTime;
    }

    public void setRepeatedTime(int repeatedTime) {
        this.repeatedTime = repeatedTime;
    }

    public int getRssiTV() {
        return this.rssiTV;
    }

    public void setRssiTV(int rssiTV) {
        this.rssiTV = rssiTV;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        if (Integer.MAX_VALUE != this.repeatedTime) {
            buffer.putInt(1, 8);
            buffer.put(this.repeatedTime, 16);
        }
        if (Integer.MAX_VALUE != this.rssiTV) {
            buffer.putInt(2, 8);
            buffer.put(this.rssiTV, 8);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseSetTagLog.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Parameter error.");
                put((byte) 2, "Save failure.");
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
