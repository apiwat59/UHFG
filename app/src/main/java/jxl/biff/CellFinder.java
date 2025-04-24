package jxl.biff;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jxl.Cell;
import jxl.CellType;
import jxl.LabelCell;
import jxl.Sheet;

/* loaded from: classes.dex */
public class CellFinder {
    private Sheet sheet;

    public CellFinder(Sheet s) {
        this.sheet = s;
    }

    public Cell findCell(String contents, int firstCol, int firstRow, int lastCol, int lastRow, boolean reverse) {
        CellFinder cellFinder = this;
        Cell cell = null;
        boolean found = false;
        int numCols = lastCol - firstCol;
        int numRows = lastRow - firstRow;
        int row1 = reverse ? lastRow : firstRow;
        if (reverse) {
        }
        int col1 = reverse ? lastCol : firstCol;
        if (reverse) {
        }
        int inc = reverse ? -1 : 1;
        int i = 0;
        while (i <= numCols && !found) {
            int j = 0;
            while (j <= numRows && !found) {
                int curCol = (i * inc) + col1;
                int curRow = (j * inc) + row1;
                if (curCol < cellFinder.sheet.getColumns() && curRow < cellFinder.sheet.getRows()) {
                    Cell c = cellFinder.sheet.getCell(curCol, curRow);
                    if (c.getType() != CellType.EMPTY && c.getContents().equals(contents)) {
                        cell = c;
                        found = true;
                    }
                }
                j++;
                cellFinder = this;
            }
            i++;
            cellFinder = this;
        }
        return cell;
    }

    public Cell findCell(String contents) {
        Cell cell = null;
        boolean found = false;
        for (int i = 0; i < this.sheet.getRows() && !found; i++) {
            Cell[] row = this.sheet.getRow(i);
            for (int j = 0; j < row.length && !found; j++) {
                if (row[j].getContents().equals(contents)) {
                    cell = row[j];
                    found = true;
                }
            }
        }
        return cell;
    }

    public Cell findCell(Pattern pattern, int firstCol, int firstRow, int lastCol, int lastRow, boolean reverse) {
        CellFinder cellFinder = this;
        Cell cell = null;
        boolean found = false;
        int numCols = lastCol - firstCol;
        int numRows = lastRow - firstRow;
        int row1 = reverse ? lastRow : firstRow;
        if (reverse) {
        }
        int col1 = reverse ? lastCol : firstCol;
        if (reverse) {
        }
        int inc = reverse ? -1 : 1;
        int i = 0;
        while (i <= numCols && !found) {
            int j = 0;
            while (j <= numRows && !found) {
                int curCol = (i * inc) + col1;
                int curRow = (j * inc) + row1;
                if (curCol < cellFinder.sheet.getColumns() && curRow < cellFinder.sheet.getRows()) {
                    Cell c = cellFinder.sheet.getCell(curCol, curRow);
                    if (c.getType() != CellType.EMPTY) {
                        Matcher m = pattern.matcher(c.getContents());
                        if (m.matches()) {
                            cell = c;
                            found = true;
                        }
                    }
                }
                j++;
                cellFinder = this;
            }
            i++;
            cellFinder = this;
        }
        return cell;
    }

    public LabelCell findLabelCell(String contents) {
        LabelCell cell = null;
        boolean found = false;
        for (int i = 0; i < this.sheet.getRows() && !found; i++) {
            Cell[] row = this.sheet.getRow(i);
            for (int j = 0; j < row.length && !found; j++) {
                if ((row[j].getType() == CellType.LABEL || row[j].getType() == CellType.STRING_FORMULA) && row[j].getContents().equals(contents)) {
                    cell = (LabelCell) row[j];
                    found = true;
                }
            }
        }
        return cell;
    }
}
