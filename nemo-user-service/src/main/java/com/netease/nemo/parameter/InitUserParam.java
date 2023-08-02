package com.netease.nemo.parameter;

import lombok.Data;

@Data
public class InitUserParam {
    private String userName;
    // 1：表示1V1, 2表示 语聊房
    private Integer sceneType;
    private String userUuid;
    private String imToken;
    private String icon;


    // 生成InitUserParam对象的json
    // {"userName":"test","sceneType":1,"userUuid":"test","imToken":"test","icon":"test"}

}
