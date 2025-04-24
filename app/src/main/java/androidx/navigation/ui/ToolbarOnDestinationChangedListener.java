package androidx.navigation.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.transition.TransitionManager;
import java.lang.ref.WeakReference;

/* loaded from: classes.dex */
class ToolbarOnDestinationChangedListener extends AbstractAppBarOnDestinationChangedListener {
    private final WeakReference<Toolbar> mToolbarWeakReference;

    ToolbarOnDestinationChangedListener(Toolbar toolbar, AppBarConfiguration configuration) {
        super(toolbar.getContext(), configuration);
        this.mToolbarWeakReference = new WeakReference<>(toolbar);
    }

    @Override // androidx.navigation.ui.AbstractAppBarOnDestinationChangedListener, androidx.navigation.NavController.OnDestinationChangedListener
    public void onDestinationChanged(NavController controller, NavDestination destination, Bundle arguments) {
        Toolbar toolbar = this.mToolbarWeakReference.get();
        if (toolbar == null) {
            controller.removeOnDestinationChangedListener(this);
        } else {
            super.onDestinationChanged(controller, destination, arguments);
        }
    }

    @Override // androidx.navigation.ui.AbstractAppBarOnDestinationChangedListener
    protected void setTitle(CharSequence title) {
        this.mToolbarWeakReference.get().setTitle(title);
    }

    @Override // androidx.navigation.ui.AbstractAppBarOnDestinationChangedListener
    protected void setNavigationIcon(Drawable icon, int contentDescription) {
        Toolbar toolbar = this.mToolbarWeakReference.get();
        if (toolbar != null) {
            boolean useTransition = icon == null && toolbar.getNavigationIcon() != null;
            toolbar.setNavigationIcon(icon);
            toolbar.setNavigationContentDescription(contentDescription);
            if (useTransition) {
                TransitionManager.beginDelayedTransition(toolbar);
            }
        }
    }
}
