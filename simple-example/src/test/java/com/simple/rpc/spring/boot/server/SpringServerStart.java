package com.simple.rpc.spring.boot.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @author ssk www.8win.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-06-13-下午 2:18
 */
@SpringBootApplication
@ImportResource("classpath:application-server.xml")
public class SpringServerStart {

    public static void main(String[] args) {

        SpringApplication.run(SpringServerStart.class);
    }
}
