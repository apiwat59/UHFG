package jxl;

import jxl.biff.BaseCellFeatures;

/* loaded from: classes.dex */
public class CellFeatures extends BaseCellFeatures {
    public CellFeatures() {
    }

    protected CellFeatures(CellFeatures cf) {
        super(cf);
    }

    @Override // jxl.biff.BaseCellFeatures
    public String getComment() {
        return super.getComment();
    }

    @Override // jxl.biff.BaseCellFeatures
    public String getDataValidationList() {
        return super.getDataValidationList();
    }

    @Override // jxl.biff.BaseCellFeatures
    public Range getSharedDataValidationRange() {
        return super.getSharedDataValidationRange();
    }
}
