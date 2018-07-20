package com.simple.admin.controller;

import com.google.common.base.Splitter;
import com.simple.admin.model.ServiceModel;
import com.simple.admin.model.ServiceProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @version v1.0
 * @date 2018-04-27-下午 3:01
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final String ZK_PATH_PREFIX = "/simplerpc/services";

    private CuratorFramework curatorFramework;

    @PostConstruct
    public void init() {

        curatorFramework = CuratorFrameworkFactory.newClient("127.0.0.1:2181", new ExponentialBackoffRetry(1000, 3));

        curatorFramework.start();
    }

    @RequestMapping("/index")
    public String index(Model model) throws Exception {

        List<String> services = curatorFramework.getChildren().forPath(ZK_PATH_PREFIX);

        final List<ServiceModel> serviceModels = new ArrayList<>();

        if (!CollectionUtils.isEmpty(services)) {

            for (String s : services) {

                ServiceModel serviceModel = new ServiceModel();

                serviceModel.setServiceName(s);

                List<ServiceProvider> providers = new ArrayList<>();

                List<String> serverPayLoadList = curatorFramework.getChildren().forPath(ZK_PATH_PREFIX + "/" + s);

                if (!CollectionUtils.isEmpty(serverPayLoadList)) {

                    for (String serverPayload : serverPayLoadList) {

                        ServiceProvider serviceProvider = new ServiceProvider();

                        List<String> serviceProviderPayLoadTokens = Splitter.on(":").splitToList(serverPayload);

                        serviceProvider.setIp(serviceProviderPayLoadTokens.get(0));

                        serviceProvider.setPort(serviceProviderPayLoadTokens.get(1));

                        providers.add(serviceProvider);

                    }
                }
                serviceModel.setServiceProviders(providers);

                serviceModels.add(serviceModel);

            }

        }

        model.addAttribute("services", serviceModels);

        return "index";
    }
}
