package jxl.common;

import java.security.AccessControlException;
import jxl.common.log.LoggerName;
import jxl.common.log.SimpleLogger;

/* loaded from: classes.dex */
public abstract class Logger {
    private static Logger logger = null;

    public abstract void debug(Object obj);

    public abstract void debug(Object obj, Throwable th);

    public abstract void error(Object obj);

    public abstract void error(Object obj, Throwable th);

    public abstract void fatal(Object obj);

    public abstract void fatal(Object obj, Throwable th);

    protected abstract Logger getLoggerImpl(Class cls);

    public abstract void info(Object obj);

    public abstract void info(Object obj, Throwable th);

    public abstract void warn(Object obj);

    public abstract void warn(Object obj, Throwable th);

    public static final Logger getLogger(Class cl) {
        if (logger == null) {
            initializeLogger();
        }
        return logger.getLoggerImpl(cl);
    }

    private static synchronized void initializeLogger() {
        synchronized (Logger.class) {
            if (logger != null) {
                return;
            }
            String loggerName = LoggerName.NAME;
            try {
                try {
                    try {
                        try {
                            loggerName = System.getProperty("logger");
                            if (loggerName == null) {
                                loggerName = LoggerName.NAME;
                            }
                            logger = (Logger) Class.forName(loggerName).newInstance();
                        } catch (InstantiationException e) {
                            SimpleLogger simpleLogger = new SimpleLogger();
                            logger = simpleLogger;
                            simpleLogger.warn("Could not instantiate logger " + loggerName + " using default");
                        }
                    } catch (IllegalAccessException e2) {
                        SimpleLogger simpleLogger2 = new SimpleLogger();
                        logger = simpleLogger2;
                        simpleLogger2.warn("Could not instantiate logger " + loggerName + " using default");
                    }
                } catch (ClassNotFoundException e3) {
                    SimpleLogger simpleLogger3 = new SimpleLogger();
                    logger = simpleLogger3;
                    simpleLogger3.warn("Could not instantiate logger " + loggerName + " using default");
                }
            } catch (AccessControlException e4) {
                SimpleLogger simpleLogger4 = new SimpleLogger();
                logger = simpleLogger4;
                simpleLogger4.warn("Could not instantiate logger " + loggerName + " using default");
            }
        }
    }

    protected Logger() {
    }

    public void setSuppressWarnings(boolean w) {
    }
}
