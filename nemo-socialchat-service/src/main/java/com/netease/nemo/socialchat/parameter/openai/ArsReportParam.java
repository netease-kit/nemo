package com.netease.nemo.socialchat.parameter.openai;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ArsReportParam {
    @NotBlank
    private String roomUuid;
    @NotBlank
    private String userUuid;
    @NotNull
    private Long reportTime;
    @NotBlank
    private String content;
}
