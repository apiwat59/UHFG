package org.apache.log4j.config;

import java.beans.BeanInfo;
import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.OptionHandler;

/* loaded from: classes.dex */
public class PropertySetter {
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$org$apache$log4j$Priority;
    protected Object obj;
    protected PropertyDescriptor[] props;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public PropertySetter(Object obj) {
        this.obj = obj;
    }

    protected void introspect() {
        try {
            BeanInfo bi = Introspector.getBeanInfo(this.obj.getClass());
            this.props = bi.getPropertyDescriptors();
        } catch (IntrospectionException ex) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Failed to introspect ");
            stringBuffer.append(this.obj);
            stringBuffer.append(": ");
            stringBuffer.append(ex.getMessage());
            LogLog.error(stringBuffer.toString());
            this.props = new PropertyDescriptor[0];
        }
    }

    public static void setProperties(Object obj, Properties properties, String prefix) {
        new PropertySetter(obj).setProperties(properties, prefix);
    }

    public void setProperties(Properties properties, String prefix) {
        int len = prefix.length();
        Enumeration e = properties.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            if (key.startsWith(prefix) && key.indexOf(46, len + 1) <= 0) {
                String value = OptionConverter.findAndSubst(key, properties);
                String key2 = key.substring(len);
                if (!"layout".equals(key2) || !(this.obj instanceof Appender)) {
                    setProperty(key2, value);
                }
            }
        }
        activate();
    }

    public void setProperty(String name, String value) {
        if (value == null) {
            return;
        }
        String name2 = Introspector.decapitalize(name);
        PropertyDescriptor prop = getPropertyDescriptor(name2);
        if (prop == null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("No such property [");
            stringBuffer.append(name2);
            stringBuffer.append("] in ");
            stringBuffer.append(this.obj.getClass().getName());
            stringBuffer.append(".");
            LogLog.warn(stringBuffer.toString());
            return;
        }
        try {
            setProperty(prop, name2, value);
        } catch (PropertySetterException ex) {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("Failed to set property [");
            stringBuffer2.append(name2);
            stringBuffer2.append("] to value \"");
            stringBuffer2.append(value);
            stringBuffer2.append("\". ");
            LogLog.warn(stringBuffer2.toString(), ex.rootCause);
        }
    }

    public void setProperty(PropertyDescriptor prop, String name, String value) throws PropertySetterException {
        Method setter = prop.getWriteMethod();
        if (setter == null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("No setter for property [");
            stringBuffer.append(name);
            stringBuffer.append("].");
            throw new PropertySetterException(stringBuffer.toString());
        }
        Class[] paramTypes = setter.getParameterTypes();
        if (paramTypes.length != 1) {
            throw new PropertySetterException("#params for setter != 1");
        }
        try {
            Object arg = convertArg(value, paramTypes[0]);
            if (arg == null) {
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append("Conversion to type [");
                stringBuffer2.append(paramTypes[0]);
                stringBuffer2.append("] failed.");
                throw new PropertySetterException(stringBuffer2.toString());
            }
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append("Setting property [");
            stringBuffer3.append(name);
            stringBuffer3.append("] to [");
            stringBuffer3.append(arg);
            stringBuffer3.append("].");
            LogLog.debug(stringBuffer3.toString());
            try {
                setter.invoke(this.obj, arg);
            } catch (Exception ex) {
                throw new PropertySetterException(ex);
            }
        } catch (Throwable t) {
            StringBuffer stringBuffer4 = new StringBuffer();
            stringBuffer4.append("Conversion to type [");
            stringBuffer4.append(paramTypes[0]);
            stringBuffer4.append("] failed. Reason: ");
            stringBuffer4.append(t);
            throw new PropertySetterException(stringBuffer4.toString());
        }
    }

    protected Object convertArg(String val, Class type) {
        if (val == null) {
            return null;
        }
        String v = val.trim();
        Class cls = class$java$lang$String;
        if (cls == null) {
            cls = class$("java.lang.String");
            class$java$lang$String = cls;
        }
        if (cls.isAssignableFrom(type)) {
            return val;
        }
        if (Integer.TYPE.isAssignableFrom(type)) {
            return new Integer(v);
        }
        if (Long.TYPE.isAssignableFrom(type)) {
            return new Long(v);
        }
        if (Boolean.TYPE.isAssignableFrom(type)) {
            if ("true".equalsIgnoreCase(v)) {
                return Boolean.TRUE;
            }
            if ("false".equalsIgnoreCase(v)) {
                return Boolean.FALSE;
            }
        } else {
            Class cls2 = class$org$apache$log4j$Priority;
            if (cls2 == null) {
                cls2 = class$("org.apache.log4j.Priority");
                class$org$apache$log4j$Priority = cls2;
            }
            if (cls2.isAssignableFrom(type)) {
                return OptionConverter.toLevel(v, Level.DEBUG);
            }
        }
        return null;
    }

    protected PropertyDescriptor getPropertyDescriptor(String name) {
        if (this.props == null) {
            introspect();
        }
        int i = 0;
        while (true) {
            FeatureDescriptor[] featureDescriptorArr = this.props;
            if (i < featureDescriptorArr.length) {
                if (!name.equals(featureDescriptorArr[i].getName())) {
                    i++;
                } else {
                    return this.props[i];
                }
            } else {
                return null;
            }
        }
    }

    public void activate() {
        Object obj = this.obj;
        if (obj instanceof OptionHandler) {
            ((OptionHandler) obj).activateOptions();
        }
    }
}
