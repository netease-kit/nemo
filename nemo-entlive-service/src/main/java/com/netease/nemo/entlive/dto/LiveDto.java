package com.netease.nemo.entlive.dto;

import lombok.Data;

import java.util.List;

@Data
public class LiveDto extends LiveRecordDto {

    /**
     * 打赏总额
     */
    private Long rewardTotal;

    /**
     * 观众人数
     */
    private Long audienceCount;

    /**
     * 上麦人数
     */
    private Long onSeatCount;

    private List<SeatUserRewardInfoDto> seatUserReward;

    public LiveDto() {
    }
}
