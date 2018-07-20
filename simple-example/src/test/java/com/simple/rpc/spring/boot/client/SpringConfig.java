package com.simple.rpc.spring.boot.client;

import com.simple.rpc.example.IHello;
import com.simple.spring.simplerpc.ClientFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-27  下午 4:47
 */
@Configuration
@RestController
@SpringBootApplication
@RequestMapping("/test")
public class SpringConfig {

    @Bean
    public IHello hello() {

        ClientFactoryBean<IHello> clientFactoryBean = new ClientFactoryBean<IHello>();
        clientFactoryBean.setZkConn("localhost:2181");
        clientFactoryBean.setServiceName("hello");
        clientFactoryBean.setServiceInterface(IHello.class);
        return clientFactoryBean.getObject();
    }

    @Resource
    private IHello hello;

    @RequestMapping("/hello")
    public String hello(String say) {

        return hello.say(say);
    }

    public static void main(String[] args) {

        SpringApplication.run(SpringConfig.class, "--server.port=8081");
    }
}
