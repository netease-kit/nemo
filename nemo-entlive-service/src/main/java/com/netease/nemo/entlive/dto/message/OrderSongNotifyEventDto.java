package com.netease.nemo.entlive.dto.message;

import com.netease.nemo.entlive.dto.BasicUserDto;
import com.netease.nemo.entlive.dto.OrderSongResultDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * 点歌台事件对象
 *
 * @Author：CH
 * @Date：2023/5/21 11:04 上午
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderSongNotifyEventDto {
    private OrderSongResultDto orderSongResultDto;
    private BasicUserDto operatorUser;
    private OrderSongResultDto nextOrderSong;
    private String attachment;
}
