package com.xry.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/*
 * 1.建module
 * 2.改pom
 * 3.写yml
 * 4.主启动
 * 5.业务类
 *
CREATE TABLE db2022.`payment`(
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `serial` varchar(200) DEFAULT '',
    PRIMARY KEY (`id`)
)ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4;
*/

@SpringBootApplication
@EnableEurekaClient // 添加Eureka注解
public class PaymentMain8002 {
    public static void main(String[] args) {
        /**
         * localhost:8001/payment/get/1
         * localhost:8001/payment/create?serial=ame
         */
        SpringApplication.run(PaymentMain8002.class, args);
    }
}
