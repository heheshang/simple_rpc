package com.simple.rpc.core.utils;

/**
 * 注册工具类
 *
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-17-下午 4:31
 */
public class RegistryUtil {

    public static final String ZK_BASE_PATH = "/simplerpc";

    public static String getServicePath(String serviceName) {

        return ZK_BASE_PATH + "/services" + "/"+serviceName;

    }


    public static String getServiceInstancePath(String serviceName, String ip, int port) {

        String servicePath = RegistryUtil.getServicePath(serviceName);

        return servicePath + "/" + ip + ":" + port;
    }

}
