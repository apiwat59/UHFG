package com.pda.uhf_g.ui.fragment;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.handheld.uhfr.UHFRManager;
import com.pda.uhf_g.BuildConfig;
import com.pda.uhf_g.MainActivity;
import com.pda.uhf_g.R;
import com.pda.uhf_g.ui.base.BaseFragment;

/* loaded from: classes.dex */
public class AboutFragment extends BaseFragment {
    private MainActivity mainActivity;

    @BindView(R.id.textView_date)
    TextView textViewDate;

    @BindView(R.id.textView_firmware)
    TextView textViewFirmware;

    @BindView(R.id.textView_soft)
    TextView textViewSoft;
    private UHFRManager uhfrManager;
    private long lastClickTime = SystemClock.elapsedRealtime();
    private int firmwareClickCount = 0;

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override // com.pda.uhf_g.ui.base.BaseFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, (ViewGroup) null);
        ButterKnife.bind(this, view);
        MainActivity mainActivity = (MainActivity) getActivity();
        this.mainActivity = mainActivity;
        this.uhfrManager = mainActivity.mUhfrManager;
        initView();
        return view;
    }

    private void initView() {
        if (this.mainActivity.isConnectUHF) {
            String version = this.uhfrManager.getHardware();
            String strVer = getResources().getString(R.string.firmware);
            String strSoft = getResources().getString(R.string.soft_version);
            String strDate = getResources().getString(R.string.version_date);
            String strSoft2 = String.format(strSoft, BuildConfig.VERSION_NAME);
            String strDate2 = String.format(strDate, BuildConfig.BUILD_TIME);
            if (version != null && version.length() > 0) {
                this.textViewFirmware.setText(String.format(strVer, version));
            }
            this.textViewSoft.setText(strSoft2);
            this.textViewDate.setText(strDate2);
            this.textViewFirmware.setOnClickListener(new View.OnClickListener() { // from class: com.pda.uhf_g.ui.fragment.-$$Lambda$AboutFragment$vfz-DkwyBmJXRcxzXPQmulIUwZM
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AboutFragment.this.lambda$initView$0$AboutFragment(view);
                }
            });
        }
    }

    public /* synthetic */ void lambda$initView$0$AboutFragment(View v) {
        long nowTime = SystemClock.elapsedRealtime();
        if (nowTime - this.lastClickTime < 500) {
            int i = this.firmwareClickCount + 1;
            this.firmwareClickCount = i;
            if (i == 7) {
                this.mainActivity.mSharedPreferences.edit().putBoolean("show_rr_advance_settings", true).apply();
                this.mainActivity.navController.navigate(R.id.nav_setting);
            }
        }
        this.lastClickTime = nowTime;
    }
}
