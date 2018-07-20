package com.simple.rpc.spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 *
 *
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @date 2018-04-27  下午 4:49
 * @version v1.0
 */
@SpringBootApplication
@ImportResource(locations = "application.xml")
public class SpringClientTest {
	public static void main(String[] args) {
		SpringApplication.run(SpringClientTest.class);
	}
}
