package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;
import java.util.Arrays;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgTestChip extends Message {
    private Integer address;
    private byte[] dataContent;
    private String hexDataContent;

    public MsgTestChip() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_TEST;
            this.msgType.msgId = (byte) 49;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public Integer getAddress() {
        return this.address;
    }

    public void setAddress(Integer address) {
        this.address = address;
    }

    public byte[] getDataContent() {
        return this.dataContent;
    }

    public void setDataContent(byte[] dataContent) {
        this.dataContent = dataContent;
    }

    public String getHexDataContent() {
        return this.hexDataContent;
    }

    public void setHexDataContent(String hexDataContent) {
        this.hexDataContent = hexDataContent;
        if (!StringUtils.isNullOfEmpty(hexDataContent)) {
            this.dataContent = HexUtils.hexString2Bytes(this.hexDataContent);
        }
    }

    public MsgTestChip(byte[] data) {
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
            this.address = Integer.valueOf(buffer.getIntUnsigned(16));
            byte[] bArr = buffer.get(new byte[2]);
            this.dataContent = bArr;
            if (bArr != null && bArr.length > 0) {
                this.hexDataContent = HexUtils.bytes2HexString(bArr);
            }
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        if (Integer.MAX_VALUE != this.address.intValue()) {
            buffer.putLong(this.address.intValue(), 16);
        }
        byte[] bArr = this.dataContent;
        if (bArr != null) {
            buffer.put(bArr);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgTestChip.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Other Error");
            }
        };
        if (this.cData != null && this.cData.length > 0) {
            setRtCode(this.cData[0]);
            if (dicErrorMsg.containsKey(Byte.valueOf(this.cData[0]))) {
                setRtMsg(dicErrorMsg.get(Byte.valueOf(this.cData[0])));
            }
            if (this.address.intValue() == 770) {
                BitBuffer buffer = BitBuffer.wrap(this.cData);
                buffer.position(0);
                this.hexDataContent = HexUtils.bytes2HexString(buffer.get(new byte[4]));
                setRtCode((byte) 0);
            }
        }
    }

    public String toString() {
        return "MsgTestChip{address=" + this.address + ", dataContent=" + Arrays.toString(this.dataContent) + ", hexDataContent='" + this.hexDataContent + "'}";
    }
}
