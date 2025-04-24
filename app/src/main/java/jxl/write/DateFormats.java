package jxl.write;

import jxl.biff.DisplayFormat;

/* loaded from: classes.dex */
public final class DateFormats {
    public static final DisplayFormat DEFAULT;
    public static final DisplayFormat FORMAT1;
    public static final DisplayFormat FORMAT10;
    public static final DisplayFormat FORMAT11;
    public static final DisplayFormat FORMAT12;
    public static final DisplayFormat FORMAT2;
    public static final DisplayFormat FORMAT3;
    public static final DisplayFormat FORMAT4;
    public static final DisplayFormat FORMAT5;
    public static final DisplayFormat FORMAT6;
    public static final DisplayFormat FORMAT7;
    public static final DisplayFormat FORMAT8;
    public static final DisplayFormat FORMAT9;

    private static class BuiltInFormat implements DisplayFormat {
        private String formatString;
        private int index;

        public BuiltInFormat(int i, String s) {
            this.index = i;
            this.formatString = s;
        }

        @Override // jxl.biff.DisplayFormat
        public int getFormatIndex() {
            return this.index;
        }

        @Override // jxl.biff.DisplayFormat
        public boolean isInitialized() {
            return true;
        }

        @Override // jxl.biff.DisplayFormat
        public void initialize(int pos) {
        }

        @Override // jxl.biff.DisplayFormat
        public boolean isBuiltIn() {
            return true;
        }

        public String getFormatString() {
            return this.formatString;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof BuiltInFormat)) {
                return false;
            }
            BuiltInFormat bif = (BuiltInFormat) o;
            return this.index == bif.index;
        }

        public int hashCode() {
            return this.index;
        }
    }

    static {
        BuiltInFormat builtInFormat = new BuiltInFormat(14, "M/d/yy");
        FORMAT1 = builtInFormat;
        DEFAULT = builtInFormat;
        FORMAT2 = new BuiltInFormat(15, "d-MMM-yy");
        FORMAT3 = new BuiltInFormat(16, "d-MMM");
        FORMAT4 = new BuiltInFormat(17, "MMM-yy");
        FORMAT5 = new BuiltInFormat(18, "h:mm a");
        FORMAT6 = new BuiltInFormat(19, "h:mm:ss a");
        FORMAT7 = new BuiltInFormat(20, "H:mm");
        FORMAT8 = new BuiltInFormat(21, "H:mm:ss");
        FORMAT9 = new BuiltInFormat(22, "M/d/yy H:mm");
        FORMAT10 = new BuiltInFormat(45, "mm:ss");
        FORMAT11 = new BuiltInFormat(46, "H:mm:ss");
        FORMAT12 = new BuiltInFormat(47, "H:mm:ss");
    }
}
