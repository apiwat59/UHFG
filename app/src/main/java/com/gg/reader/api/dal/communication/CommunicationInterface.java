package com.gg.reader.api.dal.communication;

import com.gg.reader.api.protocol.gx.Message;
import com.gg.reader.api.utils.BitBuffer;
import com.gg.reader.api.utils.HexUtils;
import com.gg.reader.api.utils.RingBuffer;
import com.gg.reader.api.utils.ThreadPoolUtils;
import java.net.Socket;

/* loaded from: classes.dex */
public abstract class CommunicationInterface {
    protected static final int MAX_BUFFER_LEN = 1048576;
    public HandlerDisconnected onDisconnected;
    public HandlerMessageReceived onMessageReceived;
    protected int connType = 255;
    protected volatile RingBuffer ringBuffer = new RingBuffer(1048576);
    protected Object lockRingBuffer = new Object();
    protected boolean keepReceived = false;
    protected byte[] rcvBuff = new byte[1024];
    protected boolean isRs485 = false;
    private int rs485Address = 0;
    public boolean _isSendHeartbeat = false;

    public abstract void close();

    public abstract void dispose();

    public abstract boolean open(String str);

    public abstract boolean open(String str, int i);

    public abstract boolean open(String str, int i, int i2);

    public abstract boolean open(Socket socket);

    public abstract int receive(byte[] bArr);

    public abstract void send(Message message);

    public abstract void send(byte[] bArr);

    public abstract boolean setBufferSize(int i);

    public boolean isRs485() {
        return this.isRs485;
    }

    public void setRs485(boolean rs485) {
        this.isRs485 = rs485;
    }

    public int getRs485Address() {
        return this.rs485Address;
    }

    public void setRs485Address(int rs485Address) {
        this.rs485Address = rs485Address;
    }

    public void setConnectType(int tt) {
        this.connType = tt;
    }

    public int getConnectType() {
        return this.connType;
    }

    protected void triggerMessageEvent(Message msg) {
        try {
            HandlerMessageReceived handlerMessageReceived = this.onMessageReceived;
            if (handlerMessageReceived != null) {
                synchronized (handlerMessageReceived) {
                    this.onMessageReceived.received(msg);
                }
            }
        } catch (Exception e) {
        }
    }

    protected void triggerDisconnected() {
        try {
            HandlerDisconnected handlerDisconnected = this.onDisconnected;
            if (handlerDisconnected != null) {
                synchronized (handlerDisconnected) {
                    this.onDisconnected.log();
                }
            }
        } catch (Exception e) {
        }
    }

    public void hdPowerOn() {
    }

    public void hdPowerOff() {
    }

    public void setUhfPower(boolean value) {
    }

    public boolean isConnected() {
        return this.keepReceived;
    }

    public void startProcess() {
        ThreadPoolUtils.run(new Runnable() { // from class: com.gg.reader.api.dal.communication.CommunicationInterface.1
            @Override // java.lang.Runnable
            public void run() {
                while (CommunicationInterface.this.keepReceived) {
                    byte[] msgByte = null;
                    synchronized (CommunicationInterface.this.lockRingBuffer) {
                        try {
                            try {
                                if (CommunicationInterface.this.ringBuffer.getDataCount() < 7) {
                                    CommunicationInterface.this.lockRingBuffer.wait();
                                }
                                if ((CommunicationInterface.this.ringBuffer.Index(0) & 255) != 90) {
                                    CommunicationInterface.this.ringBuffer.Clear(1);
                                } else if (CommunicationInterface.this.ringBuffer.Index(1) != 0) {
                                    CommunicationInterface.this.ringBuffer.Clear(1);
                                } else {
                                    BitBuffer buffer = BitBuffer.allocateDynamic();
                                    int dataLenEndIndex = 7;
                                    if (CommunicationInterface.this.isRs485) {
                                        dataLenEndIndex = 7 + 1;
                                    }
                                    byte[] preByte = new byte[dataLenEndIndex];
                                    CommunicationInterface.this.ringBuffer.ReadBuffer(preByte, 0, preByte.length);
                                    buffer.put(preByte);
                                    buffer.position((preByte.length * 8) - 16);
                                    int dataLen = buffer.getIntUnsigned(16);
                                    if (dataLen >= 0 && dataLen <= 1024) {
                                        int msgLen = dataLen + dataLenEndIndex + 2;
                                        if (CommunicationInterface.this.ringBuffer.getDataCount() < msgLen) {
                                            CommunicationInterface.this.lockRingBuffer.wait();
                                        } else {
                                            msgByte = new byte[msgLen];
                                            CommunicationInterface.this.ringBuffer.ReadBuffer(msgByte, 0, msgLen);
                                            CommunicationInterface.this.ringBuffer.Clear(msgLen);
                                        }
                                    }
                                    CommunicationInterface.this.ringBuffer.Clear(1);
                                }
                                if (msgByte != null) {
                                    Message msg = new Message(msgByte);
                                    if (msg.checkCrc()) {
                                        if (!CommunicationInterface.this.isRs485 || msg.rs485Address == CommunicationInterface.this.rs485Address) {
                                            CommunicationInterface.this.triggerMessageEvent(msg);
                                        }
                                    } else {
                                        System.out.println("crc错误-->" + HexUtils.bytes2HexString(msg.msgData));
                                    }
                                }
                            } finally {
                            }
                        } catch (Exception e) {
                        }
                    }
                }
            }
        });
    }
}
