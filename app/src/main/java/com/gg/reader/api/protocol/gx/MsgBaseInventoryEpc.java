package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.StringUtils;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgBaseInventoryEpc extends Message {
    private Long antennaEnable;
    private int ctesius;
    private int emSensor;
    private ParamEpcFilter filter;
    private String hexPassword;
    private int inventoryMode;
    private int monzaQtPeek;
    private ParamFastId paramFastId;
    private int quanray;
    private ParamEpcReadEpc readEpc;
    private ParamEpcReadReserved readReserved;
    private ParamEpcReadTid readTid;
    private ParamEpcReadUserdata readUserdata;
    private int rfmicron;
    private int seed;

    public MsgBaseInventoryEpc() {
        this.monzaQtPeek = Integer.MAX_VALUE;
        this.rfmicron = Integer.MAX_VALUE;
        this.emSensor = Integer.MAX_VALUE;
        this.ctesius = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_BASE;
            this.msgType.msgId = (byte) 16;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgBaseInventoryEpc(byte[] data) {
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
                if (pid == 18) {
                    this.ctesius = buffer.getIntUnsigned(8);
                } else if (pid == 19) {
                    this.seed = buffer.getIntUnsigned(8);
                } else if (pid != 22) {
                    switch (pid) {
                        case 1:
                            int len = buffer.getIntUnsigned(16);
                            byte[] paramData = new byte[len];
                            buffer.get(paramData);
                            this.filter = new ParamEpcFilter(paramData);
                            break;
                        case 2:
                            byte[] paramData2 = new byte[2];
                            buffer.get(paramData2);
                            this.readTid = new ParamEpcReadTid(paramData2);
                            break;
                        case 3:
                            byte[] paramData3 = new byte[3];
                            buffer.get(paramData3);
                            this.readUserdata = new ParamEpcReadUserdata(paramData3);
                            break;
                        case 4:
                            byte[] paramData4 = new byte[3];
                            buffer.get(paramData4);
                            this.readReserved = new ParamEpcReadReserved(paramData4);
                            break;
                        case 5:
                            byte[] paramData5 = new byte[4];
                            buffer.get(paramData5);
                            this.hexPassword = HexUtils.bytes2HexString(paramData5);
                            break;
                        case 6:
                            this.monzaQtPeek = buffer.getIntUnsigned(8);
                            break;
                        case 7:
                            this.rfmicron = buffer.getIntUnsigned(8);
                            break;
                        case 8:
                            this.emSensor = buffer.getIntUnsigned(8);
                            break;
                        case 9:
                            byte[] paramData6 = new byte[3];
                            buffer.get(paramData6);
                            this.readEpc = new ParamEpcReadEpc(paramData6);
                            break;
                        case 10:
                            byte[] paramData7 = new byte[2];
                            buffer.get(paramData7);
                            this.paramFastId = new ParamFastId(paramData7);
                            break;
                    }
                } else {
                    int len2 = buffer.getIntUnsigned(8);
                    this.quanray = len2;
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

    public ParamEpcReadUserdata getReadUserdata() {
        return this.readUserdata;
    }

    public void setReadUserdata(ParamEpcReadUserdata readUserdata) {
        this.readUserdata = readUserdata;
    }

    public ParamEpcReadReserved getReadReserved() {
        return this.readReserved;
    }

    public void setReadReserved(ParamEpcReadReserved readReserved) {
        this.readReserved = readReserved;
    }

    public String getHexPassword() {
        return this.hexPassword;
    }

    public void setHexPassword(String hexPassword) {
        this.hexPassword = hexPassword;
    }

    public int getMonzaQtPeek() {
        return this.monzaQtPeek;
    }

    public void setMonzaQtPeek(int monzaQtPeek) {
        this.monzaQtPeek = monzaQtPeek;
    }

    public int getRfmicron() {
        return this.rfmicron;
    }

    public void setRfmicron(int rfmicron) {
        this.rfmicron = rfmicron;
    }

    public int getEmSensor() {
        return this.emSensor;
    }

    public void setEmSensor(int emSensor) {
        this.emSensor = emSensor;
    }

    public int getCtesius() {
        return this.ctesius;
    }

    public void setCtesius(int ctesius) {
        this.ctesius = ctesius;
    }

    public ParamEpcReadEpc getReadEpc() {
        return this.readEpc;
    }

    public void setReadEpc(ParamEpcReadEpc readEpc) {
        this.readEpc = readEpc;
    }

    public int getSeed() {
        return this.seed;
    }

    public void setSeed(int seed) {
        this.seed = seed;
    }

    public ParamFastId getParamFastId() {
        return this.paramFastId;
    }

    public void setParamFastId(ParamFastId paramFastId) {
        this.paramFastId = paramFastId;
    }

    public int getQuanray() {
        return this.quanray;
    }

    public void setQuanray(int quanray) {
        this.quanray = quanray;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        try {
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
            if (this.readReserved != null) {
                buffer.putInt(4, 8);
                byte[] tmpByte4 = this.readReserved.toBytes();
                buffer.put(tmpByte4);
            }
            if (!StringUtils.isNullOfEmpty(this.hexPassword)) {
                buffer.putInt(5, 8);
                byte[] tmpByte5 = HexUtils.hexString2Bytes(this.hexPassword);
                buffer.put(tmpByte5);
            }
            if (this.monzaQtPeek != Integer.MAX_VALUE) {
                buffer.putInt(6, 8);
                buffer.putLong(this.monzaQtPeek, 8);
            }
            if (this.rfmicron != Integer.MAX_VALUE) {
                buffer.putInt(7, 8);
                buffer.putLong(this.rfmicron, 8);
            }
            if (this.emSensor != Integer.MAX_VALUE) {
                buffer.putInt(8, 8);
                buffer.putLong(this.emSensor, 8);
            }
            if (this.readEpc != null) {
                buffer.putInt(9, 8);
                byte[] tmpByte6 = this.readEpc.toBytes();
                buffer.put(tmpByte6);
            }
            if (this.paramFastId != null) {
                buffer.putInt(10, 8);
                byte[] tmpByte7 = this.paramFastId.toBytes();
                buffer.put(tmpByte7);
            }
            if (this.ctesius != Integer.MAX_VALUE) {
                buffer.putInt(18, 8);
                buffer.putInt(this.ctesius, 8);
            }
            if (this.seed != 0) {
                buffer.putInt(19, 8);
                buffer.putInt(this.seed, 8);
            }
            if (this.quanray != 0) {
                buffer.putInt(22, 8);
                buffer.putInt(this.quanray, 8);
            }
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgBaseInventoryEpc.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Antenna port parameter error.");
                put((byte) 2, "Filter parameter error.");
                put((byte) 3, "TID parameter error.");
                put((byte) 4, "User parameter error.");
                put((byte) 5, "Reserve parameter error.");
                put((byte) 6, "Other error.");
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
        return "MsgBaseInventoryEpc{antennaEnable=" + this.antennaEnable + ", inventoryMode=" + this.inventoryMode + ", filter=" + this.filter + ", readTid=" + this.readTid + ", readUserdata=" + this.readUserdata + ", readReserved=" + this.readReserved + ", hexPassword='" + this.hexPassword + "', monzaQtPeek=" + this.monzaQtPeek + ", rfmicron=" + this.rfmicron + ", emSensor=" + this.emSensor + ", readEpc=" + this.readEpc + ", paramFastId=" + this.paramFastId + ", ctesius=" + this.ctesius + ", seed=" + this.seed + ", quanray=" + this.quanray + '}';
    }
}
