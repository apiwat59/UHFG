package org.apache.log4j.lf5.viewer;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import org.apache.log4j.lf5.LogLevel;
import org.apache.log4j.lf5.LogRecord;

/* loaded from: classes.dex */
public class LogTableRowRenderer extends DefaultTableCellRenderer {
    protected boolean _highlightFatal = true;
    protected Color _color = new Color(230, 230, 230);

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
        if (row % 2 == 0) {
            setBackground(this._color);
        } else {
            setBackground(Color.white);
        }
        FilteredLogTableModel model = table.getModel();
        LogRecord record = model.getFilteredRecord(row);
        setForeground(getLogLevelColor(record.getLevel()));
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
    }

    protected Color getLogLevelColor(LogLevel level) {
        return (Color) LogLevel.getLogLevelColorMap().get(level);
    }
}
