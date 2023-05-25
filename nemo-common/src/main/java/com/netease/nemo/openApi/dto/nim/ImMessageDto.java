package com.netease.nemo.openApi.dto.nim;

public class ImMessageDto {
    private String msgid;
    private String timetag;
    private String antispam;

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getTimetag() {
        return timetag;
    }

    public void setTimetag(String timetag) {
        this.timetag = timetag;
    }

    public String getAntispam() {
        return antispam;
    }

    public void setAntispam(String antispam) {
        this.antispam = antispam;
    }
}
