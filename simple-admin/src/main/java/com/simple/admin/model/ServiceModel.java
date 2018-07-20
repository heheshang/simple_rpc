package com.simple.admin.model;

import lombok.Data;

import java.util.List;

/**
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-27-下午 2:58
 */
@Data
public class ServiceModel {
    private String serviceName;
    private String startTime;
    private List<ServiceProvider> serviceProviders;
}
