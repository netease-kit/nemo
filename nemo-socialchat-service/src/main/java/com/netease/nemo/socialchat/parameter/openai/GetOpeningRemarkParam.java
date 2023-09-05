package com.netease.nemo.socialchat.parameter.openai;

import lombok.Data;

import java.util.List;

@Data
public class GetOpeningRemarkParam {
    /**
     * 上下文
     */
    private List<String> context;
    /**
     * 是否下一批
     */
    private Boolean next;
}
