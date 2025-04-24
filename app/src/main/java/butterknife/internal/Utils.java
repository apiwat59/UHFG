package butterknife.internal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import java.lang.reflect.Array;
import java.util.List;

/* loaded from: classes.dex */
public final class Utils {
    private static final TypedValue VALUE = new TypedValue();

    public static Drawable getTintedDrawable(Context context, int id, int tintAttrId) {
        Resources.Theme theme = context.getTheme();
        TypedValue typedValue = VALUE;
        boolean attributeFound = theme.resolveAttribute(tintAttrId, typedValue, true);
        if (!attributeFound) {
            throw new Resources.NotFoundException("Required tint color attribute with name " + context.getResources().getResourceEntryName(tintAttrId) + " and attribute ID " + tintAttrId + " was not found.");
        }
        Drawable drawable = DrawableCompat.wrap(ContextCompat.getDrawable(context, id).mutate());
        int color = ContextCompat.getColor(context, typedValue.resourceId);
        DrawableCompat.setTint(drawable, color);
        return drawable;
    }

    public static float getFloat(Context context, int id) {
        TypedValue value = VALUE;
        context.getResources().getValue(id, value, true);
        if (value.type == 4) {
            return value.getFloat();
        }
        throw new Resources.NotFoundException("Resource ID #0x" + Integer.toHexString(id) + " type #0x" + Integer.toHexString(value.type) + " is not valid");
    }

    @SafeVarargs
    public static <T> T[] arrayFilteringNull(T... tArr) {
        int i = 0;
        int length = tArr.length;
        for (T t : tArr) {
            if (t != null) {
                tArr[i] = t;
                i++;
            }
        }
        if (i == length) {
            return tArr;
        }
        T[] tArr2 = (T[]) ((Object[]) Array.newInstance(tArr.getClass().getComponentType(), i));
        System.arraycopy(tArr, 0, tArr2, 0, i);
        return tArr2;
    }

    @SafeVarargs
    public static <T> List<T> listFilteringNull(T... views) {
        return new ImmutableList(arrayFilteringNull(views));
    }

    public static <T> T findOptionalViewAsType(View view, int i, String str, Class<T> cls) {
        return (T) castView(view.findViewById(i), i, str, cls);
    }

    public static View findRequiredView(View source, int id, String who) {
        View view = source.findViewById(id);
        if (view != null) {
            return view;
        }
        String name = getResourceEntryName(source, id);
        throw new IllegalStateException("Required view '" + name + "' with ID " + id + " for " + who + " was not found. If this view is optional add '@Nullable' (fields) or '@Optional' (methods) annotation.");
    }

    public static <T> T findRequiredViewAsType(View view, int i, String str, Class<T> cls) {
        return (T) castView(findRequiredView(view, i, str), i, str, cls);
    }

    public static <T> T castView(View view, int id, String who, Class<T> cls) {
        try {
            return cls.cast(view);
        } catch (ClassCastException e) {
            String name = getResourceEntryName(view, id);
            throw new IllegalStateException("View '" + name + "' with ID " + id + " for " + who + " was of the wrong type. See cause for more info.", e);
        }
    }

    public static <T> T castParam(Object value, String from, int fromPos, String to, int toPos, Class<T> cls) {
        try {
            return cls.cast(value);
        } catch (ClassCastException e) {
            throw new IllegalStateException("Parameter #" + (fromPos + 1) + " of method '" + from + "' was of the wrong type for parameter #" + (toPos + 1) + " of method '" + to + "'. See cause for more info.", e);
        }
    }

    private static String getResourceEntryName(View view, int id) {
        if (view.isInEditMode()) {
            return "<unavailable while editing>";
        }
        return view.getContext().getResources().getResourceEntryName(id);
    }

    private Utils() {
        throw new AssertionError("No instances.");
    }
}
