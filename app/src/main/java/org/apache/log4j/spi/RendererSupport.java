package org.apache.log4j.spi;

import org.apache.log4j.or.ObjectRenderer;
import org.apache.log4j.or.RendererMap;

/* loaded from: classes.dex */
public interface RendererSupport {
    RendererMap getRendererMap();

    void setRenderer(Class cls, ObjectRenderer objectRenderer);
}
