package org.apache.log4j.chainsaw;

import java.awt.BorderLayout;
import java.text.MessageFormat;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.apache.log4j.Logger;

/* loaded from: classes.dex */
class DetailPanel extends JPanel implements ListSelectionListener {
    private static final MessageFormat FORMATTER;
    private static final Logger LOG;
    static /* synthetic */ Class class$org$apache$log4j$chainsaw$DetailPanel;
    private final JEditorPane mDetails;
    private final MyTableModel mModel;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Class cls = class$org$apache$log4j$chainsaw$DetailPanel;
        if (cls == null) {
            cls = class$("org.apache.log4j.chainsaw.DetailPanel");
            class$org$apache$log4j$chainsaw$DetailPanel = cls;
        }
        LOG = Logger.getLogger(cls);
        FORMATTER = new MessageFormat("<b>Time:</b> <code>{0,time,medium}</code>&nbsp;&nbsp;<b>Priority:</b> <code>{1}</code>&nbsp;&nbsp;<b>Thread:</b> <code>{2}</code>&nbsp;&nbsp;<b>NDC:</b> <code>{3}</code><br><b>Logger:</b> <code>{4}</code><br><b>Location:</b> <code>{5}</code><br><b>Message:</b><pre>{6}</pre><b>Throwable:</b><pre>{7}</pre>");
    }

    DetailPanel(JTable aTable, MyTableModel aModel) {
        this.mModel = aModel;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Details: "));
        JEditorPane jEditorPane = new JEditorPane();
        this.mDetails = jEditorPane;
        jEditorPane.setEditable(false);
        jEditorPane.setContentType("text/html");
        add(new JScrollPane(jEditorPane), "Center");
        ListSelectionModel rowSM = aTable.getSelectionModel();
        rowSM.addListSelectionListener(this);
    }

    public void valueChanged(ListSelectionEvent aEvent) {
        if (aEvent.getValueIsAdjusting()) {
            return;
        }
        ListSelectionModel lsm = (ListSelectionModel) aEvent.getSource();
        if (lsm.isSelectionEmpty()) {
            this.mDetails.setText("Nothing selected");
            return;
        }
        int selectedRow = lsm.getMinSelectionIndex();
        EventDetails e = this.mModel.getEventDetails(selectedRow);
        Object[] args = {new Date(e.getTimeStamp()), e.getPriority(), escape(e.getThreadName()), escape(e.getNDC()), escape(e.getCategoryName()), escape(e.getLocationDetails()), escape(e.getMessage()), escape(getThrowableStrRep(e))};
        this.mDetails.setText(FORMATTER.format(args));
        this.mDetails.setCaretPosition(0);
    }

    private static String getThrowableStrRep(EventDetails aEvent) {
        String[] strs = aEvent.getThrowableStrRep();
        if (strs == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (String str : strs) {
            sb.append(str);
            sb.append("\n");
        }
        return sb.toString();
    }

    private String escape(String aStr) {
        if (aStr == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < aStr.length(); i++) {
            char c = aStr.charAt(i);
            if (c == '\"') {
                buf.append("&quot;");
            } else if (c == '&') {
                buf.append("&amp;");
            } else if (c == '<') {
                buf.append("&lt;");
            } else if (c == '>') {
                buf.append("&gt;");
            } else {
                buf.append(c);
            }
        }
        return buf.toString();
    }
}
