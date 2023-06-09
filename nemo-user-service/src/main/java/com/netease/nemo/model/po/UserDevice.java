package com.netease.nemo.model.po;

import java.util.Date;

/**
 * 数据库表 : user_device
 * 
 * @author : auto generated by mybatis generator
 */
public class UserDevice {
    /**
     * ID对应rtc房间的rtcUid
     */
    private Long id;

    /**
     * 用户唯一号
     */
    private String userUuid;

    /**
     * 设备号uuid
     */
    private String deviceId;

    /**
     * 记录创建时间
     */
    private Date createTime;

    /**
     * 记录更新时间
     */
    private Date updateTime;


    public UserDevice(String userUuid, String deviceId) {
        this.userUuid = userUuid;
        this.deviceId = deviceId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid == null ? null : userUuid.trim();
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId == null ? null : deviceId.trim();
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
}