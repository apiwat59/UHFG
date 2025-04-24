package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgAppGetUdpParam extends Message {
    private String ip;
    private int onOrOff;
    private int period;
    private int port;

    public MsgAppGetUdpParam() {
        this.ip = "0.0.0.0";
        this.port = Integer.MAX_VALUE;
        this.period = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 40;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetUdpParam(byte[] data) {
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
            this.onOrOff = buffer.getIntUnsigned(8);
            this.ip = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
            this.port = buffer.getIntUnsigned(16);
            this.period = buffer.getIntUnsigned(16);
        } catch (Exception e) {
        }
    }

    public int getOnOrOff() {
        return this.onOrOff;
    }

    public void setOnOrOff(int onOrOff) {
        this.onOrOff = onOrOff;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPeriod() {
        return this.period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putInt(this.onOrOff, 8);
            String str = this.ip;
            if (str != null) {
                String[] iPs = str.split("\\.");
                for (String i : iPs) {
                    buffer.putInt(Integer.parseInt(i), 8);
                }
            }
            int i2 = this.port;
            if (Integer.MAX_VALUE != i2) {
                buffer.put(i2, 16);
            }
            int i3 = this.period;
            if (Integer.MAX_VALUE != i3) {
                buffer.put(i3, 16);
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
            this.onOrOff = buffer.getIntUnsigned(8);
            this.ip = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
            this.port = buffer.getIntUnsigned(16);
            this.period = buffer.getIntUnsigned(16);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgAppGetUdpParam{onOrOff=" + this.onOrOff + ", ip='" + this.ip + "', port=" + this.port + ", period=" + this.period + '}';
    }
}
