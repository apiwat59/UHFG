package kotlinx.coroutines.intrinsics;

import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import kotlinx.coroutines.AbstractCoroutine;
import kotlinx.coroutines.CompletedExceptionally;
import kotlinx.coroutines.JobSupportKt;
import kotlinx.coroutines.TimeoutCancellationException;
import kotlinx.coroutines.internal.ScopesKt;
import kotlinx.coroutines.internal.ThreadContextKt;

/* compiled from: Undispatched.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000@\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a9\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u00022\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u0002H\u00020\u00042\u001a\u0010\u0005\u001a\u0016\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u0004\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006H\u0082\b\u001a>\u0010\b\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u0004\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u00062\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0004H\u0000ø\u0001\u0000¢\u0006\u0002\u0010\t\u001aR\u0010\b\u001a\u00020\u0001\"\u0004\b\u0000\u0010\n\"\u0004\b\u0001\u0010\u0002*\u001e\b\u0001\u0012\u0004\u0012\u0002H\n\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u0004\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u000b2\u0006\u0010\f\u001a\u0002H\n2\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0004H\u0000ø\u0001\u0000¢\u0006\u0002\u0010\r\u001a>\u0010\u000e\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u0004\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u00062\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0004H\u0000ø\u0001\u0000¢\u0006\u0002\u0010\t\u001aR\u0010\u000e\u001a\u00020\u0001\"\u0004\b\u0000\u0010\n\"\u0004\b\u0001\u0010\u0002*\u001e\b\u0001\u0012\u0004\u0012\u0002H\n\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u0004\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u000b2\u0006\u0010\f\u001a\u0002H\n2\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0004H\u0000ø\u0001\u0000¢\u0006\u0002\u0010\r\u001aY\u0010\u000f\u001a\u0004\u0018\u00010\u0007\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\n*\b\u0012\u0004\u0012\u0002H\u00020\u00102\u0006\u0010\f\u001a\u0002H\n2'\u0010\u0005\u001a#\b\u0001\u0012\u0004\u0012\u0002H\n\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u0004\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u000b¢\u0006\u0002\b\u0011H\u0000ø\u0001\u0000¢\u0006\u0002\u0010\u0012\u001aY\u0010\u0013\u001a\u0004\u0018\u00010\u0007\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\n*\b\u0012\u0004\u0012\u0002H\u00020\u00102\u0006\u0010\f\u001a\u0002H\n2'\u0010\u0005\u001a#\b\u0001\u0012\u0004\u0012\u0002H\n\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u0004\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u000b¢\u0006\u0002\b\u0011H\u0000ø\u0001\u0000¢\u0006\u0002\u0010\u0012\u001a?\u0010\u0014\u001a\u0004\u0018\u00010\u0007\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00102\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0016\u0012\u0004\u0012\u00020\u00170\u00062\u000e\u0010\u0018\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0019H\u0082\b\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u001a"}, d2 = {"startDirect", "", "T", "completion", "Lkotlin/coroutines/Continuation;", "block", "Lkotlin/Function1;", "", "startCoroutineUndispatched", "(Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)V", "R", "Lkotlin/Function2;", "receiver", "(Lkotlin/jvm/functions/Function2;Ljava/lang/Object;Lkotlin/coroutines/Continuation;)V", "startCoroutineUnintercepted", "startUndispatchedOrReturn", "Lkotlinx/coroutines/AbstractCoroutine;", "Lkotlin/ExtensionFunctionType;", "(Lkotlinx/coroutines/AbstractCoroutine;Ljava/lang/Object;Lkotlin/jvm/functions/Function2;)Ljava/lang/Object;", "startUndispatchedOrReturnIgnoreTimeout", "undispatchedResult", "shouldThrow", "", "", "startBlock", "Lkotlin/Function0;", "kotlinx-coroutines-core"}, k = 2, mv = {1, 1, 15})
/* loaded from: classes.dex */
public final class UndispatchedKt {
    public static final <T> void startCoroutineUnintercepted(Function1<? super Continuation<? super T>, ? extends Object> startCoroutineUnintercepted, Continuation<? super T> completion) {
        Intrinsics.checkParameterIsNotNull(startCoroutineUnintercepted, "$this$startCoroutineUnintercepted");
        Intrinsics.checkParameterIsNotNull(completion, "completion");
        Continuation actualCompletion$iv = DebugProbesKt.probeCoroutineCreated(completion);
        try {
            Object value$iv = ((Function1) TypeIntrinsics.beforeCheckcastToFunctionOfArity(startCoroutineUnintercepted, 1)).invoke(actualCompletion$iv);
            if (value$iv == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                return;
            }
            Result.Companion companion = Result.INSTANCE;
            actualCompletion$iv.resumeWith(Result.m16constructorimpl(value$iv));
        } catch (Throwable e$iv) {
            Result.Companion companion2 = Result.INSTANCE;
            actualCompletion$iv.resumeWith(Result.m16constructorimpl(ResultKt.createFailure(e$iv)));
        }
    }

    public static final <R, T> void startCoroutineUnintercepted(Function2<? super R, ? super Continuation<? super T>, ? extends Object> startCoroutineUnintercepted, R r, Continuation<? super T> completion) {
        Intrinsics.checkParameterIsNotNull(startCoroutineUnintercepted, "$this$startCoroutineUnintercepted");
        Intrinsics.checkParameterIsNotNull(completion, "completion");
        Continuation actualCompletion$iv = DebugProbesKt.probeCoroutineCreated(completion);
        try {
            Object value$iv = ((Function2) TypeIntrinsics.beforeCheckcastToFunctionOfArity(startCoroutineUnintercepted, 2)).invoke(r, actualCompletion$iv);
            if (value$iv == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                return;
            }
            Result.Companion companion = Result.INSTANCE;
            actualCompletion$iv.resumeWith(Result.m16constructorimpl(value$iv));
        } catch (Throwable e$iv) {
            Result.Companion companion2 = Result.INSTANCE;
            actualCompletion$iv.resumeWith(Result.m16constructorimpl(ResultKt.createFailure(e$iv)));
        }
    }

    public static final <T> void startCoroutineUndispatched(Function1<? super Continuation<? super T>, ? extends Object> startCoroutineUndispatched, Continuation<? super T> completion) {
        Intrinsics.checkParameterIsNotNull(startCoroutineUndispatched, "$this$startCoroutineUndispatched");
        Intrinsics.checkParameterIsNotNull(completion, "completion");
        Continuation actualCompletion$iv = DebugProbesKt.probeCoroutineCreated(completion);
        try {
            CoroutineContext context$iv = completion.get$context();
            Object oldValue$iv = ThreadContextKt.updateThreadContext(context$iv, null);
            try {
                Object value$iv = ((Function1) TypeIntrinsics.beforeCheckcastToFunctionOfArity(startCoroutineUndispatched, 1)).invoke(actualCompletion$iv);
                if (value$iv == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                    return;
                }
                Result.Companion companion = Result.INSTANCE;
                actualCompletion$iv.resumeWith(Result.m16constructorimpl(value$iv));
            } finally {
                ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
            }
        } catch (Throwable e$iv) {
            Result.Companion companion2 = Result.INSTANCE;
            actualCompletion$iv.resumeWith(Result.m16constructorimpl(ResultKt.createFailure(e$iv)));
        }
    }

    public static final <R, T> void startCoroutineUndispatched(Function2<? super R, ? super Continuation<? super T>, ? extends Object> startCoroutineUndispatched, R r, Continuation<? super T> completion) {
        Intrinsics.checkParameterIsNotNull(startCoroutineUndispatched, "$this$startCoroutineUndispatched");
        Intrinsics.checkParameterIsNotNull(completion, "completion");
        Continuation actualCompletion$iv = DebugProbesKt.probeCoroutineCreated(completion);
        try {
            CoroutineContext context$iv = completion.get$context();
            Object oldValue$iv = ThreadContextKt.updateThreadContext(context$iv, null);
            try {
                Object value$iv = ((Function2) TypeIntrinsics.beforeCheckcastToFunctionOfArity(startCoroutineUndispatched, 2)).invoke(r, actualCompletion$iv);
                if (value$iv == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                    return;
                }
                Result.Companion companion = Result.INSTANCE;
                actualCompletion$iv.resumeWith(Result.m16constructorimpl(value$iv));
            } finally {
                ThreadContextKt.restoreThreadContext(context$iv, oldValue$iv);
            }
        } catch (Throwable e$iv) {
            Result.Companion companion2 = Result.INSTANCE;
            actualCompletion$iv.resumeWith(Result.m16constructorimpl(ResultKt.createFailure(e$iv)));
        }
    }

    private static final <T> void startDirect(Continuation<? super T> continuation, Function1<? super Continuation<? super T>, ? extends Object> function1) {
        Continuation actualCompletion = DebugProbesKt.probeCoroutineCreated(continuation);
        try {
            Object value = function1.invoke(actualCompletion);
            if (value != IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                Result.Companion companion = Result.INSTANCE;
                actualCompletion.resumeWith(Result.m16constructorimpl(value));
            }
        } catch (Throwable e) {
            Result.Companion companion2 = Result.INSTANCE;
            actualCompletion.resumeWith(Result.m16constructorimpl(ResultKt.createFailure(e)));
        }
    }

    public static final <T, R> Object startUndispatchedOrReturn(AbstractCoroutine<? super T> startUndispatchedOrReturn, R r, Function2<? super R, ? super Continuation<? super T>, ? extends Object> block) {
        Object completedExceptionally;
        Intrinsics.checkParameterIsNotNull(startUndispatchedOrReturn, "$this$startUndispatchedOrReturn");
        Intrinsics.checkParameterIsNotNull(block, "block");
        startUndispatchedOrReturn.initParentJob$kotlinx_coroutines_core();
        try {
            completedExceptionally = ((Function2) TypeIntrinsics.beforeCheckcastToFunctionOfArity(block, 2)).invoke(r, startUndispatchedOrReturn);
        } catch (Throwable e$iv) {
            completedExceptionally = new CompletedExceptionally(e$iv, false, 2, null);
        }
        Object result$iv = completedExceptionally;
        if (result$iv == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            return IntrinsicsKt.getCOROUTINE_SUSPENDED();
        }
        if (startUndispatchedOrReturn.makeCompletingOnce$kotlinx_coroutines_core(result$iv, 4)) {
            Object state$iv = startUndispatchedOrReturn.getState$kotlinx_coroutines_core();
            if (state$iv instanceof CompletedExceptionally) {
                Throwable th = ((CompletedExceptionally) state$iv).cause;
                throw ScopesKt.tryRecover(startUndispatchedOrReturn, ((CompletedExceptionally) state$iv).cause);
            }
            return JobSupportKt.unboxState(state$iv);
        }
        return IntrinsicsKt.getCOROUTINE_SUSPENDED();
    }

    public static final <T, R> Object startUndispatchedOrReturnIgnoreTimeout(AbstractCoroutine<? super T> startUndispatchedOrReturnIgnoreTimeout, R r, Function2<? super R, ? super Continuation<? super T>, ? extends Object> block) {
        Object completedExceptionally;
        Intrinsics.checkParameterIsNotNull(startUndispatchedOrReturnIgnoreTimeout, "$this$startUndispatchedOrReturnIgnoreTimeout");
        Intrinsics.checkParameterIsNotNull(block, "block");
        startUndispatchedOrReturnIgnoreTimeout.initParentJob$kotlinx_coroutines_core();
        try {
            completedExceptionally = ((Function2) TypeIntrinsics.beforeCheckcastToFunctionOfArity(block, 2)).invoke(r, startUndispatchedOrReturnIgnoreTimeout);
        } catch (Throwable e$iv) {
            completedExceptionally = new CompletedExceptionally(e$iv, false, 2, null);
        }
        Object result$iv = completedExceptionally;
        if (result$iv == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            return IntrinsicsKt.getCOROUTINE_SUSPENDED();
        }
        if (startUndispatchedOrReturnIgnoreTimeout.makeCompletingOnce$kotlinx_coroutines_core(result$iv, 4)) {
            Object state$iv = startUndispatchedOrReturnIgnoreTimeout.getState$kotlinx_coroutines_core();
            if (state$iv instanceof CompletedExceptionally) {
                Throwable e = ((CompletedExceptionally) state$iv).cause;
                if (((e instanceof TimeoutCancellationException) && ((TimeoutCancellationException) e).coroutine == startUndispatchedOrReturnIgnoreTimeout) ? false : true) {
                    throw ScopesKt.tryRecover(startUndispatchedOrReturnIgnoreTimeout, ((CompletedExceptionally) state$iv).cause);
                }
                if (result$iv instanceof CompletedExceptionally) {
                    throw ScopesKt.tryRecover(startUndispatchedOrReturnIgnoreTimeout, ((CompletedExceptionally) result$iv).cause);
                }
                return result$iv;
            }
            return JobSupportKt.unboxState(state$iv);
        }
        return IntrinsicsKt.getCOROUTINE_SUSPENDED();
    }

    private static final <T> Object undispatchedResult(AbstractCoroutine<? super T> abstractCoroutine, Function1<? super Throwable, Boolean> function1, Function0<? extends Object> function0) {
        Object result;
        try {
            result = function0.invoke();
        } catch (Throwable e) {
            result = new CompletedExceptionally(e, false, 2, null);
        }
        if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            return IntrinsicsKt.getCOROUTINE_SUSPENDED();
        }
        if (abstractCoroutine.makeCompletingOnce$kotlinx_coroutines_core(result, 4)) {
            Object state = abstractCoroutine.getState$kotlinx_coroutines_core();
            if (state instanceof CompletedExceptionally) {
                if (function1.invoke(((CompletedExceptionally) state).cause).booleanValue()) {
                    throw ScopesKt.tryRecover(abstractCoroutine, ((CompletedExceptionally) state).cause);
                }
                if (!(result instanceof CompletedExceptionally)) {
                    return result;
                }
                throw ScopesKt.tryRecover(abstractCoroutine, ((CompletedExceptionally) result).cause);
            }
            return JobSupportKt.unboxState(state);
        }
        return IntrinsicsKt.getCOROUTINE_SUSPENDED();
    }
}
