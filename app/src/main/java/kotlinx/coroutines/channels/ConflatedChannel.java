package kotlinx.coroutines.channels;

import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.channels.AbstractSendChannel;
import kotlinx.coroutines.internal.LockFreeLinkedListNode;
import kotlinx.coroutines.selects.SelectInstance;
import kotlinx.coroutines.selects.SelectKt;

/* compiled from: ConflatedChannel.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0010\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\u0005¢\u0006\u0002\u0010\u0003J\u0016\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00028\u00000\rH\u0002J\u0015\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00028\u0000H\u0014¢\u0006\u0002\u0010\u0011J!\u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00028\u00002\n\u0010\u0013\u001a\u0006\u0012\u0002\b\u00030\u0014H\u0014¢\u0006\u0002\u0010\u0015J\u0010\u0010\u0016\u001a\u00020\u000b2\u0006\u0010\u0017\u001a\u00020\u0018H\u0014J\u001b\u0010\u0019\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u001a2\u0006\u0010\u0010\u001a\u00028\u0000H\u0002¢\u0006\u0002\u0010\u001bR\u0014\u0010\u0004\u001a\u00020\u00058DX\u0084\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0006R\u0014\u0010\u0007\u001a\u00020\u00058DX\u0084\u0004¢\u0006\u0006\u001a\u0004\b\u0007\u0010\u0006R\u0014\u0010\b\u001a\u00020\u00058DX\u0084\u0004¢\u0006\u0006\u001a\u0004\b\b\u0010\u0006R\u0014\u0010\t\u001a\u00020\u00058DX\u0084\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\u0006¨\u0006\u001c"}, d2 = {"Lkotlinx/coroutines/channels/ConflatedChannel;", "E", "Lkotlinx/coroutines/channels/AbstractChannel;", "()V", "isBufferAlwaysEmpty", "", "()Z", "isBufferAlwaysFull", "isBufferEmpty", "isBufferFull", "conflatePreviousSendBuffered", "", "node", "Lkotlinx/coroutines/channels/AbstractSendChannel$SendBuffered;", "offerInternal", "", "element", "(Ljava/lang/Object;)Ljava/lang/Object;", "offerSelectInternal", "select", "Lkotlinx/coroutines/selects/SelectInstance;", "(Ljava/lang/Object;Lkotlinx/coroutines/selects/SelectInstance;)Ljava/lang/Object;", "onClosedIdempotent", "closed", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "sendConflated", "Lkotlinx/coroutines/channels/ReceiveOrClosed;", "(Ljava/lang/Object;)Lkotlinx/coroutines/channels/ReceiveOrClosed;", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public class ConflatedChannel<E> extends AbstractChannel<E> {
    @Override // kotlinx.coroutines.channels.AbstractChannel
    protected final boolean isBufferAlwaysEmpty() {
        return true;
    }

    @Override // kotlinx.coroutines.channels.AbstractChannel
    protected final boolean isBufferEmpty() {
        return true;
    }

    @Override // kotlinx.coroutines.channels.AbstractSendChannel
    protected final boolean isBufferAlwaysFull() {
        return false;
    }

    @Override // kotlinx.coroutines.channels.AbstractSendChannel
    protected final boolean isBufferFull() {
        return false;
    }

    @Override // kotlinx.coroutines.channels.AbstractSendChannel
    protected void onClosedIdempotent(LockFreeLinkedListNode closed) {
        Intrinsics.checkParameterIsNotNull(closed, "closed");
        LockFreeLinkedListNode prevNode = closed.getPrevNode();
        if (!(prevNode instanceof AbstractSendChannel.SendBuffered)) {
            prevNode = null;
        }
        AbstractSendChannel.SendBuffered lastBuffered = (AbstractSendChannel.SendBuffered) prevNode;
        if (lastBuffered != null) {
            conflatePreviousSendBuffered(lastBuffered);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private final ReceiveOrClosed<?> sendConflated(E element) {
        LockFreeLinkedListNode lockFreeLinkedListNode;
        AbstractSendChannel.SendBuffered node = new AbstractSendChannel.SendBuffered(element);
        LockFreeLinkedListNode this_$iv = getQueue();
        do {
            Object prev = this_$iv.getPrev();
            if (prev != null) {
                lockFreeLinkedListNode = (LockFreeLinkedListNode) prev;
                if (lockFreeLinkedListNode instanceof ReceiveOrClosed) {
                    return (ReceiveOrClosed) lockFreeLinkedListNode;
                }
            } else {
                throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
            }
        } while (!lockFreeLinkedListNode.addNext(node, this_$iv));
        conflatePreviousSendBuffered(node);
        return null;
    }

    private final void conflatePreviousSendBuffered(AbstractSendChannel.SendBuffered<? extends E> node) {
        for (LockFreeLinkedListNode prev = node.getPrevNode(); prev instanceof AbstractSendChannel.SendBuffered; prev = prev.getPrevNode()) {
            if (!prev.remove()) {
                prev.helpRemove();
            }
        }
    }

    @Override // kotlinx.coroutines.channels.AbstractSendChannel
    protected Object offerInternal(E element) {
        ReceiveOrClosed sendResult;
        do {
            Object result = super.offerInternal(element);
            if (result == AbstractChannelKt.OFFER_SUCCESS) {
                return AbstractChannelKt.OFFER_SUCCESS;
            }
            if (result == AbstractChannelKt.OFFER_FAILED) {
                sendResult = sendConflated(element);
                if (sendResult == null) {
                    return AbstractChannelKt.OFFER_SUCCESS;
                }
            } else {
                if (result instanceof Closed) {
                    return result;
                }
                throw new IllegalStateException(("Invalid offerInternal result " + result).toString());
            }
        } while (!(sendResult instanceof Closed));
        return sendResult;
    }

    @Override // kotlinx.coroutines.channels.AbstractSendChannel
    protected Object offerSelectInternal(E element, SelectInstance<?> select) {
        Object result;
        Intrinsics.checkParameterIsNotNull(select, "select");
        do {
            if (getHasReceiveOrClosed()) {
                result = super.offerSelectInternal(element, select);
            } else {
                result = select.performAtomicTrySelect(describeSendConflated(element));
                if (result == null) {
                    result = AbstractChannelKt.OFFER_SUCCESS;
                }
            }
            if (result == SelectKt.getALREADY_SELECTED()) {
                return SelectKt.getALREADY_SELECTED();
            }
            if (result == AbstractChannelKt.OFFER_SUCCESS) {
                return AbstractChannelKt.OFFER_SUCCESS;
            }
        } while (result == AbstractChannelKt.OFFER_FAILED);
        if (result instanceof Closed) {
            return result;
        }
        throw new IllegalStateException(("Invalid result " + result).toString());
    }
}
