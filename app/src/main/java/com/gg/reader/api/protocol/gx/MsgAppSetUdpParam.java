package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.StringUtils;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgAppSetUdpParam extends Message {
    private String ip;
    private int onOrOff;
    private int period;
    private int port;

    public MsgAppSetUdpParam() {
        this.ip = "0.0.0.0";
        this.port = Integer.MAX_VALUE;
        this.period = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 39;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppSetUdpParam(byte[] data) {
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
            while (buffer.position() / 8 < data.length) {
                byte pid = buffer.getByte();
                if (pid == 1) {
                    this.ip = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
                } else if (pid == 2) {
                    this.port = buffer.getIntUnsigned(16);
                } else if (pid == 3) {
                    this.period = buffer.getIntUnsigned(16);
                }
            }
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
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putInt(this.onOrOff, 8);
        if (1 == this.onOrOff) {
            if (!StringUtils.isNullOfEmpty(this.ip)) {
                buffer.putInt(1, 8);
                String[] iPs = this.ip.split("\\.");
                for (String i : iPs) {
                    buffer.putInt(Integer.parseInt(i), 8);
                }
            }
            if (Integer.MAX_VALUE != this.port) {
                buffer.putInt(2, 8);
                buffer.put(this.port, 16);
            }
            if (Integer.MAX_VALUE != this.period) {
                buffer.putInt(3, 8);
                buffer.put(this.period, 16);
            }
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppSetUdpParam.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Fail");
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
