package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;

/* loaded from: classes.dex */
public class MsgAppGetTcpMode extends Message {
    private String clientIp;
    private int clientPort;
    private int serverPort;
    private int tcpMode;

    public MsgAppGetTcpMode() {
        this.serverPort = Integer.MAX_VALUE;
        this.clientPort = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 8;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppGetTcpMode(byte[] data) {
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
            this.tcpMode = buffer.getIntUnsigned(8);
            this.serverPort = buffer.getIntUnsigned(16);
            this.clientIp = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
            this.clientPort = buffer.getIntUnsigned(16);
        } catch (Exception e) {
        }
    }

    public int getTcpMode() {
        return this.tcpMode;
    }

    public void setTcpMode(int tcpMode) {
        this.tcpMode = tcpMode;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getClientIp() {
        return this.clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public int getClientPort() {
        return this.clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void pack() {
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackPack() {
        try {
            BitBuffer buffer = BitBuffer.allocateDynamic();
            buffer.putLong(this.tcpMode, 8);
            int i = this.serverPort;
            if (Integer.MAX_VALUE != i) {
                buffer.put(i, 16);
            }
            String str = this.clientIp;
            if (str != null) {
                String[] iPs = str.split("\\.");
                for (String i2 : iPs) {
                    buffer.put(Integer.parseInt(i2), 8);
                }
            }
            int i3 = this.clientPort;
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
            this.tcpMode = buffer.getIntUnsigned(8);
            this.serverPort = buffer.getIntUnsigned(16);
            this.clientIp = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
            this.clientPort = buffer.getIntUnsigned(16);
            setRtCode((byte) 0);
        }
    }

    public String toString() {
        return "MsgAppGetTcpMode{tcpMode=" + this.tcpMode + ", serverPort=" + this.serverPort + ", clientIp='" + this.clientIp + "', clientPort=" + this.clientPort + '}';
    }
}
