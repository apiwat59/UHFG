package com.gg.reader.api.utils;

import com.sun.crypto.provider.SunJCE;
import java.security.Key;
import java.security.Security;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/* loaded from: classes.dex */
public class DesUtils {
    public static final String DES3_CBC = "DESede/CBC/NoPadding";
    public static final String DES3_ECB = "DESede/ECB/NoPadding";
    public static final String DES_CBC = "DES/CBC/NoPadding";
    public static final String DES_ECB = "DES/ECB/NoPadding";
    public static byte[] desDcbKey;

    static {
        Security.addProvider(new SunJCE());
        desDcbKey = new byte[]{103, 120, 119, 108, 105, 111, 116, 0};
    }

    public static byte[] encode(String algorithm, byte[] key, byte[] data) {
        if (key == null || data == null) {
            throw new IllegalArgumentException();
        }
        if (key.length % 8 != 0 || data.length % 8 != 0) {
            throw new IllegalArgumentException();
        }
        if (16 == key.length) {
            byte[] tmp = new byte[24];
            System.arraycopy(key, 0, tmp, 0, 16);
            System.arraycopy(key, 0, tmp, 16, 8);
            key = tmp;
        }
        try {
            int index = algorithm.indexOf(47);
            String type = algorithm;
            if (index > 0) {
                type = algorithm.substring(0, index);
            }
            Key keySpec = new SecretKeySpec(key, type);
            Cipher encryptCipher = Cipher.getInstance(algorithm);
            encryptCipher.init(1, keySpec);
            return encryptCipher.doFinal(data);
        } catch (Exception e) {
            return null;
        }
    }

    public static String encode(String algorithm, String key, String data) {
        byte[] keyBytes = HexUtils.hexString2Bytes(key);
        byte[] dataBytes = HexUtils.hexString2Bytes(data);
        return HexUtils.bytes2HexString(encode(algorithm, keyBytes, dataBytes));
    }

    public static byte[] decode(String algorithm, byte[] key, byte[] data) {
        if (key == null || data == null) {
            throw new IllegalArgumentException();
        }
        if (key.length % 8 != 0 || data.length % 8 != 0) {
            throw new IllegalArgumentException();
        }
        if (16 == key.length) {
            byte[] tmp = new byte[24];
            System.arraycopy(key, 0, tmp, 0, 16);
            System.arraycopy(key, 0, tmp, 16, 8);
            key = tmp;
        }
        try {
            int index = algorithm.indexOf(47);
            String type = algorithm;
            if (index > 0) {
                type = algorithm.substring(0, index);
            }
            Key keySpec = new SecretKeySpec(key, type);
            Cipher encryptCipher = Cipher.getInstance(algorithm);
            encryptCipher.init(2, keySpec);
            return encryptCipher.doFinal(data);
        } catch (Exception e) {
            return null;
        }
    }

    public static String decode(String algorithm, String key, String data) {
        byte[] keyBytes = HexUtils.hexString2Bytes(key);
        byte[] dataBytes = HexUtils.hexString2Bytes(data);
        return HexUtils.bytes2HexString(decode(algorithm, keyBytes, dataBytes));
    }

    public static byte[] encodeDES_ECB(byte[] key, byte[] data) {
        return encode(DES_ECB, key, data);
    }

    public static String encodeDES_ECB(String key, String data) {
        return encode(DES_ECB, key, data);
    }

    public static byte[] decodeDES_ECB(byte[] key, byte[] data) {
        return decode(DES_ECB, key, data);
    }

    public static String decodeDES_ECB(String key, String data) {
        return decode(DES_ECB, key, data);
    }
}
