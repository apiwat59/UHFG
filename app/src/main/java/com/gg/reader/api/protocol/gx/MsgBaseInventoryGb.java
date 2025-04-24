package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgBaseInventoryGb extends Message {
    private Long antennaEnable;
    private ParamEpcFilter filter;
    private String hexPassword;
    private int inventoryMode;
    private ParamEpcReadTid readTid;
    private ParamGbReadUserdata readUserdata;
    private int safeCertificationFlag;

    public MsgBaseInventoryGb() {
        this.safeCertificationFlag = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 80;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseInventoryGb(byte[] data) {
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
            this.inventoryMode = buffer.getIntUnsigned(8);
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
                    byte[] paramData2 = new byte[2];
                    buffer.get(paramData2);
                    this.readTid = new ParamEpcReadTid(paramData2);
                } else if (pid == 3) {
                    byte[] paramData3 = new byte[4];
                    buffer.get(paramData3);
                    this.readUserdata = new ParamGbReadUserdata(paramData3);
                } else if (pid == 5) {
                    byte[] paramData4 = new byte[4];
                    buffer.get(paramData4);
                    this.hexPassword = HexUtils.bytes2HexString(paramData4);
                } else if (pid == 6) {
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

    public int getInventoryMode() {
        return this.inventoryMode;
    }

    public void setInventoryMode(int inventoryMode) {
        this.inventoryMode = inventoryMode;
    }

    public ParamEpcFilter getFilter() {
        return this.filter;
    }

    public void setFilter(ParamEpcFilter filter) {
        this.filter = filter;
    }

    public ParamEpcReadTid getReadTid() {
        return this.readTid;
    }

    public void setReadTid(ParamEpcReadTid readTid) {
        this.readTid = readTid;
    }

    public ParamGbReadUserdata getReadUserdata() {
        return this.readUserdata;
    }

    public void setReadUserdata(ParamGbReadUserdata readUserdata) {
        this.readUserdata = readUserdata;
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
        buffer.putLong(this.inventoryMode, 8);
        if (this.filter != null) {
            buffer.putInt(1, 8);
            byte[] tmpByte = this.filter.toBytes();
            buffer.putInt(tmpByte.length, 16);
            buffer.put(tmpByte);
        }
        if (this.readTid != null) {
            buffer.putInt(2, 8);
            byte[] tmpByte2 = this.readTid.toBytes();
            buffer.put(tmpByte2);
        }
        if (this.readUserdata != null) {
            buffer.putInt(3, 8);
            byte[] tmpByte3 = this.readUserdata.toBytes();
            buffer.put(tmpByte3);
        }
        if (!StringUtils.isNullOfEmpty(this.hexPassword)) {
            buffer.putInt(5, 8);
            byte[] tmpByte4 = HexUtils.hexString2Bytes(this.hexPassword);
            buffer.put(tmpByte4);
        }
        if (Integer.MAX_VALUE != this.safeCertificationFlag) {
            buffer.put(6, 8);
            buffer.put(this.safeCertificationFlag, 8);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseInventoryGb.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Antenna port parameter error.");
                put((byte) 2, "Filter parameter error.");
                put((byte) 3, "Tid parameter error.");
                put((byte) 4, "Userdata parameter error.");
                put((byte) 5, "Other error.");
            }
        };
        if (this.cData != null && this.cData.length == 1) {
            setRtCode(this.cData[0]);
            if (dicErrorMsg.containsKey(Byte.valueOf(this.cData[0]))) {
                setRtMsg(dicErrorMsg.get(Byte.valueOf(this.cData[0])));
            }
        }
    }

    public String toString() {
        return "MsgBaseInventoryGb{antennaEnable=" + this.antennaEnable + ", inventoryMode=" + this.inventoryMode + ", filter=" + this.filter + ", readTid=" + this.readTid + ", readUserdata=" + this.readUserdata + ", hexPassword='" + this.hexPassword + "', safeCertificationFlag=" + this.safeCertificationFlag + '}';
    }
}
