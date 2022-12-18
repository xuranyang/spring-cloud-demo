package com.xry.springcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaMain7001 {
    // http://localhost:7001/

    /**
     * Eureka集群环境
     * hosts文件:
     * 127.0.0.1 eureka7001.com
     * 127.0.0.1 eureka7002.com
     * eureka7001.com:7001
     * eureka7002.com:7002
     */
    public static void main(String[] args) {
        SpringApplication.run(EurekaMain7001.class, args);
    }
}