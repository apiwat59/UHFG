package kotlinx.coroutines.channels;

import java.util.concurrent.CancellationException;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.ContinuationKt;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CancelHandler;
import kotlinx.coroutines.CancelHandlerBase;
import kotlinx.coroutines.CancellableContinuation;
import kotlinx.coroutines.CancellableContinuationImpl;
import kotlinx.coroutines.DebugKt;
import kotlinx.coroutines.DebugStringsKt;
import kotlinx.coroutines.DisposableHandle;
import kotlinx.coroutines.channels.ChannelIterator;
import kotlinx.coroutines.channels.ValueOrClosed;
import kotlinx.coroutines.internal.LockFreeLinkedListHead;
import kotlinx.coroutines.internal.LockFreeLinkedListKt;
import kotlinx.coroutines.internal.LockFreeLinkedListNode;
import kotlinx.coroutines.internal.StackTraceRecoveryKt;
import kotlinx.coroutines.internal.Symbol;
import kotlinx.coroutines.intrinsics.UndispatchedKt;
import kotlinx.coroutines.selects.SelectClause1;
import kotlinx.coroutines.selects.SelectInstance;
import kotlinx.coroutines.selects.SelectKt;

/* compiled from: AbstractChannel.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0080\u0001\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\b \u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003:\u0007IJKLMNOB\u0005¢\u0006\u0002\u0010\u0004J\u0012\u0010\u0016\u001a\u00020\u00062\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0007J\u0016\u0010\u0016\u001a\u00020\u00192\u000e\u0010\u0017\u001a\n\u0018\u00010\u001aj\u0004\u0018\u0001`\u001bJ\u0017\u0010\u001c\u001a\u00020\u00062\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0010¢\u0006\u0002\b\u001dJ\b\u0010\u001e\u001a\u00020\u0019H\u0014J\u000e\u0010\u001f\u001a\b\u0012\u0004\u0012\u00028\u00000 H\u0004J\u0016\u0010!\u001a\u00020\u00062\f\u0010\"\u001a\b\u0012\u0004\u0012\u00028\u00000#H\u0002JR\u0010$\u001a\u00020\u0006\"\u0004\b\u0001\u0010%2\f\u0010&\u001a\b\u0012\u0004\u0012\u0002H%0'2$\u0010(\u001a \b\u0001\u0012\u0006\u0012\u0004\u0018\u00010*\u0012\n\u0012\b\u0012\u0004\u0012\u0002H%0+\u0012\u0006\u0012\u0004\u0018\u00010*0)2\u0006\u0010,\u001a\u00020-H\u0002ø\u0001\u0000¢\u0006\u0002\u0010.J\u000f\u0010/\u001a\b\u0012\u0004\u0012\u00028\u000000H\u0086\u0002J\b\u00101\u001a\u00020\u0019H\u0014J\b\u00102\u001a\u00020\u0019H\u0014J\r\u00103\u001a\u0004\u0018\u00018\u0000¢\u0006\u0002\u00104J\n\u00105\u001a\u0004\u0018\u00010*H\u0014J\u0016\u00106\u001a\u0004\u0018\u00010*2\n\u0010&\u001a\u0006\u0012\u0002\b\u00030'H\u0014J\u0011\u0010\"\u001a\u00028\u0000H\u0086@ø\u0001\u0000¢\u0006\u0002\u00107J\u001a\u00108\u001a\b\u0012\u0004\u0012\u00028\u00000\u0012H\u0086@ø\u0001\u0000ø\u0001\u0000¢\u0006\u0002\u00107J\u0013\u00109\u001a\u0004\u0018\u00018\u0000H\u0086@ø\u0001\u0000¢\u0006\u0002\u00107J\u0019\u0010:\u001a\u0004\u0018\u00018\u00002\b\u0010;\u001a\u0004\u0018\u00010*H\u0002¢\u0006\u0002\u0010<J\u0017\u0010=\u001a\u00028\u00002\b\u0010;\u001a\u0004\u0018\u00010*H\u0002¢\u0006\u0002\u0010<J\u001f\u0010>\u001a\u0002H%\"\u0004\b\u0001\u0010%2\u0006\u0010,\u001a\u00020-H\u0082@ø\u0001\u0000¢\u0006\u0002\u0010?JH\u0010@\u001a\u00020\u0019\"\u0004\b\u0001\u0010%2\f\u0010&\u001a\b\u0012\u0004\u0012\u0002H%0'2\"\u0010(\u001a\u001e\b\u0001\u0012\u0004\u0012\u00028\u0000\u0012\n\u0012\b\u0012\u0004\u0012\u0002H%0+\u0012\u0006\u0012\u0004\u0018\u00010*0)H\u0002ø\u0001\u0000¢\u0006\u0002\u0010AJQ\u0010B\u001a\u00020\u0019\"\u0004\b\u0001\u0010%2\f\u0010&\u001a\b\u0012\u0004\u0012\u0002H%0'2(\u0010(\u001a$\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u0012\u0012\n\u0012\b\u0012\u0004\u0012\u0002H%0+\u0012\u0006\u0012\u0004\u0018\u00010*0)H\u0002ø\u0001\u0000ø\u0001\u0000¢\u0006\u0002\u0010AJJ\u0010C\u001a\u00020\u0019\"\u0004\b\u0001\u0010%2\f\u0010&\u001a\b\u0012\u0004\u0012\u0002H%0'2$\u0010(\u001a \b\u0001\u0012\u0006\u0012\u0004\u0018\u00018\u0000\u0012\n\u0012\b\u0012\u0004\u0012\u0002H%0+\u0012\u0006\u0012\u0004\u0018\u00010*0)H\u0002ø\u0001\u0000¢\u0006\u0002\u0010AJ \u0010D\u001a\u00020\u00192\n\u0010E\u001a\u0006\u0012\u0002\b\u00030F2\n\u0010\"\u001a\u0006\u0012\u0002\b\u00030#H\u0002J\u0010\u0010G\u001a\n\u0012\u0004\u0012\u00028\u0000\u0018\u00010HH\u0014R\u0014\u0010\u0005\u001a\u00020\u00068DX\u0084\u0004¢\u0006\u0006\u001a\u0004\b\u0007\u0010\bR\u0012\u0010\t\u001a\u00020\u0006X¤\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\bR\u0012\u0010\n\u001a\u00020\u0006X¤\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\bR\u0011\u0010\u000b\u001a\u00020\u00068F¢\u0006\u0006\u001a\u0004\b\u000b\u0010\bR\u0011\u0010\f\u001a\u00020\u00068F¢\u0006\u0006\u001a\u0004\b\f\u0010\bR\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00028\u00000\u000e8F¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R#\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00000\u00120\u000e8VX\u0096\u0004ø\u0001\u0000¢\u0006\u0006\u001a\u0004\b\u0013\u0010\u0010R\u0019\u0010\u0014\u001a\n\u0012\u0006\u0012\u0004\u0018\u00018\u00000\u000e8F¢\u0006\u0006\u001a\u0004\b\u0015\u0010\u0010\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006P"}, d2 = {"Lkotlinx/coroutines/channels/AbstractChannel;", "E", "Lkotlinx/coroutines/channels/AbstractSendChannel;", "Lkotlinx/coroutines/channels/Channel;", "()V", "hasReceiveOrClosed", "", "getHasReceiveOrClosed", "()Z", "isBufferAlwaysEmpty", "isBufferEmpty", "isClosedForReceive", "isEmpty", "onReceive", "Lkotlinx/coroutines/selects/SelectClause1;", "getOnReceive", "()Lkotlinx/coroutines/selects/SelectClause1;", "onReceiveOrClosed", "Lkotlinx/coroutines/channels/ValueOrClosed;", "getOnReceiveOrClosed", "onReceiveOrNull", "getOnReceiveOrNull", "cancel", "cause", "", "", "Ljava/util/concurrent/CancellationException;", "Lkotlinx/coroutines/CancellationException;", "cancelInternal", "cancelInternal$kotlinx_coroutines_core", "cleanupSendQueueOnCancel", "describeTryPoll", "Lkotlinx/coroutines/channels/AbstractChannel$TryPollDesc;", "enqueueReceive", "receive", "Lkotlinx/coroutines/channels/Receive;", "enqueueReceiveSelect", "R", "select", "Lkotlinx/coroutines/selects/SelectInstance;", "block", "Lkotlin/Function2;", "", "Lkotlin/coroutines/Continuation;", "receiveMode", "", "(Lkotlinx/coroutines/selects/SelectInstance;Lkotlin/jvm/functions/Function2;I)Z", "iterator", "Lkotlinx/coroutines/channels/ChannelIterator;", "onReceiveDequeued", "onReceiveEnqueued", "poll", "()Ljava/lang/Object;", "pollInternal", "pollSelectInternal", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "receiveOrClosed", "receiveOrNull", "receiveOrNullResult", "result", "(Ljava/lang/Object;)Ljava/lang/Object;", "receiveResult", "receiveSuspend", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "registerSelectReceive", "(Lkotlinx/coroutines/selects/SelectInstance;Lkotlin/jvm/functions/Function2;)V", "registerSelectReceiveOrClosed", "registerSelectReceiveOrNull", "removeReceiveOnCancel", "cont", "Lkotlinx/coroutines/CancellableContinuation;", "takeFirstReceiveOrPeekClosed", "Lkotlinx/coroutines/channels/ReceiveOrClosed;", "IdempotentTokenValue", "Itr", "ReceiveElement", "ReceiveHasNext", "ReceiveSelect", "RemoveReceiveOnCancel", "TryPollDesc", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public abstract class AbstractChannel<E> extends AbstractSendChannel<E> implements Channel<E> {
    protected abstract boolean isBufferAlwaysEmpty();

    protected abstract boolean isBufferEmpty();

    @Override // kotlinx.coroutines.channels.ReceiveChannel
    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Since 1.2.0, binary compatibility with versions <= 1.1.x")
    public /* synthetic */ void cancel() {
        cancel((CancellationException) null);
    }

    protected Object pollInternal() {
        Send send;
        Object token;
        do {
            send = takeFirstSendOrPeekClosed();
            if (send == null) {
                return AbstractChannelKt.POLL_FAILED;
            }
            token = send.tryResumeSend(null);
        } while (token == null);
        send.completeResumeSend(token);
        return send.getPollResult();
    }

    protected Object pollSelectInternal(SelectInstance<?> select) {
        Intrinsics.checkParameterIsNotNull(select, "select");
        TryPollDesc pollOp = describeTryPoll();
        Object failure = select.performAtomicTrySelect(pollOp);
        if (failure != null) {
            return failure;
        }
        Send send = pollOp.getResult();
        Object obj = pollOp.resumeToken;
        if (obj == null) {
            Intrinsics.throwNpe();
        }
        send.completeResumeSend(obj);
        return pollOp.pollResult;
    }

    protected final boolean getHasReceiveOrClosed() {
        return getQueue().getNextNode() instanceof ReceiveOrClosed;
    }

    @Override // kotlinx.coroutines.channels.ReceiveChannel
    public final boolean isClosedForReceive() {
        return getClosedForReceive() != null && isBufferEmpty();
    }

    @Override // kotlinx.coroutines.channels.ReceiveChannel
    public final boolean isEmpty() {
        return !(getQueue().getNextNode() instanceof Send) && isBufferEmpty();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // kotlinx.coroutines.channels.ReceiveChannel
    public final Object receive(Continuation<? super E> continuation) {
        Object result = pollInternal();
        return result != AbstractChannelKt.POLL_FAILED ? receiveResult(result) : receiveSuspend(0, continuation);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final E receiveResult(Object result) {
        if (result instanceof Closed) {
            throw StackTraceRecoveryKt.recoverStackTrace(((Closed) result).getReceiveException());
        }
        return result;
    }

    /* JADX WARN: Multi-variable type inference failed */
    final /* synthetic */ <R> Object receiveSuspend(int receiveMode, Continuation<? super R> continuation) {
        CancellableContinuationImpl cancellable$iv = new CancellableContinuationImpl(IntrinsicsKt.intercepted(continuation), 0);
        CancellableContinuationImpl cont = cancellable$iv;
        ReceiveElement receiveElement = new ReceiveElement(cont, receiveMode);
        while (true) {
            if (enqueueReceive(receiveElement)) {
                removeReceiveOnCancel(cont, receiveElement);
                break;
            }
            Object result = pollInternal();
            if (result instanceof Closed) {
                receiveElement.resumeReceiveClosed((Closed) result);
                break;
            }
            if (result != AbstractChannelKt.POLL_FAILED) {
                Object resumeValue = receiveElement.resumeValue(result);
                Result.Companion companion = Result.INSTANCE;
                cont.resumeWith(Result.m16constructorimpl(resumeValue));
                break;
            }
        }
        Object result2 = cancellable$iv.getResult();
        if (result2 == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean enqueueReceive(Receive<? super E> receive) {
        boolean z = false;
        if (isBufferAlwaysEmpty()) {
            LockFreeLinkedListNode this_$iv = getQueue();
            while (true) {
                Object prev = this_$iv.getPrev();
                if (prev != null) {
                    LockFreeLinkedListNode prev$iv = (LockFreeLinkedListNode) prev;
                    if (!(!(prev$iv instanceof Send))) {
                        break;
                    }
                    if (prev$iv.addNext(receive, this_$iv)) {
                        z = true;
                        break;
                    }
                } else {
                    throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
                }
            }
        } else {
            LockFreeLinkedListNode this_$iv2 = getQueue();
            final Receive<? super E> receive2 = receive;
            final Receive<? super E> receive3 = receive;
            LockFreeLinkedListNode.CondAddOp condAdd$iv = new LockFreeLinkedListNode.CondAddOp(receive3) { // from class: kotlinx.coroutines.channels.AbstractChannel$enqueueReceive$$inlined$addLastIfPrevAndIf$1
                @Override // kotlinx.coroutines.internal.AtomicOp
                public Object prepare(LockFreeLinkedListNode affected) {
                    Intrinsics.checkParameterIsNotNull(affected, "affected");
                    if (this.isBufferEmpty()) {
                        return null;
                    }
                    return LockFreeLinkedListKt.getCONDITION_FALSE();
                }
            };
            while (true) {
                Object prev2 = this_$iv2.getPrev();
                if (prev2 != null) {
                    LockFreeLinkedListNode prev$iv2 = (LockFreeLinkedListNode) prev2;
                    if (!(!(prev$iv2 instanceof Send))) {
                        break;
                    }
                    int tryCondAddNext = prev$iv2.tryCondAddNext(receive, this_$iv2, condAdd$iv);
                    if (tryCondAddNext == 1) {
                        z = true;
                        break;
                    }
                    if (tryCondAddNext == 2) {
                        break;
                    }
                } else {
                    throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
                }
            }
        }
        boolean result = z;
        if (result) {
            onReceiveEnqueued();
        }
        return result;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // kotlinx.coroutines.channels.ReceiveChannel
    public final Object receiveOrNull(Continuation<? super E> continuation) {
        Object result = pollInternal();
        return result != AbstractChannelKt.POLL_FAILED ? receiveOrNullResult(result) : receiveSuspend(1, continuation);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final E receiveOrNullResult(Object result) {
        if (result instanceof Closed) {
            if (((Closed) result).closeCause != null) {
                throw StackTraceRecoveryKt.recoverStackTrace(((Closed) result).closeCause);
            }
            return null;
        }
        return result;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // kotlinx.coroutines.channels.ReceiveChannel
    public final Object receiveOrClosed(Continuation<? super ValueOrClosed<? extends E>> continuation) {
        Object m996constructorimpl;
        Object result = pollInternal();
        if (result == AbstractChannelKt.POLL_FAILED) {
            return receiveSuspend(2, continuation);
        }
        if (!(result instanceof Closed)) {
            ValueOrClosed.Companion companion = ValueOrClosed.INSTANCE;
            m996constructorimpl = ValueOrClosed.m996constructorimpl(result);
        } else {
            ValueOrClosed.Companion companion2 = ValueOrClosed.INSTANCE;
            Throwable cause$iv$iv = ((Closed) result).closeCause;
            m996constructorimpl = ValueOrClosed.m996constructorimpl(new ValueOrClosed.Closed(cause$iv$iv));
        }
        Object $this$toResult$iv = ValueOrClosed.m995boximpl(m996constructorimpl);
        return $this$toResult$iv;
    }

    @Override // kotlinx.coroutines.channels.ReceiveChannel
    public final E poll() {
        Object result = pollInternal();
        if (result == AbstractChannelKt.POLL_FAILED) {
            return null;
        }
        return receiveOrNullResult(result);
    }

    @Override // kotlinx.coroutines.channels.ReceiveChannel
    public final void cancel(CancellationException cause) {
        CancellationException cancellationException;
        if (cause != null) {
            cancellationException = cause;
        } else {
            cancellationException = new CancellationException(DebugStringsKt.getClassSimpleName(this) + " was cancelled");
        }
        cancel(cancellationException);
    }

    @Override // kotlinx.coroutines.channels.ReceiveChannel
    /* renamed from: cancelInternal$kotlinx_coroutines_core, reason: merged with bridge method [inline-methods] */
    public boolean cancel(Throwable cause) {
        boolean close = cancel(cause);
        cleanupSendQueueOnCancel();
        return close;
    }

    protected void cleanupSendQueueOnCancel() {
        Closed closed = getClosedForSend();
        if (closed == null) {
            throw new IllegalStateException("Cannot happen".toString());
        }
        while (true) {
            Send send = takeFirstSendOrPeekClosed();
            if (send == null) {
                throw new IllegalStateException("Cannot happen".toString());
            }
            if (send instanceof Closed) {
                if (DebugKt.getASSERTIONS_ENABLED()) {
                    if (!(send == closed)) {
                        throw new AssertionError();
                    }
                    return;
                }
                return;
            }
            send.resumeSendClosed(closed);
        }
    }

    @Override // kotlinx.coroutines.channels.ReceiveChannel
    public final ChannelIterator<E> iterator() {
        return new Itr(this);
    }

    protected final TryPollDesc<E> describeTryPoll() {
        return new TryPollDesc<>(getQueue());
    }

    /* compiled from: AbstractChannel.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0004\u0018\u0000*\u0004\b\u0001\u0010\u00012\u0012\u0012\u0004\u0012\u00020\u00030\u0002j\b\u0012\u0004\u0012\u00020\u0003`\u0004B\r\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0002\u0010\u0007J\u0012\u0010\f\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\r\u001a\u00020\u000eH\u0014J\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0003H\u0014R\u0016\u0010\b\u001a\u0004\u0018\u00018\u00018\u0006@\u0006X\u0087\u000e¢\u0006\u0004\n\u0002\u0010\tR\u0014\u0010\n\u001a\u0004\u0018\u00010\u000b8\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000¨\u0006\u0012"}, d2 = {"Lkotlinx/coroutines/channels/AbstractChannel$TryPollDesc;", "E", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode$RemoveFirstDesc;", "Lkotlinx/coroutines/channels/Send;", "Lkotlinx/coroutines/internal/RemoveFirstDesc;", "queue", "Lkotlinx/coroutines/internal/LockFreeLinkedListHead;", "(Lkotlinx/coroutines/internal/LockFreeLinkedListHead;)V", "pollResult", "Ljava/lang/Object;", "resumeToken", "", "failure", "affected", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "validatePrepared", "", "node", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    protected static final class TryPollDesc<E> extends LockFreeLinkedListNode.RemoveFirstDesc<Send> {
        public E pollResult;
        public Object resumeToken;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public TryPollDesc(LockFreeLinkedListHead queue) {
            super(queue);
            Intrinsics.checkParameterIsNotNull(queue, "queue");
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.RemoveFirstDesc, kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected Object failure(LockFreeLinkedListNode affected) {
            Intrinsics.checkParameterIsNotNull(affected, "affected");
            if (affected instanceof Closed) {
                return affected;
            }
            if (affected instanceof Send) {
                return null;
            }
            return AbstractChannelKt.POLL_FAILED;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.RemoveFirstDesc
        public boolean validatePrepared(Send node) {
            Intrinsics.checkParameterIsNotNull(node, "node");
            Object tryResumeSend = node.tryResumeSend(this);
            if (tryResumeSend == null) {
                return false;
            }
            this.resumeToken = tryResumeSend;
            this.pollResult = (E) node.getPollResult();
            return true;
        }
    }

    @Override // kotlinx.coroutines.channels.ReceiveChannel
    public final SelectClause1<E> getOnReceive() {
        return new SelectClause1<E>() { // from class: kotlinx.coroutines.channels.AbstractChannel$onReceive$1
            @Override // kotlinx.coroutines.selects.SelectClause1
            public <R> void registerSelectClause1(SelectInstance<? super R> select, Function2<? super E, ? super Continuation<? super R>, ? extends Object> block) {
                Intrinsics.checkParameterIsNotNull(select, "select");
                Intrinsics.checkParameterIsNotNull(block, "block");
                AbstractChannel.this.registerSelectReceive(select, block);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final <R> void registerSelectReceive(SelectInstance<? super R> select, Function2<? super E, ? super Continuation<? super R>, ? extends Object> block) {
        while (!select.isSelected()) {
            if (isEmpty()) {
                if (block == null) {
                    throw new TypeCastException("null cannot be cast to non-null type suspend (kotlin.Any?) -> R");
                }
                if (enqueueReceiveSelect(select, block, 0)) {
                    return;
                }
            } else {
                Object pollResult = pollSelectInternal(select);
                if (pollResult == SelectKt.getALREADY_SELECTED()) {
                    return;
                }
                if (pollResult != AbstractChannelKt.POLL_FAILED) {
                    if (pollResult instanceof Closed) {
                        throw StackTraceRecoveryKt.recoverStackTrace(((Closed) pollResult).getReceiveException());
                    }
                    UndispatchedKt.startCoroutineUnintercepted(block, pollResult, select.getCompletion());
                    return;
                }
            }
        }
    }

    @Override // kotlinx.coroutines.channels.ReceiveChannel
    public final SelectClause1<E> getOnReceiveOrNull() {
        return new SelectClause1<E>() { // from class: kotlinx.coroutines.channels.AbstractChannel$onReceiveOrNull$1
            @Override // kotlinx.coroutines.selects.SelectClause1
            public <R> void registerSelectClause1(SelectInstance<? super R> select, Function2<? super E, ? super Continuation<? super R>, ? extends Object> block) {
                Intrinsics.checkParameterIsNotNull(select, "select");
                Intrinsics.checkParameterIsNotNull(block, "block");
                AbstractChannel.this.registerSelectReceiveOrNull(select, block);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final <R> void registerSelectReceiveOrNull(SelectInstance<? super R> select, Function2<? super E, ? super Continuation<? super R>, ? extends Object> block) {
        while (!select.isSelected()) {
            if (isEmpty()) {
                if (block == null) {
                    throw new TypeCastException("null cannot be cast to non-null type suspend (kotlin.Any?) -> R");
                }
                if (enqueueReceiveSelect(select, block, 1)) {
                    return;
                }
            } else {
                Object pollResult = pollSelectInternal(select);
                if (pollResult == SelectKt.getALREADY_SELECTED()) {
                    return;
                }
                if (pollResult != AbstractChannelKt.POLL_FAILED) {
                    if (pollResult instanceof Closed) {
                        if (((Closed) pollResult).closeCause == null) {
                            if (select.trySelect(null)) {
                                UndispatchedKt.startCoroutineUnintercepted(block, null, select.getCompletion());
                                return;
                            }
                            return;
                        }
                        throw StackTraceRecoveryKt.recoverStackTrace(((Closed) pollResult).closeCause);
                    }
                    UndispatchedKt.startCoroutineUnintercepted(block, pollResult, select.getCompletion());
                    return;
                }
            }
        }
    }

    @Override // kotlinx.coroutines.channels.ReceiveChannel
    public SelectClause1<ValueOrClosed<E>> getOnReceiveOrClosed() {
        return new SelectClause1<ValueOrClosed<? extends E>>() { // from class: kotlinx.coroutines.channels.AbstractChannel$onReceiveOrClosed$1
            @Override // kotlinx.coroutines.selects.SelectClause1
            public <R> void registerSelectClause1(SelectInstance<? super R> select, Function2<? super ValueOrClosed<? extends E>, ? super Continuation<? super R>, ? extends Object> block) {
                Intrinsics.checkParameterIsNotNull(select, "select");
                Intrinsics.checkParameterIsNotNull(block, "block");
                AbstractChannel.this.registerSelectReceiveOrClosed(select, block);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final <R> void registerSelectReceiveOrClosed(SelectInstance<? super R> select, Function2<? super ValueOrClosed<? extends E>, ? super Continuation<? super R>, ? extends Object> block) {
        while (!select.isSelected()) {
            if (isEmpty()) {
                if (block == null) {
                    throw new TypeCastException("null cannot be cast to non-null type suspend (kotlin.Any?) -> R");
                }
                if (enqueueReceiveSelect(select, block, 2)) {
                    return;
                }
            } else {
                Object pollResult = pollSelectInternal(select);
                if (pollResult == SelectKt.getALREADY_SELECTED()) {
                    return;
                }
                if (pollResult == AbstractChannelKt.POLL_FAILED) {
                    continue;
                } else if (pollResult instanceof Closed) {
                    ValueOrClosed.Companion companion = ValueOrClosed.INSTANCE;
                    Throwable cause$iv = ((Closed) pollResult).closeCause;
                    UndispatchedKt.startCoroutineUnintercepted(block, ValueOrClosed.m995boximpl(ValueOrClosed.m996constructorimpl(new ValueOrClosed.Closed(cause$iv))), select.getCompletion());
                } else {
                    ValueOrClosed.Companion companion2 = ValueOrClosed.INSTANCE;
                    UndispatchedKt.startCoroutineUnintercepted(block, ValueOrClosed.m995boximpl(ValueOrClosed.m996constructorimpl(pollResult)), select.getCompletion());
                    return;
                }
            }
        }
    }

    private final <R> boolean enqueueReceiveSelect(SelectInstance<? super R> select, Function2<Object, ? super Continuation<? super R>, ? extends Object> block, int receiveMode) {
        ReceiveSelect node = new ReceiveSelect(this, select, block, receiveMode);
        boolean result = enqueueReceive(node);
        if (result) {
            select.disposeOnSelect(node);
        }
        return result;
    }

    @Override // kotlinx.coroutines.channels.AbstractSendChannel
    protected ReceiveOrClosed<E> takeFirstReceiveOrPeekClosed() {
        ReceiveOrClosed it = super.takeFirstReceiveOrPeekClosed();
        if (it != null && !(it instanceof Closed)) {
            onReceiveDequeued();
        }
        return it;
    }

    protected void onReceiveEnqueued() {
    }

    protected void onReceiveDequeued() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void removeReceiveOnCancel(CancellableContinuation<?> cont, Receive<?> receive) {
        CancelHandlerBase $this$asHandler$iv = new RemoveReceiveOnCancel(this, receive);
        cont.invokeOnCancellation($this$asHandler$iv);
    }

    /* compiled from: AbstractChannel.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0003\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0082\u0004\u0018\u00002\u00020\u0001B\u0011\u0012\n\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u0003¢\u0006\u0002\u0010\u0004J\u0013\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\bH\u0096\u0002J\b\u0010\t\u001a\u00020\nH\u0016R\u0012\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"}, d2 = {"Lkotlinx/coroutines/channels/AbstractChannel$RemoveReceiveOnCancel;", "Lkotlinx/coroutines/CancelHandler;", "receive", "Lkotlinx/coroutines/channels/Receive;", "(Lkotlinx/coroutines/channels/AbstractChannel;Lkotlinx/coroutines/channels/Receive;)V", "invoke", "", "cause", "", "toString", "", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    private final class RemoveReceiveOnCancel extends CancelHandler {
        private final Receive<?> receive;
        final /* synthetic */ AbstractChannel this$0;

        public RemoveReceiveOnCancel(AbstractChannel $outer, Receive<?> receive) {
            Intrinsics.checkParameterIsNotNull(receive, "receive");
            this.this$0 = $outer;
            this.receive = receive;
        }

        @Override // kotlin.jvm.functions.Function1
        public /* bridge */ /* synthetic */ Unit invoke(Throwable th) {
            invoke2(th);
            return Unit.INSTANCE;
        }

        @Override // kotlinx.coroutines.CancelHandlerBase
        /* renamed from: invoke, reason: avoid collision after fix types in other method */
        public void invoke2(Throwable cause) {
            if (this.receive.remove()) {
                this.this$0.onReceiveDequeued();
            }
        }

        public String toString() {
            return "RemoveReceiveOnCancel[" + this.receive + ']';
        }
    }

    /* compiled from: AbstractChannel.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0002\u0018\u0000*\u0004\b\u0001\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\u0013\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00010\u0004¢\u0006\u0002\u0010\u0005J\u0011\u0010\u000e\u001a\u00020\u000fH\u0096Bø\u0001\u0000¢\u0006\u0002\u0010\u0010J\u0012\u0010\u0011\u001a\u00020\u000f2\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0002J\u0011\u0010\u0012\u001a\u00020\u000fH\u0082@ø\u0001\u0000¢\u0006\u0002\u0010\u0010J\u000e\u0010\u0013\u001a\u00028\u0001H\u0096\u0002¢\u0006\u0002\u0010\u000bR\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00010\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u001c\u0010\b\u001a\u0004\u0018\u00010\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\r\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u0014"}, d2 = {"Lkotlinx/coroutines/channels/AbstractChannel$Itr;", "E", "Lkotlinx/coroutines/channels/ChannelIterator;", "channel", "Lkotlinx/coroutines/channels/AbstractChannel;", "(Lkotlinx/coroutines/channels/AbstractChannel;)V", "getChannel", "()Lkotlinx/coroutines/channels/AbstractChannel;", "result", "", "getResult", "()Ljava/lang/Object;", "setResult", "(Ljava/lang/Object;)V", "hasNext", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "hasNextResult", "hasNextSuspend", "next", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    private static final class Itr<E> implements ChannelIterator<E> {
        private final AbstractChannel<E> channel;
        private Object result;

        public Itr(AbstractChannel<E> channel) {
            Intrinsics.checkParameterIsNotNull(channel, "channel");
            this.channel = channel;
            this.result = AbstractChannelKt.POLL_FAILED;
        }

        public final AbstractChannel<E> getChannel() {
            return this.channel;
        }

        @Override // kotlinx.coroutines.channels.ChannelIterator
        @Deprecated(level = DeprecationLevel.HIDDEN, message = "Since 1.3.0, binary compatibility with versions <= 1.2.x")
        public /* synthetic */ Object next(Continuation<? super E> continuation) {
            return ChannelIterator.DefaultImpls.next(this, continuation);
        }

        public final Object getResult() {
            return this.result;
        }

        public final void setResult(Object obj) {
            this.result = obj;
        }

        @Override // kotlinx.coroutines.channels.ChannelIterator
        public Object hasNext(Continuation<? super Boolean> continuation) {
            if (this.result != AbstractChannelKt.POLL_FAILED) {
                return Boxing.boxBoolean(hasNextResult(this.result));
            }
            Object pollInternal = this.channel.pollInternal();
            this.result = pollInternal;
            return pollInternal != AbstractChannelKt.POLL_FAILED ? Boxing.boxBoolean(hasNextResult(this.result)) : hasNextSuspend(continuation);
        }

        private final boolean hasNextResult(Object result) {
            if (result instanceof Closed) {
                if (((Closed) result).closeCause != null) {
                    throw StackTraceRecoveryKt.recoverStackTrace(((Closed) result).getReceiveException());
                }
                return false;
            }
            return true;
        }

        final /* synthetic */ Object hasNextSuspend(Continuation<? super Boolean> continuation) {
            CancellableContinuationImpl cancellable$iv = new CancellableContinuationImpl(IntrinsicsKt.intercepted(continuation), 0);
            CancellableContinuationImpl cont = cancellable$iv;
            ReceiveHasNext receive = new ReceiveHasNext(this, cont);
            while (true) {
                if (getChannel().enqueueReceive(receive)) {
                    getChannel().removeReceiveOnCancel(cont, receive);
                    break;
                }
                Object result = getChannel().pollInternal();
                setResult(result);
                if (result instanceof Closed) {
                    if (((Closed) result).closeCause == null) {
                        Boolean boxBoolean = Boxing.boxBoolean(false);
                        Result.Companion companion = Result.INSTANCE;
                        cont.resumeWith(Result.m16constructorimpl(boxBoolean));
                    } else {
                        Throwable receiveException = ((Closed) result).getReceiveException();
                        Result.Companion companion2 = Result.INSTANCE;
                        cont.resumeWith(Result.m16constructorimpl(ResultKt.createFailure(receiveException)));
                    }
                } else if (result != AbstractChannelKt.POLL_FAILED) {
                    Boolean boxBoolean2 = Boxing.boxBoolean(true);
                    Result.Companion companion3 = Result.INSTANCE;
                    cont.resumeWith(Result.m16constructorimpl(boxBoolean2));
                    break;
                }
            }
            Object result2 = cancellable$iv.getResult();
            if (result2 == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                DebugProbesKt.probeCoroutineSuspended(continuation);
            }
            return result2;
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // kotlinx.coroutines.channels.ChannelIterator
        public E next() {
            E e = (E) this.result;
            if (e instanceof Closed) {
                throw StackTraceRecoveryKt.recoverStackTrace(((Closed) e).getReceiveException());
            }
            if (e != AbstractChannelKt.POLL_FAILED) {
                this.result = AbstractChannelKt.POLL_FAILED;
                return e;
            }
            throw new IllegalStateException("'hasNext' should be called prior to 'next' invocation");
        }
    }

    /* compiled from: AbstractChannel.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0004\b\u0002\u0018\u0000*\u0006\b\u0001\u0010\u0001 \u00002\b\u0012\u0004\u0012\u0002H\u00010\u0002B\u001d\u0012\u000e\u0010\u0003\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0002\u0010\bJ\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u0005H\u0016J\u0014\u0010\f\u001a\u00020\n2\n\u0010\r\u001a\u0006\u0012\u0002\b\u00030\u000eH\u0016J\u0015\u0010\u000f\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0010\u001a\u00028\u0001¢\u0006\u0002\u0010\u0011J\b\u0010\u0012\u001a\u00020\u0013H\u0016J!\u0010\u0014\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0010\u001a\u00028\u00012\b\u0010\u0015\u001a\u0004\u0018\u00010\u0005H\u0016¢\u0006\u0002\u0010\u0016R\u0018\u0010\u0003\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u00048\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u0017"}, d2 = {"Lkotlinx/coroutines/channels/AbstractChannel$ReceiveElement;", "E", "Lkotlinx/coroutines/channels/Receive;", "cont", "Lkotlinx/coroutines/CancellableContinuation;", "", "receiveMode", "", "(Lkotlinx/coroutines/CancellableContinuation;I)V", "completeResumeReceive", "", "token", "resumeReceiveClosed", "closed", "Lkotlinx/coroutines/channels/Closed;", "resumeValue", "value", "(Ljava/lang/Object;)Ljava/lang/Object;", "toString", "", "tryResumeReceive", "idempotent", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    private static final class ReceiveElement<E> extends Receive<E> {
        public final CancellableContinuation<Object> cont;
        public final int receiveMode;

        public ReceiveElement(CancellableContinuation<Object> cont, int receiveMode) {
            Intrinsics.checkParameterIsNotNull(cont, "cont");
            this.cont = cont;
            this.receiveMode = receiveMode;
        }

        public final Object resumeValue(E value) {
            if (this.receiveMode == 2) {
                ValueOrClosed.Companion companion = ValueOrClosed.INSTANCE;
                return ValueOrClosed.m995boximpl(ValueOrClosed.m996constructorimpl(value));
            }
            return value;
        }

        @Override // kotlinx.coroutines.channels.ReceiveOrClosed
        public Object tryResumeReceive(E value, Object idempotent) {
            return this.cont.tryResume(resumeValue(value), idempotent);
        }

        @Override // kotlinx.coroutines.channels.ReceiveOrClosed
        public void completeResumeReceive(Object token) {
            Intrinsics.checkParameterIsNotNull(token, "token");
            this.cont.completeResume(token);
        }

        @Override // kotlinx.coroutines.channels.Receive
        public void resumeReceiveClosed(Closed<?> closed) {
            Intrinsics.checkParameterIsNotNull(closed, "closed");
            if (this.receiveMode != 1 || closed.closeCause != null) {
                if (this.receiveMode != 2) {
                    CancellableContinuation<Object> cancellableContinuation = this.cont;
                    Throwable receiveException = closed.getReceiveException();
                    Result.Companion companion = Result.INSTANCE;
                    cancellableContinuation.resumeWith(Result.m16constructorimpl(ResultKt.createFailure(receiveException)));
                    return;
                }
                CancellableContinuation<Object> cancellableContinuation2 = this.cont;
                ValueOrClosed.Companion companion2 = ValueOrClosed.INSTANCE;
                Throwable cause$iv$iv = closed.closeCause;
                ValueOrClosed m995boximpl = ValueOrClosed.m995boximpl(ValueOrClosed.m996constructorimpl(new ValueOrClosed.Closed(cause$iv$iv)));
                Result.Companion companion3 = Result.INSTANCE;
                cancellableContinuation2.resumeWith(Result.m16constructorimpl(m995boximpl));
                return;
            }
            CancellableContinuation<Object> cancellableContinuation3 = this.cont;
            Result.Companion companion4 = Result.INSTANCE;
            cancellableContinuation3.resumeWith(Result.m16constructorimpl(null));
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode
        public String toString() {
            return "ReceiveElement[receiveMode=" + this.receiveMode + ']';
        }
    }

    /* compiled from: AbstractChannel.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0002\u0018\u0000*\u0004\b\u0001\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B!\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00010\u0004\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006¢\u0006\u0002\u0010\bJ\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0016J\u0014\u0010\r\u001a\u00020\n2\n\u0010\u000e\u001a\u0006\u0012\u0002\b\u00030\u000fH\u0016J\b\u0010\u0010\u001a\u00020\u0011H\u0016J!\u0010\u0012\u001a\u0004\u0018\u00010\f2\u0006\u0010\u0013\u001a\u00028\u00012\b\u0010\u0014\u001a\u0004\u0018\u00010\fH\u0016¢\u0006\u0002\u0010\u0015R\u0016\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00068\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00010\u00048\u0006X\u0087\u0004¢\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u0016"}, d2 = {"Lkotlinx/coroutines/channels/AbstractChannel$ReceiveHasNext;", "E", "Lkotlinx/coroutines/channels/Receive;", "iterator", "Lkotlinx/coroutines/channels/AbstractChannel$Itr;", "cont", "Lkotlinx/coroutines/CancellableContinuation;", "", "(Lkotlinx/coroutines/channels/AbstractChannel$Itr;Lkotlinx/coroutines/CancellableContinuation;)V", "completeResumeReceive", "", "token", "", "resumeReceiveClosed", "closed", "Lkotlinx/coroutines/channels/Closed;", "toString", "", "tryResumeReceive", "value", "idempotent", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    private static final class ReceiveHasNext<E> extends Receive<E> {
        public final CancellableContinuation<Boolean> cont;
        public final Itr<E> iterator;

        /* JADX WARN: Multi-variable type inference failed */
        public ReceiveHasNext(Itr<E> iterator, CancellableContinuation<? super Boolean> cont) {
            Intrinsics.checkParameterIsNotNull(iterator, "iterator");
            Intrinsics.checkParameterIsNotNull(cont, "cont");
            this.iterator = iterator;
            this.cont = cont;
        }

        @Override // kotlinx.coroutines.channels.ReceiveOrClosed
        public Object tryResumeReceive(E value, Object idempotent) {
            Object token = this.cont.tryResume(true, idempotent);
            if (token != null) {
                if (idempotent != null) {
                    return new IdempotentTokenValue(token, value);
                }
                this.iterator.setResult(value);
            }
            return token;
        }

        @Override // kotlinx.coroutines.channels.ReceiveOrClosed
        public void completeResumeReceive(Object token) {
            Intrinsics.checkParameterIsNotNull(token, "token");
            if (token instanceof IdempotentTokenValue) {
                this.iterator.setResult(((IdempotentTokenValue) token).value);
                this.cont.completeResume(((IdempotentTokenValue) token).token);
            } else {
                this.cont.completeResume(token);
            }
        }

        @Override // kotlinx.coroutines.channels.Receive
        public void resumeReceiveClosed(Closed<?> closed) {
            Object token;
            Intrinsics.checkParameterIsNotNull(closed, "closed");
            if (closed.closeCause == null) {
                token = CancellableContinuation.DefaultImpls.tryResume$default(this.cont, false, null, 2, null);
            } else {
                token = this.cont.tryResumeWithException(StackTraceRecoveryKt.recoverStackTrace(closed.getReceiveException(), this.cont));
            }
            if (token != null) {
                this.iterator.setResult(closed);
                this.cont.completeResume(token);
            }
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode
        public String toString() {
            return "ReceiveHasNext";
        }
    }

    /* compiled from: AbstractChannel.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0002\u0018\u0000*\u0004\b\u0001\u0010\u0001*\u0004\b\u0002\u0010\u00022\b\u0012\u0004\u0012\u0002H\u00020\u00032\u00020\u0004BR\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00028\u00020\u0006\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00010\b\u0012$\u0010\t\u001a \b\u0001\u0012\u0006\u0012\u0004\u0018\u00010\u000b\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00010\f\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\n\u0012\u0006\u0010\r\u001a\u00020\u000eø\u0001\u0000¢\u0006\u0002\u0010\u000fJ\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u000bH\u0016J\b\u0010\u0014\u001a\u00020\u0012H\u0016J\u0014\u0010\u0015\u001a\u00020\u00122\n\u0010\u0016\u001a\u0006\u0012\u0002\b\u00030\u0017H\u0016J\b\u0010\u0018\u001a\u00020\u0019H\u0016J!\u0010\u001a\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u001b\u001a\u00028\u00022\b\u0010\u001c\u001a\u0004\u0018\u00010\u000bH\u0016¢\u0006\u0002\u0010\u001dR3\u0010\t\u001a \b\u0001\u0012\u0006\u0012\u0004\u0018\u00010\u000b\u0012\n\u0012\b\u0012\u0004\u0012\u00028\u00010\f\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\n8\u0006X\u0087\u0004ø\u0001\u0000¢\u0006\u0004\n\u0002\u0010\u0010R\u0016\u0010\u0005\u001a\b\u0012\u0004\u0012\u00028\u00020\u00068\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u00020\u000e8\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00010\b8\u0006X\u0087\u0004¢\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u001e"}, d2 = {"Lkotlinx/coroutines/channels/AbstractChannel$ReceiveSelect;", "R", "E", "Lkotlinx/coroutines/channels/Receive;", "Lkotlinx/coroutines/DisposableHandle;", "channel", "Lkotlinx/coroutines/channels/AbstractChannel;", "select", "Lkotlinx/coroutines/selects/SelectInstance;", "block", "Lkotlin/Function2;", "", "Lkotlin/coroutines/Continuation;", "receiveMode", "", "(Lkotlinx/coroutines/channels/AbstractChannel;Lkotlinx/coroutines/selects/SelectInstance;Lkotlin/jvm/functions/Function2;I)V", "Lkotlin/jvm/functions/Function2;", "completeResumeReceive", "", "token", "dispose", "resumeReceiveClosed", "closed", "Lkotlinx/coroutines/channels/Closed;", "toString", "", "tryResumeReceive", "value", "idempotent", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    private static final class ReceiveSelect<R, E> extends Receive<E> implements DisposableHandle {
        public final Function2<Object, Continuation<? super R>, Object> block;
        public final AbstractChannel<E> channel;
        public final int receiveMode;
        public final SelectInstance<R> select;

        /* JADX WARN: Multi-variable type inference failed */
        public ReceiveSelect(AbstractChannel<E> channel, SelectInstance<? super R> select, Function2<Object, ? super Continuation<? super R>, ? extends Object> block, int receiveMode) {
            Intrinsics.checkParameterIsNotNull(channel, "channel");
            Intrinsics.checkParameterIsNotNull(select, "select");
            Intrinsics.checkParameterIsNotNull(block, "block");
            this.channel = channel;
            this.select = select;
            this.block = block;
            this.receiveMode = receiveMode;
        }

        @Override // kotlinx.coroutines.channels.ReceiveOrClosed
        public Object tryResumeReceive(E value, Object idempotent) {
            if (this.select.trySelect(idempotent)) {
                return value != null ? value : AbstractChannelKt.NULL_VALUE;
            }
            return null;
        }

        @Override // kotlinx.coroutines.channels.ReceiveOrClosed
        public void completeResumeReceive(Object token) {
            Object obj;
            Intrinsics.checkParameterIsNotNull(token, "token");
            Symbol this_$iv = AbstractChannelKt.NULL_VALUE;
            Object value = token == this_$iv ? null : token;
            Function2<Object, Continuation<? super R>, Object> function2 = this.block;
            if (this.receiveMode != 2) {
                obj = value;
            } else {
                ValueOrClosed.Companion companion = ValueOrClosed.INSTANCE;
                obj = ValueOrClosed.m995boximpl(ValueOrClosed.m996constructorimpl(value));
            }
            ContinuationKt.startCoroutine(function2, obj, this.select.getCompletion());
        }

        @Override // kotlinx.coroutines.channels.Receive
        public void resumeReceiveClosed(Closed<?> closed) {
            Intrinsics.checkParameterIsNotNull(closed, "closed");
            if (this.select.trySelect(null)) {
                int i = this.receiveMode;
                if (i == 0) {
                    this.select.resumeSelectCancellableWithException(closed.getReceiveException());
                    return;
                }
                if (i == 1) {
                    if (closed.closeCause == null) {
                        ContinuationKt.startCoroutine(this.block, null, this.select.getCompletion());
                        return;
                    } else {
                        this.select.resumeSelectCancellableWithException(closed.getReceiveException());
                        return;
                    }
                }
                if (i == 2) {
                    Function2<Object, Continuation<? super R>, Object> function2 = this.block;
                    ValueOrClosed.Companion companion = ValueOrClosed.INSTANCE;
                    Throwable cause$iv = closed.closeCause;
                    ContinuationKt.startCoroutine(function2, ValueOrClosed.m995boximpl(ValueOrClosed.m996constructorimpl(new ValueOrClosed.Closed(cause$iv))), this.select.getCompletion());
                }
            }
        }

        @Override // kotlinx.coroutines.DisposableHandle
        public void dispose() {
            if (remove()) {
                this.channel.onReceiveDequeued();
            }
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode
        public String toString() {
            return "ReceiveSelect[" + this.select + ",receiveMode=" + this.receiveMode + ']';
        }
    }

    /* compiled from: AbstractChannel.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0005\b\u0002\u0018\u0000*\u0006\b\u0001\u0010\u0001 \u00012\u00020\u0002B\u0015\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00028\u0001¢\u0006\u0002\u0010\u0005R\u0010\u0010\u0003\u001a\u00020\u00028\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0012\u0010\u0004\u001a\u00028\u00018\u0006X\u0087\u0004¢\u0006\u0004\n\u0002\u0010\u0006\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u0007"}, d2 = {"Lkotlinx/coroutines/channels/AbstractChannel$IdempotentTokenValue;", "E", "", "token", "value", "(Ljava/lang/Object;Ljava/lang/Object;)V", "Ljava/lang/Object;", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    private static final class IdempotentTokenValue<E> {
        public final Object token;
        public final E value;

        public IdempotentTokenValue(Object token, E e) {
            Intrinsics.checkParameterIsNotNull(token, "token");
            this.token = token;
            this.value = e;
        }
    }
}
