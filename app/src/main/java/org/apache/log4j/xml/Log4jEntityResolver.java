package org.apache.log4j.xml;

import java.io.InputStream;
import org.apache.log4j.helpers.LogLog;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/* loaded from: classes.dex */
public class Log4jEntityResolver implements EntityResolver {
    @Override // org.xml.sax.EntityResolver
    public InputSource resolveEntity(String publicId, String systemId) {
        if (!systemId.endsWith("log4j.dtd")) {
            return null;
        }
        Class clazz = getClass();
        InputStream in = clazz.getResourceAsStream("/org/apache/log4j/xml/log4j.dtd");
        if (in == null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Could not find [log4j.dtd]. Used [");
            stringBuffer.append(clazz.getClassLoader());
            stringBuffer.append("] class loader in the search.");
            LogLog.error(stringBuffer.toString());
            return null;
        }
        return new InputSource(in);
    }
}
