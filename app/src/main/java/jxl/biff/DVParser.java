package jxl.biff;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import jxl.WorkbookSettings;
import jxl.biff.formula.ExternalSheet;
import jxl.biff.formula.FormulaException;
import jxl.biff.formula.FormulaParser;
import jxl.biff.formula.ParseContext;
import jxl.common.Assert;
import jxl.common.Logger;
import kotlin.text.Typography;

/* loaded from: classes.dex */
public class DVParser {
    private static final int EMPTY_CELLS_ALLOWED_MASK = 256;
    private static final int MAX_COLUMNS = 255;
    private static final int MAX_ROWS = 65535;
    private static final int MAX_VALIDATION_LIST_LENGTH = 254;
    private static final int SHOW_ERROR_MASK = 524288;
    private static final int SHOW_PROMPT_MASK = 262144;
    private static final int STRING_LIST_GIVEN_MASK = 128;
    private static final int SUPPRESS_ARROW_MASK = 512;
    private int column1;
    private int column2;
    private Condition condition;
    private boolean copied;
    private boolean emptyCellsAllowed;
    private ErrorStyle errorStyle;
    private String errorText;
    private String errorTitle;
    private boolean extendedCellsValidation;
    private FormulaParser formula1;
    private String formula1String;
    private FormulaParser formula2;
    private String formula2String;
    private String promptText;
    private String promptTitle;
    private int row1;
    private int row2;
    private boolean showError;
    private boolean showPrompt;
    private boolean stringListGiven;
    private boolean suppressArrow;
    private DVType type;
    private static Logger logger = Logger.getLogger(DVParser.class);
    public static final DVType ANY = new DVType(0, "any");
    public static final DVType INTEGER = new DVType(1, "int");
    public static final DVType DECIMAL = new DVType(2, "dec");
    public static final DVType LIST = new DVType(3, "list");
    public static final DVType DATE = new DVType(4, "date");
    public static final DVType TIME = new DVType(5, "time");
    public static final DVType TEXT_LENGTH = new DVType(6, "strlen");
    public static final DVType FORMULA = new DVType(7, "form");
    public static final ErrorStyle STOP = new ErrorStyle(0);
    public static final ErrorStyle WARNING = new ErrorStyle(1);
    public static final ErrorStyle INFO = new ErrorStyle(2);
    public static final Condition BETWEEN = new Condition(0, "{0} <= x <= {1}");
    public static final Condition NOT_BETWEEN = new Condition(1, "!({0} <= x <= {1}");
    public static final Condition EQUAL = new Condition(2, "x == {0}");
    public static final Condition NOT_EQUAL = new Condition(3, "x != {0}");
    public static final Condition GREATER_THAN = new Condition(4, "x > {0}");
    public static final Condition LESS_THAN = new Condition(5, "x < {0}");
    public static final Condition GREATER_EQUAL = new Condition(6, "x >= {0}");
    public static final Condition LESS_EQUAL = new Condition(7, "x <= {0}");
    private static DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    public static class DVType {
        private static DVType[] types = new DVType[0];
        private String desc;
        private int value;

        DVType(int v, String d) {
            this.value = v;
            this.desc = d;
            DVType[] oldtypes = types;
            DVType[] dVTypeArr = new DVType[oldtypes.length + 1];
            types = dVTypeArr;
            System.arraycopy(oldtypes, 0, dVTypeArr, 0, oldtypes.length);
            types[oldtypes.length] = this;
        }

        static DVType getType(int v) {
            DVType found = null;
            int i = 0;
            while (true) {
                DVType[] dVTypeArr = types;
                if (i >= dVTypeArr.length || found != null) {
                    break;
                }
                if (dVTypeArr[i].value == v) {
                    found = dVTypeArr[i];
                }
                i++;
            }
            return found;
        }

        public int getValue() {
            return this.value;
        }

        public String getDescription() {
            return this.desc;
        }
    }

    public static class ErrorStyle {
        private static ErrorStyle[] types = new ErrorStyle[0];
        private int value;

        ErrorStyle(int v) {
            this.value = v;
            ErrorStyle[] oldtypes = types;
            ErrorStyle[] errorStyleArr = new ErrorStyle[oldtypes.length + 1];
            types = errorStyleArr;
            System.arraycopy(oldtypes, 0, errorStyleArr, 0, oldtypes.length);
            types[oldtypes.length] = this;
        }

        static ErrorStyle getErrorStyle(int v) {
            ErrorStyle found = null;
            int i = 0;
            while (true) {
                ErrorStyle[] errorStyleArr = types;
                if (i >= errorStyleArr.length || found != null) {
                    break;
                }
                if (errorStyleArr[i].value == v) {
                    found = errorStyleArr[i];
                }
                i++;
            }
            return found;
        }

        public int getValue() {
            return this.value;
        }
    }

    public static class Condition {
        private static Condition[] types = new Condition[0];
        private MessageFormat format;
        private int value;

        Condition(int v, String pattern) {
            this.value = v;
            this.format = new MessageFormat(pattern);
            Condition[] oldtypes = types;
            Condition[] conditionArr = new Condition[oldtypes.length + 1];
            types = conditionArr;
            System.arraycopy(oldtypes, 0, conditionArr, 0, oldtypes.length);
            types[oldtypes.length] = this;
        }

        static Condition getCondition(int v) {
            Condition found = null;
            int i = 0;
            while (true) {
                Condition[] conditionArr = types;
                if (i >= conditionArr.length || found != null) {
                    break;
                }
                if (conditionArr[i].value == v) {
                    found = conditionArr[i];
                }
                i++;
            }
            return found;
        }

        public int getValue() {
            return this.value;
        }

        public String getConditionString(String s1, String s2) {
            return this.format.format(new String[]{s1, s2});
        }
    }

    public DVParser(byte[] data, ExternalSheet es, WorkbookMethods nt, WorkbookSettings ws) {
        int pos;
        int pos2;
        int pos3;
        int pos4;
        byte[] tokens;
        int formula2Pos;
        int options;
        boolean z = true;
        Assert.verify(nt != null);
        this.copied = false;
        int options2 = IntegerHelper.getInt(data[0], data[1], data[2], data[3]);
        int typeVal = options2 & 15;
        this.type = DVType.getType(typeVal);
        int errorStyleVal = (options2 & 112) >> 4;
        this.errorStyle = ErrorStyle.getErrorStyle(errorStyleVal);
        int conditionVal = (15728640 & options2) >> 20;
        this.condition = Condition.getCondition(conditionVal);
        this.stringListGiven = (options2 & 128) != 0;
        this.emptyCellsAllowed = (options2 & 256) != 0;
        this.suppressArrow = (options2 & 512) != 0;
        this.showPrompt = (262144 & options2) != 0;
        this.showError = (524288 & options2) != 0;
        int length = IntegerHelper.getInt(data[4], data[4 + 1]);
        if (length > 0 && data[4 + 2] == 0) {
            this.promptTitle = StringHelper.getString(data, length, 4 + 3, ws);
            pos = 4 + length + 3;
        } else if (length > 0) {
            this.promptTitle = StringHelper.getUnicodeString(data, length, 4 + 3);
            pos = 4 + (length * 2) + 3;
        } else {
            pos = 4 + 3;
        }
        int length2 = IntegerHelper.getInt(data[pos], data[pos + 1]);
        if (length2 > 0 && data[pos + 2] == 0) {
            this.errorTitle = StringHelper.getString(data, length2, pos + 3, ws);
            pos2 = pos + length2 + 3;
        } else if (length2 > 0) {
            this.errorTitle = StringHelper.getUnicodeString(data, length2, pos + 3);
            pos2 = pos + (length2 * 2) + 3;
        } else {
            pos2 = pos + 3;
        }
        int length3 = IntegerHelper.getInt(data[pos2], data[pos2 + 1]);
        if (length3 > 0 && data[pos2 + 2] == 0) {
            this.promptText = StringHelper.getString(data, length3, pos2 + 3, ws);
            pos3 = pos2 + length3 + 3;
        } else if (length3 > 0) {
            this.promptText = StringHelper.getUnicodeString(data, length3, pos2 + 3);
            pos3 = pos2 + (length3 * 2) + 3;
        } else {
            pos3 = pos2 + 3;
        }
        int length4 = IntegerHelper.getInt(data[pos3], data[pos3 + 1]);
        if (length4 > 0 && data[pos3 + 2] == 0) {
            this.errorText = StringHelper.getString(data, length4, pos3 + 3, ws);
            pos4 = pos3 + length4 + 3;
        } else if (length4 > 0) {
            this.errorText = StringHelper.getUnicodeString(data, length4, pos3 + 3);
            pos4 = pos3 + (length4 * 2) + 3;
        } else {
            pos4 = pos3 + 3;
        }
        int formula1Length = IntegerHelper.getInt(data[pos4], data[pos4 + 1]);
        int pos5 = pos4 + 4;
        int pos6 = pos5 + formula1Length;
        int formula2Length = IntegerHelper.getInt(data[pos6], data[pos6 + 1]);
        int pos7 = pos6 + 4;
        int pos8 = pos7 + formula2Length + 2;
        this.row1 = IntegerHelper.getInt(data[pos8], data[pos8 + 1]);
        int pos9 = pos8 + 2;
        this.row2 = IntegerHelper.getInt(data[pos9], data[pos9 + 1]);
        int pos10 = pos9 + 2;
        this.column1 = IntegerHelper.getInt(data[pos10], data[pos10 + 1]);
        int pos11 = pos10 + 2;
        int i = IntegerHelper.getInt(data[pos11], data[pos11 + 1]);
        this.column2 = i;
        int i2 = pos11 + 2;
        int pos12 = this.row1;
        if (pos12 == this.row2 && this.column1 == i) {
            z = false;
        }
        this.extendedCellsValidation = z;
        try {
            EmptyCell tmprt = new EmptyCell(this.column1, pos12);
            if (formula1Length == 0) {
                formula2Pos = pos7;
                options = formula2Length;
            } else {
                try {
                    tokens = new byte[formula1Length];
                    System.arraycopy(data, pos5, tokens, 0, formula1Length);
                    formula2Pos = pos7;
                    options = formula2Length;
                } catch (FormulaException e) {
                    e = e;
                }
                try {
                    FormulaParser formulaParser = new FormulaParser(tokens, tmprt, es, nt, ws, ParseContext.DATA_VALIDATION);
                    this.formula1 = formulaParser;
                    formulaParser.parse();
                } catch (FormulaException e2) {
                    e = e2;
                    logger.warn(e.getMessage() + " for cells " + CellReferenceHelper.getCellReference(this.column1, this.row1) + "-" + CellReferenceHelper.getCellReference(this.column2, this.row2));
                }
            }
            if (options != 0) {
                try {
                    byte[] tokens2 = new byte[options];
                    System.arraycopy(data, formula2Pos, tokens2, 0, options);
                    try {
                        FormulaParser formulaParser2 = new FormulaParser(tokens2, tmprt, es, nt, ws, ParseContext.DATA_VALIDATION);
                        this.formula2 = formulaParser2;
                        formulaParser2.parse();
                    } catch (FormulaException e3) {
                        e = e3;
                        logger.warn(e.getMessage() + " for cells " + CellReferenceHelper.getCellReference(this.column1, this.row1) + "-" + CellReferenceHelper.getCellReference(this.column2, this.row2));
                    }
                } catch (FormulaException e4) {
                    e = e4;
                }
            }
        } catch (FormulaException e5) {
            e = e5;
        }
    }

    public DVParser(Collection strings) {
        this.copied = false;
        this.type = LIST;
        this.errorStyle = STOP;
        this.condition = BETWEEN;
        this.extendedCellsValidation = false;
        this.stringListGiven = true;
        this.emptyCellsAllowed = true;
        this.suppressArrow = false;
        this.showPrompt = true;
        this.showError = true;
        this.promptTitle = "\u0000";
        this.errorTitle = "\u0000";
        this.promptText = "\u0000";
        this.errorText = "\u0000";
        if (strings.size() == 0) {
            logger.warn("no validation strings - ignoring");
        }
        Iterator i = strings.iterator();
        StringBuffer formulaString = new StringBuffer();
        formulaString.append(i.next().toString());
        while (i.hasNext()) {
            formulaString.append((char) 0);
            formulaString.append(' ');
            formulaString.append(i.next().toString());
        }
        if (formulaString.length() > MAX_VALIDATION_LIST_LENGTH) {
            logger.warn("Validation list exceeds maximum number of characters - truncating");
            formulaString.delete(MAX_VALIDATION_LIST_LENGTH, formulaString.length());
        }
        formulaString.insert(0, Typography.quote);
        formulaString.append(Typography.quote);
        this.formula1String = formulaString.toString();
    }

    public DVParser(String namedRange) {
        if (namedRange.length() == 0) {
            this.copied = false;
            this.type = FORMULA;
            this.errorStyle = STOP;
            this.condition = EQUAL;
            this.extendedCellsValidation = false;
            this.stringListGiven = false;
            this.emptyCellsAllowed = false;
            this.suppressArrow = false;
            this.showPrompt = true;
            this.showError = true;
            this.promptTitle = "\u0000";
            this.errorTitle = "\u0000";
            this.promptText = "\u0000";
            this.errorText = "\u0000";
            this.formula1String = "\"\"";
            return;
        }
        this.copied = false;
        this.type = LIST;
        this.errorStyle = STOP;
        this.condition = BETWEEN;
        this.extendedCellsValidation = false;
        this.stringListGiven = false;
        this.emptyCellsAllowed = true;
        this.suppressArrow = false;
        this.showPrompt = true;
        this.showError = true;
        this.promptTitle = "\u0000";
        this.errorTitle = "\u0000";
        this.promptText = "\u0000";
        this.errorText = "\u0000";
        this.formula1String = namedRange;
    }

    public DVParser(int c1, int r1, int c2, int r2) {
        this.copied = false;
        this.type = LIST;
        this.errorStyle = STOP;
        this.condition = BETWEEN;
        this.extendedCellsValidation = false;
        this.stringListGiven = false;
        this.emptyCellsAllowed = true;
        this.suppressArrow = false;
        this.showPrompt = true;
        this.showError = true;
        this.promptTitle = "\u0000";
        this.errorTitle = "\u0000";
        this.promptText = "\u0000";
        this.errorText = "\u0000";
        StringBuffer formulaString = new StringBuffer();
        CellReferenceHelper.getCellReference(c1, r1, formulaString);
        formulaString.append(':');
        CellReferenceHelper.getCellReference(c2, r2, formulaString);
        this.formula1String = formulaString.toString();
    }

    public DVParser(double val1, double val2, Condition c) {
        this.copied = false;
        this.type = DECIMAL;
        this.errorStyle = STOP;
        this.condition = c;
        this.extendedCellsValidation = false;
        this.stringListGiven = false;
        this.emptyCellsAllowed = true;
        this.suppressArrow = false;
        this.showPrompt = true;
        this.showError = true;
        this.promptTitle = "\u0000";
        this.errorTitle = "\u0000";
        this.promptText = "\u0000";
        this.errorText = "\u0000";
        this.formula1String = DECIMAL_FORMAT.format(val1);
        if (!Double.isNaN(val2)) {
            this.formula2String = DECIMAL_FORMAT.format(val2);
        }
    }

    public DVParser(DVParser copy) {
        this.copied = true;
        this.type = copy.type;
        this.errorStyle = copy.errorStyle;
        this.condition = copy.condition;
        this.stringListGiven = copy.stringListGiven;
        this.emptyCellsAllowed = copy.emptyCellsAllowed;
        this.suppressArrow = copy.suppressArrow;
        this.showPrompt = copy.showPrompt;
        this.showError = copy.showError;
        this.promptTitle = copy.promptTitle;
        this.promptText = copy.promptText;
        this.errorTitle = copy.errorTitle;
        this.errorText = copy.errorText;
        this.extendedCellsValidation = copy.extendedCellsValidation;
        this.row1 = copy.row1;
        this.row2 = copy.row2;
        this.column1 = copy.column1;
        this.column2 = copy.column2;
        String str = copy.formula1String;
        if (str != null) {
            this.formula1String = str;
            this.formula2String = copy.formula2String;
            return;
        }
        try {
            this.formula1String = copy.formula1.getFormula();
            FormulaParser formulaParser = copy.formula2;
            this.formula2String = formulaParser != null ? formulaParser.getFormula() : null;
        } catch (FormulaException e) {
            logger.warn("Cannot parse validation formula:  " + e.getMessage());
        }
    }

    public byte[] getData() {
        FormulaParser formulaParser = this.formula1;
        byte[] f1Bytes = formulaParser != null ? formulaParser.getBytes() : new byte[0];
        FormulaParser formulaParser2 = this.formula2;
        byte[] f2Bytes = formulaParser2 != null ? formulaParser2.getBytes() : new byte[0];
        int dataLength = (this.promptTitle.length() * 2) + 4 + 3 + (this.errorTitle.length() * 2) + 3 + (this.promptText.length() * 2) + 3 + (this.errorText.length() * 2) + 3 + f1Bytes.length + 2 + f2Bytes.length + 2 + 4 + 10;
        byte[] data = new byte[dataLength];
        int options = 0 | this.type.getValue() | (this.errorStyle.getValue() << 4) | (this.condition.getValue() << 20);
        if (this.stringListGiven) {
            options |= 128;
        }
        if (this.emptyCellsAllowed) {
            options |= 256;
        }
        if (this.suppressArrow) {
            options |= 512;
        }
        if (this.showPrompt) {
            options |= 262144;
        }
        if (this.showError) {
            options |= 524288;
        }
        IntegerHelper.getFourBytes(options, data, 0);
        int pos = 0 + 4;
        IntegerHelper.getTwoBytes(this.promptTitle.length(), data, pos);
        int pos2 = pos + 2;
        data[pos2] = 1;
        int pos3 = pos2 + 1;
        StringHelper.getUnicodeBytes(this.promptTitle, data, pos3);
        int pos4 = pos3 + (this.promptTitle.length() * 2);
        IntegerHelper.getTwoBytes(this.errorTitle.length(), data, pos4);
        int pos5 = pos4 + 2;
        data[pos5] = 1;
        int pos6 = pos5 + 1;
        StringHelper.getUnicodeBytes(this.errorTitle, data, pos6);
        int pos7 = pos6 + (this.errorTitle.length() * 2);
        IntegerHelper.getTwoBytes(this.promptText.length(), data, pos7);
        int pos8 = pos7 + 2;
        data[pos8] = 1;
        int pos9 = pos8 + 1;
        StringHelper.getUnicodeBytes(this.promptText, data, pos9);
        int pos10 = pos9 + (this.promptText.length() * 2);
        IntegerHelper.getTwoBytes(this.errorText.length(), data, pos10);
        int pos11 = pos10 + 2;
        data[pos11] = 1;
        int pos12 = pos11 + 1;
        StringHelper.getUnicodeBytes(this.errorText, data, pos12);
        int pos13 = pos12 + (this.errorText.length() * 2);
        IntegerHelper.getTwoBytes(f1Bytes.length, data, pos13);
        int pos14 = pos13 + 4;
        System.arraycopy(f1Bytes, 0, data, pos14, f1Bytes.length);
        int pos15 = pos14 + f1Bytes.length;
        IntegerHelper.getTwoBytes(f2Bytes.length, data, pos15);
        int pos16 = pos15 + 4;
        System.arraycopy(f2Bytes, 0, data, pos16, f2Bytes.length);
        int pos17 = pos16 + f2Bytes.length;
        IntegerHelper.getTwoBytes(1, data, pos17);
        int pos18 = pos17 + 2;
        IntegerHelper.getTwoBytes(this.row1, data, pos18);
        int pos19 = pos18 + 2;
        IntegerHelper.getTwoBytes(this.row2, data, pos19);
        int pos20 = pos19 + 2;
        IntegerHelper.getTwoBytes(this.column1, data, pos20);
        int pos21 = pos20 + 2;
        IntegerHelper.getTwoBytes(this.column2, data, pos21);
        int i = pos21 + 2;
        return data;
    }

    public void insertRow(int row) {
        FormulaParser formulaParser = this.formula1;
        if (formulaParser != null) {
            formulaParser.rowInserted(0, row, true);
        }
        FormulaParser formulaParser2 = this.formula2;
        if (formulaParser2 != null) {
            formulaParser2.rowInserted(0, row, true);
        }
        int i = this.row1;
        if (i >= row) {
            this.row1 = i + 1;
        }
        int i2 = this.row2;
        if (i2 >= row && i2 != 65535) {
            this.row2 = i2 + 1;
        }
    }

    public void insertColumn(int col) {
        FormulaParser formulaParser = this.formula1;
        if (formulaParser != null) {
            formulaParser.columnInserted(0, col, true);
        }
        FormulaParser formulaParser2 = this.formula2;
        if (formulaParser2 != null) {
            formulaParser2.columnInserted(0, col, true);
        }
        int i = this.column1;
        if (i >= col) {
            this.column1 = i + 1;
        }
        int i2 = this.column2;
        if (i2 >= col && i2 != 255) {
            this.column2 = i2 + 1;
        }
    }

    public void removeRow(int row) {
        FormulaParser formulaParser = this.formula1;
        if (formulaParser != null) {
            formulaParser.rowRemoved(0, row, true);
        }
        FormulaParser formulaParser2 = this.formula2;
        if (formulaParser2 != null) {
            formulaParser2.rowRemoved(0, row, true);
        }
        int i = this.row1;
        if (i > row) {
            this.row1 = i - 1;
        }
        int i2 = this.row2;
        if (i2 >= row) {
            this.row2 = i2 - 1;
        }
    }

    public void removeColumn(int col) {
        FormulaParser formulaParser = this.formula1;
        if (formulaParser != null) {
            formulaParser.columnRemoved(0, col, true);
        }
        FormulaParser formulaParser2 = this.formula2;
        if (formulaParser2 != null) {
            formulaParser2.columnRemoved(0, col, true);
        }
        int i = this.column1;
        if (i > col) {
            this.column1 = i - 1;
        }
        int i2 = this.column2;
        if (i2 >= col && i2 != 255) {
            this.column2 = i2 - 1;
        }
    }

    public int getFirstColumn() {
        return this.column1;
    }

    public int getLastColumn() {
        return this.column2;
    }

    public int getFirstRow() {
        return this.row1;
    }

    public int getLastRow() {
        return this.row2;
    }

    String getValidationFormula() throws FormulaException {
        if (this.type == LIST) {
            return this.formula1.getFormula();
        }
        String s1 = this.formula1.getFormula();
        FormulaParser formulaParser = this.formula2;
        String s2 = formulaParser != null ? formulaParser.getFormula() : null;
        return this.condition.getConditionString(s1, s2) + "; x " + this.type.getDescription();
    }

    public void setCell(int col, int row, ExternalSheet es, WorkbookMethods nt, WorkbookSettings ws) throws FormulaException {
        if (this.extendedCellsValidation) {
            return;
        }
        this.row1 = row;
        this.row2 = row;
        this.column1 = col;
        this.column2 = col;
        FormulaParser formulaParser = new FormulaParser(this.formula1String, es, nt, ws, ParseContext.DATA_VALIDATION);
        this.formula1 = formulaParser;
        formulaParser.parse();
        if (this.formula2String != null) {
            FormulaParser formulaParser2 = new FormulaParser(this.formula2String, es, nt, ws, ParseContext.DATA_VALIDATION);
            this.formula2 = formulaParser2;
            formulaParser2.parse();
        }
    }

    public void extendCellValidation(int cols, int rows) {
        this.row2 = this.row1 + rows;
        this.column2 = this.column1 + cols;
        this.extendedCellsValidation = true;
    }

    public boolean extendedCellsValidation() {
        return this.extendedCellsValidation;
    }

    public boolean copied() {
        return this.copied;
    }
}
