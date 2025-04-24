package com.apkfuns.logutils;

import android.os.Environment;
import android.text.TextUtils;
import com.apkfuns.logutils.file.LogFileEngine;
import com.apkfuns.logutils.file.LogFileFilter;
import com.apkfuns.logutils.pattern.LogPattern;
import java.io.File;

/* loaded from: classes.dex */
class Log2FileConfigImpl implements Log2FileConfig {
    public static final String DEFAULT_LOG_NAME_FORMAT = "%d{yyyyMMdd}.txt";
    private static Log2FileConfigImpl singleton;
    private LogFileEngine engine;
    private LogFileFilter fileFilter;
    private String logPath;
    private int logLevel = 1;
    private boolean enable = false;
    private String logFormatName = DEFAULT_LOG_NAME_FORMAT;

    Log2FileConfigImpl() {
    }

    public static Log2FileConfigImpl getInstance() {
        if (singleton == null) {
            synchronized (Log2FileConfigImpl.class) {
                if (singleton == null) {
                    singleton = new Log2FileConfigImpl();
                }
            }
        }
        return singleton;
    }

    @Override // com.apkfuns.logutils.Log2FileConfig
    public Log2FileConfig configLog2FileEnable(boolean enable) {
        this.enable = enable;
        return this;
    }

    public boolean isEnable() {
        return this.enable;
    }

    @Override // com.apkfuns.logutils.Log2FileConfig
    public Log2FileConfig configLog2FilePath(String logPath) {
        this.logPath = logPath;
        return this;
    }

    public String getLogPath() {
        if (TextUtils.isEmpty(this.logPath)) {
            return getDefaultPath();
        }
        File file = new File(this.logPath);
        if (file.exists() && file.isDirectory()) {
            return this.logPath;
        }
        if (file.isFile() && file.getParentFile() != null) {
            if (file.getParentFile().exists()) {
                return file.getParent();
            }
            boolean ret = file.getParentFile().mkdirs();
            if (ret) {
                return file.getParent();
            }
        }
        boolean ret2 = file.mkdirs();
        if (ret2) {
            return this.logPath;
        }
        return null;
    }

    @Override // com.apkfuns.logutils.Log2FileConfig
    public Log2FileConfig configLog2FileNameFormat(String formatName) {
        if (!TextUtils.isEmpty(formatName)) {
            this.logFormatName = formatName;
        }
        return this;
    }

    public String getLogFormatName() {
        return new LogPattern.Log2FileNamePattern(this.logFormatName).doApply();
    }

    @Override // com.apkfuns.logutils.Log2FileConfig
    public Log2FileConfig configLog2FileLevel(int level) {
        this.logLevel = level;
        return this;
    }

    public int getLogLevel() {
        return this.logLevel;
    }

    @Override // com.apkfuns.logutils.Log2FileConfig
    public Log2FileConfig configLogFileEngine(LogFileEngine engine) {
        this.engine = engine;
        return this;
    }

    @Override // com.apkfuns.logutils.Log2FileConfig
    public Log2FileConfig configLogFileFilter(LogFileFilter fileFilter) {
        this.fileFilter = fileFilter;
        return this;
    }

    @Override // com.apkfuns.logutils.Log2FileConfig
    public File getLogFile() {
        String path = getLogPath();
        if (!TextUtils.isEmpty(path)) {
            return new File(path, getLogFormatName());
        }
        return null;
    }

    public LogFileFilter getFileFilter() {
        return this.fileFilter;
    }

    public LogFileEngine getEngine() {
        return this.engine;
    }

    public String getDefaultPath() {
        if ("mounted".equals(Environment.getExternalStorageState())) {
            String basePath = Environment.getExternalStorageDirectory() + File.separator;
            return basePath + Constant.TAG + File.separator + "logs";
        }
        throw new IllegalStateException("Sdcard No Access, please config Log2FilePath");
    }
}
