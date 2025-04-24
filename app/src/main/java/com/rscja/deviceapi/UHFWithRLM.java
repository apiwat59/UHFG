package com.rscja.deviceapi;

@Deprecated
/* loaded from: classes.dex */
final class UHFWithRLM {

    public enum UHFCrcFlagEnum {
        NONUSE((byte) 0),
        USE((byte) 1);

        private final byte a;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static UHFCrcFlagEnum[] valuesCustom() {
            UHFCrcFlagEnum[] valuesCustom = values();
            int length = valuesCustom.length;
            UHFCrcFlagEnum[] uHFCrcFlagEnumArr = new UHFCrcFlagEnum[length];
            System.arraycopy(valuesCustom, 0, uHFCrcFlagEnumArr, 0, length);
            return uHFCrcFlagEnumArr;
        }

        public final byte getValue() {
            return this.a;
        }

        UHFCrcFlagEnum(byte value) {
            this.a = value;
        }
    }

    public enum LockModeEnum {
        HOLD((byte) 0),
        LOCK((byte) 1),
        UNLOCK((byte) 2),
        PLOCK((byte) 3),
        PUNLOCK((byte) 4);

        private final byte a;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static LockModeEnum[] valuesCustom() {
            LockModeEnum[] valuesCustom = values();
            int length = valuesCustom.length;
            LockModeEnum[] lockModeEnumArr = new LockModeEnum[length];
            System.arraycopy(valuesCustom, 0, lockModeEnumArr, 0, length);
            return lockModeEnumArr;
        }

        public final byte getValue() {
            return this.a;
        }

        LockModeEnum(byte value) {
            this.a = value;
        }
    }

    public enum BankEnum {
        RESERVED((byte) 0),
        UII((byte) 1),
        TID((byte) 2),
        USER((byte) 3);

        private final byte a;

        /* renamed from: values, reason: to resolve conflict with enum method */
        public static BankEnum[] valuesCustom() {
            BankEnum[] valuesCustom = values();
            int length = valuesCustom.length;
            BankEnum[] bankEnumArr = new BankEnum[length];
            System.arraycopy(valuesCustom, 0, bankEnumArr, 0, length);
            return bankEnumArr;
        }

        public final byte getValue() {
            return this.a;
        }

        BankEnum(byte value) {
            this.a = value;
        }
    }
}
