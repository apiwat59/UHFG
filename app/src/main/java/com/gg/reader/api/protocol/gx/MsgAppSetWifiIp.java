package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;
import kotlin.jvm.internal.LongCompanionObject;

/* loaded from: classes.dex */
public class MsgAppSetWifiIp extends Message {
    private int autoIp;
    private String dns1;
    private String dns2;
    private String gateway;
    private Long hotId;
    private String iP;
    private String mask;

    public MsgAppSetWifiIp() {
        this.iP = "0.0.0.0";
        this.mask = "0.0.0.0";
        this.gateway = "0.0.0.0";
        this.dns1 = "0.0.0.0";
        this.dns2 = "0.0.0.0";
        this.hotId = Long.valueOf(LongCompanionObject.MAX_VALUE);
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 53;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppSetWifiIp(byte[] data) {
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
            this.autoIp = buffer.getIntUnsigned(8);
            while (buffer.position() / 8 < data.length) {
                byte pid = buffer.getByte();
                switch (pid) {
                    case 1:
                        this.iP = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
                        break;
                    case 2:
                        this.mask = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
                        break;
                    case 3:
                        this.gateway = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
                        break;
                    case 4:
                        this.dns1 = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
                        break;
                    case 5:
                        this.dns2 = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
                        break;
                    case 6:
                        this.hotId = Long.valueOf(buffer.getLongUnsigned(32));
                        break;
                }
            }
        } catch (Exception e) {
        }
    }

    public int getAutoIp() {
        return this.autoIp;
    }

    public void setAutoIp(int autoIp) {
        this.autoIp = autoIp;
    }

    public String getiP() {
        return this.iP;
    }

    public void setiP(String iP) {
        this.iP = iP;
    }

    public String getMask() {
        return this.mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getGateway() {
        return this.gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getDns1() {
        return this.dns1;
    }

    public void setDns1(String dns1) {
        this.dns1 = dns1;
    }

    public String getDns2() {
        return this.dns2;
    }

    public void setDns2(String dns2) {
        this.dns2 = dns2;
    }

    public Long getHotId() {
        return this.hotId;
    }

    public void setHotId(Long hotId) {
        this.hotId = hotId;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.autoIp, 8);
        if (1 == this.autoIp) {
            buffer.putInt(1, 8);
            String[] iPs = this.iP.split("\\.");
            for (String i : iPs) {
                buffer.putInt(Integer.parseInt(i), 8);
            }
            buffer.putInt(2, 8);
            String[] masks = this.mask.split("\\.");
            for (String m : masks) {
                buffer.putInt(Integer.parseInt(m), 8);
            }
            buffer.putInt(3, 8);
            String[] gateways = this.gateway.split("\\.");
            for (String g : gateways) {
                buffer.putInt(Integer.parseInt(g), 8);
            }
            buffer.putInt(4, 8);
            String[] dns1s = this.dns1.split("\\.");
            for (String d1 : dns1s) {
                buffer.putInt(Integer.parseInt(d1), 8);
            }
            buffer.putInt(5, 8);
            String[] dns2s = this.dns2.split("\\.");
            for (String d2 : dns2s) {
                buffer.putInt(Integer.parseInt(d2), 8);
            }
        }
        if (LongCompanionObject.MAX_VALUE != this.hotId.longValue()) {
            buffer.putInt(6, 8);
            buffer.putLong(this.hotId.longValue(), 32);
        } else {
            buffer.put(6, 8);
            buffer.putLong(0L, 32);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppSetWifiIp.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "ReaderIp parameter error .");
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
