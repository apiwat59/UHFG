package kotlinx.coroutines.flow;

import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlinx.coroutines.flow.internal.NullSurrogateKt;

/* compiled from: Distinct.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000&\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u001e\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u0001H\u0007\u001aV\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u000126\u0010\u0003\u001a2\u0012\u0013\u0012\u0011H\u0002¢\u0006\f\b\u0005\u0012\b\b\u0006\u0012\u0004\b\b(\u0007\u0012\u0013\u0012\u0011H\u0002¢\u0006\f\b\u0005\u0012\b\b\u0006\u0012\u0004\b\b(\b\u0012\u0004\u0012\u00020\t0\u0004H\u0007\u001a8\u0010\n\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u000b*\b\u0012\u0004\u0012\u0002H\u00020\u00012\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u000b0\rH\u0007\u001az\u0010\n\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u000b*\b\u0012\u0004\u0012\u0002H\u00020\u00012\u0014\b\u0004\u0010\f\u001a\u000e\u0012\u0004\u0012\u0002H\u0002\u0012\u0004\u0012\u0002H\u000b0\r28\b\u0004\u0010\u0003\u001a2\u0012\u0013\u0012\u0011H\u000b¢\u0006\f\b\u0005\u0012\b\b\u0006\u0012\u0004\b\b(\u0007\u0012\u0013\u0012\u0011H\u000b¢\u0006\f\b\u0005\u0012\b\b\u0006\u0012\u0004\b\b(\b\u0012\u0004\u0012\u00020\t0\u0004H\u0082\b¢\u0006\u0002\b\u000e¨\u0006\u000f"}, d2 = {"distinctUntilChanged", "Lkotlinx/coroutines/flow/Flow;", "T", "areEquivalent", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "old", "new", "", "distinctUntilChangedBy", "K", "keySelector", "Lkotlin/Function1;", "distinctUntilChangedBy$FlowKt__DistinctKt", "kotlinx-coroutines-core"}, k = 5, mv = {1, 1, 15}, xs = "kotlinx/coroutines/flow/FlowKt")
/* loaded from: classes.dex */
final /* synthetic */ class FlowKt__DistinctKt {
    public static final <T> Flow<T> distinctUntilChanged(Flow<? extends T> distinctUntilChanged) {
        Intrinsics.checkParameterIsNotNull(distinctUntilChanged, "$this$distinctUntilChanged");
        return FlowKt.distinctUntilChangedBy(distinctUntilChanged, new Function1<T, T>() { // from class: kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChanged$1
            @Override // kotlin.jvm.functions.Function1
            public final T invoke(T t) {
                return t;
            }
        });
    }

    public static final <T> Flow<T> distinctUntilChanged(final Flow<? extends T> distinctUntilChanged, final Function2<? super T, ? super T, Boolean> areEquivalent) {
        Intrinsics.checkParameterIsNotNull(distinctUntilChanged, "$this$distinctUntilChanged");
        Intrinsics.checkParameterIsNotNull(areEquivalent, "areEquivalent");
        return new Flow<T>() { // from class: kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1

            /* compiled from: Collect.kt */
            @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u007f\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0004\n\u0002\b\u0004\n\u0002\b\u0004\n\u0002\b\u0005*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00028\u00000\u0001J\u0019\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00028\u0000H\u0096@ø\u0001\u0000¢\u0006\u0002\u0010\u0005\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u0006¸\u0006\b"}, d2 = {"kotlinx/coroutines/flow/FlowKt__CollectKt$collect$3", "Lkotlinx/coroutines/flow/FlowCollector;", "emit", "", "value", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "kotlinx-coroutines-core", "kotlinx/coroutines/flow/FlowKt__DistinctKt$$special$$inlined$collect$1", "kotlinx/coroutines/flow/FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$unsafeFlow$1$lambda$1"}, k = 1, mv = {1, 1, 15})
            /* renamed from: kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1$2, reason: invalid class name */
            public static final class AnonymousClass2 implements FlowCollector<T> {
                final /* synthetic */ Ref.ObjectRef $previousKey$inlined;
                final /* synthetic */ FlowCollector $this_unsafeFlow$inlined;
                final /* synthetic */ FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1 this$0;

                @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0082\u0001\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\b\u0003\u0010\u0000\u001a\u0004\u0018\u00010\u0001\"\u0004\b\u0000\u0010\u00022\u0006\u0010\u0003\u001a\u0002H\u00022\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0096@¨\u0006\t"}, d2 = {"emit", "", "T", "value", "continuation", "Lkotlin/coroutines/Continuation;", "", "kotlinx/coroutines/flow/FlowKt__CollectKt$collect$3$emit$1", "kotlinx/coroutines/flow/FlowKt__DistinctKt$$special$$inlined$collect$1$1", "kotlinx/coroutines/flow/FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$unsafeFlow$1$lambda$1$1"}, k = 3, mv = {1, 1, 15})
                @DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1$2", f = "Distinct.kt", i = {0, 0, 0, 0, 0}, l = {140}, m = "emit", n = {"this", "value", "continuation", "value", "key"}, s = {"L$0", "L$1", "L$2", "L$3", "L$4"})
                /* renamed from: kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1$2$1, reason: invalid class name */
                public static final class AnonymousClass1 extends ContinuationImpl {
                    Object L$0;
                    Object L$1;
                    Object L$2;
                    Object L$3;
                    Object L$4;
                    int label;
                    /* synthetic */ Object result;

                    public AnonymousClass1(Continuation continuation) {
                        super(continuation);
                    }

                    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
                    public final Object invokeSuspend(Object obj) {
                        this.result = obj;
                        this.label |= Integer.MIN_VALUE;
                        return AnonymousClass2.this.emit(null, this);
                    }
                }

                /* JADX WARN: Multi-variable type inference failed */
                /* JADX WARN: Removed duplicated region for block: B:16:0x0048  */
                /* JADX WARN: Removed duplicated region for block: B:8:0x0024  */
                @Override // kotlinx.coroutines.flow.FlowCollector
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct code enable 'Show inconsistent code' option in preferences
                */
                public java.lang.Object emit(java.lang.Object r12, kotlin.coroutines.Continuation r13) {
                    /*
                        r11 = this;
                        boolean r0 = r13 instanceof kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1.AnonymousClass2.AnonymousClass1
                        if (r0 == 0) goto L14
                        r0 = r13
                        kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1$2$1 r0 = (kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1.AnonymousClass2.AnonymousClass1) r0
                        int r1 = r0.label
                        r2 = -2147483648(0xffffffff80000000, float:-0.0)
                        r1 = r1 & r2
                        if (r1 == 0) goto L14
                        int r1 = r0.label
                        int r1 = r1 - r2
                        r0.label = r1
                        goto L19
                    L14:
                        kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1$2$1 r0 = new kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1$2$1
                        r0.<init>(r13)
                    L19:
                        java.lang.Object r1 = r0.result
                        java.lang.Object r2 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
                        int r3 = r0.label
                        r4 = 1
                        if (r3 == 0) goto L48
                        if (r3 != r4) goto L40
                        r2 = 0
                        r3 = r2
                        r4 = 0
                        r5 = r2
                        java.lang.Object r5 = r0.L$4
                        java.lang.Object r2 = r0.L$3
                        java.lang.Object r6 = r0.L$2
                        r3 = r6
                        kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1$2$1 r3 = (kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1.AnonymousClass2.AnonymousClass1) r3
                        java.lang.Object r12 = r0.L$1
                        java.lang.Object r6 = r0.L$0
                        kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1$2 r6 = (kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1.AnonymousClass2) r6
                        kotlin.ResultKt.throwOnFailure(r1)
                        r7 = r6
                        r6 = r4
                        r4 = r1
                        goto L8d
                    L40:
                        java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
                        java.lang.String r1 = "call to 'resume' before 'invoke' with coroutine"
                        r0.<init>(r1)
                        throw r0
                    L48:
                        kotlin.ResultKt.throwOnFailure(r1)
                        r3 = r0
                        r5 = r12
                        r6 = 0
                        r7 = r5
                        r8 = 0
                        kotlin.jvm.internal.Ref$ObjectRef r8 = r11.$previousKey$inlined
                        T r8 = r8.element
                        kotlinx.coroutines.internal.Symbol r9 = kotlinx.coroutines.flow.internal.NullSurrogateKt.NULL
                        if (r8 == r9) goto L71
                        kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1 r8 = r11.this$0
                        kotlin.jvm.functions.Function2 r8 = r2
                        kotlin.jvm.internal.Ref$ObjectRef r9 = r11.$previousKey$inlined
                        T r9 = r9.element
                        java.lang.Object r8 = r8.invoke(r9, r7)
                        java.lang.Boolean r8 = (java.lang.Boolean) r8
                        boolean r8 = r8.booleanValue()
                        if (r8 != 0) goto L6f
                        goto L71
                    L6f:
                        r2 = r11
                        goto L93
                    L71:
                        kotlin.jvm.internal.Ref$ObjectRef r8 = r11.$previousKey$inlined
                        r8.element = r7
                        kotlinx.coroutines.flow.FlowCollector r8 = r11.$this_unsafeFlow$inlined
                        r0.L$0 = r11
                        r0.L$1 = r12
                        r0.L$2 = r3
                        r0.L$3 = r5
                        r0.L$4 = r7
                        r0.label = r4
                        java.lang.Object r4 = r8.emit(r5, r0)
                        if (r4 != r2) goto L8a
                        return r2
                    L8a:
                        r2 = r5
                        r5 = r7
                        r7 = r11
                    L8d:
                        kotlin.Unit r4 = (kotlin.Unit) r4
                        r10 = r5
                        r5 = r2
                        r2 = r7
                        r7 = r10
                    L93:
                        kotlin.Unit r3 = kotlin.Unit.INSTANCE
                        return r3
                    */
                    throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1.AnonymousClass2.emit(java.lang.Object, kotlin.coroutines.Continuation):java.lang.Object");
                }

                public AnonymousClass2(FlowCollector flowCollector, Ref.ObjectRef objectRef, FlowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1 flowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1) {
                    this.$this_unsafeFlow$inlined = flowCollector;
                    this.$previousKey$inlined = objectRef;
                    this.this$0 = flowKt__DistinctKt$distinctUntilChanged$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1;
                }
            }

            @Override // kotlinx.coroutines.flow.Flow
            public Object collect(FlowCollector flowCollector, Continuation continuation) {
                Ref.ObjectRef objectRef = new Ref.ObjectRef();
                objectRef.element = (T) NullSurrogateKt.NULL;
                return Flow.this.collect(new AnonymousClass2(flowCollector, objectRef, this), continuation);
            }
        };
    }

    public static final <T, K> Flow<T> distinctUntilChangedBy(final Flow<? extends T> distinctUntilChangedBy, final Function1<? super T, ? extends K> keySelector) {
        Intrinsics.checkParameterIsNotNull(distinctUntilChangedBy, "$this$distinctUntilChangedBy");
        Intrinsics.checkParameterIsNotNull(keySelector, "keySelector");
        return new Flow<T>() { // from class: kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1

            /* compiled from: Collect.kt */
            @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u007f\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0004\n\u0002\b\u0004\n\u0002\b\u0004\n\u0002\b\u0005*\u0001\u0000\b\n\u0018\u00002\b\u0012\u0004\u0012\u00028\u00000\u0001J\u0019\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00028\u0000H\u0096@ø\u0001\u0000¢\u0006\u0002\u0010\u0005\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u0006¸\u0006\b"}, d2 = {"kotlinx/coroutines/flow/FlowKt__CollectKt$collect$3", "Lkotlinx/coroutines/flow/FlowCollector;", "emit", "", "value", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "kotlinx-coroutines-core", "kotlinx/coroutines/flow/FlowKt__DistinctKt$$special$$inlined$collect$2", "kotlinx/coroutines/flow/FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$unsafeFlow$2$lambda$1"}, k = 1, mv = {1, 1, 15})
            /* renamed from: kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1$2, reason: invalid class name */
            public static final class AnonymousClass2 implements FlowCollector<T> {
                final /* synthetic */ Ref.ObjectRef $previousKey$inlined;
                final /* synthetic */ FlowCollector $this_unsafeFlow$inlined;
                final /* synthetic */ FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1 this$0;

                @Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0082\u0001\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\b\u0003\u0010\u0000\u001a\u0004\u0018\u00010\u0001\"\u0004\b\u0000\u0010\u00022\u0006\u0010\u0003\u001a\u0002H\u00022\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0096@¨\u0006\t"}, d2 = {"emit", "", "T", "value", "continuation", "Lkotlin/coroutines/Continuation;", "", "kotlinx/coroutines/flow/FlowKt__CollectKt$collect$3$emit$1", "kotlinx/coroutines/flow/FlowKt__DistinctKt$$special$$inlined$collect$2$1", "kotlinx/coroutines/flow/FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$unsafeFlow$2$lambda$1$1"}, k = 3, mv = {1, 1, 15})
                @DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1$2", f = "Distinct.kt", i = {0, 0, 0, 0, 0}, l = {140}, m = "emit", n = {"this", "value", "continuation", "value", "key"}, s = {"L$0", "L$1", "L$2", "L$3", "L$4"})
                /* renamed from: kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1$2$1, reason: invalid class name */
                public static final class AnonymousClass1 extends ContinuationImpl {
                    Object L$0;
                    Object L$1;
                    Object L$2;
                    Object L$3;
                    Object L$4;
                    int label;
                    /* synthetic */ Object result;

                    public AnonymousClass1(Continuation continuation) {
                        super(continuation);
                    }

                    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
                    public final Object invokeSuspend(Object obj) {
                        this.result = obj;
                        this.label |= Integer.MIN_VALUE;
                        return AnonymousClass2.this.emit(null, this);
                    }
                }

                /* JADX WARN: Removed duplicated region for block: B:16:0x0048  */
                /* JADX WARN: Removed duplicated region for block: B:8:0x0024  */
                @Override // kotlinx.coroutines.flow.FlowCollector
                /*
                    Code decompiled incorrectly, please refer to instructions dump.
                    To view partially-correct code enable 'Show inconsistent code' option in preferences
                */
                public java.lang.Object emit(java.lang.Object r13, kotlin.coroutines.Continuation r14) {
                    /*
                        r12 = this;
                        boolean r0 = r14 instanceof kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1.AnonymousClass2.AnonymousClass1
                        if (r0 == 0) goto L14
                        r0 = r14
                        kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1$2$1 r0 = (kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1.AnonymousClass2.AnonymousClass1) r0
                        int r1 = r0.label
                        r2 = -2147483648(0xffffffff80000000, float:-0.0)
                        r1 = r1 & r2
                        if (r1 == 0) goto L14
                        int r1 = r0.label
                        int r1 = r1 - r2
                        r0.label = r1
                        goto L19
                    L14:
                        kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1$2$1 r0 = new kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1$2$1
                        r0.<init>(r14)
                    L19:
                        java.lang.Object r1 = r0.result
                        java.lang.Object r2 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
                        int r3 = r0.label
                        r4 = 1
                        if (r3 == 0) goto L48
                        if (r3 != r4) goto L40
                        r2 = 0
                        r3 = r2
                        r4 = 0
                        r5 = r2
                        java.lang.Object r5 = r0.L$4
                        java.lang.Object r2 = r0.L$3
                        java.lang.Object r6 = r0.L$2
                        r3 = r6
                        kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1$2$1 r3 = (kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1.AnonymousClass2.AnonymousClass1) r3
                        java.lang.Object r13 = r0.L$1
                        java.lang.Object r6 = r0.L$0
                        kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1$2 r6 = (kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1.AnonymousClass2) r6
                        kotlin.ResultKt.throwOnFailure(r1)
                        r7 = r6
                        r6 = r4
                        r4 = r1
                        goto L92
                    L40:
                        java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
                        java.lang.String r1 = "call to 'resume' before 'invoke' with coroutine"
                        r0.<init>(r1)
                        throw r0
                    L48:
                        kotlin.ResultKt.throwOnFailure(r1)
                        r3 = r0
                        r5 = r13
                        r6 = 0
                        kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1 r7 = r12.this$0
                        kotlin.jvm.functions.Function1 r7 = r2
                        java.lang.Object r7 = r7.invoke(r5)
                        kotlin.jvm.internal.Ref$ObjectRef r8 = r12.$previousKey$inlined
                        T r8 = r8.element
                        kotlinx.coroutines.internal.Symbol r9 = kotlinx.coroutines.flow.internal.NullSurrogateKt.NULL
                        if (r8 == r9) goto L76
                        kotlin.jvm.internal.Ref$ObjectRef r8 = r12.$previousKey$inlined
                        T r8 = r8.element
                        r9 = r7
                        r10 = 0
                        boolean r8 = kotlin.jvm.internal.Intrinsics.areEqual(r8, r9)
                        java.lang.Boolean r8 = kotlin.coroutines.jvm.internal.Boxing.boxBoolean(r8)
                        boolean r8 = r8.booleanValue()
                        if (r8 != 0) goto L74
                        goto L76
                    L74:
                        r2 = r12
                        goto L98
                    L76:
                        kotlin.jvm.internal.Ref$ObjectRef r8 = r12.$previousKey$inlined
                        r8.element = r7
                        kotlinx.coroutines.flow.FlowCollector r8 = r12.$this_unsafeFlow$inlined
                        r0.L$0 = r12
                        r0.L$1 = r13
                        r0.L$2 = r3
                        r0.L$3 = r5
                        r0.L$4 = r7
                        r0.label = r4
                        java.lang.Object r4 = r8.emit(r5, r0)
                        if (r4 != r2) goto L8f
                        return r2
                    L8f:
                        r2 = r5
                        r5 = r7
                        r7 = r12
                    L92:
                        kotlin.Unit r4 = (kotlin.Unit) r4
                        r11 = r5
                        r5 = r2
                        r2 = r7
                        r7 = r11
                    L98:
                        kotlin.Unit r3 = kotlin.Unit.INSTANCE
                        return r3
                    */
                    throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.flow.FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1.AnonymousClass2.emit(java.lang.Object, kotlin.coroutines.Continuation):java.lang.Object");
                }

                public AnonymousClass2(FlowCollector flowCollector, Ref.ObjectRef objectRef, FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1 flowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1) {
                    this.$this_unsafeFlow$inlined = flowCollector;
                    this.$previousKey$inlined = objectRef;
                    this.this$0 = flowKt__DistinctKt$distinctUntilChangedBy$$inlined$distinctUntilChangedBy$FlowKt__DistinctKt$1;
                }
            }

            @Override // kotlinx.coroutines.flow.Flow
            public Object collect(FlowCollector flowCollector, Continuation continuation) {
                Ref.ObjectRef objectRef = new Ref.ObjectRef();
                objectRef.element = (T) NullSurrogateKt.NULL;
                return Flow.this.collect(new AnonymousClass2(flowCollector, objectRef, this), continuation);
            }
        };
    }

    private static final <T, K> Flow<T> distinctUntilChangedBy$FlowKt__DistinctKt(Flow<? extends T> flow, Function1<? super T, ? extends K> function1, Function2<? super K, ? super K, Boolean> function2) {
        return new FlowKt__DistinctKt$distinctUntilChangedBy$$inlined$unsafeFlow$3(flow, function1, function2);
    }
}
