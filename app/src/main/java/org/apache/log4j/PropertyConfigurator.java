package org.apache.log4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.helpers.FileWatchdog;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.OptionHandler;
import org.apache.log4j.spi.RendererSupport;

/* loaded from: classes.dex */
public class PropertyConfigurator implements Configurator {
    static final String ADDITIVITY_PREFIX = "log4j.additivity.";
    static final String APPENDER_PREFIX = "log4j.appender.";
    static final String CATEGORY_PREFIX = "log4j.category.";
    static final String FACTORY_PREFIX = "log4j.factory";
    private static final String INTERNAL_ROOT_NAME = "root";
    public static final String LOGGER_FACTORY_KEY = "log4j.loggerFactory";
    static final String LOGGER_PREFIX = "log4j.logger.";
    static final String RENDERER_PREFIX = "log4j.renderer.";
    static final String ROOT_CATEGORY_PREFIX = "log4j.rootCategory";
    static final String ROOT_LOGGER_PREFIX = "log4j.rootLogger";
    static final String THRESHOLD_PREFIX = "log4j.threshold";
    static /* synthetic */ Class class$org$apache$log4j$Appender;
    static /* synthetic */ Class class$org$apache$log4j$Layout;
    static /* synthetic */ Class class$org$apache$log4j$spi$LoggerFactory;
    protected Hashtable registry = new Hashtable(11);
    protected LoggerFactory loggerFactory = new DefaultCategoryFactory();

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public void doConfigure(String configFileName, LoggerRepository hierarchy) {
        Properties props = new Properties();
        try {
            FileInputStream istream = new FileInputStream(configFileName);
            props.load(istream);
            istream.close();
            doConfigure(props, hierarchy);
        } catch (IOException e) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Could not read configuration file [");
            stringBuffer.append(configFileName);
            stringBuffer.append("].");
            LogLog.error(stringBuffer.toString(), e);
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("Ignoring configuration file [");
            stringBuffer2.append(configFileName);
            stringBuffer2.append("].");
            LogLog.error(stringBuffer2.toString());
        }
    }

    public static void configure(String configFilename) {
        new PropertyConfigurator().doConfigure(configFilename, LogManager.getLoggerRepository());
    }

    public static void configure(URL configURL) {
        new PropertyConfigurator().doConfigure(configURL, LogManager.getLoggerRepository());
    }

    public static void configure(Properties properties) {
        new PropertyConfigurator().doConfigure(properties, LogManager.getLoggerRepository());
    }

    public static void configureAndWatch(String configFilename) {
        configureAndWatch(configFilename, FileWatchdog.DEFAULT_DELAY);
    }

    public static void configureAndWatch(String configFilename, long delay) {
        PropertyWatchdog pdog = new PropertyWatchdog(configFilename);
        pdog.setDelay(delay);
        pdog.start();
    }

    public void doConfigure(Properties properties, LoggerRepository hierarchy) {
        String value = properties.getProperty(LogLog.DEBUG_KEY);
        if (value == null && (value = properties.getProperty(LogLog.CONFIG_DEBUG_KEY)) != null) {
            LogLog.warn("[log4j.configDebug] is deprecated. Use [log4j.debug] instead.");
        }
        if (value != null) {
            LogLog.setInternalDebugging(OptionConverter.toBoolean(value, true));
        }
        String thresholdStr = OptionConverter.findAndSubst(THRESHOLD_PREFIX, properties);
        if (thresholdStr != null) {
            hierarchy.setThreshold(OptionConverter.toLevel(thresholdStr, Level.ALL));
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Hierarchy threshold set to [");
            stringBuffer.append(hierarchy.getThreshold());
            stringBuffer.append("].");
            LogLog.debug(stringBuffer.toString());
        }
        configureRootCategory(properties, hierarchy);
        configureLoggerFactory(properties);
        parseCatsAndRenderers(properties, hierarchy);
        LogLog.debug("Finished configuring.");
        this.registry.clear();
    }

    @Override // org.apache.log4j.spi.Configurator
    public void doConfigure(URL configURL, LoggerRepository hierarchy) {
        Properties props = new Properties();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Reading configuration from URL ");
        stringBuffer.append(configURL);
        LogLog.debug(stringBuffer.toString());
        try {
            props.load(configURL.openStream());
            doConfigure(props, hierarchy);
        } catch (IOException e) {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("Could not read configuration file from URL [");
            stringBuffer2.append(configURL);
            stringBuffer2.append("].");
            LogLog.error(stringBuffer2.toString(), e);
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append("Ignoring configuration file [");
            stringBuffer3.append(configURL);
            stringBuffer3.append("].");
            LogLog.error(stringBuffer3.toString());
        }
    }

    protected void configureLoggerFactory(Properties props) {
        String factoryClassName = OptionConverter.findAndSubst(LOGGER_FACTORY_KEY, props);
        if (factoryClassName != null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Setting category factory to [");
            stringBuffer.append(factoryClassName);
            stringBuffer.append("].");
            LogLog.debug(stringBuffer.toString());
            Class cls = class$org$apache$log4j$spi$LoggerFactory;
            if (cls == null) {
                cls = class$("org.apache.log4j.spi.LoggerFactory");
                class$org$apache$log4j$spi$LoggerFactory = cls;
            }
            LoggerFactory loggerFactory = (LoggerFactory) OptionConverter.instantiateByClassName(factoryClassName, cls, this.loggerFactory);
            this.loggerFactory = loggerFactory;
            PropertySetter.setProperties(loggerFactory, props, "log4j.factory.");
        }
    }

    void configureRootCategory(Properties props, LoggerRepository hierarchy) {
        String effectiveFrefix = ROOT_LOGGER_PREFIX;
        String value = OptionConverter.findAndSubst(ROOT_LOGGER_PREFIX, props);
        if (value == null) {
            value = OptionConverter.findAndSubst(ROOT_CATEGORY_PREFIX, props);
            effectiveFrefix = ROOT_CATEGORY_PREFIX;
        }
        if (value == null) {
            LogLog.debug("Could not find root logger information. Is this OK?");
            return;
        }
        Logger root = hierarchy.getRootLogger();
        synchronized (root) {
            parseCategory(props, root, effectiveFrefix, INTERNAL_ROOT_NAME, value);
        }
    }

    protected void parseCatsAndRenderers(Properties props, LoggerRepository hierarchy) {
        String loggerName;
        Enumeration enumeration = props.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            if (key.startsWith(CATEGORY_PREFIX) || key.startsWith(LOGGER_PREFIX)) {
                if (key.startsWith(CATEGORY_PREFIX)) {
                    String loggerName2 = key.substring(CATEGORY_PREFIX.length());
                    loggerName = loggerName2;
                } else if (!key.startsWith(LOGGER_PREFIX)) {
                    loggerName = null;
                } else {
                    String loggerName3 = key.substring(LOGGER_PREFIX.length());
                    loggerName = loggerName3;
                }
                String value = OptionConverter.findAndSubst(key, props);
                Logger logger = hierarchy.getLogger(loggerName, this.loggerFactory);
                synchronized (logger) {
                    parseCategory(props, logger, key, loggerName, value);
                    parseAdditivityForLogger(props, logger, loggerName);
                }
            } else if (key.startsWith(RENDERER_PREFIX)) {
                String renderedClass = key.substring(RENDERER_PREFIX.length());
                String renderingClass = OptionConverter.findAndSubst(key, props);
                if (hierarchy instanceof RendererSupport) {
                    RendererMap.addRenderer((RendererSupport) hierarchy, renderedClass, renderingClass);
                }
            }
        }
    }

    void parseAdditivityForLogger(Properties props, Logger cat, String loggerName) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(ADDITIVITY_PREFIX);
        stringBuffer.append(loggerName);
        String value = OptionConverter.findAndSubst(stringBuffer.toString(), props);
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append("Handling log4j.additivity.");
        stringBuffer2.append(loggerName);
        stringBuffer2.append("=[");
        stringBuffer2.append(value);
        stringBuffer2.append("]");
        LogLog.debug(stringBuffer2.toString());
        if (value != null && !value.equals("")) {
            boolean additivity = OptionConverter.toBoolean(value, true);
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append("Setting additivity for \"");
            stringBuffer3.append(loggerName);
            stringBuffer3.append("\" to ");
            stringBuffer3.append(additivity);
            LogLog.debug(stringBuffer3.toString());
            cat.setAdditivity(additivity);
        }
    }

    void parseCategory(Properties props, Logger logger, String optionKey, String loggerName, String value) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Parsing for [");
        stringBuffer.append(loggerName);
        stringBuffer.append("] with value=[");
        stringBuffer.append(value);
        stringBuffer.append("].");
        LogLog.debug(stringBuffer.toString());
        StringTokenizer st = new StringTokenizer(value, ",");
        if (!value.startsWith(",") && !value.equals("")) {
            if (!st.hasMoreTokens()) {
                return;
            }
            String levelStr = st.nextToken();
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("Level token is [");
            stringBuffer2.append(levelStr);
            stringBuffer2.append("].");
            LogLog.debug(stringBuffer2.toString());
            if (Configurator.INHERITED.equalsIgnoreCase(levelStr) || Configurator.NULL.equalsIgnoreCase(levelStr)) {
                if (loggerName.equals(INTERNAL_ROOT_NAME)) {
                    LogLog.warn("The root logger cannot be set to null.");
                } else {
                    logger.setLevel(null);
                }
            } else {
                logger.setLevel(OptionConverter.toLevel(levelStr, Level.DEBUG));
            }
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append("Category ");
            stringBuffer3.append(loggerName);
            stringBuffer3.append(" set to ");
            stringBuffer3.append(logger.getLevel());
            LogLog.debug(stringBuffer3.toString());
        }
        logger.removeAllAppenders();
        while (st.hasMoreTokens()) {
            String appenderName = st.nextToken().trim();
            if (appenderName != null && !appenderName.equals(",")) {
                StringBuffer stringBuffer4 = new StringBuffer();
                stringBuffer4.append("Parsing appender named \"");
                stringBuffer4.append(appenderName);
                stringBuffer4.append("\".");
                LogLog.debug(stringBuffer4.toString());
                Appender appender = parseAppender(props, appenderName);
                if (appender != null) {
                    logger.addAppender(appender);
                }
            }
        }
    }

    Appender parseAppender(Properties props, String appenderName) {
        Appender appender = registryGet(appenderName);
        if (appender != null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Appender \"");
            stringBuffer.append(appenderName);
            stringBuffer.append("\" was already parsed.");
            LogLog.debug(stringBuffer.toString());
            return appender;
        }
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append(APPENDER_PREFIX);
        stringBuffer2.append(appenderName);
        String prefix = stringBuffer2.toString();
        StringBuffer stringBuffer3 = new StringBuffer();
        stringBuffer3.append(prefix);
        stringBuffer3.append(".layout");
        String layoutPrefix = stringBuffer3.toString();
        Class cls = class$org$apache$log4j$Appender;
        if (cls == null) {
            cls = class$("org.apache.log4j.Appender");
            class$org$apache$log4j$Appender = cls;
        }
        Appender appender2 = (Appender) OptionConverter.instantiateByKey(props, prefix, cls, null);
        if (appender2 == null) {
            StringBuffer stringBuffer4 = new StringBuffer();
            stringBuffer4.append("Could not instantiate appender named \"");
            stringBuffer4.append(appenderName);
            stringBuffer4.append("\".");
            LogLog.error(stringBuffer4.toString());
            return null;
        }
        appender2.setName(appenderName);
        if (appender2 instanceof OptionHandler) {
            if (appender2.requiresLayout()) {
                Class cls2 = class$org$apache$log4j$Layout;
                if (cls2 == null) {
                    cls2 = class$("org.apache.log4j.Layout");
                    class$org$apache$log4j$Layout = cls2;
                }
                Layout layout = (Layout) OptionConverter.instantiateByKey(props, layoutPrefix, cls2, null);
                if (layout != null) {
                    appender2.setLayout(layout);
                    StringBuffer stringBuffer5 = new StringBuffer();
                    stringBuffer5.append("Parsing layout options for \"");
                    stringBuffer5.append(appenderName);
                    stringBuffer5.append("\".");
                    LogLog.debug(stringBuffer5.toString());
                    StringBuffer stringBuffer6 = new StringBuffer();
                    stringBuffer6.append(layoutPrefix);
                    stringBuffer6.append(".");
                    PropertySetter.setProperties(layout, props, stringBuffer6.toString());
                    StringBuffer stringBuffer7 = new StringBuffer();
                    stringBuffer7.append("End of parsing for \"");
                    stringBuffer7.append(appenderName);
                    stringBuffer7.append("\".");
                    LogLog.debug(stringBuffer7.toString());
                }
            }
            StringBuffer stringBuffer8 = new StringBuffer();
            stringBuffer8.append(prefix);
            stringBuffer8.append(".");
            PropertySetter.setProperties(appender2, props, stringBuffer8.toString());
            StringBuffer stringBuffer9 = new StringBuffer();
            stringBuffer9.append("Parsed \"");
            stringBuffer9.append(appenderName);
            stringBuffer9.append("\" options.");
            LogLog.debug(stringBuffer9.toString());
        }
        registryPut(appender2);
        return appender2;
    }

    void registryPut(Appender appender) {
        this.registry.put(appender.getName(), appender);
    }

    Appender registryGet(String name) {
        return (Appender) this.registry.get(name);
    }
}
