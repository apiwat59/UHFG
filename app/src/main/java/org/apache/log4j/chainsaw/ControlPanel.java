package org.apache.log4j.chainsaw;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

/* loaded from: classes.dex */
class ControlPanel extends JPanel {
    private static final Logger LOG;
    static /* synthetic */ Class class$org$apache$log4j$chainsaw$ControlPanel;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        } catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        Class cls = class$org$apache$log4j$chainsaw$ControlPanel;
        if (cls == null) {
            cls = class$("org.apache.log4j.chainsaw.ControlPanel");
            class$org$apache$log4j$chainsaw$ControlPanel = cls;
        }
        LOG = Logger.getLogger(cls);
    }

    ControlPanel(final MyTableModel aModel) {
        setBorder(BorderFactory.createTitledBorder("Controls: "));
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gridbag);
        c.ipadx = 5;
        c.ipady = 5;
        c.gridx = 0;
        c.anchor = 13;
        c.gridy = 0;
        JLabel label = new JLabel("Filter Level:");
        gridbag.setConstraints(label, c);
        add(label);
        c.gridy++;
        JLabel label2 = new JLabel("Filter Thread:");
        gridbag.setConstraints(label2, c);
        add(label2);
        c.gridy++;
        JLabel label3 = new JLabel("Filter Logger:");
        gridbag.setConstraints(label3, c);
        add(label3);
        c.gridy++;
        JLabel label4 = new JLabel("Filter NDC:");
        gridbag.setConstraints(label4, c);
        add(label4);
        c.gridy++;
        JLabel label5 = new JLabel("Filter Message:");
        gridbag.setConstraints(label5, c);
        add(label5);
        c.weightx = 1.0d;
        c.gridx = 1;
        c.anchor = 17;
        c.gridy = 0;
        Level[] allPriorities = {Level.FATAL, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG, Level.TRACE};
        final JComboBox priorities = new JComboBox(allPriorities);
        Level lowest = allPriorities[allPriorities.length - 1];
        priorities.setSelectedItem(lowest);
        aModel.setPriorityFilter(lowest);
        gridbag.setConstraints(priorities, c);
        add(priorities);
        priorities.setEditable(false);
        priorities.addActionListener(new ActionListener() { // from class: org.apache.log4j.chainsaw.ControlPanel.1
            public void actionPerformed(ActionEvent aEvent) {
                aModel.setPriorityFilter((Priority) priorities.getSelectedItem());
            }
        });
        c.fill = 2;
        c.gridy++;
        final JTextField threadField = new JTextField("");
        threadField.getDocument().addDocumentListener(new DocumentListener() { // from class: org.apache.log4j.chainsaw.ControlPanel.2
            public void insertUpdate(DocumentEvent aEvent) {
                aModel.setThreadFilter(threadField.getText());
            }

            public void removeUpdate(DocumentEvent aEvente) {
                aModel.setThreadFilter(threadField.getText());
            }

            public void changedUpdate(DocumentEvent aEvent) {
                aModel.setThreadFilter(threadField.getText());
            }
        });
        gridbag.setConstraints(threadField, c);
        add(threadField);
        c.gridy++;
        final JTextField catField = new JTextField("");
        catField.getDocument().addDocumentListener(new DocumentListener() { // from class: org.apache.log4j.chainsaw.ControlPanel.3
            public void insertUpdate(DocumentEvent aEvent) {
                aModel.setCategoryFilter(catField.getText());
            }

            public void removeUpdate(DocumentEvent aEvent) {
                aModel.setCategoryFilter(catField.getText());
            }

            public void changedUpdate(DocumentEvent aEvent) {
                aModel.setCategoryFilter(catField.getText());
            }
        });
        gridbag.setConstraints(catField, c);
        add(catField);
        c.gridy++;
        final JTextField ndcField = new JTextField("");
        ndcField.getDocument().addDocumentListener(new DocumentListener() { // from class: org.apache.log4j.chainsaw.ControlPanel.4
            public void insertUpdate(DocumentEvent aEvent) {
                aModel.setNDCFilter(ndcField.getText());
            }

            public void removeUpdate(DocumentEvent aEvent) {
                aModel.setNDCFilter(ndcField.getText());
            }

            public void changedUpdate(DocumentEvent aEvent) {
                aModel.setNDCFilter(ndcField.getText());
            }
        });
        gridbag.setConstraints(ndcField, c);
        add(ndcField);
        c.gridy++;
        final JTextField msgField = new JTextField("");
        msgField.getDocument().addDocumentListener(new DocumentListener() { // from class: org.apache.log4j.chainsaw.ControlPanel.5
            public void insertUpdate(DocumentEvent aEvent) {
                aModel.setMessageFilter(msgField.getText());
            }

            public void removeUpdate(DocumentEvent aEvent) {
                aModel.setMessageFilter(msgField.getText());
            }

            public void changedUpdate(DocumentEvent aEvent) {
                aModel.setMessageFilter(msgField.getText());
            }
        });
        gridbag.setConstraints(msgField, c);
        add(msgField);
        c.weightx = 0.0d;
        c.fill = 2;
        c.anchor = 13;
        c.gridx = 2;
        c.gridy = 0;
        JButton exitButton = new JButton("Exit");
        exitButton.setMnemonic('x');
        exitButton.addActionListener(ExitAction.INSTANCE);
        gridbag.setConstraints(exitButton, c);
        add(exitButton);
        c.gridy++;
        JButton clearButton = new JButton("Clear");
        clearButton.setMnemonic('c');
        clearButton.addActionListener(new ActionListener() { // from class: org.apache.log4j.chainsaw.ControlPanel.6
            public void actionPerformed(ActionEvent aEvent) {
                aModel.clear();
            }
        });
        gridbag.setConstraints(clearButton, c);
        add(clearButton);
        c.gridy++;
        final JButton toggleButton = new JButton("Pause");
        toggleButton.setMnemonic('p');
        toggleButton.addActionListener(new ActionListener() { // from class: org.apache.log4j.chainsaw.ControlPanel.7
            public void actionPerformed(ActionEvent aEvent) {
                aModel.toggle();
                toggleButton.setText(aModel.isPaused() ? "Resume" : "Pause");
            }
        });
        gridbag.setConstraints(toggleButton, c);
        add(toggleButton);
    }
}
