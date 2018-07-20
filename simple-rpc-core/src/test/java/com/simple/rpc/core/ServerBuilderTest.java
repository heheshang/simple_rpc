package com.simple.rpc.core;


import com.simple.rpc.core.bootstrap.ServerBuilder;
import com.simple.rpc.core.transport.Server;
import org.junit.Test;

import java.util.Scanner;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2017-01-19
 */
public class ServerBuilderTest {

	@Test
	public void testServerSetWithBuilder() throws InterruptedException {
		Server testBuilder = ServerBuilder.builder()
				.port(8998)
				.zkConn("127.0.0.1:2181")
				.serviceName("testBuilder")
				.serviceImpl(new HelloImpl())
				.build();
		testBuilder.start();

//		int a = new Scanner(System.in).nextInt();

		testBuilder.shutdown();
	}

}