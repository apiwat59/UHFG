package com.gg.reader.api.dal.communication;

import com.gg.reader.api.protocol.gx.Message;
import com.gg.reader.api.utils.GLog;
import com.gg.reader.api.utils.ThreadPoolUtils;
import com.gxwl.device.reader.dal.SerialPort;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/* loaded from: classes.dex */
public class AndroidSerialClient extends CommunicationInterface {
    private int iBaudRate;
    private int iDelay;
    private boolean isOpen;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private SerialPort mSerialPort;
    private String sPort;

    public AndroidSerialClient() {
        this.isOpen = false;
        this.iDelay = 100;
        this.sPort = "/dev/ttyS0";
    }

    public AndroidSerialClient(String paramString, int paramInt) {
        this.isOpen = false;
        this.iDelay = 100;
        this.sPort = "/dev/ttyS0";
        this.sPort = paramString;
        this.iBaudRate = paramInt;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(byte[] paramArrayOfByte) {
        synchronized (AndroidSerialClient.class) {
            try {
                try {
                    this.mOutputStream.write(paramArrayOfByte);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(Message msg) {
        synchronized (AndroidSerialClient.class) {
            try {
                if (this.isRs485) {
                    msg.msgType.mt_13 = "1";
                    msg.rs485Address = getRs485Address();
                }
                msg.pack();
                byte[] sendData = msg.toBytes(this.isRs485);
                send(sendData);
            } catch (Exception ex) {
                GLog.e("[AndroidSerialClient]base serial send error:" + ex.getMessage());
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
        close();
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
            if (arrParam.length != 2) {
                return false;
            }
            this.sPort = arrParam[0];
            this.iBaudRate = Integer.parseInt(arrParam[1]);
            SerialPort serialPort = new SerialPort(new File(this.sPort), this.iBaudRate, 0);
            this.mSerialPort = serialPort;
            this.mOutputStream = serialPort.getOutputStream();
            this.mInputStream = this.mSerialPort.getInputStream();
            this.isOpen = true;
            this.keepReceived = true;
            startReceive();
            startProcess();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void close() {
        try {
            this.isOpen = false;
            this.keepReceived = false;
            SerialPort serialPort = this.mSerialPort;
            if (serialPort != null) {
                serialPort.close();
                this.mInputStream = null;
                this.mOutputStream = null;
                this.mSerialPort = null;
            }
            synchronized (this.lockRingBuffer) {
                this.lockRingBuffer.notify();
            }
        } catch (Exception e) {
        }
    }

    public void startReceive() {
        ThreadPoolUtils.run(new Runnable() { // from class: com.gg.reader.api.dal.communication.AndroidSerialClient.1
            @Override // java.lang.Runnable
            public void run() {
                while (AndroidSerialClient.this.keepReceived) {
                    try {
                    } catch (Exception e) {
                        GLog.e("[AndroidSerialClient]startReceive error.");
                    }
                    if (AndroidSerialClient.this.mInputStream != null) {
                        int len = AndroidSerialClient.this.mInputStream.available();
                        if (len <= 0) {
                            Thread.sleep(AndroidSerialClient.this.iDelay);
                        }
                        if (len > 0) {
                            int len2 = AndroidSerialClient.this.mInputStream.read(AndroidSerialClient.this.rcvBuff, 0, AndroidSerialClient.this.rcvBuff.length);
                            synchronized (AndroidSerialClient.this.lockRingBuffer) {
                                while (AndroidSerialClient.this.ringBuffer.getDataCount() + len2 > 1048576) {
                                    AndroidSerialClient.this.lockRingBuffer.wait(10000L);
                                }
                                AndroidSerialClient.this.ringBuffer.WriteBuffer(AndroidSerialClient.this.rcvBuff, 0, len2);
                                AndroidSerialClient.this.lockRingBuffer.notify();
                            }
                        } else {
                            continue;
                        }
                    } else {
                        return;
                    }
                }
            }
        });
    }
}
