package com.pda.uhf_g.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.core.view.PointerIconCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pda.serialport.Tools;
import com.handheld.uhfr.UHFRManager;
import com.pda.uhf_g.MainActivity;
import com.pda.uhf_g.R;
import com.pda.uhf_g.adapter.EPCListViewAdapter;
import com.pda.uhf_g.entity.TagInfo;
import com.pda.uhf_g.ui.base.BaseFragment;
import com.pda.uhf_g.util.ExcelUtil;
import com.pda.uhf_g.util.LogUtil;
import com.pda.uhf_g.util.SharedUtil;
import com.pda.uhf_g.util.UtilSound;
import com.uhf.api.cls.Reader;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

/* loaded from: classes.dex */
public class InventoryFragment extends BaseFragment {

    @BindView(R.id.button_clean)
    Button btnClean;

    @BindView(R.id.button_cus_read)
    Button btnCusRead;

    @BindView(R.id.button_excel)
    Button btnExcel;

    @BindView(R.id.button_inventory)
    Button btnInventory;

    @BindView(R.id.checkbox_loop)
    CheckBox checkBoxLoop;

    @BindView(R.id.checkbox_multi_tag)
    CheckBox checkBoxMultiTag;

    @BindView(R.id.checkbox_tid)
    CheckBox checkBoxTid;
    private EPCListViewAdapter epcListViewAdapter;
    private KeyReceiver keyReceiver;

    @BindView(R.id.listview_epc)
    ListView listViewEPC;
    public UHFRManager mUhfrManager;
    private MainActivity mainActivity;
    SharedUtil sharedUtil;
    private Timer timer;

    @BindView(R.id.textView_all_tags)
    TextView tvAllTag;

    @BindView(R.id.textView_readCount)
    TextView tvReadCount;

    @BindView(R.id.textView_speed)
    TextView tvSpeed;

    @BindView(R.id.textView_timeCount)
    TextView tvTime;
    private Map<String, TagInfo> tagInfoMap = new LinkedHashMap();
    private List<TagInfo> tagInfoList = new ArrayList();
    private int isCtesius = 0;
    private Long index = 1L;
    private Handler mHandler = new Handler();
    private Handler soundHandler = new Handler();
    private Runnable timeTask = null;
    private Runnable soundTask = null;
    private int time = 0;
    private boolean isReader = false;
    private boolean isSound = true;
    private boolean[] isChecked = {false, false, false};
    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    private boolean isMulti = false;
    private final int MSG_INVENROTY = 1;
    private final int MSG_INVENROTY_TIME = PointerIconCompat.TYPE_CONTEXT_MENU;
    private long lastCount = 0;
    private long speed = 0;
    private Handler handler = new Handler() { // from class: com.pda.uhf_g.ui.fragment.InventoryFragment.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if (i != 1) {
                if (i == 1001) {
                    InventoryFragment.access$408(InventoryFragment.this);
                    TextView textView = InventoryFragment.this.tvTime;
                    StringBuilder sb = new StringBuilder();
                    sb.append(InventoryFragment.this.secToTime(r2.time));
                    sb.append("s");
                    textView.setText(sb.toString());
                    return;
                }
                return;
            }
            TextView textView2 = InventoryFragment.this.tvReadCount;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("");
            InventoryFragment inventoryFragment = InventoryFragment.this;
            sb2.append(inventoryFragment.getReadCount(inventoryFragment.tagInfoList));
            textView2.setText(sb2.toString());
            InventoryFragment.this.tvAllTag.setText("" + InventoryFragment.this.tagInfoList.size());
            InventoryFragment inventoryFragment2 = InventoryFragment.this;
            long currentCount = inventoryFragment2.getReadCount(inventoryFragment2.tagInfoList);
            InventoryFragment inventoryFragment3 = InventoryFragment.this;
            inventoryFragment3.speed = currentCount - inventoryFragment3.lastCount;
            if (InventoryFragment.this.speed >= 0) {
                InventoryFragment.this.lastCount = currentCount;
                InventoryFragment.this.tvSpeed.setText(InventoryFragment.this.speed + "");
            }
        }
    };
    private Runnable invenrotyThread = new Runnable() { // from class: com.pda.uhf_g.ui.fragment.InventoryFragment.2
        @Override // java.lang.Runnable
        public void run() {
            List<Reader.TAGINFO> listTag;
            LogUtil.e("invenrotyThread is running");
            if (InventoryFragment.this.isMulti) {
                listTag = InventoryFragment.this.mainActivity.mUhfrManager.tagInventoryRealTime();
            } else {
                listTag = InventoryFragment.this.checkBoxTid.isChecked() ? InventoryFragment.this.mainActivity.mUhfrManager.tagEpcTidInventoryByTimer((short) 50) : InventoryFragment.this.mainActivity.mUhfrManager.tagInventoryByTimer((short) 50);
            }
            if (listTag == null) {
                LogUtil.e("listTag = null");
                if (InventoryFragment.this.checkBoxMultiTag.isChecked()) {
                    InventoryFragment.this.mainActivity.mUhfrManager.asyncStopReading();
                    InventoryFragment.this.mainActivity.mUhfrManager.asyncStartReading();
                }
            }
            if (listTag == null || listTag.isEmpty()) {
                InventoryFragment.this.speed = 0L;
            } else {
                LogUtil.e("inventory listTag size = " + listTag.size());
                UtilSound.play(1, 0);
                for (Reader.TAGINFO taginfo : listTag) {
                    Map<String, TagInfo> infoMap = InventoryFragment.this.pooled6cData(taginfo);
                    InventoryFragment.this.tagInfoList.clear();
                    InventoryFragment.this.tagInfoList.addAll(infoMap.values());
                    InventoryFragment.this.mainActivity.listEPC.clear();
                    InventoryFragment.this.mainActivity.listEPC.addAll(infoMap.keySet());
                }
                InventoryFragment.this.epcListViewAdapter.notifyDataSetChanged();
                InventoryFragment.this.handler.sendEmptyMessage(1);
            }
            if (InventoryFragment.this.checkBoxLoop.isChecked()) {
                InventoryFragment.this.handler.postDelayed(InventoryFragment.this.invenrotyThread, 0L);
                return;
            }
            if (InventoryFragment.this.timer != null) {
                InventoryFragment.this.timer.cancel();
                InventoryFragment.this.timer = null;
            }
            InventoryFragment.this.handler.sendEmptyMessage(PointerIconCompat.TYPE_CONTEXT_MENU);
            InventoryFragment.this.isReader = false;
        }
    };
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
    private TimerTask timerTask = new TimerTask() { // from class: com.pda.uhf_g.ui.fragment.InventoryFragment.5
        @Override // java.util.TimerTask, java.lang.Runnable
        public void run() {
        }
    };

    static /* synthetic */ int access$408(InventoryFragment x0) {
        int i = x0.time;
        x0.time = i + 1;
        return i;
    }

    private void initPane() {
        this.index = 1L;
        this.time = 0;
        this.lastCount = 0L;
        this.tagInfoMap.clear();
        this.tagInfoList.clear();
        this.epcListViewAdapter.notifyDataSetChanged();
        this.tvAllTag.setText("0");
        this.tvReadCount.setText("0");
        this.tvTime.setText("00:00:00 s");
        this.tvSpeed.setText("0");
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
        registerKeyCodeReceiver();
        if (this.sharedUtil.getFastId()) {
            this.checkBoxTid.setChecked(false);
        }
        this.checkBoxTid.setEnabled(!this.sharedUtil.getFastId());
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
        Log.i("Inv", "[pooled6cData] tag epc: " + epcAndTid);
        if (this.checkBoxTid.isChecked()) {
            if (info.EmbededData != null) {
                epcAndTid = Tools.Bytes2HexString(info.EmbededData, info.EmbededData.length);
                Log.i("Inv", "[pooled6cData] tag tid: " + epcAndTid);
                if (TextUtils.isEmpty(epcAndTid)) {
                    return this.tagInfoMap;
                }
            } else {
                Log.i("Inv", "[pooled6cData] drop null tid tag");
                return this.tagInfoMap;
            }
        }
        if (this.tagInfoMap.containsKey(epcAndTid)) {
            TagInfo tagInfo = this.tagInfoMap.get(epcAndTid);
            Long count = tagInfo.getCount();
            Long count2 = Long.valueOf(count.longValue() + 1);
            tagInfo.setRssi(info.RSSI + "");
            tagInfo.setCount(count2);
            tagInfo.setIsShowTid(this.checkBoxTid.isChecked());
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
            tag.setIsShowTid(this.checkBoxTid.isChecked());
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

    private void setEnabled(boolean isEnable) {
        this.checkBoxLoop.setEnabled(isEnable);
        this.checkBoxLoop.setEnabled(isEnable);
        this.checkBoxMultiTag.setEnabled(isEnable);
        if (!this.sharedUtil.getFastId()) {
            this.checkBoxTid.setEnabled(isEnable);
        }
        this.btnExcel.setEnabled(isEnable);
    }

    private void inventoryEPC() {
        this.isReader = true;
        this.speed = 0L;
        if (this.checkBoxLoop.isChecked()) {
            this.btnInventory.setText(R.string.stop_inventory);
            setEnabled(false);
        }
        showToast(R.string.start_inventory);
        if (this.mainActivity.mUhfrManager.getGen2session() != 3) {
            this.mainActivity.mUhfrManager.setGen2session(this.isMulti);
        }
        if (this.isMulti) {
            this.mainActivity.mUhfrManager.asyncStartReading();
        }
        if (this.timer == null) {
            Timer timer = new Timer();
            this.timer = timer;
            timer.schedule(new TimerTask() { // from class: com.pda.uhf_g.ui.fragment.InventoryFragment.3
                @Override // java.util.TimerTask, java.lang.Runnable
                public void run() {
                    InventoryFragment.this.handler.sendEmptyMessage(PointerIconCompat.TYPE_CONTEXT_MENU);
                }
            }, 1000L, 1000L);
        }
        this.handler.postDelayed(this.invenrotyThread, 0L);
    }

    private void stopInventory() {
        if (this.mainActivity.isConnectUHF) {
            if (this.isReader) {
                if (this.checkBoxMultiTag.isChecked()) {
                    this.mainActivity.mUhfrManager.asyncStopReading();
                }
                this.handler.removeCallbacks(this.invenrotyThread);
                this.soundHandler.removeCallbacks(this.soundTask);
                this.isReader = false;
                Timer timer = this.timer;
                if (timer != null) {
                    timer.cancel();
                    this.timer = null;
                }
                this.btnInventory.setText(R.string.start_inventory);
            }
        } else {
            showToast(R.string.communication_timeout);
        }
        this.isReader = false;
        setEnabled(true);
    }

    public String secToTime(long time) {
        this.formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = this.formatter.format(Long.valueOf(time * 1000));
        return hms;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ArrayList<ArrayList<String>> getRecordData(List<TagInfo> infos) {
        ArrayList<ArrayList<String>> recordList = new ArrayList<>();
        for (int i = 0; i < infos.size(); i++) {
            ArrayList<String> beanList = new ArrayList<>();
            TagInfo info = infos.get(i);
            beanList.add(info.getIndex() + "");
            beanList.add(info.getType());
            beanList.add(info.getEpc() != null ? info.getEpc() : "");
            beanList.add(info.getTid() != null ? info.getTid() : "");
            beanList.add(info.getUserData() != null ? info.getUserData() : "");
            beanList.add(info.getReservedData() != null ? info.getReservedData() : "");
            beanList.add(info.getCount() + "");
            recordList.add(beanList);
        }
        return recordList;
    }

    public void notifySystemToScan(String filePath) {
        Intent intent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File file = new File(filePath);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        this.mainActivity.sendBroadcast(intent);
    }

    @OnClick({R.id.button_excel})
    public void fab_excel() {
        if (!this.isReader) {
            List<PermissionItem> permissonItems = new ArrayList<>();
            permissonItems.add(new PermissionItem("android.permission.WRITE_EXTERNAL_STORAGE", this.mainActivity.getResources().getString(R.string.store), R.drawable.permission_ic_storage));
            HiPermission.create(this.mainActivity).title(this.mainActivity.getResources().getString(R.string.export_excel_need_permission)).permissions(permissonItems).checkMutiPermission(new PermissionCallback() { // from class: com.pda.uhf_g.ui.fragment.InventoryFragment.4
                @Override // me.weyye.hipermission.PermissionCallback
                public void onClose() {
                    Log.e("onClose", "onClose");
                }

                @Override // me.weyye.hipermission.PermissionCallback
                public void onFinish() {
                    Log.e("onFinish", "onFinish");
                    String filePath = Environment.getExternalStorageDirectory() + "/Download/";
                    String fileName = "Tag_" + InventoryFragment.this.dateFormat.format(new Date()) + ".xls";
                    String[] title = {"Index", "Type", "EPC", "TID", "UserData", "ReservedData", "TotalCount"};
                    if (InventoryFragment.this.tagInfoList.size() > 0) {
                        try {
                            ExcelUtil.initExcel(filePath, fileName, title);
                            InventoryFragment inventoryFragment = InventoryFragment.this;
                            ExcelUtil.writeObjListToExcel(inventoryFragment.getRecordData(inventoryFragment.tagInfoList), filePath + fileName, this);
                            InventoryFragment.this.showToast("Export success Path=" + filePath + fileName);
                            InventoryFragment.this.notifySystemToScan(filePath + fileName);
                            return;
                        } catch (Exception e) {
                            InventoryFragment.this.showToast("Export Failed");
                            return;
                        }
                    }
                    InventoryFragment.this.showToast("No Data");
                }

                @Override // me.weyye.hipermission.PermissionCallback
                public void onDeny(String permission, int position) {
                    Log.e("onDeny", "onDeny");
                }

                @Override // me.weyye.hipermission.PermissionCallback
                public void onGuarantee(String permission, int position) {
                    Log.e("onGuarantee", "onGuarantee");
                }
            });
        }
    }

    @Override // com.pda.uhf_g.ui.base.BaseFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventory, container, false);
        ButterKnife.bind(this, view);
        initView();
        LogUtil.e("onCreateView()");
        this.sharedUtil = new SharedUtil(this.mainActivity);
        UtilSound.initSoundPool(this.mainActivity);
        return view;
    }

    private void initView() {
        EPCListViewAdapter ePCListViewAdapter = new EPCListViewAdapter(this.mainActivity, this.tagInfoList);
        this.epcListViewAdapter = ePCListViewAdapter;
        this.listViewEPC.setAdapter((ListAdapter) ePCListViewAdapter);
        this.checkBoxMultiTag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.pda.uhf_g.ui.fragment.-$$Lambda$InventoryFragment$TlT8sdhwtatW6mGlpDtShU3LZMQ
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                InventoryFragment.this.lambda$initView$0$InventoryFragment(compoundButton, z);
            }
        });
        this.checkBoxTid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.pda.uhf_g.ui.fragment.-$$Lambda$InventoryFragment$hpl_fbk7e2pQvaeTi2XbuWZH10E
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                InventoryFragment.this.lambda$initView$1$InventoryFragment(compoundButton, z);
            }
        });
    }

    public /* synthetic */ void lambda$initView$0$InventoryFragment(CompoundButton buttonView, boolean isChecked) {
        this.isMulti = isChecked;
        if (isChecked) {
            this.checkBoxTid.setChecked(false);
        }
    }

    public /* synthetic */ void lambda$initView$1$InventoryFragment(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            this.checkBoxMultiTag.setChecked(false);
        }
        clear();
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
                    InventoryFragment.this.invenroty();
                }
            }
        }
    }

    private void soundTask() {
        Runnable runnable = new Runnable() { // from class: com.pda.uhf_g.ui.fragment.InventoryFragment.6
            @Override // java.lang.Runnable
            public void run() {
                LogUtil.e("rateValue = " + InventoryFragment.this.speed);
                if (InventoryFragment.this.speed != 0) {
                    UtilSound.play(1, 0);
                }
                InventoryFragment.this.soundHandler.postDelayed(this, 40L);
            }
        };
        this.soundTask = runnable;
        this.soundHandler.postDelayed(runnable, 0L);
    }
}
