package com.netease.nemo.filter;

import com.google.common.collect.ImmutableMap;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.context.Context;
import com.netease.nemo.context.ContextHandler;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.util.IPUtil;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.LinkedHashMap;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Slf4j
@Component
@WebFilter(filterName = "NemoFilter", urlPatterns = "/*")
public class NemoFilter implements Filter {

    @Resource
    private ContextHandler contextHandler;

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(httpServletRequest);

        try {
            Context context = contextHandler.init();
            context.setClientIp(IPUtil.getRealClientIpAddr(requestWrapper));
            context.setHeaderMap(getHeader(httpServletRequest));
            context.setApiParam();

            // 过滤健康检查请求
            if (httpServletRequest.getServletPath().startsWith("/nemo/health")) {
                filterChain.doFilter(requestWrapper, servletResponse);
                return;
            }

            String appKey = Context.get().getAppKey();
            if (StringUtils.isEmpty(appKey) || !yunXinConfigProperties.getAppKey().equals(appKey)) {
                log.error("appKey is invalid, appKey: {}", appKey);
                throw new BsException(ErrorCode.BAD_REQUEST, "appKey is invalid");
            }

            log.debug("context init.");
            filterChain.doFilter(requestWrapper, servletResponse);
        } catch (BsException e) {
            log.error(e.getMessage(), e);
            writerResponse(servletResponse, e.getCode(), e.getMsg());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            writerResponse(servletResponse, ErrorCode.INTERNAL_SERVER_ERROR, "internal server error");
        } finally {
            Context.get().setReqBody(new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8));
            if (!requestWrapper.getRequestURI().startsWith("/nemo/health/")) {
                log.info("Request Info, uri: {}, header: {}, requestBody: {}, result: {}, clientIP: {}",
                        requestWrapper.getRequestURI(),
                        GsonUtil.toJson(Context.get().getHeaderMap()), Context.get().getReqBody(),
                        GsonUtil.toJson(Context.get().getRespBody()),
                        Context.get().getClientIp());
            }
            // 清理日志 MDC
            contextHandler.destroy();
        }
    }

    private void writerResponse(ServletResponse servletResponse, Integer code, String msg) throws IOException {
        LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();
        returnMap.put("code", code);
        returnMap.put("msg", msg);
        returnMap.put("requestId", Context.get().getTraceId());
        servletResponse.setContentType(APPLICATION_JSON_UTF8_VALUE);
        servletResponse.getWriter().write(GsonUtil.toJson(returnMap));
    }

    public ImmutableMap<String, String> getHeader(HttpServletRequest request) {
        ImmutableMap.Builder<java.lang.String, java.lang.String> headerBuilder = ImmutableMap.builder();
        Enumeration<java.lang.String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            java.lang.String key = headerNames.nextElement();
            headerBuilder.put(key, request.getHeader(key));
        }
        return headerBuilder.build();
    }


}
