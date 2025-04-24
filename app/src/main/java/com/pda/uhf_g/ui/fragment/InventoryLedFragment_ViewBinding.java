package com.pda.uhf_g.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.pda.uhf_g.R;

/* loaded from: classes.dex */
public class InventoryLedFragment_ViewBinding implements Unbinder {
    private InventoryLedFragment target;
    private View view7f090065;
    private View view7f090068;

    public InventoryLedFragment_ViewBinding(final InventoryLedFragment target, View source) {
        this.target = target;
        target.tvLedString = (TextView) Utils.findRequiredViewAsType(source, R.id.textView_led, "field 'tvLedString'", TextView.class);
        View view = Utils.findRequiredView(source, R.id.button_inventory, "field 'btnInventory' and method 'invenroty'");
        target.btnInventory = (Button) Utils.castView(view, R.id.button_inventory, "field 'btnInventory'", Button.class);
        this.view7f090068 = view;
        view.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.InventoryLedFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.invenroty();
            }
        });
        target.btnCusRead = (Button) Utils.findRequiredViewAsType(source, R.id.button_cus_read, "field 'btnCusRead'", Button.class);
        View view2 = Utils.findRequiredView(source, R.id.button_clean, "field 'btnClean' and method 'clear'");
        target.btnClean = (Button) Utils.castView(view2, R.id.button_clean, "field 'btnClean'", Button.class);
        this.view7f090065 = view2;
        view2.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.InventoryLedFragment_ViewBinding.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.clear();
            }
        });
        target.recyclerView = (RecyclerView) Utils.findRequiredViewAsType(source, R.id.recycle, "field 'recyclerView'", RecyclerView.class);
        target.listViewEPC = (ListView) Utils.findRequiredViewAsType(source, R.id.listview_epc, "field 'listViewEPC'", ListView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        InventoryLedFragment target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.tvLedString = null;
        target.btnInventory = null;
        target.btnCusRead = null;
        target.btnClean = null;
        target.recyclerView = null;
        target.listViewEPC = null;
        this.view7f090068.setOnClickListener(null);
        this.view7f090068 = null;
        this.view7f090065.setOnClickListener(null);
        this.view7f090065 = null;
    }
}
