package org.apache.log4j.chainsaw;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
class LoggingReceiver extends Thread {
    private static final Logger LOG;
    static /* synthetic */ Class class$org$apache$log4j$chainsaw$LoggingReceiver;
    private MyTableModel mModel;
    private ServerSocket mSvrSock;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Class cls = class$org$apache$log4j$chainsaw$LoggingReceiver;
        if (cls == null) {
            cls = class$("org.apache.log4j.chainsaw.LoggingReceiver");
            class$org$apache$log4j$chainsaw$LoggingReceiver = cls;
        }
        LOG = Logger.getLogger(cls);
    }

    private class Slurper implements Runnable {
        private final Socket mClient;

        Slurper(Socket aClient) {
            this.mClient = aClient;
        }

        @Override // java.lang.Runnable
        public void run() {
            IOException e;
            LoggingReceiver.LOG.debug("Starting to get data");
            try {
                try {
                    ObjectInputStream ois = new ObjectInputStream(this.mClient.getInputStream());
                    while (true) {
                        try {
                            LoggingEvent event = (LoggingEvent) ois.readObject();
                            LoggingReceiver.this.mModel.addEvent(new EventDetails(event));
                        } catch (ClassNotFoundException e2) {
                            e = e2;
                            LoggingReceiver.LOG.warn("Got ClassNotFoundException, closing connection", e);
                            try {
                                this.mClient.close();
                            } catch (IOException e3) {
                                LoggingReceiver.LOG.warn("Error closing connection", e3);
                                return;
                            }
                        } catch (SocketException e4) {
                            LoggingReceiver.LOG.info("Caught SocketException, closing connection");
                            this.mClient.close();
                        } catch (IOException e5) {
                            e = e5;
                            LoggingReceiver.LOG.warn("Got IOException, closing connection", e);
                            this.mClient.close();
                        }
                    }
                } catch (IOException e6) {
                    e = e6;
                } catch (ClassNotFoundException e7) {
                    e = e7;
                } catch (SocketException e8) {
                }
            } catch (EOFException e9) {
                LoggingReceiver.LOG.info("Reached EOF, closing connection");
                this.mClient.close();
            }
        }
    }

    LoggingReceiver(MyTableModel aModel, int aPort) throws IOException {
        setDaemon(true);
        this.mModel = aModel;
        this.mSvrSock = new ServerSocket(aPort);
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        LOG.info("Thread started");
        while (true) {
            try {
                Logger logger = LOG;
                logger.debug("Waiting for a connection");
                Socket client = this.mSvrSock.accept();
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("Got a connection from ");
                stringBuffer.append(client.getInetAddress().getHostName());
                logger.debug(stringBuffer.toString());
                Thread t = new Thread(new Slurper(client));
                t.setDaemon(true);
                t.start();
            } catch (IOException e) {
                LOG.error("Error in accepting connections, stopping.", e);
                return;
            }
        }
    }
}
