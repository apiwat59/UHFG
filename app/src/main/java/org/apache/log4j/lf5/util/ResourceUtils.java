package org.apache.log4j.lf5.util;

import java.io.InputStream;
import java.net.URL;

/* loaded from: classes.dex */
public class ResourceUtils {
    public static InputStream getResourceAsStream(Object object, Resource resource) {
        ClassLoader loader = object.getClass().getClassLoader();
        if (loader != null) {
            InputStream in = loader.getResourceAsStream(resource.getName());
            return in;
        }
        InputStream in2 = ClassLoader.getSystemResourceAsStream(resource.getName());
        return in2;
    }

    public static URL getResourceAsURL(Object object, Resource resource) {
        ClassLoader loader = object.getClass().getClassLoader();
        if (loader != null) {
            URL url = loader.getResource(resource.getName());
            return url;
        }
        URL url2 = ClassLoader.getSystemResource(resource.getName());
        return url2;
    }
}
