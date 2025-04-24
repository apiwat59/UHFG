package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;
import java.util.Arrays;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgBaseInventory6b extends Message {
    private Long antennaEnable;
    private int area;
    private byte[] bMatchTid;
    private String hexMatchTid;
    private int inventoryMode;
    private Param6bReadUserdata readUserdata;

    public MsgBaseInventory6b() {
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 64;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseInventory6b(byte[] data) {
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
            this.area = buffer.getIntUnsigned(8);
            while (buffer.position() / 8 < data.length) {
                byte pid = buffer.getByte();
                if (pid == 1) {
                    byte[] paramData = new byte[2];
                    buffer.get(paramData);
                    this.readUserdata = new Param6bReadUserdata(paramData);
                } else if (pid == 2) {
                    byte[] bArr = buffer.get(new byte[8]);
                    this.bMatchTid = bArr;
                    this.hexMatchTid = HexUtils.bytes2HexString(bArr);
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

    public int getArea() {
        return this.area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public Param6bReadUserdata getReadUserdata() {
        return this.readUserdata;
    }

    public void setReadUserdata(Param6bReadUserdata readUserdata) {
        this.readUserdata = readUserdata;
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

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.antennaEnable.longValue(), 32);
        buffer.putLong(this.inventoryMode, 8);
        buffer.putLong(this.area, 8);
        if (this.readUserdata != null) {
            buffer.putInt(1, 8);
            buffer.put(this.readUserdata.toBytes());
        }
        byte[] tmpByte = this.bMatchTid;
        if (tmpByte != null && tmpByte.length > 0) {
            buffer.putInt(2, 8);
            buffer.put(this.bMatchTid);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseInventory6b.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Antenna port parameter error.");
                put((byte) 2, "Read parameter error.");
                put((byte) 3, "Userdata parameter error.");
                put((byte) 4, "Other error.");
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
        return "MsgBaseInventory6b{antennaEnable=" + this.antennaEnable + ", inventoryMode=" + this.inventoryMode + ", area=" + this.area + ", readUserdata=" + this.readUserdata + ", hexMatchTid='" + this.hexMatchTid + "', bMatchTid=" + Arrays.toString(this.bMatchTid) + '}';
    }
}
