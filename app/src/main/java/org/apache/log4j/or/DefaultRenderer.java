package org.apache.log4j.or;

/* loaded from: classes.dex */
class DefaultRenderer implements ObjectRenderer {
    DefaultRenderer() {
    }

    @Override // org.apache.log4j.or.ObjectRenderer
    public String doRender(Object o) {
        return o.toString();
    }
}
