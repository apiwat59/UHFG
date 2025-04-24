package org.apache.log4j.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.xml.DOMConfigurator;

/* loaded from: classes.dex */
public class JMSSink implements MessageListener {
    static /* synthetic */ Class class$org$apache$log4j$net$JMSSink;
    static Logger logger;

    static {
        Class cls = class$org$apache$log4j$net$JMSSink;
        if (cls == null) {
            cls = class$("org.apache.log4j.net.JMSSink");
            class$org$apache$log4j$net$JMSSink = cls;
        }
        logger = Logger.getLogger(cls);
    }

    public JMSSink(String str, String str2, String str3, String str4) {
        try {
            InitialContext initialContext = new InitialContext();
            TopicConnection createTopicConnection = ((TopicConnectionFactory) lookup(initialContext, str)).createTopicConnection(str3, str4);
            createTopicConnection.start();
            createTopicConnection.createTopicSession(false, 1).createSubscriber((Topic) initialContext.lookup(str2)).setMessageListener(this);
        } catch (Exception e) {
            logger.error("Could not read JMS message.", e);
        }
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    protected static Object lookup(Context context, String str) throws NamingException {
        try {
            return context.lookup(str);
        } catch (NameNotFoundException e) {
            Logger logger2 = logger;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Could not find name [");
            stringBuffer.append(str);
            stringBuffer.append("].");
            logger2.error(stringBuffer.toString());
            throw e;
        }
    }

    public static void main(String[] strArr) throws Exception {
        if (strArr.length != 5) {
            usage("Wrong number of arguments.");
        }
        String str = strArr[0];
        String str2 = strArr[1];
        String str3 = strArr[2];
        String str4 = strArr[3];
        String str5 = strArr[4];
        if (str5.endsWith(".xml")) {
            new DOMConfigurator();
            DOMConfigurator.configure(str5);
        } else {
            new PropertyConfigurator();
            PropertyConfigurator.configure(str5);
        }
        new JMSSink(str, str2, str3, str4);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Type \"exit\" to quit JMSSink.");
        while (!bufferedReader.readLine().equalsIgnoreCase("exit")) {
        }
        System.out.println("Exiting. Kill the application if it does not exit due to daemon threads.");
    }

    static void usage(String str) {
        System.err.println(str);
        PrintStream printStream = System.err;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Usage: java ");
        Class cls = class$org$apache$log4j$net$JMSSink;
        if (cls == null) {
            cls = class$("org.apache.log4j.net.JMSSink");
            class$org$apache$log4j$net$JMSSink = cls;
        }
        stringBuffer.append(cls.getName());
        stringBuffer.append(" TopicConnectionFactoryBindingName TopicBindingName username password configFile");
        printStream.println(stringBuffer.toString());
        System.exit(1);
    }

    public void onMessage(Message message) {
        try {
            if (message instanceof ObjectMessage) {
                LoggingEvent loggingEvent = (LoggingEvent) ((ObjectMessage) message).getObject();
                Logger.getLogger(loggingEvent.getLoggerName()).callAppenders(loggingEvent);
            } else {
                Logger logger2 = logger;
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("Received message is of type ");
                stringBuffer.append(message.getJMSType());
                stringBuffer.append(", was expecting ObjectMessage.");
                logger2.warn(stringBuffer.toString());
            }
        } catch (JMSException e) {
            logger.error("Exception thrown while processing incoming message.", e);
        }
    }
}
