package org.apache.log4j.net;

import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

/* loaded from: classes.dex */
public class SimpleSocketServer {
    static Logger cat;
    static /* synthetic */ Class class$org$apache$log4j$net$SimpleSocketServer;
    static int port;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Class cls = class$org$apache$log4j$net$SimpleSocketServer;
        if (cls == null) {
            cls = class$("org.apache.log4j.net.SimpleSocketServer");
            class$org$apache$log4j$net$SimpleSocketServer = cls;
        }
        cat = Logger.getLogger(cls);
    }

    public static void main(String[] argv) {
        if (argv.length == 2) {
            init(argv[0], argv[1]);
        } else {
            usage("Wrong number of arguments.");
        }
        try {
            Logger logger = cat;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Listening on port ");
            stringBuffer.append(port);
            logger.info(stringBuffer.toString());
            ServerSocket serverSocket = new ServerSocket(port);
            while (true) {
                cat.info("Waiting to accept a new client.");
                Socket socket = serverSocket.accept();
                Logger logger2 = cat;
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append("Connected to client at ");
                stringBuffer2.append(socket.getInetAddress());
                logger2.info(stringBuffer2.toString());
                cat.info("Starting new socket node.");
                new Thread(new SocketNode(socket, LogManager.getLoggerRepository())).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void usage(String msg) {
        System.err.println(msg);
        PrintStream printStream = System.err;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Usage: java ");
        Class cls = class$org$apache$log4j$net$SimpleSocketServer;
        if (cls == null) {
            cls = class$("org.apache.log4j.net.SimpleSocketServer");
            class$org$apache$log4j$net$SimpleSocketServer = cls;
        }
        stringBuffer.append(cls.getName());
        stringBuffer.append(" port configFile");
        printStream.println(stringBuffer.toString());
        System.exit(1);
    }

    static void init(String portStr, String configFile) {
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Could not interpret port number [");
            stringBuffer.append(portStr);
            stringBuffer.append("].");
            usage(stringBuffer.toString());
        }
        if (configFile.endsWith(".xml")) {
            new DOMConfigurator();
            DOMConfigurator.configure(configFile);
        } else {
            new PropertyConfigurator();
            PropertyConfigurator.configure(configFile);
        }
    }
}
