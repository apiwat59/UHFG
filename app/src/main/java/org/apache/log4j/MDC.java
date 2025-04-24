package org.apache.log4j;

import java.util.Hashtable;
import org.apache.log4j.helpers.Loader;
import org.apache.log4j.helpers.ThreadLocalMap;

/* loaded from: classes.dex */
public class MDC {
    static final int HT_SIZE = 7;
    static final MDC mdc = new MDC();
    boolean java1;
    Object tlm;

    private MDC() {
        boolean isJava1 = Loader.isJava1();
        this.java1 = isJava1;
        if (!isJava1) {
            this.tlm = new ThreadLocalMap();
        }
    }

    public static void put(String key, Object o) {
        mdc.put0(key, o);
    }

    public static Object get(String key) {
        return mdc.get0(key);
    }

    public static void remove(String key) {
        mdc.remove0(key);
    }

    public static Hashtable getContext() {
        return mdc.getContext0();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void put0(String key, Object o) {
        if (this.java1) {
            return;
        }
        Hashtable ht = (Hashtable) ((ThreadLocalMap) this.tlm).get();
        if (ht == null) {
            ht = new Hashtable(7);
            ((ThreadLocalMap) this.tlm).set(ht);
        }
        ht.put(key, o);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Object get0(String key) {
        Hashtable ht;
        if (this.java1 || (ht = (Hashtable) ((ThreadLocalMap) this.tlm).get()) == null || key == null) {
            return null;
        }
        return ht.get(key);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void remove0(String key) {
        Hashtable ht;
        if (!this.java1 && (ht = (Hashtable) ((ThreadLocalMap) this.tlm).get()) != null) {
            ht.remove(key);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Hashtable getContext0() {
        if (this.java1) {
            return null;
        }
        return (Hashtable) ((ThreadLocalMap) this.tlm).get();
    }
}
