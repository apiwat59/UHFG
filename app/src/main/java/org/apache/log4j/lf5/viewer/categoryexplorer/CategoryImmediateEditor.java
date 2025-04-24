package org.apache.log4j.lf5.viewer.categoryexplorer;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.TreePath;

/* loaded from: classes.dex */
public class CategoryImmediateEditor extends DefaultTreeCellEditor {
    protected Icon editingIcon;
    private CategoryNodeRenderer renderer;

    public CategoryImmediateEditor(JTree tree, CategoryNodeRenderer renderer, CategoryNodeEditor editor) {
        super(tree, renderer, editor);
        this.editingIcon = null;
        this.renderer = renderer;
        renderer.setIcon((Icon) null);
        renderer.setLeafIcon((Icon) null);
        renderer.setOpenIcon((Icon) null);
        renderer.setClosedIcon((Icon) null);
        ((DefaultTreeCellEditor) this).editingIcon = null;
    }

    public boolean shouldSelectCell(EventObject e) {
        if (!(e instanceof MouseEvent)) {
            return false;
        }
        MouseEvent me2 = (MouseEvent) e;
        TreePath path = ((DefaultTreeCellEditor) this).tree.getPathForLocation(me2.getX(), me2.getY());
        CategoryNode node = (CategoryNode) path.getLastPathComponent();
        boolean rv = node.isLeaf();
        return rv;
    }

    public boolean inCheckBoxHitRegion(MouseEvent e) {
        TreePath path = ((DefaultTreeCellEditor) this).tree.getPathForLocation(e.getX(), e.getY());
        if (path == null) {
            return false;
        }
        Rectangle bounds = ((DefaultTreeCellEditor) this).tree.getRowBounds(((DefaultTreeCellEditor) this).lastRow);
        Dimension checkBoxOffset = this.renderer.getCheckBoxOffset();
        bounds.translate(((DefaultTreeCellEditor) this).offset + checkBoxOffset.width, checkBoxOffset.height);
        bounds.contains(e.getPoint());
        return true;
    }

    protected boolean canEditImmediately(EventObject e) {
        if (!(e instanceof MouseEvent)) {
            return false;
        }
        MouseEvent me2 = (MouseEvent) e;
        boolean rv = inCheckBoxHitRegion(me2);
        return rv;
    }

    protected void determineOffset(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
        ((DefaultTreeCellEditor) this).offset = 0;
    }
}
