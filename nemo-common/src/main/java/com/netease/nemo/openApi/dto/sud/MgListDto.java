package com.netease.nemo.openApi.dto.sud;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author：CH
 * @Date：2023/8/22 4:00 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MgListDto {

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
        @JsonProperty("mg_info_list")
        private List<MgInfo> mgInfoList;
    }
}
