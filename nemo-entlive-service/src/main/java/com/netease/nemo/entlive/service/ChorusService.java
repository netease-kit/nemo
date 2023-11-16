package com.netease.nemo.entlive.service;

import com.netease.nemo.entlive.dto.ChorusControlResultDto;
import com.netease.nemo.entlive.model.po.ChorusRecord;
import com.netease.nemo.entlive.parameter.CancelChorusParam;
import com.netease.nemo.entlive.parameter.ChorusInviteParam;
import com.netease.nemo.entlive.parameter.FinishChorusReadyParam;
import com.netease.nemo.entlive.parameter.JoinChorusParam;

/**
 * 合唱服务
 *
 * @Author：CH
 * @Date：2023/10/13 10:53 AM
 */
public interface ChorusService {

    /**
     * 合唱邀请
     *
     * @param operator 操作者
     * @param param    合唱邀请参数
     * @return ChorusControlResultDto
     **/
    ChorusControlResultDto chorusInvite(String operator, ChorusInviteParam param);

    /**
     * 加入合唱
     *
     * @param operator 操作者
     * @param param    合唱邀请参数
     * @return ChorusControlResultDto
     **/
    ChorusControlResultDto joinChorus(String operator, JoinChorusParam param);

    /**
     * 取消合唱
     *
     * @param operator 操作者
     * @param param    合唱邀请参数
     * @return ChorusControlResultDto
     **/
    ChorusControlResultDto cancelChorus(String operator, CancelChorusParam param);

    /**
     * 合唱准备完成
     *
     * @param operator 操作者
     * @param param    合唱准备完成参数
     * @return ChorusControlResultDto
     **/
    ChorusControlResultDto chorusReady(String operator, FinishChorusReadyParam param);


    /**
     * 判断合唱类型
     *
     * @param chorusRecord chorusRecord
     * @return
     */
    Integer judgeChorusType(ChorusRecord chorusRecord);
}
