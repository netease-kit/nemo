package com.netease.nemo.entlive.manager;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.netease.nemo.openApi.dto.neroom.LayoutCoordinateDto;
import com.netease.nemo.openApi.paramters.neroom.StartLiveParam;
import com.netease.nemo.util.gson.GsonUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class LiveLayoutManager implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(LiveLayoutManager.class);
    private static final Map<String, LayoutCoordinateDto> LAYOUT_MAP = Maps.newHashMap();
    private static final String LAYOUT_CONFIG_FILE = "live_layout.json";
    private static final String ONE_WITH_N = "OneWithN";
    @Override
    public void afterPropertiesSet() {
        /**
         * 读取配置文件，初始化直播布局
         */
        ClassPathResource classPathResource = new ClassPathResource(LAYOUT_CONFIG_FILE);
        if (!classPathResource.exists()) {
            logger.info("Layout configuration file not found: " + LAYOUT_CONFIG_FILE);
            return;
        }

        try (InputStream inputStream = classPathResource.getInputStream()) {
            // 读取配置文件内容
            String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);

            // 使用gson解析JSON
            Map<String, LayoutCoordinateDto> tmp = GsonUtil.fromJson(content, new TypeToken<Map<String, LayoutCoordinateDto>>() {
            }.getType());

            if(tmp != null){
                LAYOUT_MAP.putAll(tmp);
            }

            logger.info("Live layout configuration loaded successfully, {} layouts found", LAYOUT_MAP.size());
        } catch (IOException e) {
            logger.error("Failed to load layout configuration", e);
        }
    }

    private static LayoutCoordinateDto getLayoutCoordinateDto(String layoutType) {
        LayoutCoordinateDto layoutCoordinateDto = LAYOUT_MAP.get(layoutType);
        // 使用重新序列化后的对象
        if (layoutCoordinateDto == null) {
            logger.error("Layout not found, current layout type: {}", layoutType);
            return null;
        }
        return GsonUtil.fromJson(GsonUtil.toJson(layoutCoordinateDto), LayoutCoordinateDto.class);
    }


    public static StartLiveParam buildLayoutParam(String roomArchiveId, List<String> userUuids, String title) {
        LayoutCoordinateDto layoutCoordinateDto = LiveLayoutManager.getLayoutCoordinateDto(ONE_WITH_N);
        if(layoutCoordinateDto == null){
            logger.error("single layout not found, current room: {}", roomArchiveId);
            return null;
        }
        StartLiveParam startLiveParam = new StartLiveParam();
        startLiveParam.setRoomArchiveId(Long.parseLong(roomArchiveId));
        startLiveParam.setLiveUserUuids(userUuids);
        startLiveParam.setTitle(title);
        startLiveParam.setLiveLayout(StartLiveParam.LiveLayoutType.CUSTOM);
        startLiveParam.refreshLayout(layoutCoordinateDto);
        // 过滤掉无效坐标
        List<LayoutCoordinateDto.BaseCoordinate> filteredCoordinates = startLiveParam.getLayoutCoordinateList().stream()
                .filter(coordinate -> !StringUtils.isEmpty(coordinate.getUserUuid()))
                .collect(Collectors.toList());
        startLiveParam.setLayoutCoordinateList(filteredCoordinates);
        return startLiveParam;
    }
}
