package androidx.constraintlayout.core.motion.parse;

import androidx.constraintlayout.core.motion.utils.TypedBundle;
import androidx.constraintlayout.core.motion.utils.TypedValues;
import androidx.constraintlayout.core.parser.CLElement;
import androidx.constraintlayout.core.parser.CLKey;
import androidx.constraintlayout.core.parser.CLObject;
import androidx.constraintlayout.core.parser.CLParser;
import androidx.constraintlayout.core.parser.CLParsingException;

/* loaded from: classes.dex */
public class KeyParser {

    /* JADX INFO: Access modifiers changed from: private */
    interface DataType {
        int get(int i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    interface Ids {
        int get(String str);
    }

    private static TypedBundle parse(String str, Ids table, DataType dtype) {
        TypedBundle bundle = new TypedBundle();
        try {
            CLObject parsedContent = CLParser.parse(str);
            int n = parsedContent.size();
            for (int i = 0; i < n; i++) {
                CLKey clkey = (CLKey) parsedContent.get(i);
                String type = clkey.content();
                CLElement value = clkey.getValue();
                int id = table.get(type);
                if (id == -1) {
                    System.err.println("unknown type " + type);
                } else {
                    int i2 = dtype.get(id);
                    if (i2 == 1) {
                        bundle.add(id, parsedContent.getBoolean(i));
                    } else if (i2 == 2) {
                        bundle.add(id, value.getInt());
                        System.out.println("parse " + type + " INT_MASK > " + value.getInt());
                    } else if (i2 == 4) {
                        bundle.add(id, value.getFloat());
                        System.out.println("parse " + type + " FLOAT_MASK > " + value.getFloat());
                    } else if (i2 == 8) {
                        bundle.add(id, value.content());
                        System.out.println("parse " + type + " STRING_MASK > " + value.content());
                    }
                }
            }
        } catch (CLParsingException e) {
            e.printStackTrace();
        }
        return bundle;
    }

    public static TypedBundle parseAttributes(String str) {
        return parse(str, new Ids() { // from class: androidx.constraintlayout.core.motion.parse.-$$Lambda$hNmrxjz972As46Jl5Po5RJ1Nm6c
            @Override // androidx.constraintlayout.core.motion.parse.KeyParser.Ids
            public final int get(String str2) {
                return TypedValues.AttributesType.CC.getId(str2);
            }
        }, new DataType() { // from class: androidx.constraintlayout.core.motion.parse.-$$Lambda$H-ghhCyRcCZ40GTQ2whRMSAC7hA
            @Override // androidx.constraintlayout.core.motion.parse.KeyParser.DataType
            public final int get(int i) {
                return TypedValues.AttributesType.CC.getType(i);
            }
        });
    }

    public static void main(String[] args) {
        parseAttributes("{frame:22,\ntarget:'widget1',\neasing:'easeIn',\ncurveFit:'spline',\nprogress:0.3,\nalpha:0.2,\nelevation:0.7,\nrotationZ:23,\nrotationX:25.0,\nrotationY:27.0,\npivotX:15,\npivotY:17,\npivotTarget:'32',\npathRotate:23,\nscaleX:0.5,\nscaleY:0.7,\ntranslationX:5,\ntranslationY:7,\ntranslationZ:11,\n}");
    }
}
