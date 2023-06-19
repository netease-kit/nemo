package com.netease.nemo.controller.entertainmentLive;

import com.netease.nemo.annotation.RestResponseBody;
import com.netease.nemo.annotation.TokenAuth;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.context.Context;
import com.netease.nemo.entlive.dto.OrderSongDto;
import com.netease.nemo.entlive.dto.TokenDto;
import com.netease.nemo.entlive.enums.MusicChannelEnum;
import com.netease.nemo.entlive.parameter.CleanOrderSongParam;
import com.netease.nemo.entlive.parameter.OrderParam;
import com.netease.nemo.entlive.parameter.OrderSongParam;
import com.netease.nemo.entlive.parameter.SwitchSongParam;
import com.netease.nemo.entlive.service.OrderSongService;
import com.netease.nemo.entlive.util.TokenUtils;
import com.netease.nemo.enums.RedisKeyEnum;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.locker.LockerService;
import com.netease.nemo.util.AssertUtil;
import com.netease.nemo.util.ObjectMapperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/nemo/entertainmentLive/live/song/")
@Slf4j
@RestResponseBody
@TokenAuth
public class SongController {

    @Resource
    private ModelMapper modelMapper;

    @Resource(name = "redisDistributeLockerImpl")
    private LockerService lockerService;

    @Resource
    private OrderSongService orderSongService;

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    @PostMapping(value = "/orderSong")
    public Object orderSong(@Valid @RequestBody OrderSongParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Be Null");
        AssertUtil.notNull(param.getLiveRecordId(), ErrorCode.BAD_REQUEST, "param : LiveRecordId Not Be Null");
        AssertUtil.notNull(MusicChannelEnum.fromCode(param.getChannel()), ErrorCode.BAD_REQUEST, "The Music channel Can Not Be Null");

        if (StringUtils.isAnyEmpty(param.getSongId())) {
            throw new BsException(ErrorCode.BAD_REQUEST, "param : SongId Not Be Null");
        }

        OrderSongDto orderSongDto = ObjectMapperUtil.map(param, OrderSongDto.class);
        orderSongDto.setUserUuid(Context.get().getUserUuid());

        return lockerService.tryLockAndDoAndReturn(
                () -> orderSongService.orderSong(orderSongDto),
                RedisKeyEnum.ENT_SONG_ORDER.getKeyPrefix(), param.getLiveRecordId());
    }

    @PostMapping(value = "/switchSong")
    public void switchSong(@Valid @RequestBody SwitchSongParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Be Null");
        AssertUtil.notNull(param.getCurrentOrderId(), ErrorCode.BAD_REQUEST, "The CurrentOrderId Can Not Null");
        AssertUtil.notNull(param.getLiveRecordId(), ErrorCode.BAD_REQUEST, "The LiveRecordId Can Not Null");

        lockerService.tryLockAndDo(
                () -> orderSongService.orderSongSwitch(param.getLiveRecordId(), Context.get().getUserUuid(), param.getCurrentOrderId(), param.getAttachment()),
                RedisKeyEnum.ENT_SONG_ORDER.getKeyPrefix(), param.getLiveRecordId());
    }

    @GetMapping(value = "/getOrderSongs")
    public Object orderSongs(@RequestParam("liveRecordId") Long liveRecordId) {
        return orderSongService.getOrderSongs(liveRecordId);
    }

    @PostMapping(value = "/cancelOrderSong")
    public void cancelOrderSong(@Valid @RequestBody OrderParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Be Null");
        AssertUtil.notNull(param.getOrderId(), ErrorCode.BAD_REQUEST, "The OrderId Can Not Null");

        lockerService.tryLockAndDo(
                () -> orderSongService.cancelOrderSong(param.getLiveRecordId(), Context.get().getUserUuid(), param.getOrderId()),
                RedisKeyEnum.ENT_SONG_ORDER.getKeyPrefix(), param.getLiveRecordId());
    }

    @PostMapping(value = "/songSetTop")
    public void songSetTop(@Valid @RequestBody OrderParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Be  Null");
        AssertUtil.notNull(param.getOrderId(), ErrorCode.BAD_REQUEST, "The orderId Can Not Be Null");
        AssertUtil.notNull(param.getLiveRecordId(), ErrorCode.BAD_REQUEST, "The LiveRecordId Can Not Be Null");

        lockerService.tryLockAndDo(
                () -> orderSongService.songSetTop(param.getLiveRecordId(), Context.get().getUserUuid(), param.getOrderId()),
                RedisKeyEnum.ENT_SONG_ORDER.getKeyPrefix(), param.getLiveRecordId());
    }

    @PostMapping(value = "/cleanUserOrderSongs")
    public void cleanUserOrderSongs(@Valid @RequestBody CleanOrderSongParam param) {
        AssertUtil.notNull(param, ErrorCode.BAD_REQUEST, "The Request Body Can Not Be  Null");
        AssertUtil.notNull(param.getLiveRecordId(), ErrorCode.BAD_REQUEST, "The liveRecordId Can Not Be Null");

        lockerService.tryLockAndDo(
                () -> orderSongService.cleanUserOrderSongs(param.getLiveRecordId(), Context.get().getUserUuid()),
                RedisKeyEnum.ENT_SONG_ORDER.getKeyPrefix(), param.getLiveRecordId());
    }

    @PostMapping(value = "/getMusicToken")
    public Object getMusicToken() {
        Long ttl = 21600L;
        String token = TokenUtils.generate(yunXinConfigProperties.getAppKey(), yunXinConfigProperties.getAppSecret(), Context.get().getUserUuid(), ttl);
        return new TokenDto(token, ttl);
    }
}
