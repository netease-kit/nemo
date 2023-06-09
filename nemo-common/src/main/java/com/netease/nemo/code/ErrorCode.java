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

    public static final int FORBIDDEN = 403;
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

    /**
     * 用户不存在
     */
    public static final int USER_NOT_EXIST = 1000;

    /**
     * 礼物不存在
     */
    public static final int GIFT_NOT_EXIST = 1001;

    /**
     * 麦位个数超限
     */
    public static final int SEAT_COUNT_OVER_LIMIT = 1002;

    /**
     * 直播记录已存在
     */
    public static final int  LIVE_RECORD_EXIST = 1003;

    /**
     * 主播不在直播中
     */
    public static final int  ANCHOR_NOT_LIVING = 1004;

    /**
     * 直播记录不存在
     */
    public static final int  LIVE_RECORD_NOT_EXIST = 1005;

    /**
     * 用户未加入房间
     */
    public static final int  USER_NOT_IN_ROOM = 1006;

    /**
     * 点歌信息不存在
     */
    public static final int  ORDER_SONG_NOT_EXISTS = 1007;

    /**
     * 该点歌信息已删除
     */
    public static final int  ORDER_SONG_HAS_CANCELLED = 1008;

    /**
     * 用户点歌数量超限
     */
    public static final int USER_ORDER_SONG_EXCEED_LIMIT = 1009;

    /**
     * 房间内点歌数量超限
     */
    public static final int ROOM_ORDER_SONG_EXCEED_LIMIT = 1010;

    /**
     * 用户已点该歌曲
     */
    public static final int  SONG_IS_ALREADY_ORDER = 1011;
}

