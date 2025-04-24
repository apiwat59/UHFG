package me.weyye.hipermission;

import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/* loaded from: classes.dex */
public class PermissionAdapter extends BaseAdapter {
    private List<PermissionItem> mData;
    private int mFilterColor;
    private int mTextColor;

    public PermissionAdapter(List<PermissionItem> data) {
        this.mData = data;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.mData.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return this.mData.get(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return 0L;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        PermissionItem item = this.mData.get(position);
        View view = View.inflate(parent.getContext(), R.layout.permission_info_item, null);
        int blue = Color.blue(this.mFilterColor);
        int green = Color.green(this.mFilterColor);
        int red = Color.red(this.mFilterColor);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        float[] cm = {1.0f, 0.0f, 0.0f, 0.0f, red, 0.0f, 1.0f, 0.0f, 0.0f, green, 0.0f, 0.0f, 1.0f, 0.0f, blue, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f};
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
        icon.setColorFilter(filter);
        TextView name = (TextView) view.findViewById(R.id.name);
        int i = this.mTextColor;
        if (i != 0) {
            name.setTextColor(i);
        }
        icon.setImageResource(item.PermissionIconRes);
        name.setText(item.PermissionName);
        return view;
    }

    public void setTextColor(int itemTextColor) {
        this.mTextColor = itemTextColor;
        notifyDataSetChanged();
    }

    public void setFilterColor(int filterColor) {
        this.mFilterColor = filterColor;
        notifyDataSetChanged();
    }
}
