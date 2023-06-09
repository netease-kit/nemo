package com.netease.nemo.util;

import org.apache.commons.codec.binary.Base64;

/**
 * @author liuxiong02
 * @since 2021/12/02
 */
public class Base64Util {

    public static String encodeStr(byte[] values) {
        byte[] encodedBytes = Base64.encodeBase64(values);

        return new String(encodedBytes);
    }

    public static String encode(String value) {
        byte[] encodedBytes = Base64.encodeBase64(value.getBytes());

        return new String(encodedBytes);
    }

    public static byte[] decodeStr(String value) {
        return Base64.decodeBase64(value);
    }

    public static String decode(String value) {
        byte[] temp = Base64.decodeBase64(value);

        return new String(temp);
    }
}