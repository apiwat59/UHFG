package butterknife.internal;

import android.view.View;

/* loaded from: classes.dex */
public abstract class DebouncingOnClickListener implements View.OnClickListener {
    static boolean enabled = true;
    private static final Runnable ENABLE_AGAIN = new Runnable() { // from class: butterknife.internal.-$$Lambda$DebouncingOnClickListener$9VMd1QuOmdVg_AaYBDeumcPsza8
        @Override // java.lang.Runnable
        public final void run() {
            DebouncingOnClickListener.enabled = true;
        }
    };

    public abstract void doClick(View view);

    @Override // android.view.View.OnClickListener
    public final void onClick(View v) {
        if (enabled) {
            enabled = false;
            v.post(ENABLE_AGAIN);
            doClick(v);
        }
    }
}
