package org.apache.log4j.helpers;

/* loaded from: classes.dex */
public class FormattingInfo {
    int min = -1;
    int max = Integer.MAX_VALUE;
    boolean leftAlign = false;

    void reset() {
        this.min = -1;
        this.max = Integer.MAX_VALUE;
        this.leftAlign = false;
    }

    void dump() {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("min=");
        stringBuffer.append(this.min);
        stringBuffer.append(", max=");
        stringBuffer.append(this.max);
        stringBuffer.append(", leftAlign=");
        stringBuffer.append(this.leftAlign);
        LogLog.debug(stringBuffer.toString());
    }
}
