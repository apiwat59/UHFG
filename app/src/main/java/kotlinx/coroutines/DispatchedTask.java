package kotlinx.coroutines;

import java.util.concurrent.CancellationException;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.internal.StackTraceRecoveryKt;
import kotlinx.coroutines.internal.ThreadContextKt;
import kotlinx.coroutines.scheduling.Task;
import kotlinx.coroutines.scheduling.TaskContext;

/* compiled from: Dispatched.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u000e\b \u0018\u0000*\u0006\b\u0000\u0010\u0001 \u00002\u00060\u0002j\u0002`\u0003B\r\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u001f\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0010¢\u0006\u0002\b\u0011J\u0019\u0010\u0012\u001a\u0004\u0018\u00010\u00102\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0000¢\u0006\u0002\b\u0013J\u001f\u0010\u0014\u001a\u0002H\u0001\"\u0004\b\u0001\u0010\u00012\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0010¢\u0006\u0004\b\u0015\u0010\u0016J!\u0010\u0017\u001a\u00020\f2\b\u0010\u0018\u001a\u0004\u0018\u00010\u00102\b\u0010\u0019\u001a\u0004\u0018\u00010\u0010H\u0000¢\u0006\u0002\b\u001aJ\u0006\u0010\u001b\u001a\u00020\fJ\u000f\u0010\u001c\u001a\u0004\u0018\u00010\u000eH ¢\u0006\u0002\b\u001dR\u0018\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00000\bX \u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\nR\u0012\u0010\u0004\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000¨\u0006\u001e"}, d2 = {"Lkotlinx/coroutines/DispatchedTask;", "T", "Lkotlinx/coroutines/scheduling/Task;", "Lkotlinx/coroutines/SchedulerTask;", "resumeMode", "", "(I)V", "delegate", "Lkotlin/coroutines/Continuation;", "getDelegate$kotlinx_coroutines_core", "()Lkotlin/coroutines/Continuation;", "cancelResult", "", "state", "", "cause", "", "cancelResult$kotlinx_coroutines_core", "getExceptionalResult", "getExceptionalResult$kotlinx_coroutines_core", "getSuccessfulResult", "getSuccessfulResult$kotlinx_coroutines_core", "(Ljava/lang/Object;)Ljava/lang/Object;", "handleFatalException", "exception", "finallyException", "handleFatalException$kotlinx_coroutines_core", "run", "takeState", "takeState$kotlinx_coroutines_core", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public abstract class DispatchedTask<T> extends Task {
    public int resumeMode;

    public abstract Continuation<T> getDelegate$kotlinx_coroutines_core();

    public abstract Object takeState$kotlinx_coroutines_core();

    public DispatchedTask(int resumeMode) {
        this.resumeMode = resumeMode;
    }

    public void cancelResult$kotlinx_coroutines_core(Object state, Throwable cause) {
        Intrinsics.checkParameterIsNotNull(cause, "cause");
    }

    /* JADX WARN: Multi-variable type inference failed */
    public <T> T getSuccessfulResult$kotlinx_coroutines_core(Object state) {
        return state;
    }

    public final Throwable getExceptionalResult$kotlinx_coroutines_core(Object state) {
        CompletedExceptionally completedExceptionally = (CompletedExceptionally) (!(state instanceof CompletedExceptionally) ? null : state);
        if (completedExceptionally != null) {
            return completedExceptionally.cause;
        }
        return null;
    }

    @Override // java.lang.Runnable
    public final void run() {
        Object result;
        Continuation<T> delegate$kotlinx_coroutines_core;
        TaskContext taskContext = this.taskContext;
        Throwable fatalException = (Throwable) null;
        try {
            delegate$kotlinx_coroutines_core = getDelegate$kotlinx_coroutines_core();
        } catch (Throwable e) {
            fatalException = e;
            try {
                Result.Companion companion = Result.INSTANCE;
                DispatchedTask<T> dispatchedTask = this;
                taskContext.afterTask();
                result = Result.m16constructorimpl(Unit.INSTANCE);
            } catch (Throwable th) {
                th = th;
                Result.Companion companion2 = Result.INSTANCE;
                result = Result.m16constructorimpl(ResultKt.createFailure(th));
                handleFatalException$kotlinx_coroutines_core(fatalException, Result.m19exceptionOrNullimpl(result));
            }
        }
        if (delegate$kotlinx_coroutines_core == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.DispatchedContinuation<T>");
        }
        DispatchedContinuation delegate = (DispatchedContinuation) delegate$kotlinx_coroutines_core;
        Continuation continuation = delegate.continuation;
        CoroutineContext context = continuation.getContext();
        Object state = takeState$kotlinx_coroutines_core();
        Object countOrElement$iv = delegate.countOrElement;
        Object oldValue$iv = ThreadContextKt.updateThreadContext(context, countOrElement$iv);
        try {
            Throwable exception = getExceptionalResult$kotlinx_coroutines_core(state);
            Job job = ResumeModeKt.isCancellableMode(this.resumeMode) ? (Job) context.get(Job.INSTANCE) : null;
            if (exception == null && job != null && !job.isActive()) {
                CancellationException cause = job.getCancellationException();
                cancelResult$kotlinx_coroutines_core(state, cause);
                Result.Companion companion3 = Result.INSTANCE;
                continuation.resumeWith(Result.m16constructorimpl(ResultKt.createFailure(StackTraceRecoveryKt.recoverStackTrace(cause, continuation))));
            } else if (exception == null) {
                T successfulResult$kotlinx_coroutines_core = getSuccessfulResult$kotlinx_coroutines_core(state);
                Result.Companion companion4 = Result.INSTANCE;
                continuation.resumeWith(Result.m16constructorimpl(successfulResult$kotlinx_coroutines_core));
            } else {
                Result.Companion companion5 = Result.INSTANCE;
                continuation.resumeWith(Result.m16constructorimpl(ResultKt.createFailure(StackTraceRecoveryKt.recoverStackTrace(exception, continuation))));
            }
            Unit unit = Unit.INSTANCE;
            try {
                Result.Companion companion6 = Result.INSTANCE;
                DispatchedTask<T> dispatchedTask2 = this;
                taskContext.afterTask();
                result = Result.m16constructorimpl(Unit.INSTANCE);
            } catch (Throwable th2) {
                th = th2;
                Result.Companion companion22 = Result.INSTANCE;
                result = Result.m16constructorimpl(ResultKt.createFailure(th));
                handleFatalException$kotlinx_coroutines_core(fatalException, Result.m19exceptionOrNullimpl(result));
            }
            handleFatalException$kotlinx_coroutines_core(fatalException, Result.m19exceptionOrNullimpl(result));
        } finally {
            ThreadContextKt.restoreThreadContext(context, oldValue$iv);
        }
    }

    public final void handleFatalException$kotlinx_coroutines_core(Throwable exception, Throwable finallyException) {
        if (exception == null && finallyException == null) {
            return;
        }
        if (exception != null && finallyException != null) {
            kotlin.ExceptionsKt.addSuppressed(exception, finallyException);
        }
        Throwable cause = exception != null ? exception : finallyException;
        String str = "Fatal exception in coroutines machinery for " + this + ". Please read KDoc to 'handleFatalException' method and report this incident to maintainers";
        if (cause == null) {
            Intrinsics.throwNpe();
        }
        CoroutinesInternalError reason = new CoroutinesInternalError(str, cause);
        CoroutineExceptionHandlerKt.handleCoroutineException(getDelegate$kotlinx_coroutines_core().getContext(), reason);
    }
}
