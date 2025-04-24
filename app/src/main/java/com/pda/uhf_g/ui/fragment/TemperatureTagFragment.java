package com.pda.uhf_g.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pda.serialport.Tools;
import com.handheld.uhfr.Reader;
import com.pda.uhf_g.MainActivity;
import com.pda.uhf_g.R;
import com.pda.uhf_g.adapter.TempTagListViewAdapter;
import com.pda.uhf_g.ui.base.BaseFragment;
import com.pda.uhf_g.util.LogUtil;
import com.pda.uhf_g.util.UtilSound;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
public class TemperatureTagFragment extends BaseFragment {
    private TempTagListViewAdapter adapter;

    @BindView(R.id.button_clean)
    Button btnClean;

    @BindView(R.id.button_read)
    Button btnRead;
    private KeyReceiver keyReceiver;

    @BindView(R.id.listVew_epc)
    ListView listView;
    private MainActivity mainActivity;

    @BindView(R.id.spinner_manufactorer)
    Spinner spinnerTagManufactorer;
    private Handler mHandler = new Handler();
    private Map<String, Reader.TEMPTAGINFO> tagMap = new LinkedHashMap();
    private List<Reader.TEMPTAGINFO> listTag = new ArrayList();
    private boolean isRead = false;
    private int tagType = 0;
    private int index = 1;
    private Runnable readThread = new Runnable() { // from class: com.pda.uhf_g.ui.fragment.TemperatureTagFragment.2
        @Override // java.lang.Runnable
        public void run() {
            List<Reader.TEMPTAGINFO> list = null;
            if (TemperatureTagFragment.this.tagType == 0) {
                list = TemperatureTagFragment.this.mainActivity.mUhfrManager.getYueheTagTemperature(null);
            } else if (TemperatureTagFragment.this.tagType == 1) {
                list = TemperatureTagFragment.this.mainActivity.mUhfrManager.getYilianTagTemperature();
            }
            if (list != null && !list.isEmpty()) {
                UtilSound.voiceTips(MainActivity.type, list.size(), false);
                for (Reader.TEMPTAGINFO info : list) {
                    Map<String, Reader.TEMPTAGINFO> map = TemperatureTagFragment.this.pooledTagData(info);
                    TemperatureTagFragment.this.listTag.clear();
                    TemperatureTagFragment.this.listTag.addAll(map.values());
                    Log.e("pang", "epc:" + Tools.Bytes2HexString(info.EpcId, info.Epclen) + ", temp = " + info.Temperature);
                }
                TemperatureTagFragment.this.adapter.notifyDataSetChanged();
            }
            TemperatureTagFragment.this.mHandler.post(TemperatureTagFragment.this.readThread);
        }
    };

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mainActivity = (MainActivity) getActivity();
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
    }

    @Override // androidx.fragment.app.Fragment
    public void onStop() {
        this.isRead = false;
        this.btnRead.setText(R.string.read);
        this.mHandler.removeCallbacks(this.readThread);
        this.mainActivity.unregisterReceiver(this.keyReceiver);
        super.onStop();
    }

    @Override // androidx.fragment.app.Fragment
    public void onResume() {
        super.onResume();
        if (this.mainActivity.mUhfrManager != null) {
            this.mainActivity.mUhfrManager.setCancleInventoryFilter();
        }
        registerKeyCodeReceiver();
    }

    @Override // androidx.fragment.app.Fragment
    public void onPause() {
        super.onPause();
    }

    @Override // com.pda.uhf_g.ui.base.BaseFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_temperature_tag, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        TempTagListViewAdapter tempTagListViewAdapter = new TempTagListViewAdapter(this.mainActivity, this.listTag);
        this.adapter = tempTagListViewAdapter;
        this.listView.setAdapter((ListAdapter) tempTagListViewAdapter);
        this.spinnerTagManufactorer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.pda.uhf_g.ui.fragment.TemperatureTagFragment.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (TemperatureTagFragment.this.tagType != position) {
                    TemperatureTagFragment.this.onClean();
                }
                TemperatureTagFragment.this.tagType = position;
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public Map<String, Reader.TEMPTAGINFO> pooledTagData(Reader.TEMPTAGINFO info) {
        String epc = Tools.Bytes2HexString(info.EpcId, info.Epclen);
        if (this.tagMap.containsKey(epc)) {
            Reader.TEMPTAGINFO tag = this.tagMap.get(epc);
            int count = tag.count;
            tag.count = count + 1;
            tag.Temperature = info.Temperature;
            this.tagMap.put(epc, tag);
        } else {
            info.index = this.index;
            info.count = 1;
            this.index++;
            this.tagMap.put(epc, info);
        }
        return this.tagMap;
    }

    @OnClick({R.id.button_read})
    public void onReadTag() {
        if (!this.isRead) {
            this.isRead = true;
            this.btnRead.setText(R.string.stop_read);
            this.mHandler.postDelayed(this.readThread, 0L);
        } else {
            this.isRead = false;
            this.btnRead.setText(R.string.read);
            this.mHandler.removeCallbacks(this.readThread);
        }
    }

    @OnClick({R.id.button_clean})
    public void onClean() {
        this.listTag.clear();
        this.tagMap.clear();
        this.index = 1;
        this.adapter.notifyDataSetChanged();
    }

    private void registerKeyCodeReceiver() {
        this.keyReceiver = new KeyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.rfid.FUN_KEY");
        filter.addAction("android.intent.action.FUN_KEY");
        this.mainActivity.registerReceiver(this.keyReceiver, filter);
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
                if (keyCode == 133 || keyCode == 134 || keyCode == 137) {
                    TemperatureTagFragment.this.onReadTag();
                }
            }
        }
    }
}
