package kotlinx.coroutines.internal;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlinx.coroutines.DebugKt;
import kotlinx.coroutines.internal.Segment;

/* compiled from: SegmentQueue.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u000e\b \u0018\u0000*\u000e\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u00028\u00000\u00002\u00020\u0002B\u0019\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00018\u0000¢\u0006\u0004\b\u0006\u0010\u0007J!\u0010\u000b\u001a\u00020\n2\b\u0010\b\u001a\u0004\u0018\u00018\u00002\b\u0010\t\u001a\u0004\u0018\u00018\u0000¢\u0006\u0004\b\u000b\u0010\fJ\u0017\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00028\u0000H\u0002¢\u0006\u0004\b\u000f\u0010\u0010J\u0017\u0010\u0011\u001a\u00020\u000e2\u0006\u0010\u0005\u001a\u00028\u0000H\u0002¢\u0006\u0004\b\u0011\u0010\u0010J\r\u0010\u0012\u001a\u00020\u000e¢\u0006\u0004\b\u0012\u0010\u0013R\u0019\u0010\u0004\u001a\u00020\u00038\u0006@\u0006¢\u0006\f\n\u0004\b\u0004\u0010\u0014\u001a\u0004\b\u0015\u0010\u0016R\u0015\u0010\r\u001a\u0004\u0018\u00018\u00008F@\u0006¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0018R\u0016\u0010\u001b\u001a\u00020\n8&@&X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0019\u0010\u001a¨\u0006\u001c"}, d2 = {"Lkotlinx/coroutines/internal/Segment;", "S", "", "", "id", "prev", "<init>", "(JLkotlinx/coroutines/internal/Segment;)V", "expected", "value", "", "casNext", "(Lkotlinx/coroutines/internal/Segment;Lkotlinx/coroutines/internal/Segment;)Z", "next", "", "moveNextToRight", "(Lkotlinx/coroutines/internal/Segment;)V", "movePrevToLeft", "remove", "()V", "J", "getId", "()J", "getNext", "()Lkotlinx/coroutines/internal/Segment;", "getRemoved", "()Z", "removed", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public abstract class Segment<S extends Segment<S>> {
    private static final AtomicReferenceFieldUpdater _next$FU = AtomicReferenceFieldUpdater.newUpdater(Segment.class, Object.class, "_next");
    static final AtomicReferenceFieldUpdater prev$FU = AtomicReferenceFieldUpdater.newUpdater(Segment.class, Object.class, "prev");
    private volatile Object _next = null;
    private final long id;
    volatile Object prev;

    public abstract boolean getRemoved();

    public Segment(long id, S s) {
        this.id = id;
        this.prev = null;
        this.prev = s;
    }

    public final long getId() {
        return this.id;
    }

    public final S getNext() {
        return (S) this._next;
    }

    public final boolean casNext(S expected, S value) {
        return _next$FU.compareAndSet(this, expected, value);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v1, types: [kotlinx.coroutines.internal.Segment] */
    /* JADX WARN: Type inference failed for: r1v3, types: [kotlinx.coroutines.internal.Segment] */
    public final void remove() {
        ?? r1;
        Segment next;
        Segment segment;
        if (DebugKt.getASSERTIONS_ENABLED() && !getRemoved()) {
            throw new AssertionError();
        }
        Segment segment2 = (Segment) this._next;
        if (segment2 == null || (r1 = (Segment) this.prev) == 0) {
            return;
        }
        r1.moveNextToRight(segment2);
        S s = r1;
        while (s.getRemoved() && (segment = (Segment) s.prev) != null) {
            ?? r12 = segment;
            r12.moveNextToRight(segment2);
            s = r12;
        }
        segment2.movePrevToLeft(s);
        Segment segment3 = segment2;
        while (segment3.getRemoved() && (next = segment3.getNext()) != null) {
            Segment segment4 = next;
            segment4.movePrevToLeft(s);
            segment3 = segment4;
        }
    }

    private final void moveNextToRight(S next) {
        Segment curNext;
        do {
            Object obj = this._next;
            if (obj == null) {
                throw new TypeCastException("null cannot be cast to non-null type S");
            }
            curNext = (Segment) obj;
            if (next.id <= curNext.id) {
                return;
            }
        } while (!_next$FU.compareAndSet(this, curNext, next));
    }

    private final void movePrevToLeft(S prev) {
        Segment curPrev;
        do {
            curPrev = (Segment) this.prev;
            if (curPrev == null || curPrev.id <= prev.id) {
                return;
            }
        } while (!prev$FU.compareAndSet(this, curPrev, prev));
    }
}
