package com.netease.nemo.interceptor;

import com.netease.nemo.annotation.Checksum;
import com.netease.nemo.annotation.SudSignature;
import com.netease.nemo.annotation.TokenAuth;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.context.Context;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.service.UserService;
import com.netease.nemo.util.CheckSumBuilder;
import com.netease.nemo.util.SudSignatureUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    @Value("${business.game.appSecret}")
    private String appSecret;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        if (request.getRequestURI().startsWith("/nemo/health")) {
            return true;
        }

        if (((HandlerMethod) handler).getMethodAnnotation(Checksum.class) != null) {
            log.debug("执行Checksum鉴权, method: {}", ((HandlerMethod) handler).getBeanType().getName());
            checkCheckSum(request);
            return true;
        }

        if (((HandlerMethod) handler).getBeanType().getAnnotation(TokenAuth.class) != null
                || ((HandlerMethod) handler).getMethodAnnotation(TokenAuth.class) != null) {
            log.debug("执行TokenAuth鉴权, method: {}", ((HandlerMethod) handler).getMethod().getName());
            checkToken(Context.get());
            return true;
        }


        if (((HandlerMethod) handler).getBeanType().getAnnotation(SudSignature.class) != null
                || ((HandlerMethod) handler).getMethodAnnotation(SudSignature.class) != null) {
            log.debug("执行TokenAuth鉴权, method: {}", ((HandlerMethod) handler).getMethod().getName());
            verifySignature(request, Context.get().getReqBody());
            return true;
        }
        return true;
    }


    private void checkCheckSum(HttpServletRequest request) {
        String curTime = request.getHeader("CurTime");
        String checksum = request.getHeader("CheckSum");
        String nonce = request.getHeader("Nonce");
        String md5 = request.getHeader("MD5");
        if (StringUtils.isAnyBlank(curTime, checksum, nonce)) {
            throw new BsException(ErrorCode.UNAUTHORIZED, "Missing header parameter,Required parameters:CurTime,CheckSum,Nonce");
        }

        String checksumCal = CheckSumBuilder.getCheckSum(nonce, curTime, Context.get().getSecret(), md5 == null ? "" : md5);

        if (!checksumCal.equalsIgnoreCase(checksum)) {
            throw new BsException(ErrorCode.UNAUTHORIZED, "Bad checksum:" + checksum);
        }
    }

    private void checkToken(Context context) {
        if (Strings.isBlank(context.getUserToken()) || Strings.isBlank(context.getUserUuid())) {
            throw new BsException(ErrorCode.UNAUTHORIZED, "Missing header parameter,Required parameters:UserUuid,UserToken");
        }
        UserDto user = userService.getUser(context.getUserUuid());
        if (!user.getUserToken().equals(context.getUserToken())) {
            throw new BsException(ErrorCode.TOKEN_NOT_CORRECT, "Bad token:" + context.getUserToken());
        }
    }


    /**
     * 校验签名
     *
     * @param request     请求体
     * @param requestBody 请求内容，为保证发送方与接收方的数据一致，建议在拦截器里取对应值
     * @return true: 验签成功， false: 验签失败
     */
    public boolean verifySignature(HttpServletRequest request, String requestBody) {
        // SudAppId
        String sudAppId = request.getHeader("Sud-AppId");
        // SudTimestamp
        String sudTimestamp = request.getHeader("Sud-Timestamp");
        // SudNonce
        String sudNonce = request.getHeader("Sud-Nonce");
        // SudSignature
        String sudSignature = request.getHeader("Sud-Signature");
        // 请求body (需保证发送方与接收方的数据一致，建议在拦截器里取对应值）
        String body = requestBody;

        boolean verify = SudSignatureUtil.verifySignature(sudAppId, appSecret, body, sudTimestamp, sudNonce, sudSignature);
        if (!verify) {
            throw new BsException(ErrorCode.FORBIDDEN, "Bad sudSignature:" + sudSignature);
        }
        return true;
    }
}
