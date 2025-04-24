package androidx.navigation;

import androidx.navigation.Navigator;
import java.util.HashMap;
import java.util.Map;

/* loaded from: classes.dex */
public class NavigatorProvider {
    private static final HashMap<Class<?>, String> sAnnotationNames = new HashMap<>();
    private final HashMap<String, Navigator<? extends NavDestination>> mNavigators = new HashMap<>();

    private static boolean validateName(String name) {
        return (name == null || name.isEmpty()) ? false : true;
    }

    static String getNameForNavigator(Class<? extends Navigator> navigatorClass) {
        HashMap<Class<?>, String> hashMap = sAnnotationNames;
        String name = hashMap.get(navigatorClass);
        if (name == null) {
            Navigator.Name annotation = (Navigator.Name) navigatorClass.getAnnotation(Navigator.Name.class);
            name = annotation != null ? annotation.value() : null;
            if (!validateName(name)) {
                throw new IllegalArgumentException("No @Navigator.Name annotation found for " + navigatorClass.getSimpleName());
            }
            hashMap.put(navigatorClass, name);
        }
        return name;
    }

    public final <T extends Navigator<?>> T getNavigator(Class<T> cls) {
        return (T) getNavigator(getNameForNavigator(cls));
    }

    public <T extends Navigator<?>> T getNavigator(String name) {
        if (!validateName(name)) {
            throw new IllegalArgumentException("navigator name cannot be an empty string");
        }
        Navigator<? extends NavDestination> navigator = this.mNavigators.get(name);
        if (navigator == null) {
            throw new IllegalStateException("Could not find Navigator with name \"" + name + "\". You must call NavController.addNavigator() for each navigation type.");
        }
        return navigator;
    }

    public final Navigator<? extends NavDestination> addNavigator(Navigator<? extends NavDestination> navigator) {
        String name = getNameForNavigator(navigator.getClass());
        return addNavigator(name, navigator);
    }

    public Navigator<? extends NavDestination> addNavigator(String name, Navigator<? extends NavDestination> navigator) {
        if (!validateName(name)) {
            throw new IllegalArgumentException("navigator name cannot be an empty string");
        }
        return this.mNavigators.put(name, navigator);
    }

    Map<String, Navigator<? extends NavDestination>> getNavigators() {
        return this.mNavigators;
    }
}
