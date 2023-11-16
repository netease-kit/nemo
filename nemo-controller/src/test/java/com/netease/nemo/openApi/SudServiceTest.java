package com.netease.nemo.openApi;

import com.netease.nemo.openApi.dto.sud.ApiUrlConfigDto;
import com.netease.nemo.openApi.dto.sud.MgInfo;
import com.netease.nemo.openApi.dto.sud.MgListDto;
import com.netease.nemo.openApi.dto.sud.enums.PushEventEnum;
import com.netease.nemo.openApi.dto.sud.event.UserInReqData;
import com.netease.nemo.util.gson.GsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author：CH
 * @Date：2023/8/22 3:03 PM
 */
@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@Slf4j
public class SudServiceTest {

    @Resource
    private SudService sudService;

    @Test
    public void testSudGetApiConfig() {
        ApiUrlConfigDto result = sudService.getSudAPI();
        Assert.isTrue(result != null && null != result.getApi(), "result is null");
    }

    @Test
    public void testGameList() {
        List<MgInfo> result = sudService.gameList();
        log.info("MgListDto:{}", GsonUtil.toJson(result));
        Assert.isTrue(result != null && !CollectionUtils.isEmpty(result), "result is null");
    }

    @Test
    public void testGamePush() {
        List<MgInfo> result = sudService.gameList();
        log.info("MgListDto:{}", GsonUtil.toJson(result));
        Assert.isTrue(result != null && !CollectionUtils.isEmpty(result), "result is null");

        String mgId = result.get(0).getMgId();

        UserInReqData userInReqData = UserInReqData.builder().code("123").roomId("123").mode(1).build();

        sudService.pushGameEvent(PushEventEnum.USER_IN.getEvent(), mgId, String.valueOf(System.currentTimeMillis()), userInReqData);
    }

    @Test
    public void testGson() {
        MgListDto result = new MgListDto();
        result.setData(new MgListDto.SudData());
        log.info("MgListDto:{}", GsonUtil.toJson(result));

        String str="{\"ret_code\":0,\"ret_msg\":\"\",\"data\":{\"mg_info_list\":[{\"mg_id\":\"1461228410184400899\",\"name\":{\"default\":\"Draw Guess\",\"en-US\":\"Draw Guess\",\"zh-SG\":\"你画我猜\",\"zh-HK\":\"你畫我猜\",\"zh-TW\":\"你畫我猜\",\"zh-MO\":\"你畫我猜\",\"ms-BN\":\"Lukis Tebak\",\"ms-MY\":\"Lukis Tebak\",\"zh-CN\":\"你画我猜\",\"vi-VN\":\"Đuổi hình bắt chữ\",\"en-GB\":\"Draw Guess\",\"th-TH\":\"ทายภาพ\"},\"desc\":{\"default\":\"Draw Guess\",\"en-US\":\"Draw Guess\",\"zh-SG\":\"你画我猜\",\"zh-HK\":\"你畫我猜\",\"zh-TW\":\"你畫我猜\",\"zh-MO\":\"你畫我猜\",\"ms-BN\":\"Lukis Tebak\",\"ms-MY\":\"Lukis Tebak\",\"zh-CN\":\"你画我猜\",\"vi-VN\":\"Đuổi hình bắt chữ\",\"en-GB\":\"Draw Guess\",\"th-TH\":\"ทายภาพ\"},\"thumbnail332x332\":{\"default\":\"https://sim-sud-static-tcc.sudden.ltd/game/drawandguess/60000403/332.png\",\"zh-HK\":\"https://sim-sud-static-tcc.sudden.ltd/game/drawandguess/60000403/332.png\",\"zh-CN\":\"https://sim-sud-static-tcc.sudden.ltd/game/drawandguess/60000403/332.png\"},\"thumbnail192x192\":{\"default\":\"https://sim-sud-static-tcc.sudden.ltd/game/drawandguess/60000403/192.png\",\"zh-HK\":\"https://sim-sud-static-tcc.sudden.ltd/game/drawandguess/60000403/192.png\",\"zh-CN\":\"https://sim-sud-static-tcc.sudden.ltd/game/drawandguess/60000403/192.png\"},\"thumbnail128x128\":{\"default\":\"https://sim-sud-static-tcc.sudden.ltd/game/drawandguess/60000403/128.png\",\"zh-HK\":\"https://sim-sud-static-tcc.sudden.ltd/game/drawandguess/60000403/128.png\",\"zh-CN\":\"https://sim-sud-static-tcc.sudden.ltd/game/drawandguess/60000403/128.png\"},\"thumbnail80x80\":{\"default\":\"https://sim-sud-static-tcc.sudden.ltd/game/drawandguess/60000403/80.png\",\"zh-HK\":\"https://sim-sud-static-tcc.sudden.ltd/game/drawandguess/60000403/80.png\",\"zh-CN\":\"https://sim-sud-static-tcc.sudden.ltd/game/drawandguess/60000403/80.png\"},\"big_loading_pic\":{\"default\":\"https://sim-sud-static-tcc.sudden.ltd/game/drawandguess/60000403/bg.jpg\",\"zh-HK\":\"https://sim-sud-static-tcc.sudden.ltd/game/drawandguess/60000403/bg.jpg\",\"zh-CN\":\"https://sim-sud-static-tcc.sudden.ltd/game/drawandguess/60000403/bg.jpg\"},\"game_mode_list\":[{\"mode\":1,\"count\":[2,9],\"team_count\":[2,9],\"team_member_count\":[1,1],\"rule\":\"{}\"}]},{\"mg_id\":\"1461297734886621238\",\"name\":{\"default\":\"Gobang\",\"en-US\":\"Gobang\",\"zh-SG\":\"五子棋\",\"zh-HK\":\"五子棋\",\"zh-TW\":\"五子棋\",\"zh-MO\":\"五子棋\",\"ms-BN\":\"Gobang\",\"ms-MY\":\"Gobang\",\"zh-CN\":\"五子棋\",\"vi-VN\":\"Cờ Carô\",\"en-GB\":\"Gobang\",\"th-TH\":\"โกโมกุา\"},\"desc\":{\"default\":\"Gobang\",\"en-US\":\"Gobang\",\"zh-SG\":\"五子棋\",\"zh-HK\":\"五子棋\",\"zh-TW\":\"五子棋\",\"zh-MO\":\"五子棋\",\"ms-BN\":\"Gobang\",\"ms-MY\":\"Gobang\",\"zh-CN\":\"五子棋\",\"vi-VN\":\"Cờ Carô\",\"en-GB\":\"Gobang\",\"th-TH\":\"โกโมกุา\"},\"thumbnail332x332\":{\"default\":\"https://sim-sud-static.sudden.ltd/game/gobang/332.png\",\"zh-HK\":\"https://sim-sud-static.sudden.ltd/game/gobang/332.png\",\"zh-CN\":\"https://sim-sud-static.sudden.ltd/game/gobang/332.png\"},\"thumbnail192x192\":{\"default\":\"https://sim-sud-static.sudden.ltd/game/gobang/192.png\",\"zh-HK\":\"https://sim-sud-static.sudden.ltd/game/gobang/192.png\",\"zh-CN\":\"https://sim-sud-static.sudden.ltd/game/gobang/192.png\"},\"thumbnail128x128\":{\"default\":\"https://sim-sud-static.sudden.ltd/game/gobang/128.png\",\"zh-HK\":\"https://sim-sud-static.sudden.ltd/game/gobang/128.png\",\"zh-CN\":\"https://sim-sud-static.sudden.ltd/game/gobang/128.png\"},\"thumbnail80x80\":{\"default\":\"https://sim-sud-static.sudden.ltd/game/gobang/80.png\",\"zh-HK\":\"https://sim-sud-static.sudden.ltd/game/gobang/80.png\",\"zh-CN\":\"https://sim-sud-static.sudden.ltd/game/gobang/80.png\"},\"big_loading_pic\":{\"default\":\"https://sim-sud-static.sudden.ltd/game/gobang/bg.jpg\",\"zh-HK\":\"https://sim-sud-static.sudden.ltd/game/gobang/bg.jpg\",\"zh-CN\":\"https://sim-sud-static.sudden.ltd/game/gobang/bg.jpg\"},\"game_mode_list\":[{\"mode\":1,\"count\":[2,2],\"team_count\":[2,2],\"team_member_count\":[1,1],\"rule\":\"{}\"}]},{\"mg_id\":\"1468180338417074177\",\"name\":{\"default\":\"Ludo\",\"en-US\":\"Ludo\",\"zh-SG\":\"飞行棋\",\"zh-HK\":\"飛行棋\",\"zh-TW\":\"飛行棋\",\"zh-MO\":\"飛行棋\",\"zh-CN\":\"飞行棋\",\"vi-VN\":\"Cờ cá ngựa\",\"en-GB\":\"Ludo\",\"th-TH\":\"ลูโด\"},\"desc\":{\"default\":\"Ludo\",\"en-US\":\"Ludo\",\"zh-SG\":\"飞行棋\",\"zh-HK\":\"飛行棋\",\"zh-TW\":\"飛行棋\",\"zh-MO\":\"飛行棋\",\"zh-CN\":\"飞行棋\",\"vi-VN\":\"Cờ cá ngựa\",\"en-GB\":\"Ludo\",\"th-TH\":\"ลูโด\"},\"thumbnail332x332\":{\"default\":\"https://sim-sud-static-tcc.sudden.ltd/game/ludo/60000465/332.png\",\"zh-HK\":\"https://sim-sud-static-tcc.sudden.ltd/game/ludo/60000465/332.png\",\"zh-CN\":\"https://sim-sud-static-tcc.sudden.ltd/game/ludo/60000465/332.png\"},\"thumbnail192x192\":{\"default\":\"https://sim-sud-static-tcc.sudden.ltd/game/ludo/60000465/192.png\",\"zh-HK\":\"https://sim-sud-static-tcc.sudden.ltd/game/ludo/60000465/192.png\",\"zh-CN\":\"https://sim-sud-static-tcc.sudden.ltd/game/ludo/60000465/192.png\"},\"thumbnail128x128\":{\"default\":\"https://sim-sud-static-tcc.sudden.ltd/game/ludo/60000465/128.png\",\"zh-HK\":\"https://sim-sud-static-tcc.sudden.ltd/game/ludo/60000465/128.png\",\"zh-CN\":\"https://sim-sud-static-tcc.sudden.ltd/game/ludo/60000465/128.png\"},\"thumbnail80x80\":{\"default\":\"https://sim-sud-static-tcc.sudden.ltd/game/ludo/60000465/80.png\",\"zh-HK\":\"https://sim-sud-static-tcc.sudden.ltd/game/ludo/60000465/80.png\",\"zh-CN\":\"https://sim-sud-static-tcc.sudden.ltd/game/ludo/60000465/80.png\"},\"big_loading_pic\":{\"default\":\"https://sim-sud-static-tcc.sudden.ltd/game/ludo/60000465/bg.jpg\",\"zh-HK\":\"https://sim-sud-static-tcc.sudden.ltd/game/ludo/60000465/bg.jpg\",\"zh-CN\":\"https://sim-sud-static-tcc.sudden.ltd/game/ludo/60000465/bg.jpg\"},\"game_mode_list\":[{\"mode\":1,\"count\":[2,4],\"team_count\":[2,4],\"team_member_count\":[1,1],\"rule\":\"{\\\"defaultDisplay\\\": [0, 1, 2]}\"}]},{\"mg_id\":\"1664525565526667266\",\"name\":{\"default\":\"Monster Crush\",\"en-US\":\"Monster Crush\",\"zh-HK\":\"怪物消消樂\",\"zh-CN\":\"怪物消消乐\"},\"desc\":{\"default\":\"Monster Crush\",\"en-US\":\"Monster Crush\",\"zh-HK\":\"怪物消消樂\",\"zh-CN\":\"怪物消消乐\"},\"thumbnail332x332\":{\"default\":\"https://sim-sud-static-tcc.sudden.ltd/game/monstercrush/60000802/332.png\",\"zh-HK\":\"https://sim-sud-static-tcc.sudden.ltd/game/monstercrush/60000802/332.png\",\"zh-CN\":\"https://sim-sud-static-tcc.sudden.ltd/game/monstercrush/60000802/332.png\"},\"thumbnail192x192\":{\"default\":\"https://sim-sud-static-tcc.sudden.ltd/game/monstercrush/60000802/192.png\",\"zh-HK\":\"https://sim-sud-static-tcc.sudden.ltd/game/monstercrush/60000802/192.png\",\"zh-CN\":\"https://sim-sud-static-tcc.sudden.ltd/game/monstercrush/60000802/192.png\"},\"thumbnail128x128\":{\"default\":\"https://sim-sud-static-tcc.sudden.ltd/game/monstercrush/60000802/128.png\",\"zh-HK\":\"https://sim-sud-static-tcc.sudden.ltd/game/monstercrush/60000802/128.png\",\"zh-CN\":\"https://sim-sud-static-tcc.sudden.ltd/game/monstercrush/60000802/128.png\"},\"thumbnail80x80\":{\"default\":\"https://sim-sud-static-tcc.sudden.ltd/game/monstercrush/60000802/80.png\",\"zh-HK\":\"https://sim-sud-static-tcc.sudden.ltd/game/monstercrush/60000802/80.png\",\"zh-CN\":\"https://sim-sud-static-tcc.sudden.ltd/game/monstercrush/60000802/80.png\"},\"big_loading_pic\":{\"default\":\"https://sim-sud-static-tcc.sudden.ltd/game/monstercrush/60000802/bg.jpg\",\"zh-HK\":\"https://sim-sud-static-tcc.sudden.ltd/game/monstercrush/60000802/bg.jpg\",\"zh-CN\":\"https://sim-sud-static-tcc.sudden.ltd/game/monstercrush/60000802/bg.jpg\"},\"game_mode_list\":[{\"mode\":1,\"count\":[2,6],\"team_count\":[2,6],\"team_member_count\":[1,1],\"rule\":\"{}\"}]}]}}";

        MgListDto jsonObject = GsonUtil.fromJson(str, MgListDto.class);
        log.info(jsonObject.toString());
    }
}
