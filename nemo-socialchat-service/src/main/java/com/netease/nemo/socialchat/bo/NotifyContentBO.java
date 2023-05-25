package com.netease.nemo.socialchat.bo;

import lombok.Data;

/**
 * RTC抄送内容
 */
@Data
public class NotifyContentBO {
    Object data;
    Integer eventType;
}
