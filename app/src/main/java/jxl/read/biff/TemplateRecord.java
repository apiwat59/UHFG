package jxl.read.biff;

import jxl.biff.RecordData;
import jxl.common.Logger;

/* loaded from: classes.dex */
class TemplateRecord extends RecordData {
    private static Logger logger = Logger.getLogger(TemplateRecord.class);
    private boolean template;

    public TemplateRecord(Record t) {
        super(t);
        this.template = true;
    }

    public boolean getTemplate() {
        return this.template;
    }
}
