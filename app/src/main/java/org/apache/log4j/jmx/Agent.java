package org.apache.log4j.jmx;

import com.sun.jdmk.comm.HtmlAdaptorServer;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import org.apache.log4j.Logger;

/* loaded from: classes.dex */
public class Agent {
    static /* synthetic */ Class class$org$apache$log4j$jmx$Agent;
    static Logger log;

    static {
        Class cls = class$org$apache$log4j$jmx$Agent;
        if (cls == null) {
            cls = class$("org.apache.log4j.jmx.Agent");
            class$org$apache$log4j$jmx$Agent = cls;
        }
        log = Logger.getLogger(cls);
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    public void start() {
        MBeanServer createMBeanServer = MBeanServerFactory.createMBeanServer();
        HtmlAdaptorServer htmlAdaptorServer = new HtmlAdaptorServer();
        try {
            log.info("Registering HtmlAdaptorServer instance.");
            createMBeanServer.registerMBean(htmlAdaptorServer, new ObjectName("Adaptor:name=html,port=8082"));
            log.info("Registering HierarchyDynamicMBean instance.");
            createMBeanServer.registerMBean(new HierarchyDynamicMBean(), new ObjectName("log4j:hiearchy=default"));
            htmlAdaptorServer.start();
        } catch (Exception e) {
            log.error("Problem while regitering MBeans instances.", e);
        }
    }
}
