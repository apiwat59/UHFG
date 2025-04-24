package butterknife;

import androidx.constraintlayout.core.motion.utils.TypedValues;
import butterknife.internal.ListenerClass;
import butterknife.internal.ListenerMethod;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ListenerClass(method = {@ListenerMethod(name = "onFocusChange", parameters = {"android.view.View", TypedValues.Custom.S_BOOLEAN})}, setter = "setOnFocusChangeListener", targetType = "android.view.View", type = "android.view.View.OnFocusChangeListener")
/* loaded from: classes.dex */
public @interface OnFocusChange {
    int[] value() default {-1};
}
