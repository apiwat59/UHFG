package kotlinx.coroutines.channels;

import com.android.usbserial.driver.UsbId;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.IndexedValue;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: Add missing generic type declarations: [E] */
/* compiled from: Channels.common.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u0014\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u00040\u0003H\u008a@¢\u0006\u0004\b\u0005\u0010\u0006"}, d2 = {"<anonymous>", "", "E", "Lkotlinx/coroutines/channels/ProducerScope;", "Lkotlin/collections/IndexedValue;", "invoke", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"}, k = 3, mv = {1, 1, 15})
@DebugMetadata(c = "kotlinx.coroutines.channels.ChannelsKt__Channels_commonKt$withIndex$1", f = "Channels.common.kt", i = {0, 0, 1, 1, 1}, l = {1658, UsbId.VENDOR_PROLIFIC}, m = "invokeSuspend", n = {"$this$produce", "index", "$this$produce", "index", "e"}, s = {"L$0", "I$0", "L$0", "I$0", "L$1"})
/* loaded from: classes.dex */
final class ChannelsKt__Channels_commonKt$withIndex$1<E> extends SuspendLambda implements Function2<ProducerScope<? super IndexedValue<? extends E>>, Continuation<? super Unit>, Object> {
    final /* synthetic */ ReceiveChannel $this_withIndex;
    int I$0;
    Object L$0;
    Object L$1;
    Object L$2;
    int label;
    private ProducerScope p$;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    ChannelsKt__Channels_commonKt$withIndex$1(ReceiveChannel receiveChannel, Continuation continuation) {
        super(2, continuation);
        this.$this_withIndex = receiveChannel;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> completion) {
        Intrinsics.checkParameterIsNotNull(completion, "completion");
        ChannelsKt__Channels_commonKt$withIndex$1 channelsKt__Channels_commonKt$withIndex$1 = new ChannelsKt__Channels_commonKt$withIndex$1(this.$this_withIndex, completion);
        channelsKt__Channels_commonKt$withIndex$1.p$ = (ProducerScope) obj;
        return channelsKt__Channels_commonKt$withIndex$1;
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(Object obj, Continuation<? super Unit> continuation) {
        return ((ChannelsKt__Channels_commonKt$withIndex$1) create(obj, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x005e A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:12:0x005f  */
    /* JADX WARN: Removed duplicated region for block: B:15:0x006e  */
    /* JADX WARN: Removed duplicated region for block: B:19:0x0097  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:18:0x008a -> B:7:0x0093). Please report as a decompilation issue!!! */
    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final java.lang.Object invokeSuspend(java.lang.Object r13) {
        /*
            r12 = this;
            java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r1 = r12.label
            r2 = 2
            r3 = 1
            if (r1 == 0) goto L43
            r4 = 0
            r5 = 0
            if (r1 == r3) goto L2c
            if (r1 != r2) goto L24
            r1 = r4
            java.lang.Object r6 = r12.L$2
            kotlinx.coroutines.channels.ChannelIterator r6 = (kotlinx.coroutines.channels.ChannelIterator) r6
            java.lang.Object r4 = r12.L$1
            int r5 = r12.I$0
            java.lang.Object r7 = r12.L$0
            r1 = r7
            kotlinx.coroutines.channels.ProducerScope r1 = (kotlinx.coroutines.channels.ProducerScope) r1
            kotlin.ResultKt.throwOnFailure(r13)
            r7 = r12
            goto L93
        L24:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "call to 'resume' before 'invoke' with coroutine"
            r0.<init>(r1)
            throw r0
        L2c:
            r1 = r4
            r4 = r5
            java.lang.Object r5 = r12.L$1
            kotlinx.coroutines.channels.ChannelIterator r5 = (kotlinx.coroutines.channels.ChannelIterator) r5
            int r4 = r12.I$0
            java.lang.Object r6 = r12.L$0
            r1 = r6
            kotlinx.coroutines.channels.ProducerScope r1 = (kotlinx.coroutines.channels.ProducerScope) r1
            kotlin.ResultKt.throwOnFailure(r13)
            r6 = r12
            r7 = r5
            r5 = r4
            r4 = r1
            r1 = r0
            r0 = r13
            goto L66
        L43:
            kotlin.ResultKt.throwOnFailure(r13)
            kotlinx.coroutines.channels.ProducerScope r1 = r12.p$
            r4 = 0
            kotlinx.coroutines.channels.ReceiveChannel r5 = r12.$this_withIndex
            kotlinx.coroutines.channels.ChannelIterator r5 = r5.iterator()
            r6 = r12
        L50:
            r6.L$0 = r1
            r6.I$0 = r4
            r6.L$1 = r5
            r6.label = r3
            java.lang.Object r7 = r5.hasNext(r6)
            if (r7 != r0) goto L5f
            return r0
        L5f:
            r10 = r0
            r0 = r13
            r13 = r7
            r7 = r5
            r5 = r4
            r4 = r1
            r1 = r10
        L66:
            java.lang.Boolean r13 = (java.lang.Boolean) r13
            boolean r13 = r13.booleanValue()
            if (r13 == 0) goto L97
            java.lang.Object r13 = r7.next()
            kotlin.collections.IndexedValue r8 = new kotlin.collections.IndexedValue
            int r9 = r5 + 1
            r8.<init>(r5, r13)
            r6.L$0 = r4
            r6.I$0 = r9
            r6.L$1 = r13
            r6.L$2 = r7
            r6.label = r2
            java.lang.Object r5 = r4.send(r8, r6)
            if (r5 != r1) goto L8a
            return r1
        L8a:
            r5 = r9
            r10 = r4
            r4 = r13
            r13 = r0
            r0 = r1
            r1 = r10
            r11 = r7
            r7 = r6
            r6 = r11
        L93:
            r4 = r5
            r5 = r6
            r6 = r7
            goto L50
        L97:
            kotlin.Unit r13 = kotlin.Unit.INSTANCE
            return r13
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.channels.ChannelsKt__Channels_commonKt$withIndex$1.invokeSuspend(java.lang.Object):java.lang.Object");
    }
}
