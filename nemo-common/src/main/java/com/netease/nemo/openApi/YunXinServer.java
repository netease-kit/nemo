package com.netease.nemo.openApi;

import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.openApi.dto.response.ImResponse;
import com.netease.nemo.openApi.dto.response.LiveWallSolutionResponse;
import com.netease.nemo.openApi.dto.response.NeRoomResponse;
import com.netease.nemo.util.CheckSumBuilder;
import com.netease.nemo.util.UUIDUtil;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Map;

@Component
@Slf4j
public class YunXinServer {

    @Resource
    private  RestTemplate restTemplate;

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    /**
     * IM请求
     */
    public ImResponse requestForPostEntityNim(String uri, HttpMethod method, Map<String, String> params, Map<String, String> headerParams,
                                              Class<ImResponse> responseType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        addCheckSumHeader(httpHeaders);
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        // 添加Header
        if (headerParams != null && headerParams.size() > 0) {
            for (Map.Entry<String, String> entry : headerParams.entrySet()) {
                httpHeaders.add(entry.getKey(), entry.getValue());
            }
        }
        MultiValueMap<String, String> fromData = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            fromData.add(entry.getKey(), entry.getValue());
        }

        HttpEntity entity = new HttpEntity(fromData, httpHeaders);

        return requestForEntity(yunXinConfigProperties.getNimHost(), uri, method, entity, responseType);
    }

    /**
     * NeRoom请求
     *
     * @param url  url
     * @param body body
     * @return NeRoomResponse
     */
    public NeRoomResponse requestEntityForNeRoom(String url, HttpMethod method, Object body) {
        HttpHeaders httpHeaders = new HttpHeaders();
        addCheckSumHeader(httpHeaders);
        httpHeaders.add("Content-Type", "application/json;charset=utf-8");
        HttpEntity entity = new HttpEntity(GsonUtil.toJson(body), httpHeaders);
        return requestForEntity(yunXinConfigProperties.getNeRoomHost(), url, method, entity, NeRoomResponse.class);
    }

    /**
     * 安全通POST请求
     *
     * @param uri  url
     * @param body body
     * @return LiveWallSolutionRespons
     */
    public LiveWallSolutionResponse requestPostEntityForSecurity(String uri, Object body) {
        HttpHeaders httpHeaders = new HttpHeaders();
        addCheckSumHeader(httpHeaders);
        httpHeaders.add("Content-Type", "application/json;charset=utf-8");
        HttpEntity entity = new HttpEntity(GsonUtil.toJson(body), httpHeaders);

        return requestForEntity(yunXinConfigProperties.getSecurityAuditHost(), uri, HttpMethod.POST, entity, LiveWallSolutionResponse.class);
    }

    public <T> T requestForEntity(String host, String uri, HttpMethod method, HttpEntity<?> requestEntity, Class<T> responseType) {
        T result = null;
        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(host + uri, method, requestEntity, responseType);
            if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                result = responseEntity.getBody();
                return result;
            } else {
                log.error("requestForEntity error.");
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        } finally {
            log.info("request yunXin openApi info. the url:{}, method:{}, requestEntity:{}, result:{}",
                    host + uri, method, GsonUtil.toJson(requestEntity), GsonUtil.toJson(result));
        }
    }


    /**
     * header中添加checkSum鉴权参数
     *
     * @param headers headers
     */
    public void addCheckSumHeader(HttpHeaders headers) {
        String nonce = UUIDUtil.getUUID();
        String curTime = String.valueOf(System.currentTimeMillis() / 1000);
        headers.add("Nonce", nonce);
        headers.add("CurTime", curTime);
        headers.add("CheckSum", CheckSumBuilder.getCheckSum(nonce, curTime, yunXinConfigProperties.getAppSecret()));
        headers.add("AppKey", yunXinConfigProperties.getAppKey());
    }
}
