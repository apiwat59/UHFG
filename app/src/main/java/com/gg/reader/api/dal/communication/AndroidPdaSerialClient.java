package com.gg.reader.api.dal.communication;

import cn.pda.serialport.SerialPort;
import com.gg.reader.api.protocol.gx.Message;
import com.gg.reader.api.utils.GLog;
import com.gg.reader.api.utils.ThreadPoolUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/* loaded from: classes.dex */
public class AndroidPdaSerialClient extends CommunicationInterface {
    private int baudrate;
    private int iDelay;
    private boolean isOpen;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private SerialPort mSerialPort;
    private int port;
    private String sPort;

    public AndroidPdaSerialClient() {
        this.isOpen = false;
        this.iDelay = 100;
        this.sPort = "/dev/ttyMT1";
        this.port = 13;
        this.baudrate = 115200;
    }

    public AndroidPdaSerialClient(String paramString, int paramInt) {
        this.isOpen = false;
        this.iDelay = 100;
        this.sPort = "/dev/ttyMT1";
        this.port = 13;
        this.baudrate = 115200;
        this.sPort = paramString;
        this.baudrate = paramInt;
    }

    public boolean isOpen() {
        return this.isOpen;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(byte[] paramArrayOfByte) {
        synchronized (AndroidPdaSerialClient.class) {
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
        synchronized (AndroidPdaSerialClient.class) {
            try {
                if (this.isRs485) {
                    msg.msgType.mt_13 = "1";
                    msg.rs485Address = getRs485Address();
                }
                msg.pack();
                byte[] sendData = msg.toBytes(this.isRs485);
                send(sendData);
            } catch (Exception ex) {
                GLog.e("[AndroidPdaSerialClient]base serial send error:" + ex.getMessage());
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
            this.port = Integer.parseInt(arrParam[0]);
            int parseInt = Integer.parseInt(arrParam[1]);
            this.baudrate = parseInt;
            SerialPort serialPort = new SerialPort(this.port, parseInt, 0);
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
                serialPort.close(this.port);
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

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void hdPowerOn() {
        super.hdPowerOn();
        SerialPort serialPort = this.mSerialPort;
        if (serialPort != null) {
            serialPort.power_5Von();
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void hdPowerOff() {
        super.hdPowerOff();
        SerialPort serialPort = this.mSerialPort;
        if (serialPort != null) {
            serialPort.power_5Voff();
        }
    }

    public void startReceive() {
        ThreadPoolUtils.run(new Runnable() { // from class: com.gg.reader.api.dal.communication.AndroidPdaSerialClient.1
            @Override // java.lang.Runnable
            public void run() {
                while (AndroidPdaSerialClient.this.keepReceived) {
                    try {
                    } catch (Exception e) {
                        GLog.e("[AndroidPdaSerialClient]startReceive error.");
                    }
                    if (AndroidPdaSerialClient.this.mInputStream != null) {
                        int len = AndroidPdaSerialClient.this.mInputStream.available();
                        if (len <= 0) {
                            Thread.sleep(AndroidPdaSerialClient.this.iDelay);
                        }
                        if (len > 0) {
                            int len2 = AndroidPdaSerialClient.this.mInputStream.read(AndroidPdaSerialClient.this.rcvBuff, 0, AndroidPdaSerialClient.this.rcvBuff.length);
                            synchronized (AndroidPdaSerialClient.this.lockRingBuffer) {
                                while (AndroidPdaSerialClient.this.ringBuffer.getDataCount() + len2 > 1048576) {
                                    AndroidPdaSerialClient.this.lockRingBuffer.wait(10000L);
                                }
                                AndroidPdaSerialClient.this.ringBuffer.WriteBuffer(AndroidPdaSerialClient.this.rcvBuff, 0, len2);
                                AndroidPdaSerialClient.this.lockRingBuffer.notify();
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
