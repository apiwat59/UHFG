package kotlinx.coroutines;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.LongCompanionObject;
import kotlin.ranges.RangesKt;

/* compiled from: DefaultExecutor.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0007\bÀ\u0002\u0018\u00002\u00020\u00012\u00060\u0002j\u0002`\u0003B\u0007\b\u0002¢\u0006\u0002\u0010\u0004J\b\u0010\u0019\u001a\u00020\u001aH\u0002J\b\u0010\u001b\u001a\u00020\u0010H\u0002J\r\u0010\u001c\u001a\u00020\u001aH\u0000¢\u0006\u0002\b\u001dJ\u001c\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\b2\n\u0010!\u001a\u00060\u0002j\u0002`\u0003H\u0016J\b\u0010\"\u001a\u00020\u0014H\u0002J\b\u0010#\u001a\u00020\u001aH\u0016J\u000e\u0010$\u001a\u00020\u001a2\u0006\u0010%\u001a\u00020\bR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0086T¢\u0006\u0002\n\u0000R\u0016\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e¢\u0006\b\n\u0000\u0012\u0004\b\u0011\u0010\u0004R\u000e\u0010\u0012\u001a\u00020\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0013\u001a\u00020\u00148BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0015R\u0014\u0010\u0016\u001a\u00020\u00108TX\u0094\u0004¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0018¨\u0006&"}, d2 = {"Lkotlinx/coroutines/DefaultExecutor;", "Lkotlinx/coroutines/EventLoopImplBase;", "Ljava/lang/Runnable;", "Lkotlinx/coroutines/Runnable;", "()V", "ACTIVE", "", "DEFAULT_KEEP_ALIVE", "", "FRESH", "KEEP_ALIVE_NANOS", "SHUTDOWN_ACK", "SHUTDOWN_REQ", "THREAD_NAME", "", "_thread", "Ljava/lang/Thread;", "_thread$annotations", "debugStatus", "isShutdownRequested", "", "()Z", "thread", "getThread", "()Ljava/lang/Thread;", "acknowledgeShutdownIfNeeded", "", "createThreadSync", "ensureStarted", "ensureStarted$kotlinx_coroutines_core", "invokeOnTimeout", "Lkotlinx/coroutines/DisposableHandle;", "timeMillis", "block", "notifyStartup", "run", "shutdown", "timeout", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public final class DefaultExecutor extends EventLoopImplBase implements Runnable {
    private static final int ACTIVE = 1;
    private static final long DEFAULT_KEEP_ALIVE = 1000;
    private static final int FRESH = 0;
    public static final DefaultExecutor INSTANCE;
    private static final long KEEP_ALIVE_NANOS;
    private static final int SHUTDOWN_ACK = 3;
    private static final int SHUTDOWN_REQ = 2;
    public static final String THREAD_NAME = "kotlinx.coroutines.DefaultExecutor";
    private static volatile Thread _thread;
    private static volatile int debugStatus;

    private static /* synthetic */ void _thread$annotations() {
    }

    static {
        Long valueOf;
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        INSTANCE = defaultExecutor;
        EventLoop.incrementUseCount$default(defaultExecutor, false, 1, null);
        TimeUnit timeUnit = TimeUnit.MILLISECONDS;
        try {
            valueOf = Long.getLong("kotlinx.coroutines.DefaultExecutor.keepAlive", DEFAULT_KEEP_ALIVE);
        } catch (SecurityException e) {
            valueOf = Long.valueOf(DEFAULT_KEEP_ALIVE);
        }
        Intrinsics.checkExpressionValueIsNotNull(valueOf, "try {\n            java.l…AULT_KEEP_ALIVE\n        }");
        KEEP_ALIVE_NANOS = timeUnit.toNanos(valueOf.longValue());
    }

    private DefaultExecutor() {
    }

    @Override // kotlinx.coroutines.EventLoopImplPlatform
    protected Thread getThread() {
        Thread thread = _thread;
        return thread != null ? thread : createThreadSync();
    }

    private final boolean isShutdownRequested() {
        int debugStatus2 = debugStatus;
        return debugStatus2 == 2 || debugStatus2 == 3;
    }

    @Override // kotlinx.coroutines.EventLoopImplBase, kotlinx.coroutines.Delay
    public DisposableHandle invokeOnTimeout(long timeMillis, Runnable block) {
        Intrinsics.checkParameterIsNotNull(block, "block");
        return scheduleInvokeOnTimeout(timeMillis, block);
    }

    @Override // java.lang.Runnable
    public void run() {
        boolean isEmpty;
        ThreadLocalEventLoop.INSTANCE.setEventLoop$kotlinx_coroutines_core(this);
        TimeSource timeSource = TimeSourceKt.getTimeSource();
        if (timeSource != null) {
            timeSource.registerTimeLoopThread();
        }
        long shutdownNanos = LongCompanionObject.MAX_VALUE;
        try {
            if (!notifyStartup()) {
                if (isEmpty) {
                    return;
                } else {
                    return;
                }
            }
            while (true) {
                Thread.interrupted();
                long parkNanos = processNextEvent();
                if (parkNanos == LongCompanionObject.MAX_VALUE) {
                    if (shutdownNanos == LongCompanionObject.MAX_VALUE) {
                        TimeSource timeSource2 = TimeSourceKt.getTimeSource();
                        long now = timeSource2 != null ? timeSource2.nanoTime() : System.nanoTime();
                        if (shutdownNanos == LongCompanionObject.MAX_VALUE) {
                            shutdownNanos = now + KEEP_ALIVE_NANOS;
                        }
                        long tillShutdown = shutdownNanos - now;
                        if (tillShutdown <= 0) {
                            _thread = (Thread) null;
                            acknowledgeShutdownIfNeeded();
                            TimeSource timeSource3 = TimeSourceKt.getTimeSource();
                            if (timeSource3 != null) {
                                timeSource3.unregisterTimeLoopThread();
                            }
                            if (isEmpty()) {
                                return;
                            }
                            getThread();
                            return;
                        }
                        parkNanos = RangesKt.coerceAtMost(parkNanos, tillShutdown);
                    } else {
                        parkNanos = RangesKt.coerceAtMost(parkNanos, KEEP_ALIVE_NANOS);
                    }
                }
                if (parkNanos > 0) {
                    if (isShutdownRequested()) {
                        _thread = (Thread) null;
                        acknowledgeShutdownIfNeeded();
                        TimeSource timeSource4 = TimeSourceKt.getTimeSource();
                        if (timeSource4 != null) {
                            timeSource4.unregisterTimeLoopThread();
                        }
                        if (isEmpty()) {
                            return;
                        }
                        getThread();
                        return;
                    }
                    TimeSource timeSource5 = TimeSourceKt.getTimeSource();
                    if (timeSource5 != null) {
                        timeSource5.parkNanos(this, parkNanos);
                    } else {
                        LockSupport.parkNanos(this, parkNanos);
                    }
                }
            }
        } finally {
            _thread = (Thread) null;
            acknowledgeShutdownIfNeeded();
            TimeSource timeSource6 = TimeSourceKt.getTimeSource();
            if (timeSource6 != null) {
                timeSource6.unregisterTimeLoopThread();
            }
            if (!isEmpty()) {
                getThread();
            }
        }
    }

    private final synchronized Thread createThreadSync() {
        Thread $this$apply;
        $this$apply = _thread;
        if ($this$apply == null) {
            $this$apply = new Thread(this, THREAD_NAME);
            _thread = $this$apply;
            $this$apply.setDaemon(true);
            $this$apply.start();
        }
        return $this$apply;
    }

    public final synchronized void ensureStarted$kotlinx_coroutines_core() {
        boolean z = true;
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if ((_thread == null ? 1 : 0) == 0) {
                throw new AssertionError();
            }
        }
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (debugStatus != 0 && debugStatus != 3) {
                z = false;
            }
            throw new AssertionError();
        }
        debugStatus = 0;
        createThreadSync();
        while (debugStatus == 0) {
            wait();
        }
    }

    private final synchronized boolean notifyStartup() {
        if (isShutdownRequested()) {
            return false;
        }
        debugStatus = 1;
        notifyAll();
        return true;
    }

    public final synchronized void shutdown(long timeout) {
        long deadline = System.currentTimeMillis() + timeout;
        if (!isShutdownRequested()) {
            debugStatus = 2;
        }
        while (debugStatus != 3 && _thread != null) {
            Thread it = _thread;
            if (it != null) {
                TimeSource timeSource = TimeSourceKt.getTimeSource();
                if (timeSource != null) {
                    timeSource.unpark(it);
                } else {
                    LockSupport.unpark(it);
                }
            }
            long remaining = deadline - System.currentTimeMillis();
            if (remaining <= 0) {
                break;
            } else {
                wait(timeout);
            }
        }
        debugStatus = 0;
    }

    private final synchronized void acknowledgeShutdownIfNeeded() {
        if (isShutdownRequested()) {
            debugStatus = 3;
            resetAll();
            notifyAll();
        }
    }
}
