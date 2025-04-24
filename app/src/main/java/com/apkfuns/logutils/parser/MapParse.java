package com.apkfuns.logutils.parser;

import com.apkfuns.logutils.Parser;
import com.apkfuns.logutils.utils.ObjectUtil;
import java.util.Map;
import java.util.Set;

/* loaded from: classes.dex */
public class MapParse implements Parser<Map> {
    @Override // com.apkfuns.logutils.Parser
    public Class<Map> parseClassType() {
        return Map.class;
    }

    @Override // com.apkfuns.logutils.Parser
    public String parseString(Map map) {
        String msg = map.getClass().getName() + " [" + LINE_SEPARATOR;
        Set<Object> keys = map.keySet();
        for (Object key : keys) {
            String itemString = "%s -> %s" + LINE_SEPARATOR;
            Object value = map.get(key);
            if (value != null) {
                if (value instanceof String) {
                    value = "\"" + value + "\"";
                } else if (value instanceof Character) {
                    value = "'" + value + "'";
                }
            }
            msg = msg + String.format(itemString, ObjectUtil.objectToString(key), ObjectUtil.objectToString(value));
        }
        return msg + "]";
    }
}
