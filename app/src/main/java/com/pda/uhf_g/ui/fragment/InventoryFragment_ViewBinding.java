package com.pda.uhf_g.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.pda.uhf_g.R;

/* loaded from: classes.dex */
public class InventoryFragment_ViewBinding implements Unbinder {
    private InventoryFragment target;
    private View view7f090065;
    private View view7f090067;
    private View view7f090068;

    public InventoryFragment_ViewBinding(final InventoryFragment target, View source) {
        this.target = target;
        target.tvAllTag = (TextView) Utils.findRequiredViewAsType(source, R.id.textView_all_tags, "field 'tvAllTag'", TextView.class);
        target.tvSpeed = (TextView) Utils.findRequiredViewAsType(source, R.id.textView_speed, "field 'tvSpeed'", TextView.class);
        target.tvReadCount = (TextView) Utils.findRequiredViewAsType(source, R.id.textView_readCount, "field 'tvReadCount'", TextView.class);
        target.tvTime = (TextView) Utils.findRequiredViewAsType(source, R.id.textView_timeCount, "field 'tvTime'", TextView.class);
        View view = Utils.findRequiredView(source, R.id.button_inventory, "field 'btnInventory' and method 'invenroty'");
        target.btnInventory = (Button) Utils.castView(view, R.id.button_inventory, "field 'btnInventory'", Button.class);
        this.view7f090068 = view;
        view.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.InventoryFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.invenroty();
            }
        });
        target.btnCusRead = (Button) Utils.findRequiredViewAsType(source, R.id.button_cus_read, "field 'btnCusRead'", Button.class);
        View view2 = Utils.findRequiredView(source, R.id.button_excel, "field 'btnExcel' and method 'fab_excel'");
        target.btnExcel = (Button) Utils.castView(view2, R.id.button_excel, "field 'btnExcel'", Button.class);
        this.view7f090067 = view2;
        view2.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.InventoryFragment_ViewBinding.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.fab_excel();
            }
        });
        View view3 = Utils.findRequiredView(source, R.id.button_clean, "field 'btnClean' and method 'clear'");
        target.btnClean = (Button) Utils.castView(view3, R.id.button_clean, "field 'btnClean'", Button.class);
        this.view7f090065 = view3;
        view3.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.InventoryFragment_ViewBinding.3
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.clear();
            }
        });
        target.checkBoxMultiTag = (CheckBox) Utils.findRequiredViewAsType(source, R.id.checkbox_multi_tag, "field 'checkBoxMultiTag'", CheckBox.class);
        target.checkBoxTid = (CheckBox) Utils.findRequiredViewAsType(source, R.id.checkbox_tid, "field 'checkBoxTid'", CheckBox.class);
        target.checkBoxLoop = (CheckBox) Utils.findRequiredViewAsType(source, R.id.checkbox_loop, "field 'checkBoxLoop'", CheckBox.class);
        target.listViewEPC = (ListView) Utils.findRequiredViewAsType(source, R.id.listview_epc, "field 'listViewEPC'", ListView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        InventoryFragment target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.tvAllTag = null;
        target.tvSpeed = null;
        target.tvReadCount = null;
        target.tvTime = null;
        target.btnInventory = null;
        target.btnCusRead = null;
        target.btnExcel = null;
        target.btnClean = null;
        target.checkBoxMultiTag = null;
        target.checkBoxTid = null;
        target.checkBoxLoop = null;
        target.listViewEPC = null;
        this.view7f090068.setOnClickListener(null);
        this.view7f090068 = null;
        this.view7f090067.setOnClickListener(null);
        this.view7f090067 = null;
        this.view7f090065.setOnClickListener(null);
        this.view7f090065 = null;
    }
}
