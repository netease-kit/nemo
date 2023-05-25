package com.netease.nemo.parameter;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UpdateUserParam {
    @NotBlank(message = "{userUuid.notNull}")
    @Size(max = 64, message = "{userUuid.length.Exceed}")
    private String userUuid;
    private String userName;
    private String icon;
    private Integer sex;
    private Integer age;
}
