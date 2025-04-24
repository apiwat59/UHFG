package jxl.common.log;

import jxl.common.Logger;

/* loaded from: classes.dex */
public class SimpleLogger extends Logger {
    private boolean suppressWarnings = false;

    @Override // jxl.common.Logger
    public void debug(Object message) {
        if (!this.suppressWarnings) {
            System.out.print("Debug: ");
            System.out.println(message);
        }
    }

    @Override // jxl.common.Logger
    public void debug(Object message, Throwable t) {
        if (!this.suppressWarnings) {
            System.out.print("Debug: ");
            System.out.println(message);
            t.printStackTrace();
        }
    }

    @Override // jxl.common.Logger
    public void error(Object message) {
        System.err.print("Error: ");
        System.err.println(message);
    }

    @Override // jxl.common.Logger
    public void error(Object message, Throwable t) {
        System.err.print("Error: ");
        System.err.println(message);
        t.printStackTrace();
    }

    @Override // jxl.common.Logger
    public void fatal(Object message) {
        System.err.print("Fatal: ");
        System.err.println(message);
    }

    @Override // jxl.common.Logger
    public void fatal(Object message, Throwable t) {
        System.err.print("Fatal:  ");
        System.err.println(message);
        t.printStackTrace();
    }

    @Override // jxl.common.Logger
    public void info(Object message) {
        if (!this.suppressWarnings) {
            System.out.println(message);
        }
    }

    @Override // jxl.common.Logger
    public void info(Object message, Throwable t) {
        if (!this.suppressWarnings) {
            System.out.println(message);
            t.printStackTrace();
        }
    }

    @Override // jxl.common.Logger
    public void warn(Object message) {
        if (!this.suppressWarnings) {
            System.err.print("Warning:  ");
            System.err.println(message);
        }
    }

    @Override // jxl.common.Logger
    public void warn(Object message, Throwable t) {
        if (!this.suppressWarnings) {
            System.err.print("Warning:  ");
            System.err.println(message);
            t.printStackTrace();
        }
    }

    @Override // jxl.common.Logger
    protected Logger getLoggerImpl(Class c) {
        return this;
    }

    @Override // jxl.common.Logger
    public void setSuppressWarnings(boolean w) {
        this.suppressWarnings = w;
    }
}
