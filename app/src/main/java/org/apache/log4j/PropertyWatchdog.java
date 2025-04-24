package org.apache.log4j;

import org.apache.log4j.helpers.FileWatchdog;

/* compiled from: PropertyConfigurator.java */
/* loaded from: classes.dex */
class PropertyWatchdog extends FileWatchdog {
    PropertyWatchdog(String filename) {
        super(filename);
    }

    @Override // org.apache.log4j.helpers.FileWatchdog
    public void doOnChange() {
        new PropertyConfigurator().doConfigure(this.filename, LogManager.getLoggerRepository());
    }
}
