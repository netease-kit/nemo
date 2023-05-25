package com.netease.nemo.code;

public class ErrorCode {
    /**
     * sucess
     */
    public static final int OK = 200;

    /**
     * system error code
     */
    public static final int SYS_ERROR = -1;

    /**
     * http request error
     */
    public static final int SYS_HTTP_REQUEST_ERROR = -9;

    /**
     * unsupported encoding error
     */
    public static final int SYS_UNSUPPORTED_ENCODING_ERROR = -10;

    /**
     * not acceptable
     */
    public static final int BAD_REQUEST = 400;

    public static final int UNAUTHORIZED = 401;

    public static final int TOKEN_NOT_CORRECT = 402;

    /**
     * method not allowed
     */

    public static final int METHOD_NOT_ALLOWED = 405;

    /**
     * not acceptable
     */
    public static final int NOT_ACCEPTABLE = 406;

    /**
     * http not found
     */
    public static final int NOT_FOUND = 404;

    /**
     * unsupported media type
     */
    public static final int UNSUPPORTED_MEDIA_TYPE = 415;

    /**
     * service unavailable
     */
    public static final int SERVICE_UNAVAILABLE = 510;

    /**
     * internal server error
     */
    public static final int INTERNAL_SERVER_ERROR = 500;



    public static final int USER_NOT_EXIST = 1000;

    public static final int GIFT_NOT_EXIST = 1001;

}
