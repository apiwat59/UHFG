package me.weyye.hipermission;

import java.io.Serializable;

/* loaded from: classes.dex */
public class PermissionItem implements Serializable {
    public String Permission;
    public int PermissionIconRes;
    public String PermissionName;

    public PermissionItem(String permission, String permissionName, int permissionIconRes) {
        this.Permission = permission;
        this.PermissionName = permissionName;
        this.PermissionIconRes = permissionIconRes;
    }

    public PermissionItem(String permission) {
        this.Permission = permission;
    }
}
