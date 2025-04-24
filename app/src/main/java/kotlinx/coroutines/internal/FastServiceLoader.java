package kotlinx.coroutines.internal;

import androidx.core.app.NotificationCompat;
import com.pda.uhf_g.util.ExcelUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.InlineMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* compiled from: FastServiceLoader.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bÀ\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J1\u0010\u0005\u001a\u0002H\u0006\"\u0004\b\u0000\u0010\u00062\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u0002H\u00060\u000bH\u0002¢\u0006\u0002\u0010\fJ/\u0010\r\u001a\b\u0012\u0004\u0012\u0002H\u00060\u000e\"\u0004\b\u0000\u0010\u00062\f\u0010\n\u001a\b\u0012\u0004\u0012\u0002H\u00060\u000b2\u0006\u0010\b\u001a\u00020\tH\u0000¢\u0006\u0002\b\u000fJ/\u0010\u0010\u001a\b\u0012\u0004\u0012\u0002H\u00060\u000e\"\u0004\b\u0000\u0010\u00062\f\u0010\n\u001a\b\u0012\u0004\u0012\u0002H\u00060\u000b2\u0006\u0010\b\u001a\u00020\tH\u0000¢\u0006\u0002\b\u0011J\u0016\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e2\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0016\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00040\u000e2\u0006\u0010\u0016\u001a\u00020\u0017H\u0002J,\u0010\u0018\u001a\u0002H\u0019\"\u0004\b\u0000\u0010\u0019*\u00020\u001a2\u0012\u0010\u001b\u001a\u000e\u0012\u0004\u0012\u00020\u001a\u0012\u0004\u0012\u0002H\u00190\u001cH\u0082\b¢\u0006\u0002\u0010\u001dR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u001e"}, d2 = {"Lkotlinx/coroutines/internal/FastServiceLoader;", "", "()V", "PREFIX", "", "getProviderInstance", "S", "name", "loader", "Ljava/lang/ClassLoader;", NotificationCompat.CATEGORY_SERVICE, "Ljava/lang/Class;", "(Ljava/lang/String;Ljava/lang/ClassLoader;Ljava/lang/Class;)Ljava/lang/Object;", "load", "", "load$kotlinx_coroutines_core", "loadProviders", "loadProviders$kotlinx_coroutines_core", "parse", "url", "Ljava/net/URL;", "parseFile", "r", "Ljava/io/BufferedReader;", "use", "R", "Ljava/util/jar/JarFile;", "block", "Lkotlin/Function1;", "(Ljava/util/jar/JarFile;Lkotlin/jvm/functions/Function1;)Ljava/lang/Object;", "kotlinx-coroutines-core"}, k = 1, mv = {1, 1, 15})
/* loaded from: classes.dex */
public final class FastServiceLoader {
    public static final FastServiceLoader INSTANCE = new FastServiceLoader();
    private static final String PREFIX = "META-INF/services/";

    private FastServiceLoader() {
    }

    public final <S> List<S> load$kotlinx_coroutines_core(Class<S> service, ClassLoader loader) {
        Intrinsics.checkParameterIsNotNull(service, "service");
        Intrinsics.checkParameterIsNotNull(loader, "loader");
        try {
            return loadProviders$kotlinx_coroutines_core(service, loader);
        } catch (Throwable th) {
            ServiceLoader load = ServiceLoader.load(service, loader);
            Intrinsics.checkExpressionValueIsNotNull(load, "ServiceLoader.load(service, loader)");
            return CollectionsKt.toList(load);
        }
    }

    public final <S> List<S> loadProviders$kotlinx_coroutines_core(Class<S> service, ClassLoader loader) {
        Intrinsics.checkParameterIsNotNull(service, "service");
        Intrinsics.checkParameterIsNotNull(loader, "loader");
        String fullServiceName = PREFIX + service.getName();
        Enumeration urls = loader.getResources(fullServiceName);
        Intrinsics.checkExpressionValueIsNotNull(urls, "urls");
        Iterable list = Collections.list(urls);
        Intrinsics.checkExpressionValueIsNotNull(list, "java.util.Collections.list(this)");
        Iterable $this$flatMap$iv = (List) list;
        Collection destination$iv$iv = new ArrayList();
        for (Object element$iv$iv : $this$flatMap$iv) {
            URL it = (URL) element$iv$iv;
            FastServiceLoader fastServiceLoader = INSTANCE;
            Intrinsics.checkExpressionValueIsNotNull(it, "it");
            Iterable list$iv$iv = fastServiceLoader.parse(it);
            CollectionsKt.addAll(destination$iv$iv, list$iv$iv);
        }
        Iterable $this$flatMap$iv2 = (List) destination$iv$iv;
        Iterable providers = CollectionsKt.toSet($this$flatMap$iv2);
        if (!(!((Collection) providers).isEmpty())) {
            throw new IllegalArgumentException("No providers were loaded with FastServiceLoader".toString());
        }
        Iterable $this$map$iv = providers;
        Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault($this$map$iv, 10));
        for (Object item$iv$iv : $this$map$iv) {
            destination$iv$iv2.add(INSTANCE.getProviderInstance((String) item$iv$iv, loader, service));
        }
        return (List) destination$iv$iv2;
    }

    private final <S> S getProviderInstance(String name, ClassLoader loader, Class<S> service) {
        Class clazz = Class.forName(name, false, loader);
        if (service.isAssignableFrom(clazz)) {
            return service.cast(clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]));
        }
        throw new IllegalArgumentException(("Expected service of class " + service + ", but found " + clazz).toString());
    }

    private final List<String> parse(URL url) {
        BufferedReader bufferedReader;
        String path = url.toString();
        Intrinsics.checkExpressionValueIsNotNull(path, "url.toString()");
        if (!StringsKt.startsWith$default(path, "jar", false, 2, (Object) null)) {
            bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
            Throwable th = (Throwable) null;
            try {
                BufferedReader reader = bufferedReader;
                List<String> parseFile = INSTANCE.parseFile(reader);
                CloseableKt.closeFinally(bufferedReader, th);
                return parseFile;
            } catch (Throwable th2) {
                try {
                    throw th2;
                } finally {
                }
            }
        }
        String pathToJar = StringsKt.substringBefore$default(StringsKt.substringAfter$default(path, "jar:file:", (String) null, 2, (Object) null), '!', (String) null, 2, (Object) null);
        String entry = StringsKt.substringAfter$default(path, "!/", (String) null, 2, (Object) null);
        JarFile $this$use$iv = new JarFile(pathToJar, false);
        try {
            bufferedReader = new BufferedReader(new InputStreamReader($this$use$iv.getInputStream(new ZipEntry(entry)), ExcelUtil.UTF8_ENCODING));
            Throwable th3 = (Throwable) null;
            try {
                BufferedReader r = bufferedReader;
                List<String> parseFile2 = INSTANCE.parseFile(r);
                CloseableKt.closeFinally(bufferedReader, th3);
                $this$use$iv.close();
                return parseFile2;
            } finally {
            }
        } catch (Throwable e$iv) {
            try {
                throw e$iv;
            } catch (Throwable e$iv2) {
                try {
                    $this$use$iv.close();
                    throw e$iv2;
                } catch (Throwable closeException$iv) {
                    ExceptionsKt.addSuppressed(e$iv, closeException$iv);
                    throw e$iv;
                }
            }
        }
    }

    private final <R> R use(JarFile $this$use, Function1<? super JarFile, ? extends R> function1) {
        try {
            R invoke = function1.invoke($this$use);
            InlineMarker.finallyStart(1);
            $this$use.close();
            InlineMarker.finallyEnd(1);
            return invoke;
        } catch (Throwable e) {
            try {
                throw e;
            } catch (Throwable e2) {
                InlineMarker.finallyStart(1);
                try {
                    $this$use.close();
                    InlineMarker.finallyEnd(1);
                    throw e2;
                } catch (Throwable closeException) {
                    ExceptionsKt.addSuppressed(e, closeException);
                    throw e;
                }
            }
        }
    }

    private final List<String> parseFile(BufferedReader r) {
        boolean z;
        Set names = new LinkedHashSet();
        while (true) {
            String line = r.readLine();
            if (line == null) {
                return CollectionsKt.toList(names);
            }
            String substringBefore$default = StringsKt.substringBefore$default(line, "#", (String) null, 2, (Object) null);
            if (substringBefore$default == null) {
                throw new TypeCastException("null cannot be cast to non-null type kotlin.CharSequence");
            }
            String serviceName = StringsKt.trim((CharSequence) substringBefore$default).toString();
            String $this$all$iv = serviceName;
            int i = 0;
            while (true) {
                if (i >= $this$all$iv.length()) {
                    z = true;
                    break;
                }
                char element$iv = $this$all$iv.charAt(i);
                char it = (element$iv == '.' || Character.isJavaIdentifierPart(element$iv)) ? (char) 1 : (char) 0;
                if (it == 0) {
                    z = false;
                    break;
                }
                i++;
            }
            if (!z) {
                throw new IllegalArgumentException(("Illegal service provider class name: " + serviceName).toString());
            }
            if (serviceName.length() > 0) {
                names.add(serviceName);
            }
        }
    }
}
