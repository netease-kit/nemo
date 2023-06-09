package com.netease.nemo.entlive.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    /**
     * 访问token
     */
    private String accessToken;
    /**
     * 过期时间，剩余时间
     */
    private Long expiresIn;
}
