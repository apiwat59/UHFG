package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgAppImportWhiteList extends Message {
    private byte[] packetContent;
    private Long packetNumber;

    public MsgAppImportWhiteList() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 33;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppImportWhiteList(byte[] data) {
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
            this.packetNumber = Long.valueOf(buffer.getLongUnsigned(32));
            int len = buffer.getIntUnsigned(16);
            byte[] packetData = new byte[len];
            if (len > 0) {
                this.packetContent = buffer.get(packetData);
            }
        } catch (Exception e) {
        }
    }

    public Long getPacketNumber() {
        return this.packetNumber;
    }

    public void setPacketNumber(Long packetNumber) {
        this.packetNumber = packetNumber;
    }

    public byte[] getPacketContent() {
        return this.packetContent;
    }

    public void setPacketContent(byte[] packetContent) {
        this.packetContent = packetContent;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.put(this.packetNumber.longValue(), 32);
            byte[] bArr = this.packetContent;
            if (bArr != null && bArr.length > 0) {
                buffer.putInt(bArr.length, 16);
                buffer.put(this.packetContent, 8);
            } else {
                buffer.putInt(0, 16);
            }
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppImportWhiteList.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Fail.");
            }
        };
        if (this.cData != null && this.cData.length > 0) {
            BitBuffer buffer = BitBuffer.wrap(this.cData);
            buffer.position(0);
            this.packetNumber = Long.valueOf(buffer.getLong(32));
            byte code = buffer.getByteUnsigned(8);
            setRtCode(code);
            if (dicErrorMsg.containsKey(Byte.valueOf(code))) {
                setRtMsg(dicErrorMsg.get(Byte.valueOf(code)));
            }
        }
    }
}
