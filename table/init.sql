CREATE TABLE IF NOT EXISTS `user_device` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID对应rtc房间的rtcUid',
  `user_uuid` varchar(64) NOT NULL COMMENT '用户唯一号',
  `device_id` varchar(64) NOT NULL COMMENT '设备号uuid',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_device` (`user_uuid`,`device_id`),
  KEY `device` (`device_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用户设备表';

CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `mobile` varchar(15) NOT NULL DEFAULT '' COMMENT '手机号',
  `user_uuid` varchar(64) NOT NULL COMMENT '用户唯一号',
  `user_token` varchar(64) NOT NULL COMMENT '用户token',
  `im_token` varchar(64) NOT NULL DEFAULT '' COMMENT '用户im_token',
  `user_name` varchar(64) NOT NULL DEFAULT '' COMMENT '用户名称',
  `icon` varchar(1024) NOT NULL DEFAULT '' COMMENT '头像',
  `age` int NOT NULL DEFAULT '-1' COMMENT '年龄',
  `sex` tinyint NOT NULL DEFAULT '0' COMMENT '性别 0 未知 1男 2 女 ',
  `state` tinyint NOT NULL DEFAULT '1' COMMENT '状态 1，可用；2禁用',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_uuid` (`user_uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


CREATE TABLE IF NOT EXISTS `gift` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `gift_name` varchar(64) NOT NULL COMMENT '礼物名称',
  `gift_desc` varchar(64) NOT NULL COMMENT '礼物描述',
  `cloud_coin` bigint(20) DEFAULT NULL COMMENT '礼物价值云币金额',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '礼物是否有效：-1.无效，1.有效',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT =1 DEFAULT CHARSET = utf8mb4 COMMENT = '礼物配置表';

CREATE TABLE IF NOT EXISTS `user_reward` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_uuid` varchar(64) NOT NULL COMMENT '打赏者账号',
  `gift_id` bigint(20) NOT NULL COMMENT '礼物名称',
  `gift_count` int(11) DEFAULT '1' COMMENT '礼物个数',
  `cloud_coin` bigint DEFAULT NULL COMMENT '礼物价值云币金额',
  `target` varchar(64) NOT NULL COMMENT '被打赏者账号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT =1 DEFAULT CHARSET = utf8mb4 COMMENT = '用户打赏记录';


CREATE TABLE IF NOT EXISTS `live_record` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `room_archive_id` varchar(64) DEFAULT NULL COMMENT 'NeRoom虚拟房间唯一编号',
  `room_uuid` varchar(64) DEFAULT NULL COMMENT '虚拟房间编号',
  `room_name` varchar(64) DEFAULT NULL COMMENT '虚拟房间编号',
  `user_uuid` varchar(64) DEFAULT NULL COMMENT '主播账户编号',
  `live_topic` varchar(256) DEFAULT NULL COMMENT '直播主题',
  `cover` varchar(256) DEFAULT NULL COMMENT '封面URL，如果传空则自动生成',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '直播状态：1.有效，-1.无效',
  `live` tinyint NOT NULL DEFAULT '1' COMMENT '直播状态，-1.结束, 0.未开始，1.直播中',
  `live_type` tinyint NOT NULL DEFAULT '1' COMMENT '直播类型 1.互动直播  2.语聊房，3. KTV',
  `live_config` varchar(1024) DEFAULT NULL COMMENT '直播配置信息',
  `sing_mode` tinyint(2) NOT NULL DEFAULT '0' COMMENT '演唱模式 0:智能合唱 1:串行合唱 2:NTP实时合唱 3:独唱',
  PRIMARY KEY (`id`),
  KEY `idx_room_uuid` (`room_uuid`),
  KEY `idx_room_archive_id` (`room_archive_id`),
  KEY `idx_user_uuid_status` (`user_uuid`,`status`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='直播记录';

CREATE TABLE IF NOT EXISTS `live_reward` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `live_record_id` bigint NOT NULL COMMENT '直播编号',
  `room_archive_id` varchar(64) DEFAULT NULL COMMENT 'NeRoom虚拟房间唯一编号',
  `room_uuid` varchar(64) NOT NULL DEFAULT '' COMMENT 'NeRoom房间编号',
  `user_uuid` varchar(64) NOT NULL COMMENT '帐号id',
  `gift_id` bigint DEFAULT NULL COMMENT '礼物标号',
  `cloud_coin` bigint DEFAULT NULL COMMENT '礼物价值云币金额',
  `gift_count` int NOT NULL DEFAULT '1' COMMENT '礼物个数',
  `target` varchar(64) NOT NULL COMMENT '被打赏者用户uuid',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_live_record_id_user_uuid` (`live_record_id`,`user_uuid`),
  KEY `idx_live_record_id_target` (`live_record_id`,`target`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='直播间礼物打赏';

CREATE TABLE IF NOT EXISTS `order_song` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `live_record_id` bigint DEFAULT NULL COMMENT '直播唯一编号',
  `room_archive_id` varchar(64) DEFAULT NULL COMMENT 'NeRoom虚拟房间唯一编号',
  `room_uuid` varchar(64) NOT NULL DEFAULT '' COMMENT '关联roomUuid',
  `user_uuid` varchar(64) NOT NULL DEFAULT '' COMMENT '用户编号',
  `song_id` varchar(64) NOT NULL DEFAULT '' COMMENT '歌曲编号',
  `song_name` varchar(64) DEFAULT NULL COMMENT '歌曲名称',
  `song_cover` varchar(512) DEFAULT NULL COMMENT '歌曲封面',
  `singer` varchar(64) DEFAULT NULL COMMENT '歌手',
  `singer_cover` varchar(512) DEFAULT NULL COMMENT '封面URL',
  `song_time` bigint NOT NULL DEFAULT '0' COMMENT '歌曲时长时间',
  `channel` tinyint NOT NULL DEFAULT '1' COMMENT '版权来源：1：云音乐 2、咪咕',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '点歌状态状态 -2 已唱  -1 删除 0:等待唱 1 唱歌中 ',
  `set_top_time` bigint NOT NULL DEFAULT '0' COMMENT '置顶时间',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_live_record_id_useruuid` (`live_record_id`,`user_uuid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='点歌表';


CREATE TABLE IF NOT EXISTS `game_member` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `live_record_id` bigint(20) DEFAULT NULL COMMENT '直播唯一记录',
  `room_uuid` varchar(64) DEFAULT NULL COMMENT '虚拟房间编号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '游戏成员状态0：准备中 1 游戏中 2 离开游戏',
  `join_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '加入游戏时间',
  `exit_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '离开游戏时间',
  `game_id` bigint(20) DEFAULT NULL COMMENT '游戏Id',
  `user_uuid` varchar(64) DEFAULT NULL COMMENT '成员ID',
  `user_name` varchar(64) DEFAULT NULL COMMENT '成员名词',
  `room_archive_id` varchar(64) DEFAULT NULL COMMENT '虚拟房间唯一编号',
  `game_record_id` bigint(20) DEFAULT NULL COMMENT '游戏唯一记录编号',
  PRIMARY KEY (`id`),
  KEY `idx_gameId_room_uuid` (`game_id`, `room_uuid`),
  KEY `idx_live_record_id` (`game_id`, `live_record_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = '游戏记录';

CREATE TABLE `game_member_history` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `live_record_id` bigint(20) DEFAULT NULL COMMENT '直播唯一记录',
  `room_uuid` varchar(64) DEFAULT NULL COMMENT '虚拟房间编号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `join_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '加入游戏时间',
  `exit_time` bigint(20) NOT NULL DEFAULT '0' COMMENT '离开游戏时间',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '游戏成员状态0：准备中 1 游戏中 2 离开游戏',
  `game_id` bigint(20) DEFAULT NULL COMMENT '游戏Id',
  `user_uuid` varchar(64) DEFAULT NULL COMMENT '成员ID',
  `user_name` varchar(64) DEFAULT NULL COMMENT '成员名词',
  `room_archive_id` varchar(64) DEFAULT NULL COMMENT '虚拟房间唯一编号',
  `game_record_id` bigint(20) DEFAULT NULL COMMENT '游戏唯一记录编号',
  PRIMARY KEY (`id`),
  KEY `idx_gameId_room_uuid` (`game_id`, `room_uuid`),
  KEY `idx_live_record_id` (`game_id`, `live_record_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = '游戏成员历史表';

CREATE TABLE `game_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `live_record_id` bigint(20) DEFAULT NULL COMMENT '直播唯一记录',
  `room_uuid` varchar(64) DEFAULT NULL COMMENT '虚拟房间编号',
  `game_creator` varchar(64) DEFAULT NULL COMMENT '游戏房间创建者',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `game_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '游戏状态0：待开始 1游戏中 2 游戏结束',
  `game_id` bigint(20) DEFAULT NULL COMMENT '游戏Id',
  `game_name` varchar(64) DEFAULT NULL COMMENT '游戏名称',
  `game_desc` varchar(64) DEFAULT NULL COMMENT '游戏描述',
  `thumbnail` varchar(256) DEFAULT NULL COMMENT '游戏背景图',
  `rule` varchar(256) DEFAULT NULL COMMENT '游戏规则',
  `room_archive_id` varchar(64) DEFAULT NULL COMMENT '虚拟房间唯一编号',
  PRIMARY KEY (`id`),
  KEY `idx_room_uuid` (`room_uuid`),
  KEY `idx_live_record_id` (`live_record_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = '游戏记录';

CREATE TABLE `game_report` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `game_record_id` bigint(20) DEFAULT NULL COMMENT '每局游戏唯一标识',
  `report_msg` varchar(4096) DEFAULT NULL COMMENT '上报数据',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_game_record_id` ( `game_record_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = '游戏上报数据表';

CREATE TABLE `game_record_history` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `live_record_id` bigint(20) DEFAULT NULL COMMENT '直播唯一记录',
  `room_uuid` varchar(64) DEFAULT NULL COMMENT '虚拟房间编号',
  `game_creator` varchar(64) DEFAULT NULL COMMENT '游戏房间创建者',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `game_status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '游戏状态0：待开始 1游戏中 2 游戏结束',
  `game_id` bigint(20) DEFAULT NULL COMMENT '游戏Id',
  `game_name` varchar(64) DEFAULT NULL COMMENT '游戏名称',
  `game_desc` varchar(64) DEFAULT NULL COMMENT '游戏描述',
  `thumbnail` varchar(256) DEFAULT NULL COMMENT '游戏背景图',
  `rule` varchar(256) DEFAULT NULL COMMENT '游戏规则',
  `room_archive_id` varchar(64) DEFAULT NULL COMMENT '虚拟房间唯一编号',
  `game_record_id` bigint(20) DEFAULT NULL COMMENT '游戏唯一记录编号',
  PRIMARY KEY (`id`),
  KEY `idx_room_uuid` (`room_uuid`),
  KEY `idx_live_record_id` (`live_record_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = '游戏记录历史表';


CREATE TABLE `chorus_record` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `chorus_id` varchar(64) NOT NULL DEFAULT '' COMMENT '合唱编号',
  `room_uuid` varchar(64) NOT NULL DEFAULT '' COMMENT '虚拟房间编号',
  `room_name` varchar(64) NOT NULL DEFAULT '' COMMENT '虚拟房间名称',
  `live_record_id` bigint(20) NOT NULL COMMENT '直播编号',
  `leader_uuid` varchar(64) NOT NULL DEFAULT '' COMMENT '主唱编号',
  `assistant_uuid` varchar(64) NOT NULL DEFAULT '' COMMENT '副唱编号',
  `status` tinyint(3) NOT NULL DEFAULT '1' COMMENT '状态：1.有效，-1.无效',
  `state` tinyint(3) unsigned NOT NULL DEFAULT '0' COMMENT '0:邀请中 1:合唱中 2:拒绝 3:取消 4:合唱结束 5:同意合唱',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `leader_device_param` varchar(128) NOT NULL DEFAULT '' COMMENT '主唱设备参数',
  `assistant_device_param` varchar(128) NOT NULL DEFAULT '' COMMENT '副唱设备参数',
  `order_id` bigint(20) NOT NULL DEFAULT '0' COMMENT '点歌编号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_chorusid` (`chorus_id`)
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET = utf8mb4 COMMENT = '合唱记录表';

INSERT INTO gift(`id`,`gift_name`,`gift_desc`,`cloud_coin`,`status`)
SELECT 1,'荧光棒','荧光棒',9,1
FROM DUAL WHERE NOT EXISTS (
    SELECT 1 FROM gift WHERE id=1
);
INSERT INTO gift(`id`,`gift_name`,`gift_desc`,`cloud_coin`,`status`)
SELECT 2,'安排','安排',99,1
FROM DUAL WHERE NOT EXISTS (
    SELECT 1 FROM gift WHERE id=2
);
INSERT INTO gift(`id`,`gift_name`,`gift_desc`,`cloud_coin`,`status`)
SELECT 3,'跑车','跑车',199,1
FROM DUAL WHERE NOT EXISTS (
    SELECT 1 FROM gift WHERE id=3
);
INSERT INTO gift(`id`,`gift_name`,`gift_desc`,`cloud_coin`,`status`)
SELECT 4,'火箭','火箭',999,1
FROM DUAL WHERE NOT EXISTS (
    SELECT 1 FROM gift WHERE id=4
);