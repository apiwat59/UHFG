package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgBaseWrite6b extends Message {
    private Long antennaEnable;
    private byte[] bMatchTid;
    private byte[] bwriteData;
    private int errorIndex;
    private String hexMatchTid;
    private String hexWriteData;
    private int start;

    public MsgBaseWrite6b() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 65;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseWrite6b(byte[] data) {
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
            this.start = buffer.getIntUnsigned(8);
            int len = buffer.getIntUnsigned(16);
            if (len > 0) {
                byte[] bArr2 = buffer.get(new byte[len]);
                this.bwriteData = bArr2;
                this.hexWriteData = HexUtils.bytes2HexString(bArr2);
            }
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

    public int getStart() {
        return this.start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getHexWriteData() {
        return this.hexWriteData;
    }

    public void setHexWriteData(String hexWriteData) {
        if (!StringUtils.isNullOfEmpty(hexWriteData)) {
            this.hexWriteData = hexWriteData;
            this.bwriteData = HexUtils.hexString2Bytes(hexWriteData);
        }
    }

    public byte[] getBwriteData() {
        return this.bwriteData;
    }

    public void setBwriteData(byte[] bwriteData) {
        this.bwriteData = bwriteData;
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
        buffer.put(this.bMatchTid);
        buffer.putLong(this.start, 8);
        byte[] bArr = this.bwriteData;
        if (bArr != null && bArr.length > 0) {
            buffer.putInt(bArr.length, 16);
            buffer.put(this.bwriteData);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseWrite6b.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Antenna port parameter error.");
                put((byte) 2, "Write parameter error.");
                put((byte) 3, "Other error.");
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
                    this.errorIndex = buffer.getIntUnsigned(8);
                }
            }
        }
    }
}
