package com.pda.uhf_g.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.pda.uhf_g.R;
import com.pda.uhf_g.entity.TagInfo;
import com.pda.uhf_g.util.LogUtil;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.List;

/* loaded from: classes.dex */
public class RecycleViewAdapter extends RecyclerView.Adapter<ViewHolder> {
    private List<TagInfo> mTagList;
    private NumberFormat nf;
    private TextView showText;
    private Integer thisPosition = null;
    private boolean isTid = true;

    public Integer getThisPosition() {
        return this.thisPosition;
    }

    public void setThisPosition(Integer thisPosition) {
        this.thisPosition = thisPosition;
    }

    public boolean isTid() {
        return this.isTid;
    }

    public void setTid(boolean tid) {
        this.isTid = tid;
    }

    public TextView getShowText() {
        return this.showText;
    }

    public void setShowText(TextView showText) {
        this.showText = showText;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView count;
        TextView ctesius_data;
        TextView epc;
        TextView index;
        LinearLayout layoutTid;
        TextView rssi;
        TextView sensor_data;
        TextView tid;
        TextView tvTid;
        TextView type;

        public ViewHolder(View view) {
            super(view);
            this.index = (TextView) view.findViewById(R.id.index);
            this.type = (TextView) view.findViewById(R.id.type);
            this.sensor_data = (TextView) view.findViewById(R.id.sensor_data);
            this.epc = (TextView) view.findViewById(R.id.epc);
            this.tid = (TextView) view.findViewById(R.id.tid);
            this.rssi = (TextView) view.findViewById(R.id.rssi);
            this.count = (TextView) view.findViewById(R.id.count);
            this.ctesius_data = (TextView) view.findViewById(R.id.ctesius_data);
            this.layoutTid = (LinearLayout) view.findViewById(R.id.layout_tid);
            this.tvTid = (TextView) view.findViewById(R.id.tv_tid);
        }
    }

    public RecycleViewAdapter(List<TagInfo> list) {
        NumberFormat numberInstance = NumberFormat.getNumberInstance();
        this.nf = numberInstance;
        this.mTagList = list;
        numberInstance.setMaximumFractionDigits(2);
        this.nf.setRoundingMode(RoundingMode.DOWN);
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() { // from class: com.pda.uhf_g.adapter.RecycleViewAdapter.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                RecycleViewAdapter.this.setThisPosition(Integer.valueOf(holder.getAdapterPosition()));
                RecycleViewAdapter.this.notifyDataSetChanged();
            }
        });
        return holder;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(ViewHolder holder, int position) {
        TagInfo tag = this.mTagList.get(position);
        holder.index.setText(tag.getIndex().toString());
        holder.type.setText(tag.getType());
        holder.ctesius_data.setVisibility(8);
        holder.epc.setText(tag.getEpc());
        holder.tid.setText(tag.getTid());
        holder.rssi.setText(tag.getRssi());
        holder.count.setText(tag.getCount().toString());
        if (tag.getIsShowTid()) {
            holder.layoutTid.setVisibility(0);
            holder.tvTid.setText(tag.getTid());
        } else {
            holder.layoutTid.setVisibility(8);
        }
        if (getThisPosition() != null && position == getThisPosition().intValue()) {
            holder.itemView.setBackgroundColor(Color.rgb(135, 206, 235));
        } else {
            holder.itemView.setBackgroundColor(-1);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return this.mTagList.size();
    }

    public void notifyData(List<TagInfo> poiItemList) {
        LogUtil.e("RecycleViewAdapter notifyData()");
        if (poiItemList != null) {
            this.mTagList = poiItemList;
            notifyDataSetChanged();
        }
    }
}
