package com.netease.nemo.entlive.dto;


public class ChorusBroadcastResultDto {

    /**
     * 合唱信息
     */
    private ChorusControlResultDto chorusInfo;

    /**
     * 操作人
     **/
    private BasicUserDto operator;

    public ChorusBroadcastResultDto() {
    }

    public ChorusBroadcastResultDto(ChorusControlResultDto chorusInfo, BasicUserDto operator) {
        this.chorusInfo = chorusInfo;
        this.operator = operator;
    }

    public BasicUserDto getOperator() {
        return operator;
    }

    public void setOperator(BasicUserDto operator) {
        this.operator = operator;
    }

    public ChorusControlResultDto getChorusInfo() {
        return chorusInfo;
    }

    public void setChorusInfo(ChorusControlResultDto chorusInfo) {
        this.chorusInfo = chorusInfo;
    }
}
