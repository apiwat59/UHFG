package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.HashMap;

/* loaded from: classes.dex */
public class MsgTestDCbias extends Message {
    private int optionType;
    private int param;

    public MsgTestDCbias() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_TEST;
            this.msgType.msgId = (byte) 1;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgTestDCbias(byte[] data) {
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
            this.param = buffer.getInt(8);
            this.optionType = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public int getParam() {
        return this.param;
    }

    public void setParam(int param) {
        this.param = param;
    }

    public int getOptionType() {
        return this.optionType;
    }

    public void setOptionType(int optionType) {
        this.optionType = optionType;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.param, 8);
        buffer.putLong(this.optionType, 8);
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        HashMap<Byte, String> dicErrorMsg = new HashMap<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgTestDCbias.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Save failure.");
                put((byte) 2, "Other error.");
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
