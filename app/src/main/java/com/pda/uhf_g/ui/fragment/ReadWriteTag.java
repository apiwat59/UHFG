package com.pda.uhf_g.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pda.serialport.Tools;
import com.handheld.uhfr.UHFRManager;
import com.pda.uhf_g.MainActivity;
import com.pda.uhf_g.R;
import com.pda.uhf_g.ui.base.BaseFragment;
import com.pda.uhf_g.util.LogUtil;
import com.uhf.api.cls.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public class ReadWriteTag extends BaseFragment {
    private byte[] accessPassword;

    @BindView(R.id.button_clean)
    Button buttonClean;

    @BindView(R.id.button_kill)
    Button buttonKill;

    @BindView(R.id.button_lock)
    Button buttonLock;

    @BindView(R.id.button_modify)
    Button buttonModify;

    @BindView(R.id.button_read)
    Button buttonRead;

    @BindView(R.id.button_write)
    Button buttonWrite;

    @BindView(R.id.checkbox_filter)
    CheckBox checkBoxFilter;

    @BindView(R.id.editText_access_password)
    EditText editTextAccessPassword;

    @BindView(R.id.editText_kill_password)
    EditText editTextKillPassword;

    @BindView(R.id.editText_len)
    EditText editTextLen;

    @BindView(R.id.editText_lock_password)
    EditText editTextLockPassword;

    @BindView(R.id.editText_new_epc)
    EditText editTextNewEPC;

    @BindView(R.id.editText_read_data)
    EditText editTextReadData;

    @BindView(R.id.editText_start_addr)
    EditText editTextStartAddr;

    @BindView(R.id.editText_write_data)
    EditText editTextWriteData;
    private byte[] killPassword;
    private int len;
    int lockTypeInt;
    private UHFRManager mUhfrManager;
    private MainActivity mainActivity;

    @BindView(R.id.radio_membank)
    RadioGroup radioGroupMembank;

    @BindView(R.id.spinner_epc)
    Spinner spinnerEPC;

    @BindView(R.id.sipnner_lock_data)
    Spinner spinnerLockData;

    @BindView(R.id.sipnner_lock_type)
    Spinner spinnerLockType;
    private int startAddr;
    private String epcStr = null;
    private int membank = 3;
    private boolean isEPCNULL = true;
    private final int UNLOCK = 0;
    private final int LOCK = 1;
    private final int PERM_LOCK = 2;
    Reader.Lock_Obj lock_obj = null;
    Reader.Lock_Type lock_type = null;

    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mainActivity = (MainActivity) getActivity();
    }

    @Override // com.pda.uhf_g.ui.base.BaseFragment, androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_write_tag, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        if (this.mainActivity.listEPC != null && this.mainActivity.listEPC.size() > 0) {
            Spinner spinner = this.spinnerEPC;
            MainActivity mainActivity = this.mainActivity;
            spinner.setAdapter((SpinnerAdapter) new ArrayAdapter(mainActivity, android.R.layout.simple_spinner_dropdown_item, mainActivity.listEPC));
            this.isEPCNULL = false;
        } else {
            this.epcStr = null;
            this.isEPCNULL = true;
        }
        this.spinnerEPC.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.pda.uhf_g.ui.fragment.ReadWriteTag.1
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ReadWriteTag readWriteTag = ReadWriteTag.this;
                readWriteTag.epcStr = readWriteTag.mainActivity.listEPC.get(position);
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
                ReadWriteTag readWriteTag = ReadWriteTag.this;
                readWriteTag.epcStr = readWriteTag.mainActivity.listEPC.get(0);
            }
        });
        this.radioGroupMembank.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { // from class: com.pda.uhf_g.ui.fragment.ReadWriteTag.2
            @Override // android.widget.RadioGroup.OnCheckedChangeListener
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_epc /* 2131296659 */:
                        ReadWriteTag.this.membank = 1;
                        ReadWriteTag.this.editTextStartAddr.setText("2");
                        break;
                    case R.id.radioButton_password /* 2131296660 */:
                        ReadWriteTag.this.membank = 0;
                        ReadWriteTag.this.editTextStartAddr.setText("0");
                        break;
                    case R.id.radioButton_tid /* 2131296661 */:
                        ReadWriteTag.this.membank = 2;
                        ReadWriteTag.this.editTextStartAddr.setText("0");
                        break;
                    case R.id.radioButton_user /* 2131296662 */:
                        ReadWriteTag.this.membank = 3;
                        ReadWriteTag.this.editTextStartAddr.setText("0");
                        break;
                }
            }
        });
        this.spinnerLockData.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.pda.uhf_g.ui.fragment.ReadWriteTag.3
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ReadWriteTag.this.lock_obj = Reader.Lock_Obj.LOCK_OBJECT_ACCESS_PASSWD;
                    return;
                }
                if (position == 1) {
                    ReadWriteTag.this.lock_obj = Reader.Lock_Obj.LOCK_OBJECT_KILL_PASSWORD;
                    return;
                }
                if (position == 2) {
                    ReadWriteTag.this.lock_obj = Reader.Lock_Obj.LOCK_OBJECT_BANK1;
                } else if (position == 3) {
                    ReadWriteTag.this.lock_obj = Reader.Lock_Obj.LOCK_OBJECT_BANK2;
                } else if (position == 4) {
                    ReadWriteTag.this.lock_obj = Reader.Lock_Obj.LOCK_OBJECT_BANK3;
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
                ReadWriteTag.this.lock_obj = Reader.Lock_Obj.LOCK_OBJECT_ACCESS_PASSWD;
            }
        });
        this.spinnerLockType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { // from class: com.pda.uhf_g.ui.fragment.ReadWriteTag.4
            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    ReadWriteTag.this.lockTypeInt = 0;
                } else if (position == 1) {
                    ReadWriteTag.this.lockTypeInt = 1;
                } else if (position == 2) {
                    ReadWriteTag.this.lockTypeInt = 2;
                }
            }

            @Override // android.widget.AdapterView.OnItemSelectedListener
            public void onNothingSelected(AdapterView<?> parent) {
                ReadWriteTag.this.lock_obj = Reader.Lock_Obj.LOCK_OBJECT_ACCESS_PASSWD;
            }
        });
    }

    @OnClick({R.id.button_read})
    void readData() {
        if (!checkParam(false)) {
            return;
        }
        byte[] readData = new byte[this.len * 2];
        byte[] epc = Tools.HexString2Bytes(this.epcStr);
        Reader.READER_ERR er = Reader.READER_ERR.MT_OK_ERR;
        LogUtil.e("membank = " + this.membank + ", startAddr = " + this.startAddr + ",len =  " + this.len + ", access = " + this.accessPassword);
        if (this.checkBoxFilter.isChecked()) {
            readData = this.mainActivity.mUhfrManager.getTagDataByFilter(this.membank, this.startAddr, this.len, this.accessPassword, (short) 1000, epc, 1, 2, true);
        } else {
            er = this.mainActivity.mUhfrManager.getTagData(this.membank, this.startAddr, this.len, readData, this.accessPassword, (short) 1000);
        }
        if (er == Reader.READER_ERR.MT_OK_ERR && readData != null) {
            this.editTextReadData.append("Read data:" + Tools.Bytes2HexString(readData, readData.length) + "\n");
            return;
        }
        showToast(R.string.read_fail);
    }

    @OnClick({R.id.button_write})
    void write() {
        Reader.READER_ERR er;
        if (!checkParam(true)) {
            return;
        }
        String writeDataStr = this.editTextWriteData.getText().toString().trim();
        if (writeDataStr == null || !matchHex(writeDataStr) || writeDataStr.length() % 4 != 0) {
            showToast(R.string.please_input_right_write_data);
            return;
        }
        byte[] writeDataBytes = Tools.HexString2Bytes(writeDataStr);
        byte[] epc = Tools.HexString2Bytes(this.epcStr);
        LogUtil.e("membank = " + this.membank + ", startAddr = " + this.startAddr + ", access = " + this.accessPassword);
        if (this.checkBoxFilter.isChecked()) {
            er = this.mainActivity.mUhfrManager.writeTagDataByFilter((char) this.membank, this.startAddr, writeDataBytes, writeDataBytes.length, this.accessPassword, (short) 1000, epc, 1, 2, true);
        } else {
            er = this.mainActivity.mUhfrManager.writeTagData((char) this.membank, this.startAddr, writeDataBytes, writeDataBytes.length, this.accessPassword, (short) 1000);
        }
        if (er == Reader.READER_ERR.MT_OK_ERR) {
            showToast(R.string.write_success);
        } else {
            showToast(R.string.write_fail);
        }
    }

    @OnClick({R.id.button_modify})
    void modifyEPC() {
        Reader.READER_ERR er;
        if (this.isEPCNULL) {
            showToast(R.string.please_inventory);
            return;
        }
        String newEPC = this.editTextNewEPC.getText().toString().trim();
        String accessStr = this.editTextAccessPassword.getText().toString().trim();
        if (accessStr == null || accessStr.length() == 0) {
            showToast(R.string.access_password_not_null);
            return;
        }
        if (!matchHex(accessStr) || accessStr.length() != 8) {
            showToast(R.string.please_input_right_access_password);
            return;
        }
        if (!matchHex(newEPC) || newEPC.length() % 4 != 0) {
            showToast(R.string.please_input_right_epc);
            return;
        }
        this.accessPassword = Tools.HexString2Bytes(accessStr);
        byte[] writeDataBytes = Tools.HexString2Bytes(newEPC);
        byte[] epc = Tools.HexString2Bytes(this.epcStr);
        if (this.checkBoxFilter.isChecked()) {
            er = this.mainActivity.mUhfrManager.writeTagEPCByFilter(writeDataBytes, this.accessPassword, (short) 1000, epc, 1, 2, true);
        } else {
            er = this.mainActivity.mUhfrManager.writeTagEPC(writeDataBytes, this.accessPassword, (short) 1000);
        }
        if (er == Reader.READER_ERR.MT_OK_ERR) {
            showToast(R.string.modify_success);
        } else {
            showToast(R.string.modify_fail);
        }
    }

    @OnClick({R.id.button_lock})
    void lock() {
        Reader.READER_ERR er;
        if (this.isEPCNULL) {
            showToast(R.string.please_inventory);
            return;
        }
        String accessStr = this.editTextAccessPassword.getText().toString().trim();
        if (accessStr == null || accessStr.length() == 0) {
            showToast(R.string.access_password_not_null);
            return;
        }
        if (!matchHex(accessStr) || accessStr.length() != 8) {
            showToast(R.string.please_input_right_access_password);
            return;
        }
        byte[] epc = Tools.HexString2Bytes(this.epcStr);
        this.accessPassword = Tools.HexString2Bytes(accessStr);
        getLockType();
        if (this.checkBoxFilter.isChecked()) {
            er = this.mainActivity.mUhfrManager.lockTagByFilter(this.lock_obj, this.lock_type, this.accessPassword, (short) 1000, epc, 1, 2, true);
        } else {
            er = this.mainActivity.mUhfrManager.lockTag(this.lock_obj, this.lock_type, this.accessPassword, (short) 1000);
        }
        if (er == Reader.READER_ERR.MT_OK_ERR) {
            showToast("Lock Success!");
        } else {
            showToast("Lock Fail!");
        }
    }

    private void getLockType() {
        if (this.lock_obj == Reader.Lock_Obj.LOCK_OBJECT_ACCESS_PASSWD) {
            int i = this.lockTypeInt;
            if (i == 0) {
                this.lock_type = Reader.Lock_Type.ACCESS_PASSWD_UNLOCK;
                return;
            } else if (i == 1) {
                this.lock_type = Reader.Lock_Type.ACCESS_PASSWD_LOCK;
                return;
            } else {
                if (i == 2) {
                    this.lock_type = Reader.Lock_Type.ACCESS_PASSWD_PERM_LOCK;
                    return;
                }
                return;
            }
        }
        if (this.lock_obj == Reader.Lock_Obj.LOCK_OBJECT_KILL_PASSWORD) {
            int i2 = this.lockTypeInt;
            if (i2 == 0) {
                this.lock_type = Reader.Lock_Type.KILL_PASSWORD_UNLOCK;
                return;
            } else if (i2 == 1) {
                this.lock_type = Reader.Lock_Type.KILL_PASSWORD_LOCK;
                return;
            } else {
                if (i2 == 2) {
                    this.lock_type = Reader.Lock_Type.KILL_PASSWORD_PERM_LOCK;
                    return;
                }
                return;
            }
        }
        if (this.lock_obj == Reader.Lock_Obj.LOCK_OBJECT_BANK2) {
            int i3 = this.lockTypeInt;
            if (i3 == 0) {
                this.lock_type = Reader.Lock_Type.BANK2_UNLOCK;
                return;
            } else if (i3 == 1) {
                this.lock_type = Reader.Lock_Type.BANK2_LOCK;
                return;
            } else {
                if (i3 == 2) {
                    this.lock_type = Reader.Lock_Type.BANK2_PERM_LOCK;
                    return;
                }
                return;
            }
        }
        if (this.lock_obj == Reader.Lock_Obj.LOCK_OBJECT_BANK1) {
            int i4 = this.lockTypeInt;
            if (i4 == 0) {
                this.lock_type = Reader.Lock_Type.BANK1_UNLOCK;
                return;
            } else if (i4 == 1) {
                this.lock_type = Reader.Lock_Type.BANK1_LOCK;
                return;
            } else {
                if (i4 == 2) {
                    this.lock_type = Reader.Lock_Type.BANK1_PERM_LOCK;
                    return;
                }
                return;
            }
        }
        if (this.lock_obj == Reader.Lock_Obj.LOCK_OBJECT_BANK3) {
            int i5 = this.lockTypeInt;
            if (i5 == 0) {
                this.lock_type = Reader.Lock_Type.BANK3_UNLOCK;
            } else if (i5 == 1) {
                this.lock_type = Reader.Lock_Type.BANK3_LOCK;
            } else if (i5 == 2) {
                this.lock_type = Reader.Lock_Type.BANK3_PERM_LOCK;
            }
        }
    }

    @OnClick({R.id.button_kill})
    void kill() {
        Reader.READER_ERR er;
        if (this.isEPCNULL) {
            showToast(R.string.please_inventory);
            return;
        }
        String killStr = this.editTextKillPassword.getText().toString().trim();
        if (killStr == null || killStr.length() == 0) {
            showToast(R.string.access_password_not_null);
            return;
        }
        if (!matchHex(killStr) || killStr.length() != 8) {
            showToast(R.string.please_input_right_access_password);
        }
        byte[] epc = Tools.HexString2Bytes(this.epcStr);
        this.killPassword = Tools.HexString2Bytes(killStr);
        if (this.checkBoxFilter.isChecked()) {
            er = this.mainActivity.mUhfrManager.killTagByFilter(this.killPassword, (short) 1000, epc, 1, 2, true);
        } else {
            er = this.mainActivity.mUhfrManager.killTag(this.killPassword, (short) 1000);
        }
        if (er == Reader.READER_ERR.MT_OK_ERR) {
            showToast(R.string.kill_success);
        } else {
            showToast(R.string.kill_fail);
        }
    }

    @OnClick({R.id.button_clean})
    void clean() {
        this.editTextReadData.setText("");
    }

    private boolean checkParam(boolean isWrite) {
        if (this.isEPCNULL) {
            showToast(R.string.please_inventory);
            return false;
        }
        String startAddrStr = this.editTextStartAddr.getText().toString().trim();
        String lenStr = this.editTextLen.getText().toString().trim();
        String accessStr = this.editTextAccessPassword.getText().toString().trim();
        if (startAddrStr == null || startAddrStr.length() == 0) {
            showToast(R.string.start_address_not_null);
            return false;
        }
        if (accessStr == null || accessStr.length() == 0) {
            showToast(R.string.access_password_not_null);
            return false;
        }
        if (!isWrite) {
            if (lenStr == null || lenStr.length() == 0) {
                showToast(R.string.len_not_null);
                return false;
            }
            this.len = Integer.valueOf(lenStr).intValue();
        }
        if (!matchHex(accessStr) || accessStr.length() != 8) {
            showToast(R.string.please_input_right_access_password);
            return false;
        }
        this.startAddr = Integer.valueOf(startAddrStr).intValue();
        this.accessPassword = Tools.HexString2Bytes(accessStr);
        return true;
    }

    private boolean matchHex(String data) {
        Pattern pattern = Pattern.compile("-?[0-9a-fA-F]+");
        Matcher matcher = pattern.matcher(data);
        boolean flag = matcher.matches();
        return flag;
    }
}
