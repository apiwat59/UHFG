package org.apache.log4j.spi;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Hashtable;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.apache.log4j.Priority;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;

/* loaded from: classes.dex */
public class LoggingEvent implements Serializable {
    static final String TO_LEVEL = "toLevel";
    static /* synthetic */ Class class$org$apache$log4j$Level = null;
    static final long serialVersionUID = -868428216207166145L;
    public final String categoryName;
    public final transient String fqnOfCategoryClass;
    public transient Priority level;
    private LocationInfo locationInfo;
    private transient Category logger;
    private Hashtable mdcCopy;
    private transient Object message;
    private String ndc;
    private String renderedMessage;
    private String threadName;
    private ThrowableInformation throwableInfo;
    public final long timeStamp;
    private static long startTime = System.currentTimeMillis();
    static final Integer[] PARAM_ARRAY = new Integer[1];
    static final Class[] TO_LEVEL_PARAMS = {Integer.TYPE};
    static final Hashtable methodCache = new Hashtable(3);
    private boolean ndcLookupRequired = true;
    private boolean mdcCopyLookupRequired = true;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public LoggingEvent(String fqnOfCategoryClass, Category logger, Priority level, Object message, Throwable throwable) {
        this.fqnOfCategoryClass = fqnOfCategoryClass;
        this.logger = logger;
        this.categoryName = logger.getName();
        this.level = level;
        this.message = message;
        if (throwable != null) {
            this.throwableInfo = new ThrowableInformation(throwable);
        }
        this.timeStamp = System.currentTimeMillis();
    }

    public LoggingEvent(String fqnOfCategoryClass, Category logger, long timeStamp, Priority level, Object message, Throwable throwable) {
        this.fqnOfCategoryClass = fqnOfCategoryClass;
        this.logger = logger;
        this.categoryName = logger.getName();
        this.level = level;
        this.message = message;
        if (throwable != null) {
            this.throwableInfo = new ThrowableInformation(throwable);
        }
        this.timeStamp = timeStamp;
    }

    public LocationInfo getLocationInformation() {
        if (this.locationInfo == null) {
            this.locationInfo = new LocationInfo(new Throwable(), this.fqnOfCategoryClass);
        }
        return this.locationInfo;
    }

    public Level getLevel() {
        return (Level) this.level;
    }

    public String getLoggerName() {
        return this.categoryName;
    }

    public Object getMessage() {
        Object obj = this.message;
        if (obj != null) {
            return obj;
        }
        return getRenderedMessage();
    }

    public String getNDC() {
        if (this.ndcLookupRequired) {
            this.ndcLookupRequired = false;
            this.ndc = NDC.get();
        }
        return this.ndc;
    }

    public Object getMDC(String key) {
        Object r;
        Hashtable hashtable = this.mdcCopy;
        if (hashtable != null && (r = hashtable.get(key)) != null) {
            return r;
        }
        return MDC.get(key);
    }

    public void getMDCCopy() {
        if (this.mdcCopyLookupRequired) {
            this.mdcCopyLookupRequired = false;
            Hashtable t = MDC.getContext();
            if (t != null) {
                this.mdcCopy = (Hashtable) t.clone();
            }
        }
    }

    public String getRenderedMessage() {
        Object obj;
        if (this.renderedMessage == null && (obj = this.message) != null) {
            if (obj instanceof String) {
                this.renderedMessage = (String) obj;
            } else {
                LoggerRepository repository = this.logger.getLoggerRepository();
                if (repository instanceof RendererSupport) {
                    RendererSupport rs = (RendererSupport) repository;
                    this.renderedMessage = rs.getRendererMap().findAndRender(this.message);
                } else {
                    this.renderedMessage = this.message.toString();
                }
            }
        }
        return this.renderedMessage;
    }

    public static long getStartTime() {
        return startTime;
    }

    public String getThreadName() {
        if (this.threadName == null) {
            this.threadName = Thread.currentThread().getName();
        }
        return this.threadName;
    }

    public ThrowableInformation getThrowableInformation() {
        return this.throwableInfo;
    }

    public String[] getThrowableStrRep() {
        ThrowableInformation throwableInformation = this.throwableInfo;
        if (throwableInformation == null) {
            return null;
        }
        return throwableInformation.getThrowableStrRep();
    }

    private void readLevel(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        int p = ois.readInt();
        try {
            String className = (String) ois.readObject();
            if (className == null) {
                this.level = Level.toLevel(p);
                return;
            }
            Hashtable hashtable = methodCache;
            Method m = (Method) hashtable.get(className);
            if (m == null) {
                Class clazz = Loader.loadClass(className);
                m = clazz.getDeclaredMethod(TO_LEVEL, TO_LEVEL_PARAMS);
                hashtable.put(className, m);
            }
            Integer[] numArr = PARAM_ARRAY;
            numArr[0] = new Integer(p);
            this.level = (Level) m.invoke(null, numArr);
        } catch (Exception e) {
            LogLog.warn("Level deserialization failed, reverting to default.", e);
            this.level = Level.toLevel(p);
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        readLevel(ois);
        if (this.locationInfo == null) {
            this.locationInfo = new LocationInfo(null, null);
        }
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        getThreadName();
        getRenderedMessage();
        getNDC();
        getMDCCopy();
        getThrowableStrRep();
        oos.defaultWriteObject();
        writeLevel(oos);
    }

    private void writeLevel(ObjectOutputStream oos) throws IOException {
        oos.writeInt(this.level.toInt());
        Class clazz = this.level.getClass();
        Class cls = class$org$apache$log4j$Level;
        if (cls == null) {
            cls = class$("org.apache.log4j.Level");
            class$org$apache$log4j$Level = cls;
        }
        if (clazz == cls) {
            oos.writeObject(null);
        } else {
            oos.writeObject(clazz.getName());
        }
    }
}
