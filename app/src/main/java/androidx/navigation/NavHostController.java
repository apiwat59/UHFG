package androidx.navigation;

import android.content.Context;
import androidx.activity.OnBackPressedDispatcher;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelStore;

/* loaded from: classes.dex */
public final class NavHostController extends NavController {
    public NavHostController(Context context) {
        super(context);
    }

    @Override // androidx.navigation.NavController
    public void setLifecycleOwner(LifecycleOwner owner) {
        super.setLifecycleOwner(owner);
    }

    @Override // androidx.navigation.NavController
    public void setOnBackPressedDispatcher(OnBackPressedDispatcher dispatcher) {
        super.setOnBackPressedDispatcher(dispatcher);
    }

    @Override // androidx.navigation.NavController
    public void enableOnBackPressed(boolean enabled) {
        super.enableOnBackPressed(enabled);
    }

    @Override // androidx.navigation.NavController
    public void setViewModelStore(ViewModelStore viewModelStore) {
        super.setViewModelStore(viewModelStore);
    }
}
