package com.netease.nemo.entlive.service;

import com.netease.nemo.entlive.dto.OrderSongResultDto;
import com.netease.nemo.entlive.dto.SingDetailInfoDto;
import com.netease.nemo.entlive.parameter.AbandonSingParam;
import com.netease.nemo.entlive.parameter.SingActionParam;
import com.netease.nemo.entlive.parameter.SingParam;

import java.util.List;

/**
 * KTV演唱服务
 *
 * @Author：CH
 * @Date：2023/10/11 11:03 AM
 */
public interface SingService {

    /**
     * 开始演唱
     *
     * @param operator 操作者
     * @param param    演唱操作对象
     */
    void singStart(String operator, SingParam param);

    /**
     * 演唱状态变更
     *
     * @param operator        操作者
     * @param singActionParam singActionParam
     */
    void singControl(String operator, SingActionParam singActionParam);

    /**
     * 根据appKey、roomUuid获取演唱详情
     *
     * @param roomUuid roomUuid
     * @return SingDetailInfoDto
     */
    SingDetailInfoDto getSingInfo(String roomUuid);


    /**
     * 销毁演唱的相关信息（KTV房间摧毁时调用）
     *
     * @param operator 操作者
     * @param roomUuid roomUuid
     **/
    void destroySingInfo(String operator, String roomUuid);

    /**
     * 根据appKey、roomUuid获取已演唱过的歌曲列表
     *
     * @param roomUuid roomUuid
     * @return List<Object>
     */
    List<Object> getSingList(String roomUuid);

    /**
     * 成员离开时结束歌唱（包括离开房间或者麦位）
     *
     * @param roomUuid      房间唯一编号
     * @param userUuid      主唱uuid
     * @param oldOrderSongs 未离开前的点歌列表
     **/
    void endSingWhenMemberOut(String roomUuid, String userUuid, List<OrderSongResultDto> oldOrderSongs);

    /**
     * 歌曲被切时结束歌唱
     *
     * @param roomUuid roomUuid
     * @param operator 切歌的操作人员
     * @param orderId  被切掉的歌曲id
     **/
    void endSingWhenSongSwitch(String roomUuid, String operator, Long orderId);

    /**
     * 放弃演唱歌曲
     *
     * @param operator 操作者
     * @param param    param
     **/
    void abandonSing(String operator, AbandonSingParam param);

    /**
     * 播放下一首歌曲的聊天室消息
     *
     * @param operator  operator
     * @param roomUuid  roomUuid
     * @param resultDto resultDto
     **/
    void playNextSongMsg(String operator, String roomUuid, OrderSongResultDto resultDto);

    /**
     * 合唱同意后，下载演唱资源超时处理方式
     *
     * @param roomUuid roomUuid
     * @param chorusId chorusId
     * @param orderId  orderId
     **/
    void joinChorusTimeOut(String roomUuid, String chorusId, Long orderId);

    /**
     * 发送播放下一首歌消息后，若无这首歌的开始与结束操作时的处理方式
     *
     * @param roomUuid roomUuid
     * @param orderId  orderId
     **/
    void playNextSongTimeOut(String roomUuid, Long orderId);

    /**
     * 清空房间播放信息
     *
     * @param roomUuid roomUuid
     */
    void cleanSingInfo(String roomUuid);

}
