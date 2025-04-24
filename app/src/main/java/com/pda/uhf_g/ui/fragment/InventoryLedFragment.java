package com.pda.uhf_g.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.core.view.PointerIconCompat;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pda.serialport.Tools;
import com.pda.uhf_g.MainActivity;
import com.pda.uhf_g.R;
import com.pda.uhf_g.adapter.EPCListViewAdapter;
import com.pda.uhf_g.entity.TagInfo;
import com.pda.uhf_g.ui.base.BaseFragment;
import com.pda.uhf_g.util.LogUtil;
import com.pda.uhf_g.util.UtilSound;
import com.uhf.api.cls.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class InventoryLedFragment extends BaseFragment {

    @BindView(R.id.button_clean)
    Button btnClean;

    @BindView(R.id.button_cus_read)
    Button btnCusRead;

    @BindView(R.id.button_inventory)
    Button btnInventory;
    private EPCListViewAdapter epcListViewAdapter;
    private KeyReceiver keyReceiver;

    @BindView(R.id.listview_epc)
    ListView listViewEPC;
    private MainActivity mainActivity;

    @BindView(R.id.recycle)
    RecyclerView recyclerView;

    @BindView(R.id.textView_led)
    TextView tvLedString;
    private Map<String, TagInfo> tagInfoMap = new LinkedHashMap();
    private List<TagInfo> tagInfoList = new ArrayList();
    private Long index = 1L;
    private Handler soundHandler = new Handler();
    private boolean isReader = false;
    private long speed = 0;
    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    private final int MSG_INVENROTY = 1;
    private final int MSG_INVENROTY_TIME = PointerIconCompat.TYPE_CONTEXT_MENU;
    private boolean isMulti = false;
    private long lastCount = 0;
    private Runnable soundTask = null;
    private Handler handler = new Handler() { // from class: com.pda.uhf_g.ui.fragment.InventoryLedFragment.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                InventoryLedFragment inventoryLedFragment = InventoryLedFragment.this;
                long currentCount = inventoryLedFragment.getReadCount(inventoryLedFragment.tagInfoList);
                InventoryLedFragment inventoryLedFragment2 = InventoryLedFragment.this;
                inventoryLedFragment2.speed = currentCount - inventoryLedFragment2.lastCount;
                if (InventoryLedFragment.this.speed >= 0) {
                    InventoryLedFragment.this.lastCount = currentCount;
                }
            }
        }
    };
    String LedString = "";
    private Runnable invenrotyThread = new Runnable() { // from class: com.pda.uhf_g.ui.fragment.InventoryLedFragment.2
        @Override // java.lang.Runnable
        public void run() {
            LogUtil.e("invenrotyThread is running");
            if (!InventoryLedFragment.this.tvLedString.getText().toString().trim().equals("")) {
                InventoryLedFragment.this.mainActivity.mUhfrManager.getTagDataByFilter(0, 4, 1, Tools.HexString2Bytes("000000000"), (short) 50, Tools.HexString2Bytes(InventoryLedFragment.this.tvLedString.getText().toString().trim()), 1, 2, true);
            } else {
                List<Reader.TAGINFO> listTag = InventoryLedFragment.this.mainActivity.mUhfrManager.tagEpcOtherInventoryByTimer((short) 50, 0, 4, 4, Tools.HexString2Bytes("000000000"));
                if (listTag == null || listTag.isEmpty()) {
                    InventoryLedFragment.this.speed = 0L;
                } else {
                    LogUtil.e("inventory listTag size = " + listTag.size());
                    UtilSound.play(1, 0);
                    for (Reader.TAGINFO taginfo : listTag) {
                        Map<String, TagInfo> infoMap = InventoryLedFragment.this.pooled6cData(taginfo);
                        InventoryLedFragment.this.tagInfoList.clear();
                        InventoryLedFragment.this.tagInfoList.addAll(infoMap.values());
                        InventoryLedFragment.this.mainActivity.listEPC.clear();
                        InventoryLedFragment.this.mainActivity.listEPC.addAll(infoMap.keySet());
                    }
                    InventoryLedFragment.this.epcListViewAdapter.notifyDataSetChanged();
                    InventoryLedFragment.this.handler.sendEmptyMessage(1);
                }
            }
            InventoryLedFragment.this.handler.postDelayed(InventoryLedFragment.this.invenrotyThread, 0L);
        }
    };

    private void initPane() {
        this.index = 1L;
        this.tagInfoMap.clear();
        this.tagInfoList.clear();
        this.epcListViewAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getReadCount(List<TagInfo> tagInfoList) {
        long readCount = 0;
        for (int i = 0; i < tagInfoList.size(); i++) {
            readCount += tagInfoList.get(i).getCount().longValue();
        }
        return readCount;
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mainActivity = (MainActivity) getActivity();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        Log.e("pang", "onResume()");
        if (this.mainActivity.mUhfrManager != null) {
            this.mainActivity.mUhfrManager.setCancleInventoryFilter();
        }
        initView();
        registerKeyCodeReceiver();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
        Log.e("pang", "onPause()");
        stopInventory();
        this.mainActivity.unregisterReceiver(this.keyReceiver);
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }

    public Map<String, TagInfo> pooled6cData(Reader.TAGINFO info) {
        String epcAndTid = Tools.Bytes2HexString(info.EpcId, info.EpcId.length);
        if (this.tagInfoMap.containsKey(epcAndTid)) {
            TagInfo tagInfo = this.tagInfoMap.get(epcAndTid);
            Long count = tagInfo.getCount();
            Long count2 = Long.valueOf(count.longValue() + 1);
            tagInfo.setRssi(info.RSSI + "");
            tagInfo.setCount(count2);
            if (info.EmbededData != null && info.EmbededDatalen > 0) {
                tagInfo.setTid(Tools.Bytes2HexString(info.EmbededData, info.EmbededDatalen));
            }
            this.tagInfoMap.put(epcAndTid, tagInfo);
        } else {
            TagInfo tag = new TagInfo();
            tag.setIndex(this.index);
            tag.setType("6C");
            tag.setEpc(Tools.Bytes2HexString(info.EpcId, info.EpcId.length));
            tag.setCount(1L);
            if (info.EmbededData != null && info.EmbededDatalen > 0) {
                tag.setTid(Tools.Bytes2HexString(info.EmbededData, info.EmbededDatalen));
            }
            tag.setRssi(info.RSSI + "");
            this.tagInfoMap.put(epcAndTid, tag);
            this.index = Long.valueOf(this.index.longValue() + 1);
        }
        return this.tagInfoMap;
    }

    @OnClick({R.id.button_clean})
    public void clear() {
        initPane();
        this.mainActivity.listEPC.clear();
        if (!this.tvLedString.getText().toString().trim().equals("")) {
            this.tvLedString.setText("");
            this.mainActivity.mUhfrManager.setCancleInventoryFilter();
        }
    }

    @OnClick({R.id.button_inventory})
    public void invenroty() {
        if (this.mainActivity.mUhfrManager == null) {
            showToast(R.string.communication_timeout);
        } else if (!this.isReader) {
            inventoryEPC();
        } else {
            stopInventory();
        }
    }

    private void inventoryEPC() {
        this.isReader = true;
        inventory6C();
    }

    private void stopInventory() {
        if (this.mainActivity.mUhfrManager != null) {
            if (this.isReader) {
                this.handler.removeCallbacks(this.invenrotyThread);
                this.soundHandler.removeCallbacks(this.soundTask);
                this.isReader = false;
                this.btnInventory.setText(R.string.start_inventory);
            }
        } else {
            showToast(R.string.communication_timeout);
        }
        this.isReader = false;
    }

    private void inventory6C() {
        this.isReader = true;
        this.speed = 0L;
        this.btnInventory.setText(R.string.stop_inventory);
        showToast(R.string.start_inventory);
        this.mainActivity.mUhfrManager.setGen2session(1);
        this.handler.postDelayed(this.invenrotyThread, 0L);
    }

    @Override // com.pda.uhf_g.ui.base.BaseFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory_led, container, false);
        ButterKnife.bind(this, view);
        UtilSound.initSoundPool(this.mainActivity);
        return view;
    }

    private void registerKeyCodeReceiver() {
        this.keyReceiver = new KeyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.rfid.FUN_KEY");
        filter.addAction("android.intent.action.FUN_KEY");
        this.mainActivity.registerReceiver(this.keyReceiver, filter);
    }

    private void initView() {
        EPCListViewAdapter ePCListViewAdapter = new EPCListViewAdapter(this.mainActivity, this.tagInfoList);
        this.epcListViewAdapter = ePCListViewAdapter;
        this.listViewEPC.setAdapter((ListAdapter) ePCListViewAdapter);
        this.listViewEPC.setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.pda.uhf_g.ui.fragment.InventoryLedFragment.3
            @Override // android.widget.AdapterView.OnItemClickListener
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (((TagInfo) InventoryLedFragment.this.tagInfoList.get(position)).getEpc().equals(InventoryLedFragment.this.LedString)) {
                    InventoryLedFragment.this.LedString = "";
                } else {
                    InventoryLedFragment inventoryLedFragment = InventoryLedFragment.this;
                    inventoryLedFragment.LedString = ((TagInfo) inventoryLedFragment.tagInfoList.get(position)).getEpc();
                }
                InventoryLedFragment.this.tvLedString.setText(InventoryLedFragment.this.LedString);
            }
        });
    }

    private class KeyReceiver extends BroadcastReceiver {
        private KeyReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            int keyCode = intent.getIntExtra("keyCode", 0);
            LogUtil.e("keyCode = " + keyCode);
            if (keyCode == 0) {
                keyCode = intent.getIntExtra("keycode", 0);
            }
            boolean keyDown = intent.getBooleanExtra("keydown", false);
            if (!keyDown) {
                if (keyCode == 134 || keyCode == 135 || keyCode == 137) {
                    InventoryLedFragment.this.invenroty();
                }
            }
        }
    }
}
