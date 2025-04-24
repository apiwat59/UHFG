package androidx.navigation.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.FloatingWindow;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
abstract class AbstractAppBarOnDestinationChangedListener implements NavController.OnDestinationChangedListener {
    private ValueAnimator mAnimator;
    private DrawerArrowDrawable mArrowDrawable;
    private final Context mContext;
    private final WeakReference<DrawerLayout> mDrawerLayoutWeakReference;
    private final Set<Integer> mTopLevelDestinations;

    protected abstract void setNavigationIcon(Drawable drawable, int i);

    protected abstract void setTitle(CharSequence charSequence);

    AbstractAppBarOnDestinationChangedListener(Context context, AppBarConfiguration configuration) {
        this.mContext = context;
        this.mTopLevelDestinations = configuration.getTopLevelDestinations();
        DrawerLayout drawerLayout = configuration.getDrawerLayout();
        if (drawerLayout != null) {
            this.mDrawerLayoutWeakReference = new WeakReference<>(drawerLayout);
        } else {
            this.mDrawerLayoutWeakReference = null;
        }
    }

    @Override // androidx.navigation.NavController.OnDestinationChangedListener
    public void onDestinationChanged(NavController controller, NavDestination destination, Bundle arguments) {
        if (destination instanceof FloatingWindow) {
            return;
        }
        WeakReference<DrawerLayout> weakReference = this.mDrawerLayoutWeakReference;
        DrawerLayout drawerLayout = weakReference != null ? weakReference.get() : null;
        if (this.mDrawerLayoutWeakReference != null && drawerLayout == null) {
            controller.removeOnDestinationChangedListener(this);
            return;
        }
        CharSequence label = destination.getLabel();
        if (label != null) {
            StringBuffer title = new StringBuffer();
            Pattern fillInPattern = Pattern.compile("\\{(.+?)\\}");
            Matcher matcher = fillInPattern.matcher(label);
            while (matcher.find()) {
                String argName = matcher.group(1);
                if (arguments != null && arguments.containsKey(argName)) {
                    matcher.appendReplacement(title, "");
                    title.append(arguments.get(argName).toString());
                } else {
                    throw new IllegalArgumentException("Could not find " + argName + " in " + arguments + " to fill label " + ((Object) label));
                }
            }
            matcher.appendTail(title);
            setTitle(title);
        }
        boolean isTopLevelDestination = NavigationUI.matchDestinations(destination, this.mTopLevelDestinations);
        if (drawerLayout == null && isTopLevelDestination) {
            setNavigationIcon(null, 0);
        } else {
            setActionBarUpIndicator(drawerLayout != null && isTopLevelDestination);
        }
    }

    private void setActionBarUpIndicator(boolean showAsDrawerIndicator) {
        boolean animate = true;
        if (this.mArrowDrawable == null) {
            this.mArrowDrawable = new DrawerArrowDrawable(this.mContext);
            animate = false;
        }
        setNavigationIcon(this.mArrowDrawable, showAsDrawerIndicator ? R.string.nav_app_bar_open_drawer_description : R.string.nav_app_bar_navigate_up_description);
        float endValue = showAsDrawerIndicator ? 0.0f : 1.0f;
        if (animate) {
            float startValue = this.mArrowDrawable.getProgress();
            ValueAnimator valueAnimator = this.mAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this.mArrowDrawable, "progress", startValue, endValue);
            this.mAnimator = ofFloat;
            ofFloat.start();
            return;
        }
        this.mArrowDrawable.setProgress(endValue);
    }
}
