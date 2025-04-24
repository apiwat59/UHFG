package jxl.biff.formula;

import androidx.core.internal.view.SupportMenu;
import jxl.biff.CellReferenceHelper;
import jxl.common.Assert;
import jxl.common.Logger;

/* loaded from: classes.dex */
class ColumnRange extends Area {
    private static Logger logger = Logger.getLogger(ColumnRange.class);

    ColumnRange() {
    }

    ColumnRange(String s) {
        int seppos = s.indexOf(":");
        Assert.verify(seppos != -1);
        String startcell = s.substring(0, seppos);
        String endcell = s.substring(seppos + 1);
        int columnFirst = CellReferenceHelper.getColumn(startcell);
        int columnLast = CellReferenceHelper.getColumn(endcell);
        boolean columnFirstRelative = CellReferenceHelper.isColumnRelative(startcell);
        boolean columnLastRelative = CellReferenceHelper.isColumnRelative(endcell);
        setRangeData(columnFirst, columnLast, 0, SupportMenu.USER_MASK, columnFirstRelative, columnLastRelative, false, false);
    }

    @Override // jxl.biff.formula.Area, jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        CellReferenceHelper.getColumnReference(getFirstColumn(), buf);
        buf.append(':');
        CellReferenceHelper.getColumnReference(getLastColumn(), buf);
    }
}
