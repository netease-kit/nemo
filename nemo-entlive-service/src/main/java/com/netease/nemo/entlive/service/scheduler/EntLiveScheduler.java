package com.netease.nemo.entlive.service.scheduler;

import com.netease.nemo.context.Context;
import com.netease.nemo.util.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * 娱乐直播房间定时任务
 *
 * @Author：CH
 * @Date：2023/5/22 7:29 上午
 */
@Service
@Slf4j
public class EntLiveScheduler {

    @Scheduled(cron = "0 */2 * * * ?")
    public void cleanLiveRoom() {
        Context context = Context.init(UUIDUtil.getUUID());
        long now = System.currentTimeMillis();

        MDC.put("traceId", context.getTraceId());

        MDC.clear();
    }

}
