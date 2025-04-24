package kotlin.text;

import androidx.core.internal.view.SupportMenu;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.UInt;
import kotlin.ULong;
import kotlin.UShort;
import kotlin.UnsignedKt;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: UStrings.kt */
@Metadata(bv = {1, 0, 3}, d1 = {"\u0000,\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0013\u001a\u001e\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000¢\u0006\u0004\b\u0005\u0010\u0006\u001a\u001e\u0010\u0000\u001a\u00020\u0001*\u00020\u00072\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000¢\u0006\u0004\b\b\u0010\t\u001a\u001e\u0010\u0000\u001a\u00020\u0001*\u00020\n2\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000¢\u0006\u0004\b\u000b\u0010\f\u001a\u001e\u0010\u0000\u001a\u00020\u0001*\u00020\r2\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000¢\u0006\u0004\b\u000e\u0010\u000f\u001a\u0014\u0010\u0010\u001a\u00020\u0002*\u00020\u0001H\u0007ø\u0001\u0000¢\u0006\u0002\u0010\u0011\u001a\u001c\u0010\u0010\u001a\u00020\u0002*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000¢\u0006\u0002\u0010\u0012\u001a\u0011\u0010\u0013\u001a\u0004\u0018\u00010\u0002*\u00020\u0001H\u0007ø\u0001\u0000\u001a\u0019\u0010\u0013\u001a\u0004\u0018\u00010\u0002*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000\u001a\u0014\u0010\u0014\u001a\u00020\u0007*\u00020\u0001H\u0007ø\u0001\u0000¢\u0006\u0002\u0010\u0015\u001a\u001c\u0010\u0014\u001a\u00020\u0007*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000¢\u0006\u0002\u0010\u0016\u001a\u0011\u0010\u0017\u001a\u0004\u0018\u00010\u0007*\u00020\u0001H\u0007ø\u0001\u0000\u001a\u0019\u0010\u0017\u001a\u0004\u0018\u00010\u0007*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000\u001a\u0014\u0010\u0018\u001a\u00020\n*\u00020\u0001H\u0007ø\u0001\u0000¢\u0006\u0002\u0010\u0019\u001a\u001c\u0010\u0018\u001a\u00020\n*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000¢\u0006\u0002\u0010\u001a\u001a\u0011\u0010\u001b\u001a\u0004\u0018\u00010\n*\u00020\u0001H\u0007ø\u0001\u0000\u001a\u0019\u0010\u001b\u001a\u0004\u0018\u00010\n*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000\u001a\u0014\u0010\u001c\u001a\u00020\r*\u00020\u0001H\u0007ø\u0001\u0000¢\u0006\u0002\u0010\u001d\u001a\u001c\u0010\u001c\u001a\u00020\r*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000¢\u0006\u0002\u0010\u001e\u001a\u0011\u0010\u001f\u001a\u0004\u0018\u00010\r*\u00020\u0001H\u0007ø\u0001\u0000\u001a\u0019\u0010\u001f\u001a\u0004\u0018\u00010\r*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006 "}, d2 = {"toString", "", "Lkotlin/UByte;", "radix", "", "toString-LxnNnR4", "(BI)Ljava/lang/String;", "Lkotlin/UInt;", "toString-V7xB4Y4", "(II)Ljava/lang/String;", "Lkotlin/ULong;", "toString-JSWoG40", "(JI)Ljava/lang/String;", "Lkotlin/UShort;", "toString-olVBNx4", "(SI)Ljava/lang/String;", "toUByte", "(Ljava/lang/String;)B", "(Ljava/lang/String;I)B", "toUByteOrNull", "toUInt", "(Ljava/lang/String;)I", "(Ljava/lang/String;I)I", "toUIntOrNull", "toULong", "(Ljava/lang/String;)J", "(Ljava/lang/String;I)J", "toULongOrNull", "toUShort", "(Ljava/lang/String;)S", "(Ljava/lang/String;I)S", "toUShortOrNull", "kotlin-stdlib"}, k = 2, mv = {1, 1, 15})
/* loaded from: classes.dex */
public final class UStringsKt {
    /* renamed from: toString-LxnNnR4, reason: not valid java name */
    public static final String m931toStringLxnNnR4(byte $this$toString, int radix) {
        String num = Integer.toString($this$toString & 255, CharsKt.checkRadix(radix));
        Intrinsics.checkExpressionValueIsNotNull(num, "java.lang.Integer.toStri…(this, checkRadix(radix))");
        return num;
    }

    /* renamed from: toString-olVBNx4, reason: not valid java name */
    public static final String m933toStringolVBNx4(short $this$toString, int radix) {
        String num = Integer.toString(65535 & $this$toString, CharsKt.checkRadix(radix));
        Intrinsics.checkExpressionValueIsNotNull(num, "java.lang.Integer.toStri…(this, checkRadix(radix))");
        return num;
    }

    /* renamed from: toString-V7xB4Y4, reason: not valid java name */
    public static final String m932toStringV7xB4Y4(int $this$toString, int radix) {
        String l = Long.toString($this$toString & 4294967295L, CharsKt.checkRadix(radix));
        Intrinsics.checkExpressionValueIsNotNull(l, "java.lang.Long.toString(this, checkRadix(radix))");
        return l;
    }

    /* renamed from: toString-JSWoG40, reason: not valid java name */
    public static final String m930toStringJSWoG40(long $this$toString, int radix) {
        return UnsignedKt.ulongToString($this$toString, CharsKt.checkRadix(radix));
    }

    public static final byte toUByte(String toUByte) {
        Intrinsics.checkParameterIsNotNull(toUByte, "$this$toUByte");
        UByte uByteOrNull = toUByteOrNull(toUByte);
        if (uByteOrNull != null) {
            return uByteOrNull.getData();
        }
        StringsKt.numberFormatError(toUByte);
        throw null;
    }

    public static final byte toUByte(String toUByte, int radix) {
        Intrinsics.checkParameterIsNotNull(toUByte, "$this$toUByte");
        UByte uByteOrNull = toUByteOrNull(toUByte, radix);
        if (uByteOrNull != null) {
            return uByteOrNull.getData();
        }
        StringsKt.numberFormatError(toUByte);
        throw null;
    }

    public static final short toUShort(String toUShort) {
        Intrinsics.checkParameterIsNotNull(toUShort, "$this$toUShort");
        UShort uShortOrNull = toUShortOrNull(toUShort);
        if (uShortOrNull != null) {
            return uShortOrNull.getData();
        }
        StringsKt.numberFormatError(toUShort);
        throw null;
    }

    public static final short toUShort(String toUShort, int radix) {
        Intrinsics.checkParameterIsNotNull(toUShort, "$this$toUShort");
        UShort uShortOrNull = toUShortOrNull(toUShort, radix);
        if (uShortOrNull != null) {
            return uShortOrNull.getData();
        }
        StringsKt.numberFormatError(toUShort);
        throw null;
    }

    public static final int toUInt(String toUInt) {
        Intrinsics.checkParameterIsNotNull(toUInt, "$this$toUInt");
        UInt uIntOrNull = toUIntOrNull(toUInt);
        if (uIntOrNull != null) {
            return uIntOrNull.getData();
        }
        StringsKt.numberFormatError(toUInt);
        throw null;
    }

    public static final int toUInt(String toUInt, int radix) {
        Intrinsics.checkParameterIsNotNull(toUInt, "$this$toUInt");
        UInt uIntOrNull = toUIntOrNull(toUInt, radix);
        if (uIntOrNull != null) {
            return uIntOrNull.getData();
        }
        StringsKt.numberFormatError(toUInt);
        throw null;
    }

    public static final long toULong(String toULong) {
        Intrinsics.checkParameterIsNotNull(toULong, "$this$toULong");
        ULong uLongOrNull = toULongOrNull(toULong);
        if (uLongOrNull != null) {
            return uLongOrNull.getData();
        }
        StringsKt.numberFormatError(toULong);
        throw null;
    }

    public static final long toULong(String toULong, int radix) {
        Intrinsics.checkParameterIsNotNull(toULong, "$this$toULong");
        ULong uLongOrNull = toULongOrNull(toULong, radix);
        if (uLongOrNull != null) {
            return uLongOrNull.getData();
        }
        StringsKt.numberFormatError(toULong);
        throw null;
    }

    public static final UByte toUByteOrNull(String toUByteOrNull) {
        Intrinsics.checkParameterIsNotNull(toUByteOrNull, "$this$toUByteOrNull");
        return toUByteOrNull(toUByteOrNull, 10);
    }

    public static final UByte toUByteOrNull(String toUByteOrNull, int radix) {
        Intrinsics.checkParameterIsNotNull(toUByteOrNull, "$this$toUByteOrNull");
        UInt uIntOrNull = toUIntOrNull(toUByteOrNull, radix);
        if (uIntOrNull == null) {
            return null;
        }
        int data = uIntOrNull.getData();
        if (UnsignedKt.uintCompare(data, UInt.m101constructorimpl(255)) > 0) {
            return null;
        }
        return UByte.m28boximpl(UByte.m34constructorimpl((byte) data));
    }

    public static final UShort toUShortOrNull(String toUShortOrNull) {
        Intrinsics.checkParameterIsNotNull(toUShortOrNull, "$this$toUShortOrNull");
        return toUShortOrNull(toUShortOrNull, 10);
    }

    public static final UShort toUShortOrNull(String toUShortOrNull, int radix) {
        Intrinsics.checkParameterIsNotNull(toUShortOrNull, "$this$toUShortOrNull");
        UInt uIntOrNull = toUIntOrNull(toUShortOrNull, radix);
        if (uIntOrNull == null) {
            return null;
        }
        int data = uIntOrNull.getData();
        if (UnsignedKt.uintCompare(data, UInt.m101constructorimpl(SupportMenu.USER_MASK)) > 0) {
            return null;
        }
        return UShort.m261boximpl(UShort.m267constructorimpl((short) data));
    }

    public static final UInt toUIntOrNull(String toUIntOrNull) {
        Intrinsics.checkParameterIsNotNull(toUIntOrNull, "$this$toUIntOrNull");
        return toUIntOrNull(toUIntOrNull, 10);
    }

    public static final UInt toUIntOrNull(String toUIntOrNull, int radix) {
        int start;
        Intrinsics.checkParameterIsNotNull(toUIntOrNull, "$this$toUIntOrNull");
        CharsKt.checkRadix(radix);
        int length = toUIntOrNull.length();
        if (length == 0) {
            return null;
        }
        char firstChar = toUIntOrNull.charAt(0);
        if (firstChar < '0') {
            if (length == 1 || firstChar != '+') {
                return null;
            }
            start = 1;
        } else {
            start = 0;
        }
        int uradix = UInt.m101constructorimpl(radix);
        int limitBeforeMul = UnsignedKt.m327uintDivideJ1ME1BU(-1, uradix);
        int result = 0;
        for (int i = start; i < length; i++) {
            int digit = CharsKt.digitOf(toUIntOrNull.charAt(i), radix);
            if (digit < 0 || UnsignedKt.uintCompare(result, limitBeforeMul) > 0) {
                return null;
            }
            int result2 = UInt.m101constructorimpl(result * uradix);
            result = UInt.m101constructorimpl(UInt.m101constructorimpl(digit) + result2);
            if (UnsignedKt.uintCompare(result, result2) < 0) {
                return null;
            }
        }
        return UInt.m95boximpl(result);
    }

    public static final ULong toULongOrNull(String toULongOrNull) {
        Intrinsics.checkParameterIsNotNull(toULongOrNull, "$this$toULongOrNull");
        return toULongOrNull(toULongOrNull, 10);
    }

    public static final ULong toULongOrNull(String $this$toULongOrNull, int radix) {
        int start;
        String toULongOrNull = $this$toULongOrNull;
        Intrinsics.checkParameterIsNotNull(toULongOrNull, "$this$toULongOrNull");
        CharsKt.checkRadix(radix);
        int length = $this$toULongOrNull.length();
        ULong uLong = null;
        if (length == 0) {
            return null;
        }
        long limit = -1;
        char firstChar = toULongOrNull.charAt(0);
        if (firstChar < '0') {
            if (length == 1 || firstChar != '+') {
                return null;
            }
            start = 1;
        } else {
            start = 0;
        }
        int uradix = UInt.m101constructorimpl(radix);
        long limitBeforeMul = UnsignedKt.m329ulongDivideeb3DHEI(-1L, ULong.m170constructorimpl(uradix & 4294967295L));
        long result = 0;
        int i = start;
        while (i < length) {
            int digit = CharsKt.digitOf(toULongOrNull.charAt(i), radix);
            if (digit < 0 || UnsignedKt.ulongCompare(result, limitBeforeMul) > 0) {
                return uLong;
            }
            long limit2 = limit;
            long result2 = ULong.m170constructorimpl(ULong.m170constructorimpl(uradix & 4294967295L) * result);
            int length2 = length;
            long result3 = ULong.m170constructorimpl(ULong.m170constructorimpl(UInt.m101constructorimpl(digit) & 4294967295L) + result2);
            if (UnsignedKt.ulongCompare(result3, result2) < 0) {
                return null;
            }
            uLong = null;
            i++;
            result = result3;
            limit = limit2;
            length = length2;
            toULongOrNull = $this$toULongOrNull;
        }
        return ULong.m164boximpl(result);
    }
}
