package org.apache.log4j.net;

import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class SocketNode implements Runnable {
    static /* synthetic */ Class class$org$apache$log4j$net$SocketNode;
    static Logger logger;
    LoggerRepository hierarchy;
    ObjectInputStream ois;
    Socket socket;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Class cls = class$org$apache$log4j$net$SocketNode;
        if (cls == null) {
            cls = class$("org.apache.log4j.net.SocketNode");
            class$org$apache$log4j$net$SocketNode = cls;
        }
        logger = Logger.getLogger(cls);
    }

    public SocketNode(Socket socket, LoggerRepository hierarchy) {
        this.socket = socket;
        this.hierarchy = hierarchy;
        try {
            this.ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));
        } catch (Exception e) {
            Logger logger2 = logger;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Could not open ObjectInputStream to ");
            stringBuffer.append(socket);
            logger2.error(stringBuffer.toString(), e);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        while (true) {
            try {
                LoggingEvent event = (LoggingEvent) this.ois.readObject();
                try {
                    Logger remoteLogger = this.hierarchy.getLogger(event.getLoggerName());
                    try {
                        if (event.getLevel().isGreaterOrEqual(remoteLogger.getEffectiveLevel())) {
                            try {
                                remoteLogger.callAppenders(event);
                            } catch (EOFException e) {
                                logger.info("Caught java.io.EOFException closing conneciton.");
                                try {
                                    this.ois.close();
                                    return;
                                } catch (Exception e2) {
                                    logger.info("Could not close connection.", e2);
                                    return;
                                }
                            } catch (SocketException e3) {
                                logger.info("Caught java.net.SocketException closing conneciton.");
                                this.ois.close();
                                return;
                            } catch (IOException e4) {
                                e = e4;
                                Logger logger2 = logger;
                                StringBuffer stringBuffer = new StringBuffer();
                                stringBuffer.append("Caught java.io.IOException: ");
                                stringBuffer.append(e);
                                logger2.info(stringBuffer.toString());
                                logger.info("Closing connection.");
                                this.ois.close();
                                return;
                            } catch (Exception e5) {
                                e = e5;
                                logger.error("Unexpected exception. Closing conneciton.", e);
                                this.ois.close();
                                return;
                            }
                        }
                    } catch (EOFException e6) {
                    } catch (SocketException e7) {
                    } catch (IOException e8) {
                        e = e8;
                    } catch (Exception e9) {
                        e = e9;
                    }
                } catch (EOFException e10) {
                } catch (SocketException e11) {
                } catch (IOException e12) {
                    e = e12;
                } catch (Exception e13) {
                    e = e13;
                }
            } catch (EOFException e14) {
            } catch (SocketException e15) {
            } catch (IOException e16) {
                e = e16;
            } catch (Exception e17) {
                e = e17;
            }
        }
    }
}
