package me.weyye.hipermission;

import java.io.Serializable;

/* loaded from: classes.dex */
public interface PermissionCallback extends Serializable {
    void onClose();

    void onDeny(String str, int i);

    void onFinish();

    void onGuarantee(String str, int i);
}
