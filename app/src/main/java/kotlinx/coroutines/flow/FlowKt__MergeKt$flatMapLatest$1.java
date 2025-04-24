package kotlinx.coroutines.flow;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: Add missing generic type declarations: [R, T] */
/* compiled from: Merge.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003*\b\u0012\u0004\u0012\u0002H\u00030\u00042\u0006\u0010\u0005\u001a\u0002H\u0002H\u008a@¢\u0006\u0004\b\u0006\u0010\u0007"}, d2 = {"<anonymous>", "", "T", "R", "Lkotlinx/coroutines/flow/FlowCollector;", "it", "invoke", "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"}, k = 3, mv = {1, 1, 15})
@DebugMetadata(c = "kotlinx.coroutines.flow.FlowKt__MergeKt$flatMapLatest$1", f = "Merge.kt", i = {0, 0, 1, 1, 1, 1}, l = {154, 180}, m = "invokeSuspend", n = {"$this$transformLatest", "it", "$this$transformLatest", "it", "$this$emitAll$iv", "flow$iv"}, s = {"L$0", "L$1", "L$0", "L$1", "L$2", "L$3"})
/* loaded from: classes.dex */
public final class FlowKt__MergeKt$flatMapLatest$1<R, T> extends SuspendLambda implements Function3<FlowCollector<? super R>, T, Continuation<? super Unit>, Object> {
    final /* synthetic */ Function2 $transform;
    Object L$0;
    Object L$1;
    Object L$2;
    Object L$3;
    int label;
    private FlowCollector p$;
    private Object p$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public FlowKt__MergeKt$flatMapLatest$1(Function2 function2, Continuation continuation) {
        super(3, continuation);
        this.$transform = function2;
    }

    public final Continuation<Unit> create(FlowCollector<? super R> create, T t, Continuation<? super Unit> continuation) {
        Intrinsics.checkParameterIsNotNull(create, "$this$create");
        Intrinsics.checkParameterIsNotNull(continuation, "continuation");
        FlowKt__MergeKt$flatMapLatest$1 flowKt__MergeKt$flatMapLatest$1 = new FlowKt__MergeKt$flatMapLatest$1(this.$transform, continuation);
        flowKt__MergeKt$flatMapLatest$1.p$ = create;
        flowKt__MergeKt$flatMapLatest$1.p$0 = t;
        return flowKt__MergeKt$flatMapLatest$1;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // kotlin.jvm.functions.Function3
    public final Object invoke(Object obj, Object obj2, Continuation<? super Unit> continuation) {
        return ((FlowKt__MergeKt$flatMapLatest$1) create((FlowCollector) obj, obj2, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object $result) {
        FlowCollector $this$emitAll$iv;
        Object obj;
        Object it;
        FlowCollector $this$transformLatest;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        if (i == 0) {
            ResultKt.throwOnFailure($result);
            FlowCollector $this$transformLatest2 = this.p$;
            Object it2 = this.p$0;
            Function2 function2 = this.$transform;
            this.L$0 = $this$transformLatest2;
            this.L$1 = it2;
            this.L$2 = $this$transformLatest2;
            this.label = 1;
            Object invoke = function2.invoke(it2, this);
            if (invoke == coroutine_suspended) {
                return coroutine_suspended;
            }
            $this$emitAll$iv = $this$transformLatest2;
            obj = invoke;
            it = it2;
            $this$transformLatest = $this$emitAll$iv;
        } else {
            if (i != 1) {
                if (i != 2) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                Object it3 = this.L$1;
                ResultKt.throwOnFailure($result);
            }
            FlowCollector flowCollector = (FlowCollector) this.L$2;
            it = this.L$1;
            $this$transformLatest = (FlowCollector) this.L$0;
            ResultKt.throwOnFailure($result);
            $this$emitAll$iv = flowCollector;
            obj = $result;
        }
        Flow flow$iv = (Flow) obj;
        this.L$0 = $this$transformLatest;
        this.L$1 = it;
        this.L$2 = $this$emitAll$iv;
        this.L$3 = flow$iv;
        this.label = 2;
        return flow$iv.collect($this$emitAll$iv, this) == coroutine_suspended ? coroutine_suspended : Unit.INSTANCE;
    }

    public final Object invokeSuspend$$forInline(Object $result) {
        FlowCollector $this$transformLatest = this.p$;
        Object it = this.p$0;
        Flow flow$iv = (Flow) this.$transform.invoke(it, this);
        InlineMarker.mark(0);
        flow$iv.collect($this$transformLatest, this);
        InlineMarker.mark(2);
        InlineMarker.mark(1);
        return Unit.INSTANCE;
    }
}
