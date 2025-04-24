package org.apache.log4j.or;

import java.util.Hashtable;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.RendererSupport;

/* loaded from: classes.dex */
public class RendererMap {
    static /* synthetic */ Class class$org$apache$log4j$or$ObjectRenderer;
    static ObjectRenderer defaultRenderer = new DefaultRenderer();
    Hashtable map = new Hashtable();

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    public static void addRenderer(RendererSupport repository, String renderedClassName, String renderingClassName) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Rendering class: [");
        stringBuffer.append(renderingClassName);
        stringBuffer.append("], Rendered class: [");
        stringBuffer.append(renderedClassName);
        stringBuffer.append("].");
        LogLog.debug(stringBuffer.toString());
        Class cls = class$org$apache$log4j$or$ObjectRenderer;
        if (cls == null) {
            cls = class$("org.apache.log4j.or.ObjectRenderer");
            class$org$apache$log4j$or$ObjectRenderer = cls;
        }
        ObjectRenderer renderer = (ObjectRenderer) OptionConverter.instantiateByClassName(renderingClassName, cls, null);
        if (renderer == null) {
            StringBuffer stringBuffer2 = new StringBuffer();
            stringBuffer2.append("Could not instantiate renderer [");
            stringBuffer2.append(renderingClassName);
            stringBuffer2.append("].");
            LogLog.error(stringBuffer2.toString());
            return;
        }
        try {
            Class renderedClass = Loader.loadClass(renderedClassName);
            repository.setRenderer(renderedClass, renderer);
        } catch (ClassNotFoundException e) {
            StringBuffer stringBuffer3 = new StringBuffer();
            stringBuffer3.append("Could not find class [");
            stringBuffer3.append(renderedClassName);
            stringBuffer3.append("].");
            LogLog.error(stringBuffer3.toString(), e);
        }
    }

    public String findAndRender(Object o) {
        if (o == null) {
            return null;
        }
        return get((Class) o.getClass()).doRender(o);
    }

    public ObjectRenderer get(Object o) {
        if (o == null) {
            return null;
        }
        return get((Class) o.getClass());
    }

    public ObjectRenderer get(Class clazz) {
        for (Class c = clazz; c != null; c = c.getSuperclass()) {
            ObjectRenderer r = (ObjectRenderer) this.map.get(c);
            if (r != null) {
                return r;
            }
            ObjectRenderer r2 = searchInterfaces(c);
            if (r2 != null) {
                return r2;
            }
        }
        return defaultRenderer;
    }

    ObjectRenderer searchInterfaces(Class c) {
        ObjectRenderer r = (ObjectRenderer) this.map.get(c);
        if (r != null) {
            return r;
        }
        Class[] ia = c.getInterfaces();
        for (Class cls : ia) {
            ObjectRenderer r2 = searchInterfaces(cls);
            if (r2 != null) {
                return r2;
            }
        }
        return null;
    }

    public ObjectRenderer getDefaultRenderer() {
        return defaultRenderer;
    }

    public void clear() {
        this.map.clear();
    }

    public void put(Class clazz, ObjectRenderer or) {
        this.map.put(clazz, or);
    }
}
