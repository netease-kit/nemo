package com.netease.nemo.controller.socialChat;

import com.netease.nemo.annotation.RestResponseBody;
import com.netease.nemo.annotation.TokenAuth;
import com.netease.nemo.context.Context;
import com.netease.nemo.openApi.NimService;
import com.netease.nemo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/nemo/socialChat/user")
@Slf4j
@RestResponseBody
@TokenAuth
public class SocialChatUserController {

    @Resource
    private UserService userService;

    @PostMapping(value = "/login")
    public Object postUserLogin() {
        return userService.login(Context.get().getUserUuid(), Context.get().getDeviceId());
    }

}
