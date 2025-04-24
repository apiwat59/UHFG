package kotlinx.coroutines;

import kotlin.Metadata;

/* compiled from: Await.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000*\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u001e\n\u0002\b\u0002\u001a=\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u00022\u001e\u0010\u0003\u001a\u0010\u0012\f\b\u0001\u0012\b\u0012\u0004\u0012\u0002H\u00020\u00050\u0004\"\b\u0012\u0004\u0012\u0002H\u00020\u0005H\u0086@ø\u0001\u0000¢\u0006\u0002\u0010\u0006\u001a%\u0010\u0007\u001a\u00020\b2\u0012\u0010\t\u001a\n\u0012\u0006\b\u0001\u0012\u00020\n0\u0004\"\u00020\nH\u0086@ø\u0001\u0000¢\u0006\u0002\u0010\u000b\u001a-\u0010\u0000\u001a\b\u0012\u0004\u0012\u0002H\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u00050\fH\u0086@ø\u0001\u0000¢\u0006\u0002\u0010\r\u001a\u001b\u0010\u0007\u001a\u00020\b*\b\u0012\u0004\u0012\u00020\n0\fH\u0086@ø\u0001\u0000¢\u0006\u0002\u0010\r\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\u000e"}, d2 = {"awaitAll", "", "T", "deferreds", "", "Lkotlinx/coroutines/Deferred;", "([Lkotlinx/coroutines/Deferred;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "joinAll", "", "jobs", "Lkotlinx/coroutines/Job;", "([Lkotlinx/coroutines/Job;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "", "(Ljava/util/Collection;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "kotlinx-coroutines-core"}, k = 2, mv = {1, 1, 15})
/* loaded from: classes.dex */
public final class AwaitKt {
    /* JADX WARN: Removed duplicated region for block: B:15:0x0038  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0024  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final <T> java.lang.Object awaitAll(kotlinx.coroutines.Deferred<? extends T>[] r5, kotlin.coroutines.Continuation<? super java.util.List<? extends T>> r6) {
        /*
            boolean r0 = r6 instanceof kotlinx.coroutines.AwaitKt$awaitAll$1
            if (r0 == 0) goto L14
            r0 = r6
            kotlinx.coroutines.AwaitKt$awaitAll$1 r0 = (kotlinx.coroutines.AwaitKt$awaitAll$1) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 & r2
            if (r1 == 0) goto L14
            int r1 = r0.label
            int r1 = r1 - r2
            r0.label = r1
            goto L19
        L14:
            kotlinx.coroutines.AwaitKt$awaitAll$1 r0 = new kotlinx.coroutines.AwaitKt$awaitAll$1
            r0.<init>(r6)
        L19:
            java.lang.Object r1 = r0.result
            java.lang.Object r2 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r3 = r0.label
            r4 = 1
            if (r3 == 0) goto L38
            if (r3 != r4) goto L30
            java.lang.Object r2 = r0.L$0
            r5 = r2
            kotlinx.coroutines.Deferred[] r5 = (kotlinx.coroutines.Deferred[]) r5
            kotlin.ResultKt.throwOnFailure(r1)
            r3 = r1
            goto L58
        L30:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "call to 'resume' before 'invoke' with coroutine"
            r0.<init>(r1)
            throw r0
        L38:
            kotlin.ResultKt.throwOnFailure(r1)
            int r3 = r5.length
            if (r3 != 0) goto L40
            r3 = 1
            goto L41
        L40:
            r3 = 0
        L41:
            if (r3 == 0) goto L48
            java.util.List r2 = kotlin.collections.CollectionsKt.emptyList()
            goto L5b
        L48:
            kotlinx.coroutines.AwaitAll r3 = new kotlinx.coroutines.AwaitAll
            r3.<init>(r5)
            r0.L$0 = r5
            r0.label = r4
            java.lang.Object r3 = r3.await(r0)
            if (r3 != r2) goto L58
            return r2
        L58:
            r2 = r3
            java.util.List r2 = (java.util.List) r2
        L5b:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.AwaitKt.awaitAll(kotlinx.coroutines.Deferred[], kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0038  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0024  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final <T> java.lang.Object awaitAll(java.util.Collection<? extends kotlinx.coroutines.Deferred<? extends T>> r8, kotlin.coroutines.Continuation<? super java.util.List<? extends T>> r9) {
        /*
            boolean r0 = r9 instanceof kotlinx.coroutines.AwaitKt$awaitAll$2
            if (r0 == 0) goto L14
            r0 = r9
            kotlinx.coroutines.AwaitKt$awaitAll$2 r0 = (kotlinx.coroutines.AwaitKt$awaitAll$2) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 & r2
            if (r1 == 0) goto L14
            int r1 = r0.label
            int r1 = r1 - r2
            r0.label = r1
            goto L19
        L14:
            kotlinx.coroutines.AwaitKt$awaitAll$2 r0 = new kotlinx.coroutines.AwaitKt$awaitAll$2
            r0.<init>(r9)
        L19:
            java.lang.Object r1 = r0.result
            java.lang.Object r2 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r3 = r0.label
            r4 = 1
            if (r3 == 0) goto L38
            if (r3 != r4) goto L30
            java.lang.Object r2 = r0.L$0
            r8 = r2
            java.util.Collection r8 = (java.util.Collection) r8
            kotlin.ResultKt.throwOnFailure(r1)
            r3 = r1
            goto L64
        L30:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "call to 'resume' before 'invoke' with coroutine"
            r0.<init>(r1)
            throw r0
        L38:
            kotlin.ResultKt.throwOnFailure(r1)
            boolean r3 = r8.isEmpty()
            if (r3 == 0) goto L46
            java.util.List r2 = kotlin.collections.CollectionsKt.emptyList()
            goto L67
        L46:
            r3 = r8
            r5 = 0
            r6 = r3
            r7 = 0
            kotlinx.coroutines.Deferred[] r7 = new kotlinx.coroutines.Deferred[r7]
            java.lang.Object[] r7 = r6.toArray(r7)
            if (r7 == 0) goto L68
            kotlinx.coroutines.Deferred[] r7 = (kotlinx.coroutines.Deferred[]) r7
            kotlinx.coroutines.AwaitAll r3 = new kotlinx.coroutines.AwaitAll
            r3.<init>(r7)
            r0.L$0 = r8
            r0.label = r4
            java.lang.Object r3 = r3.await(r0)
            if (r3 != r2) goto L64
            return r2
        L64:
            r2 = r3
            java.util.List r2 = (java.util.List) r2
        L67:
            return r2
        L68:
            kotlin.TypeCastException r2 = new kotlin.TypeCastException
            java.lang.String r4 = "null cannot be cast to non-null type kotlin.Array<T>"
            r2.<init>(r4)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.AwaitKt.awaitAll(java.util.Collection, kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x005d  */
    /* JADX WARN: Removed duplicated region for block: B:17:0x0087  */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0053  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0025  */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:16:0x0078 -> B:10:0x007f). Please report as a decompilation issue!!! */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final java.lang.Object joinAll(kotlinx.coroutines.Job[] r14, kotlin.coroutines.Continuation<? super kotlin.Unit> r15) {
        /*
            boolean r0 = r15 instanceof kotlinx.coroutines.AwaitKt$joinAll$1
            if (r0 == 0) goto L14
            r0 = r15
            kotlinx.coroutines.AwaitKt$joinAll$1 r0 = (kotlinx.coroutines.AwaitKt$joinAll$1) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 & r2
            if (r1 == 0) goto L14
            int r1 = r0.label
            int r1 = r1 - r2
            r0.label = r1
            goto L19
        L14:
            kotlinx.coroutines.AwaitKt$joinAll$1 r0 = new kotlinx.coroutines.AwaitKt$joinAll$1
            r0.<init>(r15)
        L19:
            java.lang.Object r1 = r0.result
            java.lang.Object r2 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r3 = r0.label
            r4 = 1
            r5 = 0
            if (r3 == 0) goto L53
            if (r3 != r4) goto L4b
            r3 = 0
            r6 = r3
            r7 = r3
            r8 = r5
            java.lang.Object r9 = r0.L$4
            r7 = r9
            kotlinx.coroutines.Job r7 = (kotlinx.coroutines.Job) r7
            java.lang.Object r9 = r0.L$3
            r3 = r9
            kotlinx.coroutines.Job r3 = (kotlinx.coroutines.Job) r3
            int r9 = r0.I$1
            int r10 = r0.I$0
            java.lang.Object r11 = r0.L$2
            kotlinx.coroutines.Job[] r11 = (kotlinx.coroutines.Job[]) r11
            java.lang.Object r12 = r0.L$1
            r6 = r12
            kotlinx.coroutines.Job[] r6 = (kotlinx.coroutines.Job[]) r6
            java.lang.Object r12 = r0.L$0
            r14 = r12
            kotlinx.coroutines.Job[] r14 = (kotlinx.coroutines.Job[]) r14
            kotlin.ResultKt.throwOnFailure(r1)
            goto L7f
        L4b:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "call to 'resume' before 'invoke' with coroutine"
            r0.<init>(r1)
            throw r0
        L53:
            kotlin.ResultKt.throwOnFailure(r1)
            r3 = r14
            r6 = 0
            int r7 = r3.length
            r11 = r3
            r10 = r7
        L5b:
            if (r5 >= r10) goto L87
            r7 = r11[r5]
            r8 = r7
            r9 = 0
            r0.L$0 = r14
            r0.L$1 = r3
            r0.L$2 = r11
            r0.I$0 = r10
            r0.I$1 = r5
            r0.L$3 = r7
            r0.L$4 = r8
            r0.label = r4
            java.lang.Object r12 = r8.join(r0)
            if (r12 != r2) goto L78
            return r2
        L78:
            r13 = r6
            r6 = r3
            r3 = r7
            r7 = r8
            r8 = r9
            r9 = r5
            r5 = r13
        L7f:
            int r3 = r9 + 1
            r13 = r5
            r5 = r3
            r3 = r6
            r6 = r13
            goto L5b
        L87:
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.AwaitKt.joinAll(kotlinx.coroutines.Job[], kotlin.coroutines.Continuation):java.lang.Object");
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0060  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x004c  */
    /* JADX WARN: Removed duplicated region for block: B:8:0x0024  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static final java.lang.Object joinAll(java.util.Collection<? extends kotlinx.coroutines.Job> r11, kotlin.coroutines.Continuation<? super kotlin.Unit> r12) {
        /*
            boolean r0 = r12 instanceof kotlinx.coroutines.AwaitKt$joinAll$3
            if (r0 == 0) goto L14
            r0 = r12
            kotlinx.coroutines.AwaitKt$joinAll$3 r0 = (kotlinx.coroutines.AwaitKt$joinAll$3) r0
            int r1 = r0.label
            r2 = -2147483648(0xffffffff80000000, float:-0.0)
            r1 = r1 & r2
            if (r1 == 0) goto L14
            int r1 = r0.label
            int r1 = r1 - r2
            r0.label = r1
            goto L19
        L14:
            kotlinx.coroutines.AwaitKt$joinAll$3 r0 = new kotlinx.coroutines.AwaitKt$joinAll$3
            r0.<init>(r12)
        L19:
            java.lang.Object r1 = r0.result
            java.lang.Object r2 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
            int r3 = r0.label
            r4 = 1
            if (r3 == 0) goto L4c
            if (r3 != r4) goto L44
            r3 = 0
            r5 = r3
            r6 = r3
            r7 = 0
            r8 = r7
            java.lang.Object r9 = r0.L$4
            r6 = r9
            kotlinx.coroutines.Job r6 = (kotlinx.coroutines.Job) r6
            java.lang.Object r3 = r0.L$3
            java.lang.Object r9 = r0.L$2
            java.util.Iterator r9 = (java.util.Iterator) r9
            java.lang.Object r10 = r0.L$1
            r5 = r10
            java.lang.Iterable r5 = (java.lang.Iterable) r5
            java.lang.Object r10 = r0.L$0
            r11 = r10
            java.util.Collection r11 = (java.util.Collection) r11
            kotlin.ResultKt.throwOnFailure(r1)
            goto L7b
        L44:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "call to 'resume' before 'invoke' with coroutine"
            r0.<init>(r1)
            throw r0
        L4c:
            kotlin.ResultKt.throwOnFailure(r1)
            r3 = r11
            java.lang.Iterable r3 = (java.lang.Iterable) r3
            r5 = 0
            java.util.Iterator r6 = r3.iterator()
            r8 = r5
            r9 = r6
            r5 = r3
        L5a:
            boolean r3 = r9.hasNext()
            if (r3 == 0) goto L7d
            java.lang.Object r3 = r9.next()
            r6 = r3
            kotlinx.coroutines.Job r6 = (kotlinx.coroutines.Job) r6
            r7 = 0
            r0.L$0 = r11
            r0.L$1 = r5
            r0.L$2 = r9
            r0.L$3 = r3
            r0.L$4 = r6
            r0.label = r4
            java.lang.Object r10 = r6.join(r0)
            if (r10 != r2) goto L7b
            return r2
        L7b:
            goto L5a
        L7d:
            kotlin.Unit r2 = kotlin.Unit.INSTANCE
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlinx.coroutines.AwaitKt.joinAll(java.util.Collection, kotlin.coroutines.Continuation):java.lang.Object");
    }
}
