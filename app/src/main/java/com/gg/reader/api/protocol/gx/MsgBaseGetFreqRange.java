package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgBaseGetFreqRange extends Message {
    private int freqRangeIndex;

    public MsgBaseGetFreqRange() {
        this.freqRangeIndex = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 4;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseGetFreqRange(byte[] data) {
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
            this.freqRangeIndex = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public int getFreqRangeIndex() {
        return this.freqRangeIndex;
    }

    public void setFreqRangeIndex(int freqRangeIndex) {
        this.freqRangeIndex = freqRangeIndex;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putLong(this.freqRangeIndex, 8);
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        if (this.cData != null && this.cData.length == 1) {
            this.freqRangeIndex = this.cData[0];
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgBaseGetFreqRange{freqRangeIndex=" + this.freqRangeIndex + '}';
    }
}
