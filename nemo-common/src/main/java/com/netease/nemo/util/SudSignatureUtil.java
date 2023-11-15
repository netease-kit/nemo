package com.netease.nemo.util;

import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;

/**
 * @Author：CH
 * @name：caohao
 * @Date：2023/8/22 11:11 AM
 */
public class SudSignatureUtil {
    /**
     * 生成忽然服务器签名
     *
     * @param appId     appId
     * @param appSecret appSecret
     * @param body      body
     * @param timestamp timestamp
     * @param nonce     nonce
     * @return Signature
     */

    public static String createSignature(String appId, String appSecret, String body, String timestamp, String nonce) {
        // 签名串
        String signContent = String.format("%s\n%s\n%s\n%s\n", appId, timestamp, nonce, body);
        return new HmacUtils(HmacAlgorithms.HMAC_SHA_1, appSecret.getBytes()).hmacHex(signContent);
    }


    /**
     * 校验忽然服务器签名
     *
     * @param appId        appId
     * @param appSecret    appSecret
     * @param body         body
     * @param timestamp    timestamp
     * @param nonce        nonce
     * @param sudSignature sudSignature
     * @return true or false
     */
    public static boolean verifySignature(String appId, String appSecret, String body, String timestamp, String nonce, String sudSignature) {
        // 签名串
        String signContent = String.format("%s\n%s\n%s\n%s\n", appId, timestamp, nonce, body);
        String signature = new HmacUtils(HmacAlgorithms.HMAC_SHA_1, appSecret.getBytes()).hmacHex(signContent);
        // 比较签名值 true: 验签成功， false: 验签失败
        return sudSignature.equals(signature);
    }
}
