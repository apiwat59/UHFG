package org.apache.log4j.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Hashtable;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.config.PropertySetter;
import org.apache.log4j.helpers.FileWatchdog;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.spi.AppenderAttachable;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RendererSupport;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* loaded from: classes.dex */
public class DOMConfigurator implements Configurator {
    static final String ADDITIVITY_ATTR = "additivity";
    static final String APPENDER_REF_TAG = "appender-ref";
    static final String APPENDER_TAG = "appender";
    static final String CATEGORY = "category";
    static final String CATEGORY_FACTORY_TAG = "categoryFactory";
    static final String CLASS_ATTR = "class";
    static final String CONFIGURATION_TAG = "log4j:configuration";
    static final String CONFIG_DEBUG_ATTR = "configDebug";
    static final String EMPTY_STR = "";
    static final String ERROR_HANDLER_TAG = "errorHandler";
    static final String FILTER_TAG = "filter";
    static final String INTERNAL_DEBUG_ATTR = "debug";
    static final String LAYOUT_TAG = "layout";
    static final String LEVEL_TAG = "level";
    static final String LOGGER = "logger";
    static final String LOGGER_REF = "logger-ref";
    static final String NAME_ATTR = "name";
    static final String OLD_CONFIGURATION_TAG = "configuration";
    static final Class[] ONE_STRING_PARAM;
    static final String PARAM_TAG = "param";
    static final String PRIORITY_TAG = "priority";
    static final String REF_ATTR = "ref";
    static final String RENDERED_CLASS_ATTR = "renderedClass";
    static final String RENDERER_TAG = "renderer";
    static final String RENDERING_CLASS_ATTR = "renderingClass";
    static final String ROOT_REF = "root-ref";
    static final String ROOT_TAG = "root";
    static final String THRESHOLD_ATTR = "threshold";
    static final String VALUE_ATTR = "value";
    static /* synthetic */ Class class$java$lang$String = null;
    static /* synthetic */ Class class$org$apache$log4j$spi$ErrorHandler = null;
    static /* synthetic */ Class class$org$apache$log4j$spi$Filter = null;
    static /* synthetic */ Class class$org$apache$log4j$spi$LoggerFactory = null;
    static final String dbfKey = "javax.xml.parsers.DocumentBuilderFactory";
    Hashtable appenderBag = new Hashtable();
    Properties props;
    LoggerRepository repository;

    private interface ParseAction {
        Document parse(DocumentBuilder documentBuilder) throws SAXException, IOException;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Class[] clsArr = new Class[1];
        Class cls = class$java$lang$String;
        if (cls == null) {
            cls = class$("java.lang.String");
            class$java$lang$String = cls;
        }
        clsArr[0] = cls;
        ONE_STRING_PARAM = clsArr;
    }

    protected Appender findAppenderByName(Document doc, String appenderName) {
        Appender appender = (Appender) this.appenderBag.get(appenderName);
        if (appender != null) {
            return appender;
        }
        Element element = null;
        NodeList list = doc.getElementsByTagName(APPENDER_TAG);
        int t = 0;
        while (true) {
            if (t >= list.getLength()) {
                break;
            }
            Node node = list.item(t);
            NamedNodeMap map = node.getAttributes();
            Node attrNode = map.getNamedItem(NAME_ATTR);
            if (!appenderName.equals(attrNode.getNodeValue())) {
                t++;
            } else {
                element = (Element) node;
                break;
            }
        }
        if (element == null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("No appender named [");
            stringBuffer.append(appenderName);
            stringBuffer.append("] could be found.");
            LogLog.error(stringBuffer.toString());
            return null;
        }
        Appender appender2 = parseAppender(element);
        this.appenderBag.put(appenderName, appender2);
        return appender2;
    }

    protected Appender findAppenderByReference(Element appenderRef) {
        String appenderName = subst(appenderRef.getAttribute(REF_ATTR));
        Document doc = appenderRef.getOwnerDocument();
        return findAppenderByName(doc, appenderName);
    }

    protected Appender parseAppender(Element appenderElement) {
        String className = subst(appenderElement.getAttribute(CLASS_ATTR));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Class name: [");
        stringBuffer.append(className);
        stringBuffer.append(']');
        LogLog.debug(stringBuffer.toString());
        try {
            Object instance = Loader.loadClass(className).newInstance();
            Appender appender = (Appender) instance;
            PropertySetter propSetter = new PropertySetter(appender);
            appender.setName(subst(appenderElement.getAttribute(NAME_ATTR)));
            NodeList children = appenderElement.getChildNodes();
            int length = children.getLength();
            for (int loop = 0; loop < length; loop++) {
                Node currentNode = children.item(loop);
                if (currentNode.getNodeType() == 1) {
                    Element currentElement = (Element) currentNode;
                    if (currentElement.getTagName().equals(PARAM_TAG)) {
                        setParameter(currentElement, propSetter);
                    } else if (currentElement.getTagName().equals(LAYOUT_TAG)) {
                        appender.setLayout(parseLayout(currentElement));
                    } else if (currentElement.getTagName().equals(FILTER_TAG)) {
                        parseFilters(currentElement, appender);
                    } else if (currentElement.getTagName().equals(ERROR_HANDLER_TAG)) {
                        parseErrorHandler(currentElement, appender);
                    } else if (currentElement.getTagName().equals(APPENDER_REF_TAG)) {
                        String refName = subst(currentElement.getAttribute(REF_ATTR));
                        if (appender instanceof AppenderAttachable) {
                            AppenderAttachable aa = (AppenderAttachable) appender;
                            StringBuffer stringBuffer2 = new StringBuffer();
                            stringBuffer2.append("Attaching appender named [");
                            stringBuffer2.append(refName);
                            stringBuffer2.append("] to appender named [");
                            stringBuffer2.append(appender.getName());
                            stringBuffer2.append("].");
                            LogLog.debug(stringBuffer2.toString());
                            aa.addAppender(findAppenderByReference(currentElement));
                        } else {
                            StringBuffer stringBuffer3 = new StringBuffer();
                            stringBuffer3.append("Requesting attachment of appender named [");
                            stringBuffer3.append(refName);
                            stringBuffer3.append("] to appender named [");
                            stringBuffer3.append(appender.getName());
                            stringBuffer3.append("] which does not implement org.apache.log4j.spi.AppenderAttachable.");
                            LogLog.error(stringBuffer3.toString());
                        }
                    }
                }
            }
            propSetter.activate();
            return appender;
        } catch (Exception oops) {
            LogLog.error("Could not create an Appender. Reported error follows.", oops);
            return null;
        }
    }

    protected void parseErrorHandler(Element element, Appender appender) {
        String subst = subst(element.getAttribute(CLASS_ATTR));
        Class cls = class$org$apache$log4j$spi$ErrorHandler;
        if (cls == null) {
            cls = class$("org.apache.log4j.spi.ErrorHandler");
            class$org$apache$log4j$spi$ErrorHandler = cls;
        }
        ErrorHandler eh = (ErrorHandler) OptionConverter.instantiateByClassName(subst, cls, null);
        if (eh != null) {
            eh.setAppender(appender);
            PropertySetter propSetter = new PropertySetter(eh);
            NodeList children = element.getChildNodes();
            int length = children.getLength();
            for (int loop = 0; loop < length; loop++) {
                Node currentNode = children.item(loop);
                if (currentNode.getNodeType() == 1) {
                    Element currentElement = (Element) currentNode;
                    String tagName = currentElement.getTagName();
                    if (tagName.equals(PARAM_TAG)) {
                        setParameter(currentElement, propSetter);
                    } else if (tagName.equals(APPENDER_REF_TAG)) {
                        eh.setBackupAppender(findAppenderByReference(currentElement));
                    } else if (tagName.equals(LOGGER_REF)) {
                        String loggerName = currentElement.getAttribute(REF_ATTR);
                        Logger logger = this.repository.getLogger(loggerName);
                        eh.setLogger(logger);
                    } else if (tagName.equals(ROOT_REF)) {
                        Logger root = this.repository.getRootLogger();
                        eh.setLogger(root);
                    }
                }
            }
            propSetter.activate();
            appender.setErrorHandler(eh);
        }
    }

    protected void parseFilters(Element element, Appender appender) {
        String clazz = subst(element.getAttribute(CLASS_ATTR));
        Class cls = class$org$apache$log4j$spi$Filter;
        if (cls == null) {
            cls = class$("org.apache.log4j.spi.Filter");
            class$org$apache$log4j$spi$Filter = cls;
        }
        Filter filter = (Filter) OptionConverter.instantiateByClassName(clazz, cls, null);
        if (filter != null) {
            PropertySetter propSetter = new PropertySetter(filter);
            NodeList children = element.getChildNodes();
            int length = children.getLength();
            for (int loop = 0; loop < length; loop++) {
                Node currentNode = children.item(loop);
                if (currentNode.getNodeType() == 1) {
                    Element currentElement = (Element) currentNode;
                    String tagName = currentElement.getTagName();
                    if (tagName.equals(PARAM_TAG)) {
                        setParameter(currentElement, propSetter);
                    }
                }
            }
            propSetter.activate();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Adding filter of type [");
            stringBuffer.append(filter.getClass());
            stringBuffer.append("] to appender named [");
            stringBuffer.append(appender.getName());
            stringBuffer.append("].");
            LogLog.debug(stringBuffer.toString());
            appender.addFilter(filter);
        }
    }

    protected void parseCategory(Element loggerElement) {
        Logger cat;
        String catName = subst(loggerElement.getAttribute(NAME_ATTR));
        String className = subst(loggerElement.getAttribute(CLASS_ATTR));
        if ("".equals(className)) {
            LogLog.debug("Retreiving an instance of org.apache.log4j.Logger.");
            cat = this.repository.getLogger(catName);
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Desired logger sub-class: [");
            stringBuffer.append(className);
            stringBuffer.append(']');
            LogLog.debug(stringBuffer.toString());
            try {
                Class clazz = Loader.loadClass(className);
                Method getInstanceMethod = clazz.getMethod("getLogger", ONE_STRING_PARAM);
                cat = (Logger) getInstanceMethod.invoke(null, catName);
            } catch (Exception oops) {
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append("Could not retrieve category [");
                stringBuffer2.append(catName);
                stringBuffer2.append("]. Reported error follows.");
                LogLog.error(stringBuffer2.toString(), oops);
                return;
            }
        }
        synchronized (cat) {
            boolean additivity = OptionConverter.toBoolean(subst(loggerElement.getAttribute(ADDITIVITY_ATTR)), true);
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append("Setting [");
            stringBuffer3.append(cat.getName());
            stringBuffer3.append("] additivity to [");
            stringBuffer3.append(additivity);
            stringBuffer3.append("].");
            LogLog.debug(stringBuffer3.toString());
            cat.setAdditivity(additivity);
            parseChildrenOfLoggerElement(loggerElement, cat, false);
        }
    }

    protected void parseCategoryFactory(Element factoryElement) {
        String className = subst(factoryElement.getAttribute(CLASS_ATTR));
        if ("".equals(className)) {
            LogLog.error("Category Factory tag class attribute not found.");
            LogLog.debug("No Category Factory configured.");
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Desired category factory: [");
        stringBuffer.append(className);
        stringBuffer.append(']');
        LogLog.debug(stringBuffer.toString());
        Class cls = class$org$apache$log4j$spi$LoggerFactory;
        if (cls == null) {
            cls = class$("org.apache.log4j.spi.LoggerFactory");
            class$org$apache$log4j$spi$LoggerFactory = cls;
        }
        Object catFactory = OptionConverter.instantiateByClassName(className, cls, null);
        PropertySetter propSetter = new PropertySetter(catFactory);
        NodeList children = factoryElement.getChildNodes();
        int length = children.getLength();
        for (int loop = 0; loop < length; loop++) {
            Node currentNode = children.item(loop);
            if (currentNode.getNodeType() == 1) {
                Element currentElement = (Element) currentNode;
                if (currentElement.getTagName().equals(PARAM_TAG)) {
                    setParameter(currentElement, propSetter);
                }
            }
        }
    }

    protected void parseRoot(Element rootElement) {
        Logger root = this.repository.getRootLogger();
        synchronized (root) {
            parseChildrenOfLoggerElement(rootElement, root, true);
        }
    }

    protected void parseChildrenOfLoggerElement(Element catElement, Logger cat, boolean isRoot) {
        PropertySetter propSetter = new PropertySetter(cat);
        cat.removeAllAppenders();
        NodeList children = catElement.getChildNodes();
        int length = children.getLength();
        for (int loop = 0; loop < length; loop++) {
            Node currentNode = children.item(loop);
            if (currentNode.getNodeType() == 1) {
                Element currentElement = (Element) currentNode;
                String tagName = currentElement.getTagName();
                if (tagName.equals(APPENDER_REF_TAG)) {
                    Element appenderRef = (Element) currentNode;
                    Appender appender = findAppenderByReference(appenderRef);
                    String refName = subst(appenderRef.getAttribute(REF_ATTR));
                    if (appender != null) {
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("Adding appender named [");
                        stringBuffer.append(refName);
                        stringBuffer.append("] to category [");
                        stringBuffer.append(cat.getName());
                        stringBuffer.append("].");
                        LogLog.debug(stringBuffer.toString());
                    } else {
                        StringBuffer stringBuffer2 = new StringBuffer();
                        stringBuffer2.append("Appender named [");
                        stringBuffer2.append(refName);
                        stringBuffer2.append("] not found.");
                        LogLog.debug(stringBuffer2.toString());
                    }
                    cat.addAppender(appender);
                } else if (tagName.equals(LEVEL_TAG)) {
                    parseLevel(currentElement, cat, isRoot);
                } else if (tagName.equals(PRIORITY_TAG)) {
                    parseLevel(currentElement, cat, isRoot);
                } else if (tagName.equals(PARAM_TAG)) {
                    setParameter(currentElement, propSetter);
                }
            }
        }
        propSetter.activate();
    }

    protected Layout parseLayout(Element layout_element) {
        String className = subst(layout_element.getAttribute(CLASS_ATTR));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Parsing layout of class: \"");
        stringBuffer.append(className);
        stringBuffer.append("\"");
        LogLog.debug(stringBuffer.toString());
        try {
            Object instance = Loader.loadClass(className).newInstance();
            Layout layout = (Layout) instance;
            PropertySetter propSetter = new PropertySetter(layout);
            NodeList params = layout_element.getChildNodes();
            int length = params.getLength();
            for (int loop = 0; loop < length; loop++) {
                Node currentNode = params.item(loop);
                if (currentNode.getNodeType() == 1) {
                    Element currentElement = (Element) currentNode;
                    String tagName = currentElement.getTagName();
                    if (tagName.equals(PARAM_TAG)) {
                        setParameter(currentElement, propSetter);
                    }
                }
            }
            propSetter.activate();
            return layout;
        } catch (Exception oops) {
            LogLog.error("Could not create the Layout. Reported error follows.", oops);
            return null;
        }
    }

    protected void parseRenderer(Element element) {
        String renderingClass = subst(element.getAttribute(RENDERING_CLASS_ATTR));
        String renderedClass = subst(element.getAttribute(RENDERED_CLASS_ATTR));
        LoggerRepository loggerRepository = this.repository;
        if (loggerRepository instanceof RendererSupport) {
            RendererMap.addRenderer((RendererSupport) loggerRepository, renderedClass, renderingClass);
        }
    }

    protected void parseLevel(Element element, Logger logger, boolean isRoot) {
        String catName = logger.getName();
        if (isRoot) {
            catName = ROOT_TAG;
        }
        String priStr = subst(element.getAttribute(VALUE_ATTR));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Level value for ");
        stringBuffer.append(catName);
        stringBuffer.append(" is  [");
        stringBuffer.append(priStr);
        stringBuffer.append("].");
        LogLog.debug(stringBuffer.toString());
        if (Configurator.INHERITED.equalsIgnoreCase(priStr) || Configurator.NULL.equalsIgnoreCase(priStr)) {
            if (isRoot) {
                LogLog.error("Root level cannot be inherited. Ignoring directive.");
            } else {
                logger.setLevel(null);
            }
        } else {
            String className = subst(element.getAttribute(CLASS_ATTR));
            if ("".equals(className)) {
                logger.setLevel(OptionConverter.toLevel(priStr, Level.DEBUG));
            } else {
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append("Desired Level sub-class: [");
                stringBuffer2.append(className);
                stringBuffer2.append(']');
                LogLog.debug(stringBuffer2.toString());
                try {
                    Class clazz = Loader.loadClass(className);
                    Method toLevelMethod = clazz.getMethod("toLevel", ONE_STRING_PARAM);
                    Level pri = (Level) toLevelMethod.invoke(null, priStr);
                    logger.setLevel(pri);
                } catch (Exception oops) {
                    StringBuffer stringBuffer3 = new StringBuffer();
                    stringBuffer3.append("Could not create level [");
                    stringBuffer3.append(priStr);
                    stringBuffer3.append("]. Reported error follows.");
                    LogLog.error(stringBuffer3.toString(), oops);
                    return;
                }
            }
        }
        StringBuffer stringBuffer4 = new StringBuffer();
        stringBuffer4.append(catName);
        stringBuffer4.append(" level set to ");
        stringBuffer4.append(logger.getLevel());
        LogLog.debug(stringBuffer4.toString());
    }

    protected void setParameter(Element elem, PropertySetter propSetter) {
        String name = subst(elem.getAttribute(NAME_ATTR));
        String value = elem.getAttribute(VALUE_ATTR);
        propSetter.setProperty(name, subst(OptionConverter.convertSpecialChars(value)));
    }

    public static void configure(Element element) {
        DOMConfigurator configurator = new DOMConfigurator();
        configurator.doConfigure(element, LogManager.getLoggerRepository());
    }

    public static void configureAndWatch(String configFilename) {
        configureAndWatch(configFilename, FileWatchdog.DEFAULT_DELAY);
    }

    public static void configureAndWatch(String configFilename, long delay) {
        XMLWatchdog xdog = new XMLWatchdog(configFilename);
        xdog.setDelay(delay);
        xdog.start();
    }

    public void doConfigure(final String filename, LoggerRepository repository) {
        ParseAction action = new ParseAction() { // from class: org.apache.log4j.xml.DOMConfigurator.1
            @Override // org.apache.log4j.xml.DOMConfigurator.ParseAction
            public Document parse(DocumentBuilder parser) throws SAXException, IOException {
                return parser.parse(new File(filename));
            }

            public String toString() {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("file [");
                stringBuffer.append(filename);
                stringBuffer.append("]");
                return stringBuffer.toString();
            }
        };
        doConfigure(action, repository);
    }

    @Override // org.apache.log4j.spi.Configurator
    public void doConfigure(final URL url, LoggerRepository repository) {
        ParseAction action = new ParseAction() { // from class: org.apache.log4j.xml.DOMConfigurator.2
            @Override // org.apache.log4j.xml.DOMConfigurator.ParseAction
            public Document parse(DocumentBuilder parser) throws SAXException, IOException {
                return parser.parse(url.toString());
            }

            public String toString() {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("url [");
                stringBuffer.append(url.toString());
                stringBuffer.append("]");
                return stringBuffer.toString();
            }
        };
        doConfigure(action, repository);
    }

    public void doConfigure(final InputStream inputStream, LoggerRepository repository) throws FactoryConfigurationError {
        ParseAction action = new ParseAction() { // from class: org.apache.log4j.xml.DOMConfigurator.3
            @Override // org.apache.log4j.xml.DOMConfigurator.ParseAction
            public Document parse(DocumentBuilder parser) throws SAXException, IOException {
                InputSource inputSource = new InputSource(inputStream);
                inputSource.setSystemId("dummy://log4j.dtd");
                return parser.parse(inputSource);
            }

            public String toString() {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("input stream [");
                stringBuffer.append(inputStream.toString());
                stringBuffer.append("]");
                return stringBuffer.toString();
            }
        };
        doConfigure(action, repository);
    }

    public void doConfigure(final Reader reader, LoggerRepository repository) throws FactoryConfigurationError {
        ParseAction action = new ParseAction() { // from class: org.apache.log4j.xml.DOMConfigurator.4
            @Override // org.apache.log4j.xml.DOMConfigurator.ParseAction
            public Document parse(DocumentBuilder parser) throws SAXException, IOException {
                InputSource inputSource = new InputSource(reader);
                inputSource.setSystemId("dummy://log4j.dtd");
                return parser.parse(inputSource);
            }

            public String toString() {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("reader [");
                stringBuffer.append(reader.toString());
                stringBuffer.append("]");
                return stringBuffer.toString();
            }
        };
        doConfigure(action, repository);
    }

    protected void doConfigure(final InputSource inputSource, LoggerRepository repository) throws FactoryConfigurationError {
        if (inputSource.getSystemId() == null) {
            inputSource.setSystemId("dummy://log4j.dtd");
        }
        ParseAction action = new ParseAction() { // from class: org.apache.log4j.xml.DOMConfigurator.5
            @Override // org.apache.log4j.xml.DOMConfigurator.ParseAction
            public Document parse(DocumentBuilder parser) throws SAXException, IOException {
                return parser.parse(inputSource);
            }

            public String toString() {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("input source [");
                stringBuffer.append(inputSource.toString());
                stringBuffer.append("]");
                return stringBuffer.toString();
            }
        };
        doConfigure(action, repository);
    }

    private final void doConfigure(ParseAction action, LoggerRepository repository) throws FactoryConfigurationError {
        this.repository = repository;
        try {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("System property is :");
            stringBuffer.append(OptionConverter.getSystemProperty(dbfKey, null));
            LogLog.debug(stringBuffer.toString());
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            LogLog.debug("Standard DocumentBuilderFactory search succeded.");
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("DocumentBuilderFactory is: ");
            stringBuffer2.append(dbf.getClass().getName());
            LogLog.debug(stringBuffer2.toString());
            try {
                dbf.setValidating(true);
                DocumentBuilder docBuilder = dbf.newDocumentBuilder();
                docBuilder.setErrorHandler(new SAXErrorHandler());
                docBuilder.setEntityResolver(new Log4jEntityResolver());
                Document doc = action.parse(docBuilder);
                parse(doc.getDocumentElement());
            } catch (Exception e) {
                StringBuffer stringBuffer3 = new StringBuffer();
                stringBuffer3.append("Could not parse ");
                stringBuffer3.append(action.toString());
                stringBuffer3.append(".");
                LogLog.error(stringBuffer3.toString(), e);
            }
        } catch (FactoryConfigurationError fce) {
            Exception e2 = fce.getException();
            LogLog.debug("Could not instantiate a DocumentBuilderFactory.", e2);
            throw fce;
        }
    }

    public void doConfigure(Element element, LoggerRepository repository) {
        this.repository = repository;
        parse(element);
    }

    public static void configure(String filename) throws FactoryConfigurationError {
        new DOMConfigurator().doConfigure(filename, LogManager.getLoggerRepository());
    }

    public static void configure(URL url) throws FactoryConfigurationError {
        new DOMConfigurator().doConfigure(url, LogManager.getLoggerRepository());
    }

    protected void parse(Element element) {
        String rootElementName = element.getTagName();
        if (!rootElementName.equals(CONFIGURATION_TAG)) {
            if (rootElementName.equals(OLD_CONFIGURATION_TAG)) {
                LogLog.warn("The <configuration> element has been deprecated.");
                LogLog.warn("Use the <log4j:configuration> element instead.");
            } else {
                LogLog.error("DOM element is - not a <log4j:configuration> element.");
                return;
            }
        }
        String debugAttrib = subst(element.getAttribute("debug"));
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("debug attribute= \"");
        stringBuffer.append(debugAttrib);
        stringBuffer.append("\".");
        LogLog.debug(stringBuffer.toString());
        if (!debugAttrib.equals("") && !debugAttrib.equals(Configurator.NULL)) {
            LogLog.setInternalDebugging(OptionConverter.toBoolean(debugAttrib, true));
        } else {
            LogLog.debug("Ignoring debug attribute.");
        }
        String confDebug = subst(element.getAttribute(CONFIG_DEBUG_ATTR));
        if (!confDebug.equals("") && !confDebug.equals(Configurator.NULL)) {
            LogLog.warn("The \"configDebug\" attribute is deprecated.");
            LogLog.warn("Use the \"debug\" attribute instead.");
            LogLog.setInternalDebugging(OptionConverter.toBoolean(confDebug, true));
        }
        String thresholdStr = subst(element.getAttribute(THRESHOLD_ATTR));
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append("Threshold =\"");
        stringBuffer2.append(thresholdStr);
        stringBuffer2.append("\".");
        LogLog.debug(stringBuffer2.toString());
        if (!"".equals(thresholdStr) && !Configurator.NULL.equals(thresholdStr)) {
            this.repository.setThreshold(thresholdStr);
        }
        NodeList children = element.getChildNodes();
        int length = children.getLength();
        for (int loop = 0; loop < length; loop++) {
            Node currentNode = children.item(loop);
            if (currentNode.getNodeType() == 1) {
                Element currentElement = (Element) currentNode;
                if (currentElement.getTagName().equals(CATEGORY_FACTORY_TAG)) {
                    parseCategoryFactory(currentElement);
                }
            }
        }
        for (int loop2 = 0; loop2 < length; loop2++) {
            Node currentNode2 = children.item(loop2);
            if (currentNode2.getNodeType() == 1) {
                Element currentElement2 = (Element) currentNode2;
                String tagName = currentElement2.getTagName();
                if (tagName.equals(CATEGORY) || tagName.equals(LOGGER)) {
                    parseCategory(currentElement2);
                } else if (tagName.equals(ROOT_TAG)) {
                    parseRoot(currentElement2);
                } else if (tagName.equals(RENDERER_TAG)) {
                    parseRenderer(currentElement2);
                }
            }
        }
    }

    protected String subst(String value) {
        try {
            return OptionConverter.substVars(value, this.props);
        } catch (IllegalArgumentException e) {
            LogLog.warn("Could not perform variable substitution.", e);
            return value;
        }
    }
}
