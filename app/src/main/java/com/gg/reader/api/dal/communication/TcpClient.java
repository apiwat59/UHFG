package com.gg.reader.api.dal.communication;

import androidx.vectordrawable.graphics.drawable.PathInterpolatorCompat;
import com.gg.reader.api.protocol.gx.Message;
import com.gg.reader.api.protocol.gx.MsgAppHeartbeat;
import com.gg.reader.api.utils.ThreadPoolUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
import org.apache.log4j.Level;

/* loaded from: classes.dex */
public class TcpClient extends CommunicationInterface {
    public Socket sConn = null;
    public String serverIp = "192.168.1.168";
    public int serverPort = 8160;
    public InputStream inputStream = null;
    public OutputStream outputStream = null;
    boolean _isOpen = false;
    private Date lastUrgentData = new Date();
    private int count = 1;

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(String device_name, int port) {
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(Socket sConn) {
        try {
            Socket socket = this.sConn;
            if (socket != null && socket.isConnected()) {
                return false;
            }
            this.sConn = sConn;
            this.keepReceived = true;
            sConn.setSoTimeout(1000);
            sConn.setKeepAlive(true);
            this.inputStream = sConn.getInputStream();
            this.outputStream = sConn.getOutputStream();
            startReceive();
            startProcess();
            return true;
        } catch (Exception e) {
            close();
            return false;
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(String device_name, int port, int timeout) {
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public boolean open(String param) {
        try {
            String[] arrParam = param.split(":");
            if (arrParam.length == 2) {
                this.serverIp = arrParam[0];
                this.serverPort = Integer.parseInt(arrParam[1]);
                Socket socket = new Socket();
                this.sConn = socket;
                socket.connect(new InetSocketAddress(this.serverIp, this.serverPort), Level.TRACE_INT);
                this.sConn.setSoTimeout(1000);
                this.sConn.setKeepAlive(true);
                this.inputStream = this.sConn.getInputStream();
                this.outputStream = this.sConn.getOutputStream();
                this.keepReceived = true;
                startReceive();
                startProcess();
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void close() {
        try {
            this.keepReceived = false;
            this._isOpen = false;
            Socket socket = this.sConn;
            if (socket != null) {
                socket.close();
                this.inputStream = null;
                this.outputStream = null;
                this.sConn = null;
            }
            synchronized (this.lockRingBuffer) {
                this.lockRingBuffer.notifyAll();
            }
        } catch (Exception e) {
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(byte[] data) {
        synchronized (this) {
            try {
                this.outputStream.write(data);
            } catch (IOException e) {
            }
        }
    }

    @Override // com.gg.reader.api.dal.communication.CommunicationInterface
    public void send(Message msg) {
        try {
            msg.pack();
            send(msg.toBytes(this.isRs485));
        } catch (Exception e) {
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

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isRemoteClosed() {
        if (this.sConn == null) {
            return true;
        }
        Date now = new Date();
        long time = now.getTime() - this.lastUrgentData.getTime();
        int i = this.count;
        if (time > i * PathInterpolatorCompat.MAX_NUM_POINTS) {
            try {
                this.count = i + 1;
                send(new MsgAppHeartbeat());
            } catch (Exception e) {
            }
        }
        return now.getTime() - this.lastUrgentData.getTime() > 15000;
    }

    public void startReceive() {
        ThreadPoolUtils.run(new Runnable() { // from class: com.gg.reader.api.dal.communication.TcpClient.1
            @Override // java.lang.Runnable
            public void run() {
                while (TcpClient.this.keepReceived) {
                    try {
                        int len = TcpClient.this.inputStream.available();
                        if (len <= 0) {
                            Thread.sleep(100L);
                        }
                        if (len > 0) {
                            len = TcpClient.this.inputStream.read(TcpClient.this.rcvBuff, 0, TcpClient.this.rcvBuff.length);
                            synchronized (TcpClient.this.lockRingBuffer) {
                                while (TcpClient.this.ringBuffer.getDataCount() + len > 1048576) {
                                    TcpClient.this.lockRingBuffer.wait(10000L);
                                }
                                TcpClient.this.ringBuffer.WriteBuffer(TcpClient.this.rcvBuff, 0, len);
                                TcpClient.this.lockRingBuffer.notify();
                            }
                        }
                        if (!TcpClient.this._isSendHeartbeat) {
                            continue;
                        } else if (len <= 0) {
                            if (TcpClient.this.isRemoteClosed()) {
                                throw new Exception("remote closed.");
                            }
                        } else {
                            TcpClient.this.lastUrgentData = new Date();
                            TcpClient.this.count = 1;
                        }
                    } catch (Exception e) {
                        try {
                            TcpClient.this.triggerDisconnected();
                            Thread.sleep(3000L);
                        } catch (InterruptedException e2) {
                        }
                    }
                }
            }
        });
    }
}
