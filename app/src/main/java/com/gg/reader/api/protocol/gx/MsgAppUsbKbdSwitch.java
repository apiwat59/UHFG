package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgAppUsbKbdSwitch extends Message {
    private int kbdSwitch;
    private int operate;

    public MsgAppUsbKbdSwitch() {
        this.operate = Integer.MAX_VALUE;
        this.kbdSwitch = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 43;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppUsbKbdSwitch(byte[] data) {
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
            this.operate = buffer.getIntUnsigned(8);
            while (buffer.position() / 8 < data.length) {
                int pid = buffer.getIntUnsigned(8);
                if (pid == 1) {
                    this.kbdSwitch = buffer.getIntUnsigned(8);
                }
            }
        } catch (Exception e) {
        }
    }

    public int getOperate() {
        return this.operate;
    }

    public void setOperate(int operate) {
        this.operate = operate;
    }

    public int getKbdSwitch() {
        return this.kbdSwitch;
    }

    public void setKbdSwitch(int kbdSwitch) {
        this.kbdSwitch = kbdSwitch;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.operate, 8);
        if (Integer.MAX_VALUE != this.kbdSwitch) {
            buffer.putInt(1, 8);
            buffer.putLong(this.kbdSwitch, 8);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppUsbKbdSwitch.1
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
                    this.kbdSwitch = buffer.getIntUnsigned(8);
                }
            }
        }
    }
}
