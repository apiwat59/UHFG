package com.apkfuns.logutils.parser;

import android.content.Intent;
import android.text.TextUtils;
import com.apkfuns.logutils.Parser;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes.dex */
public class IntentParse implements Parser<Intent> {
    private static Map<Integer, String> flagMap = new HashMap();

    static {
        Field[] fields = Intent.class.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getName().startsWith("FLAG_")) {
                int value = 0;
                try {
                    Object object = field.get(Intent.class);
                    if ((object instanceof Integer) || object.getClass().getSimpleName().equals("int")) {
                        value = ((Integer) object).intValue();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (flagMap.get(Integer.valueOf(value)) == null) {
                    flagMap.put(Integer.valueOf(value), field.getName());
                }
            }
        }
    }

    @Override // com.apkfuns.logutils.Parser
    public Class<Intent> parseClassType() {
        return Intent.class;
    }

    @Override // com.apkfuns.logutils.Parser
    public String parseString(Intent intent) {
        StringBuilder builder = new StringBuilder(parseClassType().getSimpleName() + " [" + LINE_SEPARATOR);
        StringBuilder sb = new StringBuilder();
        sb.append("%s = %s");
        sb.append(LINE_SEPARATOR);
        builder.append(String.format(sb.toString(), "Scheme", intent.getScheme()));
        builder.append(String.format("%s = %s" + LINE_SEPARATOR, "Action", intent.getAction()));
        builder.append(String.format("%s = %s" + LINE_SEPARATOR, "DataString", intent.getDataString()));
        builder.append(String.format("%s = %s" + LINE_SEPARATOR, "Type", intent.getType()));
        builder.append(String.format("%s = %s" + LINE_SEPARATOR, "Package", intent.getPackage()));
        builder.append(String.format("%s = %s" + LINE_SEPARATOR, "ComponentInfo", intent.getComponent()));
        builder.append(String.format("%s = %s" + LINE_SEPARATOR, "Flags", getFlags(intent.getFlags())));
        builder.append(String.format("%s = %s" + LINE_SEPARATOR, "Categories", intent.getCategories()));
        builder.append(String.format("%s = %s" + LINE_SEPARATOR, "Extras", new BundleParse().parseString(intent.getExtras())));
        return builder.toString() + "]";
    }

    private String getFlags(int flags) {
        StringBuilder builder = new StringBuilder();
        Iterator<Integer> it = flagMap.keySet().iterator();
        while (it.hasNext()) {
            int flagKey = it.next().intValue();
            if ((flagKey & flags) == flagKey) {
                builder.append(flagMap.get(Integer.valueOf(flagKey)));
                builder.append(" | ");
            }
        }
        if (TextUtils.isEmpty(builder.toString())) {
            builder.append(flags);
        } else if (builder.indexOf("|") != -1) {
            builder.delete(builder.length() - 2, builder.length());
        }
        return builder.toString();
    }
}
