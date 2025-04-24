package org.apache.log4j.xml;

import org.apache.log4j.helpers.LogLog;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/* loaded from: classes.dex */
public class SAXErrorHandler implements ErrorHandler {
    @Override // org.xml.sax.ErrorHandler
    public void error(SAXParseException ex) {
        emitMessage("Continuable parsing error ", ex);
    }

    @Override // org.xml.sax.ErrorHandler
    public void fatalError(SAXParseException ex) {
        emitMessage("Fatal parsing error ", ex);
    }

    @Override // org.xml.sax.ErrorHandler
    public void warning(SAXParseException ex) {
        emitMessage("Parsing warning ", ex);
    }

    private static void emitMessage(String msg, SAXParseException ex) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(msg);
        stringBuffer.append(ex.getLineNumber());
        stringBuffer.append(" and column ");
        stringBuffer.append(ex.getColumnNumber());
        LogLog.warn(stringBuffer.toString());
        LogLog.warn(ex.getMessage(), ex.getException());
    }
}
