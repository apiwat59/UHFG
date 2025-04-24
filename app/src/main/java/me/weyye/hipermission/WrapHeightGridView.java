package me.weyye.hipermission;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

/* loaded from: classes.dex */
public class WrapHeightGridView extends GridView {
    public boolean isOnMeasure;

    public WrapHeightGridView(Context context) {
        super(context);
    }

    public WrapHeightGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WrapHeightGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.widget.GridView, android.widget.AbsListView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.isOnMeasure = true;
        int heightSpec = View.MeasureSpec.makeMeasureSpec(536870911, Integer.MIN_VALUE);
        super.onMeasure(widthMeasureSpec, heightSpec);
    }

    @Override // android.widget.AbsListView, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        this.isOnMeasure = false;
        super.onLayout(changed, l, t, r, b);
    }
}
