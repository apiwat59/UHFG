package org.apache.log4j.lf5.viewer;

import androidx.recyclerview.widget.ItemTouchHelper;
import com.apkfuns.logutils.Constant;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.apache.log4j.lf5.util.DateFormatManager;

/* loaded from: classes.dex */
public class LogTable extends JTable {
    protected JTextArea _detailTextArea;
    protected int _rowHeight = 30;
    protected int _numCols = 9;
    protected TableColumn[] _tableColumns = new TableColumn[9];
    protected int[] _colWidths = {40, 40, 40, 70, 70, 360, 440, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION, 60};
    protected LogTableColumn[] _colNames = LogTableColumn.getLogTableColumnArray();
    protected int _colDate = 0;
    protected int _colThread = 1;
    protected int _colMessageNum = 2;
    protected int _colLevel = 3;
    protected int _colNDC = 4;
    protected int _colCategory = 5;
    protected int _colMessage = 6;
    protected int _colLocation = 7;
    protected int _colThrown = 8;
    protected DateFormatManager _dateFormatManager = null;

    public LogTable(JTextArea detailTextArea) {
        init();
        this._detailTextArea = detailTextArea;
        setModel(new FilteredLogTableModel());
        Enumeration columns = getColumnModel().getColumns();
        int i = 0;
        while (columns.hasMoreElements()) {
            TableColumn col = (TableColumn) columns.nextElement();
            col.setCellRenderer(new LogTableRowRenderer());
            col.setPreferredWidth(this._colWidths[i]);
            this._tableColumns[i] = col;
            i++;
        }
        ListSelectionModel rowSM = getSelectionModel();
        rowSM.addListSelectionListener(new LogTableListSelectionListener(this));
    }

    public DateFormatManager getDateFormatManager() {
        return this._dateFormatManager;
    }

    public void setDateFormatManager(DateFormatManager dfm) {
        this._dateFormatManager = dfm;
    }

    public synchronized void clearLogRecords() {
        getFilteredLogTableModel().clear();
    }

    public FilteredLogTableModel getFilteredLogTableModel() {
        return getModel();
    }

    public void setDetailedView() {
        TableColumnModel model = getColumnModel();
        for (int f = 0; f < this._numCols; f++) {
            model.removeColumn(this._tableColumns[f]);
        }
        for (int i = 0; i < this._numCols; i++) {
            model.addColumn(this._tableColumns[i]);
        }
        sizeColumnsToFit(-1);
    }

    public void setView(List columns) {
        TableColumnModel model = getColumnModel();
        for (int f = 0; f < this._numCols; f++) {
            model.removeColumn(this._tableColumns[f]);
        }
        Iterator selectedColumns = columns.iterator();
        Vector columnNameAndNumber = getColumnNameAndNumber();
        while (selectedColumns.hasNext()) {
            model.addColumn(this._tableColumns[columnNameAndNumber.indexOf(selectedColumns.next())]);
        }
        sizeColumnsToFit(-1);
    }

    public void setFont(Font font) {
        super/*javax.swing.JComponent*/.setFont(font);
        Graphics g = getGraphics();
        if (g != null) {
            FontMetrics fm = g.getFontMetrics(font);
            int height = fm.getHeight();
            int i = (height / 3) + height;
            this._rowHeight = i;
            setRowHeight(i);
        }
    }

    protected void init() {
        setRowHeight(this._rowHeight);
        setSelectionMode(0);
    }

    protected Vector getColumnNameAndNumber() {
        Vector columnNameAndNumber = new Vector();
        int i = 0;
        while (true) {
            LogTableColumn[] logTableColumnArr = this._colNames;
            if (i < logTableColumnArr.length) {
                columnNameAndNumber.add(i, logTableColumnArr[i]);
                i++;
            } else {
                return columnNameAndNumber;
            }
        }
    }

    class LogTableListSelectionListener implements ListSelectionListener {
        protected JTable _table;

        public LogTableListSelectionListener(JTable table) {
            this._table = table;
        }

        public void valueChanged(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) {
                return;
            }
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();
            if (!lsm.isSelectionEmpty()) {
                StringBuffer buf = new StringBuffer();
                int selectedRow = lsm.getMinSelectionIndex();
                for (int i = 0; i < LogTable.this._numCols - 1; i++) {
                    String value = "";
                    Object obj = this._table.getModel().getValueAt(selectedRow, i);
                    if (obj != null) {
                        value = obj.toString();
                    }
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append(LogTable.this._colNames[i]);
                    stringBuffer.append(":");
                    buf.append(stringBuffer.toString());
                    buf.append(Constant.SPACE);
                    if (i == LogTable.this._colThread || i == LogTable.this._colMessage || i == LogTable.this._colLevel) {
                        buf.append(Constant.SPACE);
                    }
                    if (i == LogTable.this._colDate || i == LogTable.this._colNDC) {
                        buf.append("\t\t");
                    }
                    buf.append(value);
                    buf.append("\n");
                }
                StringBuffer stringBuffer2 = new StringBuffer();
                stringBuffer2.append(LogTable.this._colNames[LogTable.this._numCols - 1]);
                stringBuffer2.append(":\n");
                buf.append(stringBuffer2.toString());
                Object obj2 = this._table.getModel().getValueAt(selectedRow, LogTable.this._numCols - 1);
                if (obj2 != null) {
                    buf.append(obj2.toString());
                }
                LogTable.this._detailTextArea.setText(buf.toString());
            }
        }
    }
}
