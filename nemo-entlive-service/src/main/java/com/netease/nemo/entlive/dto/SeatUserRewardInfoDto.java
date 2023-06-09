package com.netease.nemo.entlive.dto;

import lombok.Data;

/**
 * 麦上用户信息
 */
@Data
public class SeatUserRewardInfoDto {
    private String userUuid;
    private String userName;
    private String icon;
    private Integer seatIndex;
    private Long rewardTotal;
}
