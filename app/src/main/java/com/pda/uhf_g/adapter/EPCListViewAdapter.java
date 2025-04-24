package com.pda.uhf_g.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.pda.uhf_g.R;
import com.pda.uhf_g.entity.TagInfo;
import java.util.List;

/* loaded from: classes.dex */
public class EPCListViewAdapter extends BaseAdapter {
    private List<TagInfo> list;
    private Context mContext;

    public EPCListViewAdapter(Context context, List<TagInfo> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.list.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int i) {
        return this.list.get(i);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.recycle_item, (ViewGroup) null);
            viewHolder.index = (TextView) convertView.findViewById(R.id.index);
            viewHolder.type = (TextView) convertView.findViewById(R.id.type);
            viewHolder.sensor_data = (TextView) convertView.findViewById(R.id.sensor_data);
            viewHolder.epc = (TextView) convertView.findViewById(R.id.epc);
            viewHolder.tid = (TextView) convertView.findViewById(R.id.tid);
            viewHolder.rssi = (TextView) convertView.findViewById(R.id.rssi);
            viewHolder.count = (TextView) convertView.findViewById(R.id.count);
            viewHolder.layoutTid = (LinearLayout) convertView.findViewById(R.id.layout_tid);
            viewHolder.tvTid = (TextView) convertView.findViewById(R.id.tv_tid);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        List<TagInfo> list = this.list;
        if (list != null && !list.isEmpty()) {
            TagInfo tag = this.list.get(position);
            viewHolder.index.setText(tag.getIndex().toString());
            viewHolder.type.setText(tag.getType());
            viewHolder.epc.setText(tag.getEpc());
            viewHolder.tid.setText(tag.getTid());
            viewHolder.rssi.setText(tag.getRssi());
            viewHolder.count.setText(tag.getCount().toString());
            if (tag.getIsShowTid()) {
                viewHolder.layoutTid.setVisibility(0);
                viewHolder.tvTid.setText(tag.getTid());
            } else {
                viewHolder.layoutTid.setVisibility(8);
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView count;
        TextView epc;
        TextView index;
        LinearLayout layoutTid;
        TextView rssi;
        TextView sensor_data;
        TextView tid;
        TextView tvTid;
        TextView type;

        ViewHolder() {
        }
    }
}
