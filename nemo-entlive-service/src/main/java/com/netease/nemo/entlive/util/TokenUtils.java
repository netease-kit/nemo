package com.netease.nemo.entlive.util;

import com.google.gson.JsonObject;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.util.Base64Util;
import com.netease.nemo.util.CheckSumBuilder;
import com.netease.nemo.util.gson.GsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class TokenUtils {
    private final static Logger logger = LoggerFactory.getLogger(TokenUtils.class);

    private static final String SIGNATURE = "signature";
    private static final String CUR_TIME = "curTime";//unix时间戳，单位ms
    private static final String TTL = "ttl";//单位s

    public static boolean check(String appKey, String appSecret, String identification, String token) {
        JsonObject loginJson;
        try {
            loginJson = GsonUtil.parseJsonObject(Base64Util.decode(token));
        } catch (Exception e) {
            logger.error("token is not.json");
            return false;
        }
        if (StringUtils.isAnyEmpty(appKey, appSecret, identification)) return false;

        String signature = null;
        Long curTime = null;
        Long ttl = null;
        try {
            signature = GsonUtil.getString(loginJson, SIGNATURE);
            curTime = GsonUtil.getLong(loginJson, CUR_TIME);
            ttl = GsonUtil.getLong(loginJson, TTL);
        } catch (Exception e) {
            logger.error("parse loginJson failed");
            return false;
        }

        if (signature == null || curTime == null || ttl == null) return false;

        if (curTime + ttl * 1000 < System.currentTimeMillis()) {
            logger.error("signature is expire");
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "DynamicsToken error");
        }

        String hex = encodeToSha1Hex(appKey + identification + curTime + ttl + appSecret);
        return signature.equals(hex);
    }

    private static String encodeToSha1Hex(String plaintext) {
        if (StringUtils.isBlank(plaintext)) {
            return "";
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(plaintext.getBytes(StandardCharsets.UTF_8));
            return CheckSumBuilder.getFormattedText(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            logger.error("Encode error {} msg {}", plaintext, e.getMessage());
        }

        return "";
    }

    public static String generate(String appKey, String appSecret, String identification, Long ttl) {
        long curTime = System.currentTimeMillis();
        String signature = encodeToSha1Hex(appKey + identification + curTime + ttl + appSecret);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(SIGNATURE, signature);
        jsonObject.addProperty(CUR_TIME, curTime);
        jsonObject.addProperty(TTL, ttl);
        return Base64Util.encode(jsonObject.toString());
    }
}
