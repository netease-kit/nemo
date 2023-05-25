package com.netease.nemo.openApi.dto.antispam;

import com.google.common.collect.Maps;

import java.util.Map;

public class AntispamLabel {
    private static Map<Integer, String> map;
    static {
        map = Maps.newHashMap();
        map.put(100,"色情");
        map.put(110,"性感");
        map.put(200,"广告");
        map.put(210,"二维码");
        map.put(260,"广告法");
        map.put(300,"暴恐");
        map.put(400,"违禁");
        map.put(500,"涉政");
        map.put(600,"谩骂");
        map.put(800,"恶心类");
        map.put(900,"其他");
        map.put(1020,"黑屏");
        map.put(1030,"挂机");
        map.put(1100,"涉价值观");
    }
    public static String get(Integer key){
        return map.get(key);
    }
}
