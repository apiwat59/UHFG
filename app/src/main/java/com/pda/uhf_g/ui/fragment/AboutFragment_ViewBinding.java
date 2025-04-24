package com.pda.uhf_g.ui.fragment;

import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.pda.uhf_g.R;

/* loaded from: classes.dex */
public class AboutFragment_ViewBinding implements Unbinder {
    private AboutFragment target;

    public AboutFragment_ViewBinding(AboutFragment target, View source) {
        this.target = target;
        target.textViewFirmware = (TextView) Utils.findRequiredViewAsType(source, R.id.textView_firmware, "field 'textViewFirmware'", TextView.class);
        target.textViewDate = (TextView) Utils.findRequiredViewAsType(source, R.id.textView_date, "field 'textViewDate'", TextView.class);
        target.textViewSoft = (TextView) Utils.findRequiredViewAsType(source, R.id.textView_soft, "field 'textViewSoft'", TextView.class);
    }

    @Override // butterknife.Unbinder
    public void unbind() {
        AboutFragment target = this.target;
        if (target == null) {
            throw new IllegalStateException("Bindings already cleared.");
        }
        this.target = null;
        target.textViewFirmware = null;
        target.textViewDate = null;
        target.textViewSoft = null;
    }
}
