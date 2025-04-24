package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgBaseDestroyEpc extends Message {
    private Long antennaEnable;
    private ParamEpcFilter filter;
    private String hexPassword;

    public MsgBaseDestroyEpc() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 19;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseDestroyEpc(byte[] data) {
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
            byte[] bytes = buffer.get(new byte[4]);
            this.hexPassword = HexUtils.bytes2HexString(bytes);
            while (buffer.position() / 8 < data.length) {
                byte pid = buffer.getByte();
                if (pid == 1) {
                    int len = buffer.getIntUnsigned(16);
                    byte[] paramData = new byte[len];
                    if (len > 0) {
                        buffer.get(paramData);
                        this.filter = new ParamEpcFilter(paramData);
                    }
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

    public String getHexPassword() {
        return this.hexPassword;
    }

    public void setHexPassword(String hexPassword) {
        this.hexPassword = hexPassword;
    }

    public ParamEpcFilter getFilter() {
        return this.filter;
    }

    public void setFilter(ParamEpcFilter filter) {
        this.filter = filter;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.antennaEnable.longValue(), 32);
        if (!StringUtils.isNullOfEmpty(this.hexPassword)) {
            buffer.put(HexUtils.hexString2Bytes(this.hexPassword));
        }
        if (this.filter != null) {
            buffer.putInt(1, 8);
            byte[] tmpByte = this.filter.toBytes();
            buffer.putInt(tmpByte.length, 16);
            buffer.put(tmpByte);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseDestroyEpc.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Antenna port parameter error.");
                put((byte) 2, "Filter parameter error.");
                put((byte) 3, "Lock parameter error.");
                put((byte) 4, "CRC check error.");
                put((byte) 5, "Underpower error.");
                put((byte) 6, "Data area overflow.");
                put((byte) 7, "Data area locked.");
                put((byte) 8, "Access password error.");
                put((byte) 9, "Other error.");
                put((byte) 10, "Label missing.");
                put((byte) 11, "Command error.");
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
