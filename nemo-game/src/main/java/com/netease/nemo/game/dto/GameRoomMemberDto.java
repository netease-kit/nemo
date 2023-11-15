package com.netease.nemo.game.dto;

import lombok.Data;

/**
 * 游戏房间成员信息对象
 *
 * @Author：CH
 * @Date：2023/8/23 7:26 PM
 */
@Data
public class GameRoomMemberDto {
    /**
     * 游戏ID
     */
    private String gameId;
    /**
     * 游戏名称
     */
    private String gameName;

    /**
     * appKey
     */
    private String appKey;

    /**
     * 游戏房间roomUuid
     */
    private String roomUuid;

    /**
     * roomArchiveId
     */
    private String roomArchiveId;

    /**
     * liveRecordId
     */
    private Long liveRecordId;

    /**
     * userUuid
     */
    private String userUuid;

    /**
     * userName
     */
    private String userName;

    /**
     * icon
     */
    private String icon;

    /**
     * status
     */
    private Integer status;
}
