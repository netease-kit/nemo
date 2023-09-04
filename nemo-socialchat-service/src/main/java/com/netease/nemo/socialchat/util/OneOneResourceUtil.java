package com.netease.nemo.socialchat.util;

import com.google.gson.JsonObject;
import com.netease.nemo.socialchat.dto.OnLineUserDto;
import com.netease.nemo.util.gson.GsonUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OneOneResourceUtil {

    public static List<OnLineUserDto> getVirtually1V1Users() {
        List<OnLineUserDto> users = new ArrayList<>(4);
        users.add(OnLineUserDto.builder()
                .userName("篮球宝贝")
                .userUuid("one_one_virtually_user1")
                .mobile("10000000001")
                .icon("https://yx-web-nosdn.netease.im/common/27e51fc93d1621ab6dbf2d576dc7fef9/1v1_icon_1.jpg")
                .audioUrl("https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2Fvoice1.mp3")
                .videoUrl("https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2Fvideo1.mp4")
                .callType(1).build());

        users.add(OnLineUserDto.builder()
                .userName("知心姐姐")
                .mobile("10000000002")
                .userUuid("one_one_virtually_user2")
                .icon("https://yx-web-nosdn.netease.im/common/27639c616550633ab4422db7d1be74d9/1v1_Icon_2.jpg")
                .audioUrl("https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2Fvoice2.mp3")
                .videoUrl("https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2Fvideo2.mp4")
                .callType(1).build());

        users.add(OnLineUserDto.builder()
                .userName("冲浪辣妹")
                .mobile("10000000003")
                .userUuid("one_one_virtually_user3")
                .icon("https://yx-web-nosdn.netease.im/common/62d1e353a7af1b8cff833123f5a344e7/1v1_Icon_3.jpg")
                .audioUrl("https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2Fvoice3.mp3")
                .videoUrl("https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2Fvideo3.mp4")
                .callType(1).build());

        users.add(OnLineUserDto.builder()
                .userName("三年二班")
                .mobile("10000000004")
                .userUuid("one_one_virtually_user4")
                .icon("https://yx-web-nosdn.netease.im/common/2bfae09b7501e7f4bd1de0f77168e7b1/1v1_Icon_4.jpg")
                .audioUrl("https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2Fvoice4.mp3")
                .videoUrl("https://yx-web-nosdn.netease.im/quickhtml%2Fassets%2Fyunxin%2Fdefault%2Fvideo4.mp4")
                .callType(1).build());
        return users;
    }

    public static List<String> virtually1V1UserUuids = Arrays.asList("one_one_virtually_user1", "one_one_virtually_user2", "one_one_virtually_user3", "one_one_virtually_user4");

    public static JsonObject getMessageImageJson() {
        return GsonUtil.parseJsonObject(messageImageJson);
    }

    public static JsonObject getAudioJson() {
        return GsonUtil.parseJsonObject(audioJson);
    }

    public static String messageImageJson = "{\n" +
            "    \"one_one_virtually_user1\":{\n" +
            "        \"name\":\"1v1_image_im.png\",\n" +
            "        \"md5\":\"e559d70ee085a3c5fb1ed1e9547dbc02\",\n" +
            "        \"url\":\"https://yx-web-nosdn.netease.im/common/e559d70ee085a3c5fb1ed1e9547dbc02/1v1_image_im.png\",\n" +
            "        \"ext\":\"png\",\n" +
            "        \"w\":240,\n" +
            "        \"h\":240,\n" +
            "        \"size\":58761\n" +
            "    },\n" +
            "    \"one_one_virtually_user2\":{\n" +
            "        \"name\":\"1v1_image_im2.jpeg\",\n" +
            "        \"md5\":\"9a4d7d7fcb0c7b5f0ffb7d3f8c7eb176\",\n" +
            "        \"url\":\"https://yx-web-nosdn.netease.im/common/9a4d7d7fcb0c7b5f0ffb7d3f8c7eb176/1v1_image_im2.jpeg\",\n" +
            "        \"ext\":\"jpeg\",\n" +
            "        \"w\":400,\n" +
            "        \"h\":267,\n" +
            "        \"size\":192751\n" +
            "    }\n" +
            "}";

    public static String audioJson = "{\n" +
            "    \"one_one_virtually_user2\":{\n" +
            "        \"dur\":2000,\n" +
            "        \"md5\":\"cb2370c33ab85ef4b135b8e537d6e6b7\",\n" +
            "        \"url\":\"https://yx-web-nosdn.netease.im/common/cb2370c33ab85ef4b135b8e537d6e6b7/share_picture.aac\",\n" +
            "        \"ext\":\"aac\",\n" +
            "        \"size\":23263\n" +
            "    },\n" +
            "    \"one_one_virtually_user3\":{\n" +
            "        \"dur\":2000,\n" +
            "        \"md5\":\"2562eba1e6de836cce378668d5ac30d8\",\n" +
            "        \"url\":\"https://yx-web-nosdn.netease.im/common/2562eba1e6de836cce378668d5ac30d8/talk_with_me.aac\",\n" +
            "        \"ext\":\"aac\",\n" +
            "        \"size\":24558\n" +
            "    }\n" +
            "}";
}
