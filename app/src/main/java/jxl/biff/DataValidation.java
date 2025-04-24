package jxl.biff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import jxl.WorkbookSettings;
import jxl.biff.formula.ExternalSheet;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.write.biff.File;

/* loaded from: classes.dex */
public class DataValidation {
    public static final int DEFAULT_OBJECT_ID = -1;
    private static final int MAX_NO_OF_VALIDITY_SETTINGS = 65533;
    private static Logger logger = Logger.getLogger(DataValidation.class);
    private int comboBoxObjectId;
    private boolean copied;
    private ExternalSheet externalSheet;
    private DataValidityListRecord validityList;
    private ArrayList validitySettings;
    private WorkbookMethods workbook;
    private WorkbookSettings workbookSettings;

    public DataValidation(DataValidityListRecord dvlr) {
        this.validityList = dvlr;
        this.validitySettings = new ArrayList(this.validityList.getNumberOfSettings());
        this.copied = false;
    }

    public DataValidation(int objId, ExternalSheet es, WorkbookMethods wm, WorkbookSettings ws) {
        this.workbook = wm;
        this.externalSheet = es;
        this.workbookSettings = ws;
        this.validitySettings = new ArrayList();
        this.comboBoxObjectId = objId;
        this.copied = false;
    }

    public DataValidation(DataValidation dv, ExternalSheet es, WorkbookMethods wm, WorkbookSettings ws) {
        this.workbook = wm;
        this.externalSheet = es;
        this.workbookSettings = ws;
        this.copied = true;
        this.validityList = new DataValidityListRecord(dv.getDataValidityList());
        this.validitySettings = new ArrayList();
        DataValiditySettingsRecord[] settings = dv.getDataValiditySettings();
        for (DataValiditySettingsRecord dataValiditySettingsRecord : settings) {
            this.validitySettings.add(new DataValiditySettingsRecord(dataValiditySettingsRecord, this.externalSheet, this.workbook, this.workbookSettings));
        }
    }

    public void add(DataValiditySettingsRecord dvsr) {
        this.validitySettings.add(dvsr);
        dvsr.setDataValidation(this);
        if (this.copied) {
            Assert.verify(this.validityList != null);
            this.validityList.dvAdded();
        }
    }

    public DataValidityListRecord getDataValidityList() {
        return this.validityList;
    }

    public DataValiditySettingsRecord[] getDataValiditySettings() {
        DataValiditySettingsRecord[] dvlr = new DataValiditySettingsRecord[0];
        return (DataValiditySettingsRecord[]) this.validitySettings.toArray(dvlr);
    }

    public void write(File outputFile) throws IOException {
        if (this.validitySettings.size() > MAX_NO_OF_VALIDITY_SETTINGS) {
            logger.warn("Maximum number of data validations exceeded - truncating...");
            ArrayList arrayList = new ArrayList(this.validitySettings.subList(0, 65532));
            this.validitySettings = arrayList;
            Assert.verify(arrayList.size() <= MAX_NO_OF_VALIDITY_SETTINGS);
        }
        if (this.validityList == null) {
            DValParser dvp = new DValParser(this.comboBoxObjectId, this.validitySettings.size());
            this.validityList = new DataValidityListRecord(dvp);
        }
        if (!this.validityList.hasDVRecords()) {
            return;
        }
        outputFile.write(this.validityList);
        Iterator i = this.validitySettings.iterator();
        while (i.hasNext()) {
            DataValiditySettingsRecord dvsr = (DataValiditySettingsRecord) i.next();
            outputFile.write(dvsr);
        }
    }

    public void insertRow(int row) {
        Iterator i = this.validitySettings.iterator();
        while (i.hasNext()) {
            DataValiditySettingsRecord dv = (DataValiditySettingsRecord) i.next();
            dv.insertRow(row);
        }
    }

    public void removeRow(int row) {
        Iterator i = this.validitySettings.iterator();
        while (i.hasNext()) {
            DataValiditySettingsRecord dv = (DataValiditySettingsRecord) i.next();
            if (dv.getFirstRow() == row && dv.getLastRow() == row) {
                i.remove();
                this.validityList.dvRemoved();
            } else {
                dv.removeRow(row);
            }
        }
    }

    public void insertColumn(int col) {
        Iterator i = this.validitySettings.iterator();
        while (i.hasNext()) {
            DataValiditySettingsRecord dv = (DataValiditySettingsRecord) i.next();
            dv.insertColumn(col);
        }
    }

    public void removeColumn(int col) {
        Iterator i = this.validitySettings.iterator();
        while (i.hasNext()) {
            DataValiditySettingsRecord dv = (DataValiditySettingsRecord) i.next();
            if (dv.getFirstColumn() == col && dv.getLastColumn() == col) {
                i.remove();
                this.validityList.dvRemoved();
            } else {
                dv.removeColumn(col);
            }
        }
    }

    public void removeDataValidation(int col, int row) {
        Iterator i = this.validitySettings.iterator();
        while (i.hasNext()) {
            DataValiditySettingsRecord dv = (DataValiditySettingsRecord) i.next();
            if (dv.getFirstColumn() == col && dv.getLastColumn() == col && dv.getFirstRow() == row && dv.getLastRow() == row) {
                i.remove();
                this.validityList.dvRemoved();
                return;
            }
        }
    }

    public void removeSharedDataValidation(int col1, int row1, int col2, int row2) {
        Iterator i = this.validitySettings.iterator();
        while (i.hasNext()) {
            DataValiditySettingsRecord dv = (DataValiditySettingsRecord) i.next();
            if (dv.getFirstColumn() == col1 && dv.getLastColumn() == col2 && dv.getFirstRow() == row1 && dv.getLastRow() == row2) {
                i.remove();
                this.validityList.dvRemoved();
                return;
            }
        }
    }

    public DataValiditySettingsRecord getDataValiditySettings(int col, int row) {
        boolean found = false;
        DataValiditySettingsRecord foundRecord = null;
        Iterator i = this.validitySettings.iterator();
        while (i.hasNext() && !found) {
            DataValiditySettingsRecord dvsr = (DataValiditySettingsRecord) i.next();
            if (dvsr.getFirstColumn() == col && dvsr.getFirstRow() == row) {
                found = true;
                foundRecord = dvsr;
            }
        }
        return foundRecord;
    }

    public int getComboBoxObjectId() {
        return this.comboBoxObjectId;
    }
}
