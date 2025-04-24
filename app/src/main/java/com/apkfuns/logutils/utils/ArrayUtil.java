package com.apkfuns.logutils.utils;

import java.util.Arrays;

/* loaded from: classes.dex */
public final class ArrayUtil {
    public static int getArrayDimension(Object object) {
        int dim = 0;
        for (int i = 0; i < object.toString().length() && object.toString().charAt(i) == '['; i++) {
            dim++;
        }
        return dim;
    }

    public static boolean isArray(Object object) {
        return object.getClass().isArray();
    }

    public static char getType(Object object) {
        if (!isArray(object)) {
            return (char) 0;
        }
        String str = object.toString();
        return str.substring(str.lastIndexOf("[") + 1, str.lastIndexOf("[") + 2).charAt(0);
    }

    private static void traverseArray(StringBuilder result, Object array) {
        if (isArray(array)) {
            if (getArrayDimension(array) == 1) {
                char type = getType(array);
                if (type == 'B') {
                    result.append(Arrays.toString((byte[]) array));
                    return;
                }
                if (type == 'D') {
                    result.append(Arrays.toString((double[]) array));
                    return;
                }
                if (type == 'F') {
                    result.append(Arrays.toString((float[]) array));
                    return;
                }
                if (type == 'L') {
                    Object[] objects = (Object[]) array;
                    result.append("[");
                    for (int i = 0; i < objects.length; i++) {
                        result.append(ObjectUtil.objectToString(objects[i]));
                        if (i != objects.length - 1) {
                            result.append(",");
                        }
                    }
                    result.append("]");
                    return;
                }
                if (type == 'S') {
                    result.append(Arrays.toString((short[]) array));
                    return;
                }
                if (type == 'Z') {
                    result.append(Arrays.toString((boolean[]) array));
                    return;
                }
                if (type == 'I') {
                    result.append(Arrays.toString((int[]) array));
                    return;
                } else if (type == 'J') {
                    result.append(Arrays.toString((long[]) array));
                    return;
                } else {
                    result.append(Arrays.toString((Object[]) array));
                    return;
                }
            }
            result.append("[");
            for (int i2 = 0; i2 < ((Object[]) array).length; i2++) {
                traverseArray(result, ((Object[]) array)[i2]);
                if (i2 != ((Object[]) array).length - 1) {
                    result.append(",");
                }
            }
            result.append("]");
            return;
        }
        result.append("not a array!!");
    }

    public static String parseArray(Object array) {
        StringBuilder result = new StringBuilder();
        traverseArray(result, array);
        return result.toString();
    }
}
