package androidx.navigation.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.FloatingWindow;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.NavigatorProvider;

@Navigator.Name("dialog")
/* loaded from: classes.dex */
public final class DialogFragmentNavigator extends Navigator<Destination> {
    private static final String DIALOG_TAG = "androidx-nav-fragment:navigator:dialog:";
    private static final String KEY_DIALOG_COUNT = "androidx-nav-dialogfragment:navigator:count";
    private static final String TAG = "DialogFragmentNavigator";
    private final Context mContext;
    private final FragmentManager mFragmentManager;
    private int mDialogCount = 0;
    private LifecycleEventObserver mObserver = new LifecycleEventObserver() { // from class: androidx.navigation.fragment.DialogFragmentNavigator.1
        @Override // androidx.lifecycle.LifecycleEventObserver
        public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
            if (event == Lifecycle.Event.ON_STOP) {
                DialogFragment dialogFragment = (DialogFragment) source;
                if (!dialogFragment.requireDialog().isShowing()) {
                    NavHostFragment.findNavController(dialogFragment).popBackStack();
                }
            }
        }
    };

    public DialogFragmentNavigator(Context context, FragmentManager manager) {
        this.mContext = context;
        this.mFragmentManager = manager;
    }

    @Override // androidx.navigation.Navigator
    public boolean popBackStack() {
        if (this.mDialogCount == 0) {
            return false;
        }
        if (this.mFragmentManager.isStateSaved()) {
            Log.i(TAG, "Ignoring popBackStack() call: FragmentManager has already saved its state");
            return false;
        }
        FragmentManager fragmentManager = this.mFragmentManager;
        StringBuilder sb = new StringBuilder();
        sb.append(DIALOG_TAG);
        int i = this.mDialogCount - 1;
        this.mDialogCount = i;
        sb.append(i);
        Fragment existingFragment = fragmentManager.findFragmentByTag(sb.toString());
        if (existingFragment != null) {
            existingFragment.getLifecycle().removeObserver(this.mObserver);
            ((DialogFragment) existingFragment).dismiss();
        }
        return true;
    }

    @Override // androidx.navigation.Navigator
    public Destination createDestination() {
        return new Destination(this);
    }

    @Override // androidx.navigation.Navigator
    public NavDestination navigate(Destination destination, Bundle args, NavOptions navOptions, Navigator.Extras navigatorExtras) {
        if (this.mFragmentManager.isStateSaved()) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already saved its state");
            return null;
        }
        String className = destination.getClassName();
        if (className.charAt(0) == '.') {
            className = this.mContext.getPackageName() + className;
        }
        Fragment frag = this.mFragmentManager.getFragmentFactory().instantiate(this.mContext.getClassLoader(), className);
        if (!DialogFragment.class.isAssignableFrom(frag.getClass())) {
            throw new IllegalArgumentException("Dialog destination " + destination.getClassName() + " is not an instance of DialogFragment");
        }
        DialogFragment dialogFragment = (DialogFragment) frag;
        dialogFragment.setArguments(args);
        dialogFragment.getLifecycle().addObserver(this.mObserver);
        FragmentManager fragmentManager = this.mFragmentManager;
        StringBuilder sb = new StringBuilder();
        sb.append(DIALOG_TAG);
        int i = this.mDialogCount;
        this.mDialogCount = i + 1;
        sb.append(i);
        dialogFragment.show(fragmentManager, sb.toString());
        return destination;
    }

    @Override // androidx.navigation.Navigator
    public Bundle onSaveState() {
        if (this.mDialogCount == 0) {
            return null;
        }
        Bundle b = new Bundle();
        b.putInt(KEY_DIALOG_COUNT, this.mDialogCount);
        return b;
    }

    @Override // androidx.navigation.Navigator
    public void onRestoreState(Bundle savedState) {
        if (savedState != null) {
            this.mDialogCount = savedState.getInt(KEY_DIALOG_COUNT, 0);
            for (int index = 0; index < this.mDialogCount; index++) {
                DialogFragment fragment = (DialogFragment) this.mFragmentManager.findFragmentByTag(DIALOG_TAG + index);
                if (fragment != null) {
                    fragment.getLifecycle().addObserver(this.mObserver);
                } else {
                    throw new IllegalStateException("DialogFragment " + index + " doesn't exist in the FragmentManager");
                }
            }
        }
    }

    public static class Destination extends NavDestination implements FloatingWindow {
        private String mClassName;

        public Destination(NavigatorProvider navigatorProvider) {
            this((Navigator<? extends Destination>) navigatorProvider.getNavigator(DialogFragmentNavigator.class));
        }

        public Destination(Navigator<? extends Destination> fragmentNavigator) {
            super(fragmentNavigator);
        }

        @Override // androidx.navigation.NavDestination
        public void onInflate(Context context, AttributeSet attrs) {
            super.onInflate(context, attrs);
            TypedArray a = context.getResources().obtainAttributes(attrs, R.styleable.DialogFragmentNavigator);
            String className = a.getString(R.styleable.DialogFragmentNavigator_android_name);
            if (className != null) {
                setClassName(className);
            }
            a.recycle();
        }

        public final Destination setClassName(String className) {
            this.mClassName = className;
            return this;
        }

        public final String getClassName() {
            String str = this.mClassName;
            if (str == null) {
                throw new IllegalStateException("DialogFragment class was not set");
            }
            return str;
        }
    }
}
