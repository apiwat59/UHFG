package org.apache.log4j;

/* loaded from: classes.dex */
class CategoryKey {
    static /* synthetic */ Class class$org$apache$log4j$CategoryKey;
    int hashCache;
    String name;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    CategoryKey(String name) {
        this.name = name;
        this.hashCache = name.hashCode();
    }

    public final int hashCode() {
        return this.hashCache;
    }

    public final boolean equals(Object rArg) {
        if (this == rArg) {
            return true;
        }
        if (rArg == null) {
            return false;
        }
        Class<?> cls = class$org$apache$log4j$CategoryKey;
        if (cls == null) {
            cls = class$("org.apache.log4j.CategoryKey");
            class$org$apache$log4j$CategoryKey = cls;
        }
        if (cls == rArg.getClass()) {
            return this.name.equals(((CategoryKey) rArg).name);
        }
        return false;
    }
}
