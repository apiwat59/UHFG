package kotlinx.coroutines;

import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.CoroutineStackFrame;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.internal.StackTraceRecoveryKt;

/* compiled from: CancellableContinuationImpl.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000¦\u0001\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0003\n\u0000\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0011\u0018\u0000*\u0006\b\u0000\u0010\u0001 \u00002\b\u0012\u0004\u0012\u00028\u00000\u00022\b\u0012\u0004\u0012\u00028\u00000\u00032\u00060\u0004j\u0002`\u0005B\u001d\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00000\u0006\u0012\u0006\u0010\t\u001a\u00020\b¢\u0006\u0004\b\n\u0010\u000bJ\u0019\u0010\u000f\u001a\u00020\u000e2\b\u0010\r\u001a\u0004\u0018\u00010\fH\u0002¢\u0006\u0004\b\u000f\u0010\u0010J\u0019\u0010\u0014\u001a\u00020\u00132\b\u0010\u0012\u001a\u0004\u0018\u00010\u0011H\u0016¢\u0006\u0004\b\u0014\u0010\u0015J!\u0010\u0019\u001a\u00020\u000e2\b\u0010\u0016\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0012\u001a\u00020\u0011H\u0010¢\u0006\u0004\b\u0017\u0010\u0018J\u0017\u0010\u001b\u001a\u00020\u000e2\u0006\u0010\u001a\u001a\u00020\fH\u0016¢\u0006\u0004\b\u001b\u0010\u0010J\u0017\u0010\u001d\u001a\u00020\u000e2\u0006\u0010\u001c\u001a\u00020\bH\u0002¢\u0006\u0004\b\u001d\u0010\u001eJ\u000f\u0010\u001f\u001a\u00020\u000eH\u0002¢\u0006\u0004\b\u001f\u0010 J\u0017\u0010#\u001a\u00020\u00112\u0006\u0010\"\u001a\u00020!H\u0016¢\u0006\u0004\b#\u0010$J\u0011\u0010%\u001a\u0004\u0018\u00010\fH\u0001¢\u0006\u0004\b%\u0010&J\u0017\u0010)\u001a\n\u0018\u00010'j\u0004\u0018\u0001`(H\u0016¢\u0006\u0004\b)\u0010*J\u001f\u0010-\u001a\u00028\u0001\"\u0004\b\u0001\u0010\u00012\b\u0010\u0016\u001a\u0004\u0018\u00010\fH\u0010¢\u0006\u0004\b+\u0010,J\u000f\u0010.\u001a\u00020\u000eH\u0016¢\u0006\u0004\b.\u0010 J\u000f\u0010/\u001a\u00020\u000eH\u0002¢\u0006\u0004\b/\u0010 J\u001e\u00102\u001a\u00020\u000e2\f\u00101\u001a\b\u0012\u0004\u0012\u00020\u000e00H\u0082\b¢\u0006\u0004\b2\u00103J8\u00109\u001a\u00020\u000e2'\u00108\u001a#\u0012\u0015\u0012\u0013\u0018\u00010\u0011¢\u0006\f\b5\u0012\b\b6\u0012\u0004\b\b(\u0012\u0012\u0004\u0012\u00020\u000e04j\u0002`7H\u0016¢\u0006\u0004\b9\u0010:J8\u0010<\u001a\u00020;2'\u00108\u001a#\u0012\u0015\u0012\u0013\u0018\u00010\u0011¢\u0006\f\b5\u0012\b\b6\u0012\u0004\b\b(\u0012\u0012\u0004\u0012\u00020\u000e04j\u0002`7H\u0002¢\u0006\u0004\b<\u0010=JB\u0010>\u001a\u00020\u000e2'\u00108\u001a#\u0012\u0015\u0012\u0013\u0018\u00010\u0011¢\u0006\f\b5\u0012\b\b6\u0012\u0004\b\b(\u0012\u0012\u0004\u0012\u00020\u000e04j\u0002`72\b\u0010\u0016\u001a\u0004\u0018\u00010\fH\u0002¢\u0006\u0004\b>\u0010?J\u000f\u0010A\u001a\u00020@H\u0014¢\u0006\u0004\bA\u0010BJ:\u0010E\u001a\u00020\u000e2\u0006\u0010C\u001a\u00028\u00002!\u0010D\u001a\u001d\u0012\u0013\u0012\u00110\u0011¢\u0006\f\b5\u0012\b\b6\u0012\u0004\b\b(\u0012\u0012\u0004\u0012\u00020\u000e04H\u0016¢\u0006\u0004\bE\u0010FJ#\u0010H\u001a\u0004\u0018\u00010G2\b\u0010\r\u001a\u0004\u0018\u00010\f2\u0006\u0010\t\u001a\u00020\bH\u0002¢\u0006\u0004\bH\u0010IJ \u0010L\u001a\u00020\u000e2\f\u0010K\u001a\b\u0012\u0004\u0012\u00028\u00000JH\u0016ø\u0001\u0000¢\u0006\u0004\bL\u0010\u0010J!\u0010P\u001a\u0004\u0018\u00010G2\u0006\u0010M\u001a\u00020\u00112\u0006\u0010\u001c\u001a\u00020\bH\u0000¢\u0006\u0004\bN\u0010OJ\u0011\u0010R\u001a\u0004\u0018\u00010\fH\u0010¢\u0006\u0004\bQ\u0010&J\u000f\u0010S\u001a\u00020@H\u0016¢\u0006\u0004\bS\u0010BJ\u000f\u0010T\u001a\u00020\u0013H\u0002¢\u0006\u0004\bT\u0010UJ#\u0010T\u001a\u0004\u0018\u00010\f2\u0006\u0010C\u001a\u00028\u00002\b\u0010V\u001a\u0004\u0018\u00010\fH\u0016¢\u0006\u0004\bT\u0010WJ\u0019\u0010X\u001a\u0004\u0018\u00010\f2\u0006\u0010M\u001a\u00020\u0011H\u0016¢\u0006\u0004\bX\u0010YJ\u000f\u0010Z\u001a\u00020\u0013H\u0002¢\u0006\u0004\bZ\u0010UJ\u001b\u0010\\\u001a\u00020\u000e*\u00020[2\u0006\u0010C\u001a\u00028\u0000H\u0016¢\u0006\u0004\b\\\u0010]J\u001b\u0010^\u001a\u00020\u000e*\u00020[2\u0006\u0010M\u001a\u00020\u0011H\u0016¢\u0006\u0004\b^\u0010_R\u001e\u0010b\u001a\n\u0018\u00010\u0004j\u0004\u0018\u0001`\u00058V@\u0016X\u0096\u0004¢\u0006\u0006\u001a\u0004\b`\u0010aR\u001c\u0010d\u001a\u00020c8\u0016@\u0016X\u0096\u0004¢\u0006\f\n\u0004\bd\u0010e\u001a\u0004\bf\u0010gR\"\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00000\u00068\u0000@\u0000X\u0080\u0004¢\u0006\f\n\u0004\b\u0007\u0010h\u001a\u0004\bi\u0010jR\u0016\u0010k\u001a\u00020\u00138V@\u0016X\u0096\u0004¢\u0006\u0006\u001a\u0004\bk\u0010UR\u0016\u0010l\u001a\u00020\u00138V@\u0016X\u0096\u0004¢\u0006\u0006\u001a\u0004\bl\u0010UR\u0016\u0010m\u001a\u00020\u00138V@\u0016X\u0096\u0004¢\u0006\u0006\u001a\u0004\bm\u0010UR\u0018\u0010o\u001a\u0004\u0018\u00010n8\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\bo\u0010pR\u0018\u0010\u0016\u001a\u0004\u0018\u00010\f8@@\u0000X\u0080\u0004¢\u0006\u0006\u001a\u0004\bq\u0010&\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006r"}, d2 = {"Lkotlinx/coroutines/CancellableContinuationImpl;", "T", "Lkotlinx/coroutines/DispatchedTask;", "Lkotlinx/coroutines/CancellableContinuation;", "Lkotlin/coroutines/jvm/internal/CoroutineStackFrame;", "Lkotlinx/coroutines/internal/CoroutineStackFrame;", "Lkotlin/coroutines/Continuation;", "delegate", "", "resumeMode", "<init>", "(Lkotlin/coroutines/Continuation;I)V", "", "proposedUpdate", "", "alreadyResumedError", "(Ljava/lang/Object;)V", "", "cause", "", "cancel", "(Ljava/lang/Throwable;)Z", "state", "cancelResult$kotlinx_coroutines_core", "(Ljava/lang/Object;Ljava/lang/Throwable;)V", "cancelResult", "token", "completeResume", "mode", "dispatchResume", "(I)V", "disposeParentHandle", "()V", "Lkotlinx/coroutines/Job;", "parent", "getContinuationCancellationCause", "(Lkotlinx/coroutines/Job;)Ljava/lang/Throwable;", "getResult", "()Ljava/lang/Object;", "Ljava/lang/StackTraceElement;", "Lkotlinx/coroutines/internal/StackTraceElement;", "getStackTraceElement", "()Ljava/lang/StackTraceElement;", "getSuccessfulResult$kotlinx_coroutines_core", "(Ljava/lang/Object;)Ljava/lang/Object;", "getSuccessfulResult", "initCancellability", "installParentCancellationHandler", "Lkotlin/Function0;", "block", "invokeHandlerSafely", "(Lkotlin/jvm/functions/Function0;)V", "Lkotlin/Function1;", "Lkotlin/ParameterName;", "name", "Lkotlinx/coroutines/CompletionHandler;", "handler", "invokeOnCancellation", "(Lkotlin/jvm/functions/Function1;)V", "Lkotlinx/coroutines/CancelHandler;", "makeHandler", "(Lkotlin/jvm/functions/Function1;)Lkotlinx/coroutines/CancelHandler;", "multipleHandlersError", "(Lkotlin/jvm/functions/Function1;Ljava/lang/Object;)V", "", "nameString", "()Ljava/lang/String;", "value", "onCancellation", "resume", "(Ljava/lang/Object;Lkotlin/jvm/functions/Function1;)V", "Lkotlinx/coroutines/CancelledContinuation;", "resumeImpl", "(Ljava/lang/Object;I)Lkotlinx/coroutines/CancelledContinuation;", "Lkotlin/Result;", "result", "resumeWith", "exception", "resumeWithExceptionMode$kotlinx_coroutines_core", "(Ljava/lang/Throwable;I)Lkotlinx/coroutines/CancelledContinuation;", "resumeWithExceptionMode", "takeState$kotlinx_coroutines_core", "takeState", "toString", "tryResume", "()Z", "idempotent", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "tryResumeWithException", "(Ljava/lang/Throwable;)Ljava/lang/Object;", "trySuspend", "Lkotlinx/coroutines/CoroutineDispatcher;", "resumeUndispatched", "(Lkotlinx/coroutines/CoroutineDispatcher;Ljava/lang/Object;)V", "resumeUndispatchedWithException", "(Lkotlinx/coroutines/CoroutineDispatcher;Ljava/lang/Throwable;)V", "getCallerFrame", "()Lkotlin/coroutines/jvm/internal/CoroutineStackFrame;", "callerFrame", "Lkotlin/coroutines/CoroutineContext;", "context", "Lkotlin/coroutines/CoroutineContext;", "getContext", "()Lkotlin/coroutines/CoroutineContext;", "Lkotlin/coroutines/Continuation;", "getDelegate$kotlinx_coroutines_core", "()Lkotlin/coroutines/Continuation;", "isActive", "isCancelled", "isCompleted", "Lkotlinx/coroutines/DisposableHandle;", "parentHandle", "Lkotlinx/coroutines/DisposableHandle;", "getState$kotlinx_coroutines_core", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public class CancellableContinuationImpl<T> extends DispatchedTask<T> implements CancellableContinuation<T>, CoroutineStackFrame {
    private static final AtomicIntegerFieldUpdater _decision$FU = AtomicIntegerFieldUpdater.newUpdater(CancellableContinuationImpl.class, "_decision");
    private static final AtomicReferenceFieldUpdater _state$FU = AtomicReferenceFieldUpdater.newUpdater(CancellableContinuationImpl.class, Object.class, "_state");
    private volatile int _decision;
    private volatile Object _state;
    private final CoroutineContext context;
    private final Continuation<T> delegate;
    private volatile DisposableHandle parentHandle;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public CancellableContinuationImpl(Continuation<? super T> delegate, int resumeMode) {
        super(resumeMode);
        Intrinsics.checkParameterIsNotNull(delegate, "delegate");
        this.delegate = delegate;
        this.context = delegate.get$context();
        this._decision = 0;
        this._state = Active.INSTANCE;
    }

    @Override // kotlinx.coroutines.DispatchedTask
    public final Continuation<T> getDelegate$kotlinx_coroutines_core() {
        return this.delegate;
    }

    @Override // kotlin.coroutines.Continuation
    /* renamed from: getContext, reason: from getter */
    public CoroutineContext get$context() {
        return this.context;
    }

    /* renamed from: getState$kotlinx_coroutines_core, reason: from getter */
    public final Object get_state() {
        return this._state;
    }

    @Override // kotlinx.coroutines.CancellableContinuation
    public boolean isActive() {
        return get_state() instanceof NotCompleted;
    }

    @Override // kotlinx.coroutines.CancellableContinuation
    public boolean isCompleted() {
        return !(get_state() instanceof NotCompleted);
    }

    @Override // kotlinx.coroutines.CancellableContinuation
    public boolean isCancelled() {
        return get_state() instanceof CancelledContinuation;
    }

    @Override // kotlinx.coroutines.CancellableContinuation
    public /* synthetic */ void initCancellability() {
    }

    private final void installParentCancellationHandler() {
        Job parent;
        if (isCompleted() || (parent = (Job) this.delegate.get$context().get(Job.INSTANCE)) == null) {
            return;
        }
        parent.start();
        CompletionHandlerBase $this$asHandler$iv = new ChildContinuation(parent, this);
        DisposableHandle handle = Job.DefaultImpls.invokeOnCompletion$default(parent, true, false, $this$asHandler$iv, 2, null);
        this.parentHandle = handle;
        if (isCompleted()) {
            handle.dispose();
            this.parentHandle = NonDisposableHandle.INSTANCE;
        }
    }

    @Override // kotlin.coroutines.jvm.internal.CoroutineStackFrame
    public CoroutineStackFrame getCallerFrame() {
        Continuation<T> continuation = this.delegate;
        if (!(continuation instanceof CoroutineStackFrame)) {
            continuation = null;
        }
        return (CoroutineStackFrame) continuation;
    }

    @Override // kotlin.coroutines.jvm.internal.CoroutineStackFrame
    public StackTraceElement getStackTraceElement() {
        return null;
    }

    @Override // kotlinx.coroutines.DispatchedTask
    public Object takeState$kotlinx_coroutines_core() {
        return get_state();
    }

    @Override // kotlinx.coroutines.DispatchedTask
    public void cancelResult$kotlinx_coroutines_core(Object state, Throwable cause) {
        Intrinsics.checkParameterIsNotNull(cause, "cause");
        if (state instanceof CompletedWithCancellation) {
            try {
                ((CompletedWithCancellation) state).onCancellation.invoke(cause);
            } catch (Throwable ex$iv) {
                CoroutineExceptionHandlerKt.handleCoroutineException(get$context(), new CompletionHandlerException("Exception in cancellation handler for " + this, ex$iv));
            }
        }
    }

    @Override // kotlinx.coroutines.CancellableContinuation
    public boolean cancel(Throwable cause) {
        Object state;
        CancelledContinuation update;
        do {
            state = this._state;
            if (!(state instanceof NotCompleted)) {
                return false;
            }
            update = new CancelledContinuation(this, cause, state instanceof CancelHandler);
        } while (!_state$FU.compareAndSet(this, state, update));
        if (state instanceof CancelHandler) {
            try {
                ((CancelHandler) state).invoke(cause);
            } catch (Throwable ex$iv) {
                CoroutineExceptionHandlerKt.handleCoroutineException(get$context(), new CompletionHandlerException("Exception in cancellation handler for " + this, ex$iv));
            }
        }
        disposeParentHandle();
        dispatchResume(0);
        return true;
    }

    private final void invokeHandlerSafely(Function0<Unit> block) {
        try {
            block.invoke();
        } catch (Throwable ex) {
            CoroutineExceptionHandlerKt.handleCoroutineException(get$context(), new CompletionHandlerException("Exception in cancellation handler for " + this, ex));
        }
    }

    public Throwable getContinuationCancellationCause(Job parent) {
        Intrinsics.checkParameterIsNotNull(parent, "parent");
        return parent.getCancellationException();
    }

    private final boolean trySuspend() {
        do {
            int decision = this._decision;
            if (decision != 0) {
                if (decision == 2) {
                    return false;
                }
                throw new IllegalStateException("Already suspended".toString());
            }
        } while (!_decision$FU.compareAndSet(this, 0, 1));
        return true;
    }

    private final boolean tryResume() {
        do {
            int decision = this._decision;
            if (decision != 0) {
                if (decision == 1) {
                    return false;
                }
                throw new IllegalStateException("Already resumed".toString());
            }
        } while (!_decision$FU.compareAndSet(this, 0, 2));
        return true;
    }

    public final Object getResult() {
        Job job;
        installParentCancellationHandler();
        if (trySuspend()) {
            return IntrinsicsKt.getCOROUTINE_SUSPENDED();
        }
        Object state = get_state();
        if (state instanceof CompletedExceptionally) {
            throw StackTraceRecoveryKt.recoverStackTrace(((CompletedExceptionally) state).cause, this);
        }
        if (this.resumeMode == 1 && (job = (Job) get$context().get(Job.INSTANCE)) != null && !job.isActive()) {
            CancellationException cause = job.getCancellationException();
            cancelResult$kotlinx_coroutines_core(state, cause);
            throw StackTraceRecoveryKt.recoverStackTrace(cause, this);
        }
        return getSuccessfulResult$kotlinx_coroutines_core(state);
    }

    @Override // kotlin.coroutines.Continuation
    public void resumeWith(Object result) {
        resumeImpl(CompletedExceptionallyKt.toState(result), this.resumeMode);
    }

    @Override // kotlinx.coroutines.CancellableContinuation
    public void resume(T value, Function1<? super Throwable, Unit> onCancellation) {
        Intrinsics.checkParameterIsNotNull(onCancellation, "onCancellation");
        CancelledContinuation cancelled = resumeImpl(new CompletedWithCancellation(value, onCancellation), this.resumeMode);
        if (cancelled != null) {
            try {
                onCancellation.invoke(cancelled.cause);
            } catch (Throwable ex$iv) {
                CoroutineExceptionHandlerKt.handleCoroutineException(get$context(), new CompletionHandlerException("Exception in cancellation handler for " + this, ex$iv));
            }
        }
    }

    public final CancelledContinuation resumeWithExceptionMode$kotlinx_coroutines_core(Throwable exception, int mode) {
        Intrinsics.checkParameterIsNotNull(exception, "exception");
        return resumeImpl(new CompletedExceptionally(exception, false, 2, null), mode);
    }

    @Override // kotlinx.coroutines.CancellableContinuation
    public void invokeOnCancellation(Function1<? super Throwable, Unit> handler) {
        CancelHandler cancelHandler;
        Intrinsics.checkParameterIsNotNull(handler, "handler");
        CancelHandler node = (CancelHandler) null;
        while (true) {
            Object state = this._state;
            if (state instanceof Active) {
                if (node != null) {
                    cancelHandler = node;
                } else {
                    CancelHandler it = makeHandler(handler);
                    cancelHandler = it;
                    node = it;
                }
                if (_state$FU.compareAndSet(this, state, node)) {
                    return;
                } else {
                    node = cancelHandler;
                }
            } else {
                if (!(state instanceof CancelHandler)) {
                    if (state instanceof CancelledContinuation) {
                        if (!((CancelledContinuation) state).makeHandled()) {
                            multipleHandlersError(handler, state);
                        }
                        try {
                            CompletedExceptionally completedExceptionally = (CompletedExceptionally) (!(state instanceof CompletedExceptionally) ? null : state);
                            Throwable cause$iv = completedExceptionally != null ? completedExceptionally.cause : null;
                            handler.invoke(cause$iv);
                            return;
                        } catch (Throwable ex$iv) {
                            CoroutineExceptionHandlerKt.handleCoroutineException(get$context(), new CompletionHandlerException("Exception in cancellation handler for " + this, ex$iv));
                            return;
                        }
                    }
                    return;
                }
                multipleHandlersError(handler, state);
            }
        }
    }

    private final void multipleHandlersError(Function1<? super Throwable, Unit> handler, Object state) {
        throw new IllegalStateException(("It's prohibited to register multiple handlers, tried to register " + handler + ", already has " + state).toString());
    }

    private final CancelHandler makeHandler(Function1<? super Throwable, Unit> handler) {
        return handler instanceof CancelHandler ? (CancelHandler) handler : new InvokeOnCancel(handler);
    }

    private final void dispatchResume(int mode) {
        if (tryResume()) {
            return;
        }
        DispatchedKt.dispatch(this, mode);
    }

    private final CancelledContinuation resumeImpl(Object proposedUpdate, int resumeMode) {
        while (true) {
            Object state = this._state;
            if (state instanceof NotCompleted) {
                if (_state$FU.compareAndSet(this, state, proposedUpdate)) {
                    disposeParentHandle();
                    dispatchResume(resumeMode);
                    return null;
                }
            } else {
                if ((state instanceof CancelledContinuation) && ((CancelledContinuation) state).makeResumed()) {
                    return (CancelledContinuation) state;
                }
                alreadyResumedError(proposedUpdate);
            }
        }
    }

    private final void alreadyResumedError(Object proposedUpdate) {
        throw new IllegalStateException(("Already resumed, but proposed with update " + proposedUpdate).toString());
    }

    private final void disposeParentHandle() {
        DisposableHandle it = this.parentHandle;
        if (it != null) {
            it.dispose();
            this.parentHandle = NonDisposableHandle.INSTANCE;
        }
    }

    @Override // kotlinx.coroutines.CancellableContinuation
    public Object tryResume(T value, Object idempotent) {
        Object state;
        Object update;
        do {
            state = this._state;
            if (state instanceof NotCompleted) {
                update = idempotent == null ? value : new CompletedIdempotentResult(idempotent, value, (NotCompleted) state);
            } else {
                if (!(state instanceof CompletedIdempotentResult) || ((CompletedIdempotentResult) state).idempotentResume != idempotent) {
                    return null;
                }
                if (DebugKt.getASSERTIONS_ENABLED()) {
                    if (!(((CompletedIdempotentResult) state).result == value)) {
                        throw new AssertionError();
                    }
                }
                return ((CompletedIdempotentResult) state).token;
            }
        } while (!_state$FU.compareAndSet(this, state, update));
        disposeParentHandle();
        return state;
    }

    @Override // kotlinx.coroutines.CancellableContinuation
    public Object tryResumeWithException(Throwable exception) {
        Object state;
        CompletedExceptionally update;
        Intrinsics.checkParameterIsNotNull(exception, "exception");
        do {
            state = this._state;
            if (!(state instanceof NotCompleted)) {
                return null;
            }
            update = new CompletedExceptionally(exception, false, 2, null);
        } while (!_state$FU.compareAndSet(this, state, update));
        disposeParentHandle();
        return state;
    }

    @Override // kotlinx.coroutines.CancellableContinuation
    public void completeResume(Object token) {
        Intrinsics.checkParameterIsNotNull(token, "token");
        dispatchResume(this.resumeMode);
    }

    @Override // kotlinx.coroutines.CancellableContinuation
    public void resumeUndispatched(CoroutineDispatcher resumeUndispatched, T t) {
        Intrinsics.checkParameterIsNotNull(resumeUndispatched, "$this$resumeUndispatched");
        Continuation<T> continuation = this.delegate;
        if (!(continuation instanceof DispatchedContinuation)) {
            continuation = null;
        }
        DispatchedContinuation dc = (DispatchedContinuation) continuation;
        resumeImpl(t, (dc != null ? dc.dispatcher : null) == resumeUndispatched ? 3 : this.resumeMode);
    }

    @Override // kotlinx.coroutines.CancellableContinuation
    public void resumeUndispatchedWithException(CoroutineDispatcher resumeUndispatchedWithException, Throwable exception) {
        Intrinsics.checkParameterIsNotNull(resumeUndispatchedWithException, "$this$resumeUndispatchedWithException");
        Intrinsics.checkParameterIsNotNull(exception, "exception");
        Continuation<T> continuation = this.delegate;
        if (!(continuation instanceof DispatchedContinuation)) {
            continuation = null;
        }
        DispatchedContinuation dc = (DispatchedContinuation) continuation;
        resumeImpl(new CompletedExceptionally(exception, false, 2, null), (dc != null ? dc.dispatcher : null) == resumeUndispatchedWithException ? 3 : this.resumeMode);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // kotlinx.coroutines.DispatchedTask
    public <T> T getSuccessfulResult$kotlinx_coroutines_core(Object state) {
        return state instanceof CompletedIdempotentResult ? (T) ((CompletedIdempotentResult) state).result : state instanceof CompletedWithCancellation ? (T) ((CompletedWithCancellation) state).result : state;
    }

    public String toString() {
        return nameString() + '(' + DebugStringsKt.toDebugString(this.delegate) + "){" + get_state() + "}@" + DebugStringsKt.getHexAddress(this);
    }

    protected String nameString() {
        return "CancellableContinuation";
    }
}
