package com.netease.nemo.entlive.dto;

import org.apache.commons.lang3.StringUtils;

/**
 * KTV 点歌台关播消息
 */
public class KtvOrderSongNotifyEventDto {
    /**
     * 歌曲信息
     */
    private OrderSongResultDto orderSong;

    /**
     * 聊天室编号
     */
    private Long chatRoomId;

    /**
     * 操作人uuid
     */
    private String operatorUuid;

    private String attachment;

    public KtvOrderSongNotifyEventDto(OrderSongResultDto orderSong, Long chatRoomId, String operatorUuid) {
        this.orderSong = orderSong;
        this.chatRoomId = chatRoomId;
        this.operatorUuid = operatorUuid;
    }

    public KtvOrderSongNotifyEventDto(OrderSongResultDto orderSong, Long chatRoomId, String operatorUuid, String attachment) {
        this(orderSong,chatRoomId,operatorUuid);
        if(StringUtils.isNotEmpty(attachment)) {
            this.attachment = attachment;
        }
    }


    public OrderSongResultDto getOrderSong() {
        return orderSong;
    }

    public void setOrderSong(OrderSongResultDto orderSong) {
        this.orderSong = orderSong;
    }

    public Long getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(Long chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getOperatorUuid() {
        return operatorUuid;
    }

    public void setOperatorUuid(String operatorUuid) {
        this.operatorUuid = operatorUuid;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}
