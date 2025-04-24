package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgBaseDestroyGb extends Message {
    private Long antennaEnable;
    private ParamEpcFilter filter;
    private String hexPassword;
    private int safeCertificationFlag;

    public MsgBaseDestroyGb() {
        this.safeCertificationFlag = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 83;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseDestroyGb(byte[] data) {
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
                    this.hexPassword = HexUtils.int2Hex(buffer.getIntUnsigned(32));
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

    public int getSafeCertificationFlag() {
        return this.safeCertificationFlag;
    }

    public void setSafeCertificationFlag(int safeCertificationFlag) {
        this.safeCertificationFlag = safeCertificationFlag;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.antennaEnable.longValue(), 32);
        if (this.filter != null) {
            buffer.putInt(1, 8);
            byte[] tmpByte = this.filter.toBytes();
            buffer.putInt(tmpByte.length, 16);
            buffer.put(tmpByte);
        }
        if (!StringUtils.isNullOfEmpty(this.hexPassword)) {
            buffer.put(2, 8);
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
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseDestroyGb.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Antenna port parameter error.");
                put((byte) 2, "Filter parameter error.");
                put((byte) 3, "CRC check error.");
                put((byte) 4, "Underpower error.");
                put((byte) 5, "Destroy password error.");
                put((byte) 6, "Permission denied.");
                put((byte) 7, "Identify failure.");
                put((byte) 8, "Other error.");
                put((byte) 9, "Label is missing.");
                put((byte) 10, "Command error.");
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
