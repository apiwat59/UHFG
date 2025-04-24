package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgBaseSetBaseband extends Message {
    private int baseSpeed;
    private int inventoryFlag;
    private int qValue;
    private int session;

    public MsgBaseSetBaseband() {
        this.baseSpeed = Integer.MAX_VALUE;
        this.qValue = Integer.MAX_VALUE;
        this.session = Integer.MAX_VALUE;
        this.inventoryFlag = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 11;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseSetBaseband(byte[] data) {
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
                    this.baseSpeed = buffer.getIntUnsigned(8);
                } else if (pid == 2) {
                    this.qValue = buffer.getIntUnsigned(8);
                } else if (pid == 3) {
                    this.session = buffer.getIntUnsigned(8);
                } else if (pid == 4) {
                    this.inventoryFlag = buffer.getIntUnsigned(8);
                }
            }
        } catch (Exception e) {
        }
    }

    public int getBaseSpeed() {
        return this.baseSpeed;
    }

    public void setBaseSpeed(int baseSpeed) {
        this.baseSpeed = baseSpeed;
    }

    public int getqValue() {
        return this.qValue;
    }

    public void setqValue(int qValue) {
        this.qValue = qValue;
    }

    public int getSession() {
        return this.session;
    }

    public void setSession(int session) {
        this.session = session;
    }

    public int getInventoryFlag() {
        return this.inventoryFlag;
    }

    public void setInventoryFlag(int inventoryFlag) {
        this.inventoryFlag = inventoryFlag;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        if (Integer.MAX_VALUE != this.baseSpeed) {
            buffer.putInt(1, 8);
            buffer.putLong(this.baseSpeed, 8);
        }
        if (Integer.MAX_VALUE != this.qValue) {
            buffer.putInt(2, 8);
            buffer.putLong(this.qValue, 8);
        }
        if (Integer.MAX_VALUE != this.session) {
            buffer.putInt(3, 8);
            buffer.putLong(this.session, 8);
        }
        if (Integer.MAX_VALUE != this.inventoryFlag) {
            buffer.putInt(4, 8);
            buffer.putLong(this.inventoryFlag, 8);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseSetBaseband.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Parameter not supported.");
                put((byte) 2, "Q value parameter error.");
                put((byte) 3, "Session parameter error.");
                put((byte) 4, "Inventory parameter error.");
                put((byte) 5, "Other error.");
                put((byte) 6, "Save failure.");
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
