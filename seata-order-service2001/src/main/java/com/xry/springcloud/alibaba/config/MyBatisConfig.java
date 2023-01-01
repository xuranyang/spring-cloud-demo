package com.xry.springcloud.alibaba.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"com.xry.springcloud.alibaba.dao"})
public class MyBatisConfig {
}
