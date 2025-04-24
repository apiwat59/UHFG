package kotlinx.coroutines.internal;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.internal.Segment;

/* compiled from: SegmentQueue.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\f\b \u0018\u0000*\u000e\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u00028\u00000\u00012\u00020\u0003B\u0007¢\u0006\u0004\b\u0004\u0010\u0005J!\u0010\t\u001a\u0004\u0018\u00018\u00002\u0006\u0010\u0006\u001a\u00028\u00002\u0006\u0010\b\u001a\u00020\u0007H\u0004¢\u0006\u0004\b\t\u0010\nJ!\u0010\u000b\u001a\u0004\u0018\u00018\u00002\u0006\u0010\u0006\u001a\u00028\u00002\u0006\u0010\b\u001a\u00020\u0007H\u0004¢\u0006\u0004\b\u000b\u0010\nJ\u0017\u0010\u000e\u001a\u00020\r2\u0006\u0010\f\u001a\u00028\u0000H\u0002¢\u0006\u0004\b\u000e\u0010\u000fJ\u0017\u0010\u0010\u001a\u00020\r2\u0006\u0010\f\u001a\u00028\u0000H\u0002¢\u0006\u0004\b\u0010\u0010\u000fJ#\u0010\u0012\u001a\u00028\u00002\u0006\u0010\b\u001a\u00020\u00072\n\b\u0002\u0010\u0011\u001a\u0004\u0018\u00018\u0000H&¢\u0006\u0004\b\u0012\u0010\u0013R\u0016\u0010\u0016\u001a\u00028\u00008D@\u0004X\u0084\u0004¢\u0006\u0006\u001a\u0004\b\u0014\u0010\u0015R\u0016\u0010\u0018\u001a\u00028\u00008D@\u0004X\u0084\u0004¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0015¨\u0006\u0019"}, d2 = {"Lkotlinx/coroutines/internal/SegmentQueue;", "Lkotlinx/coroutines/internal/Segment;", "S", "", "<init>", "()V", "startFrom", "", "id", "getSegment", "(Lkotlinx/coroutines/internal/Segment;J)Lkotlinx/coroutines/internal/Segment;", "getSegmentAndMoveHead", "new", "", "moveHeadForward", "(Lkotlinx/coroutines/internal/Segment;)V", "moveTailForward", "prev", "newSegment", "(JLkotlinx/coroutines/internal/Segment;)Lkotlinx/coroutines/internal/Segment;", "getHead", "()Lkotlinx/coroutines/internal/Segment;", "head", "getTail", "tail", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public abstract class SegmentQueue<S extends Segment<S>> {
    private static final AtomicReferenceFieldUpdater _head$FU = AtomicReferenceFieldUpdater.newUpdater(SegmentQueue.class, Object.class, "_head");
    private static final AtomicReferenceFieldUpdater _tail$FU = AtomicReferenceFieldUpdater.newUpdater(SegmentQueue.class, Object.class, "_tail");
    private volatile Object _head;
    private volatile Object _tail;

    public abstract S newSegment(long id, S prev);

    public SegmentQueue() {
        Segment initialSegment = newSegment$default(this, 0L, null, 2, null);
        this._head = initialSegment;
        this._tail = initialSegment;
    }

    protected final S getHead() {
        return (S) this._head;
    }

    protected final S getTail() {
        return (S) this._tail;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ Segment newSegment$default(SegmentQueue segmentQueue, long j, Segment segment, int i, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: newSegment");
        }
        if ((i & 2) != 0) {
            segment = (Segment) null;
        }
        return segmentQueue.newSegment(j, segment);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v2, types: [kotlinx.coroutines.internal.Segment] */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v4 */
    protected final S getSegment(S startFrom, long id) {
        Segment next;
        Intrinsics.checkParameterIsNotNull(startFrom, "startFrom");
        S s = startFrom;
        while (s.getId() < id) {
            ?? next2 = s.getNext();
            if (next2 == 0) {
                S newSegment = newSegment(s.getId() + 1, s);
                if (s.casNext(null, newSegment)) {
                    if (s.getRemoved()) {
                        s.remove();
                    }
                    moveTailForward(newSegment);
                    next = newSegment;
                } else {
                    next = s.getNext();
                    if (next == null) {
                        Intrinsics.throwNpe();
                    }
                }
                next2 = next;
            }
            s = next2;
        }
        if (s.getId() != id) {
            return null;
        }
        return s;
    }

    protected final S getSegmentAndMoveHead(S startFrom, long id) {
        Intrinsics.checkParameterIsNotNull(startFrom, "startFrom");
        if (startFrom.getId() == id) {
            return startFrom;
        }
        S segment = getSegment(startFrom, id);
        if (segment == null) {
            return null;
        }
        moveHeadForward(segment);
        return segment;
    }

    private final void moveHeadForward(S r10) {
        Segment curHead;
        do {
            curHead = (Segment) this._head;
            if (curHead.getId() > r10.getId()) {
                return;
            }
        } while (!_head$FU.compareAndSet(this, curHead, r10));
        r10.prev = null;
    }

    private final void moveTailForward(S r10) {
        Segment curTail;
        do {
            curTail = (Segment) this._tail;
            if (curTail.getId() > r10.getId()) {
                return;
            }
        } while (!_tail$FU.compareAndSet(this, curTail, r10));
    }
}
