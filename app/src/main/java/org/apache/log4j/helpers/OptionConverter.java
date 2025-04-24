package org.apache.log4j.helpers;

import com.gg.reader.api.protocol.gx.EnumG;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Properties;
import kotlin.text.Typography;
import org.apache.log4j.Level;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.LoggerRepository;

/* loaded from: classes.dex */
public class OptionConverter {
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$org$apache$log4j$Level;
    static /* synthetic */ Class class$org$apache$log4j$spi$Configurator;
    static String DELIM_START = "${";
    static char DELIM_STOP = '}';
    static int DELIM_START_LEN = 2;
    static int DELIM_STOP_LEN = 1;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private OptionConverter() {
    }

    public static String[] concatanateArrays(String[] l, String[] r) {
        int len = l.length + r.length;
        String[] a = new String[len];
        System.arraycopy(l, 0, a, 0, l.length);
        System.arraycopy(r, 0, a, l.length, r.length);
        return a;
    }

    public static String convertSpecialChars(String s) {
        char c;
        int len = s.length();
        StringBuffer sbuf = new StringBuffer(len);
        int i = 0;
        while (i < len) {
            int i2 = i + 1;
            char c2 = s.charAt(i);
            if (c2 != '\\') {
                c = c2;
                i = i2;
            } else {
                int i3 = i2 + 1;
                char c3 = s.charAt(i2);
                if (c3 == 'n') {
                    c = '\n';
                    i = i3;
                } else if (c3 == 'r') {
                    c = '\r';
                    i = i3;
                } else if (c3 == 't') {
                    c = '\t';
                    i = i3;
                } else if (c3 == 'f') {
                    c = '\f';
                    i = i3;
                } else if (c3 == '\b') {
                    c = '\b';
                    i = i3;
                } else if (c3 == '\"') {
                    c = '\"';
                    i = i3;
                } else if (c3 == '\'') {
                    c = '\'';
                    i = i3;
                } else {
                    if (c3 == '\\') {
                        c3 = '\\';
                    }
                    c = c3;
                    i = i3;
                }
            }
            sbuf.append(c);
        }
        return sbuf.toString();
    }

    public static String getSystemProperty(String key, String def) {
        try {
            return System.getProperty(key, def);
        } catch (Throwable th) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Was not allowed to read system property \"");
            stringBuffer.append(key);
            stringBuffer.append("\".");
            LogLog.debug(stringBuffer.toString());
            return def;
        }
    }

    public static Object instantiateByKey(Properties props, String key, Class superClass, Object defaultValue) {
        String className = findAndSubst(key, props);
        if (className == null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Could not find value for key ");
            stringBuffer.append(key);
            LogLog.error(stringBuffer.toString());
            return defaultValue;
        }
        return instantiateByClassName(className.trim(), superClass, defaultValue);
    }

    public static boolean toBoolean(String value, boolean dEfault) {
        if (value == null) {
            return dEfault;
        }
        String trimmedVal = value.trim();
        if ("true".equalsIgnoreCase(trimmedVal)) {
            return true;
        }
        if ("false".equalsIgnoreCase(trimmedVal)) {
            return false;
        }
        return dEfault;
    }

    public static int toInt(String value, int dEfault) {
        if (value != null) {
            String s = value.trim();
            try {
                return Integer.valueOf(s).intValue();
            } catch (NumberFormatException e) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("[");
                stringBuffer.append(s);
                stringBuffer.append("] is not in proper int form.");
                LogLog.error(stringBuffer.toString());
                e.printStackTrace();
            }
        }
        return dEfault;
    }

    public static Level toLevel(String value, Level defaultValue) {
        Class customLevel;
        Class[] paramTypes;
        Method toLevelMethod;
        Object[] params;
        if (value == null) {
            return defaultValue;
        }
        String value2 = value.trim();
        int hashIndex = value2.indexOf(35);
        Object o = null;
        if (hashIndex == -1) {
            if (DateLayout.NULL_DATE_FORMAT.equalsIgnoreCase(value2)) {
                return null;
            }
            return Level.toLevel(value2, defaultValue);
        }
        String clazz = value2.substring(hashIndex + 1);
        String levelName = value2.substring(0, hashIndex);
        if (DateLayout.NULL_DATE_FORMAT.equalsIgnoreCase(levelName)) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("toLevel:class=[");
        stringBuffer.append(clazz);
        stringBuffer.append("]");
        stringBuffer.append(":pri=[");
        stringBuffer.append(levelName);
        stringBuffer.append("]");
        LogLog.debug(stringBuffer.toString());
        try {
            try {
                customLevel = Loader.loadClass(clazz);
            } catch (ClassCastException e) {
                e = e;
            } catch (IllegalAccessException e2) {
                e = e2;
            } catch (NoSuchMethodException e3) {
                e = e3;
            } catch (InvocationTargetException e4) {
                e = e4;
            } catch (Exception e5) {
                e = e5;
            }
            try {
                try {
                    paramTypes = new Class[2];
                    Class cls = class$java$lang$String;
                    if (cls == null) {
                        cls = class$("java.lang.String");
                        class$java$lang$String = cls;
                    }
                    paramTypes[0] = cls;
                    Class cls2 = class$org$apache$log4j$Level;
                    if (cls2 == null) {
                        cls2 = class$("org.apache.log4j.Level");
                        class$org$apache$log4j$Level = cls2;
                    }
                    paramTypes[1] = cls2;
                } catch (ClassCastException e6) {
                    e = e6;
                    StringBuffer stringBuffer2 = new StringBuffer();
                    stringBuffer2.append("class [");
                    stringBuffer2.append(clazz);
                    stringBuffer2.append("] is not a subclass of org.apache.log4j.Level");
                    LogLog.warn(stringBuffer2.toString(), e);
                    return defaultValue;
                } catch (IllegalAccessException e7) {
                    e = e7;
                    StringBuffer stringBuffer3 = new StringBuffer();
                    stringBuffer3.append("class [");
                    stringBuffer3.append(clazz);
                    stringBuffer3.append("] cannot be instantiated due to access restrictions");
                    LogLog.warn(stringBuffer3.toString(), e);
                    return defaultValue;
                } catch (InvocationTargetException e8) {
                    e = e8;
                    StringBuffer stringBuffer4 = new StringBuffer();
                    stringBuffer4.append("custom level class [");
                    stringBuffer4.append(clazz);
                    stringBuffer4.append("]");
                    stringBuffer4.append(" could not be instantiated");
                    LogLog.warn(stringBuffer4.toString(), e);
                    return defaultValue;
                } catch (Exception e9) {
                    e = e9;
                    StringBuffer stringBuffer5 = new StringBuffer();
                    stringBuffer5.append("class [");
                    stringBuffer5.append(clazz);
                    stringBuffer5.append("], level [");
                    stringBuffer5.append(levelName);
                    stringBuffer5.append("] conversion failed.");
                    LogLog.warn(stringBuffer5.toString(), e);
                    return defaultValue;
                }
                try {
                    try {
                        toLevelMethod = customLevel.getMethod("toLevel", paramTypes);
                    } catch (ClassCastException e10) {
                        e = e10;
                    } catch (IllegalAccessException e11) {
                        e = e11;
                    } catch (Exception e12) {
                        e = e12;
                    }
                    try {
                        try {
                            params = new Object[]{levelName, defaultValue};
                        } catch (IllegalAccessException e13) {
                            e = e13;
                            StringBuffer stringBuffer32 = new StringBuffer();
                            stringBuffer32.append("class [");
                            stringBuffer32.append(clazz);
                            stringBuffer32.append("] cannot be instantiated due to access restrictions");
                            LogLog.warn(stringBuffer32.toString(), e);
                            return defaultValue;
                        } catch (Exception e14) {
                            e = e14;
                            StringBuffer stringBuffer52 = new StringBuffer();
                            stringBuffer52.append("class [");
                            stringBuffer52.append(clazz);
                            stringBuffer52.append("], level [");
                            stringBuffer52.append(levelName);
                            stringBuffer52.append("] conversion failed.");
                            LogLog.warn(stringBuffer52.toString(), e);
                            return defaultValue;
                        }
                        try {
                            o = toLevelMethod.invoke(null, params);
                            Level result = (Level) o;
                            return result;
                        } catch (IllegalAccessException e15) {
                            e = e15;
                            StringBuffer stringBuffer322 = new StringBuffer();
                            stringBuffer322.append("class [");
                            stringBuffer322.append(clazz);
                            stringBuffer322.append("] cannot be instantiated due to access restrictions");
                            LogLog.warn(stringBuffer322.toString(), e);
                            return defaultValue;
                        } catch (Exception e16) {
                            e = e16;
                            StringBuffer stringBuffer522 = new StringBuffer();
                            stringBuffer522.append("class [");
                            stringBuffer522.append(clazz);
                            stringBuffer522.append("], level [");
                            stringBuffer522.append(levelName);
                            stringBuffer522.append("] conversion failed.");
                            LogLog.warn(stringBuffer522.toString(), e);
                            return defaultValue;
                        }
                    } catch (ClassCastException e17) {
                        e = e17;
                        StringBuffer stringBuffer22 = new StringBuffer();
                        stringBuffer22.append("class [");
                        stringBuffer22.append(clazz);
                        stringBuffer22.append("] is not a subclass of org.apache.log4j.Level");
                        LogLog.warn(stringBuffer22.toString(), e);
                        return defaultValue;
                    }
                } catch (InvocationTargetException e18) {
                    e = e18;
                    StringBuffer stringBuffer42 = new StringBuffer();
                    stringBuffer42.append("custom level class [");
                    stringBuffer42.append(clazz);
                    stringBuffer42.append("]");
                    stringBuffer42.append(" could not be instantiated");
                    LogLog.warn(stringBuffer42.toString(), e);
                    return defaultValue;
                }
            } catch (NoSuchMethodException e19) {
                e = e19;
                StringBuffer stringBuffer6 = new StringBuffer();
                stringBuffer6.append("custom level class [");
                stringBuffer6.append(clazz);
                stringBuffer6.append("]");
                stringBuffer6.append(" does not have a constructor which takes one string parameter");
                LogLog.warn(stringBuffer6.toString(), e);
                return defaultValue;
            }
        } catch (ClassNotFoundException e20) {
            StringBuffer stringBuffer7 = new StringBuffer();
            stringBuffer7.append("custom level class [");
            stringBuffer7.append(clazz);
            stringBuffer7.append("] not found.");
            LogLog.warn(stringBuffer7.toString());
            return defaultValue;
        }
    }

    public static long toFileSize(String value, long dEfault) {
        if (value == null) {
            return dEfault;
        }
        String s = value.trim().toUpperCase();
        long multiplier = 1;
        int index = s.indexOf("KB");
        if (index != -1) {
            multiplier = 1024;
            s = s.substring(0, index);
        } else {
            int index2 = s.indexOf("MB");
            if (index2 != -1) {
                multiplier = 1048576;
                s = s.substring(0, index2);
            } else {
                int index3 = s.indexOf("GB");
                if (index3 != -1) {
                    multiplier = EnumG.AntennaNo_31;
                    s = s.substring(0, index3);
                }
            }
        }
        if (s != null) {
            try {
                return Long.valueOf(s).longValue() * multiplier;
            } catch (NumberFormatException e) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("[");
                stringBuffer.append(s);
                stringBuffer.append("] is not in proper int form.");
                LogLog.error(stringBuffer.toString());
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append("[");
                stringBuffer2.append(value);
                stringBuffer2.append("] not in expected format.");
                LogLog.error(stringBuffer2.toString(), e);
            }
        }
        return dEfault;
    }

    public static String findAndSubst(String key, Properties props) {
        String value = props.getProperty(key);
        if (value == null) {
            return null;
        }
        try {
            return substVars(value, props);
        } catch (IllegalArgumentException e) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Bad option value [");
            stringBuffer.append(value);
            stringBuffer.append("].");
            LogLog.error(stringBuffer.toString(), e);
            return value;
        }
    }

    public static Object instantiateByClassName(String className, Class superClass, Object defaultValue) {
        if (className != null) {
            try {
                Class classObj = Loader.loadClass(className);
                if (!superClass.isAssignableFrom(classObj)) {
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("A \"");
                    stringBuffer.append(className);
                    stringBuffer.append("\" object is not assignable to a \"");
                    stringBuffer.append(superClass.getName());
                    stringBuffer.append("\" variable.");
                    LogLog.error(stringBuffer.toString());
                    StringBuffer stringBuffer2 = new StringBuffer();
                    stringBuffer2.append("The class \"");
                    stringBuffer2.append(superClass.getName());
                    stringBuffer2.append("\" was loaded by ");
                    LogLog.error(stringBuffer2.toString());
                    StringBuffer stringBuffer3 = new StringBuffer();
                    stringBuffer3.append("[");
                    stringBuffer3.append(superClass.getClassLoader());
                    stringBuffer3.append("] whereas object of type ");
                    LogLog.error(stringBuffer3.toString());
                    StringBuffer stringBuffer4 = new StringBuffer();
                    stringBuffer4.append("\"");
                    stringBuffer4.append(classObj.getName());
                    stringBuffer4.append("\" was loaded by [");
                    stringBuffer4.append(classObj.getClassLoader());
                    stringBuffer4.append("].");
                    LogLog.error(stringBuffer4.toString());
                    return defaultValue;
                }
                return classObj.newInstance();
            } catch (Exception e) {
                StringBuffer stringBuffer5 = new StringBuffer();
                stringBuffer5.append("Could not instantiate class [");
                stringBuffer5.append(className);
                stringBuffer5.append("].");
                LogLog.error(stringBuffer5.toString(), e);
            }
        }
        return defaultValue;
    }

    public static String substVars(String val, Properties props) throws IllegalArgumentException {
        StringBuffer sbuf = new StringBuffer();
        int i = 0;
        while (true) {
            int j = val.indexOf(DELIM_START, i);
            if (j == -1) {
                if (i == 0) {
                    return val;
                }
                sbuf.append(val.substring(i, val.length()));
                return sbuf.toString();
            }
            sbuf.append(val.substring(i, j));
            int k = val.indexOf(DELIM_STOP, j);
            if (k == -1) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(Typography.quote);
                stringBuffer.append(val);
                stringBuffer.append("\" has no closing brace. Opening brace at position ");
                stringBuffer.append(j);
                stringBuffer.append('.');
                throw new IllegalArgumentException(stringBuffer.toString());
            }
            String key = val.substring(j + DELIM_START_LEN, k);
            String replacement = getSystemProperty(key, null);
            if (replacement == null && props != null) {
                replacement = props.getProperty(key);
            }
            if (replacement != null) {
                String recursiveReplacement = substVars(replacement, props);
                sbuf.append(recursiveReplacement);
            }
            i = k + DELIM_STOP_LEN;
        }
    }

    public static void selectAndConfigure(URL url, String clazz, LoggerRepository hierarchy) {
        Configurator configurator;
        String filename = url.getFile();
        if (clazz == null && filename != null && filename.endsWith(".xml")) {
            clazz = "org.apache.log4j.xml.DOMConfigurator";
        }
        if (clazz != null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Preferred configurator class: ");
            stringBuffer.append(clazz);
            LogLog.debug(stringBuffer.toString());
            Class cls = class$org$apache$log4j$spi$Configurator;
            if (cls == null) {
                cls = class$("org.apache.log4j.spi.Configurator");
                class$org$apache$log4j$spi$Configurator = cls;
            }
            configurator = (Configurator) instantiateByClassName(clazz, cls, null);
            if (configurator == null) {
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append("Could not instantiate configurator [");
                stringBuffer2.append(clazz);
                stringBuffer2.append("].");
                LogLog.error(stringBuffer2.toString());
                return;
            }
        } else {
            configurator = new PropertyConfigurator();
        }
        configurator.doConfigure(url, hierarchy);
    }
}
