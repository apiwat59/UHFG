package jxl.common;

/* loaded from: classes.dex */
public class AssertionFailed extends RuntimeException {
    public AssertionFailed() {
        printStackTrace();
    }

    public AssertionFailed(String s) {
        super(s);
    }
}
