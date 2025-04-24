package org.apache.log4j;

import org.apache.log4j.spi.LoggerFactory;

/* loaded from: classes.dex */
class DefaultCategoryFactory implements LoggerFactory {
    DefaultCategoryFactory() {
    }

    @Override // org.apache.log4j.spi.LoggerFactory
    public Logger makeNewLoggerInstance(String name) {
        return new Logger(name);
    }
}
