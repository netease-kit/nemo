package com.netease.nemo.game.service.impl;

import com.google.gson.JsonObject;
import com.netease.nemo.game.dto.GameSettleDto;
import com.netease.nemo.game.dto.GameStartDto;
import com.netease.nemo.game.enums.GameReportTypeEnum;
import com.netease.nemo.game.mapper.GameRecordMapper;
import com.netease.nemo.game.mapper.GameReportMapper;
import com.netease.nemo.game.model.po.GameRecord;
import com.netease.nemo.game.model.po.GameReport;
import com.netease.nemo.game.service.GameReportService;
import com.netease.nemo.game.service.GameService;
import com.netease.nemo.game.wrapper.GameMemberMapperWrapper;
import com.netease.nemo.game.wrapper.GameRecordMapperWrapper;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * 忽然游戏上报处理
 *
 * @Author：CH
 * @Date：2023/9/7 2:31 PM
 */
@Service
@Slf4j
public class GameReportServiceImpl implements GameReportService {

    @Resource
    private GameService gameService;
    @Resource
    private GameReportMapper gameReportMapper;

    @Resource
    private GameRecordMapper gameRecordMapper;

    @Resource
    private GameRecordMapperWrapper gameRecordMapperWrapper;

    @Resource
    private GameMemberMapperWrapper gameMemberMapperWrapper;


    @Override
    public void reportGameInfo(String reportGameInfo) {
        if (StringUtils.isEmpty(reportGameInfo)) {
            log.error("GameReportServiceImpl : 游戏上报内容为空");
            return;
        }

        JsonObject jsonObject = GsonUtil.parseJsonObject(reportGameInfo);
        String reportType = GsonUtil.getString(jsonObject, "report_type");
        GameReportTypeEnum gameReportTypeEnum = GameReportTypeEnum.fromCode(reportType);
        if (gameReportTypeEnum == null) {
            log.error("GameReportServiceImpl : 游戏上报类型错误");
            return;
        }

        GameReport gameReport = new GameReport();
        switch (gameReportTypeEnum) {
            case GAME_START:
                log.info("GameReportServiceImpl : 游戏开始上报, reportGameInfo:{}", reportGameInfo);
                GameStartDto gameStartDto = GsonUtil.fromJson(GsonUtil.getJsonObject(jsonObject, "report_msg"), GameStartDto.class);
                if (!StringUtils.isEmpty(gameStartDto.getReportGameInfoKey())) {
                    Long gameRecordId = Long.parseLong(gameStartDto.getReportGameInfoKey());
                    gameReport.setGameRecordId(gameRecordId);
                    gameReport.setReportMsg(reportGameInfo);
                    gameReportMapper.insertSelective(gameReport);
                }
                break;

            case GAME_SETTLE:
                log.info("GameReportServiceImpl : 游戏结算上报 , reportGameInfo:{}", reportGameInfo);
                GameSettleDto gameSettleDto = GsonUtil.fromJson(GsonUtil.getJsonObject(jsonObject, "report_msg"), GameSettleDto.class);
                if (!StringUtils.isEmpty(gameSettleDto.getReportGameInfoKey())) {
                    Long gameRecordId = Long.parseLong(gameSettleDto.getReportGameInfoKey());
                    gameReport.setGameRecordId(gameRecordId);
                    gameReport.setReportMsg(reportGameInfo);
                    GameRecord gameRecord = gameRecordMapper.selectByPrimaryKey(gameRecordId);

                    gameReportMapper.insertSelective(gameReport);

                    gameMemberMapperWrapper.deleteGameMember(gameRecord);
                    gameRecordMapperWrapper.deleteGameRecord(gameRecord);
                }
                break;
            default:
                log.error("GameReportServiceImpl : 游戏上报类型错误");
                break;
        }
    }
}
