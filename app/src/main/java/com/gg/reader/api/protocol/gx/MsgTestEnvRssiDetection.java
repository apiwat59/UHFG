package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.HashMap;

/* loaded from: classes.dex */
public class MsgTestEnvRssiDetection extends Message {
    private Long antennaEnable;
    private int currentRssi;
    private int frequencyPoint;
    private Long rssiFrequency;

    public MsgTestEnvRssiDetection() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_TEST;
            this.msgType.msgId = (byte) 7;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgTestEnvRssiDetection(byte[] data) {
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
            this.frequencyPoint = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public Long getAntennaEnable() {
        return this.antennaEnable;
    }

    public void setAntennaEnable(Long antennaEnable) {
        this.antennaEnable = antennaEnable;
    }

    public int getFrequencyPoint() {
        return this.frequencyPoint;
    }

    public void setFrequencyPoint(int frequencyPoint) {
        this.frequencyPoint = frequencyPoint;
    }

    public Long getRssiFrequency() {
        return this.rssiFrequency;
    }

    public void setRssiFrequency(Long rssiFrequency) {
        this.rssiFrequency = rssiFrequency;
    }

    public int getCurrentRssi() {
        return this.currentRssi;
    }

    public void setCurrentRssi(int currentRssi) {
        this.currentRssi = currentRssi;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putLong(this.antennaEnable.longValue(), 32);
            buffer.putInt(this.frequencyPoint, 8);
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putLong(this.rssiFrequency.longValue(), 32);
            buffer.putInt(this.currentRssi, 8);
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        HashMap<Byte, String> dicErrorMsg = new HashMap<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgTestEnvRssiDetection.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "FrequencyPoint Param Reader Not Support.");
                put((byte) 2, "Port Param Reader Not Support.");
                put((byte) 3, "Phase-locked loop locking failed");
                put((byte) 4, "Other error");
            }
        };
        if (this.cData != null && this.cData.length > 0) {
            setRtCode(this.cData[0]);
            if (dicErrorMsg.containsKey(Byte.valueOf(this.cData[0]))) {
                setRtMsg(dicErrorMsg.get(Byte.valueOf(this.cData[0])));
            }
            BitBuffer buffer = BitBuffer.wrap(this.cData);
            buffer.position(8);
            this.rssiFrequency = Long.valueOf(buffer.getLongUnsigned(32));
            this.currentRssi = buffer.getIntUnsigned(8);
        }
    }

    public String toString() {
        return "MsgTestEnvRssiDetection{antennaEnable=" + this.antennaEnable + ", frequencyPoint=" + this.frequencyPoint + ", rssiFrequency=" + this.rssiFrequency + ", currentRssi=" + this.currentRssi + '}';
    }
}
