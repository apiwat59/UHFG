package org.apache.log4j.spi;

import java.io.Serializable;

/* loaded from: classes.dex */
public class ThrowableInformation implements Serializable {
    static final long serialVersionUID = -4748765566864322735L;
    private String[] rep;
    private transient Throwable throwable;

    public ThrowableInformation(Throwable throwable) {
        this.throwable = throwable;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public String[] getThrowableStrRep() {
        String[] strArr = this.rep;
        if (strArr != null) {
            return (String[]) strArr.clone();
        }
        VectorWriter vw = new VectorWriter();
        this.throwable.printStackTrace(vw);
        String[] stringArray = vw.toStringArray();
        this.rep = stringArray;
        return stringArray;
    }
}
