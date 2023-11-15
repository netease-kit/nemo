package com.netease.nemo.game.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：CH
 * @Date：2023/8/14 8:23 PM
 */
@Data
@NoArgsConstructor
public class BaseResult {
    private boolean isSuccess;
    private int errorCode;
    private String errorMsg;
}
