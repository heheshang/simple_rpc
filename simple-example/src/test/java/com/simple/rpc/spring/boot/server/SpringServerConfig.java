package com.simple.rpc.spring.boot.server;

import com.simple.rpc.example.HelloImpl;
import com.simple.rpc.example.IHello;
import com.simple.spring.simplerpc.ServerFactoryBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-27  下午 4:47
 */
@SpringBootApplication
public class SpringServerConfig {

    @Bean
    public IHello hello() {

        return new HelloImpl();
    }

    @Bean(initMethod = "start")
    public ServerFactoryBean serverFactoryBean() {

        final ServerFactoryBean serverFactoryBean = new ServerFactoryBean();
        serverFactoryBean.setPort(9090);
        serverFactoryBean.setZkConn("localhost:2181");
        serverFactoryBean.setServiceInterface(IHello.class);
        serverFactoryBean.setServiceImpl(hello());
        serverFactoryBean.setServiceName("hello");

//		serverFactoryBean.start();
        new Thread(new Runnable() {
            public void run() {

                try {
                    serverFactoryBean.getObject();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "RpcServer").start();
        return serverFactoryBean;
    }

    public static void main(String[] args) {

        SpringApplication.run(SpringServerConfig.class, "--server.port=8088");
    }
}
