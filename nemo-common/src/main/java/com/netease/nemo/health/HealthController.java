package com.netease.nemo.health;

import com.netease.nemo.constants.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/nemo/")
public class HealthController {
    private static final Logger logger = LoggerFactory.getLogger(HealthController.class);

    // 允许用于激活系统的状态的白名单，可以配置在数据库中也可以设置在配置文件中，最基本可设置为127.0.0.1
    private static List<String> whiteList = null;
    // 默认设置重启处于离线状态，此处需要留意，具体更具情况设定
    private static boolean status = false;

    /**
     * check system status
     *
     * @param request
     * @param response
     */
    @GetMapping(value = "/health/status")
    public void getStatus(HttpServletRequest request,
                          HttpServletResponse response) {
        if (status) {
            response.setStatus(Constant.SC_OK);
        } else {
            response.setStatus(Constant.SC_FORBIDDEN);
        }
    }

    /**
     * active system status
     *
     * @param request
     * @param response
     */
    @GetMapping(value = "/health/online")
    public void postStatusActive(HttpServletRequest request,
                                 HttpServletResponse response) {
        String ip = request.getRemoteAddr();
        //如果没有配置白名单则设置为默认白名单
        if (whiteList == null) {
            whiteList = new ArrayList<>();
            whiteList.add(Constant.DEFAULT_WHITE_IP);
            whiteList.add(Constant.DEFAULT_WHITE_IP + ":8181");
        }
        // 如果ip在白名单范围内则激活系统
        if (whiteList.contains(ip)) {
            logger.info("system status is active by " + ip);
            status = true;
            response.setStatus(Constant.SC_OK);
        } else {
            logger.info(ip + " is forbidded to active system");
            response.setStatus(Constant.SC_FORBIDDEN);
        }
    }

    /**
     * set system offline
     *
     * @param request
     * @param response
     */
    @GetMapping(value = "/health/offline")
    public void postStatusOffline(HttpServletRequest request,
                                  HttpServletResponse response) {
        String ip = ((HttpServletRequest) request).getRemoteAddr();
        //如果没有配置白名单则设置为默认白名单
        if (whiteList == null) {
            whiteList = new ArrayList<String>();
            whiteList.add(Constant.DEFAULT_WHITE_IP);
        }
        // 如果ip在白名单范围内则下线系统
        if (whiteList.contains(ip)) {
            logger.info("system status is set offline by  " + ip);
            status = false;
            response.setStatus(Constant.SC_OK);
        } else {
            logger.info("ip is " + ip + " is forbidded to set system status offline");
            response.setStatus(Constant.SC_FORBIDDEN);
        }
    }

    /**
     * set system check
     *
     * @param request
     * @param response
     **/
    @GetMapping("/health/check")
    public void healthCheck(HttpServletRequest request, HttpServletResponse response) {
        response.setStatus(Constant.SC_OK);
    }
}
