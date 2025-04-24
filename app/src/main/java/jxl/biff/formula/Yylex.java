package jxl.biff.formula;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import jxl.biff.WorkbookMethods;

/* loaded from: classes.dex */
class Yylex {
    public static final int YYEOF = -1;
    public static final int YYINITIAL = 0;
    public static final int YYSTRING = 1;
    private static final String ZZ_ACTION_PACKED_0 = "\u0001\u0000\u0001\u0001\u0001\u0002\u0001\u0003\u0001\u0004\u0001\u0005\u0001\u0006\u0001\u0007\u0001\u0000\u0002\u0002\u0001\b\u0001\u0000\u0001\t\u0001\u0000\u0001\n\u0001\u000b\u0001\f\u0001\r\u0001\u000e\u0001\u000f\u0001\u0010\u0001\u0001\u0001\u0011\u0001\u0002\u0001\u0012\u0001\u0000\u0001\u0013\u0001\u0000\u0001\u0002\u0003\u0000\u0002\u0002\u0005\u0000\u0001\u0014\u0001\u0015\u0001\u0016\u0001\u0002\u0001\u0000\u0001\u0017\u0001\u0000\u0001\u0012\u0002\u0000\u0001\u0018\u0001\u0000\u0002\u0002\b\u0000\u0001\u0017\u0001\u0000\u0001\u0019\u0001\u0000\u0001\u001a\b\u0000\u0001\u001b\u0002\u0000\u0001\u0019\u0002\u0000\u0001\u001c\u0004\u0000\u0001\u001d\u0003\u0000\u0001\u001d\u0001\u0000\u0001\u001e\u0001\u0000";
    private static final String ZZ_ATTRIBUTE_PACKED_0 = "\u0001\u0000\u0003\u0001\u0004\t\u0001\u0000\u0002\u0001\u0001\t\u0001\u0000\u0001\t\u0001\u0000\u0004\t\u0001\u0001\u0001\t\u0002\u0001\u0001\t\u0002\u0001\u0001\u0000\u0001\t\u0001\u0000\u0001\u0001\u0003\u0000\u0002\u0001\u0005\u0000\u0003\t\u0001\u0001\u0001\u0000\u0001\u0001\u0001\u0000\u0001\u0001\u0002\u0000\u0001\u0001\u0001\u0000\u0002\u0001\b\u0000\u0001\t\u0001\u0000\u0001\u0001\u0001\u0000\u0001\u0001\b\u0000\u0001\u0001\u0002\u0000\u0001\u0001\u0002\u0000\u0001\t\u0004\u0000\u0001\u0001\u0003\u0000\u0001\t\u0001\u0000\u0001\u0001\u0001\u0000";
    private static final int ZZ_BUFFERSIZE = 16384;
    private static final int ZZ_NO_MATCH = 1;
    private static final int ZZ_PUSHBACK_2BIG = 2;
    private static final String ZZ_ROWMAP_PACKED_0 = "\u0000\u0000\u0000%\u0000J\u0000o\u0000\u0094\u0000\u0094\u0000\u0094\u0000\u0094\u0000¹\u0000Þ\u0000ă\u0000\u0094\u0000Ĩ\u0000\u0094\u0000ō\u0000\u0094\u0000\u0094\u0000\u0094\u0000\u0094\u0000Ų\u0000\u0094\u0000Ɨ\u0000Ƽ\u0000\u0094\u0000ǡ\u0000Ȇ\u0000ȫ\u0000\u0094\u0000ɐ\u0000ɵ\u0000ʚ\u0000ʿ\u0000ˤ\u0000̉\u0000̮\u0000͓\u0000\u0378\u0000Ν\u0000ς\u0000ϧ\u0000\u0094\u0000\u0094\u0000\u0094\u0000Ќ\u0000б\u0000і\u0000ѻ\u0000Ҡ\u0000Ӆ\u0000Ӫ\u0000ʿ\u0000ԏ\u0000Դ\u0000ՙ\u0000վ\u0000֣\u0000\u05c8\u0000\u05ed\u0000ؒ\u0000ط\u0000ٜ\u0000ځ\u0000\u0094\u0000ڦ\u0000ۋ\u0000ۋ\u0000Ќ\u0000۰\u0000ܕ\u0000ܺ\u0000ݟ\u0000ބ\u0000ީ\u0000ߎ\u0000߳\u0000࠘\u0000࠘\u0000࠽\u0000ࡢ\u0000ࢇ\u0000ࢬ\u0000\u0094\u0000࣑\u0000ࣶ\u0000छ\u0000ी\u0000॥\u0000ঊ\u0000য\u0000\u09d4\u0000\u0094\u0000৹\u0000ਞ\u0000ਞ";
    private static final String ZZ_TRANS_PACKED_0 = "\u0001\u0000\u0001\u0003\u0001\u0004\u0001\u0005\u0001\u0006\u0001\u0007\u0001\b\u0001\u0000\u0001\t\u0001\n\u0003\u0003\u0001\u000b\u0003\u0003\u0001\f\u0001\r\u0002\u0000\u0001\u000e\u0001\u000f\u0004\u0003\u0001\u0010\u0001\u0004\u0001\u0003\u0001\u0000\u0001\u0011\u0001\u0012\u0001\u0013\u0001\u0014\u0001\u0015\u0001\u0016\u0011\u0017\u0001\u0018\u0013\u0017\u0001\u0000\u0001\u0019\u0001\u001a\u0001\u001b\u0001\u0000\u0001\u001c\u0002\u0000\u0001\u001d\b\u0019\u0002\u0000\u0001\u001e\u0001\u001f\u0002\u0000\u0004\u0019\u0001\u0000\u0001\u001a\u0001\u0019\t\u0000\u0001\u0004\u0004\u0000\u0001 \u0014\u0000\u0001\u0004.\u0000\u0001!\u0007\u0000\b!\u0006\u0000\u0004!\u0002\u0000\u0001!\b\u0000\u0001\u0019\u0001\u001a\u0001\u001b\u0001\u0000\u0001\u001c\u0002\u0000\u0001\u001d\u0001\u0019\u0001\"\u0006\u0019\u0002\u0000\u0001\u001e\u0001\u001f\u0002\u0000\u0004\u0019\u0001\u0000\u0001\u001a\u0001\u0019\b\u0000\u0001\u0019\u0001\u001a\u0001\u001b\u0001\u0000\u0001\u001c\u0002\u0000\u0001\u001d\u0005\u0019\u0001#\u0002\u0019\u0002\u0000\u0001\u001e\u0001\u001f\u0002\u0000\u0004\u0019\u0001\u0000\u0001\u001a\u0001\u0019\u0007\u0000\u0012\r\u0001$\u0012\r\n\u0000\u0001%\f\u0000\u0001&\u0001'\u0001\u0000\u0001(-\u0000\u0001)#\u0000\u0001*\u0001+\u0001\u0000\u0011\u0017\u0001\u0000\u0013\u0017\u0001\u0000\u0001,\u0001\u001a\u0001\u001b\u0001\u0000\u0001\u001c\u0002\u0000\u0001\u001d\b,\u0002\u0000\u0001\u001e\u0001\u001f\u0002\u0000\u0004,\u0001\u0000\u0001\u001a\u0001,\b\u0000\u0001\u001e\u0001\u001a\u0001-\u0005\u0000\b\u001e\u0002\u0000\u0001\u001e\u0003\u0000\u0004\u001e\u0001\u0000\u0001\u001a\u0001\u001e\b\u0000\u0001.\u0006\u0000\u0001/\b.\u0006\u0000\u0004.\u0002\u0000\u0001.\t\u0000\u00010\u0019\u0000\u00010\t\u0000\u0002\u001e\u0006\u0000\b\u001e\u0002\u0000\u0001\u001e\u0003\u0000\u0004\u001e\u0001\u0000\u0002\u001e\b\u0000\u00011\u0006\u0000\u00012\b1\u0006\u0000\u00041\u0002\u0000\u00011\t\u0000\u00013\u0019\u0000\u00013\t\u0000\u00014\u00010\u0001\u001b\u0004\u0000\u0001\u001d\b4\u0006\u0000\u00044\u0001\u0000\u00010\u00014\b\u0000\u0001,\u0001\u001a\u0001\u001b\u0001\u0000\u0001\u001c\u0002\u0000\u0001\u001d\u0002,\u00015\u0005,\u0002\u0000\u0001\u001e\u0001\u001f\u0002\u0000\u0004,\u0001\u0000\u0001\u001a\u0001,\b\u0000\u0001,\u0001\u001a\u0001\u001b\u0001\u0000\u0001\u001c\u0002\u0000\u0001\u001d\u0006,\u00016\u0001,\u0002\u0000\u0001\u001e\u0001\u001f\u0002\u0000\u0004,\u0001\u0000\u0001\u001a\u0001,\u001b\u0000\u00017\u001c\u0000\u00018#\u0000\u00019\u0002\u0000\u0001:/\u0000\u0001;\u0019\u0000\u0001<\u0017\u0000\u0001,\u0001\u001e\u0002\u0000\u0001\u001c\u0003\u0000\b,\u0002\u0000\u0001\u001e\u0001\u001f\u0002\u0000\u0004,\u0001\u0000\u0001\u001e\u0001,\b\u0000\u0001=\u0006\u0000\u0001>\b=\u0006\u0000\u0004=\u0002\u0000\u0001=\b\u0000\u0001?\u0007\u0000\b?\u0006\u0000\u0004?\u0002\u0000\u0001?\b\u0000\u0001.\u0007\u0000\b.\u0006\u0000\u0004.\u0002\u0000\u0001.\t\u0000\u00010\u0001-\u0018\u0000\u00010\t\u0000\u0001@\u0001A\u0005\u0000\u0001B\b@\u0006\u0000\u0004@\u0001\u0000\u0001A\u0001@\b\u0000\u00011\u0007\u0000\b1\u0006\u0000\u00041\u0002\u0000\u00011\t\u0000\u00010\u0001\u001b\u0004\u0000\u0001\u001d\u0013\u0000\u00010\t\u0000\u0001,\u0001\u001e\u0002\u0000\u0001\u001c\u0003\u0000\u0003,\u0001C\u0004,\u0002\u0000\u0001\u001e\u0001\u001f\u0002\u0000\u0004,\u0001\u0000\u0001\u001e\u0001,\b\u0000\u0001,\u0001\u001e\u0002\u0000\u0001\u001c\u0003\u0000\u0007,\u00015\u0002\u0000\u0001\u001e\u0001\u001f\u0002\u0000\u0004,\u0001\u0000\u0001\u001e\u0001,\b\u0000\u0001D\u0006\u0000\u0001E\bD\u0006\u0000\u0004D\u0002\u0000\u0001D\u0014\u0000\u0001F&\u0000\u0001G\r\u0000\u0001F$\u0000\u0001H!\u0000\u0001I\u0019\u0000\u0001J\u0016\u0000\u0001K\u0001L\u0005\u0000\u0001M\bK\u0006\u0000\u0004K\u0001\u0000\u0001L\u0001K\b\u0000\u0001=\u0007\u0000\b=\u0006\u0000\u0004=\u0002\u0000\u0001=\t\u0000\u0001A\u0005\u0000\u0001B\u0013\u0000\u0001A\n\u0000\u0001A\u0019\u0000\u0001A\t\u0000\u0001N\u0001O\u0001P\u0004\u0000\u0001Q\bN\u0006\u0000\u0004N\u0001\u0000\u0001O\u0001N\b\u0000\u0001D\u0007\u0000\bD\u0006\u0000\u0004D\u0002\u0000\u0001D\u001b\u0000\u0001R\u001f\u0000\u0001F!\u0000\u0001S3\u0000\u0001T\u0014\u0000\u0001U\u001b\u0000\u0001L\u0005\u0000\u0001M\u0013\u0000\u0001L\n\u0000\u0001L\u0019\u0000\u0001L\n\u0000\u0001O\u0001P\u0004\u0000\u0001Q\u0013\u0000\u0001O\n\u0000\u0001O\u0001V\u0018\u0000\u0001O\t\u0000\u0001W\u0006\u0000\u0001X\bW\u0006\u0000\u0004W\u0002\u0000\u0001W\t\u0000\u0001O\u0019\u0000\u0001O&\u0000\u0001R\"\u0000\u0001F\u0014\u0000\u0001F\u0019\u0000\u0001Y\u0006\u0000\u0001Z\bY\u0006\u0000\u0004Y\u0002\u0000\u0001Y\b\u0000\u0001[\u0007\u0000\b[\u0006\u0000\u0004[\u0002\u0000\u0001[\b\u0000\u0001W\u0007\u0000\bW\u0006\u0000\u0004W\u0002\u0000\u0001W\b\u0000\u0001\\\u0001]\u0005\u0000\u0001^\b\\\u0006\u0000\u0004\\\u0001\u0000\u0001]\u0001\\\b\u0000\u0001Y\u0007\u0000\bY\u0006\u0000\u0004Y\u0002\u0000\u0001Y\t\u0000\u0001]\u0005\u0000\u0001^\u0013\u0000\u0001]\n\u0000\u0001]\u0019\u0000\u0001]\b\u0000";
    private static final int ZZ_UNKNOWN_ERROR = 0;
    private boolean emptyString;
    private ExternalSheet externalSheet;
    private WorkbookMethods nameTable;
    private int yychar;
    private int yycolumn;
    private int yyline;
    private boolean zzAtBOL;
    private boolean zzAtEOF;
    private char[] zzBuffer;
    private int zzCurrentPos;
    private int zzEndRead;
    private int zzLexicalState;
    private int zzMarkedPos;
    private int zzPushbackPos;
    private Reader zzReader;
    private int zzStartRead;
    private int zzState;
    private static final String ZZ_CMAP_PACKED = "\b\u0000\u0003\u0015\u0015\u0000\u0001\u0015\u0001\u0014\u0001\u0011\u0001\u0016\u0001\b\u0002\u0000\u0001\u0012\u0001\u0005\u0001\u0006\u0001!\u0001\u001f\u0001\u0004\u0001 \u0001\u0007\u0001\u001b\u0001\u001c\t\u0002\u0001\u0003\u0001\u0000\u0001$\u0001#\u0001\"\u0001\u001e\u0001\u0000\u0001\u000e\u0002\u0001\u0001\u0018\u0001\f\u0001\r\u0002\u0001\u0001\u0019\u0002\u0001\u0001\u000f\u0001\u001d\u0001\u0017\u0003\u0001\u0001\n\u0001\u0010\u0001\t\u0001\u000b\u0001\u001a\u0004\u0001\u0004\u0000\u0001\u0013\u0001\u0000\u001a\u0001ﾅ\u0000";
    private static final char[] ZZ_CMAP = zzUnpackCMap(ZZ_CMAP_PACKED);
    private static final int[] ZZ_ACTION = zzUnpackAction();
    private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
    private static final int[] ZZ_TRANS = zzUnpackTrans();
    private static final String[] ZZ_ERROR_MSG = {"Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large"};
    private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();

    private static int[] zzUnpackAction() {
        int[] result = new int[94];
        zzUnpackAction(ZZ_ACTION_PACKED_0, 0, result);
        return result;
    }

    private static int zzUnpackAction(String packed, int offset, int[] result) {
        int j;
        int count = 0;
        int j2 = offset;
        int l = packed.length();
        while (count < l) {
            int i = count + 1;
            int count2 = packed.charAt(count);
            int i2 = i + 1;
            int value = packed.charAt(i);
            while (true) {
                j = j2 + 1;
                result[j2] = value;
                count2--;
                if (count2 <= 0) {
                    break;
                }
                j2 = j;
            }
            count = i2;
            j2 = j;
        }
        return j2;
    }

    private static int[] zzUnpackRowMap() {
        int[] result = new int[94];
        zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, 0, result);
        return result;
    }

    private static int zzUnpackRowMap(String packed, int offset, int[] result) {
        int i = 0;
        int j = offset;
        int l = packed.length();
        while (i < l) {
            int i2 = i + 1;
            int high = packed.charAt(i) << 16;
            result[j] = packed.charAt(i2) | high;
            j++;
            i = i2 + 1;
        }
        return j;
    }

    private static int[] zzUnpackTrans() {
        int[] result = new int[2627];
        zzUnpackTrans(ZZ_TRANS_PACKED_0, 0, result);
        return result;
    }

    private static int zzUnpackTrans(String packed, int offset, int[] result) {
        int j;
        int count = 0;
        int j2 = offset;
        int l = packed.length();
        while (count < l) {
            int i = count + 1;
            int count2 = packed.charAt(count);
            int i2 = i + 1;
            int value = packed.charAt(i);
            int value2 = value - 1;
            while (true) {
                j = j2 + 1;
                result[j2] = value2;
                count2--;
                if (count2 <= 0) {
                    break;
                }
                j2 = j;
            }
            count = i2;
            j2 = j;
        }
        return j2;
    }

    private static int[] zzUnpackAttribute() {
        int[] result = new int[94];
        zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, 0, result);
        return result;
    }

    private static int zzUnpackAttribute(String packed, int offset, int[] result) {
        int j;
        int count = 0;
        int j2 = offset;
        int l = packed.length();
        while (count < l) {
            int i = count + 1;
            int count2 = packed.charAt(count);
            int i2 = i + 1;
            int value = packed.charAt(i);
            while (true) {
                j = j2 + 1;
                result[j2] = value;
                count2--;
                if (count2 <= 0) {
                    break;
                }
                j2 = j;
            }
            count = i2;
            j2 = j;
        }
        return j2;
    }

    int getPos() {
        return this.yychar;
    }

    void setExternalSheet(ExternalSheet es) {
        this.externalSheet = es;
    }

    void setNameTable(WorkbookMethods nt) {
        this.nameTable = nt;
    }

    Yylex(Reader in) {
        this.zzLexicalState = 0;
        this.zzBuffer = new char[16384];
        this.zzAtBOL = true;
        this.zzReader = in;
    }

    Yylex(InputStream in) {
        this(new InputStreamReader(in));
    }

    private static char[] zzUnpackCMap(String packed) {
        int j;
        char[] map = new char[65536];
        int count = 0;
        int j2 = 0;
        while (count < 100) {
            int i = count + 1;
            int count2 = packed.charAt(count);
            int i2 = i + 1;
            char value = packed.charAt(i);
            while (true) {
                j = j2 + 1;
                map[j2] = value;
                count2--;
                if (count2 <= 0) {
                    break;
                }
                j2 = j;
            }
            count = i2;
            j2 = j;
        }
        return map;
    }

    private boolean zzRefill() throws IOException {
        int i = this.zzStartRead;
        if (i > 0) {
            char[] cArr = this.zzBuffer;
            System.arraycopy(cArr, i, cArr, 0, this.zzEndRead - i);
            int i2 = this.zzEndRead;
            int i3 = this.zzStartRead;
            this.zzEndRead = i2 - i3;
            this.zzCurrentPos -= i3;
            this.zzMarkedPos -= i3;
            this.zzPushbackPos -= i3;
            this.zzStartRead = 0;
        }
        int i4 = this.zzCurrentPos;
        char[] cArr2 = this.zzBuffer;
        if (i4 >= cArr2.length) {
            char[] newBuffer = new char[i4 * 2];
            System.arraycopy(cArr2, 0, newBuffer, 0, cArr2.length);
            this.zzBuffer = newBuffer;
        }
        Reader reader = this.zzReader;
        char[] cArr3 = this.zzBuffer;
        int i5 = this.zzEndRead;
        int numRead = reader.read(cArr3, i5, cArr3.length - i5);
        if (numRead < 0) {
            return true;
        }
        this.zzEndRead += numRead;
        return false;
    }

    public final void yyclose() throws IOException {
        this.zzAtEOF = true;
        this.zzEndRead = this.zzStartRead;
        Reader reader = this.zzReader;
        if (reader != null) {
            reader.close();
        }
    }

    public final void yyreset(Reader reader) {
        this.zzReader = reader;
        this.zzAtBOL = true;
        this.zzAtEOF = false;
        this.zzStartRead = 0;
        this.zzEndRead = 0;
        this.zzPushbackPos = 0;
        this.zzMarkedPos = 0;
        this.zzCurrentPos = 0;
        this.yycolumn = 0;
        this.yychar = 0;
        this.yyline = 0;
        this.zzLexicalState = 0;
    }

    public final int yystate() {
        return this.zzLexicalState;
    }

    public final void yybegin(int newState) {
        this.zzLexicalState = newState;
    }

    public final String yytext() {
        char[] cArr = this.zzBuffer;
        int i = this.zzStartRead;
        return new String(cArr, i, this.zzMarkedPos - i);
    }

    public final char yycharat(int pos) {
        return this.zzBuffer[this.zzStartRead + pos];
    }

    public final int yylength() {
        return this.zzMarkedPos - this.zzStartRead;
    }

    private void zzScanError(int errorCode) {
        String message;
        try {
            message = ZZ_ERROR_MSG[errorCode];
        } catch (ArrayIndexOutOfBoundsException e) {
            message = ZZ_ERROR_MSG[0];
        }
        throw new Error(message);
    }

    public void yypushback(int number) {
        if (number > yylength()) {
            zzScanError(2);
        }
        this.zzMarkedPos -= number;
    }

    public ParseItem yylex() throws IOException, FormulaException {
        int zzNext;
        int zzCurrentPosL;
        char c;
        boolean zzPeek;
        int zzEndReadL = this.zzEndRead;
        char[] zzBufferL = this.zzBuffer;
        char[] zzCMapL = ZZ_CMAP;
        int[] zzTransL = ZZ_TRANS;
        int[] zzRowMapL = ZZ_ROWMAP;
        int[] zzAttrL = ZZ_ATTRIBUTE;
        while (true) {
            int zzMarkedPosL = this.zzMarkedPos;
            this.yychar += zzMarkedPosL - this.zzStartRead;
            boolean zzR = false;
            int zzCurrentPosL2 = this.zzStartRead;
            while (true) {
                int i = 1;
                if (zzCurrentPosL2 < zzMarkedPosL) {
                    char c2 = zzBufferL[zzCurrentPosL2];
                    if (c2 != 133 && c2 != 8232 && c2 != 8233) {
                        switch (c2) {
                            case '\n':
                                if (!zzR) {
                                    this.yyline++;
                                    break;
                                } else {
                                    zzR = false;
                                    break;
                                }
                            case 11:
                            case '\f':
                                break;
                            case '\r':
                                this.yyline++;
                                zzR = true;
                                break;
                            default:
                                zzR = false;
                                break;
                        }
                        zzCurrentPosL2++;
                    }
                    this.yyline++;
                    zzR = false;
                    zzCurrentPosL2++;
                } else {
                    if (zzR) {
                        if (zzMarkedPosL < zzEndReadL) {
                            zzPeek = zzBufferL[zzMarkedPosL] == '\n';
                        } else if (this.zzAtEOF) {
                            zzPeek = false;
                        } else {
                            boolean eof = zzRefill();
                            zzEndReadL = this.zzEndRead;
                            zzMarkedPosL = this.zzMarkedPos;
                            zzBufferL = this.zzBuffer;
                            zzPeek = eof ? false : zzBufferL[zzMarkedPosL] == '\n';
                        }
                        if (zzPeek) {
                            this.yyline--;
                        }
                    }
                    int zzAction = -1;
                    this.zzStartRead = zzMarkedPosL;
                    this.zzCurrentPos = zzMarkedPosL;
                    int zzInput = zzMarkedPosL;
                    this.zzState = this.zzLexicalState;
                    while (true) {
                        if (zzInput < zzEndReadL) {
                            int zzCurrentPosL3 = zzInput + 1;
                            c = zzBufferL[zzInput];
                            zzCurrentPosL = zzCurrentPosL3;
                        } else if (this.zzAtEOF) {
                            zzNext = -1;
                        } else {
                            this.zzCurrentPos = zzInput;
                            this.zzMarkedPos = zzMarkedPosL;
                            boolean eof2 = zzRefill();
                            int zzCurrentPosL4 = this.zzCurrentPos;
                            zzMarkedPosL = this.zzMarkedPos;
                            zzBufferL = this.zzBuffer;
                            zzEndReadL = this.zzEndRead;
                            if (eof2) {
                                zzNext = -1;
                            } else {
                                zzCurrentPosL = zzCurrentPosL4 + 1;
                                c = zzBufferL[zzCurrentPosL4];
                            }
                        }
                        int zzNext2 = zzTransL[zzRowMapL[this.zzState] + zzCMapL[c]];
                        if (zzNext2 == -1) {
                            zzNext = c;
                        } else {
                            this.zzState = zzNext2;
                            int zzAttributes = zzAttrL[zzNext2];
                            if ((zzAttributes & 1) == i) {
                                zzAction = this.zzState;
                                zzMarkedPosL = zzCurrentPosL;
                                if ((zzAttributes & 8) == 8) {
                                    zzNext = c;
                                } else {
                                    i = 1;
                                }
                            }
                            zzInput = zzCurrentPosL;
                        }
                    }
                    this.zzMarkedPos = zzMarkedPosL;
                    switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
                        case 1:
                            this.emptyString = false;
                            return new StringValue(yytext());
                        case 2:
                            return new NameRange(yytext(), this.nameTable);
                        case 3:
                            return new IntegerValue(yytext());
                        case 4:
                            return new RangeSeparator();
                        case 5:
                            return new ArgumentSeparator();
                        case 6:
                            return new OpenParentheses();
                        case 7:
                            return new CloseParentheses();
                        case 8:
                            this.emptyString = true;
                            yybegin(1);
                            break;
                        case 9:
                        case 31:
                        case 32:
                        case 33:
                        case 34:
                        case 35:
                        case 36:
                        case 37:
                        case 38:
                        case 39:
                        case 40:
                        case 41:
                        case 42:
                        case 43:
                        case 44:
                        case 45:
                        case 46:
                        case 47:
                        case 48:
                        case 49:
                        case 50:
                        case 51:
                        case 52:
                        case 53:
                        case 54:
                        case 55:
                        case 56:
                        case 57:
                        case 58:
                        case 59:
                        case 60:
                            break;
                        case 10:
                            return new Divide();
                        case 11:
                            return new Plus();
                        case 12:
                            return new Minus();
                        case 13:
                            return new Multiply();
                        case 14:
                            return new GreaterThan();
                        case 15:
                            return new Equal();
                        case 16:
                            return new LessThan();
                        case 17:
                            yybegin(0);
                            if (!this.emptyString) {
                                break;
                            } else {
                                return new StringValue("");
                            }
                        case 18:
                            return new CellReference(yytext());
                        case 19:
                            return new StringFunction(yytext());
                        case 20:
                            return new GreaterEqual();
                        case 21:
                            return new NotEqual();
                        case 22:
                            return new LessEqual();
                        case 23:
                            return new ColumnRange(yytext());
                        case 24:
                            return new DoubleValue(yytext());
                        case 25:
                            return new CellReference3d(yytext(), this.externalSheet);
                        case 26:
                            return new BooleanValue(yytext());
                        case 27:
                            return new Area(yytext());
                        case 28:
                            return new ErrorConstant(yytext());
                        case 29:
                            return new ColumnRange3d(yytext(), this.externalSheet);
                        case 30:
                            return new Area3d(yytext(), this.externalSheet);
                        default:
                            if (zzNext == -1 && this.zzStartRead == this.zzCurrentPos) {
                                this.zzAtEOF = true;
                                return null;
                            }
                            zzScanError(1);
                            break;
                            break;
                    }
                }
            }
        }
    }
}
