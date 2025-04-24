package org.apache.log4j.net;

import java.io.File;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RootLogger;

/* loaded from: classes.dex */
public class SocketServer {
    static Logger cat;
    static /* synthetic */ Class class$org$apache$log4j$net$SocketServer;
    static int port;
    static SocketServer server;
    File dir;
    LoggerRepository genericHierarchy;
    Hashtable hierarchyMap = new Hashtable(11);
    static String GENERIC = "generic";
    static String CONFIG_FILE_EXT = ".lcf";

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Class cls = class$org$apache$log4j$net$SocketServer;
        if (cls == null) {
            cls = class$("org.apache.log4j.net.SocketServer");
            class$org$apache$log4j$net$SocketServer = cls;
        }
        cat = Logger.getLogger(cls);
    }

    public static void main(String[] argv) {
        if (argv.length == 3) {
            init(argv[0], argv[1], argv[2]);
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
                InetAddress inetAddress = socket.getInetAddress();
                Logger logger2 = cat;
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append("Connected to client at ");
                stringBuffer2.append(inetAddress);
                logger2.info(stringBuffer2.toString());
                LoggerRepository h = (LoggerRepository) server.hierarchyMap.get(inetAddress);
                if (h == null) {
                    h = server.configureHierarchy(inetAddress);
                }
                cat.info("Starting new socket node.");
                new Thread(new SocketNode(socket, h)).start();
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
        Class cls = class$org$apache$log4j$net$SocketServer;
        if (cls == null) {
            cls = class$("org.apache.log4j.net.SocketServer");
            class$org$apache$log4j$net$SocketServer = cls;
        }
        stringBuffer.append(cls.getName());
        stringBuffer.append(" port configFile directory");
        printStream.println(stringBuffer.toString());
        System.exit(1);
    }

    static void init(String portStr, String configFile, String dirStr) {
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
        PropertyConfigurator.configure(configFile);
        File dir = new File(dirStr);
        if (!dir.isDirectory()) {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("[");
            stringBuffer2.append(dirStr);
            stringBuffer2.append("] is not a directory.");
            usage(stringBuffer2.toString());
        }
        server = new SocketServer(dir);
    }

    public SocketServer(File directory) {
        this.dir = directory;
    }

    LoggerRepository configureHierarchy(InetAddress inetAddress) {
        Logger logger = cat;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Locating configuration file for ");
        stringBuffer.append(inetAddress);
        logger.info(stringBuffer.toString());
        String s = inetAddress.toString();
        int i = s.indexOf("/");
        if (i == -1) {
            Logger logger2 = cat;
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("Could not parse the inetAddress [");
            stringBuffer2.append(inetAddress);
            stringBuffer2.append("]. Using default hierarchy.");
            logger2.warn(stringBuffer2.toString());
            return genericHierarchy();
        }
        String key = s.substring(0, i);
        File file = this.dir;
        StringBuffer stringBuffer3 = new StringBuffer();
        stringBuffer3.append(key);
        stringBuffer3.append(CONFIG_FILE_EXT);
        File configFile = new File(file, stringBuffer3.toString());
        if (configFile.exists()) {
            Hierarchy h = new Hierarchy(new RootLogger((Level) Priority.DEBUG));
            this.hierarchyMap.put(inetAddress, h);
            new PropertyConfigurator().doConfigure(configFile.getAbsolutePath(), h);
            return h;
        }
        Logger logger3 = cat;
        StringBuffer stringBuffer4 = new StringBuffer();
        stringBuffer4.append("Could not find config file [");
        stringBuffer4.append(configFile);
        stringBuffer4.append("].");
        logger3.warn(stringBuffer4.toString());
        return genericHierarchy();
    }

    LoggerRepository genericHierarchy() {
        if (this.genericHierarchy == null) {
            File file = this.dir;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(GENERIC);
            stringBuffer.append(CONFIG_FILE_EXT);
            File f = new File(file, stringBuffer.toString());
            if (f.exists()) {
                this.genericHierarchy = new Hierarchy(new RootLogger((Level) Priority.DEBUG));
                new PropertyConfigurator().doConfigure(f.getAbsolutePath(), this.genericHierarchy);
            } else {
                Logger logger = cat;
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append("Could not find config file [");
                stringBuffer2.append(f);
                stringBuffer2.append("]. Will use the default hierarchy.");
                logger.warn(stringBuffer2.toString());
                this.genericHierarchy = LogManager.getLoggerRepository();
            }
        }
        return this.genericHierarchy;
    }
}
