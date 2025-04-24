package com.gg.reader.api.protocol.gx;

import com.gg.reader.api.utils.BitBuffer;
import java.util.Hashtable;

/* loaded from: classes.dex */
public class MsgAppSetTcpMode extends Message {
    private String clientIp;
    private int clientPort;
    private int serverPort;
    private int tcpMode;

    public MsgAppSetTcpMode() {
        this.serverPort = Integer.MAX_VALUE;
        this.clientPort = Integer.MAX_VALUE;
        try {
            this.msgType = new MsgType();
            this.msgType.mt_8_11 = EnumG.MSG_TYPE_BIT_APP;
            this.msgType.msgId = (byte) 7;
            this.dataLen = 0;
        } catch (Exception e) {
        }
    }

    public MsgAppSetTcpMode(byte[] data) {
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
            while (buffer.position() / 8 < data.length) {
                byte pid = buffer.getByte();
                if (pid == 1) {
                    this.serverPort = buffer.getIntUnsigned(16);
                } else if (pid == 2) {
                    this.clientIp = buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8) + "." + buffer.getIntUnsigned(8);
                } else if (pid == 3) {
                    this.clientPort = buffer.getIntUnsigned(16);
                }
            }
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
        BitBuffer buffer = BitBuffer.allocateDynamic();
        buffer.putLong(this.tcpMode, 8);
        if (Integer.MAX_VALUE != this.serverPort) {
            buffer.putInt(1, 8);
            buffer.putInt(this.serverPort, 16);
        }
        String str = this.clientIp;
        if (str != null) {
            String[] iPs = str.split("\\.");
            buffer.putInt(2, 8);
            for (String i : iPs) {
                buffer.putInt(Integer.parseInt(i), 8);
            }
        }
        if (Integer.MAX_VALUE != this.clientPort) {
            buffer.putInt(3, 8);
            buffer.putInt(this.clientPort, 16);
        }
        this.cData = buffer.asByteArray();
        this.dataLen = this.cData.length;
    }

    @Override // com.gg.reader.api.protocol.gx.Message
    public void ackUnpack() {
        Hashtable<Byte, String> dicErrorMsg = new Hashtable<Byte, String>() { // from class: com.gg.reader.api.protocol.gx.MsgAppSetTcpMode.1
            {
                put((byte) 0, "Success.");
                put((byte) 1, "Server IP parameter error .");
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
