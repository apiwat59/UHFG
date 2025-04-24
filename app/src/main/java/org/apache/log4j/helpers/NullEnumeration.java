package org.apache.log4j.helpers;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/* loaded from: classes.dex */
public class NullEnumeration implements Enumeration {
    private static final NullEnumeration instance = new NullEnumeration();

    private NullEnumeration() {
    }

    public static NullEnumeration getInstance() {
        return instance;
    }

    @Override // java.util.Enumeration
    public boolean hasMoreElements() {
        return false;
    }

    @Override // java.util.Enumeration
    public Object nextElement() {
        throw new NoSuchElementException();
    }
}
