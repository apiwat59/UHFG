package kotlinx.coroutines.sync;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.CancellableContinuation;
import kotlinx.coroutines.internal.SegmentQueue;
import kotlinx.coroutines.internal.Symbol;

/* compiled from: Semaphore.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u0002\u0018\u00002\u00020\u00012\b\u0012\u0004\u0012\u00020\u00030\u0002B\u0017\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004¢\u0006\u0004\b\u0007\u0010\bJ\u0013\u0010\n\u001a\u00020\tH\u0096@ø\u0001\u0000¢\u0006\u0004\b\n\u0010\u000bJ\u0013\u0010\f\u001a\u00020\tH\u0082@ø\u0001\u0000¢\u0006\u0004\b\f\u0010\u000bJ\r\u0010\r\u001a\u00020\u0004¢\u0006\u0004\b\r\u0010\u000eJ!\u0010\u0012\u001a\u00020\u00032\u0006\u0010\u0010\u001a\u00020\u000f2\b\u0010\u0011\u001a\u0004\u0018\u00010\u0003H\u0016¢\u0006\u0004\b\u0012\u0010\u0013J\u000f\u0010\u0014\u001a\u00020\tH\u0016¢\u0006\u0004\b\u0014\u0010\u0015J\u000f\u0010\u0017\u001a\u00020\tH\u0000¢\u0006\u0004\b\u0016\u0010\u0015J\u000f\u0010\u0019\u001a\u00020\u0018H\u0016¢\u0006\u0004\b\u0019\u0010\u001aR\u0016\u0010\u001c\u001a\u00020\u00048V@\u0016X\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u000eR\u0016\u0010\u0005\u001a\u00020\u00048\u0002@\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u0005\u0010\u001d\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u001e"}, d2 = {"Lkotlinx/coroutines/sync/SemaphoreImpl;", "Lkotlinx/coroutines/sync/Semaphore;", "Lkotlinx/coroutines/internal/SegmentQueue;", "Lkotlinx/coroutines/sync/SemaphoreSegment;", "", "permits", "acquiredPermits", "<init>", "(II)V", "", "acquire", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addToQueueAndSuspend", "incPermits", "()I", "", "id", "prev", "newSegment", "(JLkotlinx/coroutines/sync/SemaphoreSegment;)Lkotlinx/coroutines/sync/SemaphoreSegment;", "release", "()V", "resumeNextFromQueue$kotlinx_coroutines_core", "resumeNextFromQueue", "", "tryAcquire", "()Z", "getAvailablePermits", "availablePermits", "I", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
final class SemaphoreImpl extends SegmentQueue<SemaphoreSegment> implements Semaphore {
    private volatile int _availablePermits;
    private volatile long deqIdx;
    volatile long enqIdx;
    private final int permits;
    private static final AtomicIntegerFieldUpdater _availablePermits$FU = AtomicIntegerFieldUpdater.newUpdater(SemaphoreImpl.class, "_availablePermits");
    static final AtomicLongFieldUpdater enqIdx$FU = AtomicLongFieldUpdater.newUpdater(SemaphoreImpl.class, "enqIdx");
    private static final AtomicLongFieldUpdater deqIdx$FU = AtomicLongFieldUpdater.newUpdater(SemaphoreImpl.class, "deqIdx");

    public SemaphoreImpl(int permits, int acquiredPermits) {
        this.permits = permits;
        if (!(permits > 0)) {
            throw new IllegalArgumentException(("Semaphore should have at least 1 permit, but had " + permits).toString());
        }
        if (acquiredPermits >= 0 && permits >= acquiredPermits) {
            this._availablePermits = permits - acquiredPermits;
            this.enqIdx = 0L;
            this.deqIdx = 0L;
        } else {
            throw new IllegalArgumentException(("The number of acquired permits should be in 0.." + permits).toString());
        }
    }

    public static final /* synthetic */ SemaphoreSegment access$getSegment(SemaphoreImpl $this, SemaphoreSegment startFrom, long id) {
        return $this.getSegment(startFrom, id);
    }

    public static final /* synthetic */ SemaphoreSegment access$getTail$p(SemaphoreImpl $this) {
        return $this.getTail();
    }

    @Override // kotlinx.coroutines.internal.SegmentQueue
    public SemaphoreSegment newSegment(long id, SemaphoreSegment prev) {
        return new SemaphoreSegment(id, prev);
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public int getAvailablePermits() {
        return Math.max(this._availablePermits, 0);
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public boolean tryAcquire() {
        int p;
        do {
            p = this._availablePermits;
            if (p <= 0) {
                return false;
            }
        } while (!_availablePermits$FU.compareAndSet(this, p, p - 1));
        return true;
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public Object acquire(Continuation<? super Unit> continuation) {
        int p = _availablePermits$FU.getAndDecrement(this);
        return p > 0 ? Unit.INSTANCE : addToQueueAndSuspend(continuation);
    }

    @Override // kotlinx.coroutines.sync.Semaphore
    public void release() {
        int p = incPermits();
        if (p >= 0) {
            return;
        }
        resumeNextFromQueue$kotlinx_coroutines_core();
    }

    public final int incPermits() {
        int cur$iv;
        int upd$iv;
        do {
            cur$iv = this._availablePermits;
            if (!(cur$iv < this.permits)) {
                throw new IllegalStateException(("The number of released permits cannot be greater than " + this.permits).toString());
            }
            upd$iv = cur$iv + 1;
        } while (!_availablePermits$FU.compareAndSet(this, cur$iv, upd$iv));
        return cur$iv;
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x0074  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    final /* synthetic */ java.lang.Object addToQueueAndSuspend(kotlin.coroutines.Continuation<? super kotlin.Unit> r17) {
        /*
            r16 = this;
            r0 = r16
            r1 = 0
            r2 = r17
            r3 = 0
            kotlinx.coroutines.CancellableContinuationImpl r4 = new kotlinx.coroutines.CancellableContinuationImpl
            kotlin.coroutines.Continuation r5 = kotlin.coroutines.intrinsics.IntrinsicsKt.intercepted(r2)
            r6 = 0
            r4.<init>(r5, r6)
            r5 = r4
            kotlinx.coroutines.CancellableContinuation r5 = (kotlinx.coroutines.CancellableContinuation) r5
            r6 = 0
            kotlinx.coroutines.sync.SemaphoreSegment r7 = access$getTail$p(r16)
            java.util.concurrent.atomic.AtomicLongFieldUpdater r8 = kotlinx.coroutines.sync.SemaphoreImpl.enqIdx$FU
            long r8 = r8.getAndIncrement(r0)
            int r10 = kotlinx.coroutines.sync.SemaphoreKt.access$getSEGMENT_SIZE$p()
            long r10 = (long) r10
            long r10 = r8 / r10
            kotlinx.coroutines.sync.SemaphoreSegment r10 = access$getSegment(r0, r7, r10)
            int r11 = kotlinx.coroutines.sync.SemaphoreKt.access$getSEGMENT_SIZE$p()
            long r11 = (long) r11
            long r11 = r8 % r11
            int r12 = (int) r11
            if (r10 == 0) goto L5b
            r11 = r10
            r13 = 0
            java.util.concurrent.atomic.AtomicReferenceArray r14 = r11.acquirers
            java.lang.Object r11 = r14.get(r12)
            kotlinx.coroutines.internal.Symbol r13 = kotlinx.coroutines.sync.SemaphoreKt.access$getRESUMED$p()
            if (r11 == r13) goto L5b
            r11 = 0
            r13 = r10
            r14 = 0
            java.util.concurrent.atomic.AtomicReferenceArray r15 = r13.acquirers
            boolean r11 = r15.compareAndSet(r12, r11, r5)
            if (r11 != 0) goto L4d
            goto L5b
        L4d:
            kotlinx.coroutines.sync.CancelSemaphoreAcquisitionHandler r11 = new kotlinx.coroutines.sync.CancelSemaphoreAcquisitionHandler
            r11.<init>(r0, r10, r12)
            kotlinx.coroutines.CancelHandlerBase r11 = (kotlinx.coroutines.CancelHandlerBase) r11
            r13 = 0
            kotlin.jvm.functions.Function1 r11 = (kotlin.jvm.functions.Function1) r11
            r5.invokeOnCancellation(r11)
            goto L6a
        L5b:
            r11 = r5
            kotlin.coroutines.Continuation r11 = (kotlin.coroutines.Continuation) r11
            kotlin.Unit r13 = kotlin.Unit.INSTANCE
            kotlin.Result$Companion r14 = kotlin.Result.INSTANCE
            java.lang.Object r13 = kotlin.Result.m16constructorimpl(r13)
            r11.resumeWith(r13)
        L6a:
            java.lang.Object r4 = r4.getResult()
            java.lang.Object r2 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            if (r4 != r2) goto L77
            kotlin.coroutines.jvm.internal.DebugProbesKt.probeCoroutineSuspended(r17)
        L77:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.sync.SemaphoreImpl.addToQueueAndSuspend(kotlin.coroutines.Continuation):java.lang.Object");
    }

    public final void resumeNextFromQueue$kotlinx_coroutines_core() {
        int i;
        int i2;
        Symbol symbol;
        Symbol symbol2;
        while (true) {
            SemaphoreSegment first = getHead();
            long deqIdx = deqIdx$FU.getAndIncrement(this);
            i = SemaphoreKt.SEGMENT_SIZE;
            SemaphoreSegment segment = getSegmentAndMoveHead(first, deqIdx / i);
            if (segment != null) {
                i2 = SemaphoreKt.SEGMENT_SIZE;
                int i3 = (int) (deqIdx % i2);
                symbol = SemaphoreKt.RESUMED;
                Object value$iv = segment.acquirers.getAndSet(i3, symbol);
                if (value$iv == null) {
                    return;
                }
                symbol2 = SemaphoreKt.CANCELLED;
                if (value$iv != symbol2) {
                    Unit unit = Unit.INSTANCE;
                    Result.Companion companion = Result.INSTANCE;
                    ((CancellableContinuation) value$iv).resumeWith(Result.m16constructorimpl(unit));
                    return;
                }
            }
        }
    }
}
