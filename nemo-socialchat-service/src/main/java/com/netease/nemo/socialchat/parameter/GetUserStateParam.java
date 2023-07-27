package com.netease.nemo.socialchat.parameter;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class GetUserStateParam {
    @NotEmpty(message = "{mobile.notNull}")
    private String mobile;
}
