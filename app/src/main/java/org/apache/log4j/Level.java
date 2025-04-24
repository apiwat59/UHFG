package org.apache.log4j;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

/* loaded from: classes.dex */
public class Level extends Priority implements Serializable {
    static /* synthetic */ Class class$org$apache$log4j$Level = null;
    static final long serialVersionUID = 3491141966387921974L;
    public static final Level OFF = new Level(Integer.MAX_VALUE, "OFF", 0);
    public static final Level FATAL = new Level(Priority.FATAL_INT, "FATAL", 0);
    public static final Level ERROR = new Level(Priority.ERROR_INT, "ERROR", 3);
    public static final Level WARN = new Level(Priority.WARN_INT, "WARN", 4);
    public static final Level INFO = new Level(Priority.INFO_INT, "INFO", 6);
    public static final Level DEBUG = new Level(Priority.DEBUG_INT, "DEBUG", 7);
    public static final int TRACE_INT = 5000;
    public static final Level TRACE = new Level(TRACE_INT, "TRACE", 7);
    public static final Level ALL = new Level(Integer.MIN_VALUE, "ALL", 7);

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    protected Level(int level, String levelStr, int syslogEquivalent) {
        super(level, levelStr, syslogEquivalent);
    }

    public static Level toLevel(String sArg) {
        return toLevel(sArg, DEBUG);
    }

    public static Level toLevel(int val) {
        return toLevel(val, DEBUG);
    }

    public static Level toLevel(int val, Level defaultLevel) {
        if (val == Integer.MIN_VALUE) {
            return ALL;
        }
        if (val == 5000) {
            return TRACE;
        }
        if (val == 10000) {
            return DEBUG;
        }
        if (val == 20000) {
            return INFO;
        }
        if (val == 30000) {
            return WARN;
        }
        if (val == 40000) {
            return ERROR;
        }
        if (val == 50000) {
            return FATAL;
        }
        if (val == Integer.MAX_VALUE) {
            return OFF;
        }
        return defaultLevel;
    }

    public static Level toLevel(String sArg, Level defaultLevel) {
        if (sArg == null) {
            return defaultLevel;
        }
        String s = sArg.toUpperCase();
        return s.equals("ALL") ? ALL : s.equals("DEBUG") ? DEBUG : s.equals("INFO") ? INFO : s.equals("WARN") ? WARN : s.equals("ERROR") ? ERROR : s.equals("FATAL") ? FATAL : s.equals("OFF") ? OFF : s.equals("TRACE") ? TRACE : defaultLevel;
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.level = s.readInt();
        this.syslogEquivalent = s.readInt();
        this.levelStr = s.readUTF();
        if (this.levelStr == null) {
            this.levelStr = "";
        }
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(this.level);
        s.writeInt(this.syslogEquivalent);
        s.writeUTF(this.levelStr);
    }

    private Object readResolve() throws ObjectStreamException {
        Class<?> cls = getClass();
        Class<?> cls2 = class$org$apache$log4j$Level;
        if (cls2 == null) {
            cls2 = class$("org.apache.log4j.Level");
            class$org$apache$log4j$Level = cls2;
        }
        if (cls == cls2) {
            return toLevel(this.level);
        }
        return this;
    }
}
