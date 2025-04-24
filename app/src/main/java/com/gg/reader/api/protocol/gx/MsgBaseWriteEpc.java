package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgBaseWriteEpc extends Message {
    private Long antennaEnable;
    private int area;
    private int block;
    private byte[] bwriteData;
    private int eBookFlag;
    private int errorIndex;
    private ParamEpcFilter filter;
    private String hexPassword;
    private String hexWriteData;
    private int start;
    private int stayCarrierWave;

    public MsgBaseWriteEpc() {
        this.block = 0;
        this.stayCarrierWave = Integer.MAX_VALUE;
        this.eBookFlag = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 17;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseWriteEpc(byte[] data) {
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
            this.area = buffer.getIntUnsigned(8);
            this.start = buffer.getIntUnsigned(16);
            int len = buffer.getIntUnsigned(16);
            if (len > 0) {
                byte[] bArr = buffer.get(new byte[len]);
                this.bwriteData = bArr;
                this.hexWriteData = HexUtils.bytes2HexString(bArr);
            }
            while (buffer.position() / 8 < data.length) {
                byte pid = buffer.getByte();
                if (pid == 1) {
                    int filLen = buffer.getIntUnsigned(16);
                    byte[] paramData = new byte[filLen];
                    if (filLen > 0) {
                        buffer.get(paramData);
                        this.filter = new ParamEpcFilter(paramData);
                    }
                } else if (pid == 2) {
                    byte[] paramData2 = new byte[4];
                    buffer.get(paramData2);
                    this.hexPassword = HexUtils.bytes2HexString(paramData2);
                } else if (pid == 3) {
                    this.block = buffer.getIntUnsigned(8);
                } else if (pid == 4) {
                    this.stayCarrierWave = buffer.getIntUnsigned(8);
                }
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

    public int getArea() {
        return this.area;
    }

    public void setArea(int area) {
        this.area = area;
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

    public ParamEpcFilter getFilter() {
        return this.filter;
    }

    public void setFilter(ParamEpcFilter filter) {
        this.filter = filter;
    }

    public String getHexPassword() {
        return this.hexPassword;
    }

    public void setHexPassword(String hexPassword) {
        this.hexPassword = hexPassword;
    }

    public int getBlock() {
        return this.block;
    }

    public void setBlock(int block) {
        this.block = block;
    }

    public int getStayCarrierWave() {
        return this.stayCarrierWave;
    }

    public void setStayCarrierWave(int stayCarrierWave) {
        this.stayCarrierWave = stayCarrierWave;
    }

    public int geteBookFlag() {
        return this.eBookFlag;
    }

    public void seteBookFlag(int eBookFlag) {
        this.eBookFlag = eBookFlag;
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
        buffer.putLong(this.area, 8);
        buffer.putLong(this.start, 16);
        byte[] bArr = this.bwriteData;
        if (bArr != null && bArr.length > 0) {
            buffer.putInt(bArr.length, 16);
            buffer.put(this.bwriteData);
        }
        if (this.filter != null) {
            buffer.putInt(1, 8);
            byte[] tmpByte = this.filter.toBytes();
            buffer.putInt(tmpByte.length, 16);
            buffer.put(tmpByte);
        }
        if (!StringUtils.isNullOfEmpty(this.hexPassword)) {
            buffer.putInt(2, 8);
            buffer.put(HexUtils.hexString2Bytes(this.hexPassword));
        }
        if (this.block != 0) {
            buffer.putInt(3, 8);
            buffer.putLong(this.block, 8);
        }
        if (Integer.MAX_VALUE != this.stayCarrierWave) {
            buffer.putInt(4, 8);
            buffer.putInt(this.stayCarrierWave, 8);
        }
        if (Integer.MAX_VALUE != this.eBookFlag) {
            buffer.putInt(5, 8);
            buffer.putInt(this.eBookFlag, 8);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseWriteEpc.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Antenna port parameter error.");
                put((byte) 2, "Filter parameter error.");
                put((byte) 3, "Write parameter error.");
                put((byte) 4, "CRC check error.");
                put((byte) 5, "Underpower error.");
                put((byte) 6, "Data area overflow.");
                put((byte) 7, "Data area locked.");
                put((byte) 8, "Access password error.");
                put((byte) 9, "Other error.");
                put((byte) 10, "Label is missing.");
                put((byte) 11, "Send command error.");
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
                    this.errorIndex = buffer.getIntUnsigned(16);
                }
            }
        }
    }
}
