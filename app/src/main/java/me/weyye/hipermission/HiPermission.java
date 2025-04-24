package me.weyye.hipermission;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.content.ContextCompat;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
public class HiPermission {
    private PermissionCallback mCallback;
    private List<PermissionItem> mCheckPermissions;
    private final Context mContext;
    private String mMsg;
    private String[] mNormalPermissionNames;
    private int mPermissionType;
    private String mTitle;
    private int mStyleResId = -1;
    private String[] mNormalPermissions = {"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_FINE_LOCATION", "android.permission.CAMERA"};
    private int[] mNormalPermissionIconRes = {R.drawable.permission_ic_storage, R.drawable.permission_ic_location, R.drawable.permission_ic_camera};
    private int mFilterColor = 0;
    private int mAnimStyleId = -1;

    public static HiPermission create(Context context) {
        return new HiPermission(context);
    }

    public HiPermission(Context context) {
        this.mContext = context;
        this.mNormalPermissionNames = context.getResources().getStringArray(R.array.permissionNames);
    }

    public HiPermission title(String title) {
        this.mTitle = title;
        return this;
    }

    public HiPermission msg(String msg) {
        this.mMsg = msg;
        return this;
    }

    public HiPermission permissions(List<PermissionItem> permissionItems) {
        this.mCheckPermissions = permissionItems;
        return this;
    }

    public HiPermission filterColor(int color) {
        this.mFilterColor = color;
        return this;
    }

    public HiPermission animStyle(int styleId) {
        this.mAnimStyleId = styleId;
        return this;
    }

    public HiPermission style(int styleResIdsId) {
        this.mStyleResId = styleResIdsId;
        return this;
    }

    private List<PermissionItem> getNormalPermissions() {
        List<PermissionItem> permissionItems = new ArrayList<>();
        int i = 0;
        while (true) {
            String[] strArr = this.mNormalPermissionNames;
            if (i < strArr.length) {
                permissionItems.add(new PermissionItem(this.mNormalPermissions[i], strArr[i], this.mNormalPermissionIconRes[i]));
                i++;
            } else {
                return permissionItems;
            }
        }
    }

    public static boolean checkPermission(Context context, String permission) {
        int checkPermission = ContextCompat.checkSelfPermission(context, permission);
        if (checkPermission == 0) {
            return true;
        }
        return false;
    }

    public void checkMutiPermission(PermissionCallback callback) {
        if (Build.VERSION.SDK_INT < 23) {
            if (callback != null) {
                callback.onFinish();
                return;
            }
            return;
        }
        if (this.mCheckPermissions == null) {
            ArrayList arrayList = new ArrayList();
            this.mCheckPermissions = arrayList;
            arrayList.addAll(getNormalPermissions());
        }
        Iterator<PermissionItem> iterator = this.mCheckPermissions.listIterator();
        while (iterator.hasNext()) {
            if (checkPermission(this.mContext, iterator.next().Permission)) {
                iterator.remove();
            }
        }
        this.mCallback = callback;
        if (this.mCheckPermissions.size() > 0) {
            startActivity();
        } else if (callback != null) {
            callback.onFinish();
        }
    }

    public void checkSinglePermission(String permission, PermissionCallback callback) {
        if (Build.VERSION.SDK_INT < 23 || checkPermission(this.mContext, permission)) {
            if (callback != null) {
                callback.onGuarantee(permission, 0);
            }
        } else {
            this.mCallback = callback;
            this.mPermissionType = PermissionActivity.PERMISSION_TYPE_SINGLE;
            ArrayList arrayList = new ArrayList();
            this.mCheckPermissions = arrayList;
            arrayList.add(new PermissionItem(permission));
            startActivity();
        }
    }

    private void startActivity() {
        PermissionActivity.setCallBack(this.mCallback);
        Intent intent = new Intent(this.mContext, (Class<?>) PermissionActivity.class);
        intent.putExtra(ConstantValue.DATA_TITLE, this.mTitle);
        intent.putExtra(ConstantValue.DATA_PERMISSION_TYPE, this.mPermissionType);
        intent.putExtra(ConstantValue.DATA_MSG, this.mMsg);
        intent.putExtra(ConstantValue.DATA_FILTER_COLOR, this.mFilterColor);
        intent.putExtra(ConstantValue.DATA_STYLE_ID, this.mStyleResId);
        intent.putExtra(ConstantValue.DATA_ANIM_STYLE, this.mAnimStyleId);
        intent.putExtra(ConstantValue.DATA_PERMISSIONS, (Serializable) this.mCheckPermissions);
        intent.addFlags(268435456);
        this.mContext.startActivity(intent);
    }
}
