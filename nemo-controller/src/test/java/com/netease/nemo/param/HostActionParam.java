package com.netease.nemo.param;

import lombok.Data;

@Data
public class HostActionParam extends UserActionParam {

    /**
     * 被操作的对方账号编号（主播操作时必传）
     */
    private String toUserUuid;
}
