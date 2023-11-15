package com.netease.nemo.game.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取用户信息响应
 * 注意：响应体中的字段命名格式为SNAKE_CASE，需配置spring.jackson.property-naming-strategy=SNAKE_CASE
 *
 * @author Sud
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GetUserInfoResp {

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

}


