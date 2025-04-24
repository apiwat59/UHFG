package kotlinx.coroutines;

import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.jvm.internal.CoroutineStackFrame;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.internal.StackTraceRecoveryKt;
import kotlinx.coroutines.internal.Symbol;
import kotlinx.coroutines.internal.ThreadContextKt;

/* compiled from: Dispatched.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0003\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0000\u0018\u0000*\u0006\b\u0000\u0010\u0001 \u00002\b\u0012\u0004\u0012\u0002H\u00010\u00022\u00060\u0003j\u0002`\u00042\b\u0012\u0004\u0012\u0002H\u00010\u0005B\u001b\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00028\u00000\u0005¢\u0006\u0002\u0010\tJ\u0017\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00028\u0000H\u0000¢\u0006\u0004\b\u001c\u0010\u001dJ\u0010\u0010\u001e\u001a\n\u0018\u00010\u001fj\u0004\u0018\u0001` H\u0016J\u0016\u0010!\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00028\u0000H\u0086\b¢\u0006\u0002\u0010\u001dJ\u0011\u0010\"\u001a\u00020\u001a2\u0006\u0010#\u001a\u00020$H\u0086\bJ\t\u0010%\u001a\u00020&H\u0086\bJ\u0016\u0010'\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00028\u0000H\u0086\b¢\u0006\u0002\u0010\u001dJ\u0011\u0010(\u001a\u00020\u001a2\u0006\u0010#\u001a\u00020$H\u0086\bJ\u001e\u0010)\u001a\u00020\u001a2\f\u0010*\u001a\b\u0012\u0004\u0012\u00028\u00000+H\u0016ø\u0001\u0000¢\u0006\u0002\u0010\u001dJ\u000f\u0010,\u001a\u0004\u0018\u00010\u000bH\u0010¢\u0006\u0002\b-J\b\u0010.\u001a\u00020/H\u0016R\u001a\u0010\n\u001a\u0004\u0018\u00010\u000b8\u0000@\u0000X\u0081\u000e¢\u0006\b\n\u0000\u0012\u0004\b\f\u0010\rR\u001c\u0010\u000e\u001a\n\u0018\u00010\u0003j\u0004\u0018\u0001`\u0004X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0012\u0010\u0011\u001a\u00020\u0012X\u0096\u0005¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014R\u0016\u0010\b\u001a\b\u0012\u0004\u0012\u00028\u00000\u00058\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u00020\u000b8\u0000X\u0081\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0016\u001a\b\u0012\u0004\u0012\u00028\u00000\u00058PX\u0090\u0004¢\u0006\u0006\u001a\u0004\b\u0017\u0010\u0018R\u0010\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019¨\u00060"}, d2 = {"Lkotlinx/coroutines/DispatchedContinuation;", "T", "Lkotlinx/coroutines/DispatchedTask;", "Lkotlin/coroutines/jvm/internal/CoroutineStackFrame;", "Lkotlinx/coroutines/internal/CoroutineStackFrame;", "Lkotlin/coroutines/Continuation;", "dispatcher", "Lkotlinx/coroutines/CoroutineDispatcher;", "continuation", "(Lkotlinx/coroutines/CoroutineDispatcher;Lkotlin/coroutines/Continuation;)V", "_state", "", "_state$annotations", "()V", "callerFrame", "getCallerFrame", "()Lkotlin/coroutines/jvm/internal/CoroutineStackFrame;", "context", "Lkotlin/coroutines/CoroutineContext;", "getContext", "()Lkotlin/coroutines/CoroutineContext;", "countOrElement", "delegate", "getDelegate$kotlinx_coroutines_core", "()Lkotlin/coroutines/Continuation;", "dispatchYield", "", "value", "dispatchYield$kotlinx_coroutines_core", "(Ljava/lang/Object;)V", "getStackTraceElement", "Ljava/lang/StackTraceElement;", "Lkotlinx/coroutines/internal/StackTraceElement;", "resumeCancellable", "resumeCancellableWithException", "exception", "", "resumeCancelled", "", "resumeUndispatched", "resumeUndispatchedWithException", "resumeWith", "result", "Lkotlin/Result;", "takeState", "takeState$kotlinx_coroutines_core", "toString", "", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public final class DispatchedContinuation<T> extends DispatchedTask<T> implements CoroutineStackFrame, Continuation<T> {
    public Object _state;
    private final CoroutineStackFrame callerFrame;
    public final Continuation<T> continuation;
    public final Object countOrElement;
    public final CoroutineDispatcher dispatcher;

    public static /* synthetic */ void _state$annotations() {
    }

    @Override // kotlin.coroutines.Continuation
    public CoroutineContext getContext() {
        return this.continuation.getContext();
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public DispatchedContinuation(CoroutineDispatcher dispatcher, Continuation<? super T> continuation) {
        super(0);
        Symbol symbol;
        Intrinsics.checkParameterIsNotNull(dispatcher, "dispatcher");
        Intrinsics.checkParameterIsNotNull(continuation, "continuation");
        this.dispatcher = dispatcher;
        this.continuation = continuation;
        symbol = DispatchedKt.UNDEFINED;
        this._state = symbol;
        this.callerFrame = !(continuation instanceof CoroutineStackFrame) ? null : continuation;
        this.countOrElement = ThreadContextKt.threadContextElements(getContext());
    }

    @Override // kotlin.coroutines.jvm.internal.CoroutineStackFrame
    public CoroutineStackFrame getCallerFrame() {
        return this.callerFrame;
    }

    @Override // kotlin.coroutines.jvm.internal.CoroutineStackFrame
    public StackTraceElement getStackTraceElement() {
        return null;
    }

    @Override // kotlinx.coroutines.DispatchedTask
    public Object takeState$kotlinx_coroutines_core() {
        Symbol symbol;
        Symbol symbol2;
        Object state = this._state;
        if (DebugKt.getASSERTIONS_ENABLED()) {
            symbol2 = DispatchedKt.UNDEFINED;
            if (!(state != symbol2)) {
                throw new AssertionError();
            }
        }
        symbol = DispatchedKt.UNDEFINED;
        this._state = symbol;
        return state;
    }

    @Override // kotlinx.coroutines.DispatchedTask
    public Continuation<T> getDelegate$kotlinx_coroutines_core() {
        return this;
    }

    @Override // kotlin.coroutines.Continuation
    public void resumeWith(Object result) {
        CoroutineContext context$iv;
        Object oldValue$iv;
        CoroutineContext context = this.continuation.getContext();
        Object state = CompletedExceptionallyKt.toState(result);
        if (this.dispatcher.isDispatchNeeded(context)) {
            this._state = state;
            this.resumeMode = 0;
            this.dispatcher.mo1008dispatch(context, this);
            return;
        }
        EventLoop eventLoop$iv = ThreadLocalEventLoop.INSTANCE.getEventLoop$kotlinx_coroutines_core();
        if (eventLoop$iv.isUnconfinedLoopActive()) {
            this._state = state;
            this.resumeMode = 0;
            eventLoop$iv.dispatchUnconfined(this);
            return;
        }
        DispatchedContinuation<T> $this$runUnconfinedEventLoop$iv$iv = this;
        eventLoop$iv.incrementUseCount(true);
        try {
            context$iv = getContext();
            Object countOrElement$iv = this.countOrElement;
            oldValue$iv = ThreadContextKt.updateThreadContext(context$iv, countOrElement$iv);
        } catch (Throwable th) {
            e$iv$iv = th;
        }
        try {
            try {
            } catch (Throwable th2) {
                th = th2;
            }
            try {
                this.continuation.resumeWith(result);
                Unit unit = Unit.INSTANCE;
                ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
                while (eventLoop$iv.processUnconfinedEvent()) {
                }
            } catch (Throwable th3) {
                th = th3;
                ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
                throw th;
            }
        } catch (Throwable th4) {
            e$iv$iv = th4;
            try {
                $this$runUnconfinedEventLoop$iv$iv.handleFatalException$kotlinx_coroutines_core(e$iv$iv, null);
            } finally {
                eventLoop$iv.decrementUseCount(true);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v4, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r1v5 */
    public final void resumeCancellable(T value) {
        ?? r1;
        boolean z;
        if (this.dispatcher.isDispatchNeeded(getContext())) {
            this._state = value;
            this.resumeMode = 1;
            this.dispatcher.mo1008dispatch(getContext(), this);
            return;
        }
        EventLoop eventLoop$kotlinx_coroutines_core = ThreadLocalEventLoop.INSTANCE.getEventLoop$kotlinx_coroutines_core();
        if (eventLoop$kotlinx_coroutines_core.isUnconfinedLoopActive()) {
            this._state = value;
            this.resumeMode = 1;
            eventLoop$kotlinx_coroutines_core.dispatchUnconfined(this);
            return;
        }
        DispatchedContinuation<T> $this$runUnconfinedEventLoop$iv$iv = this;
        eventLoop$kotlinx_coroutines_core.incrementUseCount(true);
        try {
            Job job$iv = (Job) getContext().get(Job.INSTANCE);
            if (job$iv != null && !job$iv.isActive()) {
                CancellationException cancellationException = job$iv.getCancellationException();
                Result.Companion companion = Result.INSTANCE;
                resumeWith(Result.m16constructorimpl(ResultKt.createFailure(cancellationException)));
                z = true;
            } else {
                z = false;
            }
            if (!z) {
                CoroutineContext context$iv$iv = getContext();
                Object countOrElement$iv$iv = this.countOrElement;
                Object oldValue$iv$iv = ThreadContextKt.updateThreadContext(context$iv$iv, countOrElement$iv$iv);
                try {
                    Continuation<T> continuation = this.continuation;
                    Result.Companion companion2 = Result.INSTANCE;
                    continuation.resumeWith(Result.m16constructorimpl(value));
                    Unit unit = Unit.INSTANCE;
                    InlineMarker.finallyStart(1);
                    ThreadContextKt.restoreThreadContext(context$iv$iv, oldValue$iv$iv);
                    InlineMarker.finallyEnd(1);
                } catch (Throwable th) {
                    InlineMarker.finallyStart(1);
                    ThreadContextKt.restoreThreadContext(context$iv$iv, oldValue$iv$iv);
                    InlineMarker.finallyEnd(1);
                    throw th;
                }
            }
            while (eventLoop$kotlinx_coroutines_core.processUnconfinedEvent()) {
            }
            r1 = 1;
            InlineMarker.finallyStart(1);
        } catch (Throwable e$iv$iv) {
            try {
                $this$runUnconfinedEventLoop$iv$iv.handleFatalException$kotlinx_coroutines_core(e$iv$iv, null);
                r1 = 1;
                InlineMarker.finallyStart(1);
            } catch (Throwable th2) {
                InlineMarker.finallyStart(1);
                eventLoop$kotlinx_coroutines_core.decrementUseCount(true);
                InlineMarker.finallyEnd(1);
                throw th2;
            }
        }
        eventLoop$kotlinx_coroutines_core.decrementUseCount(r1);
        InlineMarker.finallyEnd(r1);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v3 */
    /* JADX WARN: Type inference failed for: r1v4, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r1v5 */
    public final void resumeCancellableWithException(Throwable exception) {
        ?? r1;
        boolean z;
        Intrinsics.checkParameterIsNotNull(exception, "exception");
        CoroutineContext context = this.continuation.getContext();
        boolean z2 = false;
        int i = 2;
        DefaultConstructorMarker defaultConstructorMarker = null;
        CompletedExceptionally state = new CompletedExceptionally(exception, z2, i, defaultConstructorMarker);
        if (this.dispatcher.isDispatchNeeded(context)) {
            this._state = new CompletedExceptionally(exception, z2, i, defaultConstructorMarker);
            this.resumeMode = 1;
            this.dispatcher.mo1008dispatch(context, this);
            return;
        }
        EventLoop eventLoop$kotlinx_coroutines_core = ThreadLocalEventLoop.INSTANCE.getEventLoop$kotlinx_coroutines_core();
        if (eventLoop$kotlinx_coroutines_core.isUnconfinedLoopActive()) {
            this._state = state;
            this.resumeMode = 1;
            eventLoop$kotlinx_coroutines_core.dispatchUnconfined(this);
            return;
        }
        DispatchedContinuation<T> $this$runUnconfinedEventLoop$iv$iv = this;
        eventLoop$kotlinx_coroutines_core.incrementUseCount(true);
        try {
            Job job$iv = (Job) getContext().get(Job.INSTANCE);
            if (job$iv != null && !job$iv.isActive()) {
                CancellationException cancellationException = job$iv.getCancellationException();
                Result.Companion companion = Result.INSTANCE;
                resumeWith(Result.m16constructorimpl(ResultKt.createFailure(cancellationException)));
                z = true;
            } else {
                z = false;
            }
            if (!z) {
                CoroutineContext context$iv$iv = getContext();
                Object countOrElement$iv$iv = this.countOrElement;
                Object oldValue$iv$iv = ThreadContextKt.updateThreadContext(context$iv$iv, countOrElement$iv$iv);
                try {
                    Continuation $this$resumeWithStackTrace$iv$iv = this.continuation;
                    Result.Companion companion2 = Result.INSTANCE;
                    Throwable exception$iv$iv = StackTraceRecoveryKt.recoverStackTrace(exception, $this$resumeWithStackTrace$iv$iv);
                    $this$resumeWithStackTrace$iv$iv.resumeWith(Result.m16constructorimpl(ResultKt.createFailure(exception$iv$iv)));
                    Unit unit = Unit.INSTANCE;
                    InlineMarker.finallyStart(1);
                    ThreadContextKt.restoreThreadContext(context$iv$iv, oldValue$iv$iv);
                    InlineMarker.finallyEnd(1);
                } catch (Throwable th) {
                    InlineMarker.finallyStart(1);
                    ThreadContextKt.restoreThreadContext(context$iv$iv, oldValue$iv$iv);
                    InlineMarker.finallyEnd(1);
                    throw th;
                }
            }
            while (eventLoop$kotlinx_coroutines_core.processUnconfinedEvent()) {
            }
            r1 = 1;
            InlineMarker.finallyStart(1);
        } catch (Throwable e$iv$iv) {
            try {
                $this$runUnconfinedEventLoop$iv$iv.handleFatalException$kotlinx_coroutines_core(e$iv$iv, null);
                r1 = 1;
                InlineMarker.finallyStart(1);
            } catch (Throwable th2) {
                InlineMarker.finallyStart(1);
                eventLoop$kotlinx_coroutines_core.decrementUseCount(true);
                InlineMarker.finallyEnd(1);
                throw th2;
            }
        }
        eventLoop$kotlinx_coroutines_core.decrementUseCount(r1);
        InlineMarker.finallyEnd(r1);
    }

    public final boolean resumeCancelled() {
        Job job = (Job) getContext().get(Job.INSTANCE);
        if (job != null && !job.isActive()) {
            CancellationException cancellationException = job.getCancellationException();
            Result.Companion companion = Result.INSTANCE;
            resumeWith(Result.m16constructorimpl(ResultKt.createFailure(cancellationException)));
            return true;
        }
        return false;
    }

    public final void resumeUndispatched(T value) {
        CoroutineContext context$iv = getContext();
        Object countOrElement$iv = this.countOrElement;
        Object oldValue$iv = ThreadContextKt.updateThreadContext(context$iv, countOrElement$iv);
        try {
            Continuation<T> continuation = this.continuation;
            Result.Companion companion = Result.INSTANCE;
            continuation.resumeWith(Result.m16constructorimpl(value));
            Unit unit = Unit.INSTANCE;
        } finally {
            InlineMarker.finallyStart(1);
            ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
            InlineMarker.finallyEnd(1);
        }
    }

    public final void resumeUndispatchedWithException(Throwable exception) {
        Intrinsics.checkParameterIsNotNull(exception, "exception");
        CoroutineContext context$iv = getContext();
        Object countOrElement$iv = this.countOrElement;
        Object oldValue$iv = ThreadContextKt.updateThreadContext(context$iv, countOrElement$iv);
        try {
            Continuation $this$resumeWithStackTrace$iv = this.continuation;
            Result.Companion companion = Result.INSTANCE;
            $this$resumeWithStackTrace$iv.resumeWith(Result.m16constructorimpl(ResultKt.createFailure(StackTraceRecoveryKt.recoverStackTrace(exception, $this$resumeWithStackTrace$iv))));
            Unit unit = Unit.INSTANCE;
        } finally {
            InlineMarker.finallyStart(1);
            ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
            InlineMarker.finallyEnd(1);
        }
    }

    public final void dispatchYield$kotlinx_coroutines_core(T value) {
        CoroutineContext context = this.continuation.getContext();
        this._state = value;
        this.resumeMode = 1;
        this.dispatcher.dispatchYield(context, this);
    }

    public String toString() {
        return "DispatchedContinuation[" + this.dispatcher + ", " + DebugStringsKt.toDebugString(this.continuation) + ']';
    }
}
