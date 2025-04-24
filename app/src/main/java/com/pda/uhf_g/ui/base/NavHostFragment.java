package com.pda.uhf_g.ui.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.navigation.NavController;
import androidx.navigation.NavHost;
import androidx.navigation.NavHostController;
import androidx.navigation.Navigation;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.DialogFragmentNavigator;
import androidx.navigation.fragment.FragmentNavigator;
import com.pda.uhf_g.R;

/* loaded from: classes.dex */
public class NavHostFragment extends Fragment implements NavHost {
    private static final String KEY_DEFAULT_NAV_HOST = "android-support-nav:fragment:defaultHost";
    private static final String KEY_GRAPH_ID = "android-support-nav:fragment:graphId";
    private static final String KEY_NAV_CONTROLLER_STATE = "android-support-nav:fragment:navControllerState";
    private static final String KEY_START_DESTINATION_ARGS = "android-support-nav:fragment:startDestinationArgs";
    private boolean mDefaultNavHost;
    private int mGraphId;
    private Boolean mIsPrimaryBeforeOnCreate = null;
    private NavHostController mNavController;

    public static NavController findNavController(Fragment fragment) {
        for (Fragment findFragment = fragment; findFragment != null; findFragment = findFragment.getParentFragment()) {
            if (findFragment instanceof androidx.navigation.fragment.NavHostFragment) {
                return ((androidx.navigation.fragment.NavHostFragment) findFragment).getNavController();
            }
            Fragment primaryNavFragment = findFragment.getParentFragmentManager().getPrimaryNavigationFragment();
            if (primaryNavFragment instanceof androidx.navigation.fragment.NavHostFragment) {
                return ((androidx.navigation.fragment.NavHostFragment) primaryNavFragment).getNavController();
            }
        }
        View view = fragment.getView();
        if (view != null) {
            return Navigation.findNavController(view);
        }
        throw new IllegalStateException("Fragment " + fragment + " does not have a NavController set");
    }

    public static androidx.navigation.fragment.NavHostFragment create(int graphResId) {
        return create(graphResId, null);
    }

    public static androidx.navigation.fragment.NavHostFragment create(int graphResId, Bundle startDestinationArgs) {
        Bundle b = null;
        if (graphResId != 0) {
            b = new Bundle();
            b.putInt(KEY_GRAPH_ID, graphResId);
        }
        if (startDestinationArgs != null) {
            if (b == null) {
                b = new Bundle();
            }
            b.putBundle(KEY_START_DESTINATION_ARGS, startDestinationArgs);
        }
        androidx.navigation.fragment.NavHostFragment result = new androidx.navigation.fragment.NavHostFragment();
        if (b != null) {
            result.setArguments(b);
        }
        return result;
    }

    @Override // androidx.navigation.NavHost
    public final NavController getNavController() {
        NavHostController navHostController = this.mNavController;
        if (navHostController == null) {
            throw new IllegalStateException("NavController is not available before onCreate()");
        }
        return navHostController;
    }

    @Override // androidx.fragment.app.Fragment
    public void onAttach(Context context) {
        super.onAttach(context);
        if (this.mDefaultNavHost) {
            getParentFragmentManager().beginTransaction().setPrimaryNavigationFragment(this).commit();
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = requireContext();
        NavHostController navHostController = new NavHostController(context);
        this.mNavController = navHostController;
        navHostController.setLifecycleOwner(this);
        this.mNavController.setOnBackPressedDispatcher(requireActivity().getOnBackPressedDispatcher());
        NavHostController navHostController2 = this.mNavController;
        Boolean bool = this.mIsPrimaryBeforeOnCreate;
        navHostController2.enableOnBackPressed(bool != null && bool.booleanValue());
        Bundle startDestinationArgs = null;
        this.mIsPrimaryBeforeOnCreate = null;
        this.mNavController.setViewModelStore(getViewModelStore());
        onCreateNavController(this.mNavController);
        Bundle navState = null;
        if (savedInstanceState != null) {
            navState = savedInstanceState.getBundle(KEY_NAV_CONTROLLER_STATE);
            if (savedInstanceState.getBoolean(KEY_DEFAULT_NAV_HOST, false)) {
                this.mDefaultNavHost = true;
                getParentFragmentManager().beginTransaction().setPrimaryNavigationFragment(this).commit();
            }
            this.mGraphId = savedInstanceState.getInt(KEY_GRAPH_ID);
        }
        if (navState != null) {
            this.mNavController.restoreState(navState);
        }
        int i = this.mGraphId;
        if (i != 0) {
            this.mNavController.setGraph(i);
            return;
        }
        Bundle args = getArguments();
        int graphId = args != null ? args.getInt(KEY_GRAPH_ID) : 0;
        if (args != null) {
            startDestinationArgs = args.getBundle(KEY_START_DESTINATION_ARGS);
        }
        if (graphId != 0) {
            this.mNavController.setGraph(graphId, startDestinationArgs);
        }
    }

    protected void onCreateNavController(NavController navController) {
        navController.getNavigatorProvider().addNavigator(new DialogFragmentNavigator(requireContext(), getChildFragmentManager()));
        navController.getNavigatorProvider().addNavigator(createFragmentNavigator());
    }

    @Override // androidx.fragment.app.Fragment
    public void onPrimaryNavigationFragmentChanged(boolean isPrimaryNavigationFragment) {
        NavHostController navHostController = this.mNavController;
        if (navHostController != null) {
            navHostController.enableOnBackPressed(isPrimaryNavigationFragment);
        } else {
            this.mIsPrimaryBeforeOnCreate = Boolean.valueOf(isPrimaryNavigationFragment);
        }
    }

    @Deprecated
    protected Navigator<? extends FragmentNavigator.Destination> createFragmentNavigator() {
        return new androidx.navigation.fragment.FragmentNavigator(requireContext(), getChildFragmentManager(), getContainerId());
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentContainerView containerView = new FragmentContainerView(inflater.getContext());
        containerView.setId(getContainerId());
        return containerView;
    }

    private int getContainerId() {
        int id = getId();
        if (id != 0 && id != -1) {
            return id;
        }
        return R.id.nav_host_fragment_container;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!(view instanceof ViewGroup)) {
            throw new IllegalStateException("created host view " + view + " is not a ViewGroup");
        }
        Navigation.setViewNavController(view, this.mNavController);
        if (view.getParent() != null) {
            View rootView = (View) view.getParent();
            if (rootView.getId() == getId()) {
                Navigation.setViewNavController(rootView, this.mNavController);
            }
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onInflate(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        TypedArray navHost = context.obtainStyledAttributes(attrs, R.styleable.NavHost);
        int graphId = navHost.getResourceId(0, 0);
        if (graphId != 0) {
            this.mGraphId = graphId;
        }
        navHost.recycle();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NavHostFragment);
        boolean defaultHost = a.getBoolean(0, false);
        if (defaultHost) {
            this.mDefaultNavHost = true;
        }
        a.recycle();
    }

    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle navState = this.mNavController.saveState();
        if (navState != null) {
            outState.putBundle(KEY_NAV_CONTROLLER_STATE, navState);
        }
        if (this.mDefaultNavHost) {
            outState.putBoolean(KEY_DEFAULT_NAV_HOST, true);
        }
        int i = this.mGraphId;
        if (i != 0) {
            outState.putInt(KEY_GRAPH_ID, i);
        }
    }
}
