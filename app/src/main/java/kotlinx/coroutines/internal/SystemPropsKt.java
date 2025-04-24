package kotlinx.coroutines.internal;

import kotlin.Metadata;

@Metadata(bv = {1, 0, 3}, d1 = {"kotlinx/coroutines/internal/SystemPropsKt__SystemPropsKt", "kotlinx/coroutines/internal/SystemPropsKt__SystemProps_commonKt"}, k = 4, mv = {1, 1, 15})
/* loaded from: classes.dex */
public final class SystemPropsKt {
    public static final int getAVAILABLE_PROCESSORS() {
        return SystemPropsKt__SystemPropsKt.getAVAILABLE_PROCESSORS();
    }

    public static final int systemProp(String propertyName, int defaultValue, int minValue, int maxValue) {
        return SystemPropsKt__SystemProps_commonKt.systemProp(propertyName, defaultValue, minValue, maxValue);
    }

    public static final long systemProp(String propertyName, long defaultValue, long minValue, long maxValue) {
        return SystemPropsKt__SystemProps_commonKt.systemProp(propertyName, defaultValue, minValue, maxValue);
    }

    public static final String systemProp(String propertyName) {
        return SystemPropsKt__SystemPropsKt.systemProp(propertyName);
    }

    public static final boolean systemProp(String propertyName, boolean defaultValue) {
        return SystemPropsKt__SystemProps_commonKt.systemProp(propertyName, defaultValue);
    }

    public static /* synthetic */ long systemProp$default(String str, long j, long j2, long j3, int i, Object obj) {
        long systemProp;
        systemProp = systemProp(str, j, (r14 & 4) != 0 ? 1L : j2, (r14 & 8) != 0 ? Long.MAX_VALUE : j3);
        return systemProp;
    }
}
