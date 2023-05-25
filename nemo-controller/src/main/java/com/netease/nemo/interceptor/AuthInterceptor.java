package com.netease.nemo.interceptor;

import com.netease.nemo.annotation.Checksum;
import com.netease.nemo.annotation.TokenAuth;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.context.Context;
import com.netease.nemo.dto.UserDto;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.model.po.User;
import com.netease.nemo.service.UserService;
import com.netease.nemo.util.CheckSumBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
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


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        if (request.getRequestURI().startsWith("/health")) {
            return true;
        }

        if (((HandlerMethod) handler).getBeanType().getAnnotation(Checksum.class) != null) {
            log.debug("执行Checksum鉴权, method: {}", ((HandlerMethod) handler).getBeanType().getName());
            checkCheckSum(request);
            return true;
        }

        if (((HandlerMethod) handler).getBeanType().getAnnotation(TokenAuth.class) != null) {
            log.debug("执行TokenAuth鉴权, method: {}", ((HandlerMethod) handler).getMethod().getName());
            checkToken(Context.get());
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

        String checksumCal = CheckSumBuilder.getCheckSum(nonce, curTime, yunXinConfigProperties.getAppSecret(), md5 == null ? "" : md5);

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
}
