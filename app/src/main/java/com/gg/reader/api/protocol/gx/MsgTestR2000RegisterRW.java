package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.HashMap;

/* loaded from: classes.dex */
public class MsgTestR2000RegisterRW extends Message {
    private int address;
    private int operation;

    public MsgTestR2000RegisterRW() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_TEST;
            this.msgType.msgId = (byte) 21;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgTestR2000RegisterRW(byte[] data) {
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
            this.operation = buffer.getIntUnsigned(8);
            this.address = buffer.getIntUnsigned(16);
        } catch (Exception e) {
        }
    }

    public int getOperation() {
        return this.operation;
    }

    public void setOperation(int operation) {
        this.operation = operation;
    }

    public int getAddress() {
        return this.address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putInt(this.operation, 8);
        buffer.putInt(this.address, 16);
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        HashMap<Byte, String> dicErrorMsg = new HashMap<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgTestR2000RegisterRW.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Failure.");
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
