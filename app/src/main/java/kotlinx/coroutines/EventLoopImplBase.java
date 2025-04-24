package kotlinx.coroutines;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.LongCompanionObject;
import kotlin.ranges.RangesKt;
import kotlinx.coroutines.Delay;
import kotlinx.coroutines.internal.LockFreeTaskQueueCore;
import kotlinx.coroutines.internal.Symbol;
import kotlinx.coroutines.internal.ThreadSafeHeap;
import kotlinx.coroutines.internal.ThreadSafeHeapNode;

/* compiled from: EventLoop.common.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0011\b \u0018\u00002\u00020\u00012\u00020\u0002:\u00044567B\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\u000f\u0010\u0006\u001a\u00020\u0005H\u0002¢\u0006\u0004\b\u0006\u0010\u0004J\u0017\u0010\t\u001a\n\u0018\u00010\u0007j\u0004\u0018\u0001`\bH\u0002¢\u0006\u0004\b\t\u0010\nJ!\u0010\u000e\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\u000b2\n\u0010\r\u001a\u00060\u0007j\u0002`\b¢\u0006\u0004\b\u000e\u0010\u000fJ\u0019\u0010\u0011\u001a\u00020\u00052\n\u0010\u0010\u001a\u00060\u0007j\u0002`\b¢\u0006\u0004\b\u0011\u0010\u0012J\u001b\u0010\u0014\u001a\u00020\u00132\n\u0010\u0010\u001a\u00060\u0007j\u0002`\bH\u0002¢\u0006\u0004\b\u0014\u0010\u0015J\u000f\u0010\u0017\u001a\u00020\u0016H\u0016¢\u0006\u0004\b\u0017\u0010\u0018J\u000f\u0010\u0019\u001a\u00020\u0005H\u0002¢\u0006\u0004\b\u0019\u0010\u0004J\u000f\u0010\u001a\u001a\u00020\u0005H\u0004¢\u0006\u0004\b\u001a\u0010\u0004J\u001d\u0010\u001e\u001a\u00020\u00052\u0006\u0010\u001b\u001a\u00020\u00162\u0006\u0010\u001d\u001a\u00020\u001c¢\u0006\u0004\b\u001e\u0010\u001fJ\u001f\u0010!\u001a\u00020 2\u0006\u0010\u001b\u001a\u00020\u00162\u0006\u0010\u001d\u001a\u00020\u001cH\u0002¢\u0006\u0004\b!\u0010\"J#\u0010%\u001a\u00020$2\u0006\u0010#\u001a\u00020\u00162\n\u0010\r\u001a\u00060\u0007j\u0002`\bH\u0004¢\u0006\u0004\b%\u0010&J%\u0010)\u001a\u00020\u00052\u0006\u0010#\u001a\u00020\u00162\f\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00050'H\u0016¢\u0006\u0004\b)\u0010*J\u0017\u0010+\u001a\u00020\u00132\u0006\u0010\u0010\u001a\u00020\u001cH\u0002¢\u0006\u0004\b+\u0010,J\u000f\u0010-\u001a\u00020\u0005H\u0014¢\u0006\u0004\b-\u0010\u0004R\u0016\u0010.\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b.\u0010/R\u0016\u00100\u001a\u00020\u00138T@\u0014X\u0094\u0004¢\u0006\u0006\u001a\u0004\b0\u00101R\u0016\u00103\u001a\u00020\u00168T@\u0014X\u0094\u0004¢\u0006\u0006\u001a\u0004\b2\u0010\u0018¨\u00068"}, d2 = {"Lkotlinx/coroutines/EventLoopImplBase;", "Lkotlinx/coroutines/EventLoopImplPlatform;", "Lkotlinx/coroutines/Delay;", "<init>", "()V", "", "closeQueue", "Ljava/lang/Runnable;", "Lkotlinx/coroutines/Runnable;", "dequeue", "()Ljava/lang/Runnable;", "Lkotlin/coroutines/CoroutineContext;", "context", "block", "dispatch", "(Lkotlin/coroutines/CoroutineContext;Ljava/lang/Runnable;)V", "task", "enqueue", "(Ljava/lang/Runnable;)V", "", "enqueueImpl", "(Ljava/lang/Runnable;)Z", "", "processNextEvent", "()J", "rescheduleAllDelayed", "resetAll", "now", "Lkotlinx/coroutines/EventLoopImplBase$DelayedTask;", "delayedTask", "schedule", "(JLkotlinx/coroutines/EventLoopImplBase$DelayedTask;)V", "", "scheduleImpl", "(JLkotlinx/coroutines/EventLoopImplBase$DelayedTask;)I", "timeMillis", "Lkotlinx/coroutines/DisposableHandle;", "scheduleInvokeOnTimeout", "(JLjava/lang/Runnable;)Lkotlinx/coroutines/DisposableHandle;", "Lkotlinx/coroutines/CancellableContinuation;", "continuation", "scheduleResumeAfterDelay", "(JLkotlinx/coroutines/CancellableContinuation;)V", "shouldUnpark", "(Lkotlinx/coroutines/EventLoopImplBase$DelayedTask;)Z", "shutdown", "isCompleted", "Z", "isEmpty", "()Z", "getNextTime", "nextTime", "DelayedResumeTask", "DelayedRunnableTask", "DelayedTask", "DelayedTaskQueue", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public abstract class EventLoopImplBase extends EventLoopImplPlatform implements Delay {
    private volatile boolean isCompleted;
    private static final AtomicReferenceFieldUpdater _queue$FU = AtomicReferenceFieldUpdater.newUpdater(EventLoopImplBase.class, Object.class, "_queue");
    private static final AtomicReferenceFieldUpdater _delayed$FU = AtomicReferenceFieldUpdater.newUpdater(EventLoopImplBase.class, Object.class, "_delayed");
    private volatile Object _queue = null;
    private volatile Object _delayed = null;

    @Override // kotlinx.coroutines.Delay
    public Object delay(long time, Continuation<? super Unit> continuation) {
        return Delay.DefaultImpls.delay(this, time, continuation);
    }

    public DisposableHandle invokeOnTimeout(long timeMillis, Runnable block) {
        Intrinsics.checkParameterIsNotNull(block, "block");
        return Delay.DefaultImpls.invokeOnTimeout(this, timeMillis, block);
    }

    @Override // kotlinx.coroutines.EventLoop
    protected boolean isEmpty() {
        Symbol symbol;
        if (!isUnconfinedQueueEmpty()) {
            return false;
        }
        DelayedTaskQueue delayed = (DelayedTaskQueue) this._delayed;
        if (delayed != null && !delayed.isEmpty()) {
            return false;
        }
        Object queue = this._queue;
        if (queue == null) {
            return true;
        }
        if (queue instanceof LockFreeTaskQueueCore) {
            return ((LockFreeTaskQueueCore) queue).isEmpty();
        }
        symbol = EventLoop_commonKt.CLOSED_EMPTY;
        return queue == symbol;
    }

    @Override // kotlinx.coroutines.EventLoop
    protected long getNextTime() {
        DelayedTask nextDelayedTask;
        Symbol symbol;
        if (super.getNextTime() == 0) {
            return 0L;
        }
        Object queue = this._queue;
        if (queue != null) {
            if (!(queue instanceof LockFreeTaskQueueCore)) {
                symbol = EventLoop_commonKt.CLOSED_EMPTY;
                if (queue == symbol) {
                    return LongCompanionObject.MAX_VALUE;
                }
                return 0L;
            }
            if (!((LockFreeTaskQueueCore) queue).isEmpty()) {
                return 0L;
            }
        }
        DelayedTaskQueue delayedTaskQueue = (DelayedTaskQueue) this._delayed;
        if (delayedTaskQueue == null || (nextDelayedTask = delayedTaskQueue.peek()) == null) {
            return LongCompanionObject.MAX_VALUE;
        }
        long j = nextDelayedTask.nanoTime;
        TimeSource timeSource = TimeSourceKt.getTimeSource();
        return RangesKt.coerceAtLeast(j - (timeSource != null ? timeSource.nanoTime() : System.nanoTime()), 0L);
    }

    @Override // kotlinx.coroutines.EventLoop
    protected void shutdown() {
        ThreadLocalEventLoop.INSTANCE.resetEventLoop$kotlinx_coroutines_core();
        this.isCompleted = true;
        closeQueue();
        while (processNextEvent() <= 0) {
        }
        rescheduleAllDelayed();
    }

    @Override // kotlinx.coroutines.Delay
    /* renamed from: scheduleResumeAfterDelay */
    public void mo1009scheduleResumeAfterDelay(long timeMillis, CancellableContinuation<? super Unit> continuation) {
        Intrinsics.checkParameterIsNotNull(continuation, "continuation");
        long timeNanos = EventLoop_commonKt.delayToNanos(timeMillis);
        if (timeNanos < 4611686018427387903L) {
            TimeSource timeSource = TimeSourceKt.getTimeSource();
            long now = timeSource != null ? timeSource.nanoTime() : System.nanoTime();
            DelayedResumeTask task = new DelayedResumeTask(this, now + timeNanos, continuation);
            CancellableContinuationKt.disposeOnCancellation(continuation, task);
            schedule(now, task);
        }
    }

    protected final DisposableHandle scheduleInvokeOnTimeout(long timeMillis, Runnable block) {
        Intrinsics.checkParameterIsNotNull(block, "block");
        long timeNanos = EventLoop_commonKt.delayToNanos(timeMillis);
        if (timeNanos < 4611686018427387903L) {
            TimeSource timeSource = TimeSourceKt.getTimeSource();
            long now = timeSource != null ? timeSource.nanoTime() : System.nanoTime();
            DelayedRunnableTask task = new DelayedRunnableTask(now + timeNanos, block);
            schedule(now, task);
            return task;
        }
        return NonDisposableHandle.INSTANCE;
    }

    @Override // kotlinx.coroutines.EventLoop
    public long processNextEvent() {
        DelayedTask delayedTask;
        boolean z;
        if (processUnconfinedEvent()) {
            return getNextTime();
        }
        DelayedTaskQueue delayed = (DelayedTaskQueue) this._delayed;
        if (delayed != null && !delayed.isEmpty()) {
            TimeSource timeSource = TimeSourceKt.getTimeSource();
            long now = timeSource != null ? timeSource.nanoTime() : System.nanoTime();
            do {
                synchronized (delayed) {
                    ThreadSafeHeapNode first$iv = delayed.firstImpl();
                    if (first$iv != null) {
                        DelayedTask it = (DelayedTask) first$iv;
                        if (it.timeToExecute(now)) {
                            z = enqueueImpl(it);
                        } else {
                            z = false;
                        }
                        delayedTask = z ? delayed.removeAtImpl(0) : null;
                    }
                }
            } while (delayedTask != null);
        }
        Runnable dequeue = dequeue();
        if (dequeue != null) {
            dequeue.run();
        }
        return getNextTime();
    }

    @Override // kotlinx.coroutines.CoroutineDispatcher
    /* renamed from: dispatch */
    public final void mo1008dispatch(CoroutineContext context, Runnable block) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(block, "block");
        enqueue(block);
    }

    public final void enqueue(Runnable task) {
        Intrinsics.checkParameterIsNotNull(task, "task");
        if (enqueueImpl(task)) {
            unpark();
        } else {
            DefaultExecutor.INSTANCE.enqueue(task);
        }
    }

    private final boolean enqueueImpl(Runnable task) {
        Symbol symbol;
        while (true) {
            Object queue = this._queue;
            if (this.isCompleted) {
                return false;
            }
            if (queue == null) {
                if (_queue$FU.compareAndSet(this, null, task)) {
                    return true;
                }
            } else if (!(queue instanceof LockFreeTaskQueueCore)) {
                symbol = EventLoop_commonKt.CLOSED_EMPTY;
                if (queue == symbol) {
                    return false;
                }
                LockFreeTaskQueueCore newQueue = new LockFreeTaskQueueCore(8, true);
                if (queue == null) {
                    throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.Runnable /* = java.lang.Runnable */");
                }
                newQueue.addLast((Runnable) queue);
                newQueue.addLast(task);
                if (_queue$FU.compareAndSet(this, queue, newQueue)) {
                    return true;
                }
            } else {
                if (queue == null) {
                    throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.Queue<kotlinx.coroutines.Runnable /* = java.lang.Runnable */> /* = kotlinx.coroutines.internal.LockFreeTaskQueueCore<kotlinx.coroutines.Runnable /* = java.lang.Runnable */> */");
                }
                int addLast = ((LockFreeTaskQueueCore) queue).addLast(task);
                if (addLast != 0) {
                    if (addLast == 1) {
                        _queue$FU.compareAndSet(this, queue, ((LockFreeTaskQueueCore) queue).next());
                    } else if (addLast == 2) {
                        return false;
                    }
                } else {
                    return true;
                }
            }
        }
    }

    private final Runnable dequeue() {
        Symbol symbol;
        while (true) {
            Object queue = this._queue;
            if (queue == null) {
                return null;
            }
            if (!(queue instanceof LockFreeTaskQueueCore)) {
                symbol = EventLoop_commonKt.CLOSED_EMPTY;
                if (queue == symbol) {
                    return null;
                }
                if (_queue$FU.compareAndSet(this, queue, null)) {
                    if (queue != null) {
                        return (Runnable) queue;
                    }
                    throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.Runnable /* = java.lang.Runnable */");
                }
            } else {
                if (queue == null) {
                    throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.Queue<kotlinx.coroutines.Runnable /* = java.lang.Runnable */> /* = kotlinx.coroutines.internal.LockFreeTaskQueueCore<kotlinx.coroutines.Runnable /* = java.lang.Runnable */> */");
                }
                Object result = ((LockFreeTaskQueueCore) queue).removeFirstOrNull();
                if (result != LockFreeTaskQueueCore.REMOVE_FROZEN) {
                    return (Runnable) result;
                }
                _queue$FU.compareAndSet(this, queue, ((LockFreeTaskQueueCore) queue).next());
            }
        }
    }

    private final void closeQueue() {
        Symbol symbol;
        Symbol symbol2;
        if (DebugKt.getASSERTIONS_ENABLED() && !this.isCompleted) {
            throw new AssertionError();
        }
        while (true) {
            Object queue = this._queue;
            if (queue == null) {
                AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = _queue$FU;
                symbol = EventLoop_commonKt.CLOSED_EMPTY;
                if (atomicReferenceFieldUpdater.compareAndSet(this, null, symbol)) {
                    return;
                }
            } else if (!(queue instanceof LockFreeTaskQueueCore)) {
                symbol2 = EventLoop_commonKt.CLOSED_EMPTY;
                if (queue == symbol2) {
                    return;
                }
                LockFreeTaskQueueCore newQueue = new LockFreeTaskQueueCore(8, true);
                if (queue == null) {
                    throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.Runnable /* = java.lang.Runnable */");
                }
                newQueue.addLast((Runnable) queue);
                if (_queue$FU.compareAndSet(this, queue, newQueue)) {
                    return;
                }
            } else {
                ((LockFreeTaskQueueCore) queue).close();
                return;
            }
        }
    }

    public final void schedule(long now, DelayedTask delayedTask) {
        Intrinsics.checkParameterIsNotNull(delayedTask, "delayedTask");
        int scheduleImpl = scheduleImpl(now, delayedTask);
        if (scheduleImpl == 0) {
            if (shouldUnpark(delayedTask)) {
                unpark();
            }
        } else if (scheduleImpl == 1) {
            reschedule(now, delayedTask);
        } else if (scheduleImpl != 2) {
            throw new IllegalStateException("unexpected result".toString());
        }
    }

    private final boolean shouldUnpark(DelayedTask task) {
        DelayedTaskQueue delayedTaskQueue = (DelayedTaskQueue) this._delayed;
        return (delayedTaskQueue != null ? delayedTaskQueue.peek() : null) == task;
    }

    private final int scheduleImpl(long now, DelayedTask delayedTask) {
        if (this.isCompleted) {
            return 1;
        }
        DelayedTaskQueue delayedQueue = (DelayedTaskQueue) this._delayed;
        if (delayedQueue == null) {
            EventLoopImplBase $this$run = this;
            _delayed$FU.compareAndSet($this$run, null, new DelayedTaskQueue(now));
            Object obj = $this$run._delayed;
            if (obj == null) {
                Intrinsics.throwNpe();
            }
            delayedQueue = (DelayedTaskQueue) obj;
        }
        return delayedTask.scheduleTask(now, delayedQueue, this);
    }

    protected final void resetAll() {
        this._queue = null;
        this._delayed = null;
    }

    private final void rescheduleAllDelayed() {
        DelayedTask delayedTask;
        TimeSource timeSource = TimeSourceKt.getTimeSource();
        long now = timeSource != null ? timeSource.nanoTime() : System.nanoTime();
        while (true) {
            DelayedTaskQueue delayedTaskQueue = (DelayedTaskQueue) this._delayed;
            if (delayedTaskQueue != null && (delayedTask = delayedTaskQueue.removeFirstOrNull()) != null) {
                reschedule(now, delayedTask);
            } else {
                return;
            }
        }
    }

    /* compiled from: EventLoop.common.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\b \u0018\u00002\u00060\u0001j\u0002`\u00022\b\u0012\u0004\u0012\u00020\u00000\u00032\u00020\u00042\u00020\u0005B\r\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\u0011\u0010\u0018\u001a\u00020\u00132\u0006\u0010\u0019\u001a\u00020\u0000H\u0096\u0002J\u0006\u0010\u001a\u001a\u00020\u001bJ\u001e\u0010\u001c\u001a\u00020\u00132\u0006\u0010\u001d\u001a\u00020\u00072\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020!J\u000e\u0010\"\u001a\u00020#2\u0006\u0010\u001d\u001a\u00020\u0007J\b\u0010$\u001a\u00020%H\u0016R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n\u0000R0\u0010\r\u001a\b\u0012\u0002\b\u0003\u0018\u00010\f2\f\u0010\u000b\u001a\b\u0012\u0002\b\u0003\u0018\u00010\f8V@VX\u0096\u000e¢\u0006\f\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0012\u001a\u00020\u0013X\u0096\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u0012\u0010\u0006\u001a\u00020\u00078\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000¨\u0006&"}, d2 = {"Lkotlinx/coroutines/EventLoopImplBase$DelayedTask;", "Ljava/lang/Runnable;", "Lkotlinx/coroutines/Runnable;", "", "Lkotlinx/coroutines/DisposableHandle;", "Lkotlinx/coroutines/internal/ThreadSafeHeapNode;", "nanoTime", "", "(J)V", "_heap", "", "value", "Lkotlinx/coroutines/internal/ThreadSafeHeap;", "heap", "getHeap", "()Lkotlinx/coroutines/internal/ThreadSafeHeap;", "setHeap", "(Lkotlinx/coroutines/internal/ThreadSafeHeap;)V", "index", "", "getIndex", "()I", "setIndex", "(I)V", "compareTo", "other", "dispose", "", "scheduleTask", "now", "delayed", "Lkotlinx/coroutines/EventLoopImplBase$DelayedTaskQueue;", "eventLoop", "Lkotlinx/coroutines/EventLoopImplBase;", "timeToExecute", "", "toString", "", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    public static abstract class DelayedTask implements Runnable, Comparable<DelayedTask>, DisposableHandle, ThreadSafeHeapNode {
        private Object _heap;
        private int index = -1;
        public long nanoTime;

        public DelayedTask(long nanoTime) {
            this.nanoTime = nanoTime;
        }

        @Override // kotlinx.coroutines.internal.ThreadSafeHeapNode
        public ThreadSafeHeap<?> getHeap() {
            Object obj = this._heap;
            if (!(obj instanceof ThreadSafeHeap)) {
                obj = null;
            }
            return (ThreadSafeHeap) obj;
        }

        @Override // kotlinx.coroutines.internal.ThreadSafeHeapNode
        public void setHeap(ThreadSafeHeap<?> threadSafeHeap) {
            Symbol symbol;
            Object obj = this._heap;
            symbol = EventLoop_commonKt.DISPOSED_TASK;
            if (!(obj != symbol)) {
                throw new IllegalArgumentException("Failed requirement.".toString());
            }
            this._heap = threadSafeHeap;
        }

        @Override // kotlinx.coroutines.internal.ThreadSafeHeapNode
        public int getIndex() {
            return this.index;
        }

        @Override // kotlinx.coroutines.internal.ThreadSafeHeapNode
        public void setIndex(int i) {
            this.index = i;
        }

        @Override // java.lang.Comparable
        public int compareTo(DelayedTask other) {
            Intrinsics.checkParameterIsNotNull(other, "other");
            long dTime = this.nanoTime - other.nanoTime;
            if (dTime > 0) {
                return 1;
            }
            return dTime < 0 ? -1 : 0;
        }

        public final boolean timeToExecute(long now) {
            return now - this.nanoTime >= 0;
        }

        public final synchronized int scheduleTask(long now, DelayedTaskQueue delayed, EventLoopImplBase eventLoop) {
            Symbol symbol;
            Intrinsics.checkParameterIsNotNull(delayed, "delayed");
            Intrinsics.checkParameterIsNotNull(eventLoop, "eventLoop");
            Object obj = this._heap;
            symbol = EventLoop_commonKt.DISPOSED_TASK;
            if (obj == symbol) {
                return 2;
            }
            DelayedTask delayedTask = this;
            synchronized (delayed) {
                try {
                    DelayedTask firstTask = delayed.firstImpl();
                    if (eventLoop.isCompleted) {
                        return 1;
                    }
                    if (firstTask == null) {
                        try {
                            delayed.timeNow = now;
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    } else {
                        long firstTime = firstTask.nanoTime;
                        long minTime = firstTime - now >= 0 ? now : firstTime;
                        if (minTime - delayed.timeNow > 0) {
                            try {
                                delayed.timeNow = minTime;
                            } catch (Throwable th2) {
                                th = th2;
                                throw th;
                            }
                        }
                    }
                    if (this.nanoTime - delayed.timeNow < 0) {
                        this.nanoTime = delayed.timeNow;
                    }
                    delayed.addImpl(delayedTask);
                    return 0;
                } catch (Throwable th3) {
                    th = th3;
                }
            }
        }

        @Override // kotlinx.coroutines.DisposableHandle
        public final synchronized void dispose() {
            Symbol symbol;
            Symbol symbol2;
            Object heap = this._heap;
            symbol = EventLoop_commonKt.DISPOSED_TASK;
            if (heap == symbol) {
                return;
            }
            DelayedTaskQueue delayedTaskQueue = (DelayedTaskQueue) (!(heap instanceof DelayedTaskQueue) ? null : heap);
            if (delayedTaskQueue != null) {
                delayedTaskQueue.remove(this);
            }
            symbol2 = EventLoop_commonKt.DISPOSED_TASK;
            this._heap = symbol2;
        }

        public String toString() {
            return "Delayed[nanos=" + this.nanoTime + ']';
        }
    }

    /* compiled from: EventLoop.common.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0082\u0004\u0018\u00002\u00020\u0001B\u001b\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\u0002\u0010\u0007J\b\u0010\b\u001a\u00020\u0006H\u0016J\b\u0010\t\u001a\u00020\nH\u0016R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"}, d2 = {"Lkotlinx/coroutines/EventLoopImplBase$DelayedResumeTask;", "Lkotlinx/coroutines/EventLoopImplBase$DelayedTask;", "nanoTime", "", "cont", "Lkotlinx/coroutines/CancellableContinuation;", "", "(Lkotlinx/coroutines/EventLoopImplBase;JLkotlinx/coroutines/CancellableContinuation;)V", "run", "toString", "", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    private final class DelayedResumeTask extends DelayedTask {
        private final CancellableContinuation<Unit> cont;
        final /* synthetic */ EventLoopImplBase this$0;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        public DelayedResumeTask(EventLoopImplBase $outer, long nanoTime, CancellableContinuation<? super Unit> cont) {
            super(nanoTime);
            Intrinsics.checkParameterIsNotNull(cont, "cont");
            this.this$0 = $outer;
            this.cont = cont;
        }

        @Override // java.lang.Runnable
        public void run() {
            CancellableContinuation $this$with = this.cont;
            $this$with.resumeUndispatched(this.this$0, Unit.INSTANCE);
        }

        @Override // kotlinx.coroutines.EventLoopImplBase.DelayedTask
        public String toString() {
            return super.toString() + this.cont.toString();
        }
    }

    /* compiled from: EventLoop.common.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\u0010\u0004\u001a\u00060\u0005j\u0002`\u0006¢\u0006\u0002\u0010\u0007J\b\u0010\b\u001a\u00020\tH\u0016J\b\u0010\n\u001a\u00020\u000bH\u0016R\u0012\u0010\u0004\u001a\u00060\u0005j\u0002`\u0006X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\f"}, d2 = {"Lkotlinx/coroutines/EventLoopImplBase$DelayedRunnableTask;", "Lkotlinx/coroutines/EventLoopImplBase$DelayedTask;", "nanoTime", "", "block", "Ljava/lang/Runnable;", "Lkotlinx/coroutines/Runnable;", "(JLjava/lang/Runnable;)V", "run", "", "toString", "", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    private static final class DelayedRunnableTask extends DelayedTask {
        private final Runnable block;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public DelayedRunnableTask(long nanoTime, Runnable block) {
            super(nanoTime);
            Intrinsics.checkParameterIsNotNull(block, "block");
            this.block = block;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.block.run();
        }

        @Override // kotlinx.coroutines.EventLoopImplBase.DelayedTask
        public String toString() {
            return super.toString() + this.block.toString();
        }
    }

    /* compiled from: EventLoop.common.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\b\u0000\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005R\u0012\u0010\u0003\u001a\u00020\u00048\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000¨\u0006\u0006"}, d2 = {"Lkotlinx/coroutines/EventLoopImplBase$DelayedTaskQueue;", "Lkotlinx/coroutines/internal/ThreadSafeHeap;", "Lkotlinx/coroutines/EventLoopImplBase$DelayedTask;", "timeNow", "", "(J)V", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    public static final class DelayedTaskQueue extends ThreadSafeHeap<DelayedTask> {
        public long timeNow;

        public DelayedTaskQueue(long timeNow) {
            this.timeNow = timeNow;
        }
    }
}
