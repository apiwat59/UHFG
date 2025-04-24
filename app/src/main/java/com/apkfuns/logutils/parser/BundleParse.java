package com.apkfuns.logutils.parser;

import android.os.Bundle;
import com.apkfuns.logutils.Parser;
import com.apkfuns.logutils.utils.ObjectUtil;

/* loaded from: classes.dex */
public class BundleParse implements Parser<Bundle> {
    @Override // com.apkfuns.logutils.Parser
    public Class<Bundle> parseClassType() {
        return Bundle.class;
    }

    @Override // com.apkfuns.logutils.Parser
    public String parseString(Bundle bundle) {
        if (bundle != null) {
            StringBuilder builder = new StringBuilder(bundle.getClass().getName() + " [" + LINE_SEPARATOR);
            for (String key : bundle.keySet()) {
                builder.append(String.format("'%s' => %s " + LINE_SEPARATOR, key, ObjectUtil.objectToString(bundle.get(key))));
            }
            builder.append("]");
            return builder.toString();
        }
        return null;
    }
}
