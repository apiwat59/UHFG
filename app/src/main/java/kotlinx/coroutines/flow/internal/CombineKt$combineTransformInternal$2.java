package kotlinx.coroutines.flow.internal;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowCollector;
import org.apache.log4j.net.SyslogAppender;

/* compiled from: Combine.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0012\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002\"\u0004\b\u0001\u0010\u0003\"\u0004\b\u0002\u0010\u0004*\u00020\u0005H\u008a@¢\u0006\u0004\b\u0006\u0010\u0007"}, d2 = {"<anonymous>", "", "T1", "T2", "R", "Lkotlinx/coroutines/CoroutineScope;", "invoke", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"}, k = 3, mv = {1, 1, 15})
@DebugMetadata(c = "kotlinx.coroutines.flow.internal.CombineKt$combineTransformInternal$2", f = "Combine.kt", i = {0, 0, 0, 0, 0, 0, 0}, l = {SyslogAppender.LOG_LOCAL2}, m = "invokeSuspend", n = {"$this$coroutineScope", "firstChannel", "secondChannel", "firstValue", "secondValue", "firstIsClosed", "secondIsClosed"}, s = {"L$0", "L$1", "L$2", "L$3", "L$4", "L$5", "L$6"})
/* loaded from: classes.dex */
final class CombineKt$combineTransformInternal$2 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
    final /* synthetic */ Flow $first;
    final /* synthetic */ Flow $second;
    final /* synthetic */ FlowCollector $this_combineTransformInternal;
    final /* synthetic */ Function4 $transform;
    Object L$0;
    Object L$1;
    Object L$2;
    Object L$3;
    Object L$4;
    Object L$5;
    Object L$6;
    Object L$7;
    int label;
    private CoroutineScope p$;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    CombineKt$combineTransformInternal$2(FlowCollector flowCollector, Flow flow, Flow flow2, Function4 function4, Continuation continuation) {
        super(2, continuation);
        this.$this_combineTransformInternal = flowCollector;
        this.$first = flow;
        this.$second = flow2;
        this.$transform = function4;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> completion) {
        Intrinsics.checkParameterIsNotNull(completion, "completion");
        CombineKt$combineTransformInternal$2 combineKt$combineTransformInternal$2 = new CombineKt$combineTransformInternal$2(this.$this_combineTransformInternal, this.$first, this.$second, this.$transform, completion);
        combineKt$combineTransformInternal$2.p$ = (CoroutineScope) obj;
        return combineKt$combineTransformInternal$2;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
        return ((CombineKt$combineTransformInternal$2) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Can't wrap try/catch for region: R(14:13|(1:14)|15|16|17|18|(1:20)(4:42|43|44|45)|21|22|23|(1:25)(4:32|33|34|35)|26|(1:28)|(1:30)(18:31|5|6|(1:8)|13|14|15|16|17|18|(0)(0)|21|22|23|(0)(0)|26|(0)|(0)(0))) */
    /* JADX WARN: Can't wrap try/catch for region: R(14:13|14|15|16|17|18|(1:20)(4:42|43|44|45)|21|22|23|(1:25)(4:32|33|34|35)|26|(1:28)|(1:30)(18:31|5|6|(1:8)|13|14|15|16|17|18|(0)(0)|21|22|23|(0)(0)|26|(0)|(0)(0))) */
    /* JADX WARN: Can't wrap try/catch for region: R(4:32|33|34|35) */
    /* JADX WARN: Can't wrap try/catch for region: R(4:42|43|44|45) */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0178, code lost:
    
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x019a, code lost:
    
        r3.handleBuilderException(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:40:0x017a, code lost:
    
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:41:0x017b, code lost:
    
        r29 = r35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x017e, code lost:
    
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x017f, code lost:
    
        r29 = r35;
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0182, code lost:
    
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0183, code lost:
    
        r29 = r35;
        r31 = r15;
     */
    /* JADX WARN: Removed duplicated region for block: B:20:0x00f2  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x0148  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x01a8  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x01b1 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:31:0x01b2  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x014b A[Catch: all -> 0x017a, TRY_LEAVE, TryCatch #3 {all -> 0x017a, blocks: (B:23:0x0122, B:32:0x014b), top: B:22:0x0122 }] */
    /* JADX WARN: Removed duplicated region for block: B:42:0x00f5 A[Catch: all -> 0x0182, TRY_LEAVE, TryCatch #0 {all -> 0x0182, blocks: (B:18:0x00e2, B:42:0x00f5), top: B:17:0x00e2 }] */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0091  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:31:0x01b2 -> B:5:0x01c0). Please report as a decompilation issue!!! */
    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.Object invokeSuspend(java.lang.Object r35) {
        /*
            Method dump skipped, instructions count: 452
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.flow.internal.CombineKt$combineTransformInternal$2.invokeSuspend(java.lang.Object):java.lang.Object");
    }
}
