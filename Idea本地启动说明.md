## 安装说明——使用IDEA本地启动

1、下载nemo工程

代码地址：https://github.com/netease-kit/nemo


2、使用IDEA打开nemo工程，修改application-local.yml中的 ${APPKEY}、${APPSECRET}、数据库配置、redis配置

示例如下
```
spring:
  profiles:
    active: local
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      connection-init-sql: SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci
      minimum-idle: 2
      connection-test-query: SELECT 1
      login-timeout: 30000
      connection-timeout: 30000
      validation-timeout: 5000
      idle-timeout: 600000
      maximum-pool-size: 100
      max-lifetime: 900000
    type: com.zaxxer.hikari.HikariDataSource
    url: jdbc:mysql://127.0.0.1:3306/nemo?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: 'root'
    password: '12345678'
  redis:
    database: 0
    host: 127.0.0.1
    port: 6379  #可不配，因为底层默认值为6379
    pool:
      max-active: 100 #连接池最大连接数（负值表示没有限制）
      max-wait: 3000 #连接池最大阻塞等待时间（负值表示没有限制）
      max-idle: 200 #连接池最大空闭连接数
      min-idle: 50 #连接汉最小空闲连接数
      timeout: 600 #连接超时时间（毫秒）

yunxin:
  origin:
    appKey:  ${APPKEY} # 云信appkey,如果需要申请应用可联系商务
    appSecret: ${APPSECRET} # 云信appSecret
    nimHost: https://api.netease.im
    neRoomHost: https://roomkit.netease.im/
    securityAuditHost: https://logic-dev.netease.im/
    rtcHost: https://logic-dev.netease.im/
business:
  yunxinAssistAccid: yunxinassistaccid_1 #云信派对小助手
  systemAccid: nimsystembot_1  #服务端系统Nim账号
  1v1RtcRoomLiveTime: 10 # 单位（分钟） 1v1RTC房间存活时间，demo默认10分钟回收房间
  voiceRoomConfigId: 569 # 语聊房模板

server:
  port: 9981

```

3、部署启动redis/mysql,并执行 [init.sql](./data/mysql/init/init.sql)脚本

4、启动RestApplication main函数

5、初始化应用及测试账号

修改 {APPKey}、{AppSecret}，以及data-raw中的参数，执行curl命令，生成nemo测试账号
```bash
curl --location --request POST 'http://127.0.0.1:9981/nemo/app/initAppAndUser' \
--header 'Appkey: {APPKey}' \
--header 'AppSecret: {AppSecret}' \
--header 'Content-Type: application/json' \
 --data-raw '{"userName":"test","userUuid":"test","imToken":"test","icon":"test"}';
```
返回结果code 200 则本地启动成功

6 其他

如果需要联调客户端代码，则需要将nemo工程发布为外网可以访问的服务，同时在云信控制台配置抄送地址。
* 1v1娱乐社交需要配置IM抄送，抄送地址为: http://{nemo服务域名}/nemo/socialChat/nim/notify
* 语聊房需在NeRoom控制台配置neRoom服务抄送, 抄送地址:http://{nemo服务域名}/nemo/entertainmentLive/nim/notify
