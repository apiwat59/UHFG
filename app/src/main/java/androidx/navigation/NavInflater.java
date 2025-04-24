package androidx.navigation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.navigation.NavArgument;
import androidx.navigation.NavOptions;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

/* loaded from: classes.dex */
public final class NavInflater {
    static final String APPLICATION_ID_PLACEHOLDER = "${applicationId}";
    private static final String TAG_ACTION = "action";
    private static final String TAG_ARGUMENT = "argument";
    private static final String TAG_DEEP_LINK = "deepLink";
    private static final String TAG_INCLUDE = "include";
    private static final ThreadLocal<TypedValue> sTmpValue = new ThreadLocal<>();
    private Context mContext;
    private NavigatorProvider mNavigatorProvider;

    public NavInflater(Context context, NavigatorProvider navigatorProvider) {
        this.mContext = context;
        this.mNavigatorProvider = navigatorProvider;
    }

    public NavGraph inflate(int graphResId) {
        int type;
        Resources res = this.mContext.getResources();
        XmlResourceParser parser = res.getXml(graphResId);
        AttributeSet attrs = Xml.asAttributeSet(parser);
        do {
            try {
                try {
                    type = parser.next();
                    if (type == 2) {
                        break;
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Exception inflating " + res.getResourceName(graphResId) + " line " + parser.getLineNumber(), e);
                }
            } finally {
                parser.close();
            }
        } while (type != 1);
        if (type != 2) {
            throw new XmlPullParserException("No start tag found");
        }
        String rootElement = parser.getName();
        NavDestination destination = inflate(res, parser, attrs, graphResId);
        if (!(destination instanceof NavGraph)) {
            throw new IllegalArgumentException("Root element <" + rootElement + "> did not inflate into a NavGraph");
        }
        return (NavGraph) destination;
    }

    private NavDestination inflate(Resources res, XmlResourceParser parser, AttributeSet attrs, int graphResId) throws XmlPullParserException, IOException {
        int depth;
        Navigator<?> navigator = this.mNavigatorProvider.getNavigator(parser.getName());
        NavDestination dest = navigator.createDestination();
        dest.onInflate(this.mContext, attrs);
        int innerDepth = parser.getDepth() + 1;
        while (true) {
            int type = parser.next();
            if (type == 1 || ((depth = parser.getDepth()) < innerDepth && type == 3)) {
                break;
            }
            if (type == 2 && depth <= innerDepth) {
                String name = parser.getName();
                if (TAG_ARGUMENT.equals(name)) {
                    inflateArgumentForDestination(res, dest, attrs, graphResId);
                } else if (TAG_DEEP_LINK.equals(name)) {
                    inflateDeepLink(res, dest, attrs);
                } else if (TAG_ACTION.equals(name)) {
                    inflateAction(res, dest, attrs, parser, graphResId);
                } else if (TAG_INCLUDE.equals(name) && (dest instanceof NavGraph)) {
                    TypedArray a = res.obtainAttributes(attrs, R.styleable.NavInclude);
                    int id = a.getResourceId(R.styleable.NavInclude_graph, 0);
                    ((NavGraph) dest).addDestination(inflate(id));
                    a.recycle();
                } else if (dest instanceof NavGraph) {
                    ((NavGraph) dest).addDestination(inflate(res, parser, attrs, graphResId));
                }
            }
        }
        return dest;
    }

    private void inflateArgumentForDestination(Resources res, NavDestination dest, AttributeSet attrs, int graphResId) throws XmlPullParserException {
        TypedArray a = res.obtainAttributes(attrs, R.styleable.NavArgument);
        String name = a.getString(R.styleable.NavArgument_android_name);
        if (name == null) {
            throw new XmlPullParserException("Arguments must have a name");
        }
        NavArgument argument = inflateArgument(a, res, graphResId);
        dest.addArgument(name, argument);
        a.recycle();
    }

    private void inflateArgumentForBundle(Resources res, Bundle bundle, AttributeSet attrs, int graphResId) throws XmlPullParserException {
        TypedArray a = res.obtainAttributes(attrs, R.styleable.NavArgument);
        String name = a.getString(R.styleable.NavArgument_android_name);
        if (name == null) {
            throw new XmlPullParserException("Arguments must have a name");
        }
        NavArgument argument = inflateArgument(a, res, graphResId);
        if (argument.isDefaultValuePresent()) {
            argument.putDefaultValue(name, bundle);
        }
        a.recycle();
    }

    private NavArgument inflateArgument(TypedArray a, Resources res, int graphResId) throws XmlPullParserException {
        NavArgument.Builder argumentBuilder = new NavArgument.Builder();
        argumentBuilder.setIsNullable(a.getBoolean(R.styleable.NavArgument_nullable, false));
        ThreadLocal<TypedValue> threadLocal = sTmpValue;
        TypedValue value = threadLocal.get();
        if (value == null) {
            value = new TypedValue();
            threadLocal.set(value);
        }
        Object defaultValue = null;
        NavType navType = null;
        String argType = a.getString(R.styleable.NavArgument_argType);
        if (argType != null) {
            navType = NavType.fromArgType(argType, res.getResourcePackageName(graphResId));
        }
        if (a.getValue(R.styleable.NavArgument_android_defaultValue, value)) {
            if (navType == NavType.ReferenceType) {
                if (value.resourceId != 0) {
                    defaultValue = Integer.valueOf(value.resourceId);
                } else if (value.type == 16 && value.data == 0) {
                    defaultValue = 0;
                } else {
                    throw new XmlPullParserException("unsupported value '" + ((Object) value.string) + "' for " + navType.getName() + ". Must be a reference to a resource.");
                }
            } else if (value.resourceId != 0) {
                if (navType == null) {
                    navType = NavType.ReferenceType;
                    defaultValue = Integer.valueOf(value.resourceId);
                } else {
                    throw new XmlPullParserException("unsupported value '" + ((Object) value.string) + "' for " + navType.getName() + ". You must use a \"" + NavType.ReferenceType.getName() + "\" type to reference other resources.");
                }
            } else if (navType == NavType.StringType) {
                defaultValue = a.getString(R.styleable.NavArgument_android_defaultValue);
            } else {
                int i = value.type;
                if (i == 3) {
                    String stringValue = value.string.toString();
                    if (navType == null) {
                        navType = NavType.inferFromValue(stringValue);
                    }
                    defaultValue = navType.parseValue(stringValue);
                } else if (i == 4) {
                    navType = checkNavType(value, navType, NavType.FloatType, argType, TypedValues.Custom.S_FLOAT);
                    defaultValue = Float.valueOf(value.getFloat());
                } else if (i == 5) {
                    navType = checkNavType(value, navType, NavType.IntType, argType, TypedValues.Custom.S_DIMENSION);
                    defaultValue = Integer.valueOf((int) value.getDimension(res.getDisplayMetrics()));
                } else if (i == 18) {
                    navType = checkNavType(value, navType, NavType.BoolType, argType, TypedValues.Custom.S_BOOLEAN);
                    defaultValue = Boolean.valueOf(value.data != 0);
                } else if (value.type >= 16 && value.type <= 31) {
                    navType = checkNavType(value, navType, NavType.IntType, argType, TypedValues.Custom.S_INT);
                    defaultValue = Integer.valueOf(value.data);
                } else {
                    throw new XmlPullParserException("unsupported argument type " + value.type);
                }
            }
        }
        if (defaultValue != null) {
            argumentBuilder.setDefaultValue(defaultValue);
        }
        if (navType != null) {
            argumentBuilder.setType(navType);
        }
        return argumentBuilder.build();
    }

    private static NavType checkNavType(TypedValue value, NavType navType, NavType expectedNavType, String argType, String foundType) throws XmlPullParserException {
        if (navType == null || navType == expectedNavType) {
            return navType != null ? navType : expectedNavType;
        }
        throw new XmlPullParserException("Type is " + argType + " but found " + foundType + ": " + value.data);
    }

    private void inflateDeepLink(Resources res, NavDestination dest, AttributeSet attrs) {
        TypedArray a = res.obtainAttributes(attrs, R.styleable.NavDeepLink);
        String uri = a.getString(R.styleable.NavDeepLink_uri);
        if (TextUtils.isEmpty(uri)) {
            throw new IllegalArgumentException("Every <deepLink> must include an app:uri");
        }
        dest.addDeepLink(uri.replace(APPLICATION_ID_PLACEHOLDER, this.mContext.getPackageName()));
        a.recycle();
    }

    private void inflateAction(Resources res, NavDestination dest, AttributeSet attrs, XmlResourceParser parser, int graphResId) throws IOException, XmlPullParserException {
        int depth;
        TypedArray a = res.obtainAttributes(attrs, R.styleable.NavAction);
        int id = a.getResourceId(R.styleable.NavAction_android_id, 0);
        int destId = a.getResourceId(R.styleable.NavAction_destination, 0);
        NavAction action = new NavAction(destId);
        NavOptions.Builder builder = new NavOptions.Builder();
        builder.setLaunchSingleTop(a.getBoolean(R.styleable.NavAction_launchSingleTop, false));
        builder.setPopUpTo(a.getResourceId(R.styleable.NavAction_popUpTo, -1), a.getBoolean(R.styleable.NavAction_popUpToInclusive, false));
        builder.setEnterAnim(a.getResourceId(R.styleable.NavAction_enterAnim, -1));
        builder.setExitAnim(a.getResourceId(R.styleable.NavAction_exitAnim, -1));
        builder.setPopEnterAnim(a.getResourceId(R.styleable.NavAction_popEnterAnim, -1));
        builder.setPopExitAnim(a.getResourceId(R.styleable.NavAction_popExitAnim, -1));
        action.setNavOptions(builder.build());
        Bundle args = new Bundle();
        int innerDepth = parser.getDepth() + 1;
        while (true) {
            int type = parser.next();
            if (type != 1 && ((depth = parser.getDepth()) >= innerDepth || type != 3)) {
                if (type == 2 && depth <= innerDepth) {
                    String name = parser.getName();
                    if (TAG_ARGUMENT.equals(name)) {
                        inflateArgumentForBundle(res, args, attrs, graphResId);
                    }
                }
            }
        }
        if (!args.isEmpty()) {
            action.setDefaultArguments(args);
        }
        dest.putAction(id, action);
        a.recycle();
    }
}
