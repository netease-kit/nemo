package com.netease.nemo.entlive.dto;

import lombok.Data;
import org.springframework.beans.BeanUtils;

@Data
public class OrderSongBroadcastResultDto extends OrderSongResultDto {

    /**
     * 操作人
     **/
    private BasicUserDto operator;

    private String attachment;

    private OrderSongResultDto nextOrderSong;

    public OrderSongBroadcastResultDto() {
    }

    public static OrderSongBroadcastResultDto build(OrderSongResultDto orderSongResultDto, BasicUserDto operator) {
        if (orderSongResultDto == null) {
            return null;
        }
        OrderSongBroadcastResultDto orderSongBroadcastResultDto = new OrderSongBroadcastResultDto();
        BeanUtils.copyProperties(orderSongResultDto, orderSongBroadcastResultDto);
        if (operator != null) {
            orderSongBroadcastResultDto.setOperator(operator);
        }
        return orderSongBroadcastResultDto;
    }
}
