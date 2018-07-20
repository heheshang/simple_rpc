package com.simple.spring.simplerpc;


import com.simple.rpc.core.bootstrap.ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

/**
 *
 *
 * @author ssk www.hnapay.com Inc.All rights reserved
 * @date 2018-04-27  下午 3:34
 * @version v1.0
 */
public class ClientFactoryBean<T> implements FactoryBean<T> {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientFactoryBean.class);

	private Class<T> serviceInterface;
	private String serviceName;
	private String zkConn;

	@Override
	public T getObject() {
		return ClientBuilder.<T>builder().zkConn(zkConn)
				.serviceName(serviceName)
				.serviceInterface(serviceInterface)
				.build();
	}

	@Override
	public Class<?> getObjectType() {
		return serviceInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public Class<T> getServiceInterface() {
		return serviceInterface;
	}

	public void setServiceInterface(Class<T> serviceInterface) {
		this.serviceInterface = serviceInterface;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getZkConn() {
		return zkConn;
	}

	public void setZkConn(String zkConn) {
		this.zkConn = zkConn;
	}
}
