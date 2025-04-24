package org.apache.log4j.net;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class TelnetAppender extends AppenderSkeleton {
    private int port = 23;
    private SocketHandler sh;

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.Appender
    public boolean requiresLayout() {
        return true;
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.spi.OptionHandler
    public void activateOptions() {
        try {
            SocketHandler socketHandler = new SocketHandler(this.port);
            this.sh = socketHandler;
            socketHandler.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.Appender
    public void close() {
        this.sh.finalize();
    }

    @Override // org.apache.log4j.AppenderSkeleton
    protected void append(LoggingEvent event) {
        String[] s;
        this.sh.send(this.layout.format(event));
        if (this.layout.ignoresThrowable() && (s = event.getThrowableStrRep()) != null) {
            for (String str : s) {
                this.sh.send(str);
                this.sh.send(Layout.LINE_SEP);
            }
        }
    }

    protected class SocketHandler extends Thread {
        private ServerSocket serverSocket;
        private boolean done = false;
        private Vector writers = new Vector();
        private Vector connections = new Vector();
        private int MAX_CONNECTIONS = 20;

        public void finalize() {
            Enumeration e = this.connections.elements();
            while (e.hasMoreElements()) {
                try {
                    ((Socket) e.nextElement()).close();
                } catch (Exception e2) {
                }
            }
            try {
                this.serverSocket.close();
            } catch (Exception e3) {
            }
            this.done = true;
        }

        public void send(String message) {
            Enumeration ce = this.connections.elements();
            Enumeration e = this.writers.elements();
            while (e.hasMoreElements()) {
                Socket sock = (Socket) ce.nextElement();
                PrintWriter writer = (PrintWriter) e.nextElement();
                writer.print(message);
                if (writer.checkError()) {
                    this.connections.remove(sock);
                    this.writers.remove(writer);
                }
            }
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (!this.done) {
                try {
                    Socket newClient = this.serverSocket.accept();
                    PrintWriter pw = new PrintWriter(newClient.getOutputStream());
                    if (this.connections.size() < this.MAX_CONNECTIONS) {
                        this.connections.addElement(newClient);
                        this.writers.addElement(pw);
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("TelnetAppender v1.0 (");
                        stringBuffer.append(this.connections.size());
                        stringBuffer.append(" active connections)\r\n\r\n");
                        pw.print(stringBuffer.toString());
                        pw.flush();
                    } else {
                        pw.print("Too many connections.\r\n");
                        pw.flush();
                        newClient.close();
                    }
                } catch (Exception e) {
                    LogLog.error("Encountered error while in SocketHandler loop.", e);
                }
            }
        }

        public SocketHandler(int port) throws IOException {
            this.serverSocket = new ServerSocket(port);
        }
    }
}
