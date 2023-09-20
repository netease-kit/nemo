package com.netease.nemo.entlive.dto;

import com.netease.nemo.entlive.model.po.LiveRecord;
import com.netease.nemo.openApi.dto.nim.YunxinCreateLiveChannelDto;
import com.netease.nemo.openApi.paramters.neroom.CreateNeRoomParam;
import com.netease.nemo.util.ObjectMapperUtil;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

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

    /**
     * 直播配置
     */
    private CreateNeRoomParam.ExternalLiveConfig externalLiveConfig;


    private List<SeatUserRewardInfoDto> seatUserReward;

    public LiveDto() {
    }

    public static LiveDto fromLiveRecord(LiveRecord liveRecord) {
        LiveDto liveDto = ObjectMapperUtil.map(liveRecord, LiveDto.class);
        if (!StringUtils.isEmpty(liveDto.getLiveConfig())) {
            YunxinCreateLiveChannelDto liveChannelDto = GsonUtil.fromJson(liveRecord.getLiveConfig(), YunxinCreateLiveChannelDto.class);
            liveDto.setExternalLiveConfig(CreateNeRoomParam.ExternalLiveConfig.toExternalLiveConfig(liveChannelDto));
        }
        return liveDto;
    }

}
