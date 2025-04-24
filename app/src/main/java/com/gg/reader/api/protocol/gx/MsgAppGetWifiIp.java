package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import kotlin.jvm.internal.LongCompanionObject;

/* loaded from: classes.dex */
public class MsgAppGetWifiIp extends Message {
    private int autoIp;
    private String dns1;
    private String dns2;
    private String gateway;
    private Long hotId;
    private String iP;
    private String mask;

    public MsgAppGetWifiIp() {
        this.hotId = Long.valueOf(LongCompanionObject.MAX_VALUE);
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 54;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetWifiIp(byte[] data) {
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
            this.iP = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
            this.mask = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
            this.gateway = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
            this.dns1 = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
            this.dns2 = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
        } catch (Exception e) {
        }
    }

    public Long getHotId() {
        return this.hotId;
    }

    public void setHotId(Long hotId) {
        this.hotId = hotId;
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

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            if (LongCompanionObject.MAX_VALUE != this.hotId.longValue()) {
                buffer.putInt(1, 8);
                buffer.putLong(this.hotId.longValue(), 32);
            }
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putLong(this.autoIp, 8);
            String str = this.iP;
            if (str != null) {
                String[] iPs = str.split("\\.");
                for (String i : iPs) {
                    buffer.putInt(Integer.parseInt(i), 8);
                }
            }
            String str2 = this.mask;
            if (str2 != null) {
                String[] masks = str2.split("\\.");
                for (String m : masks) {
                    buffer.putInt(Integer.parseInt(m), 8);
                }
            }
            String str3 = this.gateway;
            if (str3 != null) {
                String[] gateways = str3.split("\\.");
                for (String g : gateways) {
                    buffer.putInt(Integer.parseInt(g), 8);
                }
            }
            String str4 = this.dns1;
            if (str4 != null) {
                String[] dns1s = str4.split("\\.");
                for (String d1 : dns1s) {
                    buffer.putInt(Integer.parseInt(d1), 8);
                }
            }
            String str5 = this.dns2;
            if (str5 != null) {
                String[] dns2s = str5.split("\\.");
                for (String d2 : dns2s) {
                    buffer.putInt(Integer.parseInt(d2), 8);
                }
            }
            this.cData = buffer.asByteArray();
            this.dataLen = this.cData.length;
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        if (this.cData != null && this.cData.length > 0) {
            BitBuffer buffer = BitBuffer.wrap(this.cData);
            buffer.position(0);
            this.autoIp = buffer.getIntUnsigned(8);
            this.iP = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
            this.mask = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
            this.gateway = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
            this.dns1 = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
            this.dns2 = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgAppGetWifiIp{autoIp=" + this.autoIp + ", iP='" + this.iP + "', mask='" + this.mask + "', gateway='" + this.gateway + "', dns1='" + this.dns1 + "', dns2='" + this.dns2 + "'}";
    }
}
