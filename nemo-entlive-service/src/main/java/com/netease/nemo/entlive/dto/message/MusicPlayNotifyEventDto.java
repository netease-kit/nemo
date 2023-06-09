package com.netease.nemo.entlive.dto.message;

import com.netease.nemo.entlive.dto.BasicUserDto;
import com.netease.nemo.entlive.dto.PlayDetailInfoDto;

public class MusicPlayNotifyEventDto {

    /**
     * 房间音乐播放信息
     */
    private PlayDetailInfoDto playMusicInfo;

    /**
     * 操作者
     */
    private BasicUserDto operatorInfo;

    public MusicPlayNotifyEventDto() {}

    public MusicPlayNotifyEventDto(PlayDetailInfoDto playMusicInfo, BasicUserDto operatorInfo) {
        this.playMusicInfo = playMusicInfo;
        this.operatorInfo = operatorInfo;
    }

    public MusicPlayNotifyEventDto(BasicUserDto operatorInfo) {
        this.operatorInfo = operatorInfo;
    }

    public PlayDetailInfoDto getPlayMusicInfo() {
        return playMusicInfo;
    }

    public void setPlayMusicInfo(PlayDetailInfoDto playMusicInfo) {
        this.playMusicInfo = playMusicInfo;
    }

    public BasicUserDto getOperatorInfo() {
        return operatorInfo;
    }

    public void setOperatorInfo(BasicUserDto operatorInfo) {
        this.operatorInfo = operatorInfo;
    }
}
