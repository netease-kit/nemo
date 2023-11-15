package com.netease.nemo.game.util;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * 游戏资源工具类
 *
 * @Author：CH
 * @Date：2023/9/5 4:09 PM
 */
public class GameResourceUtil {

    public static final Map<String, String> gameMap = ImmutableMap.of("1461228410184400899", "参与即准备\n" +
            "2-9人游戏\n" +
            "每位玩家轮流选词作画\n" +
            "其余玩家竞猜抢答\n" +
            "答对次数最多者获胜", "1461297734886621238", "参与即准备\n" +
            "双人游戏\n" +
            "双方分别使用黑白两色的棋子\n" +
            "下在棋盘直线与横线的交叉点上\n" +
            "先形成五子连珠者获胜", "1468180338417074177", "参与即准备\n" +
            "2~4人游戏\n" +
            "依次掷骰子，点数为6者可以派出一架飞机\n" +
            "留在自己的颜色上，可以额外移动一次\n" +
            "最先到达终点的玩家获胜");


    public static final Map<String, String> gameDescMap =
            ImmutableMap.of("1461228410184400899", "凑齐2人既可开始，适合3-6人玩",
                    "1461297734886621238", "双人PK，看看谁最先五子连线",
                    "1468180338417074177", "竞技游戏首选推荐");
}
