package com.netease.nemo.entlive.parameter;

import lombok.Data;

/**
 * 合唱时，主副唱歌曲资源下载完成对象
 *
 * @Author：CH
 * @Date：2023/10/11 13:06 AM
 */
@Data
public class FinishChorusReadyParam {
    /**
     * NeRoom房间编号
     */
    private String roomUuid;

    /**
     * 合唱编号
     */
    private String chorusId;
}
