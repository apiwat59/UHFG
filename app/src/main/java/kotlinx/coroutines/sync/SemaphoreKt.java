package kotlinx.coroutines.sync;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.InlineMarker;
import kotlinx.coroutines.internal.Symbol;
import kotlinx.coroutines.internal.SystemPropsKt__SystemProps_commonKt;

/* compiled from: Semaphore.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\"\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00072\b\b\u0002\u0010\f\u001a\u00020\u0007\u001a)\u0010\r\u001a\u0002H\u000e\"\u0004\b\u0000\u0010\u000e*\u00020\n2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u0002H\u000e0\u0010H\u0086Hø\u0001\u0000¢\u0006\u0002\u0010\u0011\"\u0016\u0010\u0000\u001a\u00020\u00018\u0002X\u0083\u0004¢\u0006\b\n\u0000\u0012\u0004\b\u0002\u0010\u0003\"\u0016\u0010\u0004\u001a\u00020\u00018\u0002X\u0083\u0004¢\u0006\b\n\u0000\u0012\u0004\b\u0005\u0010\u0003\"\u0016\u0010\u0006\u001a\u00020\u00078\u0002X\u0083\u0004¢\u0006\b\n\u0000\u0012\u0004\b\b\u0010\u0003\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u0012"}, d2 = {"CANCELLED", "Lkotlinx/coroutines/internal/Symbol;", "CANCELLED$annotations", "()V", "RESUMED", "RESUMED$annotations", "SEGMENT_SIZE", "", "SEGMENT_SIZE$annotations", "Semaphore", "Lkotlinx/coroutines/sync/Semaphore;", "permits", "acquiredPermits", "withPermit", "T", "action", "Lkotlin/Function0;", "(Lkotlinx/coroutines/sync/Semaphore;Lkotlin/jvm/functions/Function0;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "kotlinx-coroutines-core"}, k = 2, mv = {1, 1, 15})
/* loaded from: classes.dex */
public final class SemaphoreKt {
    private static final int SEGMENT_SIZE;
    private static final Symbol RESUMED = new Symbol("RESUMED");
    private static final Symbol CANCELLED = new Symbol("CANCELLED");

    private static /* synthetic */ void CANCELLED$annotations() {
    }

    private static /* synthetic */ void RESUMED$annotations() {
    }

    private static /* synthetic */ void SEGMENT_SIZE$annotations() {
    }

    public static final Semaphore Semaphore(int permits, int acquiredPermits) {
        return new SemaphoreImpl(permits, acquiredPermits);
    }

    public static /* synthetic */ Semaphore Semaphore$default(int i, int i2, int i3, Object obj) {
        if ((i3 & 2) != 0) {
            i2 = 0;
        }
        return Semaphore(i, i2);
    }

    /* JADX WARN: Removed duplicated region for block: B:21:0x003d  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0024  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final <T> java.lang.Object withPermit(kotlinx.coroutines.sync.Semaphore r6, kotlin.jvm.functions.Function0<? extends T> r7, kotlin.coroutines.Continuation<? super T> r8) {
        /*
            boolean r0 = r8 instanceof kotlinx.coroutines.sync.SemaphoreKt$withPermit$1
            if (r0 == 0) goto L14
            r0 = r8
            kotlinx.coroutines.sync.SemaphoreKt$withPermit$1 r0 = (kotlinx.coroutines.sync.SemaphoreKt$withPermit$1) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 & r2
            if (r1 == 0) goto L14
            int r1 = r0.label
            int r1 = r1 - r2
            r0.label = r1
            goto L19
        L14:
            kotlinx.coroutines.sync.SemaphoreKt$withPermit$1 r0 = new kotlinx.coroutines.sync.SemaphoreKt$withPermit$1
            r0.<init>(r8)
        L19:
            java.lang.Object r1 = r0.result
            java.lang.Object r2 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r3 = r0.label
            r4 = 1
            if (r3 == 0) goto L3d
            if (r3 != r4) goto L35
            r2 = 0
            java.lang.Object r3 = r0.L$1
            r7 = r3
            kotlin.jvm.functions.Function0 r7 = (kotlin.jvm.functions.Function0) r7
            java.lang.Object r3 = r0.L$0
            r6 = r3
            kotlinx.coroutines.sync.Semaphore r6 = (kotlinx.coroutines.sync.Semaphore) r6
            kotlin.ResultKt.throwOnFailure(r1)
            goto L4f
        L35:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "call to 'resume' before 'invoke' with coroutine"
            r0.<init>(r1)
            throw r0
        L3d:
            kotlin.ResultKt.throwOnFailure(r1)
            r3 = 0
            r0.L$0 = r6
            r0.L$1 = r7
            r0.label = r4
            java.lang.Object r5 = r6.acquire(r0)
            if (r5 != r2) goto L4e
            return r2
        L4e:
            r2 = r3
        L4f:
            java.lang.Object r3 = r7.invoke()     // Catch: java.lang.Throwable -> L5e
            kotlin.jvm.internal.InlineMarker.finallyStart(r4)
            r6.release()
            kotlin.jvm.internal.InlineMarker.finallyEnd(r4)
            return r3
        L5e:
            r3 = move-exception
            kotlin.jvm.internal.InlineMarker.finallyStart(r4)
            r6.release()
            kotlin.jvm.internal.InlineMarker.finallyEnd(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.sync.SemaphoreKt.withPermit(kotlinx.coroutines.sync.Semaphore, kotlin.jvm.functions.Function0, kotlin.coroutines.Continuation):java.lang.Object");
    }

    private static final Object withPermit$$forInline(Semaphore $this$withPermit, Function0 action, Continuation continuation) {
        InlineMarker.mark(0);
        $this$withPermit.acquire(continuation);
        InlineMarker.mark(2);
        InlineMarker.mark(1);
        try {
            return action.invoke();
        } finally {
            InlineMarker.finallyStart(1);
            $this$withPermit.release();
            InlineMarker.finallyEnd(1);
        }
    }

    static {
        int systemProp$default;
        systemProp$default = SystemPropsKt__SystemProps_commonKt.systemProp$default("kotlinx.coroutines.semaphore.segmentSize", 16, 0, 0, 12, (Object) null);
        SEGMENT_SIZE = systemProp$default;
    }
}
