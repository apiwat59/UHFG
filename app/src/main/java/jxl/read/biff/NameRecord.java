package jxl.read.biff;

import java.util.ArrayList;
import jxl.biff.BuiltInName;
import jxl.biff.RecordData;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class NameRecord extends RecordData {
    private static final int areaReference = 59;
    private static final int builtIn = 32;
    private static final int cellReference = 58;
    private static final int commandMacro = 12;
    private static final int subExpression = 41;
    private static final int union = 16;
    private BuiltInName builtInName;
    private int index;
    private boolean isbiff8;
    private String name;
    private ArrayList ranges;
    private int sheetRef;
    private static Logger logger = Logger.getLogger(NameRecord.class);
    public static Biff7 biff7 = new Biff7();

    /* JADX INFO: Access modifiers changed from: private */
    static class Biff7 {
        private Biff7() {
        }
    }

    public class NameRange {
        private int columnFirst;
        private int columnLast;
        private int externalSheet;
        private int rowFirst;
        private int rowLast;

        NameRange(int s1, int c1, int r1, int c2, int r2) {
            this.columnFirst = c1;
            this.rowFirst = r1;
            this.columnLast = c2;
            this.rowLast = r2;
            this.externalSheet = s1;
        }

        public int getFirstColumn() {
            return this.columnFirst;
        }

        public int getFirstRow() {
            return this.rowFirst;
        }

        public int getLastColumn() {
            return this.columnLast;
        }

        public int getLastRow() {
            return this.rowLast;
        }

        public int getExternalSheet() {
            return this.externalSheet;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:51:0x0180 A[Catch: all -> 0x0284, TryCatch #0 {all -> 0x0284, blocks: (B:7:0x0053, B:11:0x0058, B:13:0x0062, B:16:0x008e, B:18:0x00a6, B:21:0x00ba, B:23:0x00bd, B:26:0x00fa, B:30:0x0113, B:37:0x013d, B:39:0x0143, B:41:0x0151, B:43:0x0155, B:45:0x0159, B:47:0x015f, B:49:0x017d, B:51:0x0180, B:54:0x01bd, B:57:0x01d6, B:59:0x01f8, B:61:0x01fc, B:63:0x0202, B:76:0x0208, B:66:0x021b, B:73:0x0221, B:87:0x0168, B:89:0x016c, B:92:0x024a, B:95:0x0255, B:97:0x024f, B:101:0x004d), top: B:100:0x004d }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    NameRecord(jxl.read.biff.Record r23, jxl.WorkbookSettings r24, int r25) {
        /*
            Method dump skipped, instructions count: 661
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: jxl.read.biff.NameRecord.<init>(jxl.read.biff.Record, jxl.WorkbookSettings, int):void");
    }

    /* JADX WARN: Incorrect condition in loop: B:18:0x0086 */
    /* JADX WARN: Incorrect condition in loop: B:36:0x0107 */
    /* JADX WARN: Removed duplicated region for block: B:37:0x0109 A[Catch: all -> 0x0171, TryCatch #0 {all -> 0x0171, blocks: (B:6:0x0033, B:10:0x003f, B:12:0x0045, B:14:0x0074, B:17:0x0085, B:19:0x0088, B:23:0x00cc, B:25:0x00d2, B:27:0x00df, B:29:0x00e3, B:31:0x00e7, B:33:0x00ed, B:35:0x0106, B:37:0x0109, B:39:0x014d, B:41:0x0151, B:43:0x0157, B:56:0x015d, B:46:0x0162, B:64:0x00f5, B:66:0x00f9), top: B:5:0x0033 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    NameRecord(jxl.read.biff.Record r19, jxl.WorkbookSettings r20, int r21, jxl.read.biff.NameRecord.Biff7 r22) {
        /*
            Method dump skipped, instructions count: 386
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: jxl.read.biff.NameRecord.<init>(jxl.read.biff.Record, jxl.WorkbookSettings, int, jxl.read.biff.NameRecord$Biff7):void");
    }

    public String getName() {
        return this.name;
    }

    public BuiltInName getBuiltInName() {
        return this.builtInName;
    }

    public NameRange[] getRanges() {
        NameRange[] nr = new NameRange[this.ranges.size()];
        return (NameRange[]) this.ranges.toArray(nr);
    }

    int getIndex() {
        return this.index;
    }

    public int getSheetRef() {
        return this.sheetRef;
    }

    public void setSheetRef(int i) {
        this.sheetRef = i;
    }

    public byte[] getData() {
        return getRecord().getData();
    }

    public boolean isBiff8() {
        return this.isbiff8;
    }

    public boolean isGlobal() {
        return this.sheetRef == 0;
    }
}
