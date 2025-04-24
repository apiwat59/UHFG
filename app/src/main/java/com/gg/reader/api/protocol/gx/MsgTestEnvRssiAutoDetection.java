package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgTestEnvRssiAutoDetection extends Message {
    private Long antennaEnable;
    private int endFrequency;
    private int mode;
    private int startFrequency;

    public MsgTestEnvRssiAutoDetection() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_TEST;
            this.msgType.msgId = (byte) 10;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgTestEnvRssiAutoDetection(byte[] data) {
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
            this.antennaEnable = Long.valueOf(buffer.getLongUnsigned(32));
            this.startFrequency = buffer.getIntUnsigned(16);
            this.endFrequency = buffer.getIntUnsigned(16);
            this.mode = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public Long getAntennaEnable() {
        return this.antennaEnable;
    }

    public void setAntennaEnable(Long antennaEnable) {
        this.antennaEnable = antennaEnable;
    }

    public int getStartFrequency() {
        return this.startFrequency;
    }

    public void setStartFrequency(int startFrequency) {
        this.startFrequency = startFrequency;
    }

    public int getEndFrequency() {
        return this.endFrequency;
    }

    public void setEndFrequency(int endFrequency) {
        this.endFrequency = endFrequency;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.antennaEnable.longValue(), 32);
        buffer.putInt(this.startFrequency, 16);
        buffer.putInt(this.endFrequency, 16);
        buffer.putInt(this.mode, 8);
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgTestEnvRssiAutoDetection.1
            {
                put((byte) 0, "Start success.");
                put((byte) 1, "FrequencyPoint Param Reader Not Support.");
                put((byte) 2, "Port Param Reader Not Support.");
                put((byte) 3, "Phase-locked loop locking failed");
                put((byte) 4, "Other error");
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
