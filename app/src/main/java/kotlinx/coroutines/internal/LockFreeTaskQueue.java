package kotlinx.coroutines.internal;

import java.util.List;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.internal.LockFreeTaskQueueCore;

/* compiled from: LockFreeTaskQueue.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0004\b\u0010\u0018\u0000*\b\b\u0000\u0010\u0002*\u00020\u00012\u00020\u0001B\u000f\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u0015\u0010\b\u001a\u00020\u00032\u0006\u0010\u0007\u001a\u00028\u0000¢\u0006\u0004\b\b\u0010\tJ\r\u0010\u000b\u001a\u00020\n¢\u0006\u0004\b\u000b\u0010\fJ\r\u0010\r\u001a\u00020\u0003¢\u0006\u0004\b\r\u0010\u000eJ-\u0010\u0013\u001a\b\u0012\u0004\u0012\u00028\u00010\u0012\"\u0004\b\u0001\u0010\u000f2\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u0010¢\u0006\u0004\b\u0013\u0010\u0014J\u000f\u0010\u0015\u001a\u0004\u0018\u00018\u0000¢\u0006\u0004\b\u0015\u0010\u0016J&\u0010\u0018\u001a\u0004\u0018\u00018\u00002\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00020\u00030\u0010H\u0086\b¢\u0006\u0004\b\u0018\u0010\u0019R\u0013\u0010\u001a\u001a\u00020\u00038F@\u0006¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u000eR\u0013\u0010\u001e\u001a\u00020\u001b8F@\u0006¢\u0006\u0006\u001a\u0004\b\u001c\u0010\u001d¨\u0006\u001f"}, d2 = {"Lkotlinx/coroutines/internal/LockFreeTaskQueue;", "", "E", "", "singleConsumer", "<init>", "(Z)V", "element", "addLast", "(Ljava/lang/Object;)Z", "", "close", "()V", "isClosed", "()Z", "R", "Lkotlin/Function1;", "transform", "", "map", "(Lkotlin/jvm/functions/Function1;)Ljava/util/List;", "removeFirstOrNull", "()Ljava/lang/Object;", "predicate", "removeFirstOrNullIf", "(Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "isEmpty", "", "getSize", "()I", "size", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public class LockFreeTaskQueue<E> {
    public static final /* synthetic */ AtomicReferenceFieldUpdater _cur$FU$internal = AtomicReferenceFieldUpdater.newUpdater(LockFreeTaskQueue.class, Object.class, "_cur$internal");
    public volatile /* synthetic */ Object _cur$internal;

    public LockFreeTaskQueue(boolean singleConsumer) {
        this._cur$internal = new LockFreeTaskQueueCore(8, singleConsumer);
    }

    public final boolean isEmpty() {
        return ((LockFreeTaskQueueCore) this._cur$internal).isEmpty();
    }

    public final int getSize() {
        return ((LockFreeTaskQueueCore) this._cur$internal).getSize();
    }

    public final void close() {
        while (true) {
            LockFreeTaskQueueCore cur = (LockFreeTaskQueueCore) this._cur$internal;
            if (cur.close()) {
                return;
            } else {
                _cur$FU$internal.compareAndSet(this, cur, cur.next());
            }
        }
    }

    public final boolean addLast(E element) {
        Intrinsics.checkParameterIsNotNull(element, "element");
        while (true) {
            LockFreeTaskQueueCore cur = (LockFreeTaskQueueCore) this._cur$internal;
            int addLast = cur.addLast(element);
            if (addLast == 0) {
                return true;
            }
            if (addLast == 1) {
                _cur$FU$internal.compareAndSet(this, cur, cur.next());
            } else if (addLast == 2) {
                return false;
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:34:0x00cb, code lost:
    
        r1 = (E) r1;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final E removeFirstOrNull() {
        /*
            Method dump skipped, instructions count: 227
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.internal.LockFreeTaskQueue.removeFirstOrNull():java.lang.Object");
    }

    public final E removeFirstOrNullIf(Function1<? super E, Boolean> predicate) {
        int i;
        Object obj;
        Object obj2;
        Function1<? super E, Boolean> predicate2 = predicate;
        int i2 = 0;
        Intrinsics.checkParameterIsNotNull(predicate2, "predicate");
        while (true) {
            LockFreeTaskQueueCore lockFreeTaskQueueCore = (LockFreeTaskQueueCore) this._cur$internal;
            while (true) {
                long j = lockFreeTaskQueueCore._state$internal;
                i = i2;
                if ((LockFreeTaskQueueCore.FROZEN_MASK & j) == 0) {
                    LockFreeTaskQueueCore.Companion companion = LockFreeTaskQueueCore.INSTANCE;
                    int i3 = (int) ((j & LockFreeTaskQueueCore.HEAD_MASK) >> 0);
                    if ((((int) ((j & LockFreeTaskQueueCore.TAIL_MASK) >> 30)) & lockFreeTaskQueueCore.mask) != (i3 & lockFreeTaskQueueCore.mask)) {
                        obj2 = lockFreeTaskQueueCore.array$internal.get(lockFreeTaskQueueCore.mask & i3);
                        if (obj2 == null) {
                            if (lockFreeTaskQueueCore.singleConsumer) {
                                obj = null;
                                break;
                            }
                            predicate2 = predicate;
                            i2 = i;
                        } else if (!(obj2 instanceof LockFreeTaskQueueCore.Placeholder)) {
                            if (!predicate2.invoke(obj2).booleanValue()) {
                                obj = null;
                                break;
                            }
                            int i4 = (i3 + 1) & LockFreeTaskQueueCore.MAX_CAPACITY_MASK;
                            if (!LockFreeTaskQueueCore._state$FU$internal.compareAndSet(lockFreeTaskQueueCore, j, LockFreeTaskQueueCore.INSTANCE.updateHead(j, i4))) {
                                if (!lockFreeTaskQueueCore.singleConsumer) {
                                    predicate2 = predicate;
                                    i2 = i;
                                } else {
                                    LockFreeTaskQueueCore lockFreeTaskQueueCore2 = lockFreeTaskQueueCore;
                                    while (true) {
                                        LockFreeTaskQueueCore removeSlowPath = lockFreeTaskQueueCore2.removeSlowPath(i3, i4);
                                        if (removeSlowPath == null) {
                                            break;
                                        }
                                        lockFreeTaskQueueCore2 = removeSlowPath;
                                    }
                                }
                            } else {
                                lockFreeTaskQueueCore.array$internal.set(lockFreeTaskQueueCore.mask & i3, null);
                                break;
                            }
                        } else {
                            obj = null;
                            break;
                        }
                    } else {
                        obj = null;
                        break;
                    }
                } else {
                    obj = LockFreeTaskQueueCore.REMOVE_FROZEN;
                    break;
                }
            }
            obj = obj2;
            E e = (E) obj;
            if (e != LockFreeTaskQueueCore.REMOVE_FROZEN) {
                return e;
            }
            _cur$FU$internal.compareAndSet(this, lockFreeTaskQueueCore, lockFreeTaskQueueCore.next());
            predicate2 = predicate;
            i2 = i;
        }
    }

    public final <R> List<R> map(Function1<? super E, ? extends R> transform) {
        Intrinsics.checkParameterIsNotNull(transform, "transform");
        return ((LockFreeTaskQueueCore) this._cur$internal).map(transform);
    }

    public final boolean isClosed() {
        return ((LockFreeTaskQueueCore) this._cur$internal).isClosed();
    }
}
