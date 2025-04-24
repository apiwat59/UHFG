package com.apkfuns.logutils.utils;

import com.apkfuns.logutils.Constant;
import com.apkfuns.logutils.Parser;
import java.lang.reflect.Field;
import org.apache.log4j.spi.Configurator;

/* loaded from: classes.dex */
public class ObjectUtil {
    public static String objectToString(Object object) {
        return objectToString(object, 0);
    }

    public static boolean isStaticInnerClass(Class cla) {
        if (cla != null && cla.isMemberClass()) {
            int modifiers = cla.getModifiers();
            if ((modifiers & 8) == 8) {
                return true;
            }
            return false;
        }
        return false;
    }

    public static String objectToString(Object object, int childLevel) {
        if (object == null) {
            return Constant.STRING_OBJECT_NULL;
        }
        if (childLevel > 2) {
            return object.toString();
        }
        if (Constant.getParsers() != null && Constant.getParsers().size() > 0) {
            for (Parser parser : Constant.getParsers()) {
                if (parser.parseClassType().isAssignableFrom(object.getClass())) {
                    return parser.parseString(object);
                }
            }
        }
        if (ArrayUtil.isArray(object)) {
            return ArrayUtil.parseArray(object);
        }
        if (object.toString().startsWith(object.getClass().getName() + "@")) {
            StringBuilder builder = new StringBuilder();
            getClassFields(object.getClass(), builder, object, false, childLevel);
            Class superClass = object.getClass().getSuperclass();
            if (superClass != null) {
                while (!superClass.equals(Object.class)) {
                    getClassFields(superClass, builder, object, true, childLevel);
                    superClass = superClass.getSuperclass();
                }
            } else {
                builder.append(object.toString());
            }
            return builder.toString();
        }
        return object.toString();
    }

    private static void getClassFields(Class cla, StringBuilder builder, Object o, boolean isSubClass, int childOffset) {
        String str;
        String str2 = Configurator.NULL;
        if (cla.equals(Object.class)) {
            return;
        }
        if (isSubClass) {
            builder.append(Constant.BR + Constant.BR + "=> ");
        }
        builder.append(cla.getSimpleName() + " {");
        Field[] fields = cla.getDeclaredFields();
        int i = 0;
        while (i < fields.length) {
            Field field = fields[i];
            field.setAccessible(true);
            if (cla.isMemberClass() && !isStaticInnerClass(cla) && i == 0) {
                str = str2;
            } else {
                Object subObject = null;
                try {
                    Object subObject2 = field.get(o);
                    if (subObject2 != null) {
                        if (!isStaticInnerClass(cla)) {
                            if (field.getName().equals("$change")) {
                                str = str2;
                            } else if (field.getName().equalsIgnoreCase("this$0")) {
                                str = str2;
                            }
                        }
                        if (subObject2 instanceof String) {
                            subObject2 = "\"" + subObject2 + "\"";
                        } else if (subObject2 instanceof Character) {
                            subObject2 = "'" + subObject2 + "'";
                        }
                        if (childOffset < 2) {
                            subObject2 = objectToString(subObject2, childOffset + 1);
                        }
                    }
                    String formatString = "%s = %s, ";
                    str = str2;
                    Object[] objArr = new Object[2];
                    objArr[0] = field.getName();
                    objArr[1] = subObject2 == null ? str : subObject2.toString();
                    builder.append(String.format(formatString, objArr));
                } catch (IllegalAccessException e) {
                    str = str2;
                    Object subObject3 = e;
                    if (isStaticInnerClass(cla) || (!field.getName().equals("$change") && !field.getName().equalsIgnoreCase("this$0"))) {
                        if (subObject3 instanceof String) {
                            subObject3 = "\"" + subObject3 + "\"";
                        } else if (subObject3 instanceof Character) {
                            subObject3 = "'" + subObject3 + "'";
                        }
                        if (childOffset < 2) {
                            subObject3 = objectToString(subObject3, childOffset + 1);
                        }
                        String formatString2 = "%s = %s, ";
                        Object[] objArr2 = new Object[2];
                        objArr2[0] = field.getName();
                        objArr2[1] = subObject3 == null ? str : subObject3.toString();
                        builder.append(String.format(formatString2, objArr2));
                    }
                } catch (Throwable th) {
                    str = str2;
                    if (0 != 0) {
                        if (!isStaticInnerClass(cla)) {
                            if (field.getName().equals("$change")) {
                                continue;
                            } else if (field.getName().equalsIgnoreCase("this$0")) {
                            }
                        }
                        if (subObject instanceof String) {
                            subObject = "\"" + ((Object) null) + "\"";
                        } else if (subObject instanceof Character) {
                            subObject = "'" + ((Object) null) + "'";
                        }
                        if (childOffset < 2) {
                            subObject = objectToString(subObject, childOffset + 1);
                        }
                    }
                    String formatString3 = "%s = %s, ";
                    Object[] objArr3 = new Object[2];
                    objArr3[0] = field.getName();
                    if (subObject != null) {
                        str = subObject.toString();
                    }
                    objArr3[1] = str;
                    builder.append(String.format(formatString3, objArr3));
                    throw th;
                }
            }
            i++;
            str2 = str;
        }
        if (builder.toString().endsWith("{")) {
            builder.append("}");
            return;
        }
        builder.replace(builder.length() - 2, builder.length() - 1, "}");
    }
}
