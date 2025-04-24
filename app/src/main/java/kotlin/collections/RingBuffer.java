package kotlin.collections;

import java.util.Arrays;
import java.util.Iterator;
import java.util.RandomAccess;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: SlidingWindow.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010(\n\u0002\b\b\b\u0002\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u00022\u00060\u0003j\u0002`\u0004B\r\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\u0013\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00028\u0000¢\u0006\u0002\u0010\u0015J\u0016\u0010\u0016\u001a\u00028\u00002\u0006\u0010\u0017\u001a\u00020\u0006H\u0096\u0002¢\u0006\u0002\u0010\u0018J\u0006\u0010\u0019\u001a\u00020\u001aJ\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00028\u00000\u001cH\u0096\u0002J\u000e\u0010\u001d\u001a\u00020\u00132\u0006\u0010\u001e\u001a\u00020\u0006J\u0015\u0010\u001f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\tH\u0014¢\u0006\u0002\u0010 J'\u0010\u001f\u001a\b\u0012\u0004\u0012\u0002H\u00010\t\"\u0004\b\u0001\u0010\u00012\f\u0010!\u001a\b\u0012\u0004\u0012\u0002H\u00010\tH\u0014¢\u0006\u0002\u0010\"J\u0015\u0010#\u001a\u00020\u0006*\u00020\u00062\u0006\u0010\u001e\u001a\u00020\u0006H\u0082\bR\u0018\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\n0\tX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001e\u0010\u000f\u001a\u00020\u00062\u0006\u0010\u000e\u001a\u00020\u0006@RX\u0096\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u000e\u0010\u0011\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006$"}, d2 = {"Lkotlin/collections/RingBuffer;", "T", "Lkotlin/collections/AbstractList;", "Ljava/util/RandomAccess;", "Lkotlin/collections/RandomAccess;", "capacity", "", "(I)V", "buffer", "", "", "[Ljava/lang/Object;", "getCapacity", "()I", "<set-?>", "size", "getSize", "startIndex", "add", "", "element", "(Ljava/lang/Object;)V", "get", "index", "(I)Ljava/lang/Object;", "isFull", "", "iterator", "", "removeFirst", "n", "toArray", "()[Ljava/lang/Object;", "array", "([Ljava/lang/Object;)[Ljava/lang/Object;", "forward", "kotlin-stdlib"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
final class RingBuffer<T> extends AbstractList<T> implements RandomAccess {
    private final Object[] buffer;
    private final int capacity;
    private int size;
    private int startIndex;

    public RingBuffer(int capacity) {
        this.capacity = capacity;
        if (capacity >= 0) {
            this.buffer = new Object[capacity];
            return;
        }
        throw new IllegalArgumentException(("ring buffer capacity should not be negative but it is " + capacity).toString());
    }

    public final int getCapacity() {
        return this.capacity;
    }

    @Override // kotlin.collections.AbstractList, kotlin.collections.AbstractCollection
    public int getSize() {
        return this.size;
    }

    @Override // kotlin.collections.AbstractList, java.util.List
    public T get(int index) {
        AbstractList.INSTANCE.checkElementIndex$kotlin_stdlib(index, size());
        return (T) this.buffer[(this.startIndex + index) % getCapacity()];
    }

    public final boolean isFull() {
        return size() == this.capacity;
    }

    @Override // kotlin.collections.AbstractList, kotlin.collections.AbstractCollection, java.util.Collection, java.lang.Iterable
    public Iterator<T> iterator() {
        return new AbstractIterator<T>() { // from class: kotlin.collections.RingBuffer$iterator$1
            private int count;
            private int index;

            {
                int i;
                this.count = RingBuffer.this.size();
                i = RingBuffer.this.startIndex;
                this.index = i;
            }

            /* JADX WARN: Multi-variable type inference failed */
            @Override // kotlin.collections.AbstractIterator
            protected void computeNext() {
                Object[] objArr;
                if (this.count != 0) {
                    objArr = RingBuffer.this.buffer;
                    setNext(objArr[this.index]);
                    RingBuffer this_$iv = RingBuffer.this;
                    int $this$forward$iv = this.index;
                    this.index = ($this$forward$iv + 1) % this_$iv.getCapacity();
                    this.count--;
                    return;
                }
                done();
            }
        };
    }

    @Override // kotlin.collections.AbstractCollection, java.util.Collection
    public <T> T[] toArray(T[] array) {
        Object[] objArr;
        Intrinsics.checkParameterIsNotNull(array, "array");
        if (array.length < size()) {
            objArr = (T[]) Arrays.copyOf(array, size());
            Intrinsics.checkExpressionValueIsNotNull(objArr, "java.util.Arrays.copyOf(this, newSize)");
        } else {
            objArr = array;
        }
        int size = size();
        int i = 0;
        for (int i2 = this.startIndex; i < size && i2 < this.capacity; i2++) {
            objArr[i] = this.buffer[i2];
            i++;
        }
        int i3 = 0;
        while (i < size) {
            objArr[i] = this.buffer[i3];
            i++;
            i3++;
        }
        if (objArr.length > size()) {
            objArr[size()] = null;
        }
        if (objArr != null) {
            return (T[]) objArr;
        }
        throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // kotlin.collections.AbstractCollection, java.util.Collection
    public Object[] toArray() {
        return toArray(new Object[size()]);
    }

    @Override // java.util.Collection, java.util.List
    public final void add(T element) {
        if (isFull()) {
            throw new IllegalStateException("ring buffer is full");
        }
        Object[] objArr = this.buffer;
        int $this$forward$iv = this.startIndex;
        int n$iv = size();
        objArr[($this$forward$iv + n$iv) % getCapacity()] = element;
        this.size = size() + 1;
    }

    public final void removeFirst(int n) {
        if (!(n >= 0)) {
            throw new IllegalArgumentException(("n shouldn't be negative but it is " + n).toString());
        }
        if (!(n <= size())) {
            throw new IllegalArgumentException(("n shouldn't be greater than the buffer size: n = " + n + ", size = " + size()).toString());
        }
        if (n > 0) {
            int start = this.startIndex;
            int end = (start + n) % getCapacity();
            if (start <= end) {
                ArraysKt.fill(this.buffer, (Object) null, start, end);
            } else {
                ArraysKt.fill(this.buffer, (Object) null, start, this.capacity);
                ArraysKt.fill(this.buffer, (Object) null, 0, end);
            }
            this.startIndex = end;
            this.size = size() - n;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int forward(int $this$forward, int n) {
        return ($this$forward + n) % getCapacity();
    }
}
