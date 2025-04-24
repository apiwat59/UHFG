package com.pda.uhf_g.ui.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.pda.uhf_g.R;

/* loaded from: classes.dex */
public class ReadWriteTag_ViewBinding implements Unbinder {
    private ReadWriteTag target;
    private View view7f090065;
    private View view7f090069;
    private View view7f09006a;
    private View view7f09006b;
    private View view7f090072;
    private View view7f090078;

    public ReadWriteTag_ViewBinding(final ReadWriteTag target, View source) {
        this.target = target;
        target.spinnerEPC = (Spinner) Utils.findRequiredViewAsType(source, R.id.spinner_epc, "field 'spinnerEPC'", Spinner.class);
        target.radioGroupMembank = (RadioGroup) Utils.findRequiredViewAsType(source, R.id.radio_membank, "field 'radioGroupMembank'", RadioGroup.class);
        target.checkBoxFilter = (CheckBox) Utils.findRequiredViewAsType(source, R.id.checkbox_filter, "field 'checkBoxFilter'", CheckBox.class);
        target.editTextStartAddr = (EditText) Utils.findRequiredViewAsType(source, R.id.editText_start_addr, "field 'editTextStartAddr'", EditText.class);
        target.editTextLen = (EditText) Utils.findRequiredViewAsType(source, R.id.editText_len, "field 'editTextLen'", EditText.class);
        target.editTextAccessPassword = (EditText) Utils.findRequiredViewAsType(source, R.id.editText_access_password, "field 'editTextAccessPassword'", EditText.class);
        target.editTextWriteData = (EditText) Utils.findRequiredViewAsType(source, R.id.editText_write_data, "field 'editTextWriteData'", EditText.class);
        target.editTextReadData = (EditText) Utils.findRequiredViewAsType(source, R.id.editText_read_data, "field 'editTextReadData'", EditText.class);
        View view = Utils.findRequiredView(source, R.id.button_read, "field 'buttonRead' and method 'readData'");
        target.buttonRead = (Button) Utils.castView(view, R.id.button_read, "field 'buttonRead'", Button.class);
        this.view7f090072 = view;
        view.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.ReadWriteTag_ViewBinding.1
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.readData();
            }
        });
        View view2 = Utils.findRequiredView(source, R.id.button_write, "field 'buttonWrite' and method 'write'");
        target.buttonWrite = (Button) Utils.castView(view2, R.id.button_write, "field 'buttonWrite'", Button.class);
        this.view7f090078 = view2;
        view2.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.ReadWriteTag_ViewBinding.2
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.write();
            }
        });
        View view3 = Utils.findRequiredView(source, R.id.button_clean, "field 'buttonClean' and method 'clean'");
        target.buttonClean = (Button) Utils.castView(view3, R.id.button_clean, "field 'buttonClean'", Button.class);
        this.view7f090065 = view3;
        view3.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.ReadWriteTag_ViewBinding.3
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.clean();
            }
        });
        target.spinnerLockData = (Spinner) Utils.findRequiredViewAsType(source, R.id.sipnner_lock_data, "field 'spinnerLockData'", Spinner.class);
        target.spinnerLockType = (Spinner) Utils.findRequiredViewAsType(source, R.id.sipnner_lock_type, "field 'spinnerLockType'", Spinner.class);
        target.editTextLockPassword = (EditText) Utils.findRequiredViewAsType(source, R.id.editText_lock_password, "field 'editTextLockPassword'", EditText.class);
        View view4 = Utils.findRequiredView(source, R.id.button_lock, "field 'buttonLock' and method 'lock'");
        target.buttonLock = (Button) Utils.castView(view4, R.id.button_lock, "field 'buttonLock'", Button.class);
        this.view7f09006a = view4;
        view4.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.ReadWriteTag_ViewBinding.4
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.lock();
            }
        });
        target.editTextKillPassword = (EditText) Utils.findRequiredViewAsType(source, R.id.editText_kill_password, "field 'editTextKillPassword'", EditText.class);
        View view5 = Utils.findRequiredView(source, R.id.button_kill, "field 'buttonKill' and method 'kill'");
        target.buttonKill = (Button) Utils.castView(view5, R.id.button_kill, "field 'buttonKill'", Button.class);
        this.view7f090069 = view5;
        view5.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.ReadWriteTag_ViewBinding.5
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.kill();
            }
        });
        target.editTextNewEPC = (EditText) Utils.findRequiredViewAsType(source, R.id.editText_new_epc, "field 'editTextNewEPC'", EditText.class);
        View view6 = Utils.findRequiredView(source, R.id.button_modify, "field 'buttonModify' and method 'modifyEPC'");
        target.buttonModify = (Button) Utils.castView(view6, R.id.button_modify, "field 'buttonModify'", Button.class);
        this.view7f09006b = view6;
        view6.setOnClickListener(new DebouncingOnClickListener() { // from class: com.pda.uhf_g.ui.fragment.ReadWriteTag_ViewBinding.6
            @Override // butterknife.internal.DebouncingOnClickListener
            public void doClick(View p0) {
                target.modifyEPC();
            }
        });
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        ReadWriteTag target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.spinnerEPC = null;
        target.radioGroupMembank = null;
        target.checkBoxFilter = null;
        target.editTextStartAddr = null;
        target.editTextLen = null;
        target.editTextAccessPassword = null;
        target.editTextWriteData = null;
        target.editTextReadData = null;
        target.buttonRead = null;
        target.buttonWrite = null;
        target.buttonClean = null;
        target.spinnerLockData = null;
        target.spinnerLockType = null;
        target.editTextLockPassword = null;
        target.buttonLock = null;
        target.editTextKillPassword = null;
        target.buttonKill = null;
        target.editTextNewEPC = null;
        target.buttonModify = null;
        this.view7f090072.setOnClickListener(null);
        this.view7f090072 = null;
        this.view7f090078.setOnClickListener(null);
        this.view7f090078 = null;
        this.view7f090065.setOnClickListener(null);
        this.view7f090065 = null;
        this.view7f09006a.setOnClickListener(null);
        this.view7f09006a = null;
        this.view7f090069.setOnClickListener(null);
        this.view7f090069 = null;
        this.view7f09006b.setOnClickListener(null);
        this.view7f09006b = null;
    }
}
