package jxl.biff;

import jxl.WorkbookSettings;
import jxl.biff.formula.ExternalSheet;
import jxl.biff.formula.FormulaException;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.read.biff.Record;

/* loaded from: classes.dex */
public class DataValiditySettingsRecord extends WritableRecordData {
    private static Logger logger = Logger.getLogger(DataValiditySettingsRecord.class);
    private byte[] data;
    private DataValidation dataValidation;
    private DVParser dvParser;
    private ExternalSheet externalSheet;
    private WorkbookMethods workbook;
    private WorkbookSettings workbookSettings;

    public DataValiditySettingsRecord(Record t, ExternalSheet es, WorkbookMethods wm, WorkbookSettings ws) {
        super(t);
        this.data = t.getData();
        this.externalSheet = es;
        this.workbook = wm;
        this.workbookSettings = ws;
    }

    DataValiditySettingsRecord(DataValiditySettingsRecord dvsr) {
        super(Type.DV);
        this.data = dvsr.getData();
    }

    DataValiditySettingsRecord(DataValiditySettingsRecord dvsr, ExternalSheet es, WorkbookMethods w, WorkbookSettings ws) {
        super(Type.DV);
        this.workbook = w;
        this.externalSheet = es;
        this.workbookSettings = ws;
        Assert.verify(w != null);
        Assert.verify(es != null);
        byte[] bArr = new byte[dvsr.data.length];
        this.data = bArr;
        System.arraycopy(dvsr.data, 0, bArr, 0, bArr.length);
    }

    public DataValiditySettingsRecord(DVParser dvp) {
        super(Type.DV);
        this.dvParser = dvp;
    }

    private void initialize() {
        if (this.dvParser == null) {
            this.dvParser = new DVParser(this.data, this.externalSheet, this.workbook, this.workbookSettings);
        }
    }

    @Override // jxl.biff.WritableRecordData
    public byte[] getData() {
        DVParser dVParser = this.dvParser;
        if (dVParser == null) {
            return this.data;
        }
        return dVParser.getData();
    }

    public void insertRow(int row) {
        if (this.dvParser == null) {
            initialize();
        }
        this.dvParser.insertRow(row);
    }

    public void removeRow(int row) {
        if (this.dvParser == null) {
            initialize();
        }
        this.dvParser.removeRow(row);
    }

    public void insertColumn(int col) {
        if (this.dvParser == null) {
            initialize();
        }
        this.dvParser.insertColumn(col);
    }

    public void removeColumn(int col) {
        if (this.dvParser == null) {
            initialize();
        }
        this.dvParser.removeColumn(col);
    }

    public int getFirstColumn() {
        if (this.dvParser == null) {
            initialize();
        }
        return this.dvParser.getFirstColumn();
    }

    public int getLastColumn() {
        if (this.dvParser == null) {
            initialize();
        }
        return this.dvParser.getLastColumn();
    }

    public int getFirstRow() {
        if (this.dvParser == null) {
            initialize();
        }
        return this.dvParser.getFirstRow();
    }

    public int getLastRow() {
        if (this.dvParser == null) {
            initialize();
        }
        return this.dvParser.getLastRow();
    }

    void setDataValidation(DataValidation dv) {
        this.dataValidation = dv;
    }

    DVParser getDVParser() {
        return this.dvParser;
    }

    public String getValidationFormula() {
        try {
            if (this.dvParser == null) {
                initialize();
            }
            return this.dvParser.getValidationFormula();
        } catch (FormulaException e) {
            logger.warn("Cannot read drop down range " + e.getMessage());
            return "";
        }
    }
}
