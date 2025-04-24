package kotlinx.coroutines.flow;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlinx.coroutines.channels.ReceiveChannel;
import kotlinx.coroutines.flow.internal.NullSurrogateKt;

/* compiled from: Delay.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u001a\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\b\u0003\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u00022\b\u0010\u0003\u001a\u0004\u0018\u00010\u0004H\u008a@¢\u0006\u0004\b\u0005\u0010\u0006¨\u0006\u0007"}, d2 = {"<anonymous>", "", "T", "it", "", "invoke", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "kotlinx/coroutines/flow/FlowKt__DelayKt$debounce$2$1$1"}, k = 3, mv = {1, 1, 15})
/* loaded from: classes.dex */
final class FlowKt__DelayKt$debounce$2$invokeSuspend$$inlined$select$lambda$1 extends SuspendLambda implements Function2<Object, Continuation<? super Unit>, Object> {
    final /* synthetic */ FlowCollector $downstream$inlined;
    final /* synthetic */ Ref.ObjectRef $lastValue$inlined;
    final /* synthetic */ ReceiveChannel $values$inlined;
    Object L$0;
    int label;
    private Object p$0;
    final /* synthetic */ FlowKt__DelayKt$debounce$2 this$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    FlowKt__DelayKt$debounce$2$invokeSuspend$$inlined$select$lambda$1(Continuation continuation, FlowKt__DelayKt$debounce$2 flowKt__DelayKt$debounce$2, ReceiveChannel receiveChannel, Ref.ObjectRef objectRef, FlowCollector flowCollector) {
        super(2, continuation);
        this.this$0 = flowKt__DelayKt$debounce$2;
        this.$values$inlined = receiveChannel;
        this.$lastValue$inlined = objectRef;
        this.$downstream$inlined = flowCollector;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> completion) {
        Intrinsics.checkParameterIsNotNull(completion, "completion");
        FlowKt__DelayKt$debounce$2$invokeSuspend$$inlined$select$lambda$1 flowKt__DelayKt$debounce$2$invokeSuspend$$inlined$select$lambda$1 = new FlowKt__DelayKt$debounce$2$invokeSuspend$$inlined$select$lambda$1(completion, this.this$0, this.$values$inlined, this.$lastValue$inlined, this.$downstream$inlined);
        flowKt__DelayKt$debounce$2$invokeSuspend$$inlined$select$lambda$1.p$0 = obj;
        return flowKt__DelayKt$debounce$2$invokeSuspend$$inlined$select$lambda$1;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(Object obj, Continuation<? super Unit> continuation) {
        return ((FlowKt__DelayKt$debounce$2$invokeSuspend$$inlined$select$lambda$1) create(obj, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [T, java.lang.Object] */
    /* JADX WARN: Type inference failed for: r2v2, types: [T, kotlinx.coroutines.internal.Symbol] */
    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object $result) {
        Object it;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        if (i == 0) {
            ResultKt.throwOnFailure($result);
            ?? r1 = this.p$0;
            if (r1 == 0) {
                if (this.$lastValue$inlined.element != 0) {
                    FlowCollector flowCollector = this.$downstream$inlined;
                    Object this_$iv = NullSurrogateKt.NULL;
                    Object value$iv = this.$lastValue$inlined.element;
                    Object obj = value$iv != this_$iv ? value$iv : null;
                    this.L$0 = r1;
                    this.label = 1;
                    if (flowCollector.emit(obj, this) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    it = r1;
                }
                this.$lastValue$inlined.element = NullSurrogateKt.DONE;
                return Unit.INSTANCE;
            }
            this.$lastValue$inlined.element = r1;
            return Unit.INSTANCE;
        }
        if (i != 1) {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        it = this.L$0;
        ResultKt.throwOnFailure($result);
        this.$lastValue$inlined.element = NullSurrogateKt.DONE;
        return Unit.INSTANCE;
    }
}
