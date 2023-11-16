package com.netease.nemo.game.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：CH
 * @Date：2023/8/14 1:30 PM
 */
@Data
@NoArgsConstructor
public class UserClaimDto{
    /**
     * 云信appKey
     */
    private String appKey;
    /**
     * 用户uuid
     */
    private String userUuid;
    /**
     * appId
     */
    private String appId;

    /**
     * 过期时间
     */
    private long expireDate;

    public UserClaimDto(String userUuid, String appKey, String appId, long expireDate) {
        this.appId = appId;
        this.appKey = appKey;
        this.userUuid = userUuid;
        this.expireDate = expireDate;
    }
}
