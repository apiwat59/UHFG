package kotlinx.coroutines.flow.internal;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.flow.FlowCollector;
import kotlinx.coroutines.internal.ScopeCoroutine;

/* compiled from: SafeCollector.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0000\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\u001b\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00000\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0002\u0010\u0006J\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u0005H\u0002J\u0019\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00028\u0000H\u0096@ø\u0001\u0000¢\u0006\u0002\u0010\u000fJ\u001b\u0010\u0010\u001a\u0004\u0018\u00010\u0011*\u0004\u0018\u00010\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0011H\u0082\u0010R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00028\u00000\u0002X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u0013"}, d2 = {"Lkotlinx/coroutines/flow/internal/SafeCollector;", "T", "Lkotlinx/coroutines/flow/FlowCollector;", "collector", "collectContext", "Lkotlin/coroutines/CoroutineContext;", "(Lkotlinx/coroutines/flow/FlowCollector;Lkotlin/coroutines/CoroutineContext;)V", "collectContextSize", "", "lastEmissionContext", "checkContext", "", "currentContext", "emit", "value", "(Ljava/lang/Object;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "transitiveCoroutineParent", "Lkotlinx/coroutines/Job;", "collectJob", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public final class SafeCollector<T> implements FlowCollector<T> {
    private final CoroutineContext collectContext;
    private final int collectContextSize;
    private final FlowCollector<T> collector;
    private CoroutineContext lastEmissionContext;

    /* JADX WARN: Multi-variable type inference failed */
    public SafeCollector(FlowCollector<? super T> collector, CoroutineContext collectContext) {
        Intrinsics.checkParameterIsNotNull(collector, "collector");
        Intrinsics.checkParameterIsNotNull(collectContext, "collectContext");
        this.collector = collector;
        this.collectContext = collectContext;
        this.collectContextSize = ((Number) collectContext.fold(0, new Function2<Integer, CoroutineContext.Element, Integer>() { // from class: kotlinx.coroutines.flow.internal.SafeCollector$collectContextSize$1
            @Override // kotlin.jvm.functions.Function2
            public /* bridge */ /* synthetic */ Integer invoke(Integer num, CoroutineContext.Element element) {
                return Integer.valueOf(invoke(num.intValue(), element));
            }

            public final int invoke(int count, CoroutineContext.Element $noName_1) {
                Intrinsics.checkParameterIsNotNull($noName_1, "<anonymous parameter 1>");
                return count + 1;
            }
        })).intValue();
    }

    @Override // kotlinx.coroutines.flow.FlowCollector
    public Object emit(T t, Continuation<? super Unit> continuation) {
        CoroutineContext currentContext = continuation.getContext();
        if (this.lastEmissionContext != currentContext) {
            checkContext(currentContext);
            this.lastEmissionContext = currentContext;
        }
        return this.collector.emit(t, continuation);
    }

    private final void checkContext(CoroutineContext currentContext) {
        int result = ((Number) currentContext.fold(0, new Function2<Integer, CoroutineContext.Element, Integer>() { // from class: kotlinx.coroutines.flow.internal.SafeCollector$checkContext$result$1
            {
                super(2);
            }

            @Override // kotlin.jvm.functions.Function2
            public /* bridge */ /* synthetic */ Integer invoke(Integer num, CoroutineContext.Element element) {
                return Integer.valueOf(invoke(num.intValue(), element));
            }

            public final int invoke(int count, CoroutineContext.Element element) {
                CoroutineContext coroutineContext;
                Job emissionParentJob;
                Intrinsics.checkParameterIsNotNull(element, "element");
                CoroutineContext.Key key = element.getKey();
                coroutineContext = SafeCollector.this.collectContext;
                CoroutineContext.Element collectElement = coroutineContext.get(key);
                if (key != Job.INSTANCE) {
                    if (element != collectElement) {
                        return Integer.MIN_VALUE;
                    }
                    return count + 1;
                }
                Job collectJob = (Job) collectElement;
                emissionParentJob = SafeCollector.this.transitiveCoroutineParent((Job) element, collectJob);
                if (emissionParentJob == collectJob) {
                    return collectJob == null ? count : count + 1;
                }
                throw new IllegalStateException(("Flow invariant is violated:\n\t\tEmission from another coroutine is detected.\n\t\tChild of " + emissionParentJob + ", expected child of " + collectJob + ".\n\t\tFlowCollector is not thread-safe and concurrent emissions are prohibited.\n\t\tTo mitigate this restriction please use 'channelFlow' builder instead of 'flow'").toString());
            }
        })).intValue();
        if (result != this.collectContextSize) {
            throw new IllegalStateException(("Flow invariant is violated:\n\t\tFlow was collected in " + this.collectContext + ",\n\t\tbut emission happened in " + currentContext + ".\n\t\tPlease refer to 'flow' documentation or use 'flowOn' instead").toString());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Job transitiveCoroutineParent(Job $this$transitiveCoroutineParent, Job collectJob) {
        while ($this$transitiveCoroutineParent != null) {
            if ($this$transitiveCoroutineParent == collectJob || !($this$transitiveCoroutineParent instanceof ScopeCoroutine)) {
                return $this$transitiveCoroutineParent;
            }
            $this$transitiveCoroutineParent = ((ScopeCoroutine) $this$transitiveCoroutineParent).getParent$kotlinx_coroutines_core();
        }
        return null;
    }
}
