package com.netease.nemo.entlive.service.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonObject;
import com.netease.nemo.entlive.imnotify.ImMemberInOutMsgCallbackParam;
import com.netease.nemo.entlive.mapper.LiveRecordMapper;
import com.netease.nemo.entlive.model.po.LiveRecord;
import com.netease.nemo.entlive.service.NeRoomMemberService;
import com.netease.nemo.locker.LockerService;
import com.netease.nemo.util.gson.GsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Service
public class ImEventServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(ImEventServiceImpl.class);

    /**
     * 抄送消息处理锁前缀
     */
    private static final String CHATROOM_INOUT_LOCK_PREFIX = "lock:chatroom:inout:";

    @Resource(name = "redisDistributeLockerImpl")
    private LockerService lockerService;

    @Resource(name = "nemoRedisTemplate")
    private RedisTemplate<String, Object> nemoRedisTemplate;

    @Resource
    private NeRoomMemberService neRoomMemberService;

    @Resource
    private LiveRecordMapper liveRecordMapper;

    /**
     * 使用聊天室消息抄送事件维护成员在线人数,DEMO实现，具体请结合自己的业务场景进行处理
     * @param body 抄送消息体
     */
    public void handlerChatroomInOut(JsonObject body) {
        ImMemberInOutMsgCallbackParam param = GsonUtil.fromJson(body, new TypeToken<ImMemberInOutMsgCallbackParam>() {
        }.getType());

        if (param == null || StringUtils.isEmpty(param.getRoomId()) ||
                StringUtils.isEmpty(param.getAccid()) || StringUtils.isEmpty(param.getEvent()) ||
                param.getTimestamp() == null) {
            log.warn("Invalid chatroom in/out message: {}", body);
            return;
        }

        Long chatRoomId = Long.parseLong(param.getRoomId());
        String accid = param.getAccid();
        String event = param.getEvent();
        long timestamp = param.getTimestamp();
        LiveRecord liveRecord = liveRecordMapper.selectByChatRoomId(chatRoomId);
        if(liveRecord == null || Objects.equals(accid, liveRecord.getUserUuid())){
            log.info("ignore live owner in/out event, chatRoomId: {}, accid: {}", chatRoomId, accid);
            return;
        }

        // 对单个用户的进出操作加锁，避免并发问题
        String lockKey = CHATROOM_INOUT_LOCK_PREFIX + chatRoomId + ":" + accid;

        lockerService.tryLockAndDo(() -> {
            try {
                if ("IN".equalsIgnoreCase(event)) {
                    neRoomMemberService.handleUserEnter(chatRoomId, accid, timestamp);
                } else if ("OUT".equalsIgnoreCase(event)) {
                    neRoomMemberService.handleUserLeave(chatRoomId, accid, timestamp);
                } else {
                    log.warn("Unknown event type: {}, chatRoomId: {}, accid: {}", event, chatRoomId, accid);
                }
            } catch (Exception e) {
                log.error("Failed to handle chatroom in/out event", e);
            }
        }, lockKey, 5000L);
    }

}
