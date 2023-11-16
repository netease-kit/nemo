package com.netease.nemo.entlive.delay.task;


import com.netease.nemo.entlive.model.po.OrderSong;
import lombok.Data;

@Data
public class PlayNextSongTask {

    /**
     * 房间编号
     */
    private String roomUuid;

    /**
     * 点歌编号
     */
    private Long orderId;

    public PlayNextSongTask() {
    }

    public PlayNextSongTask(OrderSong orderSong) {
        this.roomUuid = orderSong.getRoomUuid();
        this.orderId = orderSong.getId();
    }
}
