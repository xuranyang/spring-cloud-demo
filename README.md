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







