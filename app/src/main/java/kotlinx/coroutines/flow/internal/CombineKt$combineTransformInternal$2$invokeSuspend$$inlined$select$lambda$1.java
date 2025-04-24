package kotlinx.coroutines.flow.internal;

import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlinx.coroutines.channels.ReceiveChannel;
import kotlinx.coroutines.flow.FlowCollector;

/* compiled from: Combine.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\"\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\b\u0004\n\u0002\b\u0004\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\b\u0003\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003\"\u0004\b\u0002\u0010\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u008a@¢\u0006\u0004\b\u0007\u0010\b¨\u0006\t"}, d2 = {"<anonymous>", "", "T1", "T2", "R", "value", "", "invoke", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", "kotlinx/coroutines/flow/internal/CombineKt$combineTransformInternal$2$1$2"}, k = 3, mv = {1, 1, 15})
/* loaded from: classes.dex */
final class CombineKt$combineTransformInternal$2$invokeSuspend$$inlined$select$lambda$1 extends SuspendLambda implements Function2<Object, Continuation<? super Unit>, Object> {
    final /* synthetic */ ReceiveChannel $firstChannel$inlined;
    final /* synthetic */ Ref.BooleanRef $firstIsClosed$inlined;
    final /* synthetic */ Ref.ObjectRef $firstValue$inlined;
    final /* synthetic */ ReceiveChannel $secondChannel$inlined;
    final /* synthetic */ Ref.BooleanRef $secondIsClosed$inlined;
    final /* synthetic */ Ref.ObjectRef $secondValue$inlined;
    Object L$0;
    int label;
    private Object p$0;
    final /* synthetic */ CombineKt$combineTransformInternal$2 this$0;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    CombineKt$combineTransformInternal$2$invokeSuspend$$inlined$select$lambda$1(Continuation continuation, CombineKt$combineTransformInternal$2 combineKt$combineTransformInternal$2, Ref.BooleanRef booleanRef, ReceiveChannel receiveChannel, Ref.ObjectRef objectRef, Ref.ObjectRef objectRef2, Ref.BooleanRef booleanRef2, ReceiveChannel receiveChannel2) {
        super(2, continuation);
        this.this$0 = combineKt$combineTransformInternal$2;
        this.$firstIsClosed$inlined = booleanRef;
        this.$firstChannel$inlined = receiveChannel;
        this.$firstValue$inlined = objectRef;
        this.$secondValue$inlined = objectRef2;
        this.$secondIsClosed$inlined = booleanRef2;
        this.$secondChannel$inlined = receiveChannel2;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> completion) {
        Intrinsics.checkParameterIsNotNull(completion, "completion");
        CombineKt$combineTransformInternal$2$invokeSuspend$$inlined$select$lambda$1 combineKt$combineTransformInternal$2$invokeSuspend$$inlined$select$lambda$1 = new CombineKt$combineTransformInternal$2$invokeSuspend$$inlined$select$lambda$1(completion, this.this$0, this.$firstIsClosed$inlined, this.$firstChannel$inlined, this.$firstValue$inlined, this.$secondValue$inlined, this.$secondIsClosed$inlined, this.$secondChannel$inlined);
        combineKt$combineTransformInternal$2$invokeSuspend$$inlined$select$lambda$1.p$0 = obj;
        return combineKt$combineTransformInternal$2$invokeSuspend$$inlined$select$lambda$1;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(Object obj, Continuation<? super Unit> continuation) {
        return ((CombineKt$combineTransformInternal$2$invokeSuspend$$inlined$select$lambda$1) create(obj, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Type inference failed for: r1v1, types: [T, java.lang.Object] */
    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object $result) {
        Object value;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        if (i == 0) {
            ResultKt.throwOnFailure($result);
            ?? r1 = this.p$0;
            this.$firstValue$inlined.element = r1;
            if (this.$secondValue$inlined.element != 0) {
                Function4 function4 = this.this$0.$transform;
                FlowCollector flowCollector = this.this$0.$this_combineTransformInternal;
                Object this_$iv = CombineKt.getNull();
                Object value$iv = this.$firstValue$inlined.element;
                if (value$iv == this_$iv) {
                    value$iv = null;
                }
                Object this_$iv2 = CombineKt.getNull();
                Object value$iv2 = this.$secondValue$inlined.element;
                Object obj = value$iv2 != this_$iv2 ? value$iv2 : null;
                this.L$0 = r1;
                this.label = 1;
                if (function4.invoke(flowCollector, value$iv, obj, this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
                value = r1;
            }
            Object value2 = Unit.INSTANCE;
            return value2;
        }
        if (i != 1) {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        value = this.L$0;
        ResultKt.throwOnFailure($result);
        Object value22 = Unit.INSTANCE;
        return value22;
    }
}
