package com.apkfuns.logutils;

/* loaded from: classes.dex */
public final class LogUtils {
    private static Logger printer = new Logger();
    private static LogConfigImpl logConfig = LogConfigImpl.getInstance();
    private static Log2FileConfigImpl log2FileConfig = Log2FileConfigImpl.getInstance();

    public static LogConfig getLogConfig() {
        return logConfig;
    }

    public static Log2FileConfig getLog2FileConfig() {
        return log2FileConfig;
    }

    public static Printer tag(String tag) {
        return printer.setTag(tag);
    }

    public static void v(String msg, Object... args) {
        printer.v(msg, args);
    }

    public static void v(Object object) {
        printer.v(object);
    }

    public static void d(String msg, Object... args) {
        printer.d(msg, args);
    }

    public static void d(Object object) {
        printer.d(object);
    }

    public static void i(String msg, Object... args) {
        printer.i(msg, args);
    }

    public static void i(Object object) {
        printer.i(object);
    }

    public static void w(String msg, Object... args) {
        printer.w(msg, args);
    }

    public static void w(Object object) {
        printer.w(object);
    }

    public static void e(String msg, Object... args) {
        printer.e(msg, args);
    }

    public static void e(Object object) {
        printer.e(object);
    }

    public static void wtf(String msg, Object... args) {
        printer.wtf(msg, args);
    }

    public static void wtf(Object object) {
        printer.wtf(object);
    }

    public static void json(String json) {
        printer.json(json);
    }

    public static void xml(String xml) {
        printer.xml(xml);
    }
}
