package com.pda.uhf_g.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import com.handheld.uhfr.UHFRManager;
import com.pda.uhf_g.MainActivity;
import com.pda.uhf_g.R;
import com.pda.uhf_g.ui.base.BaseFragment;
import com.pda.uhf_g.util.LogUtil;
import com.pda.uhf_g.util.SharedUtil;
import com.uhf.api.cls.Reader;

/**
 *
 */
public class SettingsFragment extends BaseFragment {

    @BindView(R.id.spinner_work_freq)
    Spinner spinnerWorkFreq;
    @BindView(R.id.spinner_power)
    Spinner spinnerPower;
    @BindView(R.id.spinner_session)
    Spinner spinnerSession;
    @BindView(R.id.spinner_q_value)
    Spinner spinnerQvalue;
    @BindView(R.id.spinner_inventory_type)
    Spinner spinnerInventoryType;
    @BindView(R.id.button_query_work_freq)
    Button buttonFreqQuery;
    @BindView(R.id.button_set_work_freq)
    Button buttonFreqSet;
    @BindView(R.id.editText_temp)
    EditText editTextTemp;
    @BindView(R.id.button_query_power)
    Button buttonQueryPower;
    @BindView(R.id.button_set_power)
    Button buttonSetPower;
    @BindView(R.id.button_query_inventory_type)
    Button buttonQueryInventory;
    @BindView(R.id.button_set_inventory_type)
    Button buttonSetInventory;
    @BindView(R.id.button_query_session)
    Button buttonQuerySession;
    @BindView(R.id.button_set_session)
    Button buttonSetSession;

    @BindView(R.id.checkbox_fastid)
    CheckBox checkBoxFastid;

    //    private SharedPreferences mSharedPreferences;
    private String[] arrayWorkFreq;
    //Session
    private String[] arraySession;
    private String[] arrayPower;
    private String[] arrayQvalue;
    private String[] arrayInventoryType;


    private Reader.Region_Conf workFreq;    //
    private int power = 33; //
    private int session = 1; //session
    private int qvalue = 1;//Q
    private int target = 0; //A|B
    private UHFRManager uhfrManager;
    private MainActivity mainActivity;

    private SharedUtil sharedUtil;
    Reader.READER_ERR err;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @OnClick(R.id.button_query_work_freq)
    void queryFreq() {
        if (!mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        String workFreqStr = "";
        Reader.Region_Conf region = mainActivity.mUhfrManager.getRegion();
//        if(region.value()==null)
//        LogUtil.e("workFraq = " + region.value());
        if(region==null){
            showToast(R.string.query_region_fail);

            return;
        }
        if (region == Reader.Region_Conf.RG_NA) {
            //902_928
            spinnerWorkFreq.setSelection(1);
            workFreqStr = arrayWorkFreq[1];
        } else if (region == Reader.Region_Conf.RG_PRC) {
            //_920_925
            spinnerWorkFreq.setSelection(0);
            workFreqStr = arrayWorkFreq[0];
        } else if (region == Reader.Region_Conf.RG_EU3) {
            //865_867
            spinnerWorkFreq.setSelection(2);
            workFreqStr = arrayWorkFreq[2];
        }

        showToast(mainActivity.getResources().getString(R.string.work_freq) + workFreqStr);

    }


    @OnClick(R.id.button_query_power)
    void queryPower() {
        if (!mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        int[] powerArray = mainActivity.mUhfrManager.getPower();
        if (powerArray != null && powerArray.length > 0) {
            LogUtil.e("powerArray = " + powerArray[0]);
            spinnerPower.setSelection(powerArray[0]);
            showToast(mainActivity.getResources().getString(R.string.power) + powerArray[0] + "dB");
        } else {
            showToast(R.string.query_fail);
        }
    }


    @OnClick(R.id.button_query_session)
    void querySession() {
        if (!mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        int session = mainActivity.mUhfrManager.getGen2session();
        if (session != -1) {
            spinnerSession.setSelection(session);
            showToast("Session" + session);
        } else {
            showToast(R.string.query_fail);
        }
        LogUtil.e("session = " + session);

    }


    @OnClick(R.id.button_query_qvalue)
    void queryQvalue() {
        if (!mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        int qvalue = mainActivity.mUhfrManager.getQvalue();
        if (qvalue != -1) {
            spinnerQvalue.setSelection((qvalue ));
            showToast("Q = " + qvalue);
        } else {
            showToast(R.string.query_fail);
        }
        LogUtil.e("qvalue = " + qvalue);
    }


    @OnClick(R.id.button_query_temp)
    void queryTemp() {
//        int temp = mainActivity.mUhfrManager.getTemperature();
//        LogUtil.e("temp = " + temp) ;
//        uhfrManager.get
    }


    @OnClick(R.id.button_query_inventory_type)
    void queryInventory() {
        if (!mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        target = mainActivity.mUhfrManager.getTarget();
        LogUtil.e("Target = " + target);
        if (target != -1) {
            spinnerInventoryType.setSelection(target);
            showToast(mainActivity.getResources().getString(R.string.inventory_type) + arrayInventoryType[target]);
        } else {
            showToast(R.string.query_fail);
        }

    }



    @OnClick(R.id.button_set_power)
    void setPower() {
        if (!mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        err = mainActivity.mUhfrManager.setPower(power, power);
        if (err == Reader.READER_ERR.MT_OK_ERR) {
            showToast(R.string.set_success);
            sharedUtil.savePower(power);
        } else {
            //5101 仅支持30db
            showToast(R.string.set_fail);
        }
    }


    @OnClick(R.id.button_set_work_freq)
    void setWorkFreq() {
        if (!mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        Log.e("zeng-","setworkFraq:"+workFreq);
        err = mainActivity.mUhfrManager.setRegion(workFreq);
        if (err == Reader.READER_ERR.MT_OK_ERR) {
            showToast(R.string.set_success);
            sharedUtil.saveWorkFreq(workFreq.value());
        } else {
            //5101 仅支持30db
            showToast(R.string.set_fail);
        }
    }


    @OnClick(R.id.button_set_session)
    void setSession() {
        if (!mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        boolean flag = mainActivity.mUhfrManager.setGen2session(session);
        if (flag) {
            showToast(R.string.set_success);
            sharedUtil.saveSession(session);
        } else {
            showToast(R.string.set_fail);
        }
    }



    @OnClick(R.id.button_set_qvalue)
    void setQvalue() {
        if (!mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        boolean flag = mainActivity.mUhfrManager.setQvaule(qvalue);
        if (flag) {
            showToast(R.string.set_success);
            sharedUtil.saveQvalue(qvalue);
        } else {
            showToast(R.string.set_fail);
        }
    }



    @OnClick(R.id.button_set_inventory_type)
    void setTarget() {
        if (!mainActivity.isConnectUHF) {
            showToast(R.string.communication_timeout);
            return;
        }
        boolean flag = mainActivity.mUhfrManager.setTarget(target);
        Log.e("zeng -", "setTarget:" + target);
        if (flag) {
            showToast(R.string.set_success);
            sharedUtil.saveTarget(target);
        } else {
            showToast(R.string.set_fail);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = (MainActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
//        mainActivity.mUhfrManager = UHFRManager.getInstance();
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
        checkBoxFastid.setChecked(sharedUtil.getFastId());
    }

    private void initView() {
        arrayWorkFreq = mainActivity.getResources().getStringArray(R.array.work_freq);
        arraySession = mainActivity.getResources().getStringArray(R.array.session_arrays);
        arrayPower = mainActivity.getResources().getStringArray(R.array.power_arrays);
        arrayQvalue = mainActivity.getResources().getStringArray(R.array.q_value_arrays);
        arrayInventoryType = mainActivity.getResources().getStringArray(R.array.inventory_type_arrays);

        sharedUtil = new SharedUtil(mainActivity);
        //
        spinnerPower.setSelection(sharedUtil.getPower());
        int freq = sharedUtil.getWorkFreq();
//        Log.e("zeng-","freq:"+freq);
        if (Reader.Region_Conf.valueOf(freq) == Reader.Region_Conf.RG_NA) {
            spinnerWorkFreq.setSelection(2);
        } else if (Reader.Region_Conf.valueOf(freq) == Reader.Region_Conf.RG_PRC) {
            spinnerWorkFreq.setSelection(0);
        } else if (Reader.Region_Conf.valueOf(freq) == Reader.Region_Conf.RG_EU3) {
            spinnerWorkFreq.setSelection(3);
        }
        spinnerSession.setSelection(sharedUtil.getSession());
        spinnerQvalue.setSelection((sharedUtil.getQvalue()));
        spinnerInventoryType.setSelection(sharedUtil.getTarget());


        //
        spinnerWorkFreq.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String workFreqStr = arrayWorkFreq[position];
                switch (position) {
                    case 0:
                        //1_920_925
                        workFreq = Reader.Region_Conf.RG_PRC;
                        break;
                    case 1:
                        //_902_928
                        workFreq = Reader.Region_Conf.RG_NA;
                        break;
                    case 2:
                        //865_867
                        workFreq = Reader.Region_Conf.RG_EU3;
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //
        spinnerPower.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                power = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //session
        spinnerSession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                session = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Q
        spinnerQvalue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                qvalue = position ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //
        spinnerInventoryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                target = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //fastid
        checkBoxFastid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mainActivity.mUhfrManager.setFastID(b);
                sharedUtil.saveFastId(b);
            }
        });

    }
}