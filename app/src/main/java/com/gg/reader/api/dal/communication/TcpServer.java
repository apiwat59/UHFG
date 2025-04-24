package com.gg.reader.api.dal.communication;

import com.gg.reader.api.dal.HandlerRemoteConnected;
import com.gg.reader.api.utils.ThreadPoolUtils;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/* loaded from: classes.dex */
public class TcpServer {
    public HandlerRemoteConnected onRemoteConnected;
    private ServerSocket listenSocket = null;
    public Boolean keepListen = false;
    public int listenPort = 8160;

    protected void triggerClientConnectedEvent(TcpClient client) {
        try {
            HandlerRemoteConnected handlerRemoteConnected = this.onRemoteConnected;
            if (handlerRemoteConnected != null) {
                synchronized (handlerRemoteConnected) {
                    this.onRemoteConnected.log(client);
                }
            }
        } catch (Exception e) {
        }
    }

    public boolean open(int param) {
        if (this.listenSocket != null) {
            return false;
        }
        this.keepListen = true;
        try {
            this.listenPort = param;
            ServerSocket serverSocket = new ServerSocket();
            this.listenSocket = serverSocket;
            serverSocket.bind(new InetSocketAddress("0.0.0.0", this.listenPort));
            startListen();
            return true;
        } catch (Exception e) {
            close();
            return false;
        }
    }

    public void startListen() {
        ThreadPoolUtils.run(new Runnable() { // from class: com.gg.reader.api.dal.communication.TcpServer.1
            @Override // java.lang.Runnable
            public void run() {
                while (TcpServer.this.keepListen.booleanValue()) {
                    try {
                        Socket acSocket = TcpServer.this.listenSocket.accept();
                        TcpClient tc = new TcpClient();
                        tc.connType = 3;
                        if (tc.open(acSocket)) {
                            tc.serverIp = acSocket.getInetAddress().getHostAddress();
                            tc.serverPort = acSocket.getPort();
                            TcpServer.this.triggerClientConnectedEvent(tc);
                        } else {
                            acSocket.close();
                            tc.close();
                        }
                    } catch (Exception e) {
                        return;
                    }
                }
            }
        });
    }

    public void close() {
        this.keepListen = false;
        ServerSocket serverSocket = this.listenSocket;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (Exception e) {
            }
        }
    }
}
