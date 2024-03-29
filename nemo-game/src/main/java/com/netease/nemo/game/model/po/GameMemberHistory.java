package com.netease.nemo.game.model.po;

import java.util.Date;

/**
 * 数据库表 : game_member_history
 * 
 * @author : auto generated by mybatis generator
 */
public class GameMemberHistory {
    /**
     * 
     */
    private Long id;

    /**
     * 游戏唯一记录编号
     */
    private Long gameRecordId;

    /**
     * 直播唯一记录
     */
    private Long liveRecordId;

    /**
     * 虚拟房间编号
     */
    private String roomUuid;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 加入游戏时间
     */
    private Long joinTime;

    /**
     * 离开游戏时间
     */
    private Long exitTime;

    /**
     * 游戏成员状态0：准备中 1 游戏中 2 离开游戏
     */
    private Integer status;

    /**
     * 游戏Id
     */
    private String gameId;

    /**
     * 成员ID
     */
    private String userUuid;

    /**
     * 成员名词
     */
    private String userName;

    /**
     * 虚拟房间唯一编号
     */
    private String roomArchiveId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLiveRecordId() {
        return liveRecordId;
    }

    public void setLiveRecordId(Long liveRecordId) {
        this.liveRecordId = liveRecordId;
    }

    public String getRoomUuid() {
        return roomUuid;
    }

    public void setRoomUuid(String roomUuid) {
        this.roomUuid = roomUuid == null ? null : roomUuid.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Long joinTime) {
        this.joinTime = joinTime;
    }

    public Long getExitTime() {
        return exitTime;
    }

    public void setExitTime(Long exitTime) {
        this.exitTime = exitTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid == null ? null : userUuid.trim();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getRoomArchiveId() {
        return roomArchiveId;
    }

    public void setRoomArchiveId(String roomArchiveId) {
        this.roomArchiveId = roomArchiveId == null ? null : roomArchiveId.trim();
    }

    public Long getGameRecordId() {
        return gameRecordId;
    }

    public void setGameRecordId(Long gameRecordId) {
        this.gameRecordId = gameRecordId;
    }
}