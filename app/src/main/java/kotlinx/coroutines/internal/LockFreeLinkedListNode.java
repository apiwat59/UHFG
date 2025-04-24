package kotlinx.coroutines.internal;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.DebugKt;

/* compiled from: LockFreeLinkedList.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0012\b\u0017\u0018\u00002\u00020\u0001:\u0004KLMNB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0019\u0010\u0007\u001a\u00020\u00062\n\u0010\u0005\u001a\u00060\u0000j\u0002`\u0004¢\u0006\u0004\b\u0007\u0010\bJ,\u0010\f\u001a\u00020\n2\n\u0010\u0005\u001a\u00060\u0000j\u0002`\u00042\u000e\b\u0004\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0086\b¢\u0006\u0004\b\f\u0010\rJ4\u0010\u0010\u001a\u00020\n2\n\u0010\u0005\u001a\u00060\u0000j\u0002`\u00042\u0016\u0010\u000f\u001a\u0012\u0012\b\u0012\u00060\u0000j\u0002`\u0004\u0012\u0004\u0012\u00020\n0\u000eH\u0086\b¢\u0006\u0004\b\u0010\u0010\u0011JD\u0010\u0012\u001a\u00020\n2\n\u0010\u0005\u001a\u00060\u0000j\u0002`\u00042\u0016\u0010\u000f\u001a\u0012\u0012\b\u0012\u00060\u0000j\u0002`\u0004\u0012\u0004\u0012\u00020\n0\u000e2\u000e\b\u0004\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0086\b¢\u0006\u0004\b\u0012\u0010\u0013J'\u0010\u0015\u001a\u00020\n2\n\u0010\u0005\u001a\u00060\u0000j\u0002`\u00042\n\u0010\u0014\u001a\u00060\u0000j\u0002`\u0004H\u0001¢\u0006\u0004\b\u0015\u0010\u0016J\u0019\u0010\u0017\u001a\u00020\n2\n\u0010\u0005\u001a\u00060\u0000j\u0002`\u0004¢\u0006\u0004\b\u0017\u0010\u0018J-\u0010\u001c\u001a\n\u0018\u00010\u0000j\u0004\u0018\u0001`\u00042\n\u0010\u0019\u001a\u00060\u0000j\u0002`\u00042\b\u0010\u001b\u001a\u0004\u0018\u00010\u001aH\u0002¢\u0006\u0004\b\u001c\u0010\u001dJ)\u0010 \u001a\b\u0012\u0004\u0012\u00028\u00000\u001f\"\f\b\u0000\u0010\u001e*\u00060\u0000j\u0002`\u00042\u0006\u0010\u0005\u001a\u00028\u0000¢\u0006\u0004\b \u0010!J\u0017\u0010#\u001a\f\u0012\b\u0012\u00060\u0000j\u0002`\u00040\"¢\u0006\u0004\b#\u0010$J\u0013\u0010%\u001a\u00060\u0000j\u0002`\u0004H\u0002¢\u0006\u0004\b%\u0010&J\u001b\u0010'\u001a\u00020\u00062\n\u0010\u0014\u001a\u00060\u0000j\u0002`\u0004H\u0002¢\u0006\u0004\b'\u0010\bJ\u001b\u0010(\u001a\u00020\u00062\n\u0010\u0014\u001a\u00060\u0000j\u0002`\u0004H\u0002¢\u0006\u0004\b(\u0010\bJ\u000f\u0010)\u001a\u00020\u0006H\u0001¢\u0006\u0004\b)\u0010\u0003J\r\u0010*\u001a\u00020\u0006¢\u0006\u0004\b*\u0010\u0003J,\u0010,\u001a\u00020+2\n\u0010\u0005\u001a\u00060\u0000j\u0002`\u00042\u000e\b\u0004\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0081\b¢\u0006\u0004\b,\u0010-J\u0013\u0010.\u001a\u00060\u0000j\u0002`\u0004H\u0002¢\u0006\u0004\b.\u0010&J\u000f\u0010/\u001a\u00020\nH\u0016¢\u0006\u0004\b/\u00100J\u001a\u00101\u001a\u0004\u0018\u00018\u0000\"\u0006\b\u0000\u0010\u001e\u0018\u0001H\u0086\b¢\u0006\u0004\b1\u00102J.\u00103\u001a\u0004\u0018\u00018\u0000\"\u0006\b\u0000\u0010\u001e\u0018\u00012\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00020\n0\u000eH\u0086\b¢\u0006\u0004\b3\u00104J\u0015\u00105\u001a\n\u0018\u00010\u0000j\u0004\u0018\u0001`\u0004¢\u0006\u0004\b5\u0010&J\u000f\u00107\u001a\u000206H\u0002¢\u0006\u0004\b7\u00108J\u000f\u0010:\u001a\u000209H\u0016¢\u0006\u0004\b:\u0010;J/\u0010>\u001a\u00020=2\n\u0010\u0005\u001a\u00060\u0000j\u0002`\u00042\n\u0010\u0014\u001a\u00060\u0000j\u0002`\u00042\u0006\u0010<\u001a\u00020+H\u0001¢\u0006\u0004\b>\u0010?J'\u0010C\u001a\u00020\u00062\n\u0010@\u001a\u00060\u0000j\u0002`\u00042\n\u0010\u0014\u001a\u00060\u0000j\u0002`\u0004H\u0000¢\u0006\u0004\bA\u0010BR\u0013\u0010D\u001a\u00020\n8F@\u0006¢\u0006\u0006\u001a\u0004\bD\u00100R\u0013\u0010\u0014\u001a\u00020\u00018F@\u0006¢\u0006\u0006\u001a\u0004\bE\u00102R\u0017\u0010G\u001a\u00060\u0000j\u0002`\u00048F@\u0006¢\u0006\u0006\u001a\u0004\bF\u0010&R\u0013\u0010@\u001a\u00020\u00018F@\u0006¢\u0006\u0006\u001a\u0004\bH\u00102R\u0017\u0010J\u001a\u00060\u0000j\u0002`\u00048F@\u0006¢\u0006\u0006\u001a\u0004\bI\u0010&¨\u0006O"}, d2 = {"Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "", "<init>", "()V", "Lkotlinx/coroutines/internal/Node;", "node", "", "addLast", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)V", "Lkotlin/Function0;", "", "condition", "addLastIf", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlin/jvm/functions/Function0;)Z", "Lkotlin/Function1;", "predicate", "addLastIfPrev", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlin/jvm/functions/Function1;)Z", "addLastIfPrevAndIf", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;)Z", "next", "addNext", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)Z", "addOneIfEmpty", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)Z", "_prev", "Lkotlinx/coroutines/internal/OpDescriptor;", "op", "correctPrev", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/OpDescriptor;)Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "T", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AddLastDesc;", "describeAddLast", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AddLastDesc;", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode$RemoveFirstDesc;", "describeRemoveFirst", "()Lkotlinx/coroutines/internal/LockFreeLinkedListNode$RemoveFirstDesc;", "findHead", "()Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "finishAdd", "finishRemove", "helpDelete", "helpRemove", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode$CondAddOp;", "makeCondAddOp", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlin/jvm/functions/Function0;)Lkotlinx/coroutines/internal/LockFreeLinkedListNode$CondAddOp;", "markPrev", "remove", "()Z", "removeFirstIfIsInstanceOf", "()Ljava/lang/Object;", "removeFirstIfIsInstanceOfOrPeekIf", "(Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "removeFirstOrNull", "Lkotlinx/coroutines/internal/Removed;", "removed", "()Lkotlinx/coroutines/internal/Removed;", "", "toString", "()Ljava/lang/String;", "condAdd", "", "tryCondAddNext", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode$CondAddOp;)I", "prev", "validateNode$kotlinx_coroutines_core", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)V", "validateNode", "isRemoved", "getNext", "getNextNode", "nextNode", "getPrev", "getPrevNode", "prevNode", "AbstractAtomicDesc", "AddLastDesc", "CondAddOp", "RemoveFirstDesc", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public class LockFreeLinkedListNode {
    static final AtomicReferenceFieldUpdater _next$FU = AtomicReferenceFieldUpdater.newUpdater(LockFreeLinkedListNode.class, Object.class, "_next");
    static final AtomicReferenceFieldUpdater _prev$FU = AtomicReferenceFieldUpdater.newUpdater(LockFreeLinkedListNode.class, Object.class, "_prev");
    private static final AtomicReferenceFieldUpdater _removedRef$FU = AtomicReferenceFieldUpdater.newUpdater(LockFreeLinkedListNode.class, Object.class, "_removedRef");
    volatile Object _next = this;
    volatile Object _prev = this;
    private volatile Object _removedRef = null;

    /* JADX INFO: Access modifiers changed from: private */
    public final Removed removed() {
        Removed removed = (Removed) this._removedRef;
        if (removed != null) {
            return removed;
        }
        Removed it = new Removed(this);
        _removedRef$FU.lazySet(this, it);
        return it;
    }

    /* compiled from: LockFreeLinkedList.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\b!\u0018\u00002\f\u0012\b\u0012\u00060\u0002j\u0002`\u00030\u0001B\u0011\u0012\n\u0010\u0004\u001a\u00060\u0002j\u0002`\u0003¢\u0006\u0002\u0010\u0005J\u001e\u0010\u0007\u001a\u00020\b2\n\u0010\t\u001a\u00060\u0002j\u0002`\u00032\b\u0010\n\u001a\u0004\u0018\u00010\u000bH\u0016R\u0014\u0010\u0004\u001a\u00060\u0002j\u0002`\u00038\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\n\u0018\u00010\u0002j\u0004\u0018\u0001`\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000¨\u0006\f"}, d2 = {"Lkotlinx/coroutines/internal/LockFreeLinkedListNode$CondAddOp;", "Lkotlinx/coroutines/internal/AtomicOp;", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "Lkotlinx/coroutines/internal/Node;", "newNode", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)V", "oldNext", "complete", "", "affected", "failure", "", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    public static abstract class CondAddOp extends AtomicOp<LockFreeLinkedListNode> {
        public final LockFreeLinkedListNode newNode;
        public LockFreeLinkedListNode oldNext;

        public CondAddOp(LockFreeLinkedListNode newNode) {
            Intrinsics.checkParameterIsNotNull(newNode, "newNode");
            this.newNode = newNode;
        }

        @Override // kotlinx.coroutines.internal.AtomicOp
        public void complete(LockFreeLinkedListNode affected, Object failure) {
            Intrinsics.checkParameterIsNotNull(affected, "affected");
            boolean success = failure == null;
            LockFreeLinkedListNode update = success ? this.newNode : this.oldNext;
            if (update != null && LockFreeLinkedListNode._next$FU.compareAndSet(affected, this, update) && success) {
                LockFreeLinkedListNode lockFreeLinkedListNode = this.newNode;
                LockFreeLinkedListNode lockFreeLinkedListNode2 = this.oldNext;
                if (lockFreeLinkedListNode2 == null) {
                    Intrinsics.throwNpe();
                }
                lockFreeLinkedListNode.finishAdd(lockFreeLinkedListNode2);
            }
        }
    }

    public final CondAddOp makeCondAddOp(LockFreeLinkedListNode node, Function0<Boolean> condition) {
        Intrinsics.checkParameterIsNotNull(node, "node");
        Intrinsics.checkParameterIsNotNull(condition, "condition");
        return new LockFreeLinkedListNode$makeCondAddOp$1(condition, node, node);
    }

    public final boolean isRemoved() {
        return getNext() instanceof Removed;
    }

    public final Object getNext() {
        while (true) {
            Object next = this._next;
            if (!(next instanceof OpDescriptor)) {
                return next;
            }
            ((OpDescriptor) next).perform(this);
        }
    }

    public final LockFreeLinkedListNode getNextNode() {
        return LockFreeLinkedListKt.unwrap(getNext());
    }

    public final Object getPrev() {
        while (true) {
            Object prev = this._prev;
            if (prev instanceof Removed) {
                return prev;
            }
            if (prev == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
            }
            if (((LockFreeLinkedListNode) prev).getNext() == this) {
                return prev;
            }
            correctPrev((LockFreeLinkedListNode) prev, null);
        }
    }

    public final LockFreeLinkedListNode getPrevNode() {
        return LockFreeLinkedListKt.unwrap(getPrev());
    }

    /* JADX WARN: Incorrect condition in loop: B:3:0x0017 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean addOneIfEmpty(kotlinx.coroutines.internal.LockFreeLinkedListNode r3) {
        /*
            r2 = this;
            java.lang.String r0 = "node"
            kotlin.jvm.internal.Intrinsics.checkParameterIsNotNull(r3, r0)
            java.util.concurrent.atomic.AtomicReferenceFieldUpdater r0 = kotlinx.coroutines.internal.LockFreeLinkedListNode._prev$FU
            r0.lazySet(r3, r2)
            java.util.concurrent.atomic.AtomicReferenceFieldUpdater r0 = kotlinx.coroutines.internal.LockFreeLinkedListNode._next$FU
            r0.lazySet(r3, r2)
        Lf:
            java.lang.Object r0 = r2.getNext()
            r1 = r2
            kotlinx.coroutines.internal.LockFreeLinkedListNode r1 = (kotlinx.coroutines.internal.LockFreeLinkedListNode) r1
            if (r0 == r1) goto L1b
            r1 = 0
            return r1
        L1b:
            java.util.concurrent.atomic.AtomicReferenceFieldUpdater r1 = kotlinx.coroutines.internal.LockFreeLinkedListNode._next$FU
            boolean r1 = r1.compareAndSet(r2, r2, r3)
            if (r1 == 0) goto L28
            r3.finishAdd(r2)
            r1 = 1
            return r1
        L28:
            goto Lf
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.internal.LockFreeLinkedListNode.addOneIfEmpty(kotlinx.coroutines.internal.LockFreeLinkedListNode):boolean");
    }

    public final void addLast(LockFreeLinkedListNode node) {
        LockFreeLinkedListNode prev;
        Intrinsics.checkParameterIsNotNull(node, "node");
        do {
            Object prev2 = getPrev();
            if (prev2 == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
            }
            prev = (LockFreeLinkedListNode) prev2;
        } while (!prev.addNext(node, this));
    }

    public final <T extends LockFreeLinkedListNode> AddLastDesc<T> describeAddLast(T node) {
        Intrinsics.checkParameterIsNotNull(node, "node");
        return new AddLastDesc<>(this, node);
    }

    public final boolean addLastIf(LockFreeLinkedListNode node, Function0<Boolean> condition) {
        int tryCondAddNext;
        Intrinsics.checkParameterIsNotNull(node, "node");
        Intrinsics.checkParameterIsNotNull(condition, "condition");
        CondAddOp condAdd = new LockFreeLinkedListNode$makeCondAddOp$1(condition, node, node);
        do {
            Object prev = getPrev();
            if (prev == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
            }
            LockFreeLinkedListNode prev2 = (LockFreeLinkedListNode) prev;
            tryCondAddNext = prev2.tryCondAddNext(node, this, condAdd);
            if (tryCondAddNext == 1) {
                return true;
            }
        } while (tryCondAddNext != 2);
        return false;
    }

    public final boolean addLastIfPrev(LockFreeLinkedListNode node, Function1<? super LockFreeLinkedListNode, Boolean> predicate) {
        LockFreeLinkedListNode prev;
        Intrinsics.checkParameterIsNotNull(node, "node");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        do {
            Object prev2 = getPrev();
            if (prev2 == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
            }
            prev = (LockFreeLinkedListNode) prev2;
            if (!predicate.invoke(prev).booleanValue()) {
                return false;
            }
        } while (!prev.addNext(node, this));
        return true;
    }

    public final boolean addLastIfPrevAndIf(LockFreeLinkedListNode node, Function1<? super LockFreeLinkedListNode, Boolean> predicate, Function0<Boolean> condition) {
        int tryCondAddNext;
        Intrinsics.checkParameterIsNotNull(node, "node");
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        Intrinsics.checkParameterIsNotNull(condition, "condition");
        CondAddOp condAdd = new LockFreeLinkedListNode$makeCondAddOp$1(condition, node, node);
        do {
            Object prev = getPrev();
            if (prev == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
            }
            LockFreeLinkedListNode prev2 = (LockFreeLinkedListNode) prev;
            if (!predicate.invoke(prev2).booleanValue()) {
                return false;
            }
            tryCondAddNext = prev2.tryCondAddNext(node, this, condAdd);
            if (tryCondAddNext == 1) {
                return true;
            }
        } while (tryCondAddNext != 2);
        return false;
    }

    public final boolean addNext(LockFreeLinkedListNode node, LockFreeLinkedListNode next) {
        Intrinsics.checkParameterIsNotNull(node, "node");
        Intrinsics.checkParameterIsNotNull(next, "next");
        _prev$FU.lazySet(node, this);
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = _next$FU;
        atomicReferenceFieldUpdater.lazySet(node, next);
        if (!atomicReferenceFieldUpdater.compareAndSet(this, next, node)) {
            return false;
        }
        node.finishAdd(next);
        return true;
    }

    public final int tryCondAddNext(LockFreeLinkedListNode node, LockFreeLinkedListNode next, CondAddOp condAdd) {
        Intrinsics.checkParameterIsNotNull(node, "node");
        Intrinsics.checkParameterIsNotNull(next, "next");
        Intrinsics.checkParameterIsNotNull(condAdd, "condAdd");
        _prev$FU.lazySet(node, this);
        AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = _next$FU;
        atomicReferenceFieldUpdater.lazySet(node, next);
        condAdd.oldNext = next;
        if (atomicReferenceFieldUpdater.compareAndSet(this, next, condAdd)) {
            return condAdd.perform(this) == null ? 1 : 2;
        }
        return 0;
    }

    public boolean remove() {
        Object next;
        Removed removed;
        do {
            next = getNext();
            if ((next instanceof Removed) || next == this) {
                return false;
            }
            if (next == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
            }
            removed = ((LockFreeLinkedListNode) next).removed();
        } while (!_next$FU.compareAndSet(this, next, removed));
        finishRemove((LockFreeLinkedListNode) next);
        return true;
    }

    public final void helpRemove() {
        Object next = getNext();
        if (!(next instanceof Removed)) {
            next = null;
        }
        Removed removed = (Removed) next;
        if (removed == null) {
            throw new IllegalStateException("Must be invoked on a removed node".toString());
        }
        finishRemove(removed.ref);
    }

    public final LockFreeLinkedListNode removeFirstOrNull() {
        while (true) {
            Object next = getNext();
            if (next == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
            }
            LockFreeLinkedListNode first = (LockFreeLinkedListNode) next;
            if (first == this) {
                return null;
            }
            if (first.remove()) {
                return first;
            }
            first.helpDelete();
        }
    }

    public final RemoveFirstDesc<LockFreeLinkedListNode> describeRemoveFirst() {
        return new RemoveFirstDesc<>(this);
    }

    /* JADX WARN: Type inference failed for: r1v2, types: [T, kotlinx.coroutines.internal.LockFreeLinkedListNode] */
    public final /* synthetic */ <T> T removeFirstIfIsInstanceOf() {
        while (true) {
            Object next = getNext();
            if (next == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
            }
            ?? r1 = (T) ((LockFreeLinkedListNode) next);
            if (r1 == this) {
                return null;
            }
            Intrinsics.reifiedOperationMarker(3, "T");
            if (!(r1 instanceof Object)) {
                return null;
            }
            if (r1.remove()) {
                return r1;
            }
            r1.helpDelete();
        }
    }

    /* JADX WARN: Type inference failed for: r1v3, types: [T, java.lang.Object, kotlinx.coroutines.internal.LockFreeLinkedListNode] */
    public final /* synthetic */ <T> T removeFirstIfIsInstanceOfOrPeekIf(Function1<? super T, Boolean> predicate) {
        Intrinsics.checkParameterIsNotNull(predicate, "predicate");
        while (true) {
            Object next = getNext();
            if (next == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
            }
            LockFreeLinkedListNode lockFreeLinkedListNode = (LockFreeLinkedListNode) next;
            if (lockFreeLinkedListNode == this) {
                return null;
            }
            Intrinsics.reifiedOperationMarker(3, "T");
            if (!(lockFreeLinkedListNode instanceof Object)) {
                return null;
            }
            if (predicate.invoke(lockFreeLinkedListNode).booleanValue() || lockFreeLinkedListNode.remove()) {
                return lockFreeLinkedListNode;
            }
            lockFreeLinkedListNode.helpDelete();
        }
    }

    /* compiled from: LockFreeLinkedList.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\b\u0016\u0018\u0000*\f\b\u0000\u0010\u0003*\u00060\u0001j\u0002`\u00022\u00020\u0004B\u001b\u0012\n\u0010\u0005\u001a\u00060\u0001j\u0002`\u0002\u0012\u0006\u0010\u0006\u001a\u00028\u0000¢\u0006\u0004\b\u0007\u0010\bJ'\u0010\f\u001a\u00020\u000b2\n\u0010\t\u001a\u00060\u0001j\u0002`\u00022\n\u0010\n\u001a\u00060\u0001j\u0002`\u0002H\u0014¢\u0006\u0004\b\f\u0010\bJ)\u0010\u000e\u001a\u0004\u0018\u00010\r2\n\u0010\t\u001a\u00060\u0001j\u0002`\u00022\n\u0010\n\u001a\u00060\u0001j\u0002`\u0002H\u0014¢\u0006\u0004\b\u000e\u0010\u000fJ#\u0010\u0011\u001a\u00020\u00102\n\u0010\t\u001a\u00060\u0001j\u0002`\u00022\u0006\u0010\n\u001a\u00020\rH\u0014¢\u0006\u0004\b\u0011\u0010\u0012J\u001b\u0010\u0015\u001a\u00060\u0001j\u0002`\u00022\u0006\u0010\u0014\u001a\u00020\u0013H\u0004¢\u0006\u0004\b\u0015\u0010\u0016J'\u0010\u0017\u001a\u00020\r2\n\u0010\t\u001a\u00060\u0001j\u0002`\u00022\n\u0010\n\u001a\u00060\u0001j\u0002`\u0002H\u0014¢\u0006\u0004\b\u0017\u0010\u000fR\u001e\u0010\u001a\u001a\n\u0018\u00010\u0001j\u0004\u0018\u0001`\u00028D@\u0004X\u0084\u0004¢\u0006\u0006\u001a\u0004\b\u0018\u0010\u0019R\u0016\u0010\u0006\u001a\u00028\u00008\u0006@\u0007X\u0087\u0004¢\u0006\u0006\n\u0004\b\u0006\u0010\u001bR\u001e\u0010\u001d\u001a\n\u0018\u00010\u0001j\u0004\u0018\u0001`\u00028D@\u0004X\u0084\u0004¢\u0006\u0006\u001a\u0004\b\u001c\u0010\u0019R\u001a\u0010\u0005\u001a\u00060\u0001j\u0002`\u00028\u0006@\u0007X\u0087\u0004¢\u0006\u0006\n\u0004\b\u0005\u0010\u001b¨\u0006\u001e"}, d2 = {"Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AddLastDesc;", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "Lkotlinx/coroutines/internal/Node;", "T", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AbstractAtomicDesc;", "queue", "node", "<init>", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)V", "affected", "next", "", "finishOnSuccess", "", "onPrepare", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)Ljava/lang/Object;", "", "retry", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Ljava/lang/Object;)Z", "Lkotlinx/coroutines/internal/OpDescriptor;", "op", "takeAffectedNode", "(Lkotlinx/coroutines/internal/OpDescriptor;)Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "updatedNext", "getAffectedNode", "()Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "affectedNode", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "getOriginalNext", "originalNext", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    public static class AddLastDesc<T extends LockFreeLinkedListNode> extends AbstractAtomicDesc {
        private static final AtomicReferenceFieldUpdater _affectedNode$FU = AtomicReferenceFieldUpdater.newUpdater(AddLastDesc.class, Object.class, "_affectedNode");
        private volatile Object _affectedNode;
        public final T node;
        public final LockFreeLinkedListNode queue;

        public AddLastDesc(LockFreeLinkedListNode queue, T node) {
            Intrinsics.checkParameterIsNotNull(queue, "queue");
            Intrinsics.checkParameterIsNotNull(node, "node");
            this.queue = queue;
            this.node = node;
            if (DebugKt.getASSERTIONS_ENABLED()) {
                if (!(node._next == node && node._prev == node)) {
                    throw new AssertionError();
                }
            }
            this._affectedNode = null;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected final LockFreeLinkedListNode takeAffectedNode(OpDescriptor op) {
            Intrinsics.checkParameterIsNotNull(op, "op");
            while (true) {
                Object obj = this.queue._prev;
                if (obj == null) {
                    throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
                }
                LockFreeLinkedListNode prev = (LockFreeLinkedListNode) obj;
                Object next = prev._next;
                LockFreeLinkedListNode lockFreeLinkedListNode = this.queue;
                if (next == lockFreeLinkedListNode || next == op) {
                    return prev;
                }
                if (!(next instanceof OpDescriptor)) {
                    LockFreeLinkedListNode affected = lockFreeLinkedListNode.correctPrev(prev, op);
                    if (affected != null) {
                        return affected;
                    }
                } else {
                    ((OpDescriptor) next).perform(prev);
                }
            }
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected final LockFreeLinkedListNode getAffectedNode() {
            return (LockFreeLinkedListNode) this._affectedNode;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        /* renamed from: getOriginalNext, reason: from getter */
        protected final LockFreeLinkedListNode getQueue() {
            return this.queue;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected boolean retry(LockFreeLinkedListNode affected, Object next) {
            Intrinsics.checkParameterIsNotNull(affected, "affected");
            Intrinsics.checkParameterIsNotNull(next, "next");
            return next != this.queue;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected Object onPrepare(LockFreeLinkedListNode affected, LockFreeLinkedListNode next) {
            Intrinsics.checkParameterIsNotNull(affected, "affected");
            Intrinsics.checkParameterIsNotNull(next, "next");
            _affectedNode$FU.compareAndSet(this, null, affected);
            return null;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected Object updatedNext(LockFreeLinkedListNode affected, LockFreeLinkedListNode next) {
            Intrinsics.checkParameterIsNotNull(affected, "affected");
            Intrinsics.checkParameterIsNotNull(next, "next");
            LockFreeLinkedListNode._prev$FU.compareAndSet(this.node, this.node, affected);
            LockFreeLinkedListNode._next$FU.compareAndSet(this.node, this.node, this.queue);
            return this.node;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected void finishOnSuccess(LockFreeLinkedListNode affected, LockFreeLinkedListNode next) {
            Intrinsics.checkParameterIsNotNull(affected, "affected");
            Intrinsics.checkParameterIsNotNull(next, "next");
            this.node.finishAdd(this.queue);
        }
    }

    /* compiled from: LockFreeLinkedList.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0013\b\u0016\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002B\u0013\u0012\n\u0010\u0005\u001a\u00060\u0003j\u0002`\u0004¢\u0006\u0004\b\u0006\u0010\u0007J\u001d\u0010\n\u001a\u0004\u0018\u00010\t2\n\u0010\b\u001a\u00060\u0003j\u0002`\u0004H\u0014¢\u0006\u0004\b\n\u0010\u000bJ'\u0010\u000e\u001a\u00020\r2\n\u0010\b\u001a\u00060\u0003j\u0002`\u00042\n\u0010\f\u001a\u00060\u0003j\u0002`\u0004H\u0004¢\u0006\u0004\b\u000e\u0010\u000fJ)\u0010\u0010\u001a\u0004\u0018\u00010\t2\n\u0010\b\u001a\u00060\u0003j\u0002`\u00042\n\u0010\f\u001a\u00060\u0003j\u0002`\u0004H\u0004¢\u0006\u0004\b\u0010\u0010\u0011J#\u0010\u0013\u001a\u00020\u00122\n\u0010\b\u001a\u00060\u0003j\u0002`\u00042\u0006\u0010\f\u001a\u00020\tH\u0004¢\u0006\u0004\b\u0013\u0010\u0014J\u001b\u0010\u0017\u001a\u00060\u0003j\u0002`\u00042\u0006\u0010\u0016\u001a\u00020\u0015H\u0004¢\u0006\u0004\b\u0017\u0010\u0018J'\u0010\u0019\u001a\u00020\t2\n\u0010\b\u001a\u00060\u0003j\u0002`\u00042\n\u0010\f\u001a\u00060\u0003j\u0002`\u0004H\u0004¢\u0006\u0004\b\u0019\u0010\u0011J\u0017\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u001a\u001a\u00028\u0000H\u0014¢\u0006\u0004\b\u001b\u0010\u001cR\u001e\u0010\u001f\u001a\n\u0018\u00010\u0003j\u0004\u0018\u0001`\u00048D@\u0004X\u0084\u0004¢\u0006\u0006\u001a\u0004\b\u001d\u0010\u001eR\u001e\u0010!\u001a\n\u0018\u00010\u0003j\u0004\u0018\u0001`\u00048D@\u0004X\u0084\u0004¢\u0006\u0006\u001a\u0004\b \u0010\u001eR\u001a\u0010\u0005\u001a\u00060\u0003j\u0002`\u00048\u0006@\u0007X\u0087\u0004¢\u0006\u0006\n\u0004\b\u0005\u0010\"R\u0019\u0010'\u001a\u00028\u00008F@\u0006¢\u0006\f\u0012\u0004\b%\u0010&\u001a\u0004\b#\u0010$¨\u0006("}, d2 = {"Lkotlinx/coroutines/internal/LockFreeLinkedListNode$RemoveFirstDesc;", "T", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AbstractAtomicDesc;", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "Lkotlinx/coroutines/internal/Node;", "queue", "<init>", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)V", "affected", "", "failure", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)Ljava/lang/Object;", "next", "", "finishOnSuccess", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)V", "onPrepare", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/LockFreeLinkedListNode;)Ljava/lang/Object;", "", "retry", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Ljava/lang/Object;)Z", "Lkotlinx/coroutines/internal/OpDescriptor;", "op", "takeAffectedNode", "(Lkotlinx/coroutines/internal/OpDescriptor;)Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "updatedNext", "node", "validatePrepared", "(Ljava/lang/Object;)Z", "getAffectedNode", "()Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "affectedNode", "getOriginalNext", "originalNext", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "getResult", "()Ljava/lang/Object;", "result$annotations", "()V", "result", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    public static class RemoveFirstDesc<T> extends AbstractAtomicDesc {
        private static final AtomicReferenceFieldUpdater _affectedNode$FU = AtomicReferenceFieldUpdater.newUpdater(RemoveFirstDesc.class, Object.class, "_affectedNode");
        private static final AtomicReferenceFieldUpdater _originalNext$FU = AtomicReferenceFieldUpdater.newUpdater(RemoveFirstDesc.class, Object.class, "_originalNext");
        private volatile Object _affectedNode;
        private volatile Object _originalNext;
        public final LockFreeLinkedListNode queue;

        public static /* synthetic */ void result$annotations() {
        }

        public RemoveFirstDesc(LockFreeLinkedListNode queue) {
            Intrinsics.checkParameterIsNotNull(queue, "queue");
            this.queue = queue;
            this._affectedNode = null;
            this._originalNext = null;
        }

        public final T getResult() {
            Object affectedNode = getAffectedNode();
            if (affectedNode == null) {
                Intrinsics.throwNpe();
            }
            return (T) affectedNode;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected final LockFreeLinkedListNode takeAffectedNode(OpDescriptor op) {
            Intrinsics.checkParameterIsNotNull(op, "op");
            Object next = this.queue.getNext();
            if (next != null) {
                return (LockFreeLinkedListNode) next;
            }
            throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected final LockFreeLinkedListNode getAffectedNode() {
            return (LockFreeLinkedListNode) this._affectedNode;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        /* renamed from: getOriginalNext */
        protected final LockFreeLinkedListNode getQueue() {
            return (LockFreeLinkedListNode) this._originalNext;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected Object failure(LockFreeLinkedListNode affected) {
            Intrinsics.checkParameterIsNotNull(affected, "affected");
            if (affected == this.queue) {
                return LockFreeLinkedListKt.getLIST_EMPTY();
            }
            return null;
        }

        protected boolean validatePrepared(T node) {
            return true;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected final boolean retry(LockFreeLinkedListNode affected, Object next) {
            Intrinsics.checkParameterIsNotNull(affected, "affected");
            Intrinsics.checkParameterIsNotNull(next, "next");
            if (!(next instanceof Removed)) {
                return false;
            }
            affected.helpDelete();
            return true;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected final Object onPrepare(LockFreeLinkedListNode affected, LockFreeLinkedListNode next) {
            Object obj;
            Intrinsics.checkParameterIsNotNull(affected, "affected");
            Intrinsics.checkParameterIsNotNull(next, "next");
            if (DebugKt.getASSERTIONS_ENABLED() && !(!(affected instanceof LockFreeLinkedListHead))) {
                throw new AssertionError();
            }
            if (!validatePrepared(affected)) {
                obj = LockFreeLinkedListKt.REMOVE_PREPARED;
                return obj;
            }
            _affectedNode$FU.compareAndSet(this, null, affected);
            _originalNext$FU.compareAndSet(this, null, next);
            return null;
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected final Object updatedNext(LockFreeLinkedListNode affected, LockFreeLinkedListNode next) {
            Intrinsics.checkParameterIsNotNull(affected, "affected");
            Intrinsics.checkParameterIsNotNull(next, "next");
            return next.removed();
        }

        @Override // kotlinx.coroutines.internal.LockFreeLinkedListNode.AbstractAtomicDesc
        protected final void finishOnSuccess(LockFreeLinkedListNode affected, LockFreeLinkedListNode next) {
            Intrinsics.checkParameterIsNotNull(affected, "affected");
            Intrinsics.checkParameterIsNotNull(next, "next");
            affected.finishRemove(next);
        }
    }

    /* compiled from: LockFreeLinkedList.kt */
    @Metadata(bv = {1, 0, 3}, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b&\u0018\u00002\u00020\u0001:\u0001\u001aB\u0005¢\u0006\u0002\u0010\u0002J\u001c\u0010\n\u001a\u00020\u000b2\n\u0010\f\u001a\u0006\u0012\u0002\b\u00030\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fJ\u0016\u0010\u000e\u001a\u0004\u0018\u00010\u000f2\n\u0010\u0010\u001a\u00060\u0004j\u0002`\u0005H\u0014J \u0010\u0011\u001a\u00020\u000b2\n\u0010\u0010\u001a\u00060\u0004j\u0002`\u00052\n\u0010\u0012\u001a\u00060\u0004j\u0002`\u0005H$J\"\u0010\u0013\u001a\u0004\u0018\u00010\u000f2\n\u0010\u0010\u001a\u00060\u0004j\u0002`\u00052\n\u0010\u0012\u001a\u00060\u0004j\u0002`\u0005H$J\u0014\u0010\u0014\u001a\u0004\u0018\u00010\u000f2\n\u0010\f\u001a\u0006\u0012\u0002\b\u00030\rJ\u001c\u0010\u0015\u001a\u00020\u00162\n\u0010\u0010\u001a\u00060\u0004j\u0002`\u00052\u0006\u0010\u0012\u001a\u00020\u000fH\u0014J\u0014\u0010\u0017\u001a\u00060\u0004j\u0002`\u00052\u0006\u0010\f\u001a\u00020\u0018H\u0014J \u0010\u0019\u001a\u00020\u000f2\n\u0010\u0010\u001a\u00060\u0004j\u0002`\u00052\n\u0010\u0012\u001a\u00060\u0004j\u0002`\u0005H$R\u001a\u0010\u0003\u001a\n\u0018\u00010\u0004j\u0004\u0018\u0001`\u0005X¤\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u001a\u0010\b\u001a\n\u0018\u00010\u0004j\u0004\u0018\u0001`\u0005X¤\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\u0007¨\u0006\u001b"}, d2 = {"Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AbstractAtomicDesc;", "Lkotlinx/coroutines/internal/AtomicDesc;", "()V", "affectedNode", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "Lkotlinx/coroutines/internal/Node;", "getAffectedNode", "()Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "originalNext", "getOriginalNext", "complete", "", "op", "Lkotlinx/coroutines/internal/AtomicOp;", "failure", "", "affected", "finishOnSuccess", "next", "onPrepare", "prepare", "retry", "", "takeAffectedNode", "Lkotlinx/coroutines/internal/OpDescriptor;", "updatedNext", "PrepareOp", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
    public static abstract class AbstractAtomicDesc extends AtomicDesc {
        protected abstract void finishOnSuccess(LockFreeLinkedListNode affected, LockFreeLinkedListNode next);

        protected abstract LockFreeLinkedListNode getAffectedNode();

        /* renamed from: getOriginalNext */
        protected abstract LockFreeLinkedListNode getQueue();

        protected abstract Object onPrepare(LockFreeLinkedListNode affected, LockFreeLinkedListNode next);

        protected abstract Object updatedNext(LockFreeLinkedListNode affected, LockFreeLinkedListNode next);

        protected LockFreeLinkedListNode takeAffectedNode(OpDescriptor op) {
            Intrinsics.checkParameterIsNotNull(op, "op");
            LockFreeLinkedListNode affectedNode = getAffectedNode();
            if (affectedNode == null) {
                Intrinsics.throwNpe();
            }
            return affectedNode;
        }

        protected Object failure(LockFreeLinkedListNode affected) {
            Intrinsics.checkParameterIsNotNull(affected, "affected");
            return null;
        }

        protected boolean retry(LockFreeLinkedListNode affected, Object next) {
            Intrinsics.checkParameterIsNotNull(affected, "affected");
            Intrinsics.checkParameterIsNotNull(next, "next");
            return false;
        }

        /* compiled from: LockFreeLinkedList.kt */
        @Metadata(bv = {1, 0, 3}, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\b\u0002\u0018\u00002\u00020\u0001B+\u0012\n\u0010\u0002\u001a\u00060\u0003j\u0002`\u0004\u0012\u0010\u0010\u0005\u001a\f\u0012\b\u0012\u00060\u0003j\u0002`\u00040\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0002\u0010\tJ\u0014\u0010\n\u001a\u0004\u0018\u00010\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u000bH\u0016R\u0010\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0002\u001a\u00060\u0003j\u0002`\u00048\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0005\u001a\f\u0012\b\u0012\u00060\u0003j\u0002`\u00040\u00068\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\r"}, d2 = {"Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AbstractAtomicDesc$PrepareOp;", "Lkotlinx/coroutines/internal/OpDescriptor;", "next", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode;", "Lkotlinx/coroutines/internal/Node;", "op", "Lkotlinx/coroutines/internal/AtomicOp;", "desc", "Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AbstractAtomicDesc;", "(Lkotlinx/coroutines/internal/LockFreeLinkedListNode;Lkotlinx/coroutines/internal/AtomicOp;Lkotlinx/coroutines/internal/LockFreeLinkedListNode$AbstractAtomicDesc;)V", "perform", "", "affected", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
        private static final class PrepareOp extends OpDescriptor {
            public final AbstractAtomicDesc desc;
            public final LockFreeLinkedListNode next;
            public final AtomicOp<LockFreeLinkedListNode> op;

            /* JADX WARN: Multi-variable type inference failed */
            public PrepareOp(LockFreeLinkedListNode next, AtomicOp<? super LockFreeLinkedListNode> op, AbstractAtomicDesc desc) {
                Intrinsics.checkParameterIsNotNull(next, "next");
                Intrinsics.checkParameterIsNotNull(op, "op");
                Intrinsics.checkParameterIsNotNull(desc, "desc");
                this.next = next;
                this.op = op;
                this.desc = desc;
            }

            @Override // kotlinx.coroutines.internal.OpDescriptor
            public Object perform(Object affected) {
                Object obj;
                if (affected == null) {
                    throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
                }
                Object decision = this.desc.onPrepare((LockFreeLinkedListNode) affected, this.next);
                if (decision != null) {
                    obj = LockFreeLinkedListKt.REMOVE_PREPARED;
                    if (decision == obj) {
                        Removed removed = this.next.removed();
                        if (LockFreeLinkedListNode._next$FU.compareAndSet((LockFreeLinkedListNode) affected, this, removed)) {
                            ((LockFreeLinkedListNode) affected).helpDelete();
                        }
                    } else {
                        this.op.tryDecide(decision);
                        LockFreeLinkedListNode._next$FU.compareAndSet((LockFreeLinkedListNode) affected, this, this.next);
                    }
                    return decision;
                }
                Object update = this.op.isDecided() ? this.next : this.op;
                LockFreeLinkedListNode._next$FU.compareAndSet((LockFreeLinkedListNode) affected, this, update);
                return null;
            }
        }

        @Override // kotlinx.coroutines.internal.AtomicDesc
        public final Object prepare(AtomicOp<?> op) {
            Object obj;
            Intrinsics.checkParameterIsNotNull(op, "op");
            while (true) {
                LockFreeLinkedListNode affected = takeAffectedNode(op);
                Object next = affected._next;
                if (next == op || op.isDecided()) {
                    return null;
                }
                if (next instanceof OpDescriptor) {
                    ((OpDescriptor) next).perform(affected);
                } else {
                    Object failure = failure(affected);
                    if (failure != null) {
                        return failure;
                    }
                    if (retry(affected, next)) {
                        continue;
                    } else if (next != null) {
                        PrepareOp prepareOp = new PrepareOp((LockFreeLinkedListNode) next, op, this);
                        if (LockFreeLinkedListNode._next$FU.compareAndSet(affected, next, prepareOp)) {
                            Object prepFail = prepareOp.perform(affected);
                            obj = LockFreeLinkedListKt.REMOVE_PREPARED;
                            if (prepFail != obj) {
                                return prepFail;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
                    }
                }
            }
        }

        @Override // kotlinx.coroutines.internal.AtomicDesc
        public final void complete(AtomicOp<?> op, Object failure) {
            Intrinsics.checkParameterIsNotNull(op, "op");
            boolean success = failure == null;
            LockFreeLinkedListNode affectedNode = getAffectedNode();
            if (affectedNode == null) {
                if (DebugKt.getASSERTIONS_ENABLED()) {
                    if (!(success ? false : true)) {
                        throw new AssertionError();
                    }
                    return;
                }
                return;
            }
            LockFreeLinkedListNode originalNext = getQueue();
            if (originalNext == null) {
                if (DebugKt.getASSERTIONS_ENABLED()) {
                    if (!(success ? false : true)) {
                        throw new AssertionError();
                    }
                    return;
                }
                return;
            }
            Object update = success ? updatedNext(affectedNode, originalNext) : originalNext;
            if (!LockFreeLinkedListNode._next$FU.compareAndSet(affectedNode, op, update) || !success) {
                return;
            }
            finishOnSuccess(affectedNode, originalNext);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void finishAdd(LockFreeLinkedListNode next) {
        Object nextPrev;
        do {
            nextPrev = next._prev;
            if ((nextPrev instanceof Removed) || getNext() != next) {
                return;
            }
        } while (!_prev$FU.compareAndSet(next, nextPrev, this));
        if (getNext() instanceof Removed) {
            if (nextPrev == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
            }
            next.correctPrev((LockFreeLinkedListNode) nextPrev, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void finishRemove(LockFreeLinkedListNode next) {
        helpDelete();
        next.correctPrev(LockFreeLinkedListKt.unwrap(this._prev), null);
    }

    private final LockFreeLinkedListNode markPrev() {
        Object prev;
        LockFreeLinkedListNode lockFreeLinkedListNode;
        Removed removedPrev;
        do {
            prev = this._prev;
            if (prev instanceof Removed) {
                return ((Removed) prev).ref;
            }
            if (prev == this) {
                lockFreeLinkedListNode = findHead();
            } else {
                if (prev == null) {
                    throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
                }
                lockFreeLinkedListNode = (LockFreeLinkedListNode) prev;
            }
            removedPrev = lockFreeLinkedListNode.removed();
        } while (!_prev$FU.compareAndSet(this, prev, removedPrev));
        return (LockFreeLinkedListNode) prev;
    }

    private final LockFreeLinkedListNode findHead() {
        LockFreeLinkedListNode lockFreeLinkedListNode = this;
        while (!(lockFreeLinkedListNode instanceof LockFreeLinkedListHead)) {
            lockFreeLinkedListNode = lockFreeLinkedListNode.getNextNode();
            if (DebugKt.getASSERTIONS_ENABLED()) {
                if (!(lockFreeLinkedListNode != this)) {
                    throw new AssertionError();
                }
            }
        }
        return lockFreeLinkedListNode;
    }

    public final void helpDelete() {
        LockFreeLinkedListNode last = (LockFreeLinkedListNode) null;
        LockFreeLinkedListNode prev = markPrev();
        Object obj = this._next;
        if (obj == null) {
            throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Removed");
        }
        LockFreeLinkedListNode next = ((Removed) obj).ref;
        while (true) {
            Object nextNext = next.getNext();
            if (nextNext instanceof Removed) {
                next.markPrev();
                next = ((Removed) nextNext).ref;
            } else {
                Object prevNext = prev.getNext();
                if (prevNext instanceof Removed) {
                    if (last != null) {
                        prev.markPrev();
                        _next$FU.compareAndSet(last, prev, ((Removed) prevNext).ref);
                        prev = last;
                        last = (LockFreeLinkedListNode) null;
                    } else {
                        prev = LockFreeLinkedListKt.unwrap(prev._prev);
                    }
                } else if (prevNext != this) {
                    last = prev;
                    if (prevNext == null) {
                        throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
                    }
                    prev = (LockFreeLinkedListNode) prevNext;
                    if (prev == next) {
                        return;
                    }
                } else if (_next$FU.compareAndSet(prev, this, next)) {
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final LockFreeLinkedListNode correctPrev(LockFreeLinkedListNode _prev, OpDescriptor op) {
        LockFreeLinkedListNode prev = _prev;
        LockFreeLinkedListNode last = (LockFreeLinkedListNode) null;
        while (true) {
            Object prevNext = prev._next;
            if (prevNext == op) {
                return prev;
            }
            if (prevNext instanceof OpDescriptor) {
                ((OpDescriptor) prevNext).perform(prev);
            } else if (prevNext instanceof Removed) {
                if (last != null) {
                    prev.markPrev();
                    _next$FU.compareAndSet(last, prev, ((Removed) prevNext).ref);
                    prev = last;
                    last = (LockFreeLinkedListNode) null;
                } else {
                    prev = LockFreeLinkedListKt.unwrap(prev._prev);
                }
            } else {
                Object oldPrev = this._prev;
                if (oldPrev instanceof Removed) {
                    return null;
                }
                if (prevNext != this) {
                    last = prev;
                    if (prevNext == null) {
                        throw new TypeCastException("null cannot be cast to non-null type kotlinx.coroutines.internal.Node /* = kotlinx.coroutines.internal.LockFreeLinkedListNode */");
                    }
                    prev = (LockFreeLinkedListNode) prevNext;
                } else {
                    if (oldPrev == prev) {
                        return null;
                    }
                    if (_prev$FU.compareAndSet(this, oldPrev, prev) && !(prev._prev instanceof Removed)) {
                        return null;
                    }
                }
            }
        }
    }

    public final void validateNode$kotlinx_coroutines_core(LockFreeLinkedListNode prev, LockFreeLinkedListNode next) {
        Intrinsics.checkParameterIsNotNull(prev, "prev");
        Intrinsics.checkParameterIsNotNull(next, "next");
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if ((prev == this._prev ? 1 : 0) == 0) {
                throw new AssertionError();
            }
        }
        if (DebugKt.getASSERTIONS_ENABLED()) {
            if (!(next == this._next)) {
                throw new AssertionError();
            }
        }
    }

    public String toString() {
        return getClass().getSimpleName() + '@' + Integer.toHexString(System.identityHashCode(this));
    }
}
