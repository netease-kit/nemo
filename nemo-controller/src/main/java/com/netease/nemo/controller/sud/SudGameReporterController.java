package com.netease.nemo.controller.sud;

import com.google.gson.JsonObject;
import com.netease.nemo.annotation.SudRestResponseBody;
import com.netease.nemo.annotation.SudSignature;
import com.netease.nemo.game.service.GameReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author：CH
 * @Date：2023/9/7 1:58 PM
 */
@RequestMapping("/nemo/game/sud/user/")
@Slf4j
@SudRestResponseBody
@RestController
public class SudGameReporterController {

    @Resource
    private GameReportService gameReportService;

    /**
     * 忽然游戏上报
     * 调用方：Sud-server 游戏服务
     *
     * @param reportGameInfo 上报游戏信息
     * @return
     */
    @PostMapping("/report_game_info")
    @SudSignature
    public Object reportGameInfo(@RequestBody String reportGameInfo) {
        JsonObject result = new JsonObject();
        try {
            if (StringUtils.isEmpty(reportGameInfo)) {
                log.error("SudGameReporterController : 游戏上报内容为空");
                result.addProperty("code", 414);
                return result;
            }
            gameReportService.reportGameInfo(reportGameInfo);
            result.addProperty("ret_code", 0);
            return result;
        } catch (Exception ex) {
            log.error("Nim Callback Process exception.", ex);
            result.addProperty("code", 414);
            return result;
        }
    }
}
