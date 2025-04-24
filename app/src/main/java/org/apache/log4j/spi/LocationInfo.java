package org.apache.log4j.spi;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.LogLog;

/* loaded from: classes.dex */
public class LocationInfo implements Serializable {
    public static final String NA = "?";
    static boolean inVisualAge = false;
    static final long serialVersionUID = -1325822038990805636L;
    transient String className;
    transient String fileName;
    public String fullInfo;
    transient String lineNumber;
    transient String methodName;
    private static StringWriter sw = new StringWriter();
    private static PrintWriter pw = new PrintWriter(sw);

    static {
        inVisualAge = false;
        try {
            Class.forName("com.ibm.uvm.tools.DebugSupport");
            inVisualAge = true;
            LogLog.debug("Detected IBM VisualAge environment.");
        } catch (Throwable th) {
        }
    }

    public LocationInfo(Throwable t, String fqnOfCallingClass) {
        String s;
        int ibegin;
        int ibegin2;
        int iend;
        if (t == null) {
            return;
        }
        synchronized (sw) {
            try {
                t.printStackTrace(pw);
                s = sw.toString();
            } catch (Throwable th) {
                th = th;
            }
            try {
                sw.getBuffer().setLength(0);
                int ibegin3 = s.lastIndexOf(fqnOfCallingClass);
                if (ibegin3 == -1 || (ibegin = s.indexOf(Layout.LINE_SEP, ibegin3)) == -1 || (iend = s.indexOf(Layout.LINE_SEP, (ibegin2 = ibegin + Layout.LINE_SEP_LEN))) == -1) {
                    return;
                }
                if (!inVisualAge) {
                    int ibegin4 = s.lastIndexOf("at ", iend);
                    if (ibegin4 == -1) {
                        return;
                    } else {
                        ibegin2 = ibegin4 + 3;
                    }
                }
                this.fullInfo = s.substring(ibegin2, iend);
            } catch (Throwable th2) {
                th = th2;
                throw th;
            }
        }
    }

    public String getClassName() {
        String str = this.fullInfo;
        if (str == null) {
            return NA;
        }
        if (this.className == null) {
            int iend = str.lastIndexOf(40);
            if (iend == -1) {
                this.className = NA;
            } else {
                int iend2 = this.fullInfo.lastIndexOf(46, iend);
                int ibegin = 0;
                if (inVisualAge) {
                    ibegin = this.fullInfo.lastIndexOf(32, iend2) + 1;
                }
                if (iend2 == -1) {
                    this.className = NA;
                } else {
                    this.className = this.fullInfo.substring(ibegin, iend2);
                }
            }
        }
        return this.className;
    }

    public String getFileName() {
        String str = this.fullInfo;
        if (str == null) {
            return NA;
        }
        if (this.fileName == null) {
            int iend = str.lastIndexOf(58);
            if (iend == -1) {
                this.fileName = NA;
            } else {
                int ibegin = this.fullInfo.lastIndexOf(40, iend - 1);
                this.fileName = this.fullInfo.substring(ibegin + 1, iend);
            }
        }
        return this.fileName;
    }

    public String getLineNumber() {
        String str = this.fullInfo;
        if (str == null) {
            return NA;
        }
        if (this.lineNumber == null) {
            int iend = str.lastIndexOf(41);
            int ibegin = this.fullInfo.lastIndexOf(58, iend - 1);
            if (ibegin == -1) {
                this.lineNumber = NA;
            } else {
                this.lineNumber = this.fullInfo.substring(ibegin + 1, iend);
            }
        }
        return this.lineNumber;
    }

    public String getMethodName() {
        String str = this.fullInfo;
        if (str == null) {
            return NA;
        }
        if (this.methodName == null) {
            int iend = str.lastIndexOf(40);
            int ibegin = this.fullInfo.lastIndexOf(46, iend);
            if (ibegin == -1) {
                this.methodName = NA;
            } else {
                this.methodName = this.fullInfo.substring(ibegin + 1, iend);
            }
        }
        return this.methodName;
    }
}
