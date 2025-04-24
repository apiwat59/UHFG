package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgBaseLockGb extends Message {
    private Long antennaEnable;
    private int area;
    private ParamEpcFilter filter;
    private String hexPassword;
    private int lockParam;
    private int safeCertificationFlag;

    public MsgBaseLockGb() {
        this.safeCertificationFlag = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 82;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseLockGb(byte[] data) {
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
            this.lockParam = buffer.getIntUnsigned(8);
            while (buffer.position() / 8 < data.length) {
                byte pid = buffer.getByte();
                if (pid == 1) {
                    int len = buffer.getIntUnsigned(16);
                    byte[] paramData = new byte[len];
                    if (len > 0) {
                        buffer.get(paramData);
                        this.filter = new ParamEpcFilter(paramData);
                    }
                } else if (pid == 2) {
                    byte[] paramData2 = new byte[4];
                    buffer.get(paramData2);
                    this.hexPassword = HexUtils.bytes2HexString(paramData2);
                } else if (pid == 3) {
                    this.safeCertificationFlag = buffer.getIntUnsigned(8);
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

    public int getLockParam() {
        return this.lockParam;
    }

    public void setLockParam(int lockParam) {
        this.lockParam = lockParam;
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

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.antennaEnable.longValue(), 32);
        buffer.putLong(this.area, 8);
        buffer.putLong(this.lockParam, 8);
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
        if (Integer.MAX_VALUE != this.safeCertificationFlag) {
            buffer.put(3, 8);
            buffer.put(this.safeCertificationFlag, 8);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseLockGb.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Antenna port parameter error.");
                put((byte) 2, "Filter parameter error.");
                put((byte) 3, "Lock parameter error.");
                put((byte) 4, "CRC check error.");
                put((byte) 5, "Underpower error.");
                put((byte) 6, "Data area overflow.");
                put((byte) 7, "Data area is locked.");
                put((byte) 8, "Access password error.");
                put((byte) 9, "Permission denied.");
                put((byte) 10, "Identify failure.");
                put((byte) 11, "Other error.");
                put((byte) 12, "Label is missing.");
                put((byte) 13, "Send command error.");
            }
        };
        if (this.cData != null && this.cData.length > 0) {
            setRtCode(this.cData[0]);
            if (dicErrorMsg.containsKey(Byte.valueOf(this.cData[0]))) {
                setRtMsg(dicErrorMsg.get(Byte.valueOf(this.cData[0])));
            }
        }
    }
}
