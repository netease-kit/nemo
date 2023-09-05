package com.netease.nemo.openApi.paramters.neroom.v1;

import org.apache.commons.lang3.BooleanUtils;

public class RoomResourceConfig {
    private Boolean rtc;
    private Boolean chatroom;
    private Boolean live;
    private Boolean whiteboard;
    private Boolean record;
    private Boolean sip;
    private Boolean seat;

    public RoomResourceConfig() {
    }
    public RoomResourceConfig(RoomResourceConfig roomResourceConfig) {
        if(roomResourceConfig != null){
            this.rtc = roomResourceConfig.getRtc();
            this.chatroom = roomResourceConfig.getChatroom();
            this.live = roomResourceConfig.getLive();
            this.whiteboard = roomResourceConfig.getWhiteboard();
            this.record = roomResourceConfig.getRecord();
            this.sip = roomResourceConfig.getSip();
            this.seat = roomResourceConfig.getSeat();
        }
    }

    public RoomResourceConfig(Boolean rtc, Boolean chatroom, Boolean live, Boolean whiteboard, Boolean record, Boolean sip, Boolean seat) {
        this(rtc, chatroom, live, whiteboard, record, sip);
        this.seat = seat;
    }

    public RoomResourceConfig(Boolean rtc, Boolean chatroom, Boolean live, Boolean whiteboard, Boolean record, Boolean sip) {
        this.rtc = rtc;
        this.chatroom = chatroom;
        this.live = live;
        this.whiteboard = whiteboard;
        this.record = record;
        this.sip = sip;
    }

    public static RoomResourceConfig buildEntRoomResourceConfig() {
        return new RoomResourceConfig(true, true, true, false, false, false,true);
    }

    public boolean copyIfCurrentIsNull(RoomResourceConfig resourceConfig) {
        boolean hasNewValue = false;
        if (resourceConfig != null) {
            if (this.rtc == null) {
                this.rtc = resourceConfig.getRtc();
            } else if (this.rtc != resourceConfig.getRtc()) {
                hasNewValue = true;
            }
            if (this.chatroom == null) {
                this.chatroom = resourceConfig.getChatroom();
            } else if (this.chatroom != resourceConfig.getChatroom()) {
                hasNewValue = true;
            }
            if (this.live == null) {
                this.live = resourceConfig.getLive();
            } else if (this.live != resourceConfig.getLive()) {
                hasNewValue = true;
            }
            if (this.whiteboard == null) {
                this.whiteboard = resourceConfig.getWhiteboard();
            } else if (this.whiteboard != resourceConfig.getWhiteboard()) {
                hasNewValue = true;
            }
            if (this.record == null) {
                this.record = resourceConfig.getRecord();
            } else if (this.record != resourceConfig.getRecord()) {
                hasNewValue = true;
            }
            if (this.sip == null) {
                this.sip = resourceConfig.getSip();
            } else if (this.sip != resourceConfig.getSip()) {
                hasNewValue = true;
            }
            if(this.seat == null) {
                this.seat = resourceConfig.getSeat();
            }else if(this.seat != resourceConfig.getSeat()) {
                hasNewValue = true;
            }
        }
        return hasNewValue;
    }

    public Boolean getRtc() {
        return rtc;
    }

    public Boolean getChatroom() {
        return chatroom;
    }

    public Boolean getLive() {
        return live;
    }

    public Boolean getWhiteboard() {
        return whiteboard;
    }

    public void setRtc(Boolean rtc) {
        this.rtc = rtc;
    }

    public void setChatroom(Boolean chatroom) {
        this.chatroom = chatroom;
    }

    public void setLive(Boolean live) {
        this.live = live;
    }

    public void setWhiteboard(Boolean whiteboard) {
        this.whiteboard = whiteboard;
    }

    public Boolean getRecord() {
        return record;
    }

    public void setRecord(Boolean record) {
        this.record = record;
    }

    public Boolean getSip() {
        return sip;
    }

    public void setSip(Boolean sip) {
        this.sip = sip;
    }

    public boolean rtc(){
        return BooleanUtils.isNotFalse(rtc);
    }
    public boolean chatroom(){
        return BooleanUtils.isNotFalse(chatroom);
    }
    public boolean live(){
        return BooleanUtils.isTrue(live);
    }
    public boolean whiteboard(){
        return BooleanUtils.isNotFalse(whiteboard);
    }
    public boolean record(){
        return BooleanUtils.isTrue(record);
    }
    public boolean sip(){
        return BooleanUtils.isTrue(sip);
    }
    public boolean seat() {
        return BooleanUtils.isTrue(seat);
    }

    public Boolean getSeat() {
        return seat;
    }
    public void setSeat(Boolean seat) {
        this.seat = seat;
    }
}
