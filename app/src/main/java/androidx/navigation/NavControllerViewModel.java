package androidx.navigation;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

/* loaded from: classes.dex */
class NavControllerViewModel extends ViewModel {
    private static final ViewModelProvider.Factory FACTORY = new ViewModelProvider.Factory() { // from class: androidx.navigation.NavControllerViewModel.1
        @Override // androidx.lifecycle.ViewModelProvider.Factory
        public <T extends ViewModel> T create(Class<T> modelClass) {
            NavControllerViewModel viewModel = new NavControllerViewModel();
            return viewModel;
        }
    };
    private final HashMap<UUID, ViewModelStore> mViewModelStores = new HashMap<>();

    NavControllerViewModel() {
    }

    static NavControllerViewModel getInstance(ViewModelStore viewModelStore) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(viewModelStore, FACTORY);
        return (NavControllerViewModel) viewModelProvider.get(NavControllerViewModel.class);
    }

    void clear(UUID backStackEntryUUID) {
        ViewModelStore viewModelStore = this.mViewModelStores.remove(backStackEntryUUID);
        if (viewModelStore != null) {
            viewModelStore.clear();
        }
    }

    @Override // androidx.lifecycle.ViewModel
    protected void onCleared() {
        for (ViewModelStore store : this.mViewModelStores.values()) {
            store.clear();
        }
        this.mViewModelStores.clear();
    }

    ViewModelStore getViewModelStore(UUID backStackEntryUUID) {
        ViewModelStore viewModelStore = this.mViewModelStores.get(backStackEntryUUID);
        if (viewModelStore == null) {
            ViewModelStore viewModelStore2 = new ViewModelStore();
            this.mViewModelStores.put(backStackEntryUUID, viewModelStore2);
            return viewModelStore2;
        }
        return viewModelStore;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("NavControllerViewModel{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append("} ViewModelStores (");
        Iterator<UUID> viewModelStoreIterator = this.mViewModelStores.keySet().iterator();
        while (viewModelStoreIterator.hasNext()) {
            sb.append(viewModelStoreIterator.next());
            if (viewModelStoreIterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(')');
        return sb.toString();
    }
}
