package jxl.biff;

import jxl.WorkbookSettings;
import jxl.common.Logger;

/* loaded from: classes.dex */
public class EncodedURLHelper {
    private static Logger logger = Logger.getLogger(EncodedURLHelper.class);
    private static byte msDosDriveLetter = 1;
    private static byte sameDrive = 2;
    private static byte endOfSubdirectory = 3;
    private static byte parentDirectory = 4;
    private static byte unencodedUrl = 5;

    public static byte[] getEncodedURL(String s, WorkbookSettings ws) {
        if (s.startsWith("http:")) {
            return getURL(s, ws);
        }
        return getFile(s, ws);
    }

    private static byte[] getFile(String s, WorkbookSettings ws) {
        String nextFileNameComponent;
        ByteArray byteArray = new ByteArray();
        int pos = 0;
        if (s.charAt(1) == ':') {
            byteArray.add(msDosDriveLetter);
            byteArray.add((byte) s.charAt(0));
            pos = 2;
        } else if (s.charAt(0) == '\\' || s.charAt(0) == '/') {
            byteArray.add(sameDrive);
        }
        while (true) {
            if (s.charAt(pos) != '\\' && s.charAt(pos) != '/') {
                break;
            }
            pos++;
        }
        while (pos < s.length()) {
            int nextSepIndex1 = s.indexOf(47, pos);
            int nextSepIndex2 = s.indexOf(92, pos);
            int nextSepIndex = 0;
            if (nextSepIndex1 != -1 && nextSepIndex2 != -1) {
                nextSepIndex = Math.min(nextSepIndex1, nextSepIndex2);
            } else if (nextSepIndex1 == -1 || nextSepIndex2 == -1) {
                nextSepIndex = Math.max(nextSepIndex1, nextSepIndex2);
            }
            if (nextSepIndex == -1) {
                nextFileNameComponent = s.substring(pos);
                pos = s.length();
            } else {
                nextFileNameComponent = s.substring(pos, nextSepIndex);
                pos = nextSepIndex + 1;
            }
            if (!nextFileNameComponent.equals(".")) {
                if (nextFileNameComponent.equals("..")) {
                    byteArray.add(parentDirectory);
                } else {
                    byteArray.add(StringHelper.getBytes(nextFileNameComponent, ws));
                }
            }
            if (pos < s.length()) {
                byteArray.add(endOfSubdirectory);
            }
        }
        return byteArray.getBytes();
    }

    private static byte[] getURL(String s, WorkbookSettings ws) {
        ByteArray byteArray = new ByteArray();
        byteArray.add(unencodedUrl);
        byteArray.add((byte) s.length());
        byteArray.add(StringHelper.getBytes(s, ws));
        return byteArray.getBytes();
    }
}
