package com.netease.nemo.socialchat.parameter.openai;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class GetTopicRecommendParam {
    @NotNull
    private Integer topicType;

    /**
     * 上下文
     */
    private List<String> context;
    /**
     * 是否下一批
     */
    private Boolean next;
}
