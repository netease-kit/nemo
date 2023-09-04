package com.netease.nemo.socialchat.parameter;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
public class GetUserStateParam {
    @NotEmpty(message = "{mobile.notNull}")
//    @Pattern(regexp = "^1[3456789]\\d{9}$", message = "{mobile.format.illegal}")
    private String mobile;
}
