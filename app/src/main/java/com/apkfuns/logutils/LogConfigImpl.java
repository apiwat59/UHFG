package com.apkfuns.logutils;

import android.text.TextUtils;
import com.apkfuns.logutils.pattern.LogPattern;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
class LogConfigImpl implements LogConfig {
    private static LogConfigImpl singleton;
    private String formatTag;
    private String tagPrefix;
    private boolean enable = true;
    private boolean showBorder = true;
    private int logLevel = 1;
    private int methodOffset = 0;
    private List<Parser> parseList = new ArrayList();

    private LogConfigImpl() {
    }

    static LogConfigImpl getInstance() {
        if (singleton == null) {
            synchronized (LogConfigImpl.class) {
                if (singleton == null) {
                    singleton = new LogConfigImpl();
                }
            }
        }
        return singleton;
    }

    @Override // com.apkfuns.logutils.LogConfig
    public LogConfig configAllowLog(boolean allowLog) {
        this.enable = allowLog;
        return this;
    }

    @Override // com.apkfuns.logutils.LogConfig
    public LogConfig configTagPrefix(String prefix) {
        this.tagPrefix = prefix;
        return this;
    }

    @Override // com.apkfuns.logutils.LogConfig
    public LogConfig configFormatTag(String formatTag) {
        this.formatTag = formatTag;
        return this;
    }

    public String getFormatTag(StackTraceElement caller) {
        if (TextUtils.isEmpty(this.formatTag)) {
            return null;
        }
        return LogPattern.compile(this.formatTag).apply(caller);
    }

    @Override // com.apkfuns.logutils.LogConfig
    public LogConfig configShowBorders(boolean showBorder) {
        this.showBorder = showBorder;
        return this;
    }

    @Override // com.apkfuns.logutils.LogConfig
    public LogConfig configMethodOffset(int offset) {
        this.methodOffset = offset;
        return this;
    }

    public int getMethodOffset() {
        return this.methodOffset;
    }

    @Override // com.apkfuns.logutils.LogConfig
    public LogConfig configLevel(int logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    @Override // com.apkfuns.logutils.LogConfig
    public LogConfig addParserClass(Class<? extends Parser>... classes) {
        for (Class<? extends Parser> cla : classes) {
            try {
                this.parseList.add(0, cla.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public boolean isEnable() {
        return this.enable;
    }

    public String getTagPrefix() {
        if (TextUtils.isEmpty(this.tagPrefix)) {
            return Constant.TAG;
        }
        return this.tagPrefix;
    }

    public boolean isShowBorder() {
        return this.showBorder;
    }

    public int getLogLevel() {
        return this.logLevel;
    }

    public List<Parser> getParseList() {
        return this.parseList;
    }
}
