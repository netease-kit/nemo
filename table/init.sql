CREATE TABLE `user_device` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID对应rtc房间的rtcUid',
  `user_uuid` varchar(64) NOT NULL COMMENT '用户唯一号',
  `device_id` varchar(64) NOT NULL COMMENT '设备号uuid',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_device` (`user_uuid`,`device_id`),
  KEY `device` (`device_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT='用户设备表';

CREATE TABLE `user` (
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
  UNIQUE KEY `user_uuid` (`user_uuid`),
  UNIQUE KEY `mobile` (`mobile`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户表';


CREATE TABLE `gift` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `gift_name` varchar(64) NOT NULL COMMENT '礼物名称',
  `gift_desc` varchar(64) NOT NULL COMMENT '礼物描述',
  `cloud_coin` bigint(20) DEFAULT NULL COMMENT '礼物价值云币金额',
  `status` tinyint(4) NOT NULL DEFAULT '1' COMMENT '礼物是否有效：-1.无效，1.有效',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB AUTO_INCREMENT =1 DEFAULT CHARSET = utf8mb4 COMMENT = '礼物配置表';

INSERT INTO `gift` (`id`,`gift_name`,`gift_desc`,`cloud_coin`,`status`)
VALUES (1,'荧光棒','荧光棒',9,1),
(2,'安排','安排',99,1),
(3,'跑车','跑车',199,1),
(4,'火箭','火箭',999,1);


CREATE TABLE `user_reward` (
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