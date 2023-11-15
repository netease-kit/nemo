package com.netease.nemo.entlive.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author：CH
 * @Date：2023/9/7 4:01 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameRoomCloseEvent {
    private String roomUuid;
}

