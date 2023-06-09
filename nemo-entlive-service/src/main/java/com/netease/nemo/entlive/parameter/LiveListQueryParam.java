package com.netease.nemo.entlive.parameter;


import lombok.Data;

/**
 * 直播列表查询参数
 */
@Data
public class LiveListQueryParam {
    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 页大小
     */
    private Integer pageSize;

    /**
     * 直播类型 {@link com.netease.nemo.entlive.enums.LiveTypeEnum}
     */
    private Integer liveType;
}
