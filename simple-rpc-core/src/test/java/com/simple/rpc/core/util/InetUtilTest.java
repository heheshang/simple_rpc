package com.simple.rpc.core.util;

import com.simple.rpc.core.utils.InetUtil;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Description:
 *
 * @author liuzhengyang
 * @version 1.0
 * @since 2016-12-17
 */
public class InetUtilTest {
	@Test
	public void getLocalIp() throws Exception {
		System.out.println(InetUtil.getLocalIp());
	}

}