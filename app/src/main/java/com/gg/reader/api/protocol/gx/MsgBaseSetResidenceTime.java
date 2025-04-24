package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;
import kotlin.jvm.internal.LongCompanionObject;

/* loaded from: classes.dex */
public class MsgBaseSetResidenceTime extends Message {
    private Long antResidenceTime;
    private Long frqResidenceTime;

    public MsgBaseSetResidenceTime() {
        Long valueOf = Long.valueOf(LongCompanionObject.MAX_VALUE);
        this.antResidenceTime = valueOf;
        this.frqResidenceTime = valueOf;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) -32;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseSetResidenceTime(byte[] data) {
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
        BitBuffer buffer = BitBuffer.allocateDynamic();
        if (2147483647L != this.antResidenceTime.longValue()) {
            buffer.putInt(1, 8);
            buffer.putLong(this.antResidenceTime.longValue(), 16);
        }
        if (2147483647L != this.frqResidenceTime.longValue()) {
            buffer.putInt(2, 8);
            buffer.putLong(this.frqResidenceTime.longValue(), 16);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseSetResidenceTime.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Mode parameter error.");
                put((byte) 2, "Other parameter error.");
                put((byte) 3, "Save Failure.");
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
