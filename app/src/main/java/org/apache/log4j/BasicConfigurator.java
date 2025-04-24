package org.apache.log4j;

/* loaded from: classes.dex */
public class BasicConfigurator {
    protected BasicConfigurator() {
    }

    public static void configure() {
        Logger root = Logger.getRootLogger();
        root.addAppender(new ConsoleAppender(new PatternLayout(PatternLayout.TTCC_CONVERSION_PATTERN)));
    }

    public static void configure(Appender appender) {
        Logger root = Logger.getRootLogger();
        root.addAppender(appender);
    }

    public static void resetConfiguration() {
        LogManager.resetConfiguration();
    }
}
