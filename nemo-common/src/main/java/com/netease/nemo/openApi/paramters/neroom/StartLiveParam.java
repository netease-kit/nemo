package com.netease.nemo.openApi.paramters.neroom;

import com.netease.nemo.openApi.dto.neroom.LayoutCoordinateDto;
import lombok.Data;

import java.util.List;

/**
 * https://doc.yunxin.163.com/meeting/server-apis/Tg1OTgxNjc?platform=server
 */
@Data
public class StartLiveParam {

    private Long roomArchiveId;

    private List<String> liveUserUuids;

    private String title;

    private Integer liveLayout;

    private LayoutCoordinateDto.LiveCanvas canvas;

    private List<LayoutCoordinateDto.BaseCoordinate> layoutCoordinateList;

    private boolean liveChatRoomIndependent = false;

    public void refreshLayout(LayoutCoordinateDto newLayout) {
        this.canvas = newLayout.getLayoutCanvas();
        this.layoutCoordinateList = newLayout.getLayoutList();
        if(this.liveUserUuids != null && this.layoutCoordinateList != null){
            for (int i = 0; i < Math.min(liveUserUuids.size(), layoutCoordinateList.size()); i++) {
                this.layoutCoordinateList.get(i).setUserUuid(liveUserUuids.get(i));
            }
        }
    }

    public interface LiveLayoutType{
        int CUSTOM = 5;
    }
}
