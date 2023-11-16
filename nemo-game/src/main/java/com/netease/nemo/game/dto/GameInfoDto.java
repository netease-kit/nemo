package com.netease.nemo.game.dto;

import com.netease.nemo.game.util.GameResourceUtil;
import com.netease.nemo.openApi.dto.sud.MgInfo;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * 游戏信息对象
 *
 * @Author：CH
 * @Date：2023/8/23 7:26 PM
 */
@Data
public class GameInfoDto {
    /**
     * 游戏ID
     */
    private String gameId;
    /**
     * 游戏名称
     */
    private String gameName;
    /**
     * 游戏描述
     */
    private String gameDesc;
    /**
     * 游戏图标
     */
    private String thumbnail;
    /**
     * 游戏规则
     */
    private String rule;

    public static GameInfoDto from(MgInfo mgInfo, String languageCode) {
        GameInfoDto gameInfoDto = new GameInfoDto();
        gameInfoDto.setGameId(mgInfo.getMgId());
        gameInfoDto.setGameName(mgInfo.getName().get(languageCode));
        gameInfoDto.setThumbnail(mgInfo.getThumbnail80x80().get(languageCode));
        gameInfoDto.setGameDesc(mgInfo.getDesc().get(languageCode));
        String gameDesc = mgInfo.getDesc().get(languageCode);
        if (!StringUtils.isEmpty(GameResourceUtil.gameDescMap.get(mgInfo.getMgId()))) {
            gameDesc = GameResourceUtil.gameDescMap.get(mgInfo.getMgId());
        }
        gameInfoDto.setGameDesc(gameDesc);
        return gameInfoDto;
    }
}
