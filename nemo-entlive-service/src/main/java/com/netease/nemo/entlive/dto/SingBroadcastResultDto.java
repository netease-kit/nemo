package com.netease.nemo.entlive.dto;

import lombok.Data;

@Data
public class SingBroadcastResultDto {

    /**
     * 演唱信息
     */
    private SingDetailInfoDto singInfo;

    /**
     * 操作人
     **/
    private BasicUserDto operator;

    public SingBroadcastResultDto() {
    }
}
