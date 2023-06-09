package com.netease.nemo.openApi;

import com.netease.nemo.code.ErrorCode;
import com.netease.nemo.config.YunXinConfigProperties;
import com.netease.nemo.exception.BsException;
import com.netease.nemo.openApi.dto.neroom.CreateNeRoomDto;
import com.netease.nemo.openApi.dto.neroom.NeRoomMemberDto;
import com.netease.nemo.openApi.dto.neroom.NeRoomSeatDto;
import com.netease.nemo.openApi.dto.neroom.NeRoomUserDto;
import com.netease.nemo.openApi.dto.response.NeRoomResponse;
import com.netease.nemo.openApi.paramters.neroom.CreateNeRoomParam;
import com.netease.nemo.openApi.paramters.neroom.CreateNeRoomUserParam;
import com.netease.nemo.openApi.paramters.neroom.NeRoomMessageParam;
import com.netease.nemo.util.ObjectMapperUtil;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 云信NeRoom服务相关API
 */
@Service
@Slf4j
public class NeRoomService {

    @Resource
    private YunXinServer yunXinServer;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private YunXinConfigProperties yunXinConfigProperties;

    /**
     * 创建NeRoom账号
     * https://doc.yunxin.163.com/neroom/docs/TY1NzM5MjQ?platform=server
     */
    public NeRoomUserDto createNeRoomUser(String userUuid, CreateNeRoomUserParam neRoomUserCreateParam) {
        log.info("start createNeRoomUser. param:{}, userUuid:{}", GsonUtil.toJson(neRoomUserCreateParam), userUuid);
        if (neRoomUserCreateParam == null || StringUtils.isEmpty(userUuid)) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        String url = String.format("/apps/%s/v1/users/%s", yunXinConfigProperties.getAppKey(), userUuid);

        try {
            NeRoomResponse neRoomResponse = yunXinServer.requestEntityForNeRoom(url, HttpMethod.PUT, neRoomUserCreateParam);
            Integer code = neRoomResponse.getCode();
            if (code == null || code != 0) {
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, neRoomResponse.getMsg());
            }
            if (neRoomResponse.getData() == null) {
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            return ObjectMapperUtil.map(neRoomResponse.getData(), NeRoomUserDto.class);
        } catch (BsException e) {
            throw e;
        } catch (Exception e) {
            log.info("createNeRoomUser error.", e);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 创建NeRoom房间
     * https://doc.yunxin.163.com/neroom/docs/TM0MTUyNjc?platform=server
     */
    public CreateNeRoomDto createNeRoom(CreateNeRoomParam createNeRoomParam) {
        log.info("start createNeRoom. param:{}", GsonUtil.toJson(createNeRoomParam));
        if (createNeRoomParam == null) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        String url = "/apps/v2/room";

        try {
            NeRoomResponse neRoomResponse = yunXinServer.requestEntityForNeRoom(url, HttpMethod.PUT, createNeRoomParam);
            Integer code = neRoomResponse.getCode();
            if (code == null || code != 0) {
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, neRoomResponse.getMsg());
            }

            if (neRoomResponse.getData() == null) {
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            return modelMapper.map(neRoomResponse.getData(), CreateNeRoomDto.class);
        } catch (BsException e) {
            throw e;
        } catch (Exception e) {
            log.info("createNeRoom error.", e);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 删除NeRoom房间
     * https://doc.yunxin.163.com/neroom/docs/jYzNjE5MTE?platform=server
     */
    public void deleteNeRoom(String roomArchiveId) {
        log.info("start createNeRoom. roomArchiveId:{}", roomArchiveId);
        if (StringUtils.isEmpty(roomArchiveId)) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "roomArchiveId Can not Be Null");
        }

        String url = String.format("apps/v2/room?roomArchiveId=%s", roomArchiveId);

        try {
            NeRoomResponse neRoomResponse = yunXinServer.requestEntityForNeRoom(url, HttpMethod.DELETE, null);
            Integer code = neRoomResponse.getCode();
            if (code == null || code != 0) {
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, neRoomResponse.getMsg());
            }
        } catch (BsException e) {
            throw e;
        } catch (Exception e) {
            log.info("deleteNeRoom error.", e);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * 获取NeRoom房间上麦信息
     * https://doc.yunxin.163.com/neroom/docs/TExNjQ4MzQ?platform=server
     */
    public List<NeRoomSeatDto> getNeRoomSeatList(String roomArchiveId) {
        log.info("start getNeRoomSeatList. roomArchiveId:{}", roomArchiveId);
        if (StringUtils.isEmpty(roomArchiveId)) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "roomArchiveId Can not Be Null");
        }

        String url = String.format("/neroom/seat/v1/slot_list/%s", roomArchiveId);

        try {
            NeRoomResponse neRoomResponse = yunXinServer.requestEntityForNeRoom(url, HttpMethod.GET, null);
            Integer code = neRoomResponse.getCode();
            if (code == null || code != 0) {
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, neRoomResponse.getMsg());
            }
            if (neRoomResponse.getData() == null) {
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            return GsonUtil.fromJson(GsonUtil.toJson(neRoomResponse.getData()), new TypeToken<List<NeRoomSeatDto>>() {
            }.getType());
        } catch (BsException e) {
            throw e;
        } catch (Exception e) {
            log.info("getNeRoomSeatList error.", e);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 获取NeRoom房间实时详情
     * https://doc.yunxin.163.com/neroom/docs/TExNjQ4MzQ?platform=server
     */
    public NeRoomMemberDto getNeRoomOnlineMember(String roomArchiveId, Integer pageNumber, Integer pageSize) {
        log.info("start createNeRoom. roomArchiveId:{}", roomArchiveId);
        if (StringUtils.isEmpty(roomArchiveId)) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "roomArchiveId Can not Be Null");
        }
        if (pageNumber == null) {
            pageNumber = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }

        String url = String.format("/apps/v2/online-user-list?roomArchiveId=%s&pageNumber=%d&pageSize=%d", roomArchiveId, pageNumber, pageSize);

        try {
            NeRoomResponse neRoomResponse = yunXinServer.requestEntityForNeRoom(url, HttpMethod.GET, null);
            Integer code = neRoomResponse.getCode();
            if (code == null || code != 0) {
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, neRoomResponse.getMsg());
            }
            if (neRoomResponse.getData() == null) {
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            return modelMapper.map(neRoomResponse.getData(), NeRoomMemberDto.class);
        } catch (BsException e) {
            throw e;
        } catch (Exception e) {
            log.info("getNeRoomOnlineMember error.", e);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 发送NeRoom聊天室自定义消息
     * https://doc.yunxin.163.com/neroom/docs/jQ2MDI1Nzc?platform=server
     *
     * 注：需要创建NeRoom时勾选了聊天室的资源
     */
    public void sendNeRoomCustomMessage(NeRoomMessageParam neRoomMessageParam) {
        log.info("start sendNeRoomCustomMessage. neRoomMessageParam:{}", GsonUtil.toJson(neRoomMessageParam));
        if (neRoomMessageParam == null || StringUtils.isEmpty(neRoomMessageParam.getRoomUuid())) {
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, "neRoomMessageParam error");
        }

        String url = "apps/v2/sendChatRoomCustomMessage";

        try {
            NeRoomResponse neRoomResponse = yunXinServer.requestEntityForNeRoom(url, HttpMethod.POST, neRoomMessageParam);
            Integer code = neRoomResponse.getCode();
            if (code == null || code != 0) {
                throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR, neRoomResponse.getMsg());
            }
        } catch (BsException e) {
            throw e;
        } catch (Exception e) {
            log.info("sendNeRoomCustomMessage error.", e);
            throw new BsException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

}
