package com.netease.nemo.dto;

import com.netease.nemo.model.po.User;
import com.netease.nemo.model.po.UserDevice;
import com.netease.nemo.util.ObjectMapperUtil;
import lombok.Data;

@Data
public class UserDto {
    private String userUuid;
    private String mobile;
    private String userName;
    private String icon;
    private Integer sex;
    private String userToken;
    private String imToken;
    private Long rtcUid;

    private String audioUrl;
    private String videoUrl;
    private int callType;
    /**
     * 是否进行了实名认证，true=已做 false=没有
     */
    private Boolean realNameAuth;
    public static UserDto build(User user, UserDevice userDevice) {
        UserDto userDto = ObjectMapperUtil.map(user, UserDto.class);
        userDto.setRtcUid(userDevice.getId());
        return userDto;
    }

    public static UserDto build(User user) {
        UserDto userDto = ObjectMapperUtil.map(user, UserDto.class);
        return userDto;
    }
}
