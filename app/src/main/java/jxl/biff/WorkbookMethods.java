package jxl.biff;

import jxl.Sheet;

/* loaded from: classes.dex */
public interface WorkbookMethods {
    String getName(int i) throws NameRangeException;

    int getNameIndex(String str);

    Sheet getReadSheet(int i);
}
