package com.pda.uhf_g.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.pda.uhf_g.R;

/* loaded from: classes.dex */
public class SettingsFragment_ViewBinding implements Unbinder {
    private SettingsFragment target;
    private View view7f09006c;
    private View view7f09006d;
    private View view7f09006e;
    private View view7f09006f;
    private View view7f090070;
    private View view7f090071;
    private View view7f090073;
    private View view7f090074;
    private View view7f090075;
    private View view7f090076;
    private View view7f090077;

    public SettingsFragment_ViewBinding(final SettingsFragment target, View source) {
        this.target = target;
        target.spinnerWorkFreq = (Spinner) Utils.findRequiredViewAsType(source, R.id.spinner_work_freq, "field 'spinnerWorkFreq'", Spinner.class);
        target.spinnerPower = (Spinner) Utils.findRequiredViewAsType(source, R.id.spinner_power, "field 'spinnerPower'", Spinner.class);
        target.spinnerSession = (Spinner) Utils.findRequiredViewAsType(source, R.id.spinner_session, "field 'spinnerSession'", Spinner.class);
        target.spinnerQvalue = (Spinner) Utils.findRequiredViewAsType(source, R.id.spinner_q_value, "field 'spinnerQvalue'", Spinner.class);
        target.spinnerInventoryType = (Spinner) Utils.findRequiredViewAsType(source, R.id.spinner_inventory_type, "field 'spinnerInventoryType'", Spinner.class);
        View view = Utils.findRequiredView(source, R.id.button_query_work_freq, "field 'buttonFreqQuery' and method 'queryFreq'");
        target.buttonFreqQuery = (Button) Utils.castView(view, R.id.button_query_work_freq, "field 'buttonFreqQuery'", Button.class);
        this.view7f090071 = view;
        view.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.queryFreq();
            }
        });
        View view2 = Utils.findRequiredView(source, R.id.button_set_work_freq, "field 'buttonFreqSet' and method 'setWorkFreq'");
        target.buttonFreqSet = (Button) Utils.castView(view2, R.id.button_set_work_freq, "field 'buttonFreqSet'", Button.class);
        this.view7f090077 = view2;
        view2.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment_ViewBinding.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.setWorkFreq();
            }
        });
        target.editTextTemp = (EditText) Utils.findRequiredViewAsType(source, R.id.editText_temp, "field 'editTextTemp'", EditText.class);
        View view3 = Utils.findRequiredView(source, R.id.button_query_power, "field 'buttonQueryPower' and method 'queryPower'");
        target.buttonQueryPower = (Button) Utils.castView(view3, R.id.button_query_power, "field 'buttonQueryPower'", Button.class);
        this.view7f09006d = view3;
        view3.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment_ViewBinding.3
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.queryPower();
            }
        });
        View view4 = Utils.findRequiredView(source, R.id.button_set_power, "field 'buttonSetPower' and method 'setPower'");
        target.buttonSetPower = (Button) Utils.castView(view4, R.id.button_set_power, "field 'buttonSetPower'", Button.class);
        this.view7f090074 = view4;
        view4.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment_ViewBinding.4
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.setPower();
            }
        });
        View view5 = Utils.findRequiredView(source, R.id.button_query_inventory_type, "field 'buttonQueryInventory' and method 'queryInventory'");
        target.buttonQueryInventory = (Button) Utils.castView(view5, R.id.button_query_inventory_type, "field 'buttonQueryInventory'", Button.class);
        this.view7f09006c = view5;
        view5.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment_ViewBinding.5
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.queryInventory();
            }
        });
        View view6 = Utils.findRequiredView(source, R.id.button_set_inventory_type, "field 'buttonSetInventory' and method 'setTarget'");
        target.buttonSetInventory = (Button) Utils.castView(view6, R.id.button_set_inventory_type, "field 'buttonSetInventory'", Button.class);
        this.view7f090073 = view6;
        view6.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment_ViewBinding.6
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.setTarget();
            }
        });
        View view7 = Utils.findRequiredView(source, R.id.button_query_session, "field 'buttonQuerySession' and method 'querySession'");
        target.buttonQuerySession = (Button) Utils.castView(view7, R.id.button_query_session, "field 'buttonQuerySession'", Button.class);
        this.view7f09006f = view7;
        view7.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment_ViewBinding.7
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.querySession();
            }
        });
        View view8 = Utils.findRequiredView(source, R.id.button_set_session, "field 'buttonSetSession' and method 'setSession'");
        target.buttonSetSession = (Button) Utils.castView(view8, R.id.button_set_session, "field 'buttonSetSession'", Button.class);
        this.view7f090076 = view8;
        view8.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment_ViewBinding.8
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.setSession();
            }
        });
        target.checkBoxFastid = (CheckBox) Utils.findRequiredViewAsType(source, R.id.checkbox_fastid, "field 'checkBoxFastid'", CheckBox.class);
        target.llJgTime = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.jgTime_ll, "field 'llJgTime'", LinearLayout.class);
        target.llDwell = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.dwell_ll, "field 'llDwell'", LinearLayout.class);
        target.llRadio = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.radio_ll, "field 'llRadio'", LinearLayout.class);
        target.llRadioSet = (LinearLayout) Utils.findRequiredViewAsType(source, R.id.radio_set_ll, "field 'llRadioSet'", LinearLayout.class);
        target.spDwell = (Spinner) Utils.findRequiredViewAsType(source, R.id.dwell_spinner, "field 'spDwell'", Spinner.class);
        target.spJgTime = (Spinner) Utils.findRequiredViewAsType(source, R.id.jgTime_spinner, "field 'spJgTime'", Spinner.class);
        target.rb1 = (RadioButton) Utils.findRequiredViewAsType(source, R.id.rb_perf, "field 'rb1'", RadioButton.class);
        target.rb2 = (RadioButton) Utils.findRequiredViewAsType(source, R.id.rb_bal, "field 'rb2'", RadioButton.class);
        target.rb3 = (RadioButton) Utils.findRequiredViewAsType(source, R.id.rb_ene, "field 'rb3'", RadioButton.class);
        target.rb4 = (RadioButton) Utils.findRequiredViewAsType(source, R.id.rb_cus, "field 'rb4'", RadioButton.class);
        target.read = (Button) Utils.findRequiredViewAsType(source, R.id.ivt_read, "field 'read'", Button.class);
        target.set = (Button) Utils.findRequiredViewAsType(source, R.id.ivt_setting, "field 'set'", Button.class);
        View view9 = Utils.findRequiredView(source, R.id.button_query_qvalue, "method 'queryQvalue'");
        this.view7f09006e = view9;
        view9.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment_ViewBinding.9
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.queryQvalue();
            }
        });
        View view10 = Utils.findRequiredView(source, R.id.button_query_temp, "method 'queryTemp'");
        this.view7f090070 = view10;
        view10.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment_ViewBinding.10
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.queryTemp();
            }
        });
        View view11 = Utils.findRequiredView(source, R.id.button_set_qvalue, "method 'setQvalue'");
        this.view7f090075 = view11;
        view11.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment_ViewBinding.11
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.setQvalue();
            }
        });
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        SettingsFragment target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.spinnerWorkFreq = null;
        target.spinnerPower = null;
        target.spinnerSession = null;
        target.spinnerQvalue = null;
        target.spinnerInventoryType = null;
        target.buttonFreqQuery = null;
        target.buttonFreqSet = null;
        target.editTextTemp = null;
        target.buttonQueryPower = null;
        target.buttonSetPower = null;
        target.buttonQueryInventory = null;
        target.buttonSetInventory = null;
        target.buttonQuerySession = null;
        target.buttonSetSession = null;
        target.checkBoxFastid = null;
        target.llJgTime = null;
        target.llDwell = null;
        target.llRadio = null;
        target.llRadioSet = null;
        target.spDwell = null;
        target.spJgTime = null;
        target.rb1 = null;
        target.rb2 = null;
        target.rb3 = null;
        target.rb4 = null;
        target.read = null;
        target.set = null;
        this.view7f090071.setOnClickListener(null);
        this.view7f090071 = null;
        this.view7f090077.setOnClickListener(null);
        this.view7f090077 = null;
        this.view7f09006d.setOnClickListener(null);
        this.view7f09006d = null;
        this.view7f090074.setOnClickListener(null);
        this.view7f090074 = null;
        this.view7f09006c.setOnClickListener(null);
        this.view7f09006c = null;
        this.view7f090073.setOnClickListener(null);
        this.view7f090073 = null;
        this.view7f09006f.setOnClickListener(null);
        this.view7f09006f = null;
        this.view7f090076.setOnClickListener(null);
        this.view7f090076 = null;
        this.view7f09006e.setOnClickListener(null);
        this.view7f09006e = null;
        this.view7f090070.setOnClickListener(null);
        this.view7f090070 = null;
        this.view7f090075.setOnClickListener(null);
        this.view7f090075 = null;
    }
}
