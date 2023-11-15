package com.netease.nemo.entlive.util;

import org.apache.commons.lang3.RandomUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveResourceUtil {

    /**
     * 语聊房默认麦位数
     */
    public final static Integer voiceDefaultSeatCount = 9;

    /**
     * 一起听默认麦位数
     */
    public final static Integer listenDefaultSeatCount = 2;

    /**
     * 直播主题列表
     */
    private final static List<String> topicList = Arrays.asList("来日可期 愿你前程似锦", "愿你付出甘之如饴，所得归于欢喜",
            "别害怕 宇宙都温柔", "开心的东西要专心记起", "知足且坚定 温柔且上进", "世界美好与你环环相扣", "别慌，月亮也正在大海某处迷茫",
            "保持很多的热爱和一点点野心", "一定要站在自己所热爱的世界里，闪闪发亮", "开心的东西要专心记起",
            "所求皆如愿", "所行化坦途", "多喜乐 长安宁");

    /**
     * 英文直播主题列表
     */
    private final static List<String> enTopicList = Arrays.asList("I wish you a bright future", "Buried city, to shut all lights.",
            "actions speak louder than words.", "Don't be so hard on yourself.", "A light heart lives long.", "Nothin is impossile.", "The beauty of the world is linked to you",
            "Never regret.", "learn to rest, not to quit.", "Youth means limitless possibilities.",
            "Get what you want", "The road is smooth", "Much joy grows peace");

    /**
     * 直播封面列表
     */
    private final static List<String> pictureName = Arrays.asList("37d8dad0281d74a89c1743708d11c99e/01.jpg", "a078b144371ee81870d78b63e3d209e1/02.jpg", "52062124075e6e02f4a89976fd78b2a9/03.jpg", "6984e6deb473c8159e6d57b0c273e2ef/04.jpg",
            "2d5fc800f1ed2298ff835caaa7d561df/05.jpg", "040755df5feb7e5e106b036d4eaaee1b/06.jpg", "01899457606a8db74196614a9348ae8d/07.jpg", "c885db1caef8feaec9b436e4b4a0c9ef/08.jpg");

    /**
     * 直播封面url前缀
     */
    private final static String coverPicUrlPrefix = "https://yx-web-nosdn.netease.im/common/";

    /**
     * 直播封面url后缀
     */
    private final static String avatarUrlPrefix = "https://yx-web-nosdn.netease.im/quickhtml/assets/yunxin/default/g2-demo-avatar-imgs/";

    /**
     * 用户头像列表
     */
    private final static List<String> avatarUrlList = Arrays.asList("86117772718772224.jpg", "86117780306268160.jpg", "86117781195460608.jpg",
            "86117781702971392.jpg", "86117782030127104.jpg", "86117783011594240.jpg",
            "86117783384887296.jpg", "86117783884009472.jpg", "86117784194387968.jpg", "86117789676343296.jpg",
            "86117790297100288.jpg", "86117790972383232.jpg", "86117791358259200.jpg", "86117791714775040.jpg",
            "86117792046125056.jpg", "86117792889180160.jpg", "86117793388302336.jpg");

    public final static List<String> defaultPicUrlList = Arrays.asList("https://yx-web-nosdn.netease.im/common/8d02910e144c7e37eb506cc3cd594913/Group%202789.png","https://yx-web-nosdn.netease.im/common/bdac99f1db1a90632c39fe5ad8c418e9/Group%202788.png" ,"https://yx-web-nosdn.netease.im/common/7eb7bed6a028d75112664a0934ca4e1c/Group%202794.png","https://yx-web-nosdn.netease.im/common/855b98bd26ce3164b88f70835a2e8983/Group%202795.png");

    /**
     * 用户名列表
     */
    private final static List<String> userNameList = Arrays.asList("残雪", "风过无痕", "流觞", "梦空", "北城凉筑", "狂风骤雨", "南城空", "北岛荒", "江烟", "紫剑雪枫", "邀你入夢", "诛笑靥"
            , "泪已成哀", "忆逝逝", "神明不渡", "追梦魂", "苦恋伊", "那时花开", "沈故", "一身仙气", "柠檬不萌", "无恙", "浊世悲欢", "小宇宙", "半情歌", "世俗骚", "盗梦者");

    public static String getRandomEnTopic() {
        int index = RandomUtils.nextInt(0, enTopicList.size());
        return enTopicList.get(index);
    }

    public static String getRandomUserName() {
        int index = RandomUtils.nextInt(0, userNameList.size());
        return userNameList.get(index);
    }

    public static String getRandomAvatar() {
        int index = RandomUtils.nextInt(0, avatarUrlList.size());
        return avatarUrlPrefix + avatarUrlList.get(index);
    }

    public static String getRandomTopic() {
        int index = RandomUtils.nextInt(0, topicList.size());
        return topicList.get(index);
    }

    public static Map<String, String> topicName = new HashMap<String, String>() {{
        put("virtually_voice_user_3", "顶级优质男神房");
        put("virtually_voice_user_4", "单身交友聊天房");
    }};

    public static String getRandomPicture() {
        int index = RandomUtils.nextInt(0, pictureName.size());
        return coverPicUrlPrefix + pictureName.get(index);
    }
}
