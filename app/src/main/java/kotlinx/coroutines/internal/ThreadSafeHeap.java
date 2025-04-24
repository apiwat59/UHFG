package kotlinx.coroutines.internal;

import java.lang.Comparable;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.DebugKt;
import kotlinx.coroutines.internal.ThreadSafeHeapNode;

/* compiled from: ThreadSafeHeap.common.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\u0011\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0018\b\u0017\u0018\u0000*\u0012\b\u0000\u0010\u0003*\u00020\u0001*\b\u0012\u0004\u0012\u00028\u00000\u00022\u00060\u0004j\u0002`\u0005B\u0007¢\u0006\u0004\b\u0006\u0010\u0007J\u0017\u0010\n\u001a\u00020\t2\u0006\u0010\b\u001a\u00028\u0000H\u0001¢\u0006\u0004\b\n\u0010\u000bJ\u0015\u0010\f\u001a\u00020\t2\u0006\u0010\b\u001a\u00028\u0000¢\u0006\u0004\b\f\u0010\u000bJ.\u0010\u0010\u001a\u00020\u000e2\u0006\u0010\b\u001a\u00028\u00002\u0014\u0010\u000f\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00018\u0000\u0012\u0004\u0012\u00020\u000e0\rH\u0086\b¢\u0006\u0004\b\u0010\u0010\u0011J\r\u0010\u0012\u001a\u00020\t¢\u0006\u0004\b\u0012\u0010\u0007J\u0011\u0010\u0013\u001a\u0004\u0018\u00018\u0000H\u0001¢\u0006\u0004\b\u0013\u0010\u0014J\u000f\u0010\u0015\u001a\u0004\u0018\u00018\u0000¢\u0006\u0004\b\u0015\u0010\u0014J\u0017\u0010\u0017\u001a\n\u0012\u0006\u0012\u0004\u0018\u00018\u00000\u0016H\u0002¢\u0006\u0004\b\u0017\u0010\u0018J\u0015\u0010\u0019\u001a\u00020\u000e2\u0006\u0010\b\u001a\u00028\u0000¢\u0006\u0004\b\u0019\u0010\u001aJ\u0017\u0010\u001d\u001a\u00028\u00002\u0006\u0010\u001c\u001a\u00020\u001bH\u0001¢\u0006\u0004\b\u001d\u0010\u001eJ&\u0010 \u001a\u0004\u0018\u00018\u00002\u0012\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00020\u000e0\rH\u0086\b¢\u0006\u0004\b \u0010!J\u000f\u0010\"\u001a\u0004\u0018\u00018\u0000¢\u0006\u0004\b\"\u0010\u0014J\u0018\u0010$\u001a\u00020\t2\u0006\u0010#\u001a\u00020\u001bH\u0082\u0010¢\u0006\u0004\b$\u0010%J\u0018\u0010&\u001a\u00020\t2\u0006\u0010#\u001a\u00020\u001bH\u0082\u0010¢\u0006\u0004\b&\u0010%J\u001f\u0010(\u001a\u00020\t2\u0006\u0010#\u001a\u00020\u001b2\u0006\u0010'\u001a\u00020\u001bH\u0002¢\u0006\u0004\b(\u0010)R \u0010*\u001a\f\u0012\u0006\u0012\u0004\u0018\u00018\u0000\u0018\u00010\u00168\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b*\u0010+R\u0013\u0010,\u001a\u00020\u000e8F@\u0006¢\u0006\u0006\u001a\u0004\b,\u0010-R$\u00102\u001a\u00020\u001b2\u0006\u0010.\u001a\u00020\u001b8F@BX\u0086\u000e¢\u0006\f\u001a\u0004\b/\u00100\"\u0004\b1\u0010%¨\u00063"}, d2 = {"Lkotlinx/coroutines/internal/ThreadSafeHeap;", "Lkotlinx/coroutines/internal/ThreadSafeHeapNode;", "", "T", "", "Lkotlinx/coroutines/internal/SynchronizedObject;", "<init>", "()V", "node", "", "addImpl", "(Lkotlinx/coroutines/internal/ThreadSafeHeapNode;)V", "addLast", "Lkotlin/Function1;", "", "cond", "addLastIf", "(Lkotlinx/coroutines/internal/ThreadSafeHeapNode;Lkotlin/jvm/functions/Function1;)Z", "clear", "firstImpl", "()Lkotlinx/coroutines/internal/ThreadSafeHeapNode;", "peek", "", "realloc", "()[Lkotlinx/coroutines/internal/ThreadSafeHeapNode;", "remove", "(Lkotlinx/coroutines/internal/ThreadSafeHeapNode;)Z", "", "index", "removeAtImpl", "(I)Lkotlinx/coroutines/internal/ThreadSafeHeapNode;", "predicate", "removeFirstIf", "(Lkotlin/jvm/functions/Function1;)Lkotlinx/coroutines/internal/ThreadSafeHeapNode;", "removeFirstOrNull", "i", "siftDownFrom", "(I)V", "siftUpFrom", "j", "swap", "(II)V", "a", "[Lkotlinx/coroutines/internal/ThreadSafeHeapNode;", "isEmpty", "()Z", "value", "getSize", "()I", "setSize", "size", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public class ThreadSafeHeap<T extends ThreadSafeHeapNode & Comparable<? super T>> {
    private static final AtomicIntegerFieldUpdater _size$FU = AtomicIntegerFieldUpdater.newUpdater(ThreadSafeHeap.class, "_size");
    private volatile int _size = 0;
    private T[] a;

    /* renamed from: getSize, reason: from getter */
    public final int get_size() {
        return this._size;
    }

    private final void setSize(int value) {
        this._size = value;
    }

    public final boolean isEmpty() {
        return get_size() == 0;
    }

    public final void clear() {
        synchronized (this) {
            ThreadSafeHeapNode[] it = this.a;
            if (it != null) {
                ArraysKt.fill$default(it, (Object) null, 0, 0, 6, (Object) null);
            }
            this._size = 0;
            Unit unit = Unit.INSTANCE;
        }
    }

    public final T peek() {
        T firstImpl;
        synchronized (this) {
            firstImpl = firstImpl();
        }
        return firstImpl;
    }

    public final T removeFirstOrNull() {
        T t;
        synchronized (this) {
            if (get_size() > 0) {
                t = removeAtImpl(0);
            } else {
                t = null;
            }
        }
        return t;
    }

    public final T removeFirstIf(Function1<? super T, Boolean> predicate) {
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        synchronized (this) {
            try {
                ThreadSafeHeapNode first = firstImpl();
                if (first == null) {
                    InlineMarker.finallyStart(2);
                    InlineMarker.finallyEnd(2);
                    return null;
                }
                T removeAtImpl = predicate.invoke(first).booleanValue() ? removeAtImpl(0) : null;
                InlineMarker.finallyStart(1);
                InlineMarker.finallyEnd(1);
                return removeAtImpl;
            } catch (Throwable th) {
                InlineMarker.finallyStart(1);
                InlineMarker.finallyEnd(1);
                throw th;
            }
        }
    }

    public final void addLast(T node) {
        Intrinsics.checkParameterIsNotNull(node, "node");
        synchronized (this) {
            addImpl(node);
            Unit unit = Unit.INSTANCE;
        }
    }

    public final boolean addLastIf(T node, Function1<? super T, Boolean> cond) {
        boolean z;
        Intrinsics.checkParameterIsNotNull(node, "node");
        Intrinsics.checkParameterIsNotNull(cond, "cond");
        synchronized (this) {
            try {
                if (cond.invoke(firstImpl()).booleanValue()) {
                    addImpl(node);
                    z = true;
                } else {
                    z = false;
                }
                InlineMarker.finallyStart(1);
            } catch (Throwable th) {
                InlineMarker.finallyStart(1);
                InlineMarker.finallyEnd(1);
                throw th;
            }
        }
        InlineMarker.finallyEnd(1);
        return z;
    }

    public final boolean remove(T node) {
        boolean z;
        Intrinsics.checkParameterIsNotNull(node, "node");
        synchronized (this) {
            z = true;
            if (node.getHeap() == null) {
                z = false;
            } else {
                int index = node.getIndex();
                if (DebugKt.getASSERTIONS_ENABLED()) {
                    if (!(index >= 0)) {
                        throw new AssertionError();
                    }
                }
                removeAtImpl(index);
            }
        }
        return z;
    }

    public final T firstImpl() {
        T[] tArr = this.a;
        if (tArr != null) {
            return tArr[0];
        }
        return null;
    }

    public final T removeAtImpl(int index) {
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if ((get_size() > 0 ? 1 : 0) == 0) {
                throw new AssertionError();
            }
        }
        T[] tArr = this.a;
        if (tArr == null) {
            Intrinsics.throwNpe();
        }
        setSize(get_size() - 1);
        if (index < get_size()) {
            swap(index, get_size());
            int i = (index - 1) / 2;
            if (index > 0) {
                Object obj = tArr[index];
                if (obj == null) {
                    Intrinsics.throwNpe();
                }
                Comparable comparable = (Comparable) obj;
                Object obj2 = tArr[i];
                if (obj2 == null) {
                    Intrinsics.throwNpe();
                }
                if (comparable.compareTo(obj2) < 0) {
                    swap(index, i);
                    siftUpFrom(i);
                }
            }
            siftDownFrom(index);
        }
        T t = (T) tArr[get_size()];
        if (t == null) {
            Intrinsics.throwNpe();
        }
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(t.getHeap() == this)) {
                throw new AssertionError();
            }
        }
        t.setHeap((ThreadSafeHeap) null);
        t.setIndex(-1);
        tArr[get_size()] = (ThreadSafeHeapNode) null;
        return t;
    }

    public final void addImpl(T node) {
        Intrinsics.checkParameterIsNotNull(node, "node");
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(node.getHeap() == null)) {
                throw new AssertionError();
            }
        }
        node.setHeap(this);
        ThreadSafeHeapNode[] a = realloc();
        int i = get_size();
        setSize(i + 1);
        a[i] = node;
        node.setIndex(i);
        siftUpFrom(i);
    }

    private final void siftUpFrom(int i) {
        while (i > 0) {
            T[] tArr = this.a;
            if (tArr == null) {
                Intrinsics.throwNpe();
            }
            int j = (i - 1) / 2;
            T t = tArr[j];
            if (t == null) {
                Intrinsics.throwNpe();
            }
            Comparable comparable = (Comparable) t;
            T t2 = tArr[i];
            if (t2 == null) {
                Intrinsics.throwNpe();
            }
            if (comparable.compareTo(t2) <= 0) {
                return;
            }
            swap(i, j);
            i = j;
        }
    }

    private final void siftDownFrom(int i) {
        while (true) {
            int j = (i * 2) + 1;
            if (j >= get_size()) {
                return;
            }
            T[] tArr = this.a;
            if (tArr == null) {
                Intrinsics.throwNpe();
            }
            if (j + 1 < get_size()) {
                T t = tArr[j + 1];
                if (t == null) {
                    Intrinsics.throwNpe();
                }
                Comparable comparable = (Comparable) t;
                T t2 = tArr[j];
                if (t2 == null) {
                    Intrinsics.throwNpe();
                }
                if (comparable.compareTo(t2) < 0) {
                    j++;
                }
            }
            T t3 = tArr[i];
            if (t3 == null) {
                Intrinsics.throwNpe();
            }
            Comparable comparable2 = (Comparable) t3;
            T t4 = tArr[j];
            if (t4 == null) {
                Intrinsics.throwNpe();
            }
            if (comparable2.compareTo(t4) <= 0) {
                return;
            }
            swap(i, j);
            i = j;
        }
    }

    private final T[] realloc() {
        T[] tArr = this.a;
        if (tArr == null) {
            T[] tArr2 = (T[]) new ThreadSafeHeapNode[4];
            this.a = tArr2;
            return tArr2;
        }
        if (get_size() < tArr.length) {
            return tArr;
        }
        Object[] copyOf = Arrays.copyOf(tArr, get_size() * 2);
        Intrinsics.checkExpressionValueIsNotNull(copyOf, "java.util.Arrays.copyOf(this, newSize)");
        this.a = (T[]) ((ThreadSafeHeapNode[]) copyOf);
        return (T[]) ((ThreadSafeHeapNode[]) copyOf);
    }

    private final void swap(int i, int j) {
        ThreadSafeHeapNode[] a = this.a;
        if (a == null) {
            Intrinsics.throwNpe();
        }
        ThreadSafeHeapNode ni = a[j];
        if (ni == null) {
            Intrinsics.throwNpe();
        }
        ThreadSafeHeapNode nj = a[i];
        if (nj == null) {
            Intrinsics.throwNpe();
        }
        a[i] = ni;
        a[j] = nj;
        ni.setIndex(i);
        nj.setIndex(j);
    }
}
