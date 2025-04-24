package me.weyye.hipermission;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.List;
import java.util.ListIterator;

/* loaded from: classes.dex */
public class PermissionActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_MUTI = 2;
    public static final int REQUEST_CODE_MUTI_SINGLE = 3;
    private static final int REQUEST_CODE_SINGLE = 1;
    private static final int REQUEST_SETTING = 110;
    private static final String TAG = "PermissionActivity";
    private static PermissionCallback mCallback;
    private int mAnimStyleId;
    private CharSequence mAppName;
    private List<PermissionItem> mCheckPermissions;
    private Dialog mDialog;
    private int mFilterColor;
    private String mMsg;
    private int mPermissionType;
    private int mRePermissionIndex;
    private int mStyleId;
    private String mTitle;
    public static int PERMISSION_TYPE_SINGLE = 1;
    public static int PERMISSION_TYPE_MUTI = 2;

    public static void setCallBack(PermissionCallback callBack) {
        mCallback = callBack;
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        mCallback = null;
        Dialog dialog = this.mDialog;
        if (dialog != null && dialog.isShowing()) {
            this.mDialog.dismiss();
        }
    }

    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDatas();
        if (this.mPermissionType == PERMISSION_TYPE_SINGLE) {
            List<PermissionItem> list = this.mCheckPermissions;
            if (list == null || list.size() == 0) {
                return;
            }
            requestPermission(new String[]{this.mCheckPermissions.get(0).Permission}, 1);
            return;
        }
        this.mAppName = getApplicationInfo().loadLabel(getPackageManager());
        showPermissionDialog();
    }

    private String getPermissionTitle() {
        return TextUtils.isEmpty(this.mTitle) ? String.format(getString(R.string.permission_dialog_title), this.mAppName) : this.mTitle;
    }

    private void showPermissionDialog() {
        String title = getPermissionTitle();
        String msg = TextUtils.isEmpty(this.mMsg) ? String.format(getString(R.string.permission_dialog_msg), this.mAppName) : this.mMsg;
        PermissionView contentView = new PermissionView(this);
        contentView.setGridViewColum(this.mCheckPermissions.size() < 3 ? this.mCheckPermissions.size() : 3);
        contentView.setTitle(title);
        contentView.setMsg(msg);
        contentView.setGridViewAdapter(new PermissionAdapter(this.mCheckPermissions));
        if (this.mStyleId == -1) {
            this.mStyleId = R.style.PermissionDefaultNormalStyle;
            this.mFilterColor = getResources().getColor(R.color.permissionColorGreen);
        }
        contentView.setStyleId(this.mStyleId);
        contentView.setFilterColor(this.mFilterColor);
        contentView.setBtnOnClickListener(new View.OnClickListener() { // from class: me.weyye.hipermission.PermissionActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                if (PermissionActivity.this.mDialog != null && PermissionActivity.this.mDialog.isShowing()) {
                    PermissionActivity.this.mDialog.dismiss();
                }
                String[] strs = PermissionActivity.this.getPermissionStrArray();
                ActivityCompat.requestPermissions(PermissionActivity.this, strs, 2);
            }
        });
        Dialog dialog = new Dialog(this);
        this.mDialog = dialog;
        dialog.requestWindowFeature(1);
        this.mDialog.setContentView(contentView);
        if (this.mAnimStyleId != -1) {
            this.mDialog.getWindow().setWindowAnimations(this.mAnimStyleId);
        }
        this.mDialog.setCanceledOnTouchOutside(false);
        this.mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        this.mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: me.weyye.hipermission.PermissionActivity.2
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialog2) {
                dialog2.dismiss();
                if (PermissionActivity.mCallback != null) {
                    PermissionActivity.mCallback.onClose();
                }
                PermissionActivity.this.finish();
            }
        });
        this.mDialog.show();
    }

    private void reRequestPermission(final String permission) {
        String permissionName = getPermissionItem(permission).PermissionName;
        String alertTitle = String.format(getString(R.string.permission_title), permissionName);
        String msg = String.format(getString(R.string.permission_denied), permissionName, this.mAppName);
        showAlertDialog(alertTitle, msg, getString(R.string.permission_cancel), getString(R.string.permission_ensure), new DialogInterface.OnClickListener() { // from class: me.weyye.hipermission.PermissionActivity.3
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                PermissionActivity.this.requestPermission(new String[]{permission}, 3);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void requestPermission(String[] permissions, int requestCode) {
        ActivityCompat.requestPermissions(this, permissions, requestCode);
    }

    private void showAlertDialog(String title, String msg, String cancelTxt, String PosTxt, DialogInterface.OnClickListener onClickListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle(title).setMessage(msg).setCancelable(false).setNegativeButton(cancelTxt, new DialogInterface.OnClickListener() { // from class: me.weyye.hipermission.PermissionActivity.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                PermissionActivity.this.onClose();
            }
        }).setPositiveButton(PosTxt, onClickListener).create();
        alertDialog.show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String[] getPermissionStrArray() {
        String[] str = new String[this.mCheckPermissions.size()];
        for (int i = 0; i < this.mCheckPermissions.size(); i++) {
            str[i] = this.mCheckPermissions.get(i).Permission;
        }
        return str;
    }

    private void getDatas() {
        Intent intent = getIntent();
        this.mPermissionType = intent.getIntExtra(ConstantValue.DATA_PERMISSION_TYPE, PERMISSION_TYPE_SINGLE);
        this.mTitle = intent.getStringExtra(ConstantValue.DATA_TITLE);
        this.mMsg = intent.getStringExtra(ConstantValue.DATA_MSG);
        this.mFilterColor = intent.getIntExtra(ConstantValue.DATA_FILTER_COLOR, 0);
        this.mStyleId = intent.getIntExtra(ConstantValue.DATA_STYLE_ID, -1);
        this.mAnimStyleId = intent.getIntExtra(ConstantValue.DATA_ANIM_STYLE, -1);
        this.mCheckPermissions = (List) intent.getSerializableExtra(ConstantValue.DATA_PERMISSIONS);
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity, androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            String permission = getPermissionItem(permissions[0]).Permission;
            if (grantResults[0] == 0) {
                onGuarantee(permission, 0);
            } else {
                onDeny(permission, 0);
            }
            finish();
            return;
        }
        if (requestCode == 2) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == 0) {
                    PermissionItem item = getPermissionItem(permissions[i]);
                    this.mCheckPermissions.remove(item);
                    onGuarantee(permissions[i], i);
                } else {
                    onDeny(permissions[i], i);
                }
            }
            if (this.mCheckPermissions.size() > 0) {
                reRequestPermission(this.mCheckPermissions.get(this.mRePermissionIndex).Permission);
                return;
            } else {
                onFinish();
                return;
            }
        }
        if (requestCode == 3) {
            if (grantResults[0] == -1) {
                try {
                    String name = getPermissionItem(permissions[0]).PermissionName;
                    String title = String.format(getString(R.string.permission_title), name);
                    String string = getString(R.string.permission_denied_with_naac);
                    CharSequence charSequence = this.mAppName;
                    String msg = String.format(string, charSequence, name, charSequence);
                    showAlertDialog(title, msg, getString(R.string.permission_reject), getString(R.string.permission_go_to_setting), new DialogInterface.OnClickListener() { // from class: me.weyye.hipermission.PermissionActivity.5
                        @Override // android.content.DialogInterface.OnClickListener
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Uri packageURI = Uri.parse("package:" + PermissionActivity.this.getPackageName());
                                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS", packageURI);
                                PermissionActivity.this.startActivityForResult(intent, 110);
                            } catch (Exception e) {
                                e.printStackTrace();
                                PermissionActivity.this.onClose();
                            }
                        }
                    });
                    onDeny(permissions[0], 0);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    onClose();
                    return;
                }
            }
            onGuarantee(permissions[0], 0);
            if (this.mRePermissionIndex < this.mCheckPermissions.size() - 1) {
                List<PermissionItem> list = this.mCheckPermissions;
                int i2 = this.mRePermissionIndex + 1;
                this.mRePermissionIndex = i2;
                reRequestPermission(list.get(i2).Permission);
                return;
            }
            onFinish();
        }
    }

    @Override // android.app.Activity
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @Override // androidx.activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        finish();
    }

    @Override // androidx.fragment.app.FragmentActivity, android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult--requestCode:" + requestCode + ",resultCode:" + resultCode);
        if (requestCode == 110) {
            Dialog dialog = this.mDialog;
            if (dialog != null && dialog.isShowing()) {
                this.mDialog.dismiss();
            }
            checkPermission();
            if (this.mCheckPermissions.size() > 0) {
                this.mRePermissionIndex = 0;
                reRequestPermission(this.mCheckPermissions.get(0).Permission);
            } else {
                onFinish();
            }
        }
    }

    private void checkPermission() {
        ListIterator<PermissionItem> iterator = this.mCheckPermissions.listIterator();
        while (iterator.hasNext()) {
            int checkPermission = ContextCompat.checkSelfPermission(getApplicationContext(), iterator.next().Permission);
            if (checkPermission == 0) {
                iterator.remove();
            }
        }
    }

    private void onFinish() {
        PermissionCallback permissionCallback = mCallback;
        if (permissionCallback != null) {
            permissionCallback.onFinish();
        }
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClose() {
        PermissionCallback permissionCallback = mCallback;
        if (permissionCallback != null) {
            permissionCallback.onClose();
        }
        finish();
    }

    private void onDeny(String permission, int position) {
        PermissionCallback permissionCallback = mCallback;
        if (permissionCallback != null) {
            permissionCallback.onDeny(permission, position);
        }
    }

    private void onGuarantee(String permission, int position) {
        PermissionCallback permissionCallback = mCallback;
        if (permissionCallback != null) {
            permissionCallback.onGuarantee(permission, position);
        }
    }

    private PermissionItem getPermissionItem(String permission) {
        for (PermissionItem permissionItem : this.mCheckPermissions) {
            if (permissionItem.Permission.equals(permission)) {
                return permissionItem;
            }
        }
        return null;
    }
}
