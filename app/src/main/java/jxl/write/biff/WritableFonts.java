package jxl.write.biff;

import jxl.biff.Fonts;
import jxl.write.WritableFont;

/* loaded from: classes.dex */
public class WritableFonts extends Fonts {
    public WritableFonts(WritableWorkbookImpl w) {
        addFont(w.getStyles().getArial10Pt());
        WritableFont f = new WritableFont(WritableFont.ARIAL);
        addFont(f);
        WritableFont f2 = new WritableFont(WritableFont.ARIAL);
        addFont(f2);
        WritableFont f3 = new WritableFont(WritableFont.ARIAL);
        addFont(f3);
    }
}
