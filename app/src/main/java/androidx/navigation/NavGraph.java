package androidx.navigation;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import androidx.collection.SparseArrayCompat;
import androidx.navigation.NavDestination;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/* loaded from: classes.dex */
public class NavGraph extends NavDestination implements Iterable<NavDestination> {
    final SparseArrayCompat<NavDestination> mNodes;
    private int mStartDestId;
    private String mStartDestIdName;

    public NavGraph(Navigator<? extends NavGraph> navGraphNavigator) {
        super(navGraphNavigator);
        this.mNodes = new SparseArrayCompat<>();
    }

    @Override // androidx.navigation.NavDestination
    public void onInflate(Context context, AttributeSet attrs) {
        super.onInflate(context, attrs);
        TypedArray a = context.getResources().obtainAttributes(attrs, androidx.navigation.common.R.styleable.NavGraphNavigator);
        setStartDestination(a.getResourceId(androidx.navigation.common.R.styleable.NavGraphNavigator_startDestination, 0));
        this.mStartDestIdName = getDisplayName(context, this.mStartDestId);
        a.recycle();
    }

    @Override // androidx.navigation.NavDestination
    NavDestination.DeepLinkMatch matchDeepLink(Uri uri) {
        NavDestination.DeepLinkMatch bestMatch = super.matchDeepLink(uri);
        Iterator<NavDestination> it = iterator();
        while (it.hasNext()) {
            NavDestination child = it.next();
            NavDestination.DeepLinkMatch childBestMatch = child.matchDeepLink(uri);
            if (childBestMatch != null && (bestMatch == null || childBestMatch.compareTo(bestMatch) > 0)) {
                bestMatch = childBestMatch;
            }
        }
        return bestMatch;
    }

    public final void addDestination(NavDestination node) {
        if (node.getId() == 0) {
            throw new IllegalArgumentException("Destinations must have an id. Call setId() or include an android:id in your navigation XML.");
        }
        NavDestination existingDestination = this.mNodes.get(node.getId());
        if (existingDestination == node) {
            return;
        }
        if (node.getParent() != null) {
            throw new IllegalStateException("Destination already has a parent set. Call NavGraph.remove() to remove the previous parent.");
        }
        if (existingDestination != null) {
            existingDestination.setParent(null);
        }
        node.setParent(this);
        this.mNodes.put(node.getId(), node);
    }

    public final void addDestinations(Collection<NavDestination> nodes) {
        for (NavDestination node : nodes) {
            if (node != null) {
                addDestination(node);
            }
        }
    }

    public final void addDestinations(NavDestination... nodes) {
        for (NavDestination node : nodes) {
            if (node != null) {
                addDestination(node);
            }
        }
    }

    public final NavDestination findNode(int resid) {
        return findNode(resid, true);
    }

    final NavDestination findNode(int resid, boolean searchParents) {
        NavDestination destination = this.mNodes.get(resid);
        if (destination != null) {
            return destination;
        }
        if (!searchParents || getParent() == null) {
            return null;
        }
        return getParent().findNode(resid);
    }

    @Override // java.lang.Iterable
    public final Iterator<NavDestination> iterator() {
        return new Iterator<NavDestination>() { // from class: androidx.navigation.NavGraph.1
            private int mIndex = -1;
            private boolean mWentToNext = false;

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.mIndex + 1 < NavGraph.this.mNodes.size();
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.util.Iterator
            public NavDestination next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                this.mWentToNext = true;
                SparseArrayCompat<NavDestination> sparseArrayCompat = NavGraph.this.mNodes;
                int i = this.mIndex + 1;
                this.mIndex = i;
                return sparseArrayCompat.valueAt(i);
            }

            @Override // java.util.Iterator
            public void remove() {
                if (!this.mWentToNext) {
                    throw new IllegalStateException("You must call next() before you can remove an element");
                }
                NavGraph.this.mNodes.valueAt(this.mIndex).setParent(null);
                NavGraph.this.mNodes.removeAt(this.mIndex);
                this.mIndex--;
                this.mWentToNext = false;
            }
        };
    }

    public final void addAll(NavGraph other) {
        Iterator<NavDestination> iterator = other.iterator();
        while (iterator.hasNext()) {
            NavDestination destination = iterator.next();
            iterator.remove();
            addDestination(destination);
        }
    }

    public final void remove(NavDestination node) {
        int index = this.mNodes.indexOfKey(node.getId());
        if (index >= 0) {
            this.mNodes.valueAt(index).setParent(null);
            this.mNodes.removeAt(index);
        }
    }

    public final void clear() {
        Iterator<NavDestination> iterator = iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    @Override // androidx.navigation.NavDestination
    String getDisplayName() {
        return getId() != 0 ? super.getDisplayName() : "the root navigation";
    }

    public final int getStartDestination() {
        return this.mStartDestId;
    }

    public final void setStartDestination(int startDestId) {
        this.mStartDestId = startDestId;
        this.mStartDestIdName = null;
    }

    String getStartDestDisplayName() {
        if (this.mStartDestIdName == null) {
            this.mStartDestIdName = Integer.toString(this.mStartDestId);
        }
        return this.mStartDestIdName;
    }

    @Override // androidx.navigation.NavDestination
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        sb.append(" startDestination=");
        NavDestination startDestination = findNode(getStartDestination());
        if (startDestination == null) {
            String str = this.mStartDestIdName;
            if (str == null) {
                sb.append("0x");
                sb.append(Integer.toHexString(this.mStartDestId));
            } else {
                sb.append(str);
            }
        } else {
            sb.append("{");
            sb.append(startDestination.toString());
            sb.append("}");
        }
        return sb.toString();
    }
}
