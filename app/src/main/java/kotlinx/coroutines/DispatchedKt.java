package kotlinx.coroutines;

import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.internal.StackTraceRecoveryKt;
import kotlinx.coroutines.internal.Symbol;

/* compiled from: Dispatched.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000N\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0003\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\"\u0010\u0004\u001a\u00020\u0005\"\u0004\b\u0000\u0010\u0006*\b\u0012\u0004\u0012\u0002H\u00060\u00072\b\b\u0002\u0010\b\u001a\u00020\tH\u0000\u001a;\u0010\n\u001a\u00020\u000b*\u0006\u0012\u0002\b\u00030\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\u000f\u001a\u00020\u000b2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00050\u0011H\u0082\b\u001a.\u0010\u0012\u001a\u00020\u0005\"\u0004\b\u0000\u0010\u0006*\b\u0012\u0004\u0012\u0002H\u00060\u00072\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u0002H\u00060\u00142\u0006\u0010\u0015\u001a\u00020\tH\u0000\u001a%\u0010\u0016\u001a\u00020\u0005\"\u0004\b\u0000\u0010\u0006*\b\u0012\u0004\u0012\u0002H\u00060\u00142\u0006\u0010\u0017\u001a\u0002H\u0006H\u0000¢\u0006\u0002\u0010\u0018\u001a \u0010\u0019\u001a\u00020\u0005\"\u0004\b\u0000\u0010\u0006*\b\u0012\u0004\u0012\u0002H\u00060\u00142\u0006\u0010\u001a\u001a\u00020\u001bH\u0000\u001a%\u0010\u001c\u001a\u00020\u0005\"\u0004\b\u0000\u0010\u0006*\b\u0012\u0004\u0012\u0002H\u00060\u00142\u0006\u0010\u0017\u001a\u0002H\u0006H\u0000¢\u0006\u0002\u0010\u0018\u001a \u0010\u001d\u001a\u00020\u0005\"\u0004\b\u0000\u0010\u0006*\b\u0012\u0004\u0012\u0002H\u00060\u00142\u0006\u0010\u001a\u001a\u00020\u001bH\u0000\u001a\u0010\u0010\u001e\u001a\u00020\u0005*\u0006\u0012\u0002\b\u00030\u0007H\u0002\u001a\u0019\u0010\u001f\u001a\u00020\u0005*\u0006\u0012\u0002\b\u00030\u00142\u0006\u0010\u001a\u001a\u00020\u001bH\u0080\b\u001a'\u0010 \u001a\u00020\u0005*\u0006\u0012\u0002\b\u00030\u00072\u0006\u0010!\u001a\u00020\"2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00050\u0011H\u0082\b\u001a\u0012\u0010#\u001a\u00020\u000b*\b\u0012\u0004\u0012\u00020\u00050\fH\u0000\"\u0016\u0010\u0000\u001a\u00020\u00018\u0002X\u0083\u0004¢\u0006\b\n\u0000\u0012\u0004\b\u0002\u0010\u0003¨\u0006$"}, d2 = {"UNDEFINED", "Lkotlinx/coroutines/internal/Symbol;", "UNDEFINED$annotations", "()V", "dispatch", "", "T", "Lkotlinx/coroutines/DispatchedTask;", "mode", "", "executeUnconfined", "", "Lkotlinx/coroutines/DispatchedContinuation;", "contState", "", "doYield", "block", "Lkotlin/Function0;", "resume", "delegate", "Lkotlin/coroutines/Continuation;", "useMode", "resumeCancellable", "value", "(Lkotlin/coroutines/Continuation;Ljava/lang/Object;)V", "resumeCancellableWithException", "exception", "", "resumeDirect", "resumeDirectWithException", "resumeUnconfined", "resumeWithStackTrace", "runUnconfinedEventLoop", "eventLoop", "Lkotlinx/coroutines/EventLoop;", "yieldUndispatched", "kotlinx-coroutines-core"}, k = 2, mv = {1, 1, 15})
/* loaded from: classes.dex */
public final class DispatchedKt {
    private static final Symbol UNDEFINED = new Symbol("UNDEFINED");

    private static /* synthetic */ void UNDEFINED$annotations() {
    }

    static /* synthetic */ boolean executeUnconfined$default(DispatchedContinuation $this$executeUnconfined, Object contState, int mode, boolean doYield, Function0 block, int i, Object obj) {
        if ((i & 4) != 0) {
            doYield = false;
        }
        EventLoop eventLoop = ThreadLocalEventLoop.INSTANCE.getEventLoop$kotlinx_coroutines_core();
        if (doYield && eventLoop.isUnconfinedQueueEmpty()) {
            return false;
        }
        if (eventLoop.isUnconfinedLoopActive()) {
            $this$executeUnconfined._state = contState;
            $this$executeUnconfined.resumeMode = mode;
            eventLoop.dispatchUnconfined($this$executeUnconfined);
            return true;
        }
        DispatchedContinuation $this$runUnconfinedEventLoop$iv = $this$executeUnconfined;
        eventLoop.incrementUseCount(true);
        try {
            block.invoke();
            do {
            } while (eventLoop.processUnconfinedEvent());
            InlineMarker.finallyStart(1);
        } catch (Throwable e$iv) {
            try {
                $this$runUnconfinedEventLoop$iv.handleFatalException$kotlinx_coroutines_core(e$iv, null);
                InlineMarker.finallyStart(1);
            } catch (Throwable th) {
                InlineMarker.finallyStart(1);
                eventLoop.decrementUseCount(true);
                InlineMarker.finallyEnd(1);
                throw th;
            }
        }
        eventLoop.decrementUseCount(true);
        InlineMarker.finallyEnd(1);
        return false;
    }

    private static final boolean executeUnconfined(DispatchedContinuation<?> dispatchedContinuation, Object contState, int mode, boolean doYield, Function0<Unit> function0) {
        EventLoop eventLoop = ThreadLocalEventLoop.INSTANCE.getEventLoop$kotlinx_coroutines_core();
        if (doYield && eventLoop.isUnconfinedQueueEmpty()) {
            return false;
        }
        if (eventLoop.isUnconfinedLoopActive()) {
            dispatchedContinuation._state = contState;
            dispatchedContinuation.resumeMode = mode;
            eventLoop.dispatchUnconfined(dispatchedContinuation);
            return true;
        }
        DispatchedContinuation<?> $this$runUnconfinedEventLoop$iv = dispatchedContinuation;
        eventLoop.incrementUseCount(true);
        try {
            function0.invoke();
            do {
            } while (eventLoop.processUnconfinedEvent());
            InlineMarker.finallyStart(1);
        } catch (Throwable e$iv) {
            try {
                $this$runUnconfinedEventLoop$iv.handleFatalException$kotlinx_coroutines_core(e$iv, null);
                InlineMarker.finallyStart(1);
            } catch (Throwable th) {
                InlineMarker.finallyStart(1);
                eventLoop.decrementUseCount(true);
                InlineMarker.finallyEnd(1);
                throw th;
            }
        }
        eventLoop.decrementUseCount(true);
        InlineMarker.finallyEnd(1);
        return false;
    }

    private static final void resumeUnconfined(DispatchedTask<?> dispatchedTask) {
        EventLoop eventLoop = ThreadLocalEventLoop.INSTANCE.getEventLoop$kotlinx_coroutines_core();
        if (eventLoop.isUnconfinedLoopActive()) {
            eventLoop.dispatchUnconfined(dispatchedTask);
            return;
        }
        eventLoop.incrementUseCount(true);
        try {
            resume(dispatchedTask, dispatchedTask.getDelegate$kotlinx_coroutines_core(), 3);
            do {
            } while (eventLoop.processUnconfinedEvent());
        } finally {
            try {
            } finally {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void runUnconfinedEventLoop(DispatchedTask<?> dispatchedTask, EventLoop eventLoop, Function0<Unit> function0) {
        eventLoop.incrementUseCount(true);
        try {
            function0.invoke();
            do {
            } while (eventLoop.processUnconfinedEvent());
            InlineMarker.finallyStart(1);
        } catch (Throwable e) {
            try {
                dispatchedTask.handleFatalException$kotlinx_coroutines_core(e, null);
                InlineMarker.finallyStart(1);
            } catch (Throwable th) {
                InlineMarker.finallyStart(1);
                eventLoop.decrementUseCount(true);
                InlineMarker.finallyEnd(1);
                throw th;
            }
        }
        eventLoop.decrementUseCount(true);
        InlineMarker.finallyEnd(1);
    }

    /* JADX WARN: Removed duplicated region for block: B:18:0x008f  */
    /* JADX WARN: Removed duplicated region for block: B:37:? A[LOOP:0: B:32:0x00d1->B:37:?, LOOP_END, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00cd  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final <T> void resumeCancellable(kotlin.coroutines.Continuation<? super T> r21, T r22) {
        /*
            Method dump skipped, instructions count: 254
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.DispatchedKt.resumeCancellable(kotlin.coroutines.Continuation, java.lang.Object):void");
    }

    /* JADX WARN: Can't wrap try/catch for region: R(12:12|13|14|(3:54|55|(9:57|58|17|(10:19|20|21|22|23|24|25|26|27|28)(1:45)|29|(2:35|30)|32|33|34))|16|17|(0)(0)|29|(1:30)|32|33|34) */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x0101, code lost:
    
        r0 = th;
     */
    /* JADX WARN: Removed duplicated region for block: B:19:0x00a4  */
    /* JADX WARN: Removed duplicated region for block: B:35:? A[LOOP:0: B:30:0x00f8->B:35:?, LOOP_END, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:45:0x00f4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final <T> void resumeCancellableWithException(kotlin.coroutines.Continuation<? super T> r27, java.lang.Throwable r28) {
        /*
            Method dump skipped, instructions count: 305
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.DispatchedKt.resumeCancellableWithException(kotlin.coroutines.Continuation, java.lang.Throwable):void");
    }

    public static final <T> void resumeDirect(Continuation<? super T> resumeDirect, T t) {
        Intrinsics.checkParameterIsNotNull(resumeDirect, "$this$resumeDirect");
        if (!(resumeDirect instanceof DispatchedContinuation)) {
            Result.Companion companion = Result.INSTANCE;
            resumeDirect.resumeWith(Result.m16constructorimpl(t));
        } else {
            Continuation<T> continuation = ((DispatchedContinuation) resumeDirect).continuation;
            Result.Companion companion2 = Result.INSTANCE;
            continuation.resumeWith(Result.m16constructorimpl(t));
        }
    }

    public static final <T> void resumeDirectWithException(Continuation<? super T> resumeDirectWithException, Throwable exception) {
        Intrinsics.checkParameterIsNotNull(resumeDirectWithException, "$this$resumeDirectWithException");
        Intrinsics.checkParameterIsNotNull(exception, "exception");
        if (resumeDirectWithException instanceof DispatchedContinuation) {
            Continuation $this$resumeWithStackTrace$iv = ((DispatchedContinuation) resumeDirectWithException).continuation;
            Result.Companion companion = Result.INSTANCE;
            $this$resumeWithStackTrace$iv.resumeWith(Result.m16constructorimpl(ResultKt.createFailure(StackTraceRecoveryKt.recoverStackTrace(exception, $this$resumeWithStackTrace$iv))));
        } else {
            Result.Companion companion2 = Result.INSTANCE;
            resumeDirectWithException.resumeWith(Result.m16constructorimpl(ResultKt.createFailure(StackTraceRecoveryKt.recoverStackTrace(exception, resumeDirectWithException))));
        }
    }

    public static final boolean yieldUndispatched(DispatchedContinuation<? super Unit> yieldUndispatched) {
        Intrinsics.checkParameterIsNotNull(yieldUndispatched, "$this$yieldUndispatched");
        Object contState$iv = Unit.INSTANCE;
        EventLoop eventLoop$iv = ThreadLocalEventLoop.INSTANCE.getEventLoop$kotlinx_coroutines_core();
        if (eventLoop$iv.isUnconfinedQueueEmpty()) {
            return false;
        }
        if (eventLoop$iv.isUnconfinedLoopActive()) {
            yieldUndispatched._state = contState$iv;
            yieldUndispatched.resumeMode = 1;
            eventLoop$iv.dispatchUnconfined(yieldUndispatched);
            return true;
        }
        DispatchedContinuation<? super Unit> $this$runUnconfinedEventLoop$iv$iv = yieldUndispatched;
        eventLoop$iv.incrementUseCount(true);
        try {
            yieldUndispatched.run();
            do {
            } while (eventLoop$iv.processUnconfinedEvent());
        } finally {
            try {
                return false;
            } finally {
            }
        }
        return false;
    }

    public static /* synthetic */ void dispatch$default(DispatchedTask dispatchedTask, int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = 1;
        }
        dispatch(dispatchedTask, i);
    }

    public static final <T> void dispatch(DispatchedTask<? super T> dispatch, int mode) {
        Intrinsics.checkParameterIsNotNull(dispatch, "$this$dispatch");
        Continuation delegate = dispatch.getDelegate$kotlinx_coroutines_core();
        if (ResumeModeKt.isDispatchedMode(mode) && (delegate instanceof DispatchedContinuation) && ResumeModeKt.isCancellableMode(mode) == ResumeModeKt.isCancellableMode(dispatch.resumeMode)) {
            CoroutineDispatcher dispatcher = ((DispatchedContinuation) delegate).dispatcher;
            CoroutineContext context = delegate.getContext();
            if (dispatcher.isDispatchNeeded(context)) {
                dispatcher.mo1008dispatch(context, dispatch);
                return;
            } else {
                resumeUnconfined(dispatch);
                return;
            }
        }
        resume(dispatch, delegate, mode);
    }

    public static final <T> void resume(DispatchedTask<? super T> resume, Continuation<? super T> delegate, int useMode) {
        Intrinsics.checkParameterIsNotNull(resume, "$this$resume");
        Intrinsics.checkParameterIsNotNull(delegate, "delegate");
        Object state = resume.takeState$kotlinx_coroutines_core();
        Throwable exception = resume.getExceptionalResult$kotlinx_coroutines_core(state);
        if (exception != null) {
            Throwable recovered = delegate instanceof DispatchedTask ? exception : StackTraceRecoveryKt.recoverStackTrace(exception, delegate);
            ResumeModeKt.resumeWithExceptionMode(delegate, recovered, useMode);
        } else {
            ResumeModeKt.resumeMode(delegate, resume.getSuccessfulResult$kotlinx_coroutines_core(state), useMode);
        }
    }

    public static final void resumeWithStackTrace(Continuation<?> resumeWithStackTrace, Throwable exception) {
        Intrinsics.checkParameterIsNotNull(resumeWithStackTrace, "$this$resumeWithStackTrace");
        Intrinsics.checkParameterIsNotNull(exception, "exception");
        Result.Companion companion = Result.INSTANCE;
        resumeWithStackTrace.resumeWith(Result.m16constructorimpl(ResultKt.createFailure(StackTraceRecoveryKt.recoverStackTrace(exception, resumeWithStackTrace))));
    }
}
