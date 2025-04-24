package androidx.navigation;

import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import androidx.navigation.Navigator;

@Navigator.Name(NotificationCompat.CATEGORY_NAVIGATION)
/* loaded from: classes.dex */
public class NavGraphNavigator extends Navigator<NavGraph> {
    private final NavigatorProvider mNavigatorProvider;

    public NavGraphNavigator(NavigatorProvider navigatorProvider) {
        this.mNavigatorProvider = navigatorProvider;
    }

    @Override // androidx.navigation.Navigator
    public NavGraph createDestination() {
        return new NavGraph(this);
    }

    @Override // androidx.navigation.Navigator
    public NavDestination navigate(NavGraph destination, Bundle args, NavOptions navOptions, Navigator.Extras navigatorExtras) {
        int startId = destination.getStartDestination();
        if (startId == 0) {
            throw new IllegalStateException("no start destination defined via app:startDestination for " + destination.getDisplayName());
        }
        NavDestination startDestination = destination.findNode(startId, false);
        if (startDestination == null) {
            String dest = destination.getStartDestDisplayName();
            throw new IllegalArgumentException("navigation destination " + dest + " is not a direct child of this NavGraph");
        }
        Navigator<NavDestination> navigator = this.mNavigatorProvider.getNavigator(startDestination.getNavigatorName());
        return navigator.navigate(startDestination, startDestination.addInDefaultArgs(args), navOptions, navigatorExtras);
    }

    @Override // androidx.navigation.Navigator
    public boolean popBackStack() {
        return true;
    }
}
