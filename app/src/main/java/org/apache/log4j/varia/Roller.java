package org.apache.log4j.varia;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

/* loaded from: classes.dex */
public class Roller {
    static Logger cat;
    static /* synthetic */ Class class$org$apache$log4j$varia$Roller;
    static String host;
    static int port;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Class cls = class$org$apache$log4j$varia$Roller;
        if (cls == null) {
            cls = class$("org.apache.log4j.varia.Roller");
            class$org$apache$log4j$varia$Roller = cls;
        }
        cat = Logger.getLogger(cls);
    }

    Roller() {
    }

    public static void main(String[] argv) {
        BasicConfigurator.configure();
        if (argv.length == 2) {
            init(argv[0], argv[1]);
        } else {
            usage("Wrong number of arguments.");
        }
        roll();
    }

    static void usage(String msg) {
        System.err.println(msg);
        PrintStream printStream = System.err;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Usage: java ");
        Class cls = class$org$apache$log4j$varia$Roller;
        if (cls == null) {
            cls = class$("org.apache.log4j.varia.Roller");
            class$org$apache$log4j$varia$Roller = cls;
        }
        stringBuffer.append(cls.getName());
        stringBuffer.append("host_name port_number");
        printStream.println(stringBuffer.toString());
        System.exit(1);
    }

    static void init(String hostArg, String portArg) {
        host = hostArg;
        try {
            port = Integer.parseInt(portArg);
        } catch (NumberFormatException e) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Second argument ");
            stringBuffer.append(portArg);
            stringBuffer.append(" is not a valid integer.");
            usage(stringBuffer.toString());
        }
    }

    static void roll() {
        try {
            Socket socket = new Socket(host, port);
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            dos.writeUTF(ExternallyRolledFileAppender.ROLL_OVER);
            String rc = dis.readUTF();
            if (ExternallyRolledFileAppender.OK.equals(rc)) {
                cat.info("Roll over signal acknowledged by remote appender.");
            } else {
                Logger logger = cat;
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("Unexpected return code ");
                stringBuffer.append(rc);
                stringBuffer.append(" from remote entity.");
                logger.warn(stringBuffer.toString());
                System.exit(2);
            }
        } catch (IOException e) {
            Logger logger2 = cat;
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("Could not send roll signal on host ");
            stringBuffer2.append(host);
            stringBuffer2.append(" port ");
            stringBuffer2.append(port);
            stringBuffer2.append(" .");
            logger2.error(stringBuffer2.toString(), e);
            System.exit(2);
        }
        System.exit(0);
    }
}
