package org.apache.log4j.jmx;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Hashtable;
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
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.OptionHandler;

/* loaded from: classes.dex */
public class LayoutDynamicMBean extends AbstractDynamicMBean {
    private static Logger cat;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$org$apache$log4j$Level;
    static /* synthetic */ Class class$org$apache$log4j$Priority;
    static /* synthetic */ Class class$org$apache$log4j$jmx$LayoutDynamicMBean;
    private Layout layout;
    private MBeanConstructorInfo[] dConstructors = new MBeanConstructorInfo[1];
    private Vector dAttributes = new Vector();
    private String dClassName = getClass().getName();
    private Hashtable dynamicProps = new Hashtable(5);
    private MBeanOperationInfo[] dOperations = new MBeanOperationInfo[1];
    private String dDescription = "This MBean acts as a management facade for log4j layouts.";

    static {
        Class cls = class$org$apache$log4j$jmx$LayoutDynamicMBean;
        if (cls == null) {
            cls = class$("org.apache.log4j.jmx.LayoutDynamicMBean");
            class$org$apache$log4j$jmx$LayoutDynamicMBean = cls;
        }
        cat = Logger.getLogger(cls);
    }

    public LayoutDynamicMBean(Layout layout) throws IntrospectionException {
        this.layout = layout;
        buildDynamicMBeanInfo();
    }

    private void buildDynamicMBeanInfo() throws IntrospectionException {
        int i = 0;
        this.dConstructors[0] = new MBeanConstructorInfo("LayoutDynamicMBean(): Constructs a LayoutDynamicMBean instance", getClass().getConstructors()[0]);
        PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(this.layout.getClass()).getPropertyDescriptors();
        int length = propertyDescriptors.length;
        int i2 = 0;
        while (i2 < length) {
            String name = propertyDescriptors[i2].getName();
            Method readMethod = propertyDescriptors[i2].getReadMethod();
            Method writeMethod = propertyDescriptors[i2].getWriteMethod();
            if (readMethod != null) {
                Class<?> returnType = readMethod.getReturnType();
                if (isSupportedType(returnType)) {
                    Class<?> cls = class$org$apache$log4j$Level;
                    if (cls == null) {
                        cls = class$("org.apache.log4j.Level");
                        class$org$apache$log4j$Level = cls;
                    }
                    this.dAttributes.add(new MBeanAttributeInfo(name, returnType.isAssignableFrom(cls) ? "java.lang.String" : returnType.getName(), "Dynamic", true, writeMethod != null, false));
                    this.dynamicProps.put(name, new MethodUnion(readMethod, writeMethod));
                }
            }
            i2++;
            i = 0;
        }
        this.dOperations[i] = new MBeanOperationInfo("activateOptions", "activateOptions(): add an layout", new MBeanParameterInfo[i], "void", 1);
    }

    static /* synthetic */ Class class$(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new NoClassDefFoundError(e.getMessage());
        }
    }

    private boolean isSupportedType(Class cls) {
        if (cls.isPrimitive()) {
            return true;
        }
        Class cls2 = class$java$lang$String;
        if (cls2 == null) {
            cls2 = class$("java.lang.String");
            class$java$lang$String = cls2;
        }
        if (cls == cls2) {
            return true;
        }
        Class<?> cls3 = class$org$apache$log4j$Level;
        if (cls3 == null) {
            cls3 = class$("org.apache.log4j.Level");
            class$org$apache$log4j$Level = cls3;
        }
        return cls.isAssignableFrom(cls3);
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
        MethodUnion methodUnion = (MethodUnion) this.dynamicProps.get(str);
        Logger logger = cat;
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append("----name=");
        stringBuffer2.append(str);
        stringBuffer2.append(", mu=");
        stringBuffer2.append(methodUnion);
        logger.debug(stringBuffer2.toString());
        if (methodUnion != null && methodUnion.readMethod != null) {
            try {
                return methodUnion.readMethod.invoke(this.layout, null);
            } catch (Exception e) {
                return null;
            }
        }
        StringBuffer stringBuffer3 = new StringBuffer();
        stringBuffer3.append("Cannot find ");
        stringBuffer3.append(str);
        stringBuffer3.append(" attribute in ");
        stringBuffer3.append(this.dClassName);
        throw new AttributeNotFoundException(stringBuffer3.toString());
    }

    @Override // org.apache.log4j.jmx.AbstractDynamicMBean
    protected Logger getLogger() {
        return cat;
    }

    @Override // org.apache.log4j.jmx.AbstractDynamicMBean
    public MBeanInfo getMBeanInfo() {
        cat.debug("getMBeanInfo called.");
        MBeanAttributeInfo[] mBeanAttributeInfoArr = new MBeanAttributeInfo[this.dAttributes.size()];
        this.dAttributes.toArray(mBeanAttributeInfoArr);
        return new MBeanInfo(this.dClassName, this.dDescription, mBeanAttributeInfoArr, this.dConstructors, this.dOperations, new MBeanNotificationInfo[0]);
    }

    @Override // org.apache.log4j.jmx.AbstractDynamicMBean
    public Object invoke(String str, Object[] objArr, String[] strArr) throws MBeanException, ReflectionException {
        if (!str.equals("activateOptions")) {
            return null;
        }
        Layout layout = this.layout;
        if (!(layout instanceof OptionHandler)) {
            return null;
        }
        layout.activateOptions();
        return "Options activated.";
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
        MethodUnion methodUnion = (MethodUnion) this.dynamicProps.get(name);
        if (methodUnion == null || methodUnion.writeMethod == null) {
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append("Attribute ");
            stringBuffer3.append(name);
            stringBuffer3.append(" not found in ");
            stringBuffer3.append(getClass().getName());
            throw new AttributeNotFoundException(stringBuffer3.toString());
        }
        Object[] objArr = new Object[1];
        Class<?> cls = methodUnion.writeMethod.getParameterTypes()[0];
        Class<?> cls2 = class$org$apache$log4j$Priority;
        if (cls2 == null) {
            cls2 = class$("org.apache.log4j.Priority");
            class$org$apache$log4j$Priority = cls2;
        }
        if (cls == cls2) {
            value = OptionConverter.toLevel((String) value, (Level) getAttribute(name));
        }
        objArr[0] = value;
        try {
            methodUnion.writeMethod.invoke(this.layout, objArr);
        } catch (Exception e) {
            cat.error("FIXME", e);
        }
    }
}
