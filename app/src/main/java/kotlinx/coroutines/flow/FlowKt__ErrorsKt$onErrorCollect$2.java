package kotlinx.coroutines.flow;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: Add missing generic type declarations: [T] */
/* compiled from: Errors.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u008a@¢\u0006\u0004\b\u0006\u0010\u0007"}, d2 = {"<anonymous>", "", "T", "Lkotlinx/coroutines/flow/FlowCollector;", "e", "", "invoke", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"}, k = 3, mv = {1, 1, 15})
@DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__ErrorsKt$onErrorCollect$2", f = "Errors.kt", i = {0, 0, 0, 0}, l = {230}, m = "invokeSuspend", n = {"$this$catch", "e", "$this$emitAll$iv", "flow$iv"}, s = {"L$0", "L$1", "L$2", "L$3"})
/* loaded from: classes.dex */
final class FlowKt__ErrorsKt$onErrorCollect$2<T> extends SuspendLambda implements Function3<FlowCollector<? super T>, Throwable, Continuation<? super Unit>, Object> {
    final /* synthetic */ Flow $fallback;
    final /* synthetic */ Function1 $predicate;
    Object L$0;
    Object L$1;
    Object L$2;
    Object L$3;
    int label;
    private FlowCollector p$;
    private Throwable p$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    FlowKt__ErrorsKt$onErrorCollect$2(Function1 function1, Flow flow, Continuation continuation) {
        super(3, continuation);
        this.$predicate = function1;
        this.$fallback = flow;
    }

    public final Continuation<Unit> create(FlowCollector<? super T> create, Throwable e, Continuation<? super Unit> continuation) {
        Intrinsics.checkParameterIsNotNull(create, "$this$create");
        Intrinsics.checkParameterIsNotNull(e, "e");
        Intrinsics.checkParameterIsNotNull(continuation, "continuation");
        FlowKt__ErrorsKt$onErrorCollect$2 flowKt__ErrorsKt$onErrorCollect$2 = new FlowKt__ErrorsKt$onErrorCollect$2(this.$predicate, this.$fallback, continuation);
        flowKt__ErrorsKt$onErrorCollect$2.p$ = create;
        flowKt__ErrorsKt$onErrorCollect$2.p$0 = e;
        return flowKt__ErrorsKt$onErrorCollect$2;
    }

    @Override // kotlin.jvm.functions.Function3
    public final Object invoke(Object obj, Throwable th, Continuation<? super Unit> continuation) {
        return ((FlowKt__ErrorsKt$onErrorCollect$2) create((FlowCollector) obj, th, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object $result) {
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        if (i == 0) {
            ResultKt.throwOnFailure($result);
            FlowCollector $this$catch = this.p$;
            Throwable e = this.p$0;
            if (!((Boolean) this.$predicate.invoke(e)).booleanValue()) {
                throw e;
            }
            Flow flow$iv = this.$fallback;
            this.L$0 = $this$catch;
            this.L$1 = e;
            this.L$2 = $this$catch;
            this.L$3 = flow$iv;
            this.label = 1;
            if (flow$iv.collect($this$catch, this) == coroutine_suspended) {
                return coroutine_suspended;
            }
        } else {
            if (i != 1) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            ResultKt.throwOnFailure($result);
        }
        return Unit.INSTANCE;
    }
}
