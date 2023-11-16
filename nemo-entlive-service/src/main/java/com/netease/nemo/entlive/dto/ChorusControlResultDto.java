package com.netease.nemo.entlive.dto;


import com.netease.nemo.entlive.model.po.OrderSong;
import lombok.Data;

@Data
public class ChorusControlResultDto {

    /**
     * 合唱 编号
     */
    private String chorusId;

    /**
     * 应用编号
     */
    private String appKey;

    /**
     * 虚拟房间编号
     */
    private String roomUuid;

    /**
     * 房间名称
     * **/
    private String roomName;

    /**
     * 点歌编号
     */
    private Long orderId;

    /**
     * 直播记录编号
     * **/
    private Long liveRecordId;

    /**
     * 直播主题
     */
    private String liveTopic;

    /**
     * 演唱模式，0：智能合唱， 1: 串行合唱，2：NTP实时合唱，3：独唱，默认智能合唱，KTV场景使用
     * **/
    private Integer singMode;

    /**
     * 主唱
     */
    private String userUuid;

    /**
     * 主唱昵称
     * **/
    private String userName;

    /**
     * 主唱头像
     * **/
    private String icon;

    /**
     * 副唱
     */
    private String assistantUuid;

    /**
     * 副唱昵称
     */
    private String assistantName;

    /**
     * 副唱头像
     */
    private String assistantIcon;

    /**
     * 歌曲名称
     * **/
    private String songName;

    /**
     * 歌曲封面
     * **/
    private String songCover;

    /**
     * 歌手名称
     */
    private String singer;

    /**
     * 歌手封面
     */
    private String singerCover;

    /**
     * 歌曲编号
     */
    private String songId;

    /**
     * 合唱的类型，1:串行合唱（默认） 2:实时合唱
     */
    private Integer chorusType;

    /**
     * 合唱状态，0：邀请中 1：合唱中，2: 拒绝 3：取消 4：合唱结束 5：加入合唱  6：合唱准备完成
     * **/
    private Integer chorusStatus;

    /**
     * 歌曲时长
     */
    private Long songTime;

    /**
     * 版权来源：1：云音乐 2、咪咕 3、hiHive
     */
    private Integer channel;

    public ChorusControlResultDto() {
    }

    public void setOrderSongInfo(OrderSong orderSong) {
        if(orderSong != null) {
            this.setSongName(orderSong.getSongName());
            this.setSongCover(orderSong.getSongCover());
            this.setSongId(orderSong.getSongId());
            this.setSinger(orderSong.getSinger());
            this.setSingerCover(orderSong.getSingerCover());
            this.setSongTime(orderSong.getSongTime());
            this.setChannel(orderSong.getChannel());
        }
    }
}
