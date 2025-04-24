package com.handheld.uhfr;

import com.gg.reader.api.protocol.gx.ParamEpcFilter;

/* loaded from: classes.dex */
public class CusParamFilter {
    ParamEpcFilter filter;
    boolean matching;

    public CusParamFilter(ParamEpcFilter filter, boolean matching) {
        this.filter = filter;
        this.matching = matching;
    }

    public ParamEpcFilter getFilter() {
        return this.filter;
    }

    public void setFilter(ParamEpcFilter filter) {
        this.filter = filter;
    }

    public boolean isMatching() {
        return this.matching;
    }

    public void setMatching(boolean matching) {
        this.matching = matching;
    }

    public String toString() {
        return "CusParamFilter{filter=" + this.filter + ", matching=" + this.matching + '}';
    }
}
