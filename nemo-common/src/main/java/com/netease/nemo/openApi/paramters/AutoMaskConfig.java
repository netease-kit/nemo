package com.netease.nemo.openApi.paramters;

import lombok.Data;

@Data
public class AutoMaskConfig {
    private Boolean enableMask;
    private Integer maskType;
    private Integer duration;
}
