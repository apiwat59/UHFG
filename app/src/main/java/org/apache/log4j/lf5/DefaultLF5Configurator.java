package org.apache.log4j.lf5;

import java.io.IOException;
import java.net.URL;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.Configurator;
import org.apache.log4j.spi.LoggerRepository;

/* loaded from: classes.dex */
public class DefaultLF5Configurator implements Configurator {
    static /* synthetic */ Class class$org$apache$log4j$lf5$DefaultLF5Configurator;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private DefaultLF5Configurator() {
    }

    public static void configure() throws IOException {
        Class cls = class$org$apache$log4j$lf5$DefaultLF5Configurator;
        if (cls == null) {
            cls = class$("org.apache.log4j.lf5.DefaultLF5Configurator");
            class$org$apache$log4j$lf5$DefaultLF5Configurator = cls;
        }
        URL configFileResource = cls.getResource("/org/apache/log4j/lf5/config/defaultconfig.properties");
        if (configFileResource == null) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Error: Unable to open the resource");
            stringBuffer.append("/org/apache/log4j/lf5/config/defaultconfig.properties");
            throw new IOException(stringBuffer.toString());
        }
        PropertyConfigurator.configure(configFileResource);
    }

    @Override // org.apache.log4j.spi.Configurator
    public void doConfigure(URL configURL, LoggerRepository repository) {
        throw new IllegalStateException("This class should NOT be instantiated!");
    }
}
