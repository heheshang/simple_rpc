package com.simple.rpc.example;

/**
 *
 *
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @date 2018-04-27  下午 4:43
 * @version v1.0
 */
public class HelloImpl implements IHello {

	@Override
	public String say(String hello) {
		return "return " + hello;
	}

	@Override
	public int sum(int a, int b) {
		return a + b;
	}

	@Override
	public int sum(Integer a, Integer b) {
		return a + b * 3;
	}

}
