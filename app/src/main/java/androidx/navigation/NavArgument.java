package androidx.navigation;

import android.os.Bundle;

/* loaded from: classes.dex */
public final class NavArgument {
    private final Object mDefaultValue;
    private final boolean mDefaultValuePresent;
    private final boolean mIsNullable;
    private final NavType mType;

    NavArgument(NavType<?> type, boolean isNullable, Object defaultValue, boolean defaultValuePresent) {
        if (!type.isNullableAllowed() && isNullable) {
            throw new IllegalArgumentException(type.getName() + " does not allow nullable values");
        }
        if (!isNullable && defaultValuePresent && defaultValue == null) {
            throw new IllegalArgumentException("Argument with type " + type.getName() + " has null value but is not nullable.");
        }
        this.mType = type;
        this.mIsNullable = isNullable;
        this.mDefaultValue = defaultValue;
        this.mDefaultValuePresent = defaultValuePresent;
    }

    public boolean isDefaultValuePresent() {
        return this.mDefaultValuePresent;
    }

    public NavType<?> getType() {
        return this.mType;
    }

    public boolean isNullable() {
        return this.mIsNullable;
    }

    public Object getDefaultValue() {
        return this.mDefaultValue;
    }

    void putDefaultValue(String name, Bundle bundle) {
        if (this.mDefaultValuePresent) {
            this.mType.put(bundle, name, this.mDefaultValue);
        }
    }

    boolean verify(String name, Bundle bundle) {
        if (!this.mIsNullable && bundle.containsKey(name) && bundle.get(name) == null) {
            return false;
        }
        try {
            this.mType.get(bundle, name);
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NavArgument that = (NavArgument) o;
        if (this.mIsNullable != that.mIsNullable || this.mDefaultValuePresent != that.mDefaultValuePresent || !this.mType.equals(that.mType)) {
            return false;
        }
        Object obj = this.mDefaultValue;
        return obj != null ? obj.equals(that.mDefaultValue) : that.mDefaultValue == null;
    }

    public int hashCode() {
        int hashCode = ((((this.mType.hashCode() * 31) + (this.mIsNullable ? 1 : 0)) * 31) + (this.mDefaultValuePresent ? 1 : 0)) * 31;
        Object obj = this.mDefaultValue;
        return hashCode + (obj != null ? obj.hashCode() : 0);
    }

    public static final class Builder {
        private Object mDefaultValue;
        private NavType<?> mType;
        private boolean mIsNullable = false;
        private boolean mDefaultValuePresent = false;

        public Builder setType(NavType<?> type) {
            this.mType = type;
            return this;
        }

        public Builder setIsNullable(boolean isNullable) {
            this.mIsNullable = isNullable;
            return this;
        }

        public Builder setDefaultValue(Object defaultValue) {
            this.mDefaultValue = defaultValue;
            this.mDefaultValuePresent = true;
            return this;
        }

        public NavArgument build() {
            if (this.mType == null) {
                this.mType = NavType.inferFromValueType(this.mDefaultValue);
            }
            return new NavArgument(this.mType, this.mIsNullable, this.mDefaultValue, this.mDefaultValuePresent);
        }
    }
}
