package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgBaseGetBaseband extends Message {
    private int baseSpeed;
    private int inventoryFlag;
    private int qValue;
    private int session;

    public MsgBaseGetBaseband() {
        this.baseSpeed = Integer.MAX_VALUE;
        this.qValue = Integer.MAX_VALUE;
        this.session = Integer.MAX_VALUE;
        this.inventoryFlag = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 12;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseGetBaseband(byte[] data) {
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
            this.baseSpeed = buffer.getIntUnsigned(8);
            this.qValue = buffer.getIntUnsigned(8);
            this.session = buffer.getIntUnsigned(8);
            this.inventoryFlag = buffer.getIntUnsigned(8);
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
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            int i = this.baseSpeed;
            if (Integer.MAX_VALUE != i) {
                buffer.putLong(i, 8);
            }
            int i2 = this.qValue;
            if (Integer.MAX_VALUE != i2) {
                buffer.putLong(i2, 8);
            }
            int i3 = this.session;
            if (Integer.MAX_VALUE != i3) {
                buffer.putLong(i3, 8);
            }
            int i4 = this.inventoryFlag;
            if (Integer.MAX_VALUE != i4) {
                buffer.putLong(i4, 8);
            }
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
            this.baseSpeed = buffer.getIntUnsigned(8);
            this.qValue = buffer.getIntUnsigned(8);
            this.session = buffer.getIntUnsigned(8);
            this.inventoryFlag = buffer.getIntUnsigned(8);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgBaseGetBaseband{baseSpeed=" + this.baseSpeed + ", qValue=" + this.qValue + ", session=" + this.session + ", inventoryFlag=" + this.inventoryFlag + '}';
    }
}
