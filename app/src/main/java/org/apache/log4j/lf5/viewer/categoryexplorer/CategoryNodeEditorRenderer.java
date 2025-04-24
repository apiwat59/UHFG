package org.apache.log4j.lf5.viewer.categoryexplorer;

import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JTree;

/* loaded from: classes.dex */
public class CategoryNodeEditorRenderer extends CategoryNodeRenderer {
    @Override // org.apache.log4j.lf5.viewer.categoryexplorer.CategoryNodeRenderer
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        return c;
    }

    public JCheckBox getCheckBox() {
        return this._checkBox;
    }
}
