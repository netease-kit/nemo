package com.netease.nemo.entlive.dto;

import com.netease.nemo.dto.UserDto;
import com.netease.nemo.entlive.model.po.OrderSong;
import com.netease.nemo.util.ObjectMapperUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSongResultDto {

    /**
     * 点歌信息
     */
    private OrderSongDto orderSong;

    /**
     * 点歌用户信息
     */
    private BasicUserDto orderSongUser;


    public static OrderSongResultDto build(OrderSong orderSong, UserDto userDto) {
        OrderSongResultDto orderSongResultDto = new OrderSongResultDto();
        orderSongResultDto.setOrderSong(ObjectMapperUtil.map(orderSong, OrderSongDto.class));
        orderSongResultDto.setOrderSongUser(BasicUserDto.buildBasicUser(userDto));
        return orderSongResultDto;
    }
}
