package com.netease.nemo.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：CH
 * @Date：2023/9/4 9:46 PM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SudLoginDto {
    private String code;
    private String appId;
    private String appKey;
}
