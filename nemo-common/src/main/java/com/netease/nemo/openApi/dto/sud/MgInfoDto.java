package com.netease.nemo.openApi.dto.sud;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：CH
 * @Date：2023/8/30 3:05 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MgInfoDto {
    /**
     * 响应码
     */
    @JsonProperty("ret_code")
    private int retCode;

    /**
     * 响应消息
     */
    @JsonProperty("ret_msg")
    private String retMsg;

    /**
     * 业务响应数据
     */
    @JsonProperty("data")
    private SudData data;

    @Data
    public static class SudData {
        @JsonProperty("mg_info")
        private MgInfo mgInfo;
    }
}
