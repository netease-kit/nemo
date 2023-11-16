package com.netease.nemo.ktv;

import com.netease.nemo.entlive.dto.LiveIntroDto;
import com.netease.nemo.game.dto.GameInfoDto;
import com.netease.nemo.game.dto.GameRoomInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author：CH
 * @Date：2023/8/31 4:47 PM
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestContext {
    private LiveIntroDto liveIntroDto;
    private List<GameInfoDto> gameInfoDtoList;
    private GameRoomInfoDto gameRoomInfoDto;
}
