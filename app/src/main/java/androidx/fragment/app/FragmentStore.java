package androidx.fragment.app;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.spi.Configurator;

/* loaded from: classes.dex */
class FragmentStore {
    private static final String TAG = "FragmentManager";
    private final ArrayList<Fragment> mAdded = new ArrayList<>();
    private final HashMap<String, FragmentStateManager> mActive = new HashMap<>();

    FragmentStore() {
    }

    void resetActiveFragments() {
        this.mActive.clear();
    }

    void restoreAddedFragments(List<String> added) {
        this.mAdded.clear();
        if (added != null) {
            for (String who : added) {
                Fragment f = findActiveFragment(who);
                if (f == null) {
                    throw new IllegalStateException("No instantiated fragment for (" + who + ")");
                }
                if (FragmentManager.isLoggingEnabled(2)) {
                    Log.v(TAG, "restoreSaveState: added (" + who + "): " + f);
                }
                addFragment(f);
            }
        }
    }

    void makeActive(FragmentStateManager newlyActive) {
        this.mActive.put(newlyActive.getFragment().mWho, newlyActive);
    }

    void addFragment(Fragment fragment) {
        if (this.mAdded.contains(fragment)) {
            throw new IllegalStateException("Fragment already added: " + fragment);
        }
        synchronized (this.mAdded) {
            this.mAdded.add(fragment);
        }
        fragment.mAdded = true;
    }

    void dispatchStateChange(int state) {
        Iterator<Fragment> it = this.mAdded.iterator();
        while (it.hasNext()) {
            Fragment f = it.next();
            FragmentStateManager fragmentStateManager = this.mActive.get(f.mWho);
            if (fragmentStateManager != null) {
                fragmentStateManager.setFragmentManagerState(state);
            }
        }
        for (FragmentStateManager fragmentStateManager2 : this.mActive.values()) {
            if (fragmentStateManager2 != null) {
                fragmentStateManager2.setFragmentManagerState(state);
            }
        }
    }

    void removeFragment(Fragment fragment) {
        synchronized (this.mAdded) {
            this.mAdded.remove(fragment);
        }
        fragment.mAdded = false;
    }

    void makeInactive(FragmentStateManager newlyInactive) {
        Fragment f = newlyInactive.getFragment();
        for (FragmentStateManager fragmentStateManager : this.mActive.values()) {
            if (fragmentStateManager != null) {
                Fragment fragment = fragmentStateManager.getFragment();
                if (f.mWho.equals(fragment.mTargetWho)) {
                    fragment.mTarget = f;
                    fragment.mTargetWho = null;
                }
            }
        }
        this.mActive.put(f.mWho, null);
        if (f.mTargetWho != null) {
            f.mTarget = findActiveFragment(f.mTargetWho);
        }
    }

    void burpActive() {
        Collection<FragmentStateManager> values = this.mActive.values();
        values.removeAll(Collections.singleton(null));
    }

    ArrayList<FragmentState> saveActiveFragments() {
        ArrayList<FragmentState> active = new ArrayList<>(this.mActive.size());
        for (FragmentStateManager fragmentStateManager : this.mActive.values()) {
            if (fragmentStateManager != null) {
                Fragment f = fragmentStateManager.getFragment();
                FragmentState fs = fragmentStateManager.saveState();
                active.add(fs);
                if (FragmentManager.isLoggingEnabled(2)) {
                    Log.v(TAG, "Saved state of " + f + ": " + fs.mSavedFragmentState);
                }
            }
        }
        return active;
    }

    ArrayList<String> saveAddedFragments() {
        synchronized (this.mAdded) {
            if (this.mAdded.isEmpty()) {
                return null;
            }
            ArrayList<String> added = new ArrayList<>(this.mAdded.size());
            Iterator<Fragment> it = this.mAdded.iterator();
            while (it.hasNext()) {
                Fragment f = it.next();
                added.add(f.mWho);
                if (FragmentManager.isLoggingEnabled(2)) {
                    Log.v(TAG, "saveAllState: adding fragment (" + f.mWho + "): " + f);
                }
            }
            return added;
        }
    }

    List<Fragment> getFragments() {
        ArrayList arrayList;
        if (this.mAdded.isEmpty()) {
            return Collections.emptyList();
        }
        synchronized (this.mAdded) {
            arrayList = new ArrayList(this.mAdded);
        }
        return arrayList;
    }

    List<Fragment> getActiveFragments() {
        ArrayList<Fragment> activeFragments = new ArrayList<>();
        for (FragmentStateManager fragmentStateManager : this.mActive.values()) {
            if (fragmentStateManager != null) {
                activeFragments.add(fragmentStateManager.getFragment());
            } else {
                activeFragments.add(null);
            }
        }
        return activeFragments;
    }

    int getActiveFragmentCount() {
        return this.mActive.size();
    }

    Fragment findFragmentById(int id) {
        for (int i = this.mAdded.size() - 1; i >= 0; i--) {
            Fragment f = this.mAdded.get(i);
            if (f != null && f.mFragmentId == id) {
                return f;
            }
        }
        for (FragmentStateManager fragmentStateManager : this.mActive.values()) {
            if (fragmentStateManager != null) {
                Fragment f2 = fragmentStateManager.getFragment();
                if (f2.mFragmentId == id) {
                    return f2;
                }
            }
        }
        return null;
    }

    Fragment findFragmentByTag(String tag) {
        if (tag != null) {
            for (int i = this.mAdded.size() - 1; i >= 0; i--) {
                Fragment f = this.mAdded.get(i);
                if (f != null && tag.equals(f.mTag)) {
                    return f;
                }
            }
        }
        if (tag != null) {
            for (FragmentStateManager fragmentStateManager : this.mActive.values()) {
                if (fragmentStateManager != null) {
                    Fragment f2 = fragmentStateManager.getFragment();
                    if (tag.equals(f2.mTag)) {
                        return f2;
                    }
                }
            }
            return null;
        }
        return null;
    }

    boolean containsActiveFragment(String who) {
        return this.mActive.containsKey(who);
    }

    FragmentStateManager getFragmentStateManager(String who) {
        return this.mActive.get(who);
    }

    Fragment findFragmentByWho(String who) {
        Fragment f;
        for (FragmentStateManager fragmentStateManager : this.mActive.values()) {
            if (fragmentStateManager != null && (f = fragmentStateManager.getFragment().findFragmentByWho(who)) != null) {
                return f;
            }
        }
        return null;
    }

    Fragment findActiveFragment(String who) {
        FragmentStateManager fragmentStateManager = this.mActive.get(who);
        if (fragmentStateManager != null) {
            return fragmentStateManager.getFragment();
        }
        return null;
    }

    Fragment findFragmentUnder(Fragment f) {
        ViewGroup container = f.mContainer;
        View view = f.mView;
        if (container == null || view == null) {
            return null;
        }
        int fragmentIndex = this.mAdded.indexOf(f);
        for (int i = fragmentIndex - 1; i >= 0; i--) {
            Fragment underFragment = this.mAdded.get(i);
            if (underFragment.mContainer == container && underFragment.mView != null) {
                return underFragment;
            }
        }
        return null;
    }

    void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        String innerPrefix = prefix + "    ";
        if (!this.mActive.isEmpty()) {
            writer.print(prefix);
            writer.print("Active Fragments:");
            for (FragmentStateManager fragmentStateManager : this.mActive.values()) {
                writer.print(prefix);
                if (fragmentStateManager != null) {
                    Fragment f = fragmentStateManager.getFragment();
                    writer.println(f);
                    f.dump(innerPrefix, fd, writer, args);
                } else {
                    writer.println(Configurator.NULL);
                }
            }
        }
        int count = this.mAdded.size();
        if (count > 0) {
            writer.print(prefix);
            writer.println("Added Fragments:");
            for (int i = 0; i < count; i++) {
                Fragment f2 = this.mAdded.get(i);
                writer.print(prefix);
                writer.print("  #");
                writer.print(i);
                writer.print(": ");
                writer.println(f2.toString());
            }
        }
    }
}
