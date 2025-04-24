package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgBaseSetGbBaseband extends Message {
    private int inventoryFlag;
    private ParamGbAntiCollision paramGbAntiCollision;
    private ParamGbBaseSpeed paramGbBaseSpeed;
    private int session;

    public MsgBaseSetGbBaseband() {
        this.session = Integer.MAX_VALUE;
        this.inventoryFlag = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) -30;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseSetGbBaseband(byte[] data) {
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
                    int len = buffer.getIntUnsigned(8);
                    byte[] paramData = new byte[len];
                    buffer.get(paramData);
                    this.paramGbBaseSpeed = new ParamGbBaseSpeed(paramData);
                } else if (pid == 2) {
                    int length = buffer.getIntUnsigned(8);
                    byte[] paramData2 = new byte[length];
                    buffer.get(paramData2);
                    this.paramGbAntiCollision = new ParamGbAntiCollision(paramData2);
                } else if (pid == 3) {
                    this.session = buffer.getIntUnsigned(8);
                } else if (pid == 4) {
                    this.inventoryFlag = buffer.getIntUnsigned(8);
                }
            }
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
        BitBuffer buffer = BitBuffer.allocateDynamic();
        if (this.paramGbBaseSpeed != null) {
            buffer.putInt(1, 8);
            byte[] tempByte = this.paramGbBaseSpeed.toBytes();
            buffer.put(tempByte);
        }
        if (this.paramGbAntiCollision != null) {
            buffer.putInt(2, 8);
            byte[] tempByte2 = this.paramGbAntiCollision.toBytes();
            buffer.put(tempByte2);
        }
        if (Integer.MAX_VALUE != this.session) {
            buffer.putInt(3, 8);
            buffer.putInt(this.session, 8);
        }
        if (Integer.MAX_VALUE != this.inventoryFlag) {
            buffer.putInt(4, 8);
            buffer.putInt(this.inventoryFlag, 8);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseSetGbBaseband.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Baseband rate not support.");
                put((byte) 2, "Session parameter error.");
                put((byte) 3, "Inventory parameter error.");
                put((byte) 4, "Other error.");
                put((byte) 5, "Save failure.");
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
