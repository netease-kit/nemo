package com.netease.nemo.entlive.dto;

import com.netease.nemo.entlive.enums.MusicPlayerStatusEnum;
import com.netease.nemo.entlive.model.po.OrderSong;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class PlayDetailInfoDto {

    /**
     * 点歌编号
     */
    private Long orderId;

    /**
     * 直播间编号
     **/
    private Long liveRecordId;

    /**
     * 状态 1:播放  2:暂停  3:歌曲下载准备中, 4:准备完成
     **/
    private Integer musicStatus;

    /**
     * NeRoom虚拟房间唯一编号
     */
    private String roomArchiveId;

    /**
     * 关联roomUuid
     */
    private String roomUuid;

    /**
     * 演唱的扩展信息
     **/
    private Map<String, Object> ext;


    /**
     * 歌曲编号
     */
    private String songId;

    /**
     * 歌曲名称
     */
    private String songName;

    /**
     * 歌曲封面
     */
    private String songCover;

    /**
     * 演唱者的名称
     */
    private String singer;

    /**
     * 歌曲时长
     */
    private Long songTime;

    /**
     * 演唱者头像
     */
    private String singerCover;

    /**
     * 版权来源：1：云音乐 2、咪咕
     */
    private Integer channel;

    public PlayDetailInfoDto() {}


    public PlayDetailInfoDto(OrderSong orderSong, MusicPlayerStatusEnum musicPlayerStatusEnum) {
        if(null != orderSong) {
            this.orderId = orderSong.getId();
            this.liveRecordId = orderSong.getLiveRecordId();
            this.roomUuid = orderSong.getRoomUuid();
            this.roomArchiveId = orderSong.getRoomArchiveId();
            this.songCover = StringUtils.isEmpty(orderSong.getSongCover()) ? "" : orderSong.getSongCover();
            this.singerCover = StringUtils.isEmpty(orderSong.getSingerCover()) ? "" : orderSong.getSingerCover();
            this.songName = StringUtils.isEmpty(orderSong.getSongName()) ? "" : orderSong.getSongName();
            this.singer = StringUtils.isEmpty(orderSong.getSinger()) ? "" : orderSong.getSinger();
            this.songId = StringUtils.isEmpty(orderSong.getSongId()) ? "" : orderSong.getSongId();
            this.songTime = orderSong.getSongTime();
            this.channel = orderSong.getChannel();
            this.musicStatus = musicPlayerStatusEnum.getStatus();
        }
    }


    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getRoomUuid() {
        return roomUuid;
    }

    public void setRoomUuid(String roomUuid) {
        this.roomUuid = roomUuid;
    }

    public Map<String, Object> getExt() {
        return ext;
    }

    public void setExt(Map<String, Object> ext) {
        this.ext = ext;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongCover() {
        return songCover;
    }

    public void setSongCover(String songCover) {
        this.songCover = songCover;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSingerCover() {
        return singerCover;
    }

    public void setSingerCover(String singerCover) {
        this.singerCover = singerCover;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Long getSongTime() {
        return songTime;
    }

    public void setSongTime(Long songTime) {
        this.songTime = songTime;
    }

    public Long getLiveRecordId() {
        return liveRecordId;
    }

    public void setLiveRecordId(Long liveRecordId) {
        this.liveRecordId = liveRecordId;
    }

    public Integer getMusicStatus() {
        return musicStatus;
    }

    public void setMusicStatus(Integer musicStatus) {
        this.musicStatus = musicStatus;
    }

    public String getRoomArchiveId() {
        return roomArchiveId;
    }

    public void setRoomArchiveId(String roomArchiveId) {
        this.roomArchiveId = roomArchiveId;
    }

}
