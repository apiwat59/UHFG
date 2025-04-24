package androidx.navigation;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.navigation.Navigator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Navigator.Name("activity")
/* loaded from: classes.dex */
public class ActivityNavigator extends Navigator<Destination> {
    private static final String EXTRA_NAV_CURRENT = "android-support-navigation:ActivityNavigator:current";
    private static final String EXTRA_NAV_SOURCE = "android-support-navigation:ActivityNavigator:source";
    private static final String EXTRA_POP_ENTER_ANIM = "android-support-navigation:ActivityNavigator:popEnterAnim";
    private static final String EXTRA_POP_EXIT_ANIM = "android-support-navigation:ActivityNavigator:popExitAnim";
    private Context mContext;
    private Activity mHostActivity;

    public ActivityNavigator(Context context) {
        this.mContext = context;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                this.mHostActivity = (Activity) context;
                return;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
    }

    public static void applyPopAnimationsToPendingTransition(Activity activity) {
        Intent intent = activity.getIntent();
        if (intent == null) {
            return;
        }
        int popEnterAnim = intent.getIntExtra(EXTRA_POP_ENTER_ANIM, -1);
        int popExitAnim = intent.getIntExtra(EXTRA_POP_EXIT_ANIM, -1);
        if (popEnterAnim != -1 || popExitAnim != -1) {
            activity.overridePendingTransition(popEnterAnim != -1 ? popEnterAnim : 0, popExitAnim != -1 ? popExitAnim : 0);
        }
    }

    final Context getContext() {
        return this.mContext;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // androidx.navigation.Navigator
    public Destination createDestination() {
        return new Destination(this);
    }

    @Override // androidx.navigation.Navigator
    public boolean popBackStack() {
        Activity activity = this.mHostActivity;
        if (activity != null) {
            activity.finish();
            return true;
        }
        return false;
    }

    @Override // androidx.navigation.Navigator
    public NavDestination navigate(Destination destination, Bundle args, NavOptions navOptions, Navigator.Extras navigatorExtras) {
        Intent hostIntent;
        int hostCurrentId;
        if (destination.getIntent() == null) {
            throw new IllegalStateException("Destination " + destination.getId() + " does not have an Intent set.");
        }
        Intent intent = new Intent(destination.getIntent());
        if (args != null) {
            intent.putExtras(args);
            String dataPattern = destination.getDataPattern();
            if (!TextUtils.isEmpty(dataPattern)) {
                StringBuffer data = new StringBuffer();
                Pattern fillInPattern = Pattern.compile("\\{(.+?)\\}");
                Matcher matcher = fillInPattern.matcher(dataPattern);
                while (matcher.find()) {
                    String argName = matcher.group(1);
                    if (args.containsKey(argName)) {
                        matcher.appendReplacement(data, "");
                        data.append(Uri.encode(args.get(argName).toString()));
                    } else {
                        throw new IllegalArgumentException("Could not find " + argName + " in " + args + " to fill data pattern " + dataPattern);
                    }
                }
                matcher.appendTail(data);
                intent.setData(Uri.parse(data.toString()));
            }
        }
        if (navigatorExtras instanceof Extras) {
            Extras extras = (Extras) navigatorExtras;
            intent.addFlags(extras.getFlags());
        }
        if (!(this.mContext instanceof Activity)) {
            intent.addFlags(268435456);
        }
        if (navOptions != null && navOptions.shouldLaunchSingleTop()) {
            intent.addFlags(536870912);
        }
        Activity activity = this.mHostActivity;
        if (activity != null && (hostIntent = activity.getIntent()) != null && (hostCurrentId = hostIntent.getIntExtra(EXTRA_NAV_CURRENT, 0)) != 0) {
            intent.putExtra(EXTRA_NAV_SOURCE, hostCurrentId);
        }
        int destId = destination.getId();
        intent.putExtra(EXTRA_NAV_CURRENT, destId);
        if (navOptions != null) {
            intent.putExtra(EXTRA_POP_ENTER_ANIM, navOptions.getPopEnterAnim());
            intent.putExtra(EXTRA_POP_EXIT_ANIM, navOptions.getPopExitAnim());
        }
        if (navigatorExtras instanceof Extras) {
            Extras extras2 = (Extras) navigatorExtras;
            ActivityOptionsCompat activityOptions = extras2.getActivityOptions();
            if (activityOptions != null) {
                ActivityCompat.startActivity(this.mContext, intent, activityOptions.toBundle());
            } else {
                this.mContext.startActivity(intent);
            }
        } else {
            this.mContext.startActivity(intent);
        }
        if (navOptions != null && this.mHostActivity != null) {
            int enterAnim = navOptions.getEnterAnim();
            int exitAnim = navOptions.getExitAnim();
            if (enterAnim != -1 || exitAnim != -1) {
                this.mHostActivity.overridePendingTransition(enterAnim != -1 ? enterAnim : 0, exitAnim != -1 ? exitAnim : 0);
                return null;
            }
            return null;
        }
        return null;
    }

    public static class Destination extends NavDestination {
        private String mDataPattern;
        private Intent mIntent;

        public Destination(NavigatorProvider navigatorProvider) {
            this((Navigator<? extends Destination>) navigatorProvider.getNavigator(ActivityNavigator.class));
        }

        public Destination(Navigator<? extends Destination> activityNavigator) {
            super(activityNavigator);
        }

        @Override // androidx.navigation.NavDestination
        public void onInflate(Context context, AttributeSet attrs) {
            super.onInflate(context, attrs);
            TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.ActivityNavigator);
            String targetPackage = a.getString(R.styleable.ActivityNavigator_targetPackage);
            if (targetPackage != null) {
                targetPackage = targetPackage.replace("${applicationId}", context.getPackageName());
            }
            setTargetPackage(targetPackage);
            String className = a.getString(R.styleable.ActivityNavigator_android_name);
            if (className != null) {
                if (className.charAt(0) == '.') {
                    className = context.getPackageName() + className;
                }
                setComponentName(new ComponentName(context, className));
            }
            setAction(a.getString(R.styleable.ActivityNavigator_action));
            String data = a.getString(R.styleable.ActivityNavigator_data);
            if (data != null) {
                setData(Uri.parse(data));
            }
            setDataPattern(a.getString(R.styleable.ActivityNavigator_dataPattern));
            a.recycle();
        }

        public final Destination setIntent(Intent intent) {
            this.mIntent = intent;
            return this;
        }

        public final Intent getIntent() {
            return this.mIntent;
        }

        public final Destination setTargetPackage(String packageName) {
            if (this.mIntent == null) {
                this.mIntent = new Intent();
            }
            this.mIntent.setPackage(packageName);
            return this;
        }

        public final String getTargetPackage() {
            Intent intent = this.mIntent;
            if (intent == null) {
                return null;
            }
            return intent.getPackage();
        }

        public final Destination setComponentName(ComponentName name) {
            if (this.mIntent == null) {
                this.mIntent = new Intent();
            }
            this.mIntent.setComponent(name);
            return this;
        }

        public final ComponentName getComponent() {
            Intent intent = this.mIntent;
            if (intent == null) {
                return null;
            }
            return intent.getComponent();
        }

        public final Destination setAction(String action) {
            if (this.mIntent == null) {
                this.mIntent = new Intent();
            }
            this.mIntent.setAction(action);
            return this;
        }

        public final String getAction() {
            Intent intent = this.mIntent;
            if (intent == null) {
                return null;
            }
            return intent.getAction();
        }

        public final Destination setData(Uri data) {
            if (this.mIntent == null) {
                this.mIntent = new Intent();
            }
            this.mIntent.setData(data);
            return this;
        }

        public final Uri getData() {
            Intent intent = this.mIntent;
            if (intent == null) {
                return null;
            }
            return intent.getData();
        }

        public final Destination setDataPattern(String dataPattern) {
            this.mDataPattern = dataPattern;
            return this;
        }

        public final String getDataPattern() {
            return this.mDataPattern;
        }

        @Override // androidx.navigation.NavDestination
        boolean supportsActions() {
            return false;
        }

        @Override // androidx.navigation.NavDestination
        public String toString() {
            ComponentName componentName = getComponent();
            StringBuilder sb = new StringBuilder();
            sb.append(super.toString());
            if (componentName != null) {
                sb.append(" class=");
                sb.append(componentName.getClassName());
            } else {
                String action = getAction();
                if (action != null) {
                    sb.append(" action=");
                    sb.append(action);
                }
            }
            return sb.toString();
        }
    }

    public static final class Extras implements Navigator.Extras {
        private final ActivityOptionsCompat mActivityOptions;
        private final int mFlags;

        Extras(int flags, ActivityOptionsCompat activityOptions) {
            this.mFlags = flags;
            this.mActivityOptions = activityOptions;
        }

        public int getFlags() {
            return this.mFlags;
        }

        public ActivityOptionsCompat getActivityOptions() {
            return this.mActivityOptions;
        }

        public static final class Builder {
            private ActivityOptionsCompat mActivityOptions;
            private int mFlags;

            public Builder addFlags(int flags) {
                this.mFlags |= flags;
                return this;
            }

            public Builder setActivityOptions(ActivityOptionsCompat activityOptions) {
                this.mActivityOptions = activityOptions;
                return this;
            }

            public Extras build() {
                return new Extras(this.mFlags, this.mActivityOptions);
            }
        }
    }
}
