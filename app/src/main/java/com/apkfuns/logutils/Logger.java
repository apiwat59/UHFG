package com.apkfuns.logutils;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.apkfuns.logutils.file.LogFileParam;
import com.apkfuns.logutils.utils.ObjectUtil;
import com.apkfuns.logutils.utils.Utils;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.MissingFormatArgumentException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
class Logger implements Printer {
    private final ThreadLocal<String> localTags = new ThreadLocal<>();
    private LogConfigImpl mLogConfig = LogConfigImpl.getInstance();
    private Log2FileConfigImpl log2FileConfig = Log2FileConfigImpl.getInstance();

    protected Logger() {
        this.mLogConfig.addParserClass(Constant.DEFAULT_PARSE_CLASS);
    }

    public Printer setTag(String tag) {
        if (!TextUtils.isEmpty(tag) && this.mLogConfig.isEnable()) {
            this.localTags.set(tag);
        }
        return this;
    }

    private synchronized void logString(int type, String msg, Object... args) {
        logString(type, msg, false, args);
    }

    private void logString(int type, String msg, boolean isPart, Object... args) {
        String tag = generateTag();
        if (!isPart) {
            if (args.length > 0) {
                try {
                    msg = String.format(msg, args);
                } catch (MissingFormatArgumentException e) {
                }
            }
            writeToFile(tag, msg, type);
        }
        if (!this.mLogConfig.isEnable() || type < this.mLogConfig.getLogLevel()) {
            return;
        }
        if (msg.length() > 3072) {
            if (this.mLogConfig.isShowBorder()) {
                printLog(type, tag, Utils.printDividingLine(1));
                printLog(type, tag, Utils.printDividingLine(3) + getTopStackInfo());
                printLog(type, tag, Utils.printDividingLine(4));
            }
            for (String subMsg : Utils.largeStringToList(msg)) {
                logString(type, subMsg, true, args);
            }
            if (this.mLogConfig.isShowBorder()) {
                printLog(type, tag, Utils.printDividingLine(2));
                return;
            }
            return;
        }
        if (this.mLogConfig.isShowBorder()) {
            int i = 0;
            if (isPart) {
                String[] split = msg.split(Constant.BR);
                int length = split.length;
                while (i < length) {
                    String sub = split[i];
                    printLog(type, tag, Utils.printDividingLine(3) + sub);
                    i++;
                }
                return;
            }
            printLog(type, tag, Utils.printDividingLine(1));
            printLog(type, tag, Utils.printDividingLine(3) + getTopStackInfo());
            printLog(type, tag, Utils.printDividingLine(4));
            String[] split2 = msg.split(Constant.BR);
            int length2 = split2.length;
            while (i < length2) {
                String sub2 = split2[i];
                printLog(type, tag, Utils.printDividingLine(3) + sub2);
                i++;
            }
            printLog(type, tag, Utils.printDividingLine(2));
            return;
        }
        printLog(type, tag, msg);
    }

    private void logObject(int type, Object object) {
        logString(type, ObjectUtil.objectToString(object), new Object[0]);
    }

    private String generateTag() {
        String tempTag = this.localTags.get();
        if (!TextUtils.isEmpty(tempTag)) {
            this.localTags.remove();
            return tempTag;
        }
        return this.mLogConfig.getTagPrefix();
    }

    private StackTraceElement getCurrentStackTrace() {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        int stackOffset = getStackOffset(trace, LogUtils.class);
        if (stackOffset == -1 && (stackOffset = getStackOffset(trace, Logger.class)) == -1) {
            return null;
        }
        if (this.mLogConfig.getMethodOffset() > 0) {
            stackOffset += this.mLogConfig.getMethodOffset();
        }
        StackTraceElement caller = trace[stackOffset];
        return caller;
    }

    private String getTopStackInfo() {
        String customTag = this.mLogConfig.getFormatTag(getCurrentStackTrace());
        if (customTag != null) {
            return customTag;
        }
        StackTraceElement caller = getCurrentStackTrace();
        String stackTrace = caller.toString();
        String stackTrace2 = stackTrace.substring(stackTrace.lastIndexOf(40), stackTrace.length());
        String callerClazzName = caller.getClassName();
        String tag = String.format("%s.%s%s", callerClazzName.substring(callerClazzName.lastIndexOf(".") + 1), caller.getMethodName(), stackTrace2);
        return tag;
    }

    private int getStackOffset(StackTraceElement[] trace, Class cla) {
        for (int i = 5; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if ((!cla.equals(Logger.class) || i >= trace.length - 1 || !trace[i + 1].getClassName().equals(Logger.class.getName())) && name.equals(cla.getName())) {
                return i + 1;
            }
        }
        return -1;
    }

    @Override // com.apkfuns.logutils.Printer
    public void d(String message, Object... args) {
        logString(2, message, args);
    }

    @Override // com.apkfuns.logutils.Printer
    public void d(Object object) {
        logObject(2, object);
    }

    @Override // com.apkfuns.logutils.Printer
    public void e(String message, Object... args) {
        logString(5, message, args);
    }

    @Override // com.apkfuns.logutils.Printer
    public void e(Object object) {
        logObject(5, object);
    }

    @Override // com.apkfuns.logutils.Printer
    public void w(String message, Object... args) {
        logString(4, message, args);
    }

    @Override // com.apkfuns.logutils.Printer
    public void w(Object object) {
        logObject(4, object);
    }

    @Override // com.apkfuns.logutils.Printer
    public void i(String message, Object... args) {
        logString(3, message, args);
    }

    @Override // com.apkfuns.logutils.Printer
    public void i(Object object) {
        logObject(3, object);
    }

    @Override // com.apkfuns.logutils.Printer
    public void v(String message, Object... args) {
        logString(1, message, args);
    }

    @Override // com.apkfuns.logutils.Printer
    public void v(Object object) {
        logObject(1, object);
    }

    @Override // com.apkfuns.logutils.Printer
    public void wtf(String message, Object... args) {
        logString(6, message, args);
    }

    @Override // com.apkfuns.logutils.Printer
    public void wtf(Object object) {
        logObject(6, object);
    }

    @Override // com.apkfuns.logutils.Printer
    public void json(String json) {
        if (TextUtils.isEmpty(json)) {
            d("JSON{json is empty}");
            return;
        }
        try {
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String msg = jsonObject.toString(4);
                d(msg);
            } else if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String msg2 = jsonArray.toString(4);
                d(msg2);
            }
        } catch (JSONException e) {
            e(e.toString() + "\n\njson = " + json);
        }
    }

    @Override // com.apkfuns.logutils.Printer
    public void xml(String xml) {
        if (TextUtils.isEmpty(xml)) {
            d("XML{xml is empty}");
            return;
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            d(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
        } catch (TransformerException e) {
            e(e.toString() + "\n\nxml = " + xml);
        }
    }

    private void printLog(int type, String tag, String msg) {
        if (!this.mLogConfig.isShowBorder()) {
            msg = getTopStackInfo() + ": " + msg;
        }
        switch (type) {
            case 1:
                Log.v(tag, msg);
                break;
            case 2:
                Log.d(tag, msg);
                break;
            case 3:
                Log.i(tag, msg);
                break;
            case 4:
                Log.w(tag, msg);
                break;
            case 5:
                Log.e(tag, msg);
                break;
            case 6:
                Log.wtf(tag, msg);
                break;
        }
    }

    private void writeToFile(String tagName, String logContent, int logLevel) {
        if (!this.log2FileConfig.isEnable()) {
            return;
        }
        if ((this.log2FileConfig.getFileFilter() != null && !this.log2FileConfig.getFileFilter().accept(logLevel, tagName, logContent)) || logLevel < this.log2FileConfig.getLogLevel()) {
            return;
        }
        String path = this.log2FileConfig.getLogPath();
        if (TextUtils.isEmpty(path)) {
            if (Build.VERSION.SDK_INT >= 23) {
                Log.e(tagName, "LogUtils write to logFile error. No sdcard access permission?");
                return;
            }
            throw new IllegalArgumentException("Log2FilePath is an invalid path");
        }
        File logFile = new File(path, this.log2FileConfig.getLogFormatName());
        LogFileParam param = new LogFileParam(System.currentTimeMillis(), logLevel, Thread.currentThread().getName(), tagName);
        if (this.log2FileConfig.getEngine() != null) {
            this.log2FileConfig.getEngine().writeToFile(logFile, logContent, param);
            return;
        }
        throw new NullPointerException("LogFileEngine must not Null");
    }
}
