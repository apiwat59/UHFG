package com.pda.uhf_g.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.handheld.uhfr.UHFRManager;
import com.pda.uhf_g.MainActivity;
import com.pda.uhf_g.R;
import com.pda.uhf_g.ui.base.BaseFragment;
import com.pda.uhf_g.util.LogUtil;
import com.pda.uhf_g.util.SharedUtil;
import com.uhf.api.cls.Reader;

/* loaded from: classes.dex */
public class SettingsFragment extends BaseFragment {
    private String[] arrayInventoryType;
    private String[] arrayPower;
    private String[] arrayQvalue;
    private String[] arraySession;
    private String[] arrayWorkFreq;

    @BindView(R.id.button_query_work_freq)
    Button buttonFreqQuery;

    @BindView(R.id.button_set_work_freq)
    Button buttonFreqSet;

    @BindView(R.id.button_query_inventory_type)
    Button buttonQueryInventory;

    @BindView(R.id.button_query_power)
    Button buttonQueryPower;

    @BindView(R.id.button_query_session)
    Button buttonQuerySession;

    @BindView(R.id.button_set_inventory_type)
    Button buttonSetInventory;

    @BindView(R.id.button_set_power)
    Button buttonSetPower;

    @BindView(R.id.button_set_session)
    Button buttonSetSession;

    @BindView(R.id.checkbox_fastid)
    CheckBox checkBoxFastid;

    @BindView(R.id.editText_temp)
    EditText editTextTemp;
    Reader.READER_ERR err;

    @BindView(R.id.dwell_ll)
    LinearLayout llDwell;

    @BindView(R.id.jgTime_ll)
    LinearLayout llJgTime;

    @BindView(R.id.radio_ll)
    LinearLayout llRadio;

    @BindView(R.id.radio_set_ll)
    LinearLayout llRadioSet;
    private MainActivity mainActivity;

    @BindView(R.id.rb_perf)
    RadioButton rb1;

    @BindView(R.id.rb_bal)
    RadioButton rb2;

    @BindView(R.id.rb_ene)
    RadioButton rb3;

    @BindView(R.id.rb_cus)
    RadioButton rb4;

    @BindView(R.id.ivt_read)
    Button read;

    @BindView(R.id.ivt_setting)
    Button set;
    private SharedUtil sharedUtil;

    @BindView(R.id.dwell_spinner)
    Spinner spDwell;

    @BindView(R.id.jgTime_spinner)
    Spinner spJgTime;

    @BindView(R.id.spinner_inventory_type)
    Spinner spinnerInventoryType;

    @BindView(R.id.spinner_power)
    Spinner spinnerPower;

    @BindView(R.id.spinner_q_value)
    Spinner spinnerQvalue;

    @BindView(R.id.spinner_session)
    Spinner spinnerSession;

    @BindView(R.id.spinner_work_freq)
    Spinner spinnerWorkFreq;
    private UHFRManager uhfrManager;
    private Reader.Region_Conf workFreq;
    private int checkedRadio = 3;
    private int power = 33;
    private int session = 1;
    private int qvalue = 1;
    private int target = 0;

    @OnClick({R.id.button_query_work_freq})
    void queryFreq() {
        if (!this.mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        String workFreqStr = "";
        Reader.Region_Conf region = this.mainActivity.mUhfrManager.getRegion();
        if (region == null) {
            showToast(R.string.query_region_fail);
            return;
        }
        if (region == Reader.Region_Conf.RG_NA) {
            this.spinnerWorkFreq.setSelection(1);
            workFreqStr = this.arrayWorkFreq[1];
        } else if (region == Reader.Region_Conf.RG_PRC) {
            this.spinnerWorkFreq.setSelection(0);
            workFreqStr = this.arrayWorkFreq[0];
        } else if (region == Reader.Region_Conf.RG_EU3) {
            this.spinnerWorkFreq.setSelection(2);
            workFreqStr = this.arrayWorkFreq[2];
        } else if (region == Reader.Region_Conf.RG_OPEN) {
            this.spinnerWorkFreq.setSelection(3);
            workFreqStr = this.arrayWorkFreq[3];
        }
        showToast(this.mainActivity.getResources().getString(R.string.work_freq) + workFreqStr);
    }

    @OnClick({R.id.button_query_power})
    void queryPower() {
        if (!this.mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        int[] powerArray = this.mainActivity.mUhfrManager.getPower();
        if (powerArray != null && powerArray.length > 0) {
            LogUtil.e("powerArray = " + powerArray[0]);
            this.spinnerPower.setSelection(powerArray[0]);
            showToast(this.mainActivity.getResources().getString(R.string.power) + powerArray[0] + "dB");
            return;
        }
        showToast(R.string.query_fail);
    }

    @OnClick({R.id.button_query_session})
    void querySession() {
        if (!this.mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        int session = this.mainActivity.mUhfrManager.getGen2session();
        if (session != -1) {
            this.spinnerSession.setSelection(session);
            showToast("Session" + session);
        } else {
            showToast(R.string.query_fail);
        }
        LogUtil.e("session = " + session);
    }

    @OnClick({R.id.button_query_qvalue})
    void queryQvalue() {
        if (!this.mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        int qvalue = this.mainActivity.mUhfrManager.getQvalue();
        if (qvalue != -1) {
            this.spinnerQvalue.setSelection(qvalue);
            showToast("Q = " + qvalue);
        } else {
            showToast(R.string.query_fail);
        }
        LogUtil.e("qvalue = " + qvalue);
    }

    @OnClick({R.id.button_query_temp})
    void queryTemp() {
    }

    @OnClick({R.id.button_query_inventory_type})
    void queryInventory() {
        if (!this.mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        this.target = this.mainActivity.mUhfrManager.getTarget();
        LogUtil.e("Target = " + this.target);
        int i = this.target;
        if (i != -1) {
            this.spinnerInventoryType.setSelection(i);
            showToast(this.mainActivity.getResources().getString(R.string.inventory_type) + this.arrayInventoryType[this.target]);
            return;
        }
        showToast(R.string.query_fail);
    }

    @OnClick({R.id.button_set_power})
    void setPower() {
        if (!this.mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        UHFRManager uHFRManager = this.mainActivity.mUhfrManager;
        int i = this.power;
        Reader.READER_ERR power = uHFRManager.setPower(i, i);
        this.err = power;
        if (power == Reader.READER_ERR.MT_OK_ERR) {
            showToast(R.string.set_success);
            this.sharedUtil.savePower(this.power);
        } else {
            showToast(R.string.set_fail);
        }
    }

    @OnClick({R.id.button_set_work_freq})
    void setWorkFreq() {
        if (!this.mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        Log.e("zeng-", "setworkFraq:" + this.workFreq);
        Reader.READER_ERR region = this.mainActivity.mUhfrManager.setRegion(this.workFreq);
        this.err = region;
        if (region == Reader.READER_ERR.MT_OK_ERR) {
            showToast(R.string.set_success);
            this.sharedUtil.saveWorkFreq(this.workFreq.value());
        } else {
            showToast(R.string.set_fail);
        }
    }

    @OnClick({R.id.button_set_session})
    void setSession() {
        if (!this.mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        boolean flag = this.mainActivity.mUhfrManager.setGen2session(this.session);
        if (flag) {
            showToast(R.string.set_success);
            this.sharedUtil.saveSession(this.session);
        } else {
            showToast(R.string.set_fail);
        }
    }

    @OnClick({R.id.button_set_qvalue})
    void setQvalue() {
        if (!this.mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        boolean flag = this.mainActivity.mUhfrManager.setQvaule(this.qvalue);
        if (flag) {
            showToast(R.string.set_success);
            this.sharedUtil.saveQvalue(this.qvalue);
        } else {
            showToast(R.string.set_fail);
        }
    }

    @OnClick({R.id.button_set_inventory_type})
    void setTarget() {
        if (!this.mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        boolean flag = this.mainActivity.mUhfrManager.setTarget(this.target);
        Log.e("zeng -", "setTarget:" + this.target);
        if (flag) {
            showToast(R.string.set_success);
            this.sharedUtil.saveTarget(this.target);
        } else {
            showToast(R.string.set_fail);
        }
    }

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mainActivity = (MainActivity) getActivity();
    }

    @Override // com.pda.uhf_g.ui.base.BaseFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        initView();
        Click();
        return view;
    }

    void Click() {
        queryFreq();
        queryPower();
        querySession();
        queryQvalue();
        queryInventory();
        this.checkBoxFastid.setChecked(this.sharedUtil.getFastId());
    }

    private void initView() {
        this.arrayWorkFreq = this.mainActivity.getResources().getStringArray(R.array.work_freq);
        this.arraySession = this.mainActivity.getResources().getStringArray(R.array.session_arrays);
        this.arrayPower = this.mainActivity.getResources().getStringArray(R.array.power_arrays);
        this.arrayQvalue = this.mainActivity.getResources().getStringArray(R.array.q_value_arrays);
        this.arrayInventoryType = this.mainActivity.getResources().getStringArray(R.array.inventory_type_arrays);
        SharedUtil sharedUtil = new SharedUtil(this.mainActivity);
        this.sharedUtil = sharedUtil;
        this.spinnerPower.setSelection(sharedUtil.getPower());
        int freq = this.sharedUtil.getWorkFreq();
        if (Reader.Region_Conf.valueOf(freq) == Reader.Region_Conf.RG_NA) {
            this.spinnerWorkFreq.setSelection(1);
        } else if (Reader.Region_Conf.valueOf(freq) == Reader.Region_Conf.RG_PRC) {
            this.spinnerWorkFreq.setSelection(0);
        } else if (Reader.Region_Conf.valueOf(freq) == Reader.Region_Conf.RG_EU3) {
            this.spinnerWorkFreq.setSelection(2);
        } else if (Reader.Region_Conf.valueOf(freq) == Reader.Region_Conf.RG_OPEN) {
            this.spinnerWorkFreq.setSelection(3);
        }
        this.spinnerSession.setSelection(this.sharedUtil.getSession());
        this.spinnerQvalue.setSelection(this.sharedUtil.getQvalue());
        this.spinnerInventoryType.setSelection(this.sharedUtil.getTarget());
        this.spinnerWorkFreq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    SettingsFragment.this.workFreq = Reader.Region_Conf.RG_PRC;
                    return;
                }
                if (position == 1) {
                    SettingsFragment.this.workFreq = Reader.Region_Conf.RG_NA;
                } else if (position == 2) {
                    SettingsFragment.this.workFreq = Reader.Region_Conf.RG_EU3;
                } else if (position == 3) {
                    SettingsFragment.this.workFreq = Reader.Region_Conf.RG_OPEN;
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        this.spinnerPower.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment.2
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SettingsFragment.this.power = position;
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        this.spinnerSession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment.3
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                SettingsFragment.this.session = position;
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.spinnerQvalue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment.4
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                SettingsFragment.this.qvalue = position;
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.spinnerInventoryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment.5
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                SettingsFragment.this.target = position;
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        this.checkBoxFastid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.pda.uhf_g.ui.fragment.SettingsFragment.6
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SettingsFragment.this.mainActivity.mUhfrManager.setFastID(b);
                SettingsFragment.this.sharedUtil.saveFastId(b);
            }
        });
        initRrAdvanceSettings();
    }

    private void initRrAdvanceSettings() {
        boolean b = this.mainActivity.mSharedPreferences.getBoolean("show_rr_advance_settings", false);
        if (!b) {
            this.llJgTime.setVisibility(8);
            this.llDwell.setVisibility(8);
            this.llRadioSet.setVisibility(8);
            this.llRadio.setVisibility(8);
            return;
        }
        this.llJgTime.setVisibility(0);
        this.llDwell.setVisibility(0);
        this.llRadioSet.setVisibility(0);
        this.llRadio.setVisibility(0);
        initRrAdvanceView();
        initRrAdvanceRadio();
        initRrAdvanceListener();
    }

    private void initRrAdvanceView() {
        String[] strjtTime = new String[7];
        for (int index = 0; index < 7; index++) {
            strjtTime[index] = (index * 10) + "ms";
        }
        ArrayAdapter<String> spada_jgTime = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, strjtTime);
        spada_jgTime.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spJgTime.setAdapter((SpinnerAdapter) spada_jgTime);
        String[] dwelltime = new String[254];
        for (int index2 = 2; index2 < 256; index2++) {
            dwelltime[index2 - 2] = (index2 * 100) + "ms";
        }
        ArrayAdapter<String> spada_dwell = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, dwelltime);
        spada_dwell.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.spDwell.setAdapter((SpinnerAdapter) spada_dwell);
    }

    private void initRrAdvanceListener() {
        this.rb1.setOnClickListener(new View.OnClickListener() { // from class: com.pda.uhf_g.ui.fragment.-$$Lambda$SettingsFragment$Th-FaJrzGO2bIdlWSQgiUCEXbCM
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SettingsFragment.this.lambda$initRrAdvanceListener$0$SettingsFragment(view);
            }
        });
        this.rb2.setOnClickListener(new View.OnClickListener() { // from class: com.pda.uhf_g.ui.fragment.-$$Lambda$SettingsFragment$cq01XD9DJmv8ccUXLgY317Wwbe0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SettingsFragment.this.lambda$initRrAdvanceListener$1$SettingsFragment(view);
            }
        });
        this.rb3.setOnClickListener(new View.OnClickListener() { // from class: com.pda.uhf_g.ui.fragment.-$$Lambda$SettingsFragment$7jyhCj9QbjAbhf2SvZBGf7qYdc8
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SettingsFragment.this.lambda$initRrAdvanceListener$2$SettingsFragment(view);
            }
        });
        this.rb4.setOnClickListener(new View.OnClickListener() { // from class: com.pda.uhf_g.ui.fragment.-$$Lambda$SettingsFragment$aou7sNbZxGs-FBvCcu1PuskKU6A
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SettingsFragment.this.lambda$initRrAdvanceListener$3$SettingsFragment(view);
            }
        });
        this.read.setOnClickListener(new View.OnClickListener() { // from class: com.pda.uhf_g.ui.fragment.-$$Lambda$SettingsFragment$9V7CxSkKWNS0avs6A_aBMaRtRkk
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SettingsFragment.this.lambda$initRrAdvanceListener$4$SettingsFragment(view);
            }
        });
        this.set.setOnClickListener(new View.OnClickListener() { // from class: com.pda.uhf_g.ui.fragment.-$$Lambda$SettingsFragment$wfkBv6ZZ1R9rVnySSQWRU1UHIrw
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SettingsFragment.this.lambda$initRrAdvanceListener$5$SettingsFragment(view);
            }
        });
    }

    public /* synthetic */ void lambda$initRrAdvanceListener$0$SettingsFragment(View v) {
        this.spJgTime.setEnabled(false);
        this.spDwell.setEnabled(false);
        this.spJgTime.setSelection(0, false);
        this.spDwell.setSelection(8, false);
        this.checkedRadio = 1;
    }

    public /* synthetic */ void lambda$initRrAdvanceListener$1$SettingsFragment(View v) {
        this.spJgTime.setEnabled(false);
        this.spDwell.setEnabled(false);
        this.spJgTime.setSelection(3, false);
        this.spDwell.setSelection(0, false);
        this.checkedRadio = 2;
    }

    public /* synthetic */ void lambda$initRrAdvanceListener$2$SettingsFragment(View v) {
        this.spJgTime.setEnabled(false);
        this.spDwell.setEnabled(false);
        this.spJgTime.setSelection(6, false);
        this.spDwell.setSelection(0, false);
        this.checkedRadio = 3;
    }

    public /* synthetic */ void lambda$initRrAdvanceListener$3$SettingsFragment(View v) {
        this.spJgTime.setEnabled(true);
        this.spDwell.setEnabled(true);
        this.checkedRadio = 4;
    }

    public /* synthetic */ void lambda$initRrAdvanceListener$4$SettingsFragment(View v) {
        int[] rrJgDwell = this.mainActivity.mUhfrManager.getRrJgDwell();
        if (rrJgDwell[0] != -1 && rrJgDwell[1] != -1) {
            this.spJgTime.setSelection(rrJgDwell[0], true);
            this.spDwell.setSelection(rrJgDwell[1] - 2, true);
            int i = this.mainActivity.mSharedPreferences.getInt("checked_radio", 3);
            this.checkedRadio = i;
            if (i == 1) {
                this.rb1.setChecked(true);
                this.spJgTime.setEnabled(false);
                this.spDwell.setEnabled(false);
            } else if (i == 2) {
                this.rb2.setChecked(true);
                this.spJgTime.setEnabled(false);
                this.spDwell.setEnabled(false);
            } else if (i == 3) {
                this.rb3.setChecked(true);
                this.spJgTime.setEnabled(false);
                this.spDwell.setEnabled(false);
            } else {
                this.spJgTime.setEnabled(true);
                this.spDwell.setEnabled(true);
                this.rb4.setChecked(true);
            }
            showToast(getString(R.string.query_success));
            return;
        }
        showToast(getString(R.string.query_fail));
    }

    public /* synthetic */ void lambda$initRrAdvanceListener$5$SettingsFragment(View v) {
        int jgTimes = this.spJgTime.getSelectedItemPosition();
        int dwell = this.spDwell.getSelectedItemPosition();
        int i = this.mainActivity.mUhfrManager.setRrJgDwell(jgTimes, dwell + 2);
        if (i == 0) {
            this.mainActivity.mSharedPreferences.edit().putInt("checked_radio", this.checkedRadio).apply();
            this.mainActivity.mSharedPreferences.edit().putInt("jg_time", jgTimes).apply();
            this.mainActivity.mSharedPreferences.edit().putInt("dwell", dwell + 2).apply();
            showToast(getString(R.string.set_success));
            return;
        }
        showToast(getString(R.string.set_fail));
    }

    private void initRrAdvanceRadio() {
        this.checkedRadio = this.mainActivity.mSharedPreferences.getInt("checked_radio", 3);
        int jgTime = this.mainActivity.mSharedPreferences.getInt("jg_time", 6);
        int dwell = this.mainActivity.mSharedPreferences.getInt("dwell", 2);
        int i = this.checkedRadio;
        if (i == 1) {
            this.rb1.setChecked(true);
            this.spJgTime.setEnabled(false);
            this.spDwell.setEnabled(false);
        } else if (i == 2) {
            this.rb2.setChecked(true);
            this.spJgTime.setEnabled(false);
            this.spDwell.setEnabled(false);
        } else if (i == 3) {
            this.rb3.setChecked(true);
            this.spJgTime.setEnabled(false);
            this.spDwell.setEnabled(false);
        } else {
            this.spJgTime.setEnabled(true);
            this.spDwell.setEnabled(true);
            this.rb4.setChecked(true);
        }
        this.spJgTime.setSelection(jgTime, true);
        this.spDwell.setSelection(dwell - 2, true);
    }
}
