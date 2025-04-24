package org.apache.log4j.net;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Vector;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class SocketHubAppender extends AppenderSkeleton {
    static final int DEFAULT_PORT = 4560;
    private boolean locationInfo;
    private Vector oosList;
    private int port;
    private ServerMonitor serverMonitor;

    public SocketHubAppender() {
        this.port = DEFAULT_PORT;
        this.oosList = new Vector();
        this.serverMonitor = null;
        this.locationInfo = false;
    }

    public SocketHubAppender(int _port) {
        this.port = DEFAULT_PORT;
        this.oosList = new Vector();
        this.serverMonitor = null;
        this.locationInfo = false;
        this.port = _port;
        startServer();
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.spi.OptionHandler
    public void activateOptions() {
        startServer();
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.Appender
    public synchronized void close() {
        if (this.closed) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("closing SocketHubAppender ");
        stringBuffer.append(getName());
        LogLog.debug(stringBuffer.toString());
        this.closed = true;
        cleanUp();
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append("SocketHubAppender ");
        stringBuffer2.append(getName());
        stringBuffer2.append(" closed");
        LogLog.debug(stringBuffer2.toString());
    }

    public void cleanUp() {
        LogLog.debug("stopping ServerSocket");
        this.serverMonitor.stopMonitor();
        this.serverMonitor = null;
        LogLog.debug("closing client connections");
        while (this.oosList.size() != 0) {
            ObjectOutputStream oos = (ObjectOutputStream) this.oosList.elementAt(0);
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    LogLog.error("could not close oos.", e);
                }
                this.oosList.removeElementAt(0);
            }
        }
    }

    @Override // org.apache.log4j.AppenderSkeleton
    public void append(LoggingEvent event) {
        if (event == null || this.oosList.size() == 0) {
            return;
        }
        if (this.locationInfo) {
            event.getLocationInformation();
        }
        int streamCount = 0;
        while (streamCount < this.oosList.size()) {
            ObjectOutputStream oos = null;
            try {
                oos = (ObjectOutputStream) this.oosList.elementAt(streamCount);
            } catch (ArrayIndexOutOfBoundsException e) {
            }
            if (oos != null) {
                try {
                    oos.writeObject(event);
                    oos.flush();
                    oos.reset();
                } catch (IOException e2) {
                    this.oosList.removeElementAt(streamCount);
                    LogLog.debug("dropped connection");
                    streamCount--;
                }
                streamCount++;
            } else {
                return;
            }
        }
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.Appender
    public boolean requiresLayout() {
        return false;
    }

    public void setPort(int _port) {
        this.port = _port;
    }

    public int getPort() {
        return this.port;
    }

    public void setLocationInfo(boolean _locationInfo) {
        this.locationInfo = _locationInfo;
    }

    public boolean getLocationInfo() {
        return this.locationInfo;
    }

    private void startServer() {
        this.serverMonitor = new ServerMonitor(this.port, this.oosList);
    }

    private class ServerMonitor implements Runnable {
        private boolean keepRunning = true;
        private Thread monitorThread;
        private Vector oosList;
        private int port;

        public ServerMonitor(int _port, Vector _oosList) {
            this.port = _port;
            this.oosList = _oosList;
            Thread thread = new Thread(this);
            this.monitorThread = thread;
            thread.setDaemon(true);
            this.monitorThread.start();
        }

        public synchronized void stopMonitor() {
            if (this.keepRunning) {
                LogLog.debug("server monitor thread shutting down");
                this.keepRunning = false;
                try {
                    this.monitorThread.join();
                } catch (InterruptedException e) {
                }
                this.monitorThread = null;
                LogLog.debug("server monitor thread shut down");
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                ServerSocket serverSocket = new ServerSocket(this.port);
                serverSocket.setSoTimeout(1000);
                try {
                    try {
                        serverSocket.setSoTimeout(1000);
                        while (this.keepRunning) {
                            Socket socket = null;
                            try {
                                try {
                                    socket = serverSocket.accept();
                                } catch (InterruptedIOException e) {
                                } catch (IOException e2) {
                                    LogLog.error("exception accepting socket.", e2);
                                }
                            } catch (SocketException e3) {
                                LogLog.error("exception accepting socket, shutting down server socket.", e3);
                                this.keepRunning = false;
                            }
                            if (socket != null) {
                                try {
                                    InetAddress remoteAddress = socket.getInetAddress();
                                    StringBuffer stringBuffer = new StringBuffer();
                                    stringBuffer.append("accepting connection from ");
                                    stringBuffer.append(remoteAddress.getHostName());
                                    stringBuffer.append(" (");
                                    stringBuffer.append(remoteAddress.getHostAddress());
                                    stringBuffer.append(")");
                                    LogLog.debug(stringBuffer.toString());
                                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                                    this.oosList.addElement(oos);
                                } catch (IOException e4) {
                                    LogLog.error("exception creating output stream on socket.", e4);
                                }
                            }
                        }
                        try {
                            serverSocket.close();
                        } catch (IOException e5) {
                        }
                    } catch (Throwable e6) {
                        try {
                            serverSocket.close();
                        } catch (IOException e7) {
                        }
                        throw e6;
                    }
                } catch (SocketException e8) {
                    LogLog.error("exception setting timeout, shutting down server socket.", e8);
                    try {
                        serverSocket.close();
                    } catch (IOException e9) {
                    }
                }
            } catch (Exception e10) {
                LogLog.error("exception setting timeout, shutting down server socket.", e10);
                this.keepRunning = false;
            }
        }
    }
}
