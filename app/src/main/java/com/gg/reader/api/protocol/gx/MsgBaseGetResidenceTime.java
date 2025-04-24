package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgBaseGetResidenceTime extends Message {
    private Long antResidenceTime;
    private Long frqResidenceTime;

    public MsgBaseGetResidenceTime() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) -31;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseGetResidenceTime(byte[] data) {
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
                    this.antResidenceTime = Long.valueOf(buffer.getLongUnsigned(16));
                } else if (pid == 2) {
                    this.frqResidenceTime = Long.valueOf(buffer.getLongUnsigned(16));
                }
            }
        } catch (Exception e) {
        }
    }

    public Long getAntResidenceTime() {
        return this.antResidenceTime;
    }

    public void setAntResidenceTime(Long antResidenceTime) {
        this.antResidenceTime = antResidenceTime;
    }

    public Long getFrqResidenceTime() {
        return this.frqResidenceTime;
    }

    public void setFrqResidenceTime(Long frqResidenceTime) {
        this.frqResidenceTime = frqResidenceTime;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.put(1, 8);
            buffer.putLong(this.antResidenceTime.longValue(), 16);
            buffer.put(2, 8);
            buffer.putLong(this.frqResidenceTime.longValue(), 16);
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
            while (buffer.position() / 8 < this.cData.length) {
                int pid = buffer.getIntUnsigned(8);
                if (pid == 1) {
                    this.antResidenceTime = Long.valueOf(buffer.getLongUnsigned(16));
                } else if (pid == 2) {
                    this.frqResidenceTime = Long.valueOf(buffer.getLongUnsigned(16));
                }
            }
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgBaseGetResidenceTime{antResidenceTime=" + this.antResidenceTime + ", frqResidenceTime=" + this.frqResidenceTime + '}';
    }
}
