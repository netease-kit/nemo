package com.netease.nemo.entlive.dto;

import com.netease.nemo.dto.UserDto;
import lombok.Data;

@Data
public class BasicUserDto {

    /**
     * 用户编号
     */
    private String userUuid;

    /**
     * 昵称
     */
    private String userName;

    /**
     * 头像地址
     */
    private String icon;


    public BasicUserDto() {
    }

    public static BasicUserDto buildBasicUser(UserDto userDto) {
        BasicUserDto basicUserDto = new BasicUserDto();
        basicUserDto.setUserUuid(userDto.getUserUuid());

        basicUserDto.setUserName(userDto.getUserName());
        basicUserDto.setIcon(userDto.getIcon());

        return basicUserDto;

    }
}
