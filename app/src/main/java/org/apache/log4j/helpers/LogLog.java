package org.apache.log4j.helpers;

import java.io.PrintStream;

/* loaded from: classes.dex */
public class LogLog {
    public static final String CONFIG_DEBUG_KEY = "log4j.configDebug";
    public static final String DEBUG_KEY = "log4j.debug";
    private static final String ERR_PREFIX = "log4j:ERROR ";
    private static final String PREFIX = "log4j: ";
    private static final String WARN_PREFIX = "log4j:WARN ";
    protected static boolean debugEnabled;
    private static boolean quietMode = false;

    static {
        debugEnabled = false;
        String key = OptionConverter.getSystemProperty(DEBUG_KEY, null);
        if (key == null) {
            key = OptionConverter.getSystemProperty(CONFIG_DEBUG_KEY, null);
        }
        if (key == null) {
            return;
        }
        debugEnabled = OptionConverter.toBoolean(key, true);
    }

    public static void setInternalDebugging(boolean enabled) {
        debugEnabled = enabled;
    }

    public static void debug(String msg) {
        if (debugEnabled && !quietMode) {
            PrintStream printStream = System.out;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(PREFIX);
            stringBuffer.append(msg);
            printStream.println(stringBuffer.toString());
        }
    }

    public static void debug(String msg, Throwable t) {
        if (debugEnabled && !quietMode) {
            PrintStream printStream = System.out;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(PREFIX);
            stringBuffer.append(msg);
            printStream.println(stringBuffer.toString());
            if (t != null) {
                t.printStackTrace(System.out);
            }
        }
    }

    public static void error(String msg) {
        if (quietMode) {
            return;
        }
        PrintStream printStream = System.err;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(ERR_PREFIX);
        stringBuffer.append(msg);
        printStream.println(stringBuffer.toString());
    }

    public static void error(String msg, Throwable t) {
        if (quietMode) {
            return;
        }
        PrintStream printStream = System.err;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(ERR_PREFIX);
        stringBuffer.append(msg);
        printStream.println(stringBuffer.toString());
        if (t != null) {
            t.printStackTrace();
        }
    }

    public static void setQuietMode(boolean quietMode2) {
        quietMode = quietMode2;
    }

    public static void warn(String msg) {
        if (quietMode) {
            return;
        }
        PrintStream printStream = System.err;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(WARN_PREFIX);
        stringBuffer.append(msg);
        printStream.println(stringBuffer.toString());
    }

    public static void warn(String msg, Throwable t) {
        if (quietMode) {
            return;
        }
        PrintStream printStream = System.err;
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(WARN_PREFIX);
        stringBuffer.append(msg);
        printStream.println(stringBuffer.toString());
        if (t != null) {
            t.printStackTrace();
        }
    }
}
