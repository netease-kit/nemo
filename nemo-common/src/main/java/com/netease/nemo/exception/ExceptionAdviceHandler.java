package com.netease.nemo.exception;

import com.google.common.collect.ImmutableMap;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.result.ResultView;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@ControllerAdvice
@Slf4j
public class ExceptionAdviceHandler {

    @Autowired
    private MessageSource messageSource;

    @ExceptionHandler(BsException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResultView BsExceptionHandler(HttpServletRequest request, BsException e) {
        log(e.getCode(), request, e);
        String msg = e.getMsg();
        if (StringUtils.isEmpty(msg)) {
            msg = getMessage(e.getCode());
        }
        return ResultView.failed(e.getCode(), msg);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResultView requestHandlingNoHandlerFound(HttpServletRequest request, Exception e) {
        log(ErrorCode.NOT_FOUND, request, e);
        return ResultView.failed(ErrorCode.NOT_FOUND, getMessage(ErrorCode.NOT_FOUND));
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class, ServletRequestBindingException.class,
            TypeMismatchException.class, HttpMessageNotReadableException.class,
            MissingServletRequestPartException.class
    })
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResultView requestHandlingBadRequests(HttpServletRequest request, Exception e) {
        log(ErrorCode.BAD_REQUEST, request, e);
        return ResultView.failed(ErrorCode.BAD_REQUEST, getMessage(ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(AsyncRequestTimeoutException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResultView requestHandlingNotAvailable(HttpServletRequest request, Exception e) {
        log(ErrorCode.SERVICE_UNAVAILABLE, request, e);
        return ResultView.failed(ErrorCode.SERVICE_UNAVAILABLE, getMessage(ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResultView requestHandlingMethodNotSupported(HttpServletRequest request, Exception e) {
        log(ErrorCode.METHOD_NOT_ALLOWED, request, e);
        return ResultView.failed(ErrorCode.METHOD_NOT_ALLOWED, getMessage(ErrorCode.METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResultView requestHandlingHttpMediaTypeNotSupported(HttpServletRequest request, Exception e) {
        log(ErrorCode.UNSUPPORTED_MEDIA_TYPE, request, e);
        return ResultView.failed(ErrorCode.UNSUPPORTED_MEDIA_TYPE, getMessage(ErrorCode.UNSUPPORTED_MEDIA_TYPE));
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResultView requestHandlingNumberFormatException(HttpServletRequest request, Exception e) {
        log(ErrorCode.BAD_REQUEST, request, e);
        return ResultView.failed(ErrorCode.BAD_REQUEST, getMessage(ErrorCode.BAD_REQUEST));
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResultView requestHandlingHttpMediaTypeNotAcceptable(HttpServletRequest request, Exception e) {
        log(ErrorCode.NOT_ACCEPTABLE, request, e);
        return ResultView.failed(ErrorCode.NOT_ACCEPTABLE, getMessage(ErrorCode.NOT_ACCEPTABLE));
    }

    @ExceptionHandler({
            MissingPathVariableException.class, ConversionNotSupportedException.class,
            HttpMessageNotWritableException.class})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResultView requestHandlingInternalError(HttpServletRequest request, Exception e) {
        log(ErrorCode.INTERNAL_SERVER_ERROR, request, e);
        return ResultView.failed(ErrorCode.INTERNAL_SERVER_ERROR, getMessage(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler({
            NullPointerException.class})
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResultView nullPointerExceptionHandler(HttpServletRequest request, Exception e) {
        log(ErrorCode.INTERNAL_SERVER_ERROR, request, e);
        return ResultView.failed(ErrorCode.INTERNAL_SERVER_ERROR, getMessage(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ResultView exceptionHandler(HttpServletRequest request, Exception e) {
        log(ErrorCode.INTERNAL_SERVER_ERROR, request, e);
        return ResultView.failed(ErrorCode.INTERNAL_SERVER_ERROR, getMessage(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class, org.springframework.validation.BindException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResultView handleMethodArgumentNotValidException(Exception e) {
        BindingResult result;
        if (e instanceof org.springframework.validation.BindException) {
            result = ((org.springframework.validation.BindException) e).getBindingResult();
        } else if (e instanceof  MethodArgumentNotValidException) {
            result = ((MethodArgumentNotValidException) e).getBindingResult();
        } else {
            result = null;
        }
        String errMsg = result == null ? null : result.getAllErrors().get(0).getDefaultMessage();
        return ResultView.failed(ErrorCode.INTERNAL_SERVER_ERROR, errMsg);
    }

    /**
     * 统一打印异常信息
     *
     * @param code 状态码
     * @param request 请求
     * @param e 异常
     */
    public void log(int code, HttpServletRequest request, Exception e) {
        ImmutableMap.Builder<String, Object> headerBuilder = ImmutableMap.builder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            headerBuilder.put(key, request.getHeader(key));
        }
        ImmutableMap<String, Object> headers = headerBuilder.build();
        ImmutableMap.Builder<String, Object> req = ImmutableMap.<String, Object>builder()
                .put("uri", request.getRequestURI())
                .put("header", headers)
                .put("host", request.getRemoteAddr())
                .put("code", code)
                .put("msg", messageSource.getMessage(String.valueOf(code), null, LocaleContextHolder.getLocale()));
        ImmutableMap<String, Object> reqMap = req.build();

        log.info(GsonUtil.toJson(reqMap), StringUtils.isBlank(e.getMessage()) ? new Exception(reqMap.get("msg").toString(), e) : e);

    }


    private String getMessage(int code) {
        return messageSource.getMessage(String.valueOf(code), null, LocaleContextHolder.getLocale());
    }
}
