package com.netease.nemo.parameter;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class LoginParam {
    @NotBlank(message = "{userUuid.notNull}")
    @Size(max = 64, message = "{userUuid.length.Exceed}")
    private String userUuid;
    private String mobile;
    @NotBlank(message = "{deviceId.notNull}")
    @Size(max = 64, message = "{deviceId.length.Exceed}")
    private String deviceId;
    private String userName;
    private String icon;
    private Integer sex;
    private Integer age;

    /**
     * 是否创建IM账户，如果userUuid在IM中已存在则传false
     */
    private Boolean isCreateImAccId;

    /**
     * 是否添加云信派对
     */
    private Boolean isAddFriend;
}
