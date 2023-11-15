package com.netease.nemo.game.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.netease.nemo.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：CH
 * @Date：2023/8/15 2:07 PM
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SudUserDto {
    /**
     * 用户ID，字段名：uid
     */
    @SerializedName("uid")
    private String uid;

    /**
     * 昵称，字段名：nick_name
     */
    @SerializedName("nick_name")
    private String nickName;

    /**
     * 头像地址（最优解为128*128），字段名：avatar_url
     */
    @SerializedName("avatar_url")
    private String avatarUrl;

    /**
     * 性别（男：male, 女：female），字段名：gender
     */
    @SerializedName("gender")
    private String gender;

    public SudUserDto(UserDto userDto) {
        this.uid = userDto.getUserUuid();
        this.nickName = userDto.getUserName();
        this.avatarUrl = userDto.getIcon();
        if (userDto.getSex() != null) {
            gender = userDto.getSex() == 1 ? "male" : "female";
        }
    }
}
