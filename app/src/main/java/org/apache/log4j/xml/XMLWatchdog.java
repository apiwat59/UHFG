package org.apache.log4j.xml;

import org.apache.log4j.LogManager;
import org.apache.log4j.helpers.FileWatchdog;

/* compiled from: DOMConfigurator.java */
/* loaded from: classes.dex */
class XMLWatchdog extends FileWatchdog {
    XMLWatchdog(String filename) {
        super(filename);
    }

    @Override // org.apache.log4j.helpers.FileWatchdog
    public void doOnChange() {
        new DOMConfigurator().doConfigure(this.filename, LogManager.getLoggerRepository());
    }
}
