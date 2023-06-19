package com.netease.nemo.service;

/**
 * 云信抄送处理接口
 */
public interface NotifyService {

    /**
     * 抄送处理
     *
     * @param body 抄送body
     */
    void handlerNotify(String body);
}
