package org.apache.log4j.varia;

import java.util.Vector;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.LoggingEvent;

/* loaded from: classes.dex */
public class FallbackErrorHandler implements ErrorHandler {
    Appender backup;
    Vector loggers;
    Appender primary;

    @Override // org.apache.log4j.spi.ErrorHandler
    public void setLogger(Logger logger) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("FB: Adding logger [");
        stringBuffer.append(logger.getName());
        stringBuffer.append("].");
        LogLog.debug(stringBuffer.toString());
        if (this.loggers == null) {
            this.loggers = new Vector();
        }
        this.loggers.addElement(logger);
    }

    @Override // org.apache.log4j.spi.OptionHandler
    public void activateOptions() {
    }

    @Override // org.apache.log4j.spi.ErrorHandler
    public void error(String message, Exception e, int errorCode) {
        error(message, e, errorCode, null);
    }

    @Override // org.apache.log4j.spi.ErrorHandler
    public void error(String message, Exception e, int errorCode, LoggingEvent event) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("FB: The following error reported: ");
        stringBuffer.append(message);
        LogLog.debug(stringBuffer.toString(), e);
        LogLog.debug("FB: INITIATING FALLBACK PROCEDURE.");
        if (this.loggers != null) {
            for (int i = 0; i < this.loggers.size(); i++) {
                Logger l = (Logger) this.loggers.elementAt(i);
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append("FB: Searching for [");
                stringBuffer2.append(this.primary.getName());
                stringBuffer2.append("] in logger [");
                stringBuffer2.append(l.getName());
                stringBuffer2.append("].");
                LogLog.debug(stringBuffer2.toString());
                StringBuffer stringBuffer3 = new StringBuffer();
                stringBuffer3.append("FB: Replacing [");
                stringBuffer3.append(this.primary.getName());
                stringBuffer3.append("] by [");
                stringBuffer3.append(this.backup.getName());
                stringBuffer3.append("] in logger [");
                stringBuffer3.append(l.getName());
                stringBuffer3.append("].");
                LogLog.debug(stringBuffer3.toString());
                l.removeAppender(this.primary);
                StringBuffer stringBuffer4 = new StringBuffer();
                stringBuffer4.append("FB: Adding appender [");
                stringBuffer4.append(this.backup.getName());
                stringBuffer4.append("] to logger ");
                stringBuffer4.append(l.getName());
                LogLog.debug(stringBuffer4.toString());
                l.addAppender(this.backup);
            }
        }
    }

    @Override // org.apache.log4j.spi.ErrorHandler
    public void error(String message) {
    }

    @Override // org.apache.log4j.spi.ErrorHandler
    public void setAppender(Appender primary) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("FB: Setting primary appender to [");
        stringBuffer.append(primary.getName());
        stringBuffer.append("].");
        LogLog.debug(stringBuffer.toString());
        this.primary = primary;
    }

    @Override // org.apache.log4j.spi.ErrorHandler
    public void setBackupAppender(Appender backup) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("FB: Setting backup appender to [");
        stringBuffer.append(backup.getName());
        stringBuffer.append("].");
        LogLog.debug(stringBuffer.toString());
        this.backup = backup;
    }
}
