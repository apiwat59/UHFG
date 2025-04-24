package org.apache.log4j.config;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import org.apache.log4j.helpers.LogLog;

/* loaded from: classes.dex */
public class PropertyGetter {
    protected static final Object[] NULL_ARG = new Object[0];
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$org$apache$log4j$Priority;
    protected Object obj;
    protected PropertyDescriptor[] props;

    public interface PropertyCallback {
        void foundProperty(Object obj, String str, String str2, Object obj2);
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public PropertyGetter(Object obj) throws IntrospectionException {
        BeanInfo bi = Introspector.getBeanInfo(obj.getClass());
        this.props = bi.getPropertyDescriptors();
        this.obj = obj;
    }

    public static void getProperties(Object obj, PropertyCallback callback, String prefix) {
        try {
            new PropertyGetter(obj).getProperties(callback, prefix);
        } catch (IntrospectionException ex) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Failed to introspect object ");
            stringBuffer.append(obj);
            LogLog.error(stringBuffer.toString(), ex);
        }
    }

    public void getProperties(PropertyCallback callback, String prefix) {
        int i = 0;
        while (true) {
            PropertyDescriptor[] propertyDescriptorArr = this.props;
            if (i < propertyDescriptorArr.length) {
                Method getter = propertyDescriptorArr[i].getReadMethod();
                if (getter != null && isHandledType(getter.getReturnType())) {
                    String name = this.props[i].getName();
                    try {
                        Object result = getter.invoke(this.obj, NULL_ARG);
                        if (result != null) {
                            callback.foundProperty(this.obj, prefix, name, result);
                        }
                    } catch (Exception e) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("Failed to get value of property ");
                        stringBuffer.append(name);
                        LogLog.warn(stringBuffer.toString());
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    protected boolean isHandledType(Class type) {
        Class cls = class$java$lang$String;
        if (cls == null) {
            cls = class$("java.lang.String");
            class$java$lang$String = cls;
        }
        if (!cls.isAssignableFrom(type) && !Integer.TYPE.isAssignableFrom(type) && !Long.TYPE.isAssignableFrom(type) && !Boolean.TYPE.isAssignableFrom(type)) {
            Class cls2 = class$org$apache$log4j$Priority;
            if (cls2 == null) {
                cls2 = class$("org.apache.log4j.Priority");
                class$org$apache$log4j$Priority = cls2;
            }
            if (!cls2.isAssignableFrom(type)) {
                return false;
            }
        }
        return true;
    }
}
