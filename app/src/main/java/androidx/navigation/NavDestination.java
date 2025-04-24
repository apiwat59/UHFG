package androidx.navigation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import androidx.collection.SparseArrayCompat;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public class NavDestination {
    private static final HashMap<String, Class<?>> sClasses = new HashMap<>();
    private SparseArrayCompat<NavAction> mActions;
    private HashMap<String, NavArgument> mArguments;
    private ArrayList<NavDeepLink> mDeepLinks;
    private int mId;
    private String mIdName;
    private CharSequence mLabel;
    private final String mNavigatorName;
    private NavGraph mParent;

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.CLASS)
    public @interface ClassType {
        Class<?> value();
    }

    static class DeepLinkMatch implements Comparable<DeepLinkMatch> {
        private final NavDestination mDestination;
        private final boolean mIsExactDeepLink;
        private final Bundle mMatchingArgs;

        DeepLinkMatch(NavDestination destination, Bundle matchingArgs, boolean isExactDeepLink) {
            this.mDestination = destination;
            this.mMatchingArgs = matchingArgs;
            this.mIsExactDeepLink = isExactDeepLink;
        }

        NavDestination getDestination() {
            return this.mDestination;
        }

        Bundle getMatchingArgs() {
            return this.mMatchingArgs;
        }

        @Override // java.lang.Comparable
        public int compareTo(DeepLinkMatch other) {
            boolean z = this.mIsExactDeepLink;
            if (z && !other.mIsExactDeepLink) {
                return 1;
            }
            if (!z && other.mIsExactDeepLink) {
                return -1;
            }
            return this.mMatchingArgs.size() - other.mMatchingArgs.size();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    protected static <C> Class<? extends C> parseClassFromName(Context context, String name, Class<? extends C> expectedClassType) {
        if (name.charAt(0) == '.') {
            name = context.getPackageName() + name;
        }
        HashMap<String, Class<?>> hashMap = sClasses;
        Class cls = hashMap.get(name);
        if (cls == null) {
            try {
                cls = Class.forName(name, true, context.getClassLoader());
                hashMap.put(name, cls);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
        if (!expectedClassType.isAssignableFrom(cls)) {
            throw new IllegalArgumentException(name + " must be a subclass of " + expectedClassType);
        }
        return cls;
    }

    static String getDisplayName(Context context, int id) {
        if (id <= 16777215) {
            return Integer.toString(id);
        }
        try {
            return context.getResources().getResourceName(id);
        } catch (Resources.NotFoundException e) {
            return Integer.toString(id);
        }
    }

    public final Map<String, NavArgument> getArguments() {
        HashMap<String, NavArgument> hashMap = this.mArguments;
        return hashMap == null ? Collections.emptyMap() : Collections.unmodifiableMap(hashMap);
    }

    public NavDestination(Navigator<? extends NavDestination> navigator) {
        this(NavigatorProvider.getNameForNavigator(navigator.getClass()));
    }

    public NavDestination(String navigatorName) {
        this.mNavigatorName = navigatorName;
    }

    public void onInflate(Context context, AttributeSet attrs) {
        TypedArray a = context.getResources().obtainAttributes(attrs, androidx.navigation.common.R.styleable.Navigator);
        setId(a.getResourceId(androidx.navigation.common.R.styleable.Navigator_android_id, 0));
        this.mIdName = getDisplayName(context, this.mId);
        setLabel(a.getText(androidx.navigation.common.R.styleable.Navigator_android_label));
        a.recycle();
    }

    final void setParent(NavGraph parent) {
        this.mParent = parent;
    }

    public final NavGraph getParent() {
        return this.mParent;
    }

    public final int getId() {
        return this.mId;
    }

    public final void setId(int id) {
        this.mId = id;
        this.mIdName = null;
    }

    String getDisplayName() {
        if (this.mIdName == null) {
            this.mIdName = Integer.toString(this.mId);
        }
        return this.mIdName;
    }

    public final void setLabel(CharSequence label) {
        this.mLabel = label;
    }

    public final CharSequence getLabel() {
        return this.mLabel;
    }

    public final String getNavigatorName() {
        return this.mNavigatorName;
    }

    public boolean hasDeepLink(Uri deepLink) {
        return matchDeepLink(deepLink) != null;
    }

    public final void addDeepLink(String uriPattern) {
        if (this.mDeepLinks == null) {
            this.mDeepLinks = new ArrayList<>();
        }
        this.mDeepLinks.add(new NavDeepLink(uriPattern));
    }

    DeepLinkMatch matchDeepLink(Uri uri) {
        ArrayList<NavDeepLink> arrayList = this.mDeepLinks;
        if (arrayList == null) {
            return null;
        }
        DeepLinkMatch bestMatch = null;
        Iterator<NavDeepLink> it = arrayList.iterator();
        while (it.hasNext()) {
            NavDeepLink deepLink = it.next();
            Bundle matchingArguments = deepLink.getMatchingArguments(uri, getArguments());
            if (matchingArguments != null) {
                DeepLinkMatch newMatch = new DeepLinkMatch(this, matchingArguments, deepLink.isExactDeepLink());
                if (bestMatch == null || newMatch.compareTo(bestMatch) > 0) {
                    bestMatch = newMatch;
                }
            }
        }
        return bestMatch;
    }

    int[] buildDeepLinkIds() {
        ArrayDeque<NavDestination> hierarchy = new ArrayDeque<>();
        NavDestination current = this;
        do {
            NavGraph parent = current.getParent();
            if (parent == null || parent.getStartDestination() != current.getId()) {
                hierarchy.addFirst(current);
            }
            current = parent;
        } while (current != null);
        int[] deepLinkIds = new int[hierarchy.size()];
        int index = 0;
        Iterator<NavDestination> it = hierarchy.iterator();
        while (it.hasNext()) {
            NavDestination destination = it.next();
            deepLinkIds[index] = destination.getId();
            index++;
        }
        return deepLinkIds;
    }

    boolean supportsActions() {
        return true;
    }

    public final NavAction getAction(int id) {
        SparseArrayCompat<NavAction> sparseArrayCompat = this.mActions;
        NavAction destination = sparseArrayCompat == null ? null : sparseArrayCompat.get(id);
        if (destination != null) {
            return destination;
        }
        if (getParent() != null) {
            return getParent().getAction(id);
        }
        return null;
    }

    public final void putAction(int actionId, int destId) {
        putAction(actionId, new NavAction(destId));
    }

    public final void putAction(int actionId, NavAction action) {
        if (!supportsActions()) {
            throw new UnsupportedOperationException("Cannot add action " + actionId + " to " + this + " as it does not support actions, indicating that it is a terminal destination in your navigation graph and will never trigger actions.");
        }
        if (actionId == 0) {
            throw new IllegalArgumentException("Cannot have an action with actionId 0");
        }
        if (this.mActions == null) {
            this.mActions = new SparseArrayCompat<>();
        }
        this.mActions.put(actionId, action);
    }

    public final void removeAction(int actionId) {
        SparseArrayCompat<NavAction> sparseArrayCompat = this.mActions;
        if (sparseArrayCompat == null) {
            return;
        }
        sparseArrayCompat.delete(actionId);
    }

    public final void addArgument(String argumentName, NavArgument argument) {
        if (this.mArguments == null) {
            this.mArguments = new HashMap<>();
        }
        this.mArguments.put(argumentName, argument);
    }

    public final void removeArgument(String argumentName) {
        HashMap<String, NavArgument> hashMap = this.mArguments;
        if (hashMap == null) {
            return;
        }
        hashMap.remove(argumentName);
    }

    Bundle addInDefaultArgs(Bundle args) {
        HashMap<String, NavArgument> hashMap;
        if (args == null && ((hashMap = this.mArguments) == null || hashMap.isEmpty())) {
            return null;
        }
        Bundle defaultArgs = new Bundle();
        HashMap<String, NavArgument> hashMap2 = this.mArguments;
        if (hashMap2 != null) {
            for (Map.Entry<String, NavArgument> argument : hashMap2.entrySet()) {
                argument.getValue().putDefaultValue(argument.getKey(), defaultArgs);
            }
        }
        if (args != null) {
            defaultArgs.putAll(args);
            HashMap<String, NavArgument> hashMap3 = this.mArguments;
            if (hashMap3 != null) {
                for (Map.Entry<String, NavArgument> argument2 : hashMap3.entrySet()) {
                    if (!argument2.getValue().verify(argument2.getKey(), args)) {
                        throw new IllegalArgumentException("Wrong argument type for '" + argument2.getKey() + "' in argument bundle. " + argument2.getValue().getType().getName() + " expected.");
                    }
                }
            }
        }
        return defaultArgs;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append("(");
        String str = this.mIdName;
        if (str == null) {
            sb.append("0x");
            sb.append(Integer.toHexString(this.mId));
        } else {
            sb.append(str);
        }
        sb.append(")");
        if (this.mLabel != null) {
            sb.append(" label=");
            sb.append(this.mLabel);
        }
        return sb.toString();
    }
}
