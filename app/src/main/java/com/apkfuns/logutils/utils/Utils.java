package com.apkfuns.logutils.utils;

import com.apkfuns.logutils.Constant;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
public class Utils {
    public static final int DIVIDER_BOTTOM = 2;
    public static final int DIVIDER_CENTER = 4;
    public static final int DIVIDER_NORMAL = 3;
    public static final int DIVIDER_TOP = 1;

    public static String printDividingLine(int dir) {
        if (dir == 1) {
            return "╔═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════";
        }
        if (dir == 2) {
            return "╚═══════════════════════════════════════════════════════════════════════════════════════════════════════════════════";
        }
        if (dir == 3) {
            return "║ ";
        }
        if (dir == 4) {
            return "╟───────────────────────────────────────────────────────────────────────────────────────────────────────────────────";
        }
        return "";
    }

    public static List<String> largeStringToList(String msg) {
        List<String> stringList = new ArrayList<>();
        int index = 0;
        int countOfSub = msg.length() / Constant.LINE_MAX;
        if (countOfSub > 0) {
            for (int i = 0; i < countOfSub; i++) {
                String sub = msg.substring(index, index + Constant.LINE_MAX);
                stringList.add(sub);
                index += Constant.LINE_MAX;
            }
            int i2 = msg.length();
            stringList.add(msg.substring(index, i2));
        } else {
            stringList.add(msg);
        }
        return stringList;
    }

    public static String shorten(String string, int count, int length) {
        if (string == null) {
            return null;
        }
        String resultString = string;
        if (Math.abs(length) < resultString.length()) {
            if (length > 0) {
                resultString = string.substring(0, length);
            }
            if (length < 0) {
                resultString = string.substring(string.length() + length, string.length());
            }
        }
        if (Math.abs(count) > resultString.length()) {
            return String.format("%" + count + "s", resultString);
        }
        return resultString;
    }

    public static String shortenClassName(String className, int count, int maxLength) throws Exception {
        String className2 = shortenPackagesName(className, count);
        if (className2 == null) {
            return null;
        }
        if (maxLength == 0 || maxLength > className2.length()) {
            return className2;
        }
        if (maxLength < 0) {
            int maxLength2 = -maxLength;
            StringBuilder builder = new StringBuilder();
            int index = className2.length() - 1;
            while (true) {
                if (index > 0) {
                    int i = className2.lastIndexOf(46, index);
                    if (i == -1) {
                        if (builder.length() <= 0 || builder.length() + index + 1 <= maxLength2) {
                            builder.insert(0, className2.substring(0, index + 1));
                            index = i - 1;
                        } else {
                            builder.insert(0, '*');
                            break;
                        }
                    } else if (builder.length() <= 0 || builder.length() + ((index + 1) - i) + 1 <= maxLength2) {
                        builder.insert(0, className2.substring(i, index + 1));
                        index = i - 1;
                    } else {
                        builder.insert(0, '*');
                        break;
                    }
                } else {
                    break;
                }
            }
            return builder.toString();
        }
        StringBuilder builder2 = new StringBuilder();
        int index2 = 0;
        while (true) {
            if (index2 < className2.length()) {
                int i2 = className2.indexOf(46, index2);
                if (i2 == -1) {
                    if (builder2.length() > 0) {
                        builder2.insert(builder2.length(), '*');
                    } else {
                        builder2.insert(builder2.length(), className2.substring(index2, className2.length()));
                    }
                } else {
                    if (builder2.length() > 0 && i2 + 1 > maxLength) {
                        builder2.insert(builder2.length(), '*');
                        break;
                    }
                    builder2.insert(builder2.length(), className2.substring(index2, i2 + 1));
                    index2 = i2 + 1;
                }
            } else {
                break;
            }
        }
        return builder2.toString();
    }

    private static String shortenPackagesName(String className, int count) {
        if (className == null) {
            return null;
        }
        if (count == 0) {
            return className;
        }
        StringBuilder builder = new StringBuilder();
        if (count > 0) {
            int points = 1;
            int index = 0;
            while (true) {
                if (index >= className.length()) {
                    break;
                }
                int i = className.indexOf(46, index);
                if (i == -1) {
                    builder.insert(builder.length(), className.substring(index, className.length()));
                    break;
                }
                if (points == count) {
                    builder.insert(builder.length(), className.substring(index, i));
                    break;
                }
                builder.insert(builder.length(), className.substring(index, i + 1));
                index = i + 1;
                points++;
            }
        } else if (count < 0) {
            String exceptString = shortenPackagesName(className, -count);
            if (className.equals(exceptString)) {
                int from = className.lastIndexOf(46) + 1;
                int to = className.length();
                builder.insert(builder.length(), className.substring(from, to));
            } else {
                return className.replaceFirst(exceptString + '.', "");
            }
        }
        return builder.toString();
    }
}
