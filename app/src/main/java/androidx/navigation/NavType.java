package androidx.navigation;

import android.os.Bundle;
import android.os.Parcelable;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import java.io.Serializable;

/* loaded from: classes.dex */
public abstract class NavType<T> {
    public static final NavType<boolean[]> BoolArrayType;
    public static final NavType<Boolean> BoolType;
    public static final NavType<float[]> FloatArrayType;
    public static final NavType<Float> FloatType;
    public static final NavType<int[]> IntArrayType;
    public static final NavType<Integer> IntType;
    public static final NavType<long[]> LongArrayType;
    public static final NavType<Long> LongType;
    public static final NavType<Integer> ReferenceType;
    public static final NavType<String[]> StringArrayType;
    public static final NavType<String> StringType;
    private final boolean mNullableAllowed;

    public abstract T get(Bundle bundle, String str);

    public abstract String getName();

    public abstract T parseValue(String str);

    public abstract void put(Bundle bundle, String str, T t);

    NavType(boolean nullableAllowed) {
        this.mNullableAllowed = nullableAllowed;
    }

    public boolean isNullableAllowed() {
        return this.mNullableAllowed;
    }

    T parseAndPut(Bundle bundle, String key, String value) {
        T parsedValue = parseValue(value);
        put(bundle, key, parsedValue);
        return parsedValue;
    }

    public String toString() {
        return getName();
    }

    public static NavType<?> fromArgType(String type, String packageName) {
        String className;
        NavType<Integer> navType = IntType;
        if (navType.getName().equals(type)) {
            return navType;
        }
        NavType navType2 = IntArrayType;
        if (navType2.getName().equals(type)) {
            return navType2;
        }
        NavType<Long> navType3 = LongType;
        if (navType3.getName().equals(type)) {
            return navType3;
        }
        NavType navType4 = LongArrayType;
        if (navType4.getName().equals(type)) {
            return navType4;
        }
        NavType<Boolean> navType5 = BoolType;
        if (navType5.getName().equals(type)) {
            return navType5;
        }
        NavType navType6 = BoolArrayType;
        if (navType6.getName().equals(type)) {
            return navType6;
        }
        NavType<String> navType7 = StringType;
        if (navType7.getName().equals(type)) {
            return navType7;
        }
        NavType navType8 = StringArrayType;
        if (navType8.getName().equals(type)) {
            return navType8;
        }
        NavType<Float> navType9 = FloatType;
        if (navType9.getName().equals(type)) {
            return navType9;
        }
        NavType navType10 = FloatArrayType;
        if (navType10.getName().equals(type)) {
            return navType10;
        }
        NavType<Integer> navType11 = ReferenceType;
        if (navType11.getName().equals(type)) {
            return navType11;
        }
        if (type != null && !type.isEmpty()) {
            try {
                if (type.startsWith(".") && packageName != null) {
                    className = packageName + type;
                } else {
                    className = type;
                }
                if (type.endsWith("[]")) {
                    className = className.substring(0, className.length() - 2);
                    Class<?> clazz = Class.forName(className);
                    if (Parcelable.class.isAssignableFrom(clazz)) {
                        return new ParcelableArrayType(clazz);
                    }
                    if (Serializable.class.isAssignableFrom(clazz)) {
                        return new SerializableArrayType(clazz);
                    }
                } else {
                    Class<?> clazz2 = Class.forName(className);
                    if (Parcelable.class.isAssignableFrom(clazz2)) {
                        return new ParcelableType(clazz2);
                    }
                    if (Enum.class.isAssignableFrom(clazz2)) {
                        return new EnumType(clazz2);
                    }
                    if (Serializable.class.isAssignableFrom(clazz2)) {
                        return new SerializableType(clazz2);
                    }
                }
                throw new IllegalArgumentException(className + " is not Serializable or Parcelable.");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return navType7;
    }

    static NavType inferFromValue(String value) {
        try {
            NavType<Integer> navType = IntType;
            navType.parseValue(value);
            return navType;
        } catch (IllegalArgumentException e) {
            try {
                NavType<Long> navType2 = LongType;
                navType2.parseValue(value);
                return navType2;
            } catch (IllegalArgumentException e2) {
                try {
                    NavType<Float> navType3 = FloatType;
                    navType3.parseValue(value);
                    return navType3;
                } catch (IllegalArgumentException e3) {
                    try {
                        NavType<Boolean> navType4 = BoolType;
                        navType4.parseValue(value);
                        return navType4;
                    } catch (IllegalArgumentException e4) {
                        return StringType;
                    }
                }
            }
        }
    }

    static NavType inferFromValueType(Object value) {
        if (value instanceof Integer) {
            return IntType;
        }
        if (value instanceof int[]) {
            return IntArrayType;
        }
        if (value instanceof Long) {
            return LongType;
        }
        if (value instanceof long[]) {
            return LongArrayType;
        }
        if (value instanceof Float) {
            return FloatType;
        }
        if (value instanceof float[]) {
            return FloatArrayType;
        }
        if (value instanceof Boolean) {
            return BoolType;
        }
        if (value instanceof boolean[]) {
            return BoolArrayType;
        }
        if ((value instanceof String) || value == null) {
            return StringType;
        }
        if (value instanceof String[]) {
            return StringArrayType;
        }
        if (value.getClass().isArray() && Parcelable.class.isAssignableFrom(value.getClass().getComponentType())) {
            return new ParcelableArrayType(value.getClass().getComponentType());
        }
        if (value.getClass().isArray() && Serializable.class.isAssignableFrom(value.getClass().getComponentType())) {
            return new SerializableArrayType(value.getClass().getComponentType());
        }
        if (value instanceof Parcelable) {
            return new ParcelableType(value.getClass());
        }
        if (value instanceof Enum) {
            return new EnumType(value.getClass());
        }
        if (value instanceof Serializable) {
            return new SerializableType(value.getClass());
        }
        throw new IllegalArgumentException("Object of type " + value.getClass().getName() + " is not supported for navigation arguments.");
    }

    static {
        boolean z = false;
        IntType = new NavType<Integer>(z) { // from class: androidx.navigation.NavType.1
            @Override // androidx.navigation.NavType
            public void put(Bundle bundle, String key, Integer value) {
                bundle.putInt(key, value.intValue());
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // androidx.navigation.NavType
            public Integer get(Bundle bundle, String key) {
                return (Integer) bundle.get(key);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // androidx.navigation.NavType
            public Integer parseValue(String value) {
                if (value.startsWith("0x")) {
                    return Integer.valueOf(Integer.parseInt(value.substring(2), 16));
                }
                return Integer.valueOf(Integer.parseInt(value));
            }

            @Override // androidx.navigation.NavType
            public String getName() {
                return TypedValues.Custom.S_INT;
            }
        };
        ReferenceType = new NavType<Integer>(z) { // from class: androidx.navigation.NavType.2
            @Override // androidx.navigation.NavType
            public void put(Bundle bundle, String key, Integer value) {
                bundle.putInt(key, value.intValue());
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // androidx.navigation.NavType
            public Integer get(Bundle bundle, String key) {
                return (Integer) bundle.get(key);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // androidx.navigation.NavType
            public Integer parseValue(String value) {
                throw new UnsupportedOperationException("References don't support parsing string values.");
            }

            @Override // androidx.navigation.NavType
            public String getName() {
                return "reference";
            }
        };
        boolean z2 = true;
        IntArrayType = new NavType<int[]>(z2) { // from class: androidx.navigation.NavType.3
            @Override // androidx.navigation.NavType
            public void put(Bundle bundle, String key, int[] value) {
                bundle.putIntArray(key, value);
            }

            @Override // androidx.navigation.NavType
            public int[] get(Bundle bundle, String key) {
                return (int[]) bundle.get(key);
            }

            @Override // androidx.navigation.NavType
            public int[] parseValue(String value) {
                throw new UnsupportedOperationException("Arrays don't support default values.");
            }

            @Override // androidx.navigation.NavType
            public String getName() {
                return "integer[]";
            }
        };
        LongType = new NavType<Long>(z) { // from class: androidx.navigation.NavType.4
            @Override // androidx.navigation.NavType
            public void put(Bundle bundle, String key, Long value) {
                bundle.putLong(key, value.longValue());
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // androidx.navigation.NavType
            public Long get(Bundle bundle, String key) {
                return (Long) bundle.get(key);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // androidx.navigation.NavType
            public Long parseValue(String value) {
                if (value.endsWith("L")) {
                    value = value.substring(0, value.length() - 1);
                }
                if (value.startsWith("0x")) {
                    return Long.valueOf(Long.parseLong(value.substring(2), 16));
                }
                return Long.valueOf(Long.parseLong(value));
            }

            @Override // androidx.navigation.NavType
            public String getName() {
                return "long";
            }
        };
        LongArrayType = new NavType<long[]>(z2) { // from class: androidx.navigation.NavType.5
            @Override // androidx.navigation.NavType
            public void put(Bundle bundle, String key, long[] value) {
                bundle.putLongArray(key, value);
            }

            @Override // androidx.navigation.NavType
            public long[] get(Bundle bundle, String key) {
                return (long[]) bundle.get(key);
            }

            @Override // androidx.navigation.NavType
            public long[] parseValue(String value) {
                throw new UnsupportedOperationException("Arrays don't support default values.");
            }

            @Override // androidx.navigation.NavType
            public String getName() {
                return "long[]";
            }
        };
        FloatType = new NavType<Float>(z) { // from class: androidx.navigation.NavType.6
            @Override // androidx.navigation.NavType
            public void put(Bundle bundle, String key, Float value) {
                bundle.putFloat(key, value.floatValue());
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // androidx.navigation.NavType
            public Float get(Bundle bundle, String key) {
                return (Float) bundle.get(key);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // androidx.navigation.NavType
            public Float parseValue(String value) {
                return Float.valueOf(Float.parseFloat(value));
            }

            @Override // androidx.navigation.NavType
            public String getName() {
                return TypedValues.Custom.S_FLOAT;
            }
        };
        FloatArrayType = new NavType<float[]>(z2) { // from class: androidx.navigation.NavType.7
            @Override // androidx.navigation.NavType
            public void put(Bundle bundle, String key, float[] value) {
                bundle.putFloatArray(key, value);
            }

            @Override // androidx.navigation.NavType
            public float[] get(Bundle bundle, String key) {
                return (float[]) bundle.get(key);
            }

            @Override // androidx.navigation.NavType
            public float[] parseValue(String value) {
                throw new UnsupportedOperationException("Arrays don't support default values.");
            }

            @Override // androidx.navigation.NavType
            public String getName() {
                return "float[]";
            }
        };
        BoolType = new NavType<Boolean>(z) { // from class: androidx.navigation.NavType.8
            @Override // androidx.navigation.NavType
            public void put(Bundle bundle, String key, Boolean value) {
                bundle.putBoolean(key, value.booleanValue());
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // androidx.navigation.NavType
            public Boolean get(Bundle bundle, String key) {
                return (Boolean) bundle.get(key);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // androidx.navigation.NavType
            public Boolean parseValue(String value) {
                if ("true".equals(value)) {
                    return true;
                }
                if ("false".equals(value)) {
                    return false;
                }
                throw new IllegalArgumentException("A boolean NavType only accepts \"true\" or \"false\" values.");
            }

            @Override // androidx.navigation.NavType
            public String getName() {
                return TypedValues.Custom.S_BOOLEAN;
            }
        };
        BoolArrayType = new NavType<boolean[]>(z2) { // from class: androidx.navigation.NavType.9
            @Override // androidx.navigation.NavType
            public void put(Bundle bundle, String key, boolean[] value) {
                bundle.putBooleanArray(key, value);
            }

            @Override // androidx.navigation.NavType
            public boolean[] get(Bundle bundle, String key) {
                return (boolean[]) bundle.get(key);
            }

            @Override // androidx.navigation.NavType
            public boolean[] parseValue(String value) {
                throw new UnsupportedOperationException("Arrays don't support default values.");
            }

            @Override // androidx.navigation.NavType
            public String getName() {
                return "boolean[]";
            }
        };
        StringType = new NavType<String>(z2) { // from class: androidx.navigation.NavType.10
            @Override // androidx.navigation.NavType
            public void put(Bundle bundle, String key, String value) {
                bundle.putString(key, value);
            }

            @Override // androidx.navigation.NavType
            public String get(Bundle bundle, String key) {
                return (String) bundle.get(key);
            }

            @Override // androidx.navigation.NavType
            public String parseValue(String value) {
                return value;
            }

            @Override // androidx.navigation.NavType
            public String getName() {
                return TypedValues.Custom.S_STRING;
            }
        };
        StringArrayType = new NavType<String[]>(z2) { // from class: androidx.navigation.NavType.11
            @Override // androidx.navigation.NavType
            public void put(Bundle bundle, String key, String[] value) {
                bundle.putStringArray(key, value);
            }

            @Override // androidx.navigation.NavType
            public String[] get(Bundle bundle, String key) {
                return (String[]) bundle.get(key);
            }

            @Override // androidx.navigation.NavType
            public String[] parseValue(String value) {
                throw new UnsupportedOperationException("Arrays don't support default values.");
            }

            @Override // androidx.navigation.NavType
            public String getName() {
                return "string[]";
            }
        };
    }

    public static final class ParcelableType<D> extends NavType<D> {
        private final Class<D> mType;

        public ParcelableType(Class<D> type) {
            super(true);
            if (!Parcelable.class.isAssignableFrom(type) && !Serializable.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException(type + " does not implement Parcelable or Serializable.");
            }
            this.mType = type;
        }

        @Override // androidx.navigation.NavType
        public void put(Bundle bundle, String key, D value) {
            this.mType.cast(value);
            if (value == null || (value instanceof Parcelable)) {
                bundle.putParcelable(key, (Parcelable) value);
            } else if (value instanceof Serializable) {
                bundle.putSerializable(key, (Serializable) value);
            }
        }

        @Override // androidx.navigation.NavType
        public D get(Bundle bundle, String str) {
            return (D) bundle.get(str);
        }

        @Override // androidx.navigation.NavType
        public D parseValue(String value) {
            throw new UnsupportedOperationException("Parcelables don't support default values.");
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return this.mType.getName();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ParcelableType<?> that = (ParcelableType) o;
            return this.mType.equals(that.mType);
        }

        public int hashCode() {
            return this.mType.hashCode();
        }
    }

    public static final class ParcelableArrayType<D extends Parcelable> extends NavType<D[]> {
        private final Class<D[]> mArrayType;

        public ParcelableArrayType(Class<D> cls) {
            super(true);
            if (!Parcelable.class.isAssignableFrom(cls)) {
                throw new IllegalArgumentException(cls + " does not implement Parcelable.");
            }
            try {
                this.mArrayType = (Class<D[]>) Class.forName("[L" + cls.getName() + ";");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        @Override // androidx.navigation.NavType
        public void put(Bundle bundle, String key, D[] value) {
            this.mArrayType.cast(value);
            bundle.putParcelableArray(key, value);
        }

        @Override // androidx.navigation.NavType
        public D[] get(Bundle bundle, String str) {
            return (D[]) ((Parcelable[]) bundle.get(str));
        }

        @Override // androidx.navigation.NavType
        public D[] parseValue(String value) {
            throw new UnsupportedOperationException("Arrays don't support default values.");
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return this.mArrayType.getName();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ParcelableArrayType<?> that = (ParcelableArrayType) o;
            return this.mArrayType.equals(that.mArrayType);
        }

        public int hashCode() {
            return this.mArrayType.hashCode();
        }
    }

    public static class SerializableType<D extends Serializable> extends NavType<D> {
        private final Class<D> mType;

        public SerializableType(Class<D> type) {
            super(true);
            if (!Serializable.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException(type + " does not implement Serializable.");
            }
            if (type.isEnum()) {
                throw new IllegalArgumentException(type + " is an Enum. You should use EnumType instead.");
            }
            this.mType = type;
        }

        SerializableType(boolean nullableAllowed, Class<D> type) {
            super(nullableAllowed);
            if (!Serializable.class.isAssignableFrom(type)) {
                throw new IllegalArgumentException(type + " does not implement Serializable.");
            }
            this.mType = type;
        }

        @Override // androidx.navigation.NavType
        public void put(Bundle bundle, String key, D value) {
            this.mType.cast(value);
            bundle.putSerializable(key, value);
        }

        @Override // androidx.navigation.NavType
        public D get(Bundle bundle, String key) {
            return (D) bundle.get(key);
        }

        @Override // androidx.navigation.NavType
        public D parseValue(String value) {
            throw new UnsupportedOperationException("Serializables don't support default values.");
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return this.mType.getName();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SerializableType)) {
                return false;
            }
            SerializableType<?> that = (SerializableType) o;
            return this.mType.equals(that.mType);
        }

        public int hashCode() {
            return this.mType.hashCode();
        }
    }

    public static final class EnumType<D extends Enum> extends SerializableType<D> {
        private final Class<D> mType;

        public EnumType(Class<D> type) {
            super(false, type);
            if (!type.isEnum()) {
                throw new IllegalArgumentException(type + " is not an Enum type.");
            }
            this.mType = type;
        }

        @Override // androidx.navigation.NavType.SerializableType, androidx.navigation.NavType
        public D parseValue(String value) {
            for (Object constant : (Enum[]) this.mType.getEnumConstants()) {
                if (((Enum) constant).name().equals(value)) {
                    return (Enum) constant;
                }
            }
            throw new IllegalArgumentException("Enum value " + value + " not found for type " + this.mType.getName() + ".");
        }

        @Override // androidx.navigation.NavType.SerializableType, androidx.navigation.NavType
        public String getName() {
            return this.mType.getName();
        }
    }

    public static final class SerializableArrayType<D extends Serializable> extends NavType<D[]> {
        private final Class<D[]> mArrayType;

        public SerializableArrayType(Class<D> cls) {
            super(true);
            if (!Serializable.class.isAssignableFrom(cls)) {
                throw new IllegalArgumentException(cls + " does not implement Serializable.");
            }
            try {
                this.mArrayType = (Class<D[]>) Class.forName("[L" + cls.getName() + ";");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // androidx.navigation.NavType
        public void put(Bundle bundle, String key, D[] dArr) {
            this.mArrayType.cast(dArr);
            bundle.putSerializable(key, dArr);
        }

        @Override // androidx.navigation.NavType
        public D[] get(Bundle bundle, String str) {
            return (D[]) ((Serializable[]) bundle.get(str));
        }

        @Override // androidx.navigation.NavType
        public D[] parseValue(String value) {
            throw new UnsupportedOperationException("Arrays don't support default values.");
        }

        @Override // androidx.navigation.NavType
        public String getName() {
            return this.mArrayType.getName();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            SerializableArrayType<?> that = (SerializableArrayType) o;
            return this.mArrayType.equals(that.mArrayType);
        }

        public int hashCode() {
            return this.mArrayType.hashCode();
        }
    }
}
