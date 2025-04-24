package org.apache.log4j.varia;

import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.helpers.LogLog;

/* compiled from: ExternallyRolledFileAppender.java */
/* loaded from: classes.dex */
class HUP extends Thread {
    ExternallyRolledFileAppender er;
    int port;

    HUP(ExternallyRolledFileAppender er, int port) {
        this.er = er;
        this.port = port;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        while (!isInterrupted()) {
            try {
                ServerSocket serverSocket = new ServerSocket(this.port);
                while (true) {
                    Socket socket = serverSocket.accept();
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("Connected to client at ");
                    stringBuffer.append(socket.getInetAddress());
                    LogLog.debug(stringBuffer.toString());
                    new Thread(new HUPNode(socket, this.er)).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
