package org.apache.log4j;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.DefaultRepositorySelector;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.RootLogger;

/* loaded from: classes.dex */
public class LogManager {
    public static final String CONFIGURATOR_CLASS_KEY = "log4j.configuratorClass";
    public static final String DEFAULT_CONFIGURATION_FILE = "log4j.properties";
    public static final String DEFAULT_CONFIGURATION_KEY = "log4j.configuration";
    public static final String DEFAULT_INIT_OVERRIDE_KEY = "log4j.defaultInitOverride";
    static final String DEFAULT_XML_CONFIGURATION_FILE = "log4j.xml";
    private static Object guard = null;
    private static RepositorySelector repositorySelector;

    static {
        URL url;
        Hierarchy h = new Hierarchy(new RootLogger(Level.DEBUG));
        repositorySelector = new DefaultRepositorySelector(h);
        String override = OptionConverter.getSystemProperty(DEFAULT_INIT_OVERRIDE_KEY, null);
        if (override == null || "false".equalsIgnoreCase(override)) {
            String configurationOptionStr = OptionConverter.getSystemProperty(DEFAULT_CONFIGURATION_KEY, null);
            String configuratorClassName = OptionConverter.getSystemProperty(CONFIGURATOR_CLASS_KEY, null);
            if (configurationOptionStr == null) {
                url = Loader.getResource(DEFAULT_XML_CONFIGURATION_FILE);
                if (url == null) {
                    url = Loader.getResource(DEFAULT_CONFIGURATION_FILE);
                }
            } else {
                try {
                    url = new URL(configurationOptionStr);
                } catch (MalformedURLException e) {
                    url = Loader.getResource(configurationOptionStr);
                }
            }
            if (url != null) {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("Using URL [");
                stringBuffer.append(url);
                stringBuffer.append("] for automatic log4j configuration.");
                LogLog.debug(stringBuffer.toString());
                OptionConverter.selectAndConfigure(url, configuratorClassName, getLoggerRepository());
                return;
            }
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("Could not find resource: [");
            stringBuffer2.append(configurationOptionStr);
            stringBuffer2.append("].");
            LogLog.debug(stringBuffer2.toString());
        }
    }

    public static void setRepositorySelector(RepositorySelector selector, Object guard2) throws IllegalArgumentException {
        Object obj = guard;
        if (obj != null && obj != guard2) {
            throw new IllegalArgumentException("Attempted to reset the LoggerFactory without possessing the guard.");
        }
        if (selector == null) {
            throw new IllegalArgumentException("RepositorySelector must be non-null.");
        }
        guard = guard2;
        repositorySelector = selector;
    }

    public static LoggerRepository getLoggerRepository() {
        return repositorySelector.getLoggerRepository();
    }

    public static Logger getRootLogger() {
        return repositorySelector.getLoggerRepository().getRootLogger();
    }

    public static Logger getLogger(String name) {
        return repositorySelector.getLoggerRepository().getLogger(name);
    }

    public static Logger getLogger(Class clazz) {
        return repositorySelector.getLoggerRepository().getLogger(clazz.getName());
    }

    public static Logger getLogger(String name, LoggerFactory factory) {
        return repositorySelector.getLoggerRepository().getLogger(name, factory);
    }

    public static Logger exists(String name) {
        return repositorySelector.getLoggerRepository().exists(name);
    }

    public static Enumeration getCurrentLoggers() {
        return repositorySelector.getLoggerRepository().getCurrentLoggers();
    }

    public static void shutdown() {
        repositorySelector.getLoggerRepository().shutdown();
    }

    public static void resetConfiguration() {
        repositorySelector.getLoggerRepository().resetConfiguration();
    }
}
