package kotlinx.coroutines.internal;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.SequencesKt;
import kotlinx.coroutines.MainCoroutineDispatcher;

/* compiled from: MainDispatchers.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bÀ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\b\u0010\u0007\u001a\u00020\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\b"}, d2 = {"Lkotlinx/coroutines/internal/MainDispatcherLoader;", "", "()V", "FAST_SERVICE_LOADER_ENABLED", "", "dispatcher", "Lkotlinx/coroutines/MainCoroutineDispatcher;", "loadMainDispatcher", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public final class MainDispatcherLoader {
    private static final boolean FAST_SERVICE_LOADER_ENABLED;
    public static final MainDispatcherLoader INSTANCE;
    public static final MainCoroutineDispatcher dispatcher;

    static {
        MainDispatcherLoader mainDispatcherLoader = new MainDispatcherLoader();
        INSTANCE = mainDispatcherLoader;
        FAST_SERVICE_LOADER_ENABLED = SystemPropsKt.systemProp("kotlinx.coroutines.fast.service.loader", true);
        dispatcher = mainDispatcherLoader.loadMainDispatcher();
    }

    private MainDispatcherLoader() {
    }

    private final MainCoroutineDispatcher loadMainDispatcher() {
        List list;
        Object maxElem$iv;
        MainCoroutineDispatcher tryCreateDispatcher;
        try {
            if (FAST_SERVICE_LOADER_ENABLED) {
                FastServiceLoader fastServiceLoader = FastServiceLoader.INSTANCE;
                ClassLoader classLoader = MainDispatcherFactory.class.getClassLoader();
                Intrinsics.checkExpressionValueIsNotNull(classLoader, "clz.classLoader");
                list = fastServiceLoader.load$kotlinx_coroutines_core(MainDispatcherFactory.class, classLoader);
            } else {
                Iterator it = ServiceLoader.load(MainDispatcherFactory.class, MainDispatcherFactory.class.getClassLoader()).iterator();
                Intrinsics.checkExpressionValueIsNotNull(it, "ServiceLoader.load(\n    …             ).iterator()");
                list = SequencesKt.toList(SequencesKt.asSequence(it));
            }
            List factories = list;
            List $this$maxBy$iv = factories;
            Iterator iterator$iv = $this$maxBy$iv.iterator();
            if (iterator$iv.hasNext()) {
                maxElem$iv = iterator$iv.next();
                if (iterator$iv.hasNext()) {
                    MainDispatcherFactory it2 = (MainDispatcherFactory) maxElem$iv;
                    int maxValue$iv = it2.getLoadPriority();
                    do {
                        Object e$iv = iterator$iv.next();
                        MainDispatcherFactory it3 = (MainDispatcherFactory) e$iv;
                        int v$iv = it3.getLoadPriority();
                        if (maxValue$iv < v$iv) {
                            maxElem$iv = e$iv;
                            maxValue$iv = v$iv;
                        }
                    } while (iterator$iv.hasNext());
                }
            } else {
                maxElem$iv = null;
            }
            MainDispatcherFactory mainDispatcherFactory = (MainDispatcherFactory) maxElem$iv;
            return (mainDispatcherFactory == null || (tryCreateDispatcher = MainDispatchersKt.tryCreateDispatcher(mainDispatcherFactory, factories)) == null) ? new MissingMainCoroutineDispatcher(null, null, 2, null) : tryCreateDispatcher;
        } catch (Throwable e) {
            return new MissingMainCoroutineDispatcher(e, null, 2, null);
        }
    }
}
