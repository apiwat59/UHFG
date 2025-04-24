package org.apache.log4j.helpers;

import java.io.Writer;
import org.apache.log4j.spi.ErrorHandler;

/* loaded from: classes.dex */
public class SyslogQuietWriter extends QuietWriter {
    int level;
    int syslogFacility;

    public SyslogQuietWriter(Writer writer, int syslogFacility, ErrorHandler eh) {
        super(writer, eh);
        this.syslogFacility = syslogFacility;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setSyslogFacility(int syslogFacility) {
        this.syslogFacility = syslogFacility;
    }

    @Override // org.apache.log4j.helpers.QuietWriter, java.io.Writer
    public void write(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("<");
        stringBuffer.append(this.syslogFacility | this.level);
        stringBuffer.append(">");
        stringBuffer.append(string);
        super.write(stringBuffer.toString());
    }
}
