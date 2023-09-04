package com.netease.nemo.controller.entertainmentLive;

import com.netease.nemo.annotation.Checksum;
import com.netease.nemo.annotation.RestResponseBody;
import com.netease.nemo.context.Context;
import com.netease.nemo.parameter.CreateUserParam;
import com.netease.nemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;


@RestController
@RequestMapping("/nemo/entertainmentLive/server/user")
@Slf4j
@RestResponseBody
public class EntLiveUserServerController {

    @Resource
    private UserService userService;

    @Checksum
    @PostMapping(value = "/createUser")
    public Object createUser(@Valid @RequestBody CreateUserParam createUserParam) {
        return userService.createNeRoomUser(createUserParam);
    }
}
