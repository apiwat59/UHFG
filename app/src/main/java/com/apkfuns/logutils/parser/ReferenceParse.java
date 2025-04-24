package com.apkfuns.logutils.parser;

import com.apkfuns.logutils.Parser;
import com.apkfuns.logutils.utils.ObjectUtil;
import java.lang.ref.Reference;

/* loaded from: classes.dex */
public class ReferenceParse implements Parser<Reference> {
    @Override // com.apkfuns.logutils.Parser
    public Class<Reference> parseClassType() {
        return Reference.class;
    }

    @Override // com.apkfuns.logutils.Parser
    public String parseString(Reference reference) {
        Object actual = reference.get();
        StringBuilder builder = new StringBuilder(reference.getClass().getSimpleName() + "<" + actual.getClass().getSimpleName() + "> {");
        StringBuilder sb = new StringBuilder();
        sb.append("→");
        sb.append(ObjectUtil.objectToString(actual));
        builder.append(sb.toString());
        return builder.toString() + "}";
    }
}
