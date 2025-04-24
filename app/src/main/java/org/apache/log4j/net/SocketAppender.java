package org.apache.log4j.net;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class SocketAppender extends AppenderSkeleton {
    static final int DEFAULT_PORT = 4560;
    static final int DEFAULT_RECONNECTION_DELAY = 30000;
    private static final int RESET_FREQUENCY = 1;
    InetAddress address;
    private Connector connector;
    int counter;
    boolean locationInfo;
    ObjectOutputStream oos;
    int port;
    int reconnectionDelay;
    String remoteHost;

    public SocketAppender() {
        this.port = DEFAULT_PORT;
        this.reconnectionDelay = 30000;
        this.locationInfo = false;
        this.counter = 0;
    }

    public SocketAppender(InetAddress address, int port) {
        this.port = DEFAULT_PORT;
        this.reconnectionDelay = 30000;
        this.locationInfo = false;
        this.counter = 0;
        this.address = address;
        this.remoteHost = address.getHostName();
        this.port = port;
        connect(address, port);
    }

    public SocketAppender(String host, int port) {
        this.port = DEFAULT_PORT;
        this.reconnectionDelay = 30000;
        this.locationInfo = false;
        this.counter = 0;
        this.port = port;
        InetAddress addressByName = getAddressByName(host);
        this.address = addressByName;
        this.remoteHost = host;
        connect(addressByName, port);
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.spi.OptionHandler
    public void activateOptions() {
        connect(this.address, this.port);
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.Appender
    public synchronized void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        cleanUp();
    }

    public void cleanUp() {
        ObjectOutputStream objectOutputStream = this.oos;
        if (objectOutputStream != null) {
            try {
                objectOutputStream.close();
            } catch (IOException e) {
                LogLog.error("Could not close oos.", e);
            }
            this.oos = null;
        }
        Connector connector = this.connector;
        if (connector != null) {
            connector.interrupted = true;
            this.connector = null;
        }
    }

    void connect(InetAddress address, int port) {
        if (this.address == null) {
            return;
        }
        try {
            cleanUp();
            this.oos = new ObjectOutputStream(new Socket(address, port).getOutputStream());
        } catch (IOException e) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Could not connect to remote log4j server at [");
            stringBuffer.append(address.getHostName());
            stringBuffer.append("].");
            String msg = stringBuffer.toString();
            if (this.reconnectionDelay > 0) {
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append(msg);
                stringBuffer2.append(" We will try again later.");
                msg = stringBuffer2.toString();
                fireConnector();
            }
            LogLog.error(msg, e);
        }
    }

    @Override // org.apache.log4j.AppenderSkeleton
    public void append(LoggingEvent event) {
        if (event == null) {
            return;
        }
        if (this.address == null) {
            ErrorHandler errorHandler = this.errorHandler;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("No remote host is set for SocketAppender named \"");
            stringBuffer.append(this.name);
            stringBuffer.append("\".");
            errorHandler.error(stringBuffer.toString());
            return;
        }
        if (this.oos != null) {
            try {
                if (this.locationInfo) {
                    event.getLocationInformation();
                }
                this.oos.writeObject(event);
                this.oos.flush();
                int i = this.counter + 1;
                this.counter = i;
                if (i >= 1) {
                    this.counter = 0;
                    this.oos.reset();
                }
            } catch (IOException e) {
                this.oos = null;
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append("Detected problem with connection: ");
                stringBuffer2.append(e);
                LogLog.warn(stringBuffer2.toString());
                if (this.reconnectionDelay > 0) {
                    fireConnector();
                }
            }
        }
    }

    void fireConnector() {
        if (this.connector == null) {
            LogLog.debug("Starting a new connector thread.");
            Connector connector = new Connector();
            this.connector = connector;
            connector.setDaemon(true);
            this.connector.setPriority(1);
            this.connector.start();
        }
    }

    static InetAddress getAddressByName(String host) {
        try {
            return InetAddress.getByName(host);
        } catch (Exception e) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Could not find address of [");
            stringBuffer.append(host);
            stringBuffer.append("].");
            LogLog.error(stringBuffer.toString(), e);
            return null;
        }
    }

    @Override // org.apache.log4j.AppenderSkeleton, org.apache.log4j.Appender
    public boolean requiresLayout() {
        return false;
    }

    public void setRemoteHost(String host) {
        this.address = getAddressByName(host);
        this.remoteHost = host;
    }

    public String getRemoteHost() {
        return this.remoteHost;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return this.port;
    }

    public void setLocationInfo(boolean locationInfo) {
        this.locationInfo = locationInfo;
    }

    public boolean getLocationInfo() {
        return this.locationInfo;
    }

    public void setReconnectionDelay(int delay) {
        this.reconnectionDelay = delay;
    }

    public int getReconnectionDelay() {
        return this.reconnectionDelay;
    }

    class Connector extends Thread {
        boolean interrupted = false;

        Connector() {
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            IOException e;
            IOException e2;
            Connector connector;
            IOException e3;
            Connector connector2 = null;
            IOException e4 = null;
            while (!this.interrupted) {
                try {
                    Thread.sleep(SocketAppender.this.reconnectionDelay);
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("Attempting connection to ");
                    stringBuffer.append(SocketAppender.this.address.getHostName());
                    LogLog.debug(stringBuffer.toString());
                    Socket socket = new Socket(SocketAppender.this.address, SocketAppender.this.port);
                    try {
                        try {
                            synchronized (this) {
                                try {
                                    SocketAppender.this.oos = new ObjectOutputStream(socket.getOutputStream());
                                    SocketAppender.this.connector = null;
                                    LogLog.debug("Connection established. Exiting connector thread.");
                                } catch (Throwable th) {
                                    try {
                                        throw th;
                                    } catch (IOException e5) {
                                        e2 = e5;
                                        e = th;
                                        connector2 = this;
                                        StringBuffer stringBuffer2 = new StringBuffer();
                                        stringBuffer2.append("Could not connect to ");
                                        stringBuffer2.append(SocketAppender.this.address.getHostName());
                                        stringBuffer2.append(". Exception is ");
                                        stringBuffer2.append(e2);
                                        LogLog.debug(stringBuffer2.toString());
                                        e4 = e;
                                    }
                                }
                            }
                            return;
                        } catch (InterruptedException e6) {
                            LogLog.debug("Connector interrupted. Leaving loop.");
                            return;
                        } catch (ConnectException e7) {
                            e3 = e7;
                            connector = this;
                            StringBuffer stringBuffer3 = new StringBuffer();
                            stringBuffer3.append("Remote host ");
                            stringBuffer3.append(SocketAppender.this.address.getHostName());
                            stringBuffer3.append(" refused connection.");
                            LogLog.debug(stringBuffer3.toString());
                            Connector connector3 = connector;
                            e4 = e3;
                            connector2 = connector3;
                        }
                    } catch (IOException e8) {
                        IOException iOException = e4;
                        e2 = e8;
                        connector2 = this;
                        e = iOException;
                    }
                } catch (ConnectException e9) {
                    connector = connector2;
                    e3 = e9;
                } catch (IOException e10) {
                    e = e4;
                    e2 = e10;
                } catch (InterruptedException e11) {
                }
            }
        }
    }
}
