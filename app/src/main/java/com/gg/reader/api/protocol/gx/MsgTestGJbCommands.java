package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.HashMap;

/* loaded from: classes.dex */
public class MsgTestGJbCommands extends Message {
    private Long antennaNum;
    private int freqCursor;

    public MsgTestGJbCommands(int mid) {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_TEST;
            this.msgType.msgId = (byte) mid;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgTestGJbCommands(byte[] data, int mid) {
        this(mid);
        if (data == null) {
            return;
        }
        try {
            if (data.length <= 0) {
                return;
            }
            BitBuffer buffer = BitBuffer.wrap(data);
            buffer.position(0);
            this.antennaNum = Long.valueOf(buffer.getLongUnsigned(32));
            this.freqCursor = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public Long getAntennaNum() {
        return this.antennaNum;
    }

    public void setAntennaNum(Long antennaNum) {
        this.antennaNum = antennaNum;
    }

    public int getFreqCursor() {
        return this.freqCursor;
    }

    public void setFreqCursor(int freqCursor) {
        this.freqCursor = freqCursor;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.antennaNum.longValue(), 32);
        buffer.putLong(this.freqCursor, 8);
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        HashMap<Byte, String> dicErrorMsg = new HashMap<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgTestGJbCommands.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Frequency parameter reader is not supported.");
                put((byte) 2, "Port parameter reader is not supported.");
                put((byte) 3, "Lock failure.");
                put((byte) 4, "Other error.");
            }
        };
        if (this.cData != null && this.cData.length > 0) {
            setRtCode(this.cData[0]);
            if (dicErrorMsg.containsKey(Byte.valueOf(this.cData[0]))) {
                setRtMsg(dicErrorMsg.get(Byte.valueOf(this.cData[0])));
            }
        }
    }
}
