package org.apache.log4j.lf5.viewer.categoryexplorer;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JCheckBox;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/* loaded from: classes.dex */
public class CategoryNodeEditor extends CategoryAbstractCellEditor {
    protected CategoryExplorerModel _categoryModel;
    protected JCheckBox _checkBox;
    protected CategoryNode _lastEditedNode;
    protected CategoryNodeEditorRenderer _renderer;
    protected JTree _tree;

    public CategoryNodeEditor(CategoryExplorerModel model) {
        CategoryNodeEditorRenderer categoryNodeEditorRenderer = new CategoryNodeEditorRenderer();
        this._renderer = categoryNodeEditorRenderer;
        JCheckBox checkBox = categoryNodeEditorRenderer.getCheckBox();
        this._checkBox = checkBox;
        this._categoryModel = model;
        checkBox.addActionListener(new ActionListener() { // from class: org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNodeEditor.1
            public void actionPerformed(ActionEvent e) {
                CategoryNodeEditor.this._categoryModel.update(CategoryNodeEditor.this._lastEditedNode, CategoryNodeEditor.this._checkBox.isSelected());
                CategoryNodeEditor.this.stopCellEditing();
            }
        });
        this._renderer.addMouseListener(new MouseAdapter() { // from class: org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNodeEditor.2
            public void mousePressed(MouseEvent e) {
                if ((e.getModifiers() & 4) != 0) {
                    CategoryNodeEditor categoryNodeEditor = CategoryNodeEditor.this;
                    categoryNodeEditor.showPopup(categoryNodeEditor._lastEditedNode, e.getX(), e.getY());
                }
                CategoryNodeEditor.this.stopCellEditing();
            }
        });
    }

    @Override // org.apache.log4j.lf5.viewer.categoryexplorer.CategoryAbstractCellEditor
    public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row) {
        this._lastEditedNode = (CategoryNode) value;
        this._tree = tree;
        return this._renderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, true);
    }

    @Override // org.apache.log4j.lf5.viewer.categoryexplorer.CategoryAbstractCellEditor
    public Object getCellEditorValue() {
        return this._lastEditedNode.getUserObject();
    }

    protected JMenuItem createPropertiesMenuItem(final CategoryNode node) {
        JMenuItem result = new JMenuItem("Properties");
        result.addActionListener(new ActionListener() { // from class: org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNodeEditor.3
            public void actionPerformed(ActionEvent e) {
                CategoryNodeEditor.this.showPropertiesDialog(node);
            }
        });
        return result;
    }

    protected void showPropertiesDialog(CategoryNode node) {
        JTree jTree = this._tree;
        Object displayedProperties = getDisplayedProperties(node);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Category Properties: ");
        stringBuffer.append(node.getTitle());
        JOptionPane.showMessageDialog(jTree, displayedProperties, stringBuffer.toString(), -1);
    }

    protected Object getDisplayedProperties(CategoryNode node) {
        ArrayList result = new ArrayList();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("Category: ");
        stringBuffer.append(node.getTitle());
        result.add(stringBuffer.toString());
        if (node.hasFatalRecords()) {
            result.add("Contains at least one fatal LogRecord.");
        }
        if (node.hasFatalChildren()) {
            result.add("Contains descendants with a fatal LogRecord.");
        }
        StringBuffer stringBuffer2 = new StringBuffer();
        stringBuffer2.append("LogRecords in this category alone: ");
        stringBuffer2.append(node.getNumberOfContainedRecords());
        result.add(stringBuffer2.toString());
        StringBuffer stringBuffer3 = new StringBuffer();
        stringBuffer3.append("LogRecords in descendant categories: ");
        stringBuffer3.append(node.getNumberOfRecordsFromChildren());
        result.add(stringBuffer3.toString());
        StringBuffer stringBuffer4 = new StringBuffer();
        stringBuffer4.append("LogRecords in this category including descendants: ");
        stringBuffer4.append(node.getTotalNumberOfRecords());
        result.add(stringBuffer4.toString());
        return result.toArray();
    }

    protected void showPopup(CategoryNode node, int x, int y) {
        JPopupMenu popup = new JPopupMenu();
        popup.setSize(150, 400);
        if (node.getParent() == null) {
            popup.add(createRemoveMenuItem());
            popup.addSeparator();
        }
        popup.add(createSelectDescendantsMenuItem(node));
        popup.add(createUnselectDescendantsMenuItem(node));
        popup.addSeparator();
        popup.add(createExpandMenuItem(node));
        popup.add(createCollapseMenuItem(node));
        popup.addSeparator();
        popup.add(createPropertiesMenuItem(node));
        popup.show(this._renderer, x, y);
    }

    protected JMenuItem createSelectDescendantsMenuItem(final CategoryNode node) {
        JMenuItem selectDescendants = new JMenuItem("Select All Descendant Categories");
        selectDescendants.addActionListener(new ActionListener() { // from class: org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNodeEditor.4
            public void actionPerformed(ActionEvent e) {
                CategoryNodeEditor.this._categoryModel.setDescendantSelection(node, true);
            }
        });
        return selectDescendants;
    }

    protected JMenuItem createUnselectDescendantsMenuItem(final CategoryNode node) {
        JMenuItem unselectDescendants = new JMenuItem("Deselect All Descendant Categories");
        unselectDescendants.addActionListener(new ActionListener() { // from class: org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNodeEditor.5
            public void actionPerformed(ActionEvent e) {
                CategoryNodeEditor.this._categoryModel.setDescendantSelection(node, false);
            }
        });
        return unselectDescendants;
    }

    protected JMenuItem createExpandMenuItem(final CategoryNode node) {
        JMenuItem result = new JMenuItem("Expand All Descendant Categories");
        result.addActionListener(new ActionListener() { // from class: org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNodeEditor.6
            public void actionPerformed(ActionEvent e) {
                CategoryNodeEditor.this.expandDescendants(node);
            }
        });
        return result;
    }

    protected JMenuItem createCollapseMenuItem(final CategoryNode node) {
        JMenuItem result = new JMenuItem("Collapse All Descendant Categories");
        result.addActionListener(new ActionListener() { // from class: org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNodeEditor.7
            public void actionPerformed(ActionEvent e) {
                CategoryNodeEditor.this.collapseDescendants(node);
            }
        });
        return result;
    }

    protected JMenuItem createRemoveMenuItem() {
        JMenuItem result = new JMenuItem("Remove All Empty Categories");
        result.addActionListener(new ActionListener() { // from class: org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNodeEditor.8
            public void actionPerformed(ActionEvent e) {
                while (CategoryNodeEditor.this.removeUnusedNodes() > 0) {
                }
            }
        });
        return result;
    }

    protected void expandDescendants(CategoryNode node) {
        Enumeration descendants = node.depthFirstEnumeration();
        while (descendants.hasMoreElements()) {
            CategoryNode current = (CategoryNode) descendants.nextElement();
            expand(current);
        }
    }

    protected void collapseDescendants(CategoryNode node) {
        Enumeration descendants = node.depthFirstEnumeration();
        while (descendants.hasMoreElements()) {
            CategoryNode current = (CategoryNode) descendants.nextElement();
            collapse(current);
        }
    }

    protected int removeUnusedNodes() {
        int count = 0;
        CategoryNode root = this._categoryModel.getRootCategoryNode();
        Enumeration enumeration = root.depthFirstEnumeration();
        while (enumeration.hasMoreElements()) {
            CategoryNode node = (CategoryNode) enumeration.nextElement();
            if (node.isLeaf() && node.getNumberOfContainedRecords() == 0 && node.getParent() != null) {
                this._categoryModel.removeNodeFromParent(node);
                count++;
            }
        }
        return count;
    }

    protected void expand(CategoryNode node) {
        this._tree.expandPath(getTreePath(node));
    }

    protected TreePath getTreePath(CategoryNode node) {
        return new TreePath(node.getPath());
    }

    protected void collapse(CategoryNode node) {
        this._tree.collapsePath(getTreePath(node));
    }
}
