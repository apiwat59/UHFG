package org.apache.log4j.config;

/* loaded from: classes.dex */
public class PropertySetterException extends Exception {
    protected Throwable rootCause;

    public PropertySetterException(String msg) {
        super(msg);
    }

    public PropertySetterException(Throwable rootCause) {
        this.rootCause = rootCause;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        Throwable th;
        String msg = super.getMessage();
        if (msg == null && (th = this.rootCause) != null) {
            return th.getMessage();
        }
        return msg;
    }
}
