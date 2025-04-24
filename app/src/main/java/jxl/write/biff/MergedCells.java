package jxl.write.biff;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import jxl.Cell;
import jxl.CellType;
import jxl.Range;
import jxl.WorkbookSettings;
import jxl.biff.SheetRangeImpl;
import jxl.common.Assert;
import jxl.common.Logger;
import jxl.write.Blank;
import jxl.write.WritableSheet;
import jxl.write.WriteException;

/* loaded from: classes.dex */
class MergedCells {
    private static Logger logger = Logger.getLogger(MergedCells.class);
    private static final int maxRangesPerSheet = 1020;
    private ArrayList ranges = new ArrayList();
    private WritableSheet sheet;

    public MergedCells(WritableSheet ws) {
        this.sheet = ws;
    }

    void add(Range r) {
        this.ranges.add(r);
    }

    void insertRow(int row) {
        Iterator i = this.ranges.iterator();
        while (i.hasNext()) {
            SheetRangeImpl sr = (SheetRangeImpl) i.next();
            sr.insertRow(row);
        }
    }

    void insertColumn(int col) {
        Iterator i = this.ranges.iterator();
        while (i.hasNext()) {
            SheetRangeImpl sr = (SheetRangeImpl) i.next();
            sr.insertColumn(col);
        }
    }

    void removeColumn(int col) {
        Iterator i = this.ranges.iterator();
        while (i.hasNext()) {
            SheetRangeImpl sr = (SheetRangeImpl) i.next();
            if (sr.getTopLeft().getColumn() == col && sr.getBottomRight().getColumn() == col) {
                i.remove();
            } else {
                sr.removeColumn(col);
            }
        }
    }

    void removeRow(int row) {
        Iterator i = this.ranges.iterator();
        while (i.hasNext()) {
            SheetRangeImpl sr = (SheetRangeImpl) i.next();
            if (sr.getTopLeft().getRow() == row && sr.getBottomRight().getRow() == row) {
                i.remove();
            } else {
                sr.removeRow(row);
            }
        }
    }

    Range[] getMergedCells() {
        Range[] cells = new Range[this.ranges.size()];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = (Range) this.ranges.get(i);
        }
        return cells;
    }

    void unmergeCells(Range r) {
        int index = this.ranges.indexOf(r);
        if (index != -1) {
            this.ranges.remove(index);
        }
    }

    private void checkIntersections() {
        ArrayList newcells = new ArrayList(this.ranges.size());
        Iterator mci = this.ranges.iterator();
        while (mci.hasNext()) {
            SheetRangeImpl r = (SheetRangeImpl) mci.next();
            Iterator i = newcells.iterator();
            boolean intersects = false;
            while (i.hasNext() && !intersects) {
                SheetRangeImpl range = (SheetRangeImpl) i.next();
                if (range.intersects(r)) {
                    logger.warn("Could not merge cells " + r + " as they clash with an existing set of merged cells.");
                    intersects = true;
                }
            }
            if (!intersects) {
                newcells.add(r);
            }
        }
        this.ranges = newcells;
    }

    private void checkRanges() {
        for (int i = 0; i < this.ranges.size(); i++) {
            try {
                SheetRangeImpl range = (SheetRangeImpl) this.ranges.get(i);
                Cell tl = range.getTopLeft();
                Cell br = range.getBottomRight();
                boolean found = false;
                for (int c = tl.getColumn(); c <= br.getColumn(); c++) {
                    for (int r = tl.getRow(); r <= br.getRow(); r++) {
                        Cell cell = this.sheet.getCell(c, r);
                        if (cell.getType() != CellType.EMPTY) {
                            if (!found) {
                                found = true;
                            } else {
                                logger.warn("Range " + range + " contains more than one data cell.  Setting the other cells to blank.");
                                Blank b = new Blank(c, r);
                                this.sheet.addCell(b);
                            }
                        }
                    }
                }
            } catch (WriteException e) {
                Assert.verify(false);
                return;
            }
        }
    }

    void write(File outputFile) throws IOException {
        if (this.ranges.size() == 0) {
            return;
        }
        WorkbookSettings ws = ((WritableSheetImpl) this.sheet).getWorkbookSettings();
        if (!ws.getMergedCellCheckingDisabled()) {
            checkIntersections();
            checkRanges();
        }
        if (this.ranges.size() < 1020) {
            MergedCellsRecord mcr = new MergedCellsRecord(this.ranges);
            outputFile.write(mcr);
            return;
        }
        int numRecordsRequired = (this.ranges.size() / 1020) + 1;
        int pos = 0;
        for (int i = 0; i < numRecordsRequired; i++) {
            int numranges = Math.min(1020, this.ranges.size() - pos);
            ArrayList cells = new ArrayList(numranges);
            for (int j = 0; j < numranges; j++) {
                cells.add(this.ranges.get(pos + j));
            }
            MergedCellsRecord mcr2 = new MergedCellsRecord(cells);
            outputFile.write(mcr2);
            pos += numranges;
        }
    }
}
