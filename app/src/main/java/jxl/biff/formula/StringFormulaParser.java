package jxl.biff.formula;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import jxl.WorkbookSettings;
import jxl.biff.WorkbookMethods;
import jxl.common.Logger;

/* loaded from: classes.dex */
class StringFormulaParser implements Parser {
    private static Logger logger = Logger.getLogger(StringFormulaParser.class);
    private Stack arguments;
    private ExternalSheet externalSheet;
    private String formula;
    private WorkbookMethods nameTable;
    private ParseContext parseContext;
    private String parsedFormula;
    private ParseItem root;
    private WorkbookSettings settings;

    public StringFormulaParser(String f, ExternalSheet es, WorkbookMethods nt, WorkbookSettings ws, ParseContext pc) {
        this.formula = f;
        this.settings = ws;
        this.externalSheet = es;
        this.nameTable = nt;
        this.parseContext = pc;
    }

    @Override // jxl.biff.formula.Parser
    public void parse() throws FormulaException {
        ArrayList tokens = getTokens();
        Iterator i = tokens.iterator();
        this.root = parseCurrent(i);
    }

    private ParseItem parseCurrent(Iterator i) throws FormulaException {
        Stack stack = new Stack();
        Stack operators = new Stack();
        Stack args = null;
        boolean parenthesesClosed = false;
        ParseItem lastParseItem = null;
        while (i.hasNext() && !parenthesesClosed) {
            ParseItem pi = (ParseItem) i.next();
            pi.setParseContext(this.parseContext);
            if (pi instanceof Operand) {
                handleOperand((Operand) pi, stack);
            } else if (pi instanceof StringFunction) {
                handleFunction((StringFunction) pi, i, stack);
            } else if (pi instanceof Operator) {
                Operator op = (Operator) pi;
                if (op instanceof StringOperator) {
                    StringOperator sop = (StringOperator) op;
                    if (stack.isEmpty() || (lastParseItem instanceof Operator)) {
                        op = sop.getUnaryOperator();
                    } else {
                        op = sop.getBinaryOperator();
                    }
                }
                if (operators.empty()) {
                    operators.push(op);
                } else {
                    Operator operator = (Operator) operators.peek();
                    if (op.getPrecedence() < operator.getPrecedence()) {
                        operators.push(op);
                    } else if (op.getPrecedence() == operator.getPrecedence() && (op instanceof UnaryOperator)) {
                        operators.push(op);
                    } else {
                        operators.pop();
                        operator.getOperands(stack);
                        stack.push(operator);
                        operators.push(op);
                    }
                }
            } else if (pi instanceof ArgumentSeparator) {
                while (!operators.isEmpty()) {
                    Operator o = (Operator) operators.pop();
                    o.getOperands(stack);
                    stack.push(o);
                }
                if (args == null) {
                    args = new Stack();
                }
                args.push(stack.pop());
                stack.clear();
            } else if (pi instanceof OpenParentheses) {
                ParseItem pi2 = parseCurrent(i);
                Parenthesis p = new Parenthesis();
                pi2.setParent(p);
                p.add(pi2);
                stack.push(p);
            } else if (pi instanceof CloseParentheses) {
                parenthesesClosed = true;
            }
            lastParseItem = pi;
        }
        while (!operators.isEmpty()) {
            Operator o2 = (Operator) operators.pop();
            o2.getOperands(stack);
            stack.push(o2);
        }
        ParseItem rt = !stack.empty() ? (ParseItem) stack.pop() : null;
        if (args != null && rt != null) {
            args.push(rt);
        }
        this.arguments = args;
        if (!stack.empty() || !operators.empty()) {
            logger.warn("Formula " + this.formula + " has a non-empty parse stack");
        }
        return rt;
    }

    private ArrayList getTokens() throws FormulaException {
        ArrayList tokens = new ArrayList();
        StringReader sr = new StringReader(this.formula);
        Yylex lex = new Yylex(sr);
        lex.setExternalSheet(this.externalSheet);
        lex.setNameTable(this.nameTable);
        try {
            for (ParseItem pi = lex.yylex(); pi != null; pi = lex.yylex()) {
                tokens.add(pi);
            }
        } catch (IOException e) {
            logger.warn(e.toString());
        } catch (Error e2) {
            throw new FormulaException(FormulaException.LEXICAL_ERROR, this.formula + " at char  " + lex.getPos());
        }
        return tokens;
    }

    @Override // jxl.biff.formula.Parser
    public String getFormula() {
        if (this.parsedFormula == null) {
            StringBuffer sb = new StringBuffer();
            this.root.getString(sb);
            this.parsedFormula = sb.toString();
        }
        return this.parsedFormula;
    }

    @Override // jxl.biff.formula.Parser
    public byte[] getBytes() {
        byte[] bytes = this.root.getBytes();
        if (this.root.isVolatile()) {
            byte[] newBytes = new byte[bytes.length + 4];
            System.arraycopy(bytes, 0, newBytes, 4, bytes.length);
            newBytes[0] = Token.ATTRIBUTE.getCode();
            newBytes[1] = 1;
            return newBytes;
        }
        return bytes;
    }

    private void handleFunction(StringFunction sf, Iterator i, Stack stack) throws FormulaException {
        ParseItem pi2 = parseCurrent(i);
        if (sf.getFunction(this.settings) == Function.UNKNOWN) {
            throw new FormulaException(FormulaException.UNRECOGNIZED_FUNCTION);
        }
        if (sf.getFunction(this.settings) == Function.SUM && this.arguments == null) {
            Attribute a = new Attribute(sf, this.settings);
            a.add(pi2);
            stack.push(a);
            return;
        }
        if (sf.getFunction(this.settings) == Function.IF) {
            Attribute a2 = new Attribute(sf, this.settings);
            VariableArgFunction vaf = new VariableArgFunction(this.settings);
            int numargs = this.arguments.size();
            for (int j = 0; j < numargs; j++) {
                ParseItem pi3 = (ParseItem) this.arguments.get(j);
                vaf.add(pi3);
            }
            a2.setIfConditions(vaf);
            stack.push(a2);
            return;
        }
        if (sf.getFunction(this.settings).getNumArgs() == 255) {
            Stack stack2 = this.arguments;
            if (stack2 == null) {
                int numArgs = pi2 == null ? 0 : 1;
                VariableArgFunction vaf2 = new VariableArgFunction(sf.getFunction(this.settings), numArgs, this.settings);
                if (pi2 != null) {
                    vaf2.add(pi2);
                }
                stack.push(vaf2);
                return;
            }
            int numargs2 = stack2.size();
            VariableArgFunction vaf3 = new VariableArgFunction(sf.getFunction(this.settings), numargs2, this.settings);
            ParseItem[] args = new ParseItem[numargs2];
            for (int j2 = 0; j2 < numargs2; j2++) {
                ParseItem pi32 = (ParseItem) this.arguments.pop();
                args[(numargs2 - j2) - 1] = pi32;
            }
            for (ParseItem parseItem : args) {
                vaf3.add(parseItem);
            }
            stack.push(vaf3);
            this.arguments.clear();
            this.arguments = null;
            return;
        }
        BuiltInFunction bif = new BuiltInFunction(sf.getFunction(this.settings), this.settings);
        int numargs3 = sf.getFunction(this.settings).getNumArgs();
        if (numargs3 == 1) {
            bif.add(pi2);
        } else {
            Stack stack3 = this.arguments;
            if ((stack3 == null && numargs3 != 0) || (stack3 != null && numargs3 != stack3.size())) {
                throw new FormulaException(FormulaException.INCORRECT_ARGUMENTS);
            }
            for (int j3 = 0; j3 < numargs3; j3++) {
                ParseItem pi33 = (ParseItem) this.arguments.get(j3);
                bif.add(pi33);
            }
        }
        stack.push(bif);
    }

    @Override // jxl.biff.formula.Parser
    public void adjustRelativeCellReferences(int colAdjust, int rowAdjust) {
        this.root.adjustRelativeCellReferences(colAdjust, rowAdjust);
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

    private void handleOperand(Operand o, Stack stack) {
        if (!(o instanceof IntegerValue)) {
            stack.push(o);
            return;
        }
        if (o instanceof IntegerValue) {
            IntegerValue iv = (IntegerValue) o;
            if (!iv.isOutOfRange()) {
                stack.push(iv);
            } else {
                DoubleValue dv = new DoubleValue(iv.getValue());
                stack.push(dv);
            }
        }
    }

    @Override // jxl.biff.formula.Parser
    public boolean handleImportedCellReferences() {
        this.root.handleImportedCellReferences();
        return this.root.isValid();
    }
}
