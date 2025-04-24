package jxl.biff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import jxl.common.Assert;
import jxl.write.biff.File;

/* loaded from: classes.dex */
public class Fonts {
    private static final int numDefaultFonts = 4;
    private ArrayList fonts = new ArrayList();

    public void addFont(FontRecord f) {
        if (!f.isInitialized()) {
            int pos = this.fonts.size();
            if (pos >= 4) {
                pos++;
            }
            f.initialize(pos);
            this.fonts.add(f);
        }
    }

    public FontRecord getFont(int index) {
        if (index > 4) {
            index--;
        }
        return (FontRecord) this.fonts.get(index);
    }

    public void write(File outputFile) throws IOException {
        Iterator i = this.fonts.iterator();
        while (i.hasNext()) {
            FontRecord font = (FontRecord) i.next();
            outputFile.write(font);
        }
    }

    IndexMapping rationalize() {
        IndexMapping mapping = new IndexMapping(this.fonts.size() + 1);
        ArrayList newfonts = new ArrayList();
        int numremoved = 0;
        for (int i = 0; i < 4; i++) {
            FontRecord fr = (FontRecord) this.fonts.get(i);
            newfonts.add(fr);
            mapping.setMapping(fr.getFontIndex(), fr.getFontIndex());
        }
        for (int i2 = 4; i2 < this.fonts.size(); i2++) {
            FontRecord fr2 = (FontRecord) this.fonts.get(i2);
            boolean duplicate = false;
            Iterator it = newfonts.iterator();
            while (it.hasNext() && !duplicate) {
                FontRecord fr22 = (FontRecord) it.next();
                if (fr2.equals(fr22)) {
                    duplicate = true;
                    mapping.setMapping(fr2.getFontIndex(), mapping.getNewIndex(fr22.getFontIndex()));
                    numremoved++;
                }
            }
            if (!duplicate) {
                newfonts.add(fr2);
                int newindex = fr2.getFontIndex() - numremoved;
                Assert.verify(newindex > 4);
                mapping.setMapping(fr2.getFontIndex(), newindex);
            }
        }
        Iterator it2 = newfonts.iterator();
        while (it2.hasNext()) {
            FontRecord fr3 = (FontRecord) it2.next();
            fr3.initialize(mapping.getNewIndex(fr3.getFontIndex()));
        }
        this.fonts = newfonts;
        return mapping;
    }
}
