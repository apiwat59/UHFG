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
@ListenerClass(method = {@ListenerMethod(defaultReturn = "true", name = "onTouch", parameters = {"android.view.View", "android.view.MotionEvent"}, returnType = TypedValues.Custom.S_BOOLEAN)}, setter = "setOnTouchListener", targetType = "android.view.View", type = "android.view.View.OnTouchListener")
/* loaded from: classes.dex */
public @interface OnTouch {
    int[] value() default {-1};
}
