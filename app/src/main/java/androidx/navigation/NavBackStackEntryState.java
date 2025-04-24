package androidx.navigation;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import java.util.UUID;

/* loaded from: classes.dex */
final class NavBackStackEntryState implements Parcelable {
    public static final Parcelable.Creator<NavBackStackEntryState> CREATOR = new Parcelable.Creator<NavBackStackEntryState>() { // from class: androidx.navigation.NavBackStackEntryState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NavBackStackEntryState createFromParcel(Parcel in) {
            return new NavBackStackEntryState(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public NavBackStackEntryState[] newArray(int size) {
            return new NavBackStackEntryState[size];
        }
    };
    private final Bundle mArgs;
    private final int mDestinationId;
    private final Bundle mSavedState;
    private final UUID mUUID;

    NavBackStackEntryState(NavBackStackEntry entry) {
        this.mUUID = entry.mId;
        this.mDestinationId = entry.getDestination().getId();
        this.mArgs = entry.getArguments();
        Bundle bundle = new Bundle();
        this.mSavedState = bundle;
        entry.saveState(bundle);
    }

    NavBackStackEntryState(Parcel in) {
        this.mUUID = UUID.fromString(in.readString());
        this.mDestinationId = in.readInt();
        this.mArgs = in.readBundle(getClass().getClassLoader());
        this.mSavedState = in.readBundle(getClass().getClassLoader());
    }

    UUID getUUID() {
        return this.mUUID;
    }

    int getDestinationId() {
        return this.mDestinationId;
    }

    Bundle getArgs() {
        return this.mArgs;
    }

    Bundle getSavedState() {
        return this.mSavedState;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mUUID.toString());
        parcel.writeInt(this.mDestinationId);
        parcel.writeBundle(this.mArgs);
        parcel.writeBundle(this.mSavedState);
    }
}
