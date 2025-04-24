package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgBaseLock6b extends Message {
    private Long antennaEnable;
    private byte[] bMatchTid;
    private int errorIndex;
    private String hexMatchTid;
    private int lockIndex;

    public MsgBaseLock6b() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 66;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseLock6b(byte[] data) {
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
            this.antennaEnable = Long.valueOf(buffer.getLongUnsigned(32));
            byte[] bArr = buffer.get(new byte[8]);
            this.bMatchTid = bArr;
            this.hexMatchTid = HexUtils.bytes2HexString(bArr);
            this.lockIndex = buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public Long getAntennaEnable() {
        return this.antennaEnable;
    }

    public void setAntennaEnable(Long antennaEnable) {
        this.antennaEnable = antennaEnable;
    }

    public String getHexMatchTid() {
        return this.hexMatchTid;
    }

    public void setHexMatchTid(String hexMatchTid) {
        if (!StringUtils.isNullOfEmpty(hexMatchTid)) {
            this.hexMatchTid = hexMatchTid;
            this.bMatchTid = HexUtils.hexString2Bytes(hexMatchTid);
        }
    }

    public byte[] getbMatchTid() {
        return this.bMatchTid;
    }

    public void setbMatchTid(byte[] bMatchTid) {
        this.bMatchTid = bMatchTid;
    }

    public int getLockIndex() {
        return this.lockIndex;
    }

    public void setLockIndex(int lockIndex) {
        this.lockIndex = lockIndex;
    }

    public int getErrorIndex() {
        return this.errorIndex;
    }

    public void setErrorIndex(int errorIndex) {
        this.errorIndex = errorIndex;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.antennaEnable.longValue(), 32);
        byte[] bArr = this.bMatchTid;
        if (bArr != null && bArr.length > 0) {
            buffer.put(bArr);
        }
        buffer.putLong(this.lockIndex, 8);
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseLock6b.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Other error.");
            }
        };
        if (this.cData != null && this.cData.length > 0) {
            setRtCode(this.cData[0]);
            if (dicErrorMsg.containsKey(Byte.valueOf(this.cData[0]))) {
                setRtMsg(dicErrorMsg.get(Byte.valueOf(this.cData[0])));
            }
            if (this.cData.length > 1) {
                BitBuffer buffer = BitBuffer.wrap(this.cData);
                buffer.position(8);
                if (buffer.getIntUnsigned(8) == 1) {
                    this.errorIndex = buffer.getIntUnsigned(8);
                }
            }
        }
    }
}
