package org.apache.log4j.jmx;

import java.util.Enumeration;
import java.util.Vector;
import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.DateLayout;
import org.apache.log4j.helpers.OptionConverter;

/* loaded from: classes.dex */
public class LoggerDynamicMBean extends AbstractDynamicMBean implements NotificationListener {
    private static Logger cat;
    static /* synthetic */ Class class$org$apache$log4j$Appender;
    static /* synthetic */ Class class$org$apache$log4j$jmx$LoggerDynamicMBean;
    private Logger logger;
    private MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
    private MBeanOperationInfo[] dOperations = new MBeanOperationInfo[1];
    private Vector dAttributes = new Vector();
    private String dClassName = getClass().getName();
    private String dDescription = "This MBean acts as a management facade for a org.apache.log4j.Logger instance.";

    static {
        Class cls = class$org$apache$log4j$jmx$LoggerDynamicMBean;
        if (cls == null) {
            cls = class$("org.apache.log4j.jmx.LoggerDynamicMBean");
            class$org$apache$log4j$jmx$LoggerDynamicMBean = cls;
        }
        cat = Logger.getLogger(cls);
    }

    public LoggerDynamicMBean(Logger logger) {
        this.logger = logger;
        buildDynamicMBeanInfo();
    }

    private void buildDynamicMBeanInfo() {
        this.dConstructors[0] = new MBeanConstructorInfo("HierarchyDynamicMBean(): Constructs a HierarchyDynamicMBean instance", getClass().getConstructors()[0]);
        this.dAttributes.add(new MBeanAttributeInfo("name", "java.lang.String", "The name of this Logger.", true, false, false));
        this.dAttributes.add(new MBeanAttributeInfo("priority", "java.lang.String", "The priority of this logger.", true, true, false));
        this.dOperations[0] = new MBeanOperationInfo("addAppender", "addAppender(): add an appender", new MBeanParameterInfo[]{new MBeanParameterInfo("class name", "java.lang.String", "add an appender to this logger"), new MBeanParameterInfo("appender name", "java.lang.String", "name of the appender")}, "void", 1);
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    void addAppender(String str, String str2) {
        Logger logger = cat;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("addAppender called with ");
        stringBuffer.append(str);
        stringBuffer.append(", ");
        stringBuffer.append(str2);
        logger.debug(stringBuffer.toString());
        Class cls = class$org$apache$log4j$Appender;
        if (cls == null) {
            cls = class$("org.apache.log4j.Appender");
            class$org$apache$log4j$Appender = cls;
        }
        Appender appender = (Appender) OptionConverter.instantiateByClassName(str, cls, null);
        appender.setName(str2);
        this.logger.addAppender(appender);
    }

    void appenderMBeanRegistration() {
        Enumeration allAppenders = this.logger.getAllAppenders();
        while (allAppenders.hasMoreElements()) {
            registerAppenderMBean((Appender) allAppenders.nextElement());
        }
    }

    @Override // org.apache.log4j.jmx.AbstractDynamicMBean
    public Object getAttribute(String str) throws AttributeNotFoundException, MBeanException, ReflectionException {
        if (str == null) {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Attribute name cannot be null");
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Cannot invoke a getter of ");
            stringBuffer.append(this.dClassName);
            stringBuffer.append(" with null attribute name");
            throw new RuntimeOperationsException(illegalArgumentException, stringBuffer.toString());
        }
        if (str.equals("name")) {
            return this.logger.getName();
        }
        if (str.equals("priority")) {
            Level level = this.logger.getLevel();
            if (level == null) {
                return null;
            }
            return level.toString();
        }
        if (str.startsWith("appender=")) {
            try {
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append("log4j:");
                stringBuffer2.append(str);
                return new ObjectName(stringBuffer2.toString());
            } catch (Exception e) {
                Logger logger = cat;
                StringBuffer stringBuffer3 = new StringBuffer();
                stringBuffer3.append("Could not create ObjectName");
                stringBuffer3.append(str);
                logger.error(stringBuffer3.toString());
            }
        }
        StringBuffer stringBuffer4 = new StringBuffer();
        stringBuffer4.append("Cannot find ");
        stringBuffer4.append(str);
        stringBuffer4.append(" attribute in ");
        stringBuffer4.append(this.dClassName);
        throw new AttributeNotFoundException(stringBuffer4.toString());
    }

    @Override // org.apache.log4j.jmx.AbstractDynamicMBean
    protected Logger getLogger() {
        return this.logger;
    }

    @Override // org.apache.log4j.jmx.AbstractDynamicMBean
    public MBeanInfo getMBeanInfo() {
        MBeanAttributeInfo[] mBeanAttributeInfoArr = new MBeanAttributeInfo[this.dAttributes.size()];
        this.dAttributes.toArray(mBeanAttributeInfoArr);
        return new MBeanInfo(this.dClassName, this.dDescription, mBeanAttributeInfoArr, this.dConstructors, this.dOperations, new MBeanNotificationInfo[0]);
    }

    public void handleNotification(Notification notification, Object obj) {
        Logger logger = cat;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Received notification: ");
        stringBuffer.append(notification.getType());
        logger.debug(stringBuffer.toString());
        registerAppenderMBean((Appender) notification.getUserData());
    }

    @Override // org.apache.log4j.jmx.AbstractDynamicMBean
    public Object invoke(String str, Object[] objArr, String[] strArr) throws MBeanException, ReflectionException {
        if (!str.equals("addAppender")) {
            return null;
        }
        addAppender((String) objArr[0], (String) objArr[1]);
        return "Hello world.";
    }

    @Override // org.apache.log4j.jmx.AbstractDynamicMBean
    public void postRegister(Boolean bool) {
        appenderMBeanRegistration();
    }

    void registerAppenderMBean(Appender appender) {
        String name = appender.getName();
        Logger logger = cat;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Adding AppenderMBean for appender named ");
        stringBuffer.append(name);
        logger.debug(stringBuffer.toString());
        try {
            this.server.registerMBean(new AppenderDynamicMBean(appender), new ObjectName("log4j", "appender", name));
            Vector vector = this.dAttributes;
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("appender=");
            stringBuffer2.append(name);
            String stringBuffer3 = stringBuffer2.toString();
            StringBuffer stringBuffer4 = new StringBuffer();
            stringBuffer4.append("The ");
            stringBuffer4.append(name);
            stringBuffer4.append(" appender.");
            vector.add(new MBeanAttributeInfo(stringBuffer3, "javax.management.ObjectName", stringBuffer4.toString(), true, true, false));
        } catch (Exception e) {
            Logger logger2 = cat;
            StringBuffer stringBuffer5 = new StringBuffer();
            stringBuffer5.append("Could not add appenderMBean for [");
            stringBuffer5.append(name);
            stringBuffer5.append("].");
            logger2.error(stringBuffer5.toString(), e);
        }
    }

    @Override // org.apache.log4j.jmx.AbstractDynamicMBean
    public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException {
        if (attribute == null) {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException("Attribute cannot be null");
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Cannot invoke a setter of ");
            stringBuffer.append(this.dClassName);
            stringBuffer.append(" with null attribute");
            throw new RuntimeOperationsException(illegalArgumentException, stringBuffer.toString());
        }
        String name = attribute.getName();
        Object value = attribute.getValue();
        if (name == null) {
            IllegalArgumentException illegalArgumentException2 = new IllegalArgumentException("Attribute name cannot be null");
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("Cannot invoke the setter of ");
            stringBuffer2.append(this.dClassName);
            stringBuffer2.append(" with null attribute name");
            throw new RuntimeOperationsException(illegalArgumentException2, stringBuffer2.toString());
        }
        if (name.equals("priority")) {
            if (value instanceof String) {
                String str = (String) value;
                this.logger.setLevel(str.equalsIgnoreCase(DateLayout.NULL_DATE_FORMAT) ? null : OptionConverter.toLevel(str, this.logger.getLevel()));
                return;
            }
            return;
        }
        StringBuffer stringBuffer3 = new StringBuffer();
        stringBuffer3.append("Attribute ");
        stringBuffer3.append(name);
        stringBuffer3.append(" not found in ");
        stringBuffer3.append(getClass().getName());
        throw new AttributeNotFoundException(stringBuffer3.toString());
    }
}
