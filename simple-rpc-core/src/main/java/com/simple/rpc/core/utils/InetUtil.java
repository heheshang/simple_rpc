package com.simple.rpc.core.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ip 工具类
 *
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-17-下午 4:29
 */
public class InetUtil {

    private InetUtil() {

    }

    public static String getLocalIp() {

        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
