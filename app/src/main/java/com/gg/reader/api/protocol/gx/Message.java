package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.Crc16Utils;
import com.gg.reader.api.utils.GLog;
import com.gg.reader.api.utils.HexUtils;

/* loaded from: classes.dex */
public class Message {
    public static final byte HEAD = 90;
    public static final int MSG_MAX_LEN = 1024;
    public byte[] cData;
    public byte[] crc;
    public byte[] crcData;
    public int dataLen;
    public byte[] msgData;
    public MsgType msgType;
    public int rs485Address;
    private byte rtCode;
    private String rtMsg;

    public byte getRtCode() {
        return this.rtCode;
    }

    public String getRtMsg() {
        return this.rtMsg;
    }

    public void setRtCode(byte rtCode) {
        this.rtCode = rtCode;
    }

    public void setRtMsg(String rtMsg) {
        this.rtMsg = rtMsg;
    }

    public Message() {
        this.msgType = null;
        this.rs485Address = 0;
        this.dataLen = 0;
        this.cData = null;
        this.crc = null;
        this.crcData = null;
        this.msgData = null;
        this.rtCode = (byte) -1;
        this.rtMsg = "";
    }

    public Message(byte[] data) {
        this.msgType = null;
        this.rs485Address = 0;
        this.dataLen = 0;
        this.cData = null;
        this.crc = null;
        this.crcData = null;
        this.msgData = null;
        this.rtCode = (byte) -1;
        this.rtMsg = "";
        try {
            this.msgData = data;
            this.crcData = new byte[data.length - 3];
            BitBuffer buffer = BitBuffer.wrap(data);
            buffer.position(8);
            byte[] bMsgType = new byte[4];
            MsgType msgType = new MsgType(buffer.get(bMsgType));
            this.msgType = msgType;
            if (msgType.mt_13.equals("1")) {
                this.rs485Address = buffer.getIntUnsigned(8);
            }
            int intUnsigned = buffer.getIntUnsigned(16);
            this.dataLen = intUnsigned;
            if (intUnsigned > 0) {
                byte[] bArr = new byte[intUnsigned];
                this.cData = bArr;
                this.cData = buffer.get(bArr);
            }
            int oPosition = buffer.position();
            buffer.position(8);
            this.crcData = buffer.get(this.crcData);
            buffer.position(oPosition);
            byte[] bArr2 = new byte[2];
            this.crc = bArr2;
            this.crc = buffer.get(bArr2);
        } catch (Exception e) {
            GLog.e("Message unpacking error :" + e.getStackTrace());
        }
    }

    public byte[] toBytes() {
        return toBytes(false);
    }

    public byte[] toBytes(boolean is485) {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.put(HEAD);
        buffer.put(this.msgType.toBytes());
        if (is485) {
            buffer.putInt(this.rs485Address, 8);
        }
        buffer.putInt(this.dataLen, 16);
        byte[] bArr = this.cData;
        if (bArr != null && bArr.length > 0 && bArr.length == this.dataLen) {
            buffer.put(bArr);
        }
        int oPosition = buffer.position();
        this.crcData = new byte[(buffer.position() / 8) - 1];
        buffer.position(8);
        this.crcData = buffer.get(this.crcData);
        buffer.position(oPosition);
        byte[] short2Bytes = HexUtils.short2Bytes(Crc16Utils.CRC_XModem(this.crcData));
        this.crc = short2Bytes;
        buffer.put(short2Bytes);
        byte[] asByteArray = buffer.asByteArray();
        this.msgData = asByteArray;
        return asByteArray;
    }

    public void pack() {
        this.dataLen = 0;
    }

    public void ackPack() {
        this.crcData = new byte[]{this.rtCode};
        this.dataLen = 1;
    }

    public void ackUnpack() {
    }

    public void ackUnpack(byte[] data) {
        this.cData = data;
        ackUnpack();
    }

    public boolean checkCrc() {
        try {
            byte[] bArr = this.crcData;
            if (bArr != null && this.crc != null) {
                byte[] bCrc = HexUtils.short2Bytes(Crc16Utils.CRC_XModem(bArr));
                for (int i = 0; i < bCrc.length; i++) {
                    if (this.crc[i] != bCrc[i]) {
                        return false;
                    }
                }
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }
}
