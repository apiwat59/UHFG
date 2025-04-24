package org.apache.log4j.spi;

/* loaded from: classes.dex */
public class DefaultRepositorySelector implements RepositorySelector {
    final LoggerRepository repository;

    public DefaultRepositorySelector(LoggerRepository repository) {
        this.repository = repository;
    }

    @Override // org.apache.log4j.spi.RepositorySelector
    public LoggerRepository getLoggerRepository() {
        return this.repository;
    }
}
