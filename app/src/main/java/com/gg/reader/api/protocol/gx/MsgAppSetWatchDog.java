package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgAppSetWatchDog extends Message {
    private int onOff;
    private int operationType;

    public MsgAppSetWatchDog() {
        this.onOff = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 75;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppSetWatchDog(byte[] data) {
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
            this.operationType = buffer.getIntUnsigned(8);
            while (buffer.position() / 8 < data.length) {
                int pid = buffer.getIntUnsigned(8);
                if (pid == 1) {
                    this.onOff = buffer.getIntUnsigned(8);
                }
            }
        } catch (Exception e) {
        }
    }

    public int getOperationType() {
        return this.operationType;
    }

    public void setOperationType(int operationType) {
        this.operationType = operationType;
    }

    public int getOnOff() {
        return this.onOff;
    }

    public void setOnOff(int onOff) {
        this.onOff = onOff;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.operationType, 8);
        if (Integer.MAX_VALUE != this.onOff) {
            buffer.putInt(1, 8);
            buffer.putLong(this.onOff, 8);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppSetWatchDog.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Failed.");
            }
        };
        if (this.cData != null && this.cData.length >= 1) {
            setRtCode(this.cData[0]);
            if (dicErrorMsg.containsKey(Byte.valueOf(this.cData[0]))) {
                setRtMsg(dicErrorMsg.get(Byte.valueOf(this.cData[0])));
            }
            if (this.cData.length > 1) {
                BitBuffer buffer = BitBuffer.wrap(this.cData);
                buffer.position(8);
                if (buffer.getIntUnsigned(8) == 1) {
                    this.onOff = buffer.getIntUnsigned(8);
                }
            }
        }
    }
}
