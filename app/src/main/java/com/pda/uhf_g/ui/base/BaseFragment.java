package com.pda.uhf_g.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

/* loaded from: classes.dex */
public class BaseFragment extends Fragment {
    public boolean isNavigationViewInit = false;
    private View lastView = null;
    private Toast toast;

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.lastView == null) {
            this.lastView = super.onCreateView(inflater, container, savedInstanceState);
        }
        return this.lastView;
    }

    @Override // androidx.fragment.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (!this.isNavigationViewInit) {
            super.onViewCreated(view, savedInstanceState);
            this.isNavigationViewInit = true;
        }
    }

    public void showToast(String msg) {
        try {
            Toast toast = this.toast;
            if (toast == null) {
                this.toast = Toast.makeText(getActivity(), msg, 0);
            } else {
                toast.cancel();
                this.toast = Toast.makeText(getActivity(), msg, 0);
            }
            this.toast.show();
        } catch (Exception e) {
        }
    }

    public void showToast(int resID) {
        try {
            Toast toast = this.toast;
            if (toast == null) {
                this.toast = Toast.makeText(getActivity(), resID, 0);
            } else {
                toast.cancel();
                this.toast = Toast.makeText(getActivity(), resID, 0);
            }
            this.toast.show();
        } catch (Exception e) {
        }
    }
}
