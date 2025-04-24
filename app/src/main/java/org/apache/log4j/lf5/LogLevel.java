package org.apache.log4j.lf5;

import java.awt.Color;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class LogLevel implements Serializable {
    public static final LogLevel CONFIG;
    public static final LogLevel DEBUG;
    public static final LogLevel ERROR;
    public static final LogLevel FATAL;
    public static final LogLevel FINE;
    public static final LogLevel FINER;
    public static final LogLevel FINEST;
    public static final LogLevel INFO;
    public static final LogLevel SEVERE;
    public static final LogLevel WARN;
    public static final LogLevel WARNING;
    private static LogLevel[] _allDefaultLevels;
    private static LogLevel[] _jdk14Levels;
    private static LogLevel[] _log4JLevels;
    private static Map _logLevelColorMap;
    private static Map _logLevelMap;
    private static Map _registeredLogLevelMap;
    protected String _label;
    protected int _precedence;

    static {
        LogLevel logLevel = new LogLevel("FATAL", 0);
        FATAL = logLevel;
        LogLevel logLevel2 = new LogLevel("ERROR", 1);
        ERROR = logLevel2;
        LogLevel logLevel3 = new LogLevel("WARN", 2);
        WARN = logLevel3;
        LogLevel logLevel4 = new LogLevel("INFO", 3);
        INFO = logLevel4;
        LogLevel logLevel5 = new LogLevel("DEBUG", 4);
        DEBUG = logLevel5;
        LogLevel logLevel6 = new LogLevel("SEVERE", 1);
        SEVERE = logLevel6;
        LogLevel logLevel7 = new LogLevel("WARNING", 2);
        WARNING = logLevel7;
        LogLevel logLevel8 = new LogLevel("CONFIG", 4);
        CONFIG = logLevel8;
        LogLevel logLevel9 = new LogLevel("FINE", 5);
        FINE = logLevel9;
        LogLevel logLevel10 = new LogLevel("FINER", 6);
        FINER = logLevel10;
        LogLevel logLevel11 = new LogLevel("FINEST", 7);
        FINEST = logLevel11;
        _registeredLogLevelMap = new HashMap();
        _log4JLevels = new LogLevel[]{logLevel, logLevel2, logLevel3, logLevel4, logLevel5};
        _jdk14Levels = new LogLevel[]{logLevel6, logLevel7, logLevel4, logLevel8, logLevel9, logLevel10, logLevel11};
        _allDefaultLevels = new LogLevel[]{logLevel, logLevel2, logLevel3, logLevel4, logLevel5, logLevel6, logLevel7, logLevel8, logLevel9, logLevel10, logLevel11};
        _logLevelMap = new HashMap();
        int i = 0;
        while (true) {
            LogLevel[] logLevelArr = _allDefaultLevels;
            if (i >= logLevelArr.length) {
                break;
            }
            _logLevelMap.put(logLevelArr[i].getLabel(), _allDefaultLevels[i]);
            i++;
        }
        _logLevelColorMap = new HashMap();
        int i2 = 0;
        while (true) {
            LogLevel[] logLevelArr2 = _allDefaultLevels;
            if (i2 >= logLevelArr2.length) {
                return;
            }
            _logLevelColorMap.put(logLevelArr2[i2], Color.black);
            i2++;
        }
    }

    public LogLevel(String label, int precedence) {
        this._label = label;
        this._precedence = precedence;
    }

    public String getLabel() {
        return this._label;
    }

    public boolean encompasses(LogLevel level) {
        if (level.getPrecedence() <= getPrecedence()) {
            return true;
        }
        return false;
    }

    public static LogLevel valueOf(String level) throws LogLevelFormatException {
        LogLevel logLevel = null;
        if (level != null) {
            level = level.trim().toUpperCase();
            logLevel = (LogLevel) _logLevelMap.get(level);
        }
        if (logLevel == null && _registeredLogLevelMap.size() > 0) {
            logLevel = (LogLevel) _registeredLogLevelMap.get(level);
        }
        if (logLevel == null) {
            StringBuffer buf = new StringBuffer();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Error while trying to parse (");
            stringBuffer.append(level);
            stringBuffer.append(") into");
            buf.append(stringBuffer.toString());
            buf.append(" a LogLevel.");
            throw new LogLevelFormatException(buf.toString());
        }
        return logLevel;
    }

    public static LogLevel register(LogLevel logLevel) {
        if (logLevel == null || _logLevelMap.get(logLevel.getLabel()) != null) {
            return null;
        }
        return (LogLevel) _registeredLogLevelMap.put(logLevel.getLabel(), logLevel);
    }

    public static void register(LogLevel[] logLevels) {
        if (logLevels != null) {
            for (LogLevel logLevel : logLevels) {
                register(logLevel);
            }
        }
    }

    public static void register(List logLevels) {
        if (logLevels != null) {
            Iterator it = logLevels.iterator();
            while (it.hasNext()) {
                register((LogLevel) it.next());
            }
        }
    }

    public boolean equals(Object o) {
        if (!(o instanceof LogLevel) || getPrecedence() != ((LogLevel) o).getPrecedence()) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        return this._label.hashCode();
    }

    public String toString() {
        return this._label;
    }

    public void setLogLevelColorMap(LogLevel level, Color color) {
        _logLevelColorMap.remove(level);
        if (color == null) {
            color = Color.black;
        }
        _logLevelColorMap.put(level, color);
    }

    public static void resetLogLevelColorMap() {
        _logLevelColorMap.clear();
        int i = 0;
        while (true) {
            LogLevel[] logLevelArr = _allDefaultLevels;
            if (i < logLevelArr.length) {
                _logLevelColorMap.put(logLevelArr[i], Color.black);
                i++;
            } else {
                return;
            }
        }
    }

    public static List getLog4JLevels() {
        return Arrays.asList(_log4JLevels);
    }

    public static List getJdk14Levels() {
        return Arrays.asList(_jdk14Levels);
    }

    public static List getAllDefaultLevels() {
        return Arrays.asList(_allDefaultLevels);
    }

    public static Map getLogLevelColorMap() {
        return _logLevelColorMap;
    }

    protected int getPrecedence() {
        return this._precedence;
    }
}
