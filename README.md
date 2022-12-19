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