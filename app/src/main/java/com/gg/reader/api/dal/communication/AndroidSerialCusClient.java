package com.gg.reader.api.dal.communication;

import com.gg.reader.api.protocol.gx.Message;
import com.gg.reader.api.utils.GLog;
import com.gg.reader.api.utils.ThreadPoolUtils;
import com.gxwl.device.reader.dal.SerialPortJNI;
import java.net.Socket;

/* loaded from: classes.dex */
public class AndroidSerialCusClient extends CommunicationInterface {
    private int freeWait;
    private int packageSize;

    public AndroidSerialCusClient(int packageSize, int freeWait) {
        this.packageSize = 64;
        this.freeWait = 1;
        this.packageSize = packageSize;
        this.freeWait = freeWait;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(String device_name, int port) {
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(Socket sConn) {
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(String device_name, int port, int timeout) {
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(String param) {
        try {
            String[] arrParam = param.split(":");
            if (arrParam.length != 2 || 1 != SerialPortJNI.openPort(arrParam[0], Integer.parseInt(arrParam[1]), 8, 1, 'N')) {
                return false;
            }
            this.keepReceived = true;
            startReceive();
            startProcess();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void startReceive() {
        ThreadPoolUtils.run(new Runnable() { // from class: com.gg.reader.api.dal.communication.AndroidSerialCusClient.1
            @Override // java.lang.Runnable
            public void run() {
                while (AndroidSerialCusClient.this.keepReceived) {
                    try {
                        byte[] bytes = SerialPortJNI.readPort(AndroidSerialCusClient.this.packageSize);
                        if (bytes == null || bytes.length <= 0) {
                            Thread.sleep(AndroidSerialCusClient.this.freeWait);
                        } else {
                            synchronized (AndroidSerialCusClient.this.lockRingBuffer) {
                                while (bytes.length + AndroidSerialCusClient.this.ringBuffer.getDataCount() > 1048576) {
                                    AndroidSerialCusClient.this.lockRingBuffer.wait(10000L);
                                }
                                AndroidSerialCusClient.this.ringBuffer.WriteBuffer(bytes, 0, bytes.length);
                                AndroidSerialCusClient.this.lockRingBuffer.notify();
                            }
                        }
                    } catch (Exception e) {
                        GLog.e("[AndroidSerialCusClient]startReceive error.");
                    }
                }
            }
        });
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void close() {
        try {
            this.keepReceived = false;
            SerialPortJNI.closePort();
            synchronized (this.lockRingBuffer) {
                this.lockRingBuffer.notifyAll();
            }
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(byte[] data) {
        synchronized (AndroidSerialCusClient.class) {
            try {
                SerialPortJNI.writePort(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(Message msg) {
        synchronized (AndroidSerialCusClient.class) {
            try {
                if (this.isRs485) {
                    msg.msgType.mt_13 = "1";
                    msg.rs485Address = getRs485Address();
                }
                msg.pack();
                byte[] sendData = msg.toBytes(this.isRs485);
                send(sendData);
            } catch (Exception ex) {
                GLog.e("[AndroidSerialCusClient]base serial send error:" + ex.getMessage());
            }
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public int receive(byte[] buffer) {
        return 0;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean setBufferSize(int size) {
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void dispose() {
    }
}
