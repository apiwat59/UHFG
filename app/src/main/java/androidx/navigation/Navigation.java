package androidx.navigation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import androidx.core.app.ActivityCompat;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
public final class Navigation {
    private Navigation() {
    }

    public static NavController findNavController(Activity activity, int viewId) {
        View view = ActivityCompat.requireViewById(activity, viewId);
        NavController navController = findViewNavController(view);
        if (navController == null) {
            throw new IllegalStateException("Activity " + activity + " does not have a NavController set on " + viewId);
        }
        return navController;
    }

    public static NavController findNavController(View view) {
        NavController navController = findViewNavController(view);
        if (navController == null) {
            throw new IllegalStateException("View " + view + " does not have a NavController set");
        }
        return navController;
    }

    public static View.OnClickListener createNavigateOnClickListener(int resId) {
        return createNavigateOnClickListener(resId, null);
    }

    public static View.OnClickListener createNavigateOnClickListener(final int resId, final Bundle args) {
        return new View.OnClickListener() { // from class: androidx.navigation.Navigation.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(resId, args);
            }
        };
    }

    public static View.OnClickListener createNavigateOnClickListener(final NavDirections directions) {
        return new View.OnClickListener() { // from class: androidx.navigation.Navigation.2
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(NavDirections.this);
            }
        };
    }

    public static void setViewNavController(View view, NavController controller) {
        view.setTag(R.id.nav_controller_view_tag, controller);
    }

    private static NavController findViewNavController(View view) {
        while (true) {
            View view2 = null;
            if (view == null) {
                return null;
            }
            NavController controller = getViewNavController(view);
            if (controller != null) {
                return controller;
            }
            Object parent = view.getParent();
            if (parent instanceof View) {
                view2 = (View) parent;
            }
            view = view2;
        }
    }

    private static NavController getViewNavController(View view) {
        Object tag = view.getTag(R.id.nav_controller_view_tag);
        if (tag instanceof WeakReference) {
            NavController controller = (NavController) ((WeakReference) tag).get();
            return controller;
        }
        if (!(tag instanceof NavController)) {
            return null;
        }
        NavController controller2 = (NavController) tag;
        return controller2;
    }
}
