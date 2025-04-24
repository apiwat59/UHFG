package kotlinx.coroutines.flow;

import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.channels.ProduceKt;
import kotlinx.coroutines.channels.ReceiveChannel;
import kotlinx.coroutines.flow.internal.FlowCoroutineKt;

/* compiled from: Delay.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a&\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007\u001a$\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006*\u00020\b2\u0006\u0010\t\u001a\u00020\u00042\b\b\u0002\u0010\n\u001a\u00020\u0004H\u0000\u001a&\u0010\u000b\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00012\u0006\u0010\f\u001a\u00020\u0004H\u0007¨\u0006\r"}, d2 = {"debounce", "Lkotlinx/coroutines/flow/Flow;", "T", "timeoutMillis", "", "fixedPeriodTicker", "Lkotlinx/coroutines/channels/ReceiveChannel;", "", "Lkotlinx/coroutines/CoroutineScope;", "delayMillis", "initialDelayMillis", "sample", "periodMillis", "kotlinx-coroutines-core"}, k = 5, mv = {1, 1, 15}, xs = "kotlinx/coroutines/flow/FlowKt")
/* loaded from: classes.dex */
final /* synthetic */ class FlowKt__DelayKt {
    public static final <T> Flow<T> debounce(Flow<? extends T> debounce, long timeoutMillis) {
        Intrinsics.checkParameterIsNotNull(debounce, "$this$debounce");
        if (timeoutMillis > 0) {
            return FlowCoroutineKt.scopedFlow(new FlowKt__DelayKt$debounce$2(debounce, timeoutMillis, null));
        }
        throw new IllegalArgumentException("Debounce timeout should be positive".toString());
    }

    public static final <T> Flow<T> sample(Flow<? extends T> sample, long periodMillis) {
        Intrinsics.checkParameterIsNotNull(sample, "$this$sample");
        if (periodMillis > 0) {
            return FlowCoroutineKt.scopedFlow(new FlowKt__DelayKt$sample$2(sample, periodMillis, null));
        }
        throw new IllegalArgumentException("Sample period should be positive".toString());
    }

    public static /* synthetic */ ReceiveChannel fixedPeriodTicker$default(CoroutineScope coroutineScope, long j, long j2, int i, Object obj) {
        if ((i & 2) != 0) {
            j2 = j;
        }
        return FlowKt.fixedPeriodTicker(coroutineScope, j, j2);
    }

    public static final ReceiveChannel<Unit> fixedPeriodTicker(CoroutineScope fixedPeriodTicker, long delayMillis, long initialDelayMillis) {
        Intrinsics.checkParameterIsNotNull(fixedPeriodTicker, "$this$fixedPeriodTicker");
        if (!(delayMillis >= 0)) {
            throw new IllegalArgumentException(("Expected non-negative delay, but has " + delayMillis + " ms").toString());
        }
        if (initialDelayMillis >= 0) {
            return ProduceKt.produce$default(fixedPeriodTicker, null, 0, new FlowKt__DelayKt$fixedPeriodTicker$3(initialDelayMillis, delayMillis, null), 1, null);
        }
        throw new IllegalArgumentException(("Expected non-negative initial delay, but has " + initialDelayMillis + " ms").toString());
    }
}
