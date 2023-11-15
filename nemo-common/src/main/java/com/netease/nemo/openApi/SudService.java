package com.netease.nemo.openApi;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.gson.JsonObject;
import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.openApi.dto.sud.ApiUrlConfigDto;
import com.netease.nemo.openApi.dto.sud.MgInfo;
import com.netease.nemo.openApi.dto.sud.MgListDto;
import com.netease.nemo.openApi.dto.sud.*;
import com.netease.nemo.openApi.dto.sud.enums.PushEventEnum;
import com.netease.nemo.openApi.dto.sud.event.GamePushEventDto;
import com.netease.nemo.util.UUIDUtil;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 忽然API service
 *
 * @Author：CH
 * @Date：2023/8/22 11:25 AM
 */
@Service
@Slf4j
public class SudService {

    @Value("${business.game.appId}")
    private String appId;

    @Value("${business.game.appSecret}")
    private String appSecret;

    @Value("${business.game.sudUrl}")
    private String sudUrl;

    @Resource
    private YunXinServer yunXinServer;

    private final Cache<String, Object> cache = Caffeine.newBuilder()
            .initialCapacity(200)//初始大小
            .maximumSize(500)//最大数量
            .expireAfterWrite(24, TimeUnit.HOURS)//过期时间
            .build();


    /**
     * 获取忽然API地址
     *
     * @return ApiUrlConfigDto
     */
    public ApiUrlConfigDto getSudAPI() {
        ApiUrlConfigDto apiUrlConfigDto;

        String cacheKey = StringUtils.join("GetSudAPI", appId);
        Object cacheResult = cache.getIfPresent(cacheKey);
        if (cacheResult != null) {
            apiUrlConfigDto = (ApiUrlConfigDto) cacheResult;
            return apiUrlConfigDto;
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json;charset=utf-8");
        HttpEntity entity = new HttpEntity(null, httpHeaders);
        String signature = new HmacUtils(HmacAlgorithms.HMAC_MD5, appSecret.getBytes()).hmacHex(appId);
        apiUrlConfigDto = yunXinServer.requestForEntity(sudUrl + signature, "", HttpMethod.GET, entity, ApiUrlConfigDto.class);
        if (apiUrlConfigDto == null) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "获取忽然游戏API地址失败");
        }
        cache.put(cacheKey, apiUrlConfigDto);
        return apiUrlConfigDto;
    }

    /**
     * 获取忽然游戏列表
     *
     * @return MgListDto
     */
    public List<MgInfo> gameList() {
        List<MgInfo> mgInfoList;

        String cacheKey = StringUtils.join("GetSudMgList", appId);
        Object cacheResult = cache.getIfPresent(cacheKey);
        if (cacheResult != null) {
            mgInfoList = (List<MgInfo>) cacheResult;
            return mgInfoList;
        }

        ApiUrlConfigDto apiUrlConfigDto = getSudAPI();
        String url = apiUrlConfigDto.getApi().getMgList();

        JsonObject body = new JsonObject();
        HttpEntity entity = buildEntity(body);

        MgListDto result = yunXinServer.requestForEntity("", url, HttpMethod.POST, entity, MgListDto.class);
        if (result == null) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "获取忽然游戏列表失败");
        }
        if (result.getRetCode() != 0) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "获取忽然游戏列表失败:" + result.getRetMsg());
        }

        mgInfoList = result.getData().getMgInfoList();
        cache.put(cacheKey, mgInfoList);
        return mgInfoList;
    }

    /**
     * 获取忽然游戏详细信息
     *
     * @return MgListDto
     */
    public MgInfo gameInfo(String mgId) {

        MgInfo mgInfo;

        String cacheKey = StringUtils.join("GetSudMgInfo", appId);
        Object cacheResult = cache.getIfPresent(cacheKey);
        if (cacheResult != null) {
            mgInfo = (MgInfo) cacheResult;
            return mgInfo;
        }

        ApiUrlConfigDto apiUrlConfigDto = getSudAPI();
        String url = apiUrlConfigDto.getApi().getMgInfo();

        JsonObject body = new JsonObject();
        body.addProperty("mg_id", mgId);
        HttpEntity entity = buildEntity(body);

        MgInfoDto result = yunXinServer.requestForEntity("", url, HttpMethod.POST, entity, MgInfoDto.class);
        if (result == null) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "获取忽然游戏信息失败");
        }
        if (result.getRetCode() != 0) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "获取忽然游戏信息失败:" + result.getRetMsg());
        }

        mgInfo = result.getData().getMgInfo();
        cache.put(cacheKey, mgInfo);
        return mgInfo;
    }


    /**
     * 服务器推送游戏事件
     *
     * @param event     事件 event
     * @param mgId      游戏mgId
     * @param timestamp 时间戳
     * @param data      事件数据
     */
    public void pushGameEvent(String event, String mgId, String timestamp, Object data) {
        ApiUrlConfigDto apiUrlConfigDto = getSudAPI();
        String url = apiUrlConfigDto.getApi().getPushEvent();

        GamePushEventDto gamePushEventDto = GamePushEventDto.builder()
                .event(event)
                .mgId(mgId)
                .appId(appId)
                .timestamp(timestamp)
                .data(data)
                .build();

        HttpEntity entity = buildEntity(gamePushEventDto);

        String result = yunXinServer.requestForEntity("", url, HttpMethod.POST, entity, String.class);
        if (result == null) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "忽然游戏事件推送失败");
        }

        JsonObject resultJson = GsonUtil.parseJsonObjectIfNotNull(result);
        int retCode = resultJson.get("ret_code").getAsInt();
        if (retCode != 0) {
            if (event.equals(PushEventEnum.USER_IN.getEvent()) && retCode == 100203) {
                log.info(resultJson.get("ret_msg").getAsString());
                return;
            }
            if (event.equals(PushEventEnum.USER_OUT.getEvent()) && (retCode == 100400 || retCode == 100301)) {
                log.info(resultJson.get("ret_msg").getAsString());
                return;
            }

            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "忽然游戏事件推送失败:" + resultJson.get("ret_msg").getAsString());
        }
    }

    /**
     * 封装忽然请求头及body
     *
     * @param body 请求body
     * @return HttpEntity
     */
    private HttpEntity buildEntity(Object body) {
        HttpHeaders httpHeaders = new HttpHeaders();
        yunXinServer.addSudAuthorization(httpHeaders, appId, appSecret, GsonUtil.toJson(body), String.valueOf(System.currentTimeMillis()), UUIDUtil.getUUID());
        httpHeaders.add("Content-Type", "application/json;charset=utf-8");
        HttpEntity entity = new HttpEntity(GsonUtil.toJson(body), httpHeaders);
        return entity;
    }

}
