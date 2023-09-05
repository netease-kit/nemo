package com.netease.nemo.socialchat.parameter.openai;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class ChatReplyParam {

    /**
     * 对方消息
     */
    @NotEmpty
    private String msg;
    /**
     * 上下文
     */
    private List<String> context;
    /**
     * 是否下一批
     */
    private Boolean next;
}
