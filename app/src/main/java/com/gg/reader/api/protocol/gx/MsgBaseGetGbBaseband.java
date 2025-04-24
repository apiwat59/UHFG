package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgBaseGetGbBaseband extends Message {
    private int inventoryFlag;
    private ParamGbAntiCollision paramGbAntiCollision;
    private ParamGbBaseSpeed paramGbBaseSpeed;
    private int session;

    public MsgBaseGetGbBaseband() {
        this.session = Integer.MAX_VALUE;
        this.inventoryFlag = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) -29;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseGetGbBaseband(byte[] data) {
        this();
        if (data == null) {
            return;
        }
        try {
            if (data.length <= 0) {
                return;
            }
            BitBuffer buffer = BitBuffer.wrap(this.cData);
            buffer.position(0);
            byte[] paramData = new byte[1];
            buffer.get(paramData);
            this.paramGbBaseSpeed = new ParamGbBaseSpeed(paramData);
            byte[] paramData2 = new byte[1];
            buffer.get(paramData2);
            this.paramGbAntiCollision = new ParamGbAntiCollision(paramData2);
            this.session = buffer.getIntUnsigned(8);
            this.inventoryFlag = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public ParamGbBaseSpeed getParamGbBaseSpeed() {
        return this.paramGbBaseSpeed;
    }

    public void setParamGbBaseSpeed(ParamGbBaseSpeed paramGbBaseSpeed) {
        this.paramGbBaseSpeed = paramGbBaseSpeed;
    }

    public ParamGbAntiCollision getParamGbAntiCollision() {
        return this.paramGbAntiCollision;
    }

    public void setParamGbAntiCollision(ParamGbAntiCollision paramGbAntiCollision) {
        this.paramGbAntiCollision = paramGbAntiCollision;
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
            ParamGbBaseSpeed paramGbBaseSpeed = this.paramGbBaseSpeed;
            if (paramGbBaseSpeed != null) {
                byte[] bytes = paramGbBaseSpeed.toBytes();
                buffer.put(bytes);
            }
            ParamGbAntiCollision paramGbAntiCollision = this.paramGbAntiCollision;
            if (paramGbAntiCollision != null) {
                byte[] bytes2 = paramGbAntiCollision.toBytes();
                buffer.put(bytes2);
            }
            int i = this.session;
            if (Integer.MAX_VALUE != i) {
                buffer.putLong(i, 8);
            }
            int i2 = this.inventoryFlag;
            if (Integer.MAX_VALUE != i2) {
                buffer.putLong(i2, 8);
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
            byte[] paramData = new byte[1];
            buffer.get(paramData);
            this.paramGbBaseSpeed = new ParamGbBaseSpeed(paramData);
            byte[] paramData2 = new byte[1];
            buffer.get(paramData2);
            this.paramGbAntiCollision = new ParamGbAntiCollision(paramData2);
            this.session = buffer.getIntUnsigned(8);
            this.inventoryFlag = buffer.getIntUnsigned(8);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgBaseGetGbBaseband{paramGbBaseSpeed=" + this.paramGbBaseSpeed + ", paramGbAntiCollision=" + this.paramGbAntiCollision + ", session=" + this.session + ", inventoryFlag=" + this.inventoryFlag + '}';
    }
}
