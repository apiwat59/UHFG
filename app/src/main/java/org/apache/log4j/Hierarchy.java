package org.apache.log4j;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RendererSupport;

/* loaded from: classes.dex */
public class Hierarchy implements LoggerRepository, RendererSupport {
    private LoggerFactory defaultFactory;
    boolean emittedNoAppenderWarning = false;
    boolean emittedNoResourceBundleWarning = false;
    Hashtable ht = new Hashtable();
    private Vector listeners = new Vector(1);
    RendererMap rendererMap;
    Logger root;
    Level threshold;
    int thresholdInt;

    public Hierarchy(Logger root) {
        this.root = root;
        setThreshold(Level.ALL);
        this.root.setHierarchy(this);
        this.rendererMap = new RendererMap();
        this.defaultFactory = new DefaultCategoryFactory();
    }

    public void addRenderer(Class classToRender, ObjectRenderer or) {
        this.rendererMap.put(classToRender, or);
    }

    @Override // org.apache.log4j.spi.LoggerRepository
    public void addHierarchyEventListener(HierarchyEventListener listener) {
        if (this.listeners.contains(listener)) {
            LogLog.warn("Ignoring attempt to add an existent listener.");
        } else {
            this.listeners.addElement(listener);
        }
    }

    public void clear() {
        this.ht.clear();
    }

    @Override // org.apache.log4j.spi.LoggerRepository
    public void emitNoAppenderWarning(Category cat) {
        if (!this.emittedNoAppenderWarning) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("No appenders could be found for logger (");
            stringBuffer.append(cat.getName());
            stringBuffer.append(").");
            LogLog.warn(stringBuffer.toString());
            LogLog.warn("Please initialize the log4j system properly.");
            this.emittedNoAppenderWarning = true;
        }
    }

    @Override // org.apache.log4j.spi.LoggerRepository
    public Logger exists(String name) {
        Object o = this.ht.get(new CategoryKey(name));
        if (o instanceof Logger) {
            return (Logger) o;
        }
        return null;
    }

    @Override // org.apache.log4j.spi.LoggerRepository
    public void setThreshold(String levelStr) {
        Level l = Level.toLevel(levelStr, (Level) null);
        if (l != null) {
            setThreshold(l);
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Could not convert [");
        stringBuffer.append(levelStr);
        stringBuffer.append("] to Level.");
        LogLog.warn(stringBuffer.toString());
    }

    @Override // org.apache.log4j.spi.LoggerRepository
    public void setThreshold(Level l) {
        if (l != null) {
            this.thresholdInt = l.level;
            this.threshold = l;
        }
    }

    @Override // org.apache.log4j.spi.LoggerRepository
    public void fireAddAppenderEvent(Category logger, Appender appender) {
        Vector vector = this.listeners;
        if (vector != null) {
            int size = vector.size();
            for (int i = 0; i < size; i++) {
                HierarchyEventListener listener = (HierarchyEventListener) this.listeners.elementAt(i);
                listener.addAppenderEvent(logger, appender);
            }
        }
    }

    void fireRemoveAppenderEvent(Category logger, Appender appender) {
        Vector vector = this.listeners;
        if (vector != null) {
            int size = vector.size();
            for (int i = 0; i < size; i++) {
                HierarchyEventListener listener = (HierarchyEventListener) this.listeners.elementAt(i);
                listener.removeAppenderEvent(logger, appender);
            }
        }
    }

    @Override // org.apache.log4j.spi.LoggerRepository
    public Level getThreshold() {
        return this.threshold;
    }

    @Override // org.apache.log4j.spi.LoggerRepository
    public Logger getLogger(String name) {
        return getLogger(name, this.defaultFactory);
    }

    @Override // org.apache.log4j.spi.LoggerRepository
    public Logger getLogger(String name, LoggerFactory factory) {
        CategoryKey key = new CategoryKey(name);
        synchronized (this.ht) {
            try {
                Object o = this.ht.get(key);
                try {
                    if (o == null) {
                        Logger logger = factory.makeNewLoggerInstance(name);
                        logger.setHierarchy(this);
                        this.ht.put(key, logger);
                        updateParents(logger);
                        return logger;
                    }
                    try {
                        if (o instanceof Logger) {
                            return (Logger) o;
                        }
                        if (!(o instanceof ProvisionNode)) {
                            return null;
                        }
                        Logger logger2 = factory.makeNewLoggerInstance(name);
                        logger2.setHierarchy(this);
                        this.ht.put(key, logger2);
                        updateChildren((ProvisionNode) o, logger2);
                        updateParents(logger2);
                        return logger2;
                    } catch (Throwable th) {
                        th = th;
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        }
    }

    @Override // org.apache.log4j.spi.LoggerRepository
    public Enumeration getCurrentLoggers() {
        Vector v = new Vector(this.ht.size());
        Enumeration elems = this.ht.elements();
        while (elems.hasMoreElements()) {
            Object o = elems.nextElement();
            if (o instanceof Logger) {
                v.addElement(o);
            }
        }
        return v.elements();
    }

    @Override // org.apache.log4j.spi.LoggerRepository
    public Enumeration getCurrentCategories() {
        return getCurrentLoggers();
    }

    @Override // org.apache.log4j.spi.RendererSupport
    public RendererMap getRendererMap() {
        return this.rendererMap;
    }

    @Override // org.apache.log4j.spi.LoggerRepository
    public Logger getRootLogger() {
        return this.root;
    }

    @Override // org.apache.log4j.spi.LoggerRepository
    public boolean isDisabled(int level) {
        return this.thresholdInt > level;
    }

    public void overrideAsNeeded(String override) {
        LogLog.warn("The Hiearchy.overrideAsNeeded method has been deprecated.");
    }

    @Override // org.apache.log4j.spi.LoggerRepository
    public void resetConfiguration() {
        getRootLogger().setLevel(Level.DEBUG);
        this.root.setResourceBundle(null);
        setThreshold(Level.ALL);
        synchronized (this.ht) {
            shutdown();
            Enumeration cats = getCurrentLoggers();
            while (cats.hasMoreElements()) {
                Logger c = (Logger) cats.nextElement();
                c.setLevel(null);
                c.setAdditivity(true);
                c.setResourceBundle(null);
            }
        }
        this.rendererMap.clear();
    }

    public void setDisableOverride(String override) {
        LogLog.warn("The Hiearchy.setDisableOverride method has been deprecated.");
    }

    @Override // org.apache.log4j.spi.RendererSupport
    public void setRenderer(Class renderedClass, ObjectRenderer renderer) {
        this.rendererMap.put(renderedClass, renderer);
    }

    @Override // org.apache.log4j.spi.LoggerRepository
    public void shutdown() {
        Logger root = getRootLogger();
        root.closeNestedAppenders();
        synchronized (this.ht) {
            Enumeration cats = getCurrentLoggers();
            while (cats.hasMoreElements()) {
                Logger c = (Logger) cats.nextElement();
                c.closeNestedAppenders();
            }
            root.removeAllAppenders();
            Enumeration cats2 = getCurrentLoggers();
            while (cats2.hasMoreElements()) {
                Logger c2 = (Logger) cats2.nextElement();
                c2.removeAllAppenders();
            }
        }
    }

    private final void updateParents(Logger cat) {
        String name = cat.name;
        int length = name.length();
        boolean parentFound = false;
        int i = name.lastIndexOf(46, length - 1);
        while (true) {
            if (i < 0) {
                break;
            }
            String substr = name.substring(0, i);
            CategoryKey key = new CategoryKey(substr);
            Object o = this.ht.get(key);
            if (o == null) {
                ProvisionNode pn = new ProvisionNode(cat);
                this.ht.put(key, pn);
            } else if (o instanceof Category) {
                parentFound = true;
                cat.parent = (Category) o;
                break;
            } else if (o instanceof ProvisionNode) {
                ((ProvisionNode) o).addElement(cat);
            } else {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append("unexpected object type ");
                stringBuffer.append(o.getClass());
                stringBuffer.append(" in ht.");
                Exception e = new IllegalStateException(stringBuffer.toString());
                e.printStackTrace();
            }
            i = name.lastIndexOf(46, i - 1);
        }
        if (!parentFound) {
            cat.parent = this.root;
        }
    }

    private final void updateChildren(ProvisionNode pn, Logger logger) {
        int last = pn.size();
        for (int i = 0; i < last; i++) {
            Logger l = (Logger) pn.elementAt(i);
            if (!l.parent.name.startsWith(logger.name)) {
                logger.parent = l.parent;
                l.parent = logger;
            }
        }
    }
}
