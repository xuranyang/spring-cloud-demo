# spring-cloud-demo
this is spring cloud demo

```
create database db2022;

CREATE TABLE db2022.`payment`(
`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
`serial` varchar(200) DEFAULT '',
PRIMARY KEY (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
```

* 1.建module
* 2.改pom
* 3.写yml
* 4.主启动
* 5.业务类

```
hosts文件
127.0.0.1 eureka7001.com
127.0.0.1 eureka7002.com
```

* eureka7001.com:7001
* eureka7002.com:7002


API
```
localhost:8001/payment/get/1
localhost:80/consumer/payment/get/2
localhost:80/consumer/payment/create?serial=111
localhost:8002/payment/get/1

http://localhost:8001/actuator/health
http://localhost:8001/payment/discovery
```


### Nacos
https://github.com/alibaba/nacos/releases <br>
解压安装包，直接运行bin目录下的startup.cmd -m standalone<br>
命令运行成功后直接访问http://localhost:8848/nacos，默认账号密码都是nacos

http://localhost:9001/payment/nacos/1
http://localhost:9002/payment/nacos/1

Nacos负载均衡<br>
http://localhost:83/consumer/payment/nacos/13


Nacos把默认AP切换成CP<br>
```shell
curl -X PUT '$NACOS_SERVER:8848/nacos/v1/ns/operator/switches?entry=serverMode&value=CP
```


http://localhost:3377/config/info

config:
    info: nacos config center, nacos-config-client-dev.yaml, version=1


<br>
<br>
<br>

### Sentinel
https://github.com/alibaba/Sentinel/releases

```shell
java -jar sentinel-dashboard-1.8.6.jar
```
localhost:8080
<br>
登录账号密码均为 sentinel

Sentinel采用的懒加载说明：<br>
执行一次访问即可
- http://localhost:8401/testA
- http://localhost:8401/testB

效果：sentinel 8080 正在监控微服务8401
<br><br>

#### Sentinel流控-QPS直接失败
测试：快速多次点击访问 http://localhost:8401/testA 
<br>
结果：返回页面 Blocked by Sentinel (flow limiting)

<br>

#### Sentinel热点key

```
http://localhost:8401/testHotKey?p1=abc
http://localhost:8401/testHotKey?p1=abc&p2=33
http://localhost:8401/testHotKey?p2=abc


# 当p1等于5的时候，阈值变为200
# 当p1不等于5的时候，阈值就是平常的1
http://localhost:8401/testHotKey?p1=5
http://localhost:8401/testHotKey?p1=3
```

先启动Nacos和Sentinel
http://localhost:8401/byResource
```json
{"code":200,"message":"按资源名称限流测试OK","data":{"id":2020,"serial":"serial001"}}
```
<br>

疯狂点击，返回了自己定义的限流处理信息，限流发生
```json
{"code":444,"message":"com.alibaba.csp.sentinel.slots.block.flow.FlowException\t 服务不可用","data":null}
```

<br>

按照Url地址限流 + 后续处理
http://localhost:8401/rateLimit/byUrl
```json
{"code":200,"message":"按url限流测试OK","data":{"id":2020,"serial":"serial002"}}
```

通过访问的URL来限流，会返回Sentinel自带默认的限流处理信息<br>
```Blocked by Sentinel (flow limiting)```

<br>

http://localhost:8401/rateLimit/customerBlockHandler
```json
{"code":200,"message":"按客戶自定义","data":{"id":2020,"serial":"serial003"}}
```
<br>
多次刷新后，我们自定义兜底方法的字符串信息就返回到前端

```json
{"code":4444,"message":"按客戶自定义,global handlerException----2","data":null}
```


### Seata

##### Seata安装
1. 步骤一：下载启动包<br>
https://github.com/seata/seata/releases/download/v1.4.2/seata-server-1.4.2.zip
<br>

2. 步骤二：建表(仅db)<br>
```
create database seata;
```
seata/script/server/db/mysql.sql
<br>

3. 步骤三：修改store.mode<br>
启动包: seata-->conf-->application.yml，修改store.mode="db或者redis"
<br>
1.5.0以下版本:
<br>
**启动包: seata-->conf-->file.conf，修改store.mode="db或者redis"**
<br>

4. 步骤四：修改数据库连接|redis属性配置<br>
启动包: seata-->conf-->application.example.yml中附带额外配置，将其db|redis相关配置复制至application.yml,进行修改store.db或store.redis相关属性。
<br>
1.5.0以下版本:
<br>
**启动包: seata-->conf-->file.conf，修改store.db或store.redis相关属性。**

5. 步骤五：修改registry.conf<br>

6. 步骤六：启动<br>
源码启动: 执行ServerApplication.java的main方法
命令启动: seata-server.sh -h 127.0.0.1 -p 8091 -m db
<br><br>
1.5.0以下版本
<br>
源码启动: 执行Server.java的main方法
命令启动: seata-server.sh -h 127.0.0.1 -p 8091 -m db -n 1 -e test



<br><br>
#### Seata业务数据库准备
```mysql
CREATE DATABASE seata_order;
CREATE DATABASE seata_storage;
CREATE DATABASE seata_account;

CREATE TABLE seata_order.t_order (
    `id` BIGINT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT(11) DEFAULT NULL COMMENT '用户id',
    `product_id` BIGINT(11) DEFAULT NULL COMMENT '产品id',
    `count` INT(11) DEFAULT NULL COMMENT '数量',
    `money` DECIMAL(11,0) DEFAULT NULL COMMENT '金额',
    `status` INT(1) DEFAULT NULL COMMENT '订单状态: 0:创建中; 1:已完结'
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE seata_storage.t_storage (
    `id` BIGINT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `product_id` BIGINT(11) DEFAULT NULL COMMENT '产品id',
    `total` INT(11) DEFAULT NULL COMMENT '总库存',
    `used` INT(11) DEFAULT NULL COMMENT '已用库存',
    `residue` INT(11) DEFAULT NULL COMMENT '剩余库存'
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

INSERT INTO seata_storage.t_storage(`id`, `product_id`, `total`, `used`, `residue`)
VALUES ('1', '1', '100', '0','100');


CREATE TABLE seata_account.t_account(
    `id` BIGINT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT 'id',
    `user_id` BIGINT(11) DEFAULT NULL COMMENT '用户id',
    `total` DECIMAL(10,0) DEFAULT NULL COMMENT '总额度',
    `used` DECIMAL(10,0) DEFAULT NULL COMMENT '已用余额',
    `residue` DECIMAL(10,0) DEFAULT '0' COMMENT '剩余可用额度'
) ENGINE=INNODB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

INSERT INTO seata_account.t_account(`id`, `user_id`, `total`, `used`, `residue`)
VALUES ('1', '1', '1000', '0', '1000');



drop table seata_order.`undo_log`;
CREATE TABLE seata_order.`undo_log` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `branch_id` bigint(20) NOT NULL,
    `xid` varchar(100) NOT NULL,
    `context` varchar(128) NOT NULL,
    `rollback_info` longblob NOT NULL,
    `log_status` int(11) NOT NULL,
    `log_created` datetime NOT NULL,
    `log_modified` datetime NOT NULL,
    `ext` varchar(100) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

drop table seata_storage.`undo_log`;
CREATE TABLE seata_storage.`undo_log` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `branch_id` bigint(20) NOT NULL,
    `xid` varchar(100) NOT NULL,
    `context` varchar(128) NOT NULL,
    `rollback_info` longblob NOT NULL,
    `log_status` int(11) NOT NULL,
    `log_created` datetime NOT NULL,
    `log_modified` datetime NOT NULL,
    `ext` varchar(100) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

drop table seata_account.`undo_log`;
CREATE TABLE seata_account.`undo_log` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT,
    `branch_id` bigint(20) NOT NULL,
    `xid` varchar(100) NOT NULL,
    `context` varchar(128) NOT NULL,
    `rollback_info` longblob NOT NULL,
    `log_status` int(11) NOT NULL,
    `log_created` datetime NOT NULL,
    `log_modified` datetime NOT NULL,
    `ext` varchar(100) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `ux_undo_log` (`xid`,`branch_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
```


http://localhost:2001/order/create?userId=1&productId=1&count=10&money=100
```
{"code":200,"message":"订单创建成功","data":null}
```

```
Read timed out executing POST http://seata-account-service/account/decrease?userId=1&money=100
```

```mysql
SELECT * from seata_order.t_order;
--
-- 1	1	1	10	100	1
-- 2	1	1	10	100	0

SELECT * from seata_storage.t_storage;
-- 1	1	100	0	100
-- 1	1	100	10	90
-- 1	1	100	20	80

SELECT * from seata_account.t_account;
-- 1	1	1000	0	1000
-- 1	1	1000	100	900
-- 1	1	1000	200	800
```


<br>

#### Sentinel服务熔断
sentinel整合 ribbon + openFeign + fallback <br>
Ribbon系列
- 启动nacos和sentinel
- 提供者9003/9004
- 消费者84
```
http://localhost:9003/paymentSQL/1
http://localhost:9004/paymentSQL/1

http://localhost:84/consumer/fallback/1
http://localhost:84/consumer/fallback/4
http://localhost:84/consumer/fallback/5
```

##### Sentinel服务熔断无配置
没有任何配置 - 给用户error页面，不友好 <br>
java.lang.IllegalArgumentException: IllegalArgumentException,非法参数异常.... <br>
java.lang.NullPointerException: NullPointerException,该ID没有对应记录,空指针异常 <br>

<br>

##### Sentinel服务熔断只配置fallback
```json
{"code":200,"message":"from mysql,serverPort:  9004","data":null}
```
```json
{"code":444,"message":"兜底异常handlerFallback,exception内容  NullPointerException,该ID没有对应记录,空指针异常","data":{"id":5,"serial":"null"}}
```
<br>

##### Sentinel服务熔断只配置blockHandler
Sentinel->簇点链路->列表视图->资源名:fallback->操作:熔断(老版本叫降级)
->熔断策略:异常数->异常数:2->统计时长:2000ms->熔断时长:1s(随意)

快速点击 <br>
http://localhost:84/consumer/fallback/4 <br>
http://localhost:84/consumer/fallback/5 <br>

{"code":445,"message":"blockHandler-sentinel限流,无此流水: blockException  null","data":{"id":4,"serial":"null"}}
{"code":445,"message":"blockHandler-sentinel限流,无此流水: blockException  null","data":{"id":5,"serial":"null"}}

<br>

##### Sentinel服务熔断fallback和blockHandler都配置
若blockHandler和fallback 都进行了配置，则被限流降级而抛出BlockException时只会进入blockHandler处理逻辑。
<br>
Sentinel->簇点链路->列表视图->资源名:fallback->操作:流控 ->阈值类型:QPS->单机阈值:1
```json
{"code":445,"message":"blockHandler-sentinel限流,无此流水: blockException  null","data":{"id":1,"serial":"null"}}
```
```json
{"code":444,"message":"兜底异常handlerFallback,exception内容  IllegalArgumentException,非法参数异常....","data":{"id":4,"serial":"null"}}
```
```json
{"code":445,"message":"blockHandler-sentinel限流,无此流水: blockException  null","data":{"id":4,"serial":"null"}}
```
```json
{"code":444,"message":"兜底异常handlerFallback,exception内容  NullPointerException,该ID没有对应记录,空指针异常","data":{"id":5,"serial":"null"}}
```
```json
{"code":445,"message":"blockHandler-sentinel限流,无此流水: blockException  null","data":{"id":5,"serial":"null"}}
```

<br>

##### Sentinel服务熔断exceptionsToIgnore
exceptionsToIgnore，忽略指定异常，即这些异常不用兜底方法处理。

<br>

##### Sentinel服务熔断OpenFeign
Feign组件一般是消费侧<br>
修改84模块:<br>
- yml文件
```yml
# 激活Sentinel对Feign的支持
feign:
  sentinel:
    enabled: true
```
- 业务类

带@Feignclient注解的业务接口，fallback = PaymentFallbackService.class

- 主启动

带@EnableFeignClients注解

<br>


http://localhost:84/consumer/openfeign/1

```json
{"code":200,"message":"from mysql,serverPort:  9003","data":{"id":1,"serial":"28a8c1e3bc2742d8848569891fb42181"}}
```

<br>
直接F5刷新页面 http://localhost:84/consumer/openfeign/1 <br>
此时故意关闭9003和9004微服务提供者，84消费侧自动降级，不会被耗死。

```json
{"code":44444,"message":"服务降级返回,---PaymentFallbackService","data":{"id":1,"serial":"errorSerial"}}
```

<br>

##### Sentinel持久化规则
一旦我们重启应用，sentinel规则将消失，生产环境需要将配置规则进行持久化。<br>
将限流配置规则持久化进Nacos保存，只要刷新8401某个rest地址，sentinel控制台的流控规则就能看到，只要Nacos里面的配置不删除，针对8401上sentinel上的流控规则持续有效。<br>

- yml文件
```yml
      datasource: #<---------------------------关注点，添加Nacos数据源配置
        ds1:
          nacos:
            server-addr: localhost:8848
            dataId: cloudalibaba-sentinel-service
            groupId: DEFAULT_GROUP
            data-type: json
            rule-type: flow
```

添加Nacos业务规则配置
<br>
配置列表->创建配置
<br>
- Data ID：cloudalibaba-sentinel-service <br>
- 配置格式：JSON
```json
[{
    "resource": "/rateLimit/byUrl",
    "limitApp": "default",
    "grade": 1,
    "count": 1, 
    "strategy": 0,
    "controlBehavior": 0,
    "clusterMode": false
}]
```
- resource：资源名称；
- limitApp：来源应用；
- grade：阈值类型，0表示线程数, 1表示QPS；
- count：单机阈值；
- strategy：流控模式，0表示直接，1表示关联，2表示链路；
- controlBehavior：流控效果，0表示快速失败，1表示Warm Up，2表示排队等待；
- clusterMode：是否集群。

<br>

正常访问
http://localhost:8401/rateLimit/byUrl

```json
{"code":200,"message":"按url限流测试OK","data":{"id":2020,"serial":"serial002"}}
```

快速访问测试接口 
- http://localhost:8401/rateLimit/byUrl 
- 页面返回Blocked by Sentinel (flow limiting)

<br>停止8401再看sentinel - 停机后发现流控规则没有了


<br>重新启动8401再看sentinel
- 调用1次 - http://localhost:8401/rateLimit/byUrl
- 重新配置出现了，持久化验证通过


