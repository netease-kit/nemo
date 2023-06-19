package com.netease.nemo.controller.entertainmentLive;

import com.netease.nemo.annotation.RestResponseBody;
import com.netease.nemo.annotation.TokenAuth;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.context.Context;
import com.netease.nemo.entlive.enums.MusicPlayerActionEnum;
import com.netease.nemo.entlive.enums.MusicPlayerStatusEnum;
import com.netease.nemo.entlive.parameter.MusicActionParam;
import com.netease.nemo.entlive.parameter.MusicReadyParam;
import com.netease.nemo.entlive.service.MusicPlayService;
import com.netease.nemo.enums.RedisKeyEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.locker.LockerService;
import com.netease.nemo.util.AssertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.websocket.server.PathParam;

/**
 * 歌曲播放API
 *
 * @Author：CH
 * @Date：2023/5/31 3:56 下午
 */
@RestController
@RequestMapping("/nemo/entertainmentLive/music/")
@Slf4j
@RestResponseBody
@TokenAuth
public class MusicPlayerController {

    @Resource
    private MusicPlayService musicPlayService;

    @Resource(name = "redisDistributeLockerImpl")
    private LockerService lockerService;

    @GetMapping(value = "/info")
    public Object getPlayMusicInfo(@PathParam("liveRecordId") Long liveRecordId) {
        return musicPlayService.getPlayMusicInfo(liveRecordId);
    }

    @PostMapping(value = "/action")
    public void musicAction(@Valid @RequestBody MusicActionParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Null");
        if (!MusicPlayerActionEnum.checkMusicPlayerAction(param.getAction())) {
            throw new BsException(ErrorCode.BAD_REQUEST, "action enum error");
        }

        String userUuid = Context.get().getUserUuid();
        lockerService.tryLockAndDo(() -> musicPlayService.musicAction(userUuid, param),
                RedisKeyEnum.ENT_SONG_ORDER, param.getLiveRecordId());
    }


    @PostMapping(value = "/ready")
    public void musicReady(@Valid @RequestBody MusicReadyParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Null");
        AssertUtil.notNull(param.getLiveRecordId(), ErrorCode.BAD_REQUEST, "Missing Required Parameter: liveRecordId");
        AssertUtil.notNull(param.getOrderId(), ErrorCode.BAD_REQUEST, "Missing Required Parameter: orderId");

        lockerService.tryLockAndDo(() -> musicPlayService.musicReady(param.getLiveRecordId(), param.getOrderId()),
                RedisKeyEnum.ENT_SONG_ORDER.getKeyPrefix(), param.getLiveRecordId());
    }
}
