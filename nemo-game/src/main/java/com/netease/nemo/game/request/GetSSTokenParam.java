package com.netease.nemo.game.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 获取长期令牌请求
 * 注意：请求体中的字段命名格式为SNAKE_CASE，需配置spring.jackson.property-naming-strategy=SNAKE_CASE
 *
 * @author Sud
 */
@Data
public class GetSSTokenParam {
    /**
     * 短期令牌，字段名：code
     */
    @JsonProperty(value = "code")
    private String code;
}


