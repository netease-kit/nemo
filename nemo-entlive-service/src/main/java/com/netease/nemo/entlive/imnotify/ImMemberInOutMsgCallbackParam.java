package com.netease.nemo.entlive.imnotify;

import lombok.Data;

/**
 * 作用描述：https://doc.yunxin.163.com/docs/TM5MzM5Njk/TcxNzU4NzU?platformId=60353#%E8%81%8A%E5%A4%A9%E5%AE%A4%E6%88%90%E5%91%98%E8%BF%9B%E5%87%BA%E8%81%8A%E5%A4%A9%E5%AE%A4%E4%BA%8B%E4%BB%B6%E6%8A%84%E9%80%81
 * 使用场景：聊天室成员进出聊天室事件抄送
 * 数据示例：
 * {
 * "clientType": "WEB",
 * "code": "200",
 * "clientIp": "115.236.119.138",
 * "accid": "anonymous_1_11",
 * "sdkVersion": "200",
 * "eventType": "9",
 * "event": "IN",
 * "roleType": "1",
 * "roomId": "761480713",
 * "timestamp": "1644546460085"
 * }
 */
@Data
public class ImMemberInOutMsgCallbackParam {
    /**
     * 值为9，主播或管理员进出聊天室事件抄送 和 全员进出聊天室抄送
     */
    private String eventType;
    /**
     * 聊天室id
     */
    private String roomId;
    /**
     * 用户帐号，字符串类型
     */
    private String accid;
    /**
     * 进入或退出。IN：进入聊天室；OUT：主动退出聊天室，或掉线
     */
    private String event;
    /**
     * 成员角色：0:普通用户;1:创建者;2:管理员;3:临时用户(游客) 4:匿名用户;-1:受限用户(禁言、黑名单)
     */
    private String roleType;
    private String clientType;
    private String code;
    private String clientIp;
    private String sdkVersion;
    private Long timestamp;
    private String tags;
}
