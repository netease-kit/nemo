package com.netease.nemo.game.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.netease.nemo.code.SudErrorCodeEnum;
import com.netease.nemo.exception.SudException;
import com.netease.nemo.game.dto.TokenResponse;
import com.netease.nemo.game.dto.UserClaimDto;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description：JwtUtil
 * @name：caohao
 * @Date：2023/8/11 5:54 PM
 */
@Slf4j
public class JwtUtil {
    private static final long EXPIRE_TIME = 15 * 60 * 1000;
    public static final Long DEFAULT_MIN_SS_TOKEN_EXPIRE_DURATION = 7200000L;

    /**
     * 生成签名的密钥
     *
     * @param appKey   云信appKey
     * @param userUuid 用户uuid
     * @return token
     */
    public static String createToken(String appKey, String userUuid, String appId, String secret) {
        try {
            // 设置过期时间
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            // 私钥和加密算法
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 设置头部信息
            Map<String, Object> header = createHeader();

            return JWT.create()
                    .withHeader(header)
                    .withClaim("userUuid", userUuid)
                    .withClaim("appId", appId)
                    .withClaim("appKey", appKey)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            log.info("JwtUtil createToken error:{}", e.getMessage());
            throw new SudException(SudErrorCodeEnum.TOKEN_CREATE_FAILED);
        }
    }

    private static Map<String, Object> createHeader() {
        Map<String, Object> header = new HashMap(2);
        header.put("typ", "JWT");
        header.put("alg", "HS256");
        return header;
    }

    /**
     * 生成签名的密钥
     *
     * @param appKey     云信appKey
     * @param userUuid   userUuid
     * @param appId      忽然游戏appId
     * @param secret     忽然游戏secret
     * @param expireDate expireDate
     * @return token
     */
    public static String createToken(String appKey, String userUuid, String appId, String secret, long expireDate) {
        try {
            // 设置过期时间
            Date date = new Date(System.currentTimeMillis() + expireDate);
            // 私钥和加密算法
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 设置头部信息
            Map<String, Object> header = createHeader();

            // 返回token字符串
            return JWT.create()
                    .withHeader(header)
                    .withClaim("appKey", appKey)
                    .withClaim("userUuid", userUuid)
                    .withClaim("appId", appId)
                    .withExpiresAt(date)
                    .sign(algorithm);
        } catch (Exception e) {
            log.info("JwtUtil createToken error:{}", e.getMessage());
            throw new SudException(SudErrorCodeEnum.TOKEN_CREATE_FAILED);
        }
    }

    /**
     * 生成SSToken
     *
     * @param appKey     云信appKey
     * @param userUuid   userUuid
     * @param appId      忽然游戏appId
     * @param secret     忽然游戏secret
     * @param expireDate expireDate
     * @return TokenResponse
     */
    public static TokenResponse createSSToken(String appKey, String userUuid, String appId, String secret, long expireDate) {
        try {
            // 设置过期时间
            Date date = new Date(System.currentTimeMillis() + expireDate);
            // 私钥和加密算法
            Algorithm algorithm = Algorithm.HMAC256(secret);
            // 设置头部信息
            Map<String, Object> header = createHeader();

            // 返回token字符串
            String token = JWT.create()
                    .withHeader(header)
                    .withClaim("appKey", appKey)
                    .withClaim("userUuid", userUuid)
                    .withClaim("appId", appId)
                    .withExpiresAt(date)
                    .sign(algorithm);
            return TokenResponse.builder().ssToken(token).expireDate(date.getTime()).build();
        } catch (Exception e) {
            log.info("JwtUtil createToken error:{}", e.getMessage());
            throw new SudException(SudErrorCodeEnum.TOKEN_CREATE_FAILED);
        }
    }



    /**
     * 校验token是否正确
     *
     * @param appId  appId
     * @param secret secret
     * @param token  token
     * @return
     */
    public static String verifyToken(String appId, String secret, String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String userId = jwt.getClaim("userId").asString();
            return userId;
        } catch (JWTVerificationException e) {
            log.info("JwtUtil verifyToken JWTVerificationException :{}", e.getMessage());
            throw new SudException(handleTokenException(e));
        } catch (Exception e1) {
            log.info("JwtUtil verifyToken Exception :{}", e1.getMessage());
            throw new SudException(SudErrorCodeEnum.UNDEFINE);
        }
    }

    public static UserClaimDto verifyToken(String secret, String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String userUuid = jwt.getClaim("userUuid").asString();
            String appKey = jwt.getClaim("appKey").asString();
            String appId = jwt.getClaim("appId").asString();
            return new UserClaimDto(userUuid, appKey, appId, jwt.getExpiresAt().getTime());
        } catch (JWTVerificationException e) {
            log.info("JwtUtil verifyToken JWTVerificationException :{}", e.getMessage());
            throw new SudException(handleTokenException(e));
        } catch (Exception e1) {
            log.info("JwtUtil verifyToken Exception :{}", e1.getMessage());
            throw new SudException(SudErrorCodeEnum.UNDEFINE);
        }
    }

    private static SudErrorCodeEnum handleTokenException(JWTVerificationException e) {
        if (e instanceof AlgorithmMismatchException) {
            return SudErrorCodeEnum.TOKEN_VERIFY_FAILED;
        } else if (e instanceof SignatureVerificationException) {
            return SudErrorCodeEnum.TOKEN_VERIFY_FAILED;
        } else if (e instanceof JWTDecodeException) {
            return SudErrorCodeEnum.TOKEN_DECODE_FAILED;
        } else if (e instanceof InvalidClaimException) {
            return SudErrorCodeEnum.TOKEN_INVALID;
        } else {
            return e instanceof TokenExpiredException ? SudErrorCodeEnum.TOKEN_EXPIRED : SudErrorCodeEnum.UNDEFINE;
        }
    }

}
