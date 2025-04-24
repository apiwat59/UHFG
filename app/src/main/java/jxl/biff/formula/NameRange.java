package jxl.biff.formula;

import jxl.biff.IntegerHelper;
import jxl.biff.NameRangeException;
import jxl.biff.WorkbookMethods;
import jxl.common.Assert;
import jxl.common.Logger;

/* loaded from: classes.dex */
class NameRange extends Operand implements ParsedThing {
    private static Logger logger = Logger.getLogger(NameRange.class);
    private int index;
    private String name;
    private WorkbookMethods nameTable;

    public NameRange(WorkbookMethods nt) {
        this.nameTable = nt;
        Assert.verify(nt != null);
    }

    public NameRange(String nm, WorkbookMethods nt) throws FormulaException {
        this.name = nm;
        this.nameTable = nt;
        int nameIndex = nt.getNameIndex(nm);
        this.index = nameIndex;
        if (nameIndex < 0) {
            throw new FormulaException(FormulaException.CELL_NAME_NOT_FOUND, this.name);
        }
        this.index = nameIndex + 1;
    }

    @Override // jxl.biff.formula.ParsedThing
    public int read(byte[] data, int pos) throws FormulaException {
        try {
            int i = IntegerHelper.getInt(data[pos], data[pos + 1]);
            this.index = i;
            this.name = this.nameTable.getName(i - 1);
            return 4;
        } catch (NameRangeException e) {
            throw new FormulaException(FormulaException.CELL_NAME_NOT_FOUND, "");
        }
    }

    @Override // jxl.biff.formula.ParseItem
    byte[] getBytes() {
        byte[] data = new byte[5];
        data[0] = Token.NAMED_RANGE.getValueCode();
        if (getParseContext() == ParseContext.DATA_VALIDATION) {
            data[0] = Token.NAMED_RANGE.getReferenceCode();
        }
        IntegerHelper.getTwoBytes(this.index, data, 1);
        return data;
    }

    @Override // jxl.biff.formula.ParseItem
    public void getString(StringBuffer buf) {
        buf.append(this.name);
    }

    @Override // jxl.biff.formula.ParseItem
    void handleImportedCellReferences() {
        setInvalid();
    }
}
