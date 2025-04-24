package org.apache.log4j.helpers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;

/* loaded from: classes.dex */
public class Loader {
    static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";
    static /* synthetic */ Class class$java$lang$Thread;
    static /* synthetic */ Class class$org$apache$log4j$helpers$Loader;
    private static boolean ignoreTCL;
    private static boolean java1;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        int i;
        java1 = true;
        ignoreTCL = false;
        String prop = OptionConverter.getSystemProperty("java.version", null);
        if (prop != null && (i = prop.indexOf(46)) != -1 && prop.charAt(i + 1) != '1') {
            java1 = false;
        }
        String ignoreTCLProp = OptionConverter.getSystemProperty("log4j.ignoreTCL", null);
        if (ignoreTCLProp == null) {
            return;
        }
        ignoreTCL = OptionConverter.toBoolean(ignoreTCLProp, true);
    }

    public static URL getResource(String resource, Class clazz) {
        return getResource(resource);
    }

    public static URL getResource(String resource) {
        ClassLoader classLoader;
        try {
            if (!java1 && (classLoader = getTCL()) != null) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("Trying to find [");
                stringBuffer.append(resource);
                stringBuffer.append("] using context classloader ");
                stringBuffer.append(classLoader);
                stringBuffer.append(".");
                LogLog.debug(stringBuffer.toString());
                URL url = classLoader.getResource(resource);
                if (url != null) {
                    return url;
                }
            }
            Class cls = class$org$apache$log4j$helpers$Loader;
            if (cls == null) {
                cls = class$("org.apache.log4j.helpers.Loader");
                class$org$apache$log4j$helpers$Loader = cls;
            }
            ClassLoader classLoader2 = cls.getClassLoader();
            if (classLoader2 != null) {
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append("Trying to find [");
                stringBuffer2.append(resource);
                stringBuffer2.append("] using ");
                stringBuffer2.append(classLoader2);
                stringBuffer2.append(" class loader.");
                LogLog.debug(stringBuffer2.toString());
                URL url2 = classLoader2.getResource(resource);
                if (url2 != null) {
                    return url2;
                }
            }
        } catch (Throwable t) {
            LogLog.warn(TSTR, t);
        }
        StringBuffer stringBuffer3 = new StringBuffer();
        stringBuffer3.append("Trying to find [");
        stringBuffer3.append(resource);
        stringBuffer3.append("] using ClassLoader.getSystemResource().");
        LogLog.debug(stringBuffer3.toString());
        return ClassLoader.getSystemResource(resource);
    }

    public static boolean isJava1() {
        return java1;
    }

    private static ClassLoader getTCL() throws IllegalAccessException, InvocationTargetException {
        try {
            Class cls = class$java$lang$Thread;
            if (cls == null) {
                cls = class$("java.lang.Thread");
                class$java$lang$Thread = cls;
            }
            Method method = cls.getMethod("getContextClassLoader", null);
            return (ClassLoader) method.invoke(Thread.currentThread(), null);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Class loadClass(String clazz) throws ClassNotFoundException {
        if (java1 || ignoreTCL) {
            return Class.forName(clazz);
        }
        try {
            return getTCL().loadClass(clazz);
        } catch (Throwable th) {
            return Class.forName(clazz);
        }
    }
}
