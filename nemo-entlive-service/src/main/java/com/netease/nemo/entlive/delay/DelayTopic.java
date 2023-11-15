package com.netease.nemo.entlive.delay;

/**
 * @Author：CH
 * @Date：2023/8/8 9:40 PM
 */
public class DelayTopic {

    /**
     * 一起听播放延迟队列
     */
    public static final String LISTEN_TOGETHER_TOPIC = "delayQueue:listen_together_topic";

    /**
     * 副唱加入合唱后超时准备延迟队列，
     */
    public static final String JOIN_CHORUS_TOPIC = "delayQueue:join_chorus_topic";


    /**
     * 播放下一首歌曲延迟队列
     */
    public static final String PLAY_NEXT_SONG_TOPIC = "delayQueue:play_next_song_topic";
}
