package org.apache.log4j.spi;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;

/* loaded from: classes.dex */
public interface HierarchyEventListener {
    void addAppenderEvent(Category category, Appender appender);

    void removeAppenderEvent(Category category, Appender appender);
}
