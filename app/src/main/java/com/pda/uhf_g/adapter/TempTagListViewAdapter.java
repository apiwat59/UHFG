package com.pda.uhf_g.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.pda.serialport.Tools;
import com.handheld.uhfr.Reader;
import com.pda.uhf_g.R;
import java.util.List;

/* loaded from: classes.dex */
public class TempTagListViewAdapter extends BaseAdapter {
    private List<Reader.TEMPTAGINFO> list;
    private Context mContext;

    public TempTagListViewAdapter(Context mContext, List<Reader.TEMPTAGINFO> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override // android.widget.Adapter
    public int getCount() {
        return this.list.size();
    }

    @Override // android.widget.Adapter
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @Override // android.widget.Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(this.mContext).inflate(R.layout.tag_item, (ViewGroup) null);
            viewHolder.index = (TextView) convertView.findViewById(R.id.tv_sn);
            viewHolder.epc = (TextView) convertView.findViewById(R.id.tv_epc);
            viewHolder.temp = (TextView) convertView.findViewById(R.id.tv_temp);
            viewHolder.count = (TextView) convertView.findViewById(R.id.tv_count);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        List<Reader.TEMPTAGINFO> list = this.list;
        if (list != null && !list.isEmpty()) {
            Reader.TEMPTAGINFO info = this.list.get(position);
            viewHolder.index.setText(info.index + "");
            viewHolder.epc.setText(Tools.Bytes2HexString(info.EpcId, info.Epclen));
            viewHolder.temp.setText(Double.toString(info.Temperature));
            viewHolder.count.setText(info.count + "");
        }
        return convertView;
    }

    class ViewHolder {
        TextView count;
        TextView epc;
        TextView index;
        TextView temp;

        ViewHolder() {
        }
    }
}
