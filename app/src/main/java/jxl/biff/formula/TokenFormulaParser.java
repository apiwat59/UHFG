package jxl.biff.formula;

import java.util.Stack;
import jxl.Cell;
import jxl.WorkbookSettings;
import jxl.biff.WorkbookMethods;
import jxl.common.Assert;
import jxl.common.Logger;

/* loaded from: classes.dex */
class TokenFormulaParser implements Parser {
    private static Logger logger = Logger.getLogger(TokenFormulaParser.class);
    private WorkbookMethods nameTable;
    private ParseContext parseContext;
    private Cell relativeTo;
    private ParseItem root;
    private WorkbookSettings settings;
    private byte[] tokenData;
    private ExternalSheet workbook;
    private int pos = 0;
    private Stack tokenStack = new Stack();

    public TokenFormulaParser(byte[] data, Cell c, ExternalSheet es, WorkbookMethods nt, WorkbookSettings ws, ParseContext pc) {
        this.tokenData = data;
        this.relativeTo = c;
        this.workbook = es;
        this.nameTable = nt;
        this.settings = ws;
        this.parseContext = pc;
        Assert.verify(this.nameTable != null);
    }

    @Override // jxl.biff.formula.Parser
    public void parse() throws FormulaException {
        parseSubExpression(this.tokenData.length);
        this.root = (ParseItem) this.tokenStack.pop();
        Assert.verify(this.tokenStack.empty());
    }

    private void parseSubExpression(int len) throws FormulaException {
        Attribute ifattr;
        Stack ifStack = new Stack();
        int endpos = this.pos + len;
        while (true) {
            int i = this.pos;
            if (i < endpos) {
                int tokenVal = this.tokenData[i];
                this.pos = i + 1;
                Token t = Token.getToken(tokenVal);
                if (t == Token.UNKNOWN) {
                    throw new FormulaException(FormulaException.UNRECOGNIZED_TOKEN, tokenVal);
                }
                Assert.verify(t != Token.UNKNOWN);
                if (t == Token.REF) {
                    CellReference cr = new CellReference(this.relativeTo);
                    int i2 = this.pos;
                    this.pos = i2 + cr.read(this.tokenData, i2);
                    this.tokenStack.push(cr);
                } else if (t == Token.REFERR) {
                    CellReferenceError cr2 = new CellReferenceError();
                    int i3 = this.pos;
                    this.pos = i3 + cr2.read(this.tokenData, i3);
                    this.tokenStack.push(cr2);
                } else if (t == Token.ERR) {
                    ErrorConstant ec = new ErrorConstant();
                    int i4 = this.pos;
                    this.pos = i4 + ec.read(this.tokenData, i4);
                    this.tokenStack.push(ec);
                } else if (t == Token.REFV) {
                    SharedFormulaCellReference cr3 = new SharedFormulaCellReference(this.relativeTo);
                    int i5 = this.pos;
                    this.pos = i5 + cr3.read(this.tokenData, i5);
                    this.tokenStack.push(cr3);
                } else if (t == Token.REF3D) {
                    CellReference3d cr4 = new CellReference3d(this.relativeTo, this.workbook);
                    int i6 = this.pos;
                    this.pos = i6 + cr4.read(this.tokenData, i6);
                    this.tokenStack.push(cr4);
                } else if (t == Token.AREA) {
                    Area a = new Area();
                    int i7 = this.pos;
                    this.pos = i7 + a.read(this.tokenData, i7);
                    this.tokenStack.push(a);
                } else if (t == Token.AREAV) {
                    SharedFormulaArea a2 = new SharedFormulaArea(this.relativeTo);
                    int i8 = this.pos;
                    this.pos = i8 + a2.read(this.tokenData, i8);
                    this.tokenStack.push(a2);
                } else if (t == Token.AREA3D) {
                    Area3d a3 = new Area3d(this.workbook);
                    int i9 = this.pos;
                    this.pos = i9 + a3.read(this.tokenData, i9);
                    this.tokenStack.push(a3);
                } else if (t == Token.NAME) {
                    Name n = new Name();
                    int i10 = this.pos;
                    this.pos = i10 + n.read(this.tokenData, i10);
                    n.setParseContext(this.parseContext);
                    this.tokenStack.push(n);
                } else if (t == Token.NAMED_RANGE) {
                    NameRange nr = new NameRange(this.nameTable);
                    int i11 = this.pos;
                    this.pos = i11 + nr.read(this.tokenData, i11);
                    nr.setParseContext(this.parseContext);
                    this.tokenStack.push(nr);
                } else if (t == Token.INTEGER) {
                    IntegerValue i12 = new IntegerValue();
                    int i13 = this.pos;
                    this.pos = i13 + i12.read(this.tokenData, i13);
                    this.tokenStack.push(i12);
                } else if (t == Token.DOUBLE) {
                    DoubleValue d = new DoubleValue();
                    int i14 = this.pos;
                    this.pos = i14 + d.read(this.tokenData, i14);
                    this.tokenStack.push(d);
                } else if (t == Token.BOOL) {
                    BooleanValue bv = new BooleanValue();
                    int i15 = this.pos;
                    this.pos = i15 + bv.read(this.tokenData, i15);
                    this.tokenStack.push(bv);
                } else if (t == Token.STRING) {
                    StringValue sv = new StringValue(this.settings);
                    int i16 = this.pos;
                    this.pos = i16 + sv.read(this.tokenData, i16);
                    this.tokenStack.push(sv);
                } else if (t == Token.MISSING_ARG) {
                    MissingArg ma = new MissingArg();
                    int i17 = this.pos;
                    this.pos = i17 + ma.read(this.tokenData, i17);
                    this.tokenStack.push(ma);
                } else if (t == Token.UNARY_PLUS) {
                    UnaryPlus up = new UnaryPlus();
                    int i18 = this.pos;
                    this.pos = i18 + up.read(this.tokenData, i18);
                    addOperator(up);
                } else if (t == Token.UNARY_MINUS) {
                    UnaryMinus um = new UnaryMinus();
                    int i19 = this.pos;
                    this.pos = i19 + um.read(this.tokenData, i19);
                    addOperator(um);
                } else if (t == Token.PERCENT) {
                    Percent p = new Percent();
                    int i20 = this.pos;
                    this.pos = i20 + p.read(this.tokenData, i20);
                    addOperator(p);
                } else if (t == Token.SUBTRACT) {
                    Subtract s = new Subtract();
                    int i21 = this.pos;
                    this.pos = i21 + s.read(this.tokenData, i21);
                    addOperator(s);
                } else if (t == Token.ADD) {
                    Add s2 = new Add();
                    int i22 = this.pos;
                    this.pos = i22 + s2.read(this.tokenData, i22);
                    addOperator(s2);
                } else if (t == Token.MULTIPLY) {
                    Multiply s3 = new Multiply();
                    int i23 = this.pos;
                    this.pos = i23 + s3.read(this.tokenData, i23);
                    addOperator(s3);
                } else if (t == Token.DIVIDE) {
                    Divide s4 = new Divide();
                    int i24 = this.pos;
                    this.pos = i24 + s4.read(this.tokenData, i24);
                    addOperator(s4);
                } else if (t == Token.CONCAT) {
                    Concatenate c = new Concatenate();
                    int i25 = this.pos;
                    this.pos = i25 + c.read(this.tokenData, i25);
                    addOperator(c);
                } else if (t == Token.POWER) {
                    Power p2 = new Power();
                    int i26 = this.pos;
                    this.pos = i26 + p2.read(this.tokenData, i26);
                    addOperator(p2);
                } else if (t == Token.LESS_THAN) {
                    LessThan lt = new LessThan();
                    int i27 = this.pos;
                    this.pos = i27 + lt.read(this.tokenData, i27);
                    addOperator(lt);
                } else if (t == Token.LESS_EQUAL) {
                    LessEqual lte = new LessEqual();
                    int i28 = this.pos;
                    this.pos = i28 + lte.read(this.tokenData, i28);
                    addOperator(lte);
                } else if (t == Token.GREATER_THAN) {
                    GreaterThan gt = new GreaterThan();
                    int i29 = this.pos;
                    this.pos = i29 + gt.read(this.tokenData, i29);
                    addOperator(gt);
                } else if (t == Token.GREATER_EQUAL) {
                    GreaterEqual gte = new GreaterEqual();
                    int i30 = this.pos;
                    this.pos = i30 + gte.read(this.tokenData, i30);
                    addOperator(gte);
                } else if (t == Token.NOT_EQUAL) {
                    NotEqual ne = new NotEqual();
                    int i31 = this.pos;
                    this.pos = i31 + ne.read(this.tokenData, i31);
                    addOperator(ne);
                } else if (t == Token.EQUAL) {
                    Equal e = new Equal();
                    int i32 = this.pos;
                    this.pos = i32 + e.read(this.tokenData, i32);
                    addOperator(e);
                } else if (t == Token.PARENTHESIS) {
                    Parenthesis p3 = new Parenthesis();
                    int i33 = this.pos;
                    this.pos = i33 + p3.read(this.tokenData, i33);
                    addOperator(p3);
                } else if (t == Token.ATTRIBUTE) {
                    Attribute a4 = new Attribute(this.settings);
                    int i34 = this.pos;
                    this.pos = i34 + a4.read(this.tokenData, i34);
                    if (a4.isSum()) {
                        addOperator(a4);
                    } else if (a4.isIf()) {
                        ifStack.push(a4);
                    }
                } else if (t == Token.FUNCTION) {
                    BuiltInFunction bif = new BuiltInFunction(this.settings);
                    int i35 = this.pos;
                    this.pos = i35 + bif.read(this.tokenData, i35);
                    addOperator(bif);
                } else if (t == Token.FUNCTIONVARARG) {
                    VariableArgFunction vaf = new VariableArgFunction(this.settings);
                    int i36 = this.pos;
                    this.pos = i36 + vaf.read(this.tokenData, i36);
                    if (vaf.getFunction() != Function.ATTRIBUTE) {
                        addOperator(vaf);
                    } else {
                        vaf.getOperands(this.tokenStack);
                        if (ifStack.empty()) {
                            ifattr = new Attribute(this.settings);
                        } else {
                            ifattr = (Attribute) ifStack.pop();
                        }
                        ifattr.setIfConditions(vaf);
                        this.tokenStack.push(ifattr);
                    }
                } else if (t == Token.MEM_FUNC) {
                    SubExpression memFunc = new MemFunc();
                    handleMemoryFunction(memFunc);
                } else if (t == Token.MEM_AREA) {
                    SubExpression memArea = new MemArea();
                    handleMemoryFunction(memArea);
                }
            } else {
                return;
            }
        }
    }

    private void handleMemoryFunction(SubExpression subxp) throws FormulaException {
        int i = this.pos;
        this.pos = i + subxp.read(this.tokenData, i);
        Stack oldStack = this.tokenStack;
        this.tokenStack = new Stack();
        parseSubExpression(subxp.getLength());
        ParseItem[] subexpr = new ParseItem[this.tokenStack.size()];
        int i2 = 0;
        while (!this.tokenStack.isEmpty()) {
            subexpr[i2] = (ParseItem) this.tokenStack.pop();
            i2++;
        }
        subxp.setSubExpression(subexpr);
        this.tokenStack = oldStack;
        oldStack.push(subxp);
    }

    private void addOperator(Operator o) {
        o.getOperands(this.tokenStack);
        this.tokenStack.push(o);
    }

    @Override // jxl.biff.formula.Parser
    public String getFormula() {
        StringBuffer sb = new StringBuffer();
        this.root.getString(sb);
        return sb.toString();
    }

    @Override // jxl.biff.formula.Parser
    public void adjustRelativeCellReferences(int colAdjust, int rowAdjust) {
        this.root.adjustRelativeCellReferences(colAdjust, rowAdjust);
    }

    @Override // jxl.biff.formula.Parser
    public byte[] getBytes() {
        return this.root.getBytes();
    }

    @Override // jxl.biff.formula.Parser
    public void columnInserted(int sheetIndex, int col, boolean currentSheet) {
        this.root.columnInserted(sheetIndex, col, currentSheet);
    }

    @Override // jxl.biff.formula.Parser
    public void columnRemoved(int sheetIndex, int col, boolean currentSheet) {
        this.root.columnRemoved(sheetIndex, col, currentSheet);
    }

    @Override // jxl.biff.formula.Parser
    public void rowInserted(int sheetIndex, int row, boolean currentSheet) {
        this.root.rowInserted(sheetIndex, row, currentSheet);
    }

    @Override // jxl.biff.formula.Parser
    public void rowRemoved(int sheetIndex, int row, boolean currentSheet) {
        this.root.rowRemoved(sheetIndex, row, currentSheet);
    }

    @Override // jxl.biff.formula.Parser
    public boolean handleImportedCellReferences() {
        this.root.handleImportedCellReferences();
        return this.root.isValid();
    }
}
