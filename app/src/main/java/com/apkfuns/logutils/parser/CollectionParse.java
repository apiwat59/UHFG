package com.apkfuns.logutils.parser;

import com.apkfuns.logutils.Parser;
import com.apkfuns.logutils.utils.ObjectUtil;
import java.util.Collection;

/* loaded from: classes.dex */
public class CollectionParse implements Parser<Collection> {
    @Override // com.apkfuns.logutils.Parser
    public Class<Collection> parseClassType() {
        return Collection.class;
    }

    @Override // com.apkfuns.logutils.Parser
    public String parseString(Collection collection) {
        String simpleName = collection.getClass().getName();
        String msg = String.format("%s size = %d [" + LINE_SEPARATOR, simpleName, Integer.valueOf(collection.size()));
        if (!collection.isEmpty()) {
            int flag = 0;
            for (Object item : collection) {
                StringBuilder sb = new StringBuilder();
                sb.append(msg);
                Object[] objArr = new Object[3];
                objArr[0] = Integer.valueOf(flag);
                objArr[1] = ObjectUtil.objectToString(item);
                int flag2 = flag + 1;
                objArr[2] = flag < collection.size() - 1 ? "," + LINE_SEPARATOR : LINE_SEPARATOR;
                sb.append(String.format("[%d]:%s%s", objArr));
                msg = sb.toString();
                flag = flag2;
            }
        }
        return msg + "]";
    }
}
