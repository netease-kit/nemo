package com.netease.nemo.entlive.dto;


import com.netease.nemo.entlive.model.po.OrderSong;
import lombok.Data;

/**
 * 演唱详情对象
 */
@Data
public class SingDetailInfoDto extends SingBaseInfoDto {
    /**
     * 主唱昵称
     * **/
    private String userName;

    /**
     * 主唱头像
     * **/
    private String icon;

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
     * 演唱模式，0：智能合唱， 1: 串行合唱，2：NTP实时合唱，3：独唱，默认智能合唱
     * **/
    private Integer singMode;

    /**
     * 合唱的类型，1:串行合唱（默认） 2:实时合唱
     */
    private Integer chorusType;

    /**
     * 合唱状态，0：邀请中 1：合唱中，2: 拒绝 3：取消 4：合唱结束 5：同意合唱  6：合唱准备完成
     * **/
    private Integer chorusStatus;

    /**
     * 歌曲时长
     */
    private Long songTime;

    /**
     * 版权来源：1：云音乐 2、咪咕
     */
    private Integer channel;

    public SingDetailInfoDto() {
    }

    public void setOrderSongInfo(OrderSong orderSong) {
        if (orderSong != null) {
            this.setOrderId(orderSong.getId());
            this.setSongName(orderSong.getSongName());
            this.setSongCover(orderSong.getSongCover());
            this.setSongId(orderSong.getSongId());
            this.setChannel(orderSong.getChannel());
            this.setSinger(orderSong.getSinger());
            this.setSingerCover(orderSong.getSingerCover());
            this.setSongTime(orderSong.getSongTime());
        }
    }

    public void setOrderSongInfo(OrderSongResultDto resultDto) {
        if (resultDto != null) {
            OrderSongDto orderSong = resultDto.getOrderSong();
            this.setChannel(orderSong.getChannel());
            this.setOrderId(orderSong.getId());
            this.setSongId(orderSong.getSongId());
            this.setUserUuid(orderSong.getUserUuid());
            this.setSongName(orderSong.getSongName());
            this.setSongCover(orderSong.getSongCover());
            this.setSinger(orderSong.getSinger());
            this.setSingerCover(orderSong.getSingerCover());
            this.setSongTime(orderSong.getSongTime());

            BasicUserDto orderSongUser = resultDto.getOrderSongUser();
            this.setUserName(orderSongUser.getUserName());
            this.setIcon(orderSongUser.getIcon());
        }
    }
}
