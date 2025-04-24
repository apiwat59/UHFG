package com.pda.uhf_g.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.pda.uhf_g.R;

/* loaded from: classes.dex */
public class TemperatureTagFragment_ViewBinding implements Unbinder {
    private TemperatureTagFragment target;
    private View view7f090065;
    private View view7f090072;

    public TemperatureTagFragment_ViewBinding(final TemperatureTagFragment target, View source) {
        this.target = target;
        target.spinnerTagManufactorer = (Spinner) Utils.findRequiredViewAsType(source, R.id.spinner_manufactorer, "field 'spinnerTagManufactorer'", Spinner.class);
        target.listView = (ListView) Utils.findRequiredViewAsType(source, R.id.listVew_epc, "field 'listView'", ListView.class);
        View view = Utils.findRequiredView(source, R.id.button_read, "field 'btnRead' and method 'onReadTag'");
        target.btnRead = (Button) Utils.castView(view, R.id.button_read, "field 'btnRead'", Button.class);
        this.view7f090072 = view;
        view.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.TemperatureTagFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onReadTag();
            }
        });
        View view2 = Utils.findRequiredView(source, R.id.button_clean, "field 'btnClean' and method 'onClean'");
        target.btnClean = (Button) Utils.castView(view2, R.id.button_clean, "field 'btnClean'", Button.class);
        this.view7f090065 = view2;
        view2.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.TemperatureTagFragment_ViewBinding.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.onClean();
            }
        });
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        TemperatureTagFragment target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.spinnerTagManufactorer = null;
        target.listView = null;
        target.btnRead = null;
        target.btnClean = null;
        this.view7f090072.setOnClickListener(null);
        this.view7f090072 = null;
        this.view7f090065.setOnClickListener(null);
        this.view7f090065 = null;
    }
}
