package com.netease.nemo.socialchat.parameter.openai;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class GetSkillsWithContextParam {
    @NotEmpty
    private String context;
}
