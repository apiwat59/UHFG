package kotlinx.coroutines.scheduling;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.locks.LockSupport;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlinx.coroutines.DebugKt;
import kotlinx.coroutines.DebugStringsKt;
import kotlinx.coroutines.TimeSourceKt;
import kotlinx.coroutines.internal.Symbol;
import kotlinx.coroutines.internal.SystemPropsKt__SystemProps_commonKt;

/* compiled from: CoroutineScheduler.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000r\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b!\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\b\u0006\b\u0000\u0018\u0000 U2\u00020\u00012\u00020\u0002:\u0003UVWB+\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0006\u0012\b\b\u0002\u0010\t\u001a\u00020\b¢\u0006\u0004\b\n\u0010\u000bJ\u0018\u0010\r\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\u0006H\u0082\b¢\u0006\u0004\b\r\u0010\u000eJ\u000f\u0010\u0010\u001a\u00020\u000fH\u0016¢\u0006\u0004\b\u0010\u0010\u0011J\u000f\u0010\u0012\u001a\u00020\u0003H\u0002¢\u0006\u0004\b\u0012\u0010\u0013J#\u0010\u001c\u001a\u00020\u00192\n\u0010\u0016\u001a\u00060\u0014j\u0002`\u00152\u0006\u0010\u0018\u001a\u00020\u0017H\u0000¢\u0006\u0004\b\u001a\u0010\u001bJ\u0018\u0010\u001d\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\u0006H\u0082\b¢\u0006\u0004\b\u001d\u0010\u000eJ\u0015\u0010\u001f\u001a\b\u0018\u00010\u001eR\u00020\u0000H\u0002¢\u0006\u0004\b\u001f\u0010 J\u0010\u0010!\u001a\u00020\u000fH\u0082\b¢\u0006\u0004\b!\u0010\u0011J\u0010\u0010\"\u001a\u00020\u0003H\u0082\b¢\u0006\u0004\b\"\u0010\u0013J-\u0010%\u001a\u00020\u000f2\n\u0010\u0016\u001a\u00060\u0014j\u0002`\u00152\b\b\u0002\u0010\u0018\u001a\u00020\u00172\b\b\u0002\u0010$\u001a\u00020#¢\u0006\u0004\b%\u0010&J\u001b\u0010(\u001a\u00020\u000f2\n\u0010'\u001a\u00060\u0014j\u0002`\u0015H\u0016¢\u0006\u0004\b(\u0010)J\u0010\u0010*\u001a\u00020\u000fH\u0082\b¢\u0006\u0004\b*\u0010\u0011J\u0010\u0010+\u001a\u00020\u0003H\u0082\b¢\u0006\u0004\b+\u0010\u0013J\u001b\u0010-\u001a\u00020\u00032\n\u0010,\u001a\u00060\u001eR\u00020\u0000H\u0002¢\u0006\u0004\b-\u0010.J\u0015\u0010/\u001a\b\u0018\u00010\u001eR\u00020\u0000H\u0002¢\u0006\u0004\b/\u0010 J\u001b\u00100\u001a\u00020\u000f2\n\u0010,\u001a\u00060\u001eR\u00020\u0000H\u0002¢\u0006\u0004\b0\u00101J+\u00104\u001a\u00020\u000f2\n\u0010,\u001a\u00060\u001eR\u00020\u00002\u0006\u00102\u001a\u00020\u00032\u0006\u00103\u001a\u00020\u0003H\u0002¢\u0006\u0004\b4\u00105J\u000f\u00106\u001a\u00020\u000fH\u0002¢\u0006\u0004\b6\u0010\u0011J\u0017\u00108\u001a\u00020\u000f2\u0006\u00107\u001a\u00020\u0019H\u0002¢\u0006\u0004\b8\u00109J\u0015\u0010;\u001a\u00020\u000f2\u0006\u0010:\u001a\u00020\u0006¢\u0006\u0004\b;\u0010<J\u001f\u0010=\u001a\u00020\u00032\u0006\u00107\u001a\u00020\u00192\u0006\u0010$\u001a\u00020#H\u0002¢\u0006\u0004\b=\u0010>J\u000f\u0010?\u001a\u00020\bH\u0016¢\u0006\u0004\b?\u0010@J\u000f\u0010A\u001a\u00020#H\u0002¢\u0006\u0004\bA\u0010BR\u0017\u0010\r\u001a\u00020\u00038Â\u0002@\u0002X\u0082\u0004¢\u0006\u0006\u001a\u0004\bC\u0010\u0013R\u0016\u0010\u0004\u001a\u00020\u00038\u0002@\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u0004\u0010DR\u0016\u0010F\u001a\u00020E8\u0002@\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\bF\u0010GR\u0017\u0010\u001d\u001a\u00020\u00038Â\u0002@\u0002X\u0082\u0004¢\u0006\u0006\u001a\u0004\bH\u0010\u0013R\u0016\u0010J\u001a\u00020I8\u0002@\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\bJ\u0010KR\u0016\u0010\u0007\u001a\u00020\u00068\u0002@\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u0007\u0010LR\u0016\u0010M\u001a\u00020#8B@\u0002X\u0082\u0004¢\u0006\u0006\u001a\u0004\bM\u0010BR\u0016\u0010\u0005\u001a\u00020\u00038\u0002@\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u0005\u0010DR\u0016\u0010O\u001a\u00020N8\u0002@\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\bO\u0010PR\u0016\u0010\t\u001a\u00020\b8\u0002@\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\t\u0010QR\"\u0010S\u001a\u000e\u0012\n\u0012\b\u0018\u00010\u001eR\u00020\u00000R8\u0002@\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\bS\u0010T¨\u0006X"}, d2 = {"Lkotlinx/coroutines/scheduling/CoroutineScheduler;", "Ljava/util/concurrent/Executor;", "Ljava/io/Closeable;", "", "corePoolSize", "maxPoolSize", "", "idleWorkerKeepAliveNs", "", "schedulerName", "<init>", "(IIJLjava/lang/String;)V", "state", "blockingWorkers", "(J)I", "", "close", "()V", "createNewWorker", "()I", "Ljava/lang/Runnable;", "Lkotlinx/coroutines/Runnable;", "block", "Lkotlinx/coroutines/scheduling/TaskContext;", "taskContext", "Lkotlinx/coroutines/scheduling/Task;", "createTask$kotlinx_coroutines_core", "(Ljava/lang/Runnable;Lkotlinx/coroutines/scheduling/TaskContext;)Lkotlinx/coroutines/scheduling/Task;", "createTask", "createdWorkers", "Lkotlinx/coroutines/scheduling/CoroutineScheduler$Worker;", "currentWorker", "()Lkotlinx/coroutines/scheduling/CoroutineScheduler$Worker;", "decrementBlockingWorkers", "decrementCreatedWorkers", "", "fair", "dispatch", "(Ljava/lang/Runnable;Lkotlinx/coroutines/scheduling/TaskContext;Z)V", "command", "execute", "(Ljava/lang/Runnable;)V", "incrementBlockingWorkers", "incrementCreatedWorkers", "worker", "parkedWorkersStackNextIndex", "(Lkotlinx/coroutines/scheduling/CoroutineScheduler$Worker;)I", "parkedWorkersStackPop", "parkedWorkersStackPush", "(Lkotlinx/coroutines/scheduling/CoroutineScheduler$Worker;)V", "oldIndex", "newIndex", "parkedWorkersStackTopUpdate", "(Lkotlinx/coroutines/scheduling/CoroutineScheduler$Worker;II)V", "requestCpuWorker", "task", "runSafely", "(Lkotlinx/coroutines/scheduling/Task;)V", "timeout", "shutdown", "(J)V", "submitToLocalQueue", "(Lkotlinx/coroutines/scheduling/Task;Z)I", "toString", "()Ljava/lang/String;", "tryUnpark", "()Z", "getBlockingWorkers", "I", "Ljava/util/concurrent/Semaphore;", "cpuPermits", "Ljava/util/concurrent/Semaphore;", "getCreatedWorkers", "Lkotlinx/coroutines/scheduling/GlobalQueue;", "globalQueue", "Lkotlinx/coroutines/scheduling/GlobalQueue;", "J", "isTerminated", "Ljava/util/Random;", "random", "Ljava/util/Random;", "Ljava/lang/String;", "", "workers", "[Lkotlinx/coroutines/scheduling/CoroutineScheduler$Worker;", "Companion", "Worker", "WorkerState", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public final class CoroutineScheduler implements Executor, Closeable {
    private static final int ADDED = -1;
    private static final int ADDED_REQUIRES_HELP = 0;
    private static final int ALLOWED = 0;
    private static final long BLOCKING_MASK = 4398044413952L;
    private static final int BLOCKING_SHIFT = 21;
    private static final long CREATED_MASK = 2097151;

    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final int FORBIDDEN = -1;
    private static final int MAX_PARK_TIME_NS;
    private static final int MAX_SPINS;
    public static final int MAX_SUPPORTED_POOL_SIZE = 2097150;
    private static final int MAX_YIELDS;
    private static final int MIN_PARK_TIME_NS;
    public static final int MIN_SUPPORTED_POOL_SIZE = 1;
    private static final int NOT_ADDED = 1;
    private static final Symbol NOT_IN_STACK;
    private static final long PARKED_INDEX_MASK = 2097151;
    private static final long PARKED_VERSION_INC = 2097152;
    private static final long PARKED_VERSION_MASK = -2097152;
    private static final int TERMINATED = 1;
    private static final AtomicIntegerFieldUpdater _isTerminated$FU;
    static final AtomicLongFieldUpdater controlState$FU;
    private static final AtomicLongFieldUpdater parkedWorkersStack$FU;
    private volatile int _isTerminated;
    volatile long controlState;
    private final int corePoolSize;
    private final Semaphore cpuPermits;
    private final GlobalQueue globalQueue;
    private final long idleWorkerKeepAliveNs;
    private final int maxPoolSize;
    private volatile long parkedWorkersStack;
    private final Random random;
    private final String schedulerName;
    private final Worker[] workers;

    @Metadata(bv = {1, 0, 3}, k = 3, mv = {1, 1, 15})
    public final /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[WorkerState.values().length];
            $EnumSwitchMapping$0 = iArr;
            iArr[WorkerState.PARKING.ordinal()] = 1;
            iArr[WorkerState.BLOCKING.ordinal()] = 2;
            iArr[WorkerState.CPU_ACQUIRED.ordinal()] = 3;
            iArr[WorkerState.RETIRING.ordinal()] = 4;
            iArr[WorkerState.TERMINATED.ordinal()] = 5;
        }
    }

    /* compiled from: CoroutineScheduler.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0007\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007¨\u0006\b"}, d2 = {"Lkotlinx/coroutines/scheduling/CoroutineScheduler$WorkerState;", "", "(Ljava/lang/String;I)V", "CPU_ACQUIRED", "BLOCKING", "PARKING", "RETIRING", "TERMINATED", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    public enum WorkerState {
        CPU_ACQUIRED,
        BLOCKING,
        PARKING,
        RETIRING,
        TERMINATED
    }

    public CoroutineScheduler(int corePoolSize, int maxPoolSize, long idleWorkerKeepAliveNs, String schedulerName) {
        Intrinsics.checkParameterIsNotNull(schedulerName, "schedulerName");
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.idleWorkerKeepAliveNs = idleWorkerKeepAliveNs;
        this.schedulerName = schedulerName;
        if (!(corePoolSize >= 1)) {
            throw new IllegalArgumentException(("Core pool size " + corePoolSize + " should be at least 1").toString());
        }
        if (!(maxPoolSize >= corePoolSize)) {
            throw new IllegalArgumentException(("Max pool size " + maxPoolSize + " should be greater than or equals to core pool size " + corePoolSize).toString());
        }
        if (!(maxPoolSize <= 2097150)) {
            throw new IllegalArgumentException(("Max pool size " + maxPoolSize + " should not exceed maximal supported number of threads 2097150").toString());
        }
        if (!(idleWorkerKeepAliveNs > 0)) {
            throw new IllegalArgumentException(("Idle worker keep alive time " + idleWorkerKeepAliveNs + " must be positive").toString());
        }
        this.globalQueue = new GlobalQueue();
        this.cpuPermits = new Semaphore(corePoolSize, false);
        this.parkedWorkersStack = 0L;
        this.workers = new Worker[maxPoolSize + 1];
        this.controlState = 0L;
        this.random = new Random();
        this._isTerminated = 0;
    }

    public /* synthetic */ CoroutineScheduler(int i, int i2, long j, String str, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        this(i, i2, (i3 & 4) != 0 ? TasksKt.IDLE_WORKER_KEEP_ALIVE_NS : j, (i3 & 8) != 0 ? TasksKt.DEFAULT_SCHEDULER_NAME : str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void parkedWorkersStackTopUpdate(Worker worker, int oldIndex, int newIndex) {
        int i;
        while (true) {
            long top = this.parkedWorkersStack;
            int index = (int) (2097151 & top);
            long updVersion = (2097152 + top) & PARKED_VERSION_MASK;
            if (index == oldIndex) {
                if (newIndex != 0) {
                    i = newIndex;
                } else {
                    i = parkedWorkersStackNextIndex(worker);
                }
            } else {
                i = index;
            }
            int updIndex = i;
            if (updIndex >= 0 && parkedWorkersStack$FU.compareAndSet(this, top, updVersion | updIndex)) {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void parkedWorkersStackPush(Worker worker) {
        long top;
        long updVersion;
        int updIndex;
        if (worker.getNextParkedWorker() != NOT_IN_STACK) {
            return;
        }
        do {
            top = this.parkedWorkersStack;
            int index = (int) (2097151 & top);
            updVersion = (2097152 + top) & PARKED_VERSION_MASK;
            updIndex = worker.getIndexInArray();
            if (DebugKt.getASSERTIONS_ENABLED()) {
                if (!(updIndex != 0)) {
                    throw new AssertionError();
                }
            }
            worker.setNextParkedWorker(this.workers[index]);
        } while (!parkedWorkersStack$FU.compareAndSet(this, top, updVersion | updIndex));
    }

    private final Worker parkedWorkersStackPop() {
        while (true) {
            long top = this.parkedWorkersStack;
            int index = (int) (2097151 & top);
            Worker worker = this.workers[index];
            if (worker == null) {
                return null;
            }
            long updVersion = (2097152 + top) & PARKED_VERSION_MASK;
            int updIndex = parkedWorkersStackNextIndex(worker);
            if (updIndex >= 0 && parkedWorkersStack$FU.compareAndSet(this, top, updVersion | updIndex)) {
                worker.setNextParkedWorker(NOT_IN_STACK);
                return worker;
            }
        }
    }

    private final int parkedWorkersStackNextIndex(Worker worker) {
        Object next = worker.getNextParkedWorker();
        while (next != NOT_IN_STACK) {
            if (next == null) {
                return 0;
            }
            Worker nextWorker = (Worker) next;
            int updIndex = nextWorker.getIndexInArray();
            if (updIndex != 0) {
                return updIndex;
            }
            next = nextWorker.getNextParkedWorker();
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int getCreatedWorkers() {
        return (int) (this.controlState & 2097151);
    }

    private final int getBlockingWorkers() {
        return (int) ((this.controlState & BLOCKING_MASK) >> 21);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int createdWorkers(long state) {
        return (int) (2097151 & state);
    }

    private final int blockingWorkers(long state) {
        return (int) ((BLOCKING_MASK & state) >> 21);
    }

    private final int incrementCreatedWorkers() {
        long state$iv = controlState$FU.incrementAndGet(this);
        return (int) (2097151 & state$iv);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int decrementCreatedWorkers() {
        long state$iv = controlState$FU.getAndDecrement(this);
        return (int) (2097151 & state$iv);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void incrementBlockingWorkers() {
        controlState$FU.addAndGet(this, 2097152L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void decrementBlockingWorkers() {
        controlState$FU.addAndGet(this, PARKED_VERSION_MASK);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean isTerminated() {
        return this._isTerminated != 0;
    }

    /* compiled from: CoroutineScheduler.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u0016\u0010\f\u001a\u00020\u00048\u0002X\u0083\u0004¢\u0006\b\n\u0000\u0012\u0004\b\r\u0010\u0002R\u000e\u0010\u000e\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0004X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0011\u001a\u00020\u00048\u0002X\u0083\u0004¢\u0006\b\n\u0000\u0012\u0004\b\u0012\u0010\u0002R\u000e\u0010\u0013\u001a\u00020\u0004X\u0080T¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u001b"}, d2 = {"Lkotlinx/coroutines/scheduling/CoroutineScheduler$Companion;", "", "()V", "ADDED", "", "ADDED_REQUIRES_HELP", "ALLOWED", "BLOCKING_MASK", "", "BLOCKING_SHIFT", "CREATED_MASK", "FORBIDDEN", "MAX_PARK_TIME_NS", "MAX_PARK_TIME_NS$annotations", "MAX_SPINS", "MAX_SUPPORTED_POOL_SIZE", "MAX_YIELDS", "MIN_PARK_TIME_NS", "MIN_PARK_TIME_NS$annotations", "MIN_SUPPORTED_POOL_SIZE", "NOT_ADDED", "NOT_IN_STACK", "Lkotlinx/coroutines/internal/Symbol;", "PARKED_INDEX_MASK", "PARKED_VERSION_INC", "PARKED_VERSION_MASK", "TERMINATED", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    public static final class Companion {
        @JvmStatic
        private static /* synthetic */ void MAX_PARK_TIME_NS$annotations() {
        }

        @JvmStatic
        private static /* synthetic */ void MIN_PARK_TIME_NS$annotations() {
        }

        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }

    static {
        int systemProp$default;
        int systemProp$default2;
        systemProp$default = SystemPropsKt__SystemProps_commonKt.systemProp$default("kotlinx.coroutines.scheduler.spins", 1000, 1, 0, 8, (Object) null);
        MAX_SPINS = systemProp$default;
        systemProp$default2 = SystemPropsKt__SystemProps_commonKt.systemProp$default("kotlinx.coroutines.scheduler.yields", 0, 0, 0, 8, (Object) null);
        MAX_YIELDS = systemProp$default + systemProp$default2;
        int nanos = (int) TimeUnit.SECONDS.toNanos(1L);
        MAX_PARK_TIME_NS = nanos;
        MIN_PARK_TIME_NS = (int) RangesKt.coerceAtMost(RangesKt.coerceAtLeast(TasksKt.WORK_STEALING_TIME_RESOLUTION_NS / 4, 10L), nanos);
        NOT_IN_STACK = new Symbol("NOT_IN_STACK");
        parkedWorkersStack$FU = AtomicLongFieldUpdater.newUpdater(CoroutineScheduler.class, "parkedWorkersStack");
        controlState$FU = AtomicLongFieldUpdater.newUpdater(CoroutineScheduler.class, "controlState");
        _isTerminated$FU = AtomicIntegerFieldUpdater.newUpdater(CoroutineScheduler.class, "_isTerminated");
    }

    @Override // java.util.concurrent.Executor
    public void execute(Runnable command) {
        Intrinsics.checkParameterIsNotNull(command, "command");
        dispatch$default(this, command, null, false, 6, null);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        shutdown(10000L);
    }

    /* JADX WARN: Code restructure failed: missing block: B:40:0x0076, code lost:
    
        if (r4 != null) goto L41;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void shutdown(long r13) {
        /*
            r12 = this;
            java.util.concurrent.atomic.AtomicIntegerFieldUpdater r0 = kotlinx.coroutines.scheduling.CoroutineScheduler._isTerminated$FU
            r1 = 0
            r2 = 1
            boolean r0 = r0.compareAndSet(r12, r1, r2)
            if (r0 != 0) goto Lb
            return
        Lb:
            kotlinx.coroutines.scheduling.CoroutineScheduler$Worker r0 = r12.currentWorker()
            kotlinx.coroutines.scheduling.CoroutineScheduler$Worker[] r3 = r12.workers
            r4 = 0
            monitor-enter(r3)
            r5 = 0
            r6 = r12
            r7 = 0
            long r8 = r6.controlState     // Catch: java.lang.Throwable -> Lb2
            r10 = 2097151(0x1fffff, double:1.0361303E-317)
            long r8 = r8 & r10
            int r6 = (int) r8
            monitor-exit(r3)
            r3 = r6
            if (r2 > r3) goto L6a
            r4 = r2
        L24:
            kotlinx.coroutines.scheduling.CoroutineScheduler$Worker[] r5 = r12.workers
            r5 = r5[r4]
            if (r5 != 0) goto L2d
            kotlin.jvm.internal.Intrinsics.throwNpe()
        L2d:
            if (r5 == r0) goto L65
        L2f:
            boolean r6 = r5.isAlive()
            if (r6 == 0) goto L3f
            r6 = r5
            java.lang.Thread r6 = (java.lang.Thread) r6
            java.util.concurrent.locks.LockSupport.unpark(r6)
            r5.join(r13)
            goto L2f
        L3f:
            kotlinx.coroutines.scheduling.CoroutineScheduler$WorkerState r6 = r5.getState()
            boolean r7 = kotlinx.coroutines.DebugKt.getASSERTIONS_ENABLED()
            if (r7 == 0) goto L5c
            r7 = 0
            kotlinx.coroutines.scheduling.CoroutineScheduler$WorkerState r8 = kotlinx.coroutines.scheduling.CoroutineScheduler.WorkerState.TERMINATED
            if (r6 != r8) goto L50
            r7 = 1
            goto L51
        L50:
            r7 = 0
        L51:
            if (r7 == 0) goto L54
            goto L5c
        L54:
            java.lang.AssertionError r1 = new java.lang.AssertionError
            r1.<init>()
            java.lang.Throwable r1 = (java.lang.Throwable) r1
            throw r1
        L5c:
            kotlinx.coroutines.scheduling.WorkQueue r7 = r5.getLocalQueue()
            kotlinx.coroutines.scheduling.GlobalQueue r8 = r12.globalQueue
            r7.offloadAllWork$kotlinx_coroutines_core(r8)
        L65:
            if (r4 == r3) goto L6a
            int r4 = r4 + 1
            goto L24
        L6a:
            kotlinx.coroutines.scheduling.GlobalQueue r4 = r12.globalQueue
            r4.close()
        L6f:
            if (r0 == 0) goto L79
            kotlinx.coroutines.scheduling.Task r4 = r0.findTask$kotlinx_coroutines_core()
            if (r4 == 0) goto L79
            goto L81
        L79:
            kotlinx.coroutines.scheduling.GlobalQueue r4 = r12.globalQueue
            java.lang.Object r4 = r4.removeFirstOrNull()
            kotlinx.coroutines.scheduling.Task r4 = (kotlinx.coroutines.scheduling.Task) r4
        L81:
            if (r4 == 0) goto L87
            r12.runSafely(r4)
            goto L6f
        L87:
            if (r0 == 0) goto L8e
            kotlinx.coroutines.scheduling.CoroutineScheduler$WorkerState r4 = kotlinx.coroutines.scheduling.CoroutineScheduler.WorkerState.TERMINATED
            r0.tryReleaseCpu$kotlinx_coroutines_core(r4)
        L8e:
            boolean r4 = kotlinx.coroutines.DebugKt.getASSERTIONS_ENABLED()
            if (r4 == 0) goto Lab
            r4 = 0
            java.util.concurrent.Semaphore r5 = r12.cpuPermits
            int r5 = r5.availablePermits()
            int r6 = r12.corePoolSize
            if (r5 != r6) goto La0
            r1 = 1
        La0:
            if (r1 == 0) goto La3
            goto Lab
        La3:
            java.lang.AssertionError r1 = new java.lang.AssertionError
            r1.<init>()
            java.lang.Throwable r1 = (java.lang.Throwable) r1
            throw r1
        Lab:
            r1 = 0
            r12.parkedWorkersStack = r1
            r12.controlState = r1
            return
        Lb2:
            r1 = move-exception
            monitor-exit(r3)
            goto Lb6
        Lb5:
            throw r1
        Lb6:
            goto Lb5
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.scheduling.CoroutineScheduler.shutdown(long):void");
    }

    public static /* synthetic */ void dispatch$default(CoroutineScheduler coroutineScheduler, Runnable runnable, TaskContext taskContext, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            taskContext = NonBlockingContext.INSTANCE;
        }
        if ((i & 4) != 0) {
            z = false;
        }
        coroutineScheduler.dispatch(runnable, taskContext, z);
    }

    public final void dispatch(Runnable block, TaskContext taskContext, boolean fair) {
        Intrinsics.checkParameterIsNotNull(block, "block");
        Intrinsics.checkParameterIsNotNull(taskContext, "taskContext");
        kotlinx.coroutines.TimeSource timeSource = TimeSourceKt.getTimeSource();
        if (timeSource != null) {
            timeSource.trackTask();
        }
        Task task = createTask$kotlinx_coroutines_core(block, taskContext);
        int submitToLocalQueue = submitToLocalQueue(task, fair);
        if (submitToLocalQueue != -1) {
            if (submitToLocalQueue == 1) {
                if (!this.globalQueue.addLast(task)) {
                    throw new RejectedExecutionException(this.schedulerName + " was terminated");
                }
                requestCpuWorker();
                return;
            }
            requestCpuWorker();
        }
    }

    public final Task createTask$kotlinx_coroutines_core(Runnable block, TaskContext taskContext) {
        Intrinsics.checkParameterIsNotNull(block, "block");
        Intrinsics.checkParameterIsNotNull(taskContext, "taskContext");
        long nanoTime = TasksKt.schedulerTimeSource.nanoTime();
        if (block instanceof Task) {
            ((Task) block).submissionTime = nanoTime;
            ((Task) block).taskContext = taskContext;
            return (Task) block;
        }
        return new TaskImpl(block, nanoTime, taskContext);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void requestCpuWorker() {
        if (this.cpuPermits.availablePermits() == 0) {
            tryUnpark();
            return;
        }
        if (tryUnpark()) {
            return;
        }
        long state = this.controlState;
        int created = (int) (2097151 & state);
        int blocking = (int) ((BLOCKING_MASK & state) >> 21);
        int cpuWorkers = created - blocking;
        if (cpuWorkers < this.corePoolSize) {
            int newCpuWorkers = createNewWorker();
            if (newCpuWorkers == 1 && this.corePoolSize > 1) {
                createNewWorker();
            }
            if (newCpuWorkers > 0) {
                return;
            }
        }
        tryUnpark();
    }

    private final boolean tryUnpark() {
        while (true) {
            Worker worker = parkedWorkersStackPop();
            if (worker == null) {
                return false;
            }
            worker.idleResetBeforeUnpark();
            boolean wasParking = worker.isParking();
            LockSupport.unpark(worker);
            if (wasParking && worker.tryForbidTermination()) {
                return true;
            }
        }
    }

    private final int createNewWorker() {
        Object lock$iv = this.workers;
        synchronized (lock$iv) {
            if (isTerminated()) {
                return -1;
            }
            long state = this.controlState;
            int created = (int) (state & 2097151);
            int blocking = (int) ((BLOCKING_MASK & state) >> 21);
            int cpuWorkers = created - blocking;
            if (cpuWorkers >= this.corePoolSize) {
                return 0;
            }
            if (created < this.maxPoolSize && this.cpuPermits.availablePermits() != 0) {
                int newIndex = ((int) (this.controlState & 2097151)) + 1;
                if (newIndex > 0 && this.workers[newIndex] == null) {
                    Worker worker = new Worker(this, newIndex);
                    worker.start();
                    long state$iv$iv = controlState$FU.incrementAndGet(this);
                    if (!(newIndex == ((int) (state$iv$iv & 2097151)))) {
                        throw new IllegalArgumentException("Failed requirement.".toString());
                    }
                    this.workers[newIndex] = worker;
                    return cpuWorkers + 1;
                }
                throw new IllegalArgumentException("Failed requirement.".toString());
            }
            return 0;
        }
    }

    private final int submitToLocalQueue(Task task, boolean fair) {
        boolean noOffloadingHappened;
        Worker worker = currentWorker();
        if (worker == null || worker.getState() == WorkerState.TERMINATED) {
            return 1;
        }
        int result = -1;
        if (task.getMode() == TaskMode.NON_BLOCKING) {
            if (worker.isBlocking()) {
                result = 0;
            } else {
                boolean hasPermit = worker.tryAcquireCpuPermit();
                if (!hasPermit) {
                    return 1;
                }
            }
        }
        if (fair) {
            noOffloadingHappened = worker.getLocalQueue().addLast(task, this.globalQueue);
        } else {
            noOffloadingHappened = worker.getLocalQueue().add(task, this.globalQueue);
        }
        if (!noOffloadingHappened || worker.getLocalQueue().getBufferSize$kotlinx_coroutines_core() > TasksKt.QUEUE_SIZE_OFFLOAD_THRESHOLD) {
            return 0;
        }
        return result;
    }

    private final Worker currentWorker() {
        Thread currentThread = Thread.currentThread();
        if (!(currentThread instanceof Worker)) {
            currentThread = null;
        }
        Worker it = (Worker) currentThread;
        if (it == null || !Intrinsics.areEqual(it.getThis$0(), this)) {
            return null;
        }
        return it;
    }

    public String toString() {
        int parkedWorkers = 0;
        int blockingWorkers = 0;
        int cpuWorkers = 0;
        int retired = 0;
        int terminated = 0;
        ArrayList queueSizes = new ArrayList();
        for (Worker worker : this.workers) {
            if (worker != null) {
                int queueSize = worker.getLocalQueue().size$kotlinx_coroutines_core();
                int i = WhenMappings.$EnumSwitchMapping$0[worker.getState().ordinal()];
                if (i == 1) {
                    parkedWorkers++;
                } else if (i == 2) {
                    blockingWorkers++;
                    queueSizes.add(String.valueOf(queueSize) + "b");
                } else if (i == 3) {
                    cpuWorkers++;
                    queueSizes.add(String.valueOf(queueSize) + "c");
                } else if (i == 4) {
                    retired++;
                    if (queueSize > 0) {
                        queueSizes.add(String.valueOf(queueSize) + "r");
                    }
                } else if (i == 5) {
                    terminated++;
                }
            }
        }
        long state = this.controlState;
        return this.schedulerName + '@' + DebugStringsKt.getHexAddress(this) + "[Pool Size {core = " + this.corePoolSize + ", max = " + this.maxPoolSize + "}, Worker States {CPU = " + cpuWorkers + ", blocking = " + blockingWorkers + ", parked = " + parkedWorkers + ", retired = " + retired + ", terminated = " + terminated + "}, running workers queues = " + queueSizes + ", global queue size = " + this.globalQueue.getSize() + ", Control State Workers {created = " + ((int) (2097151 & state)) + ", blocking = " + ((int) ((BLOCKING_MASK & state) >> 21)) + "}]";
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void runSafely(Task task) {
        try {
            task.run();
        } catch (Throwable e) {
            try {
                Thread thread = Thread.currentThread();
                Intrinsics.checkExpressionValueIsNotNull(thread, "thread");
                thread.getUncaughtExceptionHandler().uncaughtException(thread, e);
                kotlinx.coroutines.TimeSource timeSource = TimeSourceKt.getTimeSource();
                if (timeSource == null) {
                }
            } finally {
                kotlinx.coroutines.TimeSource timeSource2 = TimeSourceKt.getTimeSource();
                if (timeSource2 != null) {
                    timeSource2.unTrackTask();
                }
            }
        }
    }

    /* compiled from: CoroutineScheduler.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\f\b\u0080\u0004\u0018\u00002\u00020\u0001B\u0011\b\u0016\u0012\u0006\u0010\u0003\u001a\u00020\u0002¢\u0006\u0004\b\u0004\u0010\u0005B\t\b\u0002¢\u0006\u0004\b\u0004\u0010\u0006J\u0017\u0010\n\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\u0007H\u0002¢\u0006\u0004\b\n\u0010\u000bJ\u001f\u0010\u000e\u001a\u00020\t2\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\r\u001a\u00020\fH\u0002¢\u0006\u0004\b\u000e\u0010\u000fJ\u000f\u0010\u0011\u001a\u00020\u0010H\u0002¢\u0006\u0004\b\u0011\u0010\u0012J\u000f\u0010\u0013\u001a\u00020\tH\u0002¢\u0006\u0004\b\u0013\u0010\u0014J\u000f\u0010\u0015\u001a\u00020\tH\u0002¢\u0006\u0004\b\u0015\u0010\u0014J\u0017\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0016\u001a\u00020\fH\u0002¢\u0006\u0004\b\u0017\u0010\u0018J\u0011\u0010\u001c\u001a\u0004\u0018\u00010\u0019H\u0000¢\u0006\u0004\b\u001a\u0010\u001bJ\u0011\u0010\u001d\u001a\u0004\u0018\u00010\u0019H\u0002¢\u0006\u0004\b\u001d\u0010\u001bJ\u0017\u0010\u001f\u001a\u00020\t2\u0006\u0010\u001e\u001a\u00020\u0007H\u0002¢\u0006\u0004\b\u001f\u0010\u000bJ\r\u0010 \u001a\u00020\t¢\u0006\u0004\b \u0010\u0014J\u0017\u0010$\u001a\u00020\u00022\u0006\u0010!\u001a\u00020\u0002H\u0000¢\u0006\u0004\b\"\u0010#J\u000f\u0010%\u001a\u00020\tH\u0016¢\u0006\u0004\b%\u0010\u0014J\r\u0010&\u001a\u00020\u0010¢\u0006\u0004\b&\u0010\u0012J\r\u0010'\u001a\u00020\u0010¢\u0006\u0004\b'\u0010\u0012J\u0017\u0010,\u001a\u00020\u00102\u0006\u0010)\u001a\u00020(H\u0000¢\u0006\u0004\b*\u0010+J\u0011\u0010-\u001a\u0004\u0018\u00010\u0019H\u0002¢\u0006\u0004\b-\u0010\u001bJ\u000f\u0010.\u001a\u00020\tH\u0002¢\u0006\u0004\b.\u0010\u0014R*\u0010/\u001a\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u00028\u0006@FX\u0086\u000e¢\u0006\u0012\n\u0004\b/\u00100\u001a\u0004\b1\u00102\"\u0004\b3\u00104R\u0013\u00105\u001a\u00020\u00108F@\u0006¢\u0006\u0006\u001a\u0004\b5\u0010\u0012R\u0013\u00106\u001a\u00020\u00108F@\u0006¢\u0006\u0006\u001a\u0004\b6\u0010\u0012R\u0016\u00107\u001a\u00020\f8\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b7\u00108R\u0016\u00109\u001a\u00020\u00028\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b9\u00100R\u0019\u0010;\u001a\u00020:8\u0006@\u0006¢\u0006\f\n\u0004\b;\u0010<\u001a\u0004\b=\u0010>R$\u0010@\u001a\u0004\u0018\u00010?8\u0006@\u0006X\u0086\u000e¢\u0006\u0012\n\u0004\b@\u0010A\u001a\u0004\bB\u0010C\"\u0004\bD\u0010ER\u0016\u0010F\u001a\u00020\u00028\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\bF\u00100R\u0016\u0010G\u001a\u00020\u00028\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\bG\u00100R\u0013\u0010K\u001a\u00020H8F@\u0006¢\u0006\u0006\u001a\u0004\bI\u0010JR\u0016\u0010L\u001a\u00020\u00028\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\bL\u00100R\"\u0010M\u001a\u00020(8\u0006@\u0006X\u0086\u000e¢\u0006\u0012\n\u0004\bM\u0010N\u001a\u0004\bO\u0010P\"\u0004\bQ\u0010RR\u0016\u0010S\u001a\u00020\f8\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\bS\u00108¨\u0006T"}, d2 = {"Lkotlinx/coroutines/scheduling/CoroutineScheduler$Worker;", "Ljava/lang/Thread;", "", "index", "<init>", "(Lkotlinx/coroutines/scheduling/CoroutineScheduler;I)V", "(Lkotlinx/coroutines/scheduling/CoroutineScheduler;)V", "Lkotlinx/coroutines/scheduling/TaskMode;", "taskMode", "", "afterTask", "(Lkotlinx/coroutines/scheduling/TaskMode;)V", "", "taskSubmissionTime", "beforeTask", "(Lkotlinx/coroutines/scheduling/TaskMode;J)V", "", "blockingQuiescence", "()Z", "blockingWorkerIdle", "()V", "cpuWorkerIdle", "nanos", "doPark", "(J)Z", "Lkotlinx/coroutines/scheduling/Task;", "findTask$kotlinx_coroutines_core", "()Lkotlinx/coroutines/scheduling/Task;", "findTask", "findTaskWithCpuPermit", "mode", "idleReset", "idleResetBeforeUnpark", "upperBound", "nextInt$kotlinx_coroutines_core", "(I)I", "nextInt", "run", "tryAcquireCpuPermit", "tryForbidTermination", "Lkotlinx/coroutines/scheduling/CoroutineScheduler$WorkerState;", "newState", "tryReleaseCpu$kotlinx_coroutines_core", "(Lkotlinx/coroutines/scheduling/CoroutineScheduler$WorkerState;)Z", "tryReleaseCpu", "trySteal", "tryTerminateWorker", "indexInArray", "I", "getIndexInArray", "()I", "setIndexInArray", "(I)V", "isBlocking", "isParking", "lastExhaustionTime", "J", "lastStealIndex", "Lkotlinx/coroutines/scheduling/WorkQueue;", "localQueue", "Lkotlinx/coroutines/scheduling/WorkQueue;", "getLocalQueue", "()Lkotlinx/coroutines/scheduling/WorkQueue;", "", "nextParkedWorker", "Ljava/lang/Object;", "getNextParkedWorker", "()Ljava/lang/Object;", "setNextParkedWorker", "(Ljava/lang/Object;)V", "parkTimeNs", "rngState", "Lkotlinx/coroutines/scheduling/CoroutineScheduler;", "getScheduler", "()Lkotlinx/coroutines/scheduling/CoroutineScheduler;", "scheduler", "spins", "state", "Lkotlinx/coroutines/scheduling/CoroutineScheduler$WorkerState;", "getState", "()Lkotlinx/coroutines/scheduling/CoroutineScheduler$WorkerState;", "setState", "(Lkotlinx/coroutines/scheduling/CoroutineScheduler$WorkerState;)V", "terminationDeadline", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    public final class Worker extends Thread {
        private static final AtomicIntegerFieldUpdater terminationState$FU = AtomicIntegerFieldUpdater.newUpdater(Worker.class, "terminationState");
        private volatile int indexInArray;
        private long lastExhaustionTime;
        private int lastStealIndex;
        private final WorkQueue localQueue;
        private volatile Object nextParkedWorker;
        private int parkTimeNs;
        private int rngState;
        private volatile int spins;
        private volatile WorkerState state;
        private long terminationDeadline;
        private volatile int terminationState;

        private Worker() {
            setDaemon(true);
            this.localQueue = new WorkQueue();
            this.state = WorkerState.RETIRING;
            this.terminationState = 0;
            this.nextParkedWorker = CoroutineScheduler.NOT_IN_STACK;
            this.parkTimeNs = CoroutineScheduler.MIN_PARK_TIME_NS;
            this.rngState = CoroutineScheduler.this.random.nextInt();
        }

        public final int getIndexInArray() {
            return this.indexInArray;
        }

        public final void setIndexInArray(int index) {
            StringBuilder sb = new StringBuilder();
            sb.append(CoroutineScheduler.this.schedulerName);
            sb.append("-worker-");
            sb.append(index == 0 ? "TERMINATED" : String.valueOf(index));
            setName(sb.toString());
            this.indexInArray = index;
        }

        public Worker(CoroutineScheduler $outer, int index) {
            this();
            setIndexInArray(index);
        }

        /* renamed from: getScheduler, reason: from getter */
        public final CoroutineScheduler getThis$0() {
            return CoroutineScheduler.this;
        }

        public final WorkQueue getLocalQueue() {
            return this.localQueue;
        }

        @Override // java.lang.Thread
        public final WorkerState getState() {
            return this.state;
        }

        public final void setState(WorkerState workerState) {
            Intrinsics.checkParameterIsNotNull(workerState, "<set-?>");
            this.state = workerState;
        }

        public final boolean isParking() {
            return this.state == WorkerState.PARKING;
        }

        public final boolean isBlocking() {
            return this.state == WorkerState.BLOCKING;
        }

        public final Object getNextParkedWorker() {
            return this.nextParkedWorker;
        }

        public final void setNextParkedWorker(Object obj) {
            this.nextParkedWorker = obj;
        }

        public final boolean tryForbidTermination() {
            int state = this.terminationState;
            if (state == 1 || state == -1) {
                return false;
            }
            if (state == 0) {
                return terminationState$FU.compareAndSet(this, 0, -1);
            }
            throw new IllegalStateException(("Invalid terminationState = " + state).toString());
        }

        public final boolean tryAcquireCpuPermit() {
            if (this.state == WorkerState.CPU_ACQUIRED) {
                return true;
            }
            if (CoroutineScheduler.this.cpuPermits.tryAcquire()) {
                this.state = WorkerState.CPU_ACQUIRED;
                return true;
            }
            return false;
        }

        public final boolean tryReleaseCpu$kotlinx_coroutines_core(WorkerState newState) {
            Intrinsics.checkParameterIsNotNull(newState, "newState");
            WorkerState previousState = this.state;
            boolean hadCpu = previousState == WorkerState.CPU_ACQUIRED;
            if (hadCpu) {
                CoroutineScheduler.this.cpuPermits.release();
            }
            if (previousState != newState) {
                this.state = newState;
            }
            return hadCpu;
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            boolean wasIdle = false;
            while (!CoroutineScheduler.this.isTerminated() && this.state != WorkerState.TERMINATED) {
                Task task = findTask$kotlinx_coroutines_core();
                if (task == null) {
                    if (this.state == WorkerState.CPU_ACQUIRED) {
                        cpuWorkerIdle();
                    } else {
                        blockingWorkerIdle();
                    }
                    wasIdle = true;
                } else {
                    TaskMode taskMode = task.getMode();
                    if (wasIdle) {
                        idleReset(taskMode);
                        wasIdle = false;
                    }
                    beforeTask(taskMode, task.submissionTime);
                    CoroutineScheduler.this.runSafely(task);
                    afterTask(taskMode);
                }
            }
            tryReleaseCpu$kotlinx_coroutines_core(WorkerState.TERMINATED);
        }

        private final void beforeTask(TaskMode taskMode, long taskSubmissionTime) {
            if (taskMode == TaskMode.NON_BLOCKING) {
                if (CoroutineScheduler.this.cpuPermits.availablePermits() == 0) {
                    return;
                }
                long now = TasksKt.schedulerTimeSource.nanoTime();
                if (now - taskSubmissionTime >= TasksKt.WORK_STEALING_TIME_RESOLUTION_NS && now - this.lastExhaustionTime >= TasksKt.WORK_STEALING_TIME_RESOLUTION_NS * 5) {
                    this.lastExhaustionTime = now;
                    CoroutineScheduler.this.requestCpuWorker();
                    return;
                }
                return;
            }
            CoroutineScheduler this_$iv = CoroutineScheduler.this;
            CoroutineScheduler.controlState$FU.addAndGet(this_$iv, 2097152L);
            if (tryReleaseCpu$kotlinx_coroutines_core(WorkerState.BLOCKING)) {
                CoroutineScheduler.this.requestCpuWorker();
            }
        }

        private final void afterTask(TaskMode taskMode) {
            if (taskMode != TaskMode.NON_BLOCKING) {
                CoroutineScheduler this_$iv = CoroutineScheduler.this;
                CoroutineScheduler.controlState$FU.addAndGet(this_$iv, CoroutineScheduler.PARKED_VERSION_MASK);
                WorkerState currentState = this.state;
                if (currentState != WorkerState.TERMINATED) {
                    if (DebugKt.getASSERTIONS_ENABLED()) {
                        if (!(currentState == WorkerState.BLOCKING)) {
                            throw new AssertionError();
                        }
                    }
                    this.state = WorkerState.RETIRING;
                }
            }
        }

        public final int nextInt$kotlinx_coroutines_core(int upperBound) {
            int i = this.rngState;
            int i2 = i ^ (i << 13);
            this.rngState = i2;
            int i3 = i2 ^ (i2 >> 17);
            this.rngState = i3;
            int i4 = i3 ^ (i3 << 5);
            this.rngState = i4;
            int mask = upperBound - 1;
            if ((mask & upperBound) == 0) {
                return i4 & mask;
            }
            return (i4 & Integer.MAX_VALUE) % upperBound;
        }

        private final void cpuWorkerIdle() {
            int spins = this.spins;
            if (spins > CoroutineScheduler.MAX_YIELDS) {
                if (this.parkTimeNs < CoroutineScheduler.MAX_PARK_TIME_NS) {
                    this.parkTimeNs = RangesKt.coerceAtMost((this.parkTimeNs * 3) >>> 1, CoroutineScheduler.MAX_PARK_TIME_NS);
                }
                tryReleaseCpu$kotlinx_coroutines_core(WorkerState.PARKING);
                doPark(this.parkTimeNs);
                return;
            }
            this.spins = spins + 1;
            if (spins >= CoroutineScheduler.MAX_SPINS) {
                Thread.yield();
            }
        }

        private final void blockingWorkerIdle() {
            tryReleaseCpu$kotlinx_coroutines_core(WorkerState.PARKING);
            if (blockingQuiescence()) {
                this.terminationState = 0;
                if (this.terminationDeadline == 0) {
                    this.terminationDeadline = System.nanoTime() + CoroutineScheduler.this.idleWorkerKeepAliveNs;
                }
                if (doPark(CoroutineScheduler.this.idleWorkerKeepAliveNs) && System.nanoTime() - this.terminationDeadline >= 0) {
                    this.terminationDeadline = 0L;
                    tryTerminateWorker();
                }
            }
        }

        private final boolean doPark(long nanos) {
            CoroutineScheduler.this.parkedWorkersStackPush(this);
            if (!blockingQuiescence()) {
                return false;
            }
            LockSupport.parkNanos(nanos);
            return true;
        }

        private final void tryTerminateWorker() {
            Object lock$iv = CoroutineScheduler.this.workers;
            synchronized (lock$iv) {
                if (CoroutineScheduler.this.isTerminated()) {
                    return;
                }
                if (CoroutineScheduler.this.getCreatedWorkers() <= CoroutineScheduler.this.corePoolSize) {
                    return;
                }
                if (blockingQuiescence()) {
                    if (terminationState$FU.compareAndSet(this, 0, 1)) {
                        int oldIndex = this.indexInArray;
                        setIndexInArray(0);
                        CoroutineScheduler.this.parkedWorkersStackTopUpdate(this, oldIndex, 0);
                        CoroutineScheduler this_$iv = CoroutineScheduler.this;
                        long state$iv$iv = CoroutineScheduler.controlState$FU.getAndDecrement(this_$iv);
                        int lastIndex = (int) (2097151 & state$iv$iv);
                        if (lastIndex != oldIndex) {
                            Worker lastWorker = CoroutineScheduler.this.workers[lastIndex];
                            if (lastWorker == null) {
                                Intrinsics.throwNpe();
                            }
                            CoroutineScheduler.this.workers[oldIndex] = lastWorker;
                            lastWorker.setIndexInArray(oldIndex);
                            CoroutineScheduler.this.parkedWorkersStackTopUpdate(lastWorker, lastIndex, oldIndex);
                        }
                        CoroutineScheduler.this.workers[lastIndex] = (Worker) null;
                        Unit unit = Unit.INSTANCE;
                        this.state = WorkerState.TERMINATED;
                    }
                }
            }
        }

        private final boolean blockingQuiescence() {
            Task it = CoroutineScheduler.this.globalQueue.removeFirstWithModeOrNull(TaskMode.PROBABLY_BLOCKING);
            if (it != null) {
                this.localQueue.add(it, CoroutineScheduler.this.globalQueue);
                return false;
            }
            return true;
        }

        private final void idleReset(TaskMode mode) {
            this.terminationDeadline = 0L;
            this.lastStealIndex = 0;
            if (this.state == WorkerState.PARKING) {
                if (DebugKt.getASSERTIONS_ENABLED()) {
                    if (!(mode == TaskMode.PROBABLY_BLOCKING)) {
                        throw new AssertionError();
                    }
                }
                this.state = WorkerState.BLOCKING;
                this.parkTimeNs = CoroutineScheduler.MIN_PARK_TIME_NS;
            }
            this.spins = 0;
        }

        public final void idleResetBeforeUnpark() {
            this.parkTimeNs = CoroutineScheduler.MIN_PARK_TIME_NS;
            this.spins = 0;
        }

        public final Task findTask$kotlinx_coroutines_core() {
            if (tryAcquireCpuPermit()) {
                return findTaskWithCpuPermit();
            }
            Task poll = this.localQueue.poll();
            return poll != null ? poll : CoroutineScheduler.this.globalQueue.removeFirstWithModeOrNull(TaskMode.PROBABLY_BLOCKING);
        }

        private final Task findTaskWithCpuPermit() {
            Task it;
            Task it2;
            boolean globalFirst = nextInt$kotlinx_coroutines_core(CoroutineScheduler.this.corePoolSize * 2) == 0;
            if (globalFirst && (it2 = CoroutineScheduler.this.globalQueue.removeFirstWithModeOrNull(TaskMode.NON_BLOCKING)) != null) {
                return it2;
            }
            Task it3 = this.localQueue.poll();
            return it3 != null ? it3 : (globalFirst || (it = CoroutineScheduler.this.globalQueue.removeFirstOrNull()) == null) ? trySteal() : it;
        }

        private final Task trySteal() {
            int created = CoroutineScheduler.this.getCreatedWorkers();
            if (created < 2) {
                return null;
            }
            int stealIndex = this.lastStealIndex;
            if (stealIndex == 0) {
                stealIndex = nextInt$kotlinx_coroutines_core(created);
            }
            int stealIndex2 = stealIndex + 1;
            if (stealIndex2 > created) {
                stealIndex2 = 1;
            }
            this.lastStealIndex = stealIndex2;
            Worker worker = CoroutineScheduler.this.workers[stealIndex2];
            if (worker == null || worker == this || !this.localQueue.trySteal(worker.localQueue, CoroutineScheduler.this.globalQueue)) {
                return null;
            }
            return this.localQueue.poll();
        }
    }
}
