package org.apache.log4j.jmx;

import java.util.Iterator;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import org.apache.log4j.Logger;

/* loaded from: classes.dex */
public abstract class AbstractDynamicMBean implements DynamicMBean, MBeanRegistration {
    String dClassName;
    MBeanServer server;

    public abstract Object getAttribute(String str) throws AttributeNotFoundException, MBeanException, ReflectionException;

    public AttributeList getAttributes(String[] strArr) {
        if (strArr == null) {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException("attributeNames[] cannot be null");
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Cannot invoke a getter of ");
            stringBuffer.append(this.dClassName);
            throw new RuntimeOperationsException(illegalArgumentException, stringBuffer.toString());
        }
        AttributeList attributeList = new AttributeList();
        if (strArr.length == 0) {
            return attributeList;
        }
        for (int i = 0; i < strArr.length; i++) {
            try {
                attributeList.add(new Attribute(strArr[i], getAttribute(strArr[i])));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return attributeList;
    }

    protected abstract Logger getLogger();

    public abstract MBeanInfo getMBeanInfo();

    public abstract Object invoke(String str, Object[] objArr, String[] strArr) throws MBeanException, ReflectionException;

    public void postDeregister() {
        getLogger().debug("postDeregister is called.");
    }

    public void postRegister(Boolean bool) {
    }

    public void preDeregister() {
        getLogger().debug("preDeregister called.");
    }

    public ObjectName preRegister(MBeanServer mBeanServer, ObjectName objectName) {
        Logger logger = getLogger();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("preRegister called. Server=");
        stringBuffer.append(mBeanServer);
        stringBuffer.append(", name=");
        stringBuffer.append(objectName);
        logger.debug(stringBuffer.toString());
        this.server = mBeanServer;
        return objectName;
    }

    public abstract void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException;

    public AttributeList setAttributes(AttributeList attributeList) {
        if (attributeList == null) {
            IllegalArgumentException illegalArgumentException = new IllegalArgumentException("AttributeList attributes cannot be null");
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Cannot invoke a setter of ");
            stringBuffer.append(this.dClassName);
            throw new RuntimeOperationsException(illegalArgumentException, stringBuffer.toString());
        }
        AttributeList attributeList2 = new AttributeList();
        if (attributeList.isEmpty()) {
            return attributeList2;
        }
        Iterator it = attributeList.iterator();
        while (it.hasNext()) {
            Attribute attribute = (Attribute) it.next();
            try {
                setAttribute(attribute);
                String name = attribute.getName();
                attributeList2.add(new Attribute(name, getAttribute(name)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return attributeList2;
    }
}
