package com.zst.gateway.core;

import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Properties;

/**
 * 用于注册GatewayEntranceHandler到WebFlux的请求分发体系中的配置类
 */
public class GatewayEntranceRegister implements ApplicationContextAware, ApplicationListener<ApplicationReadyEvent> {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        SimpleUrlHandlerMapping mapping = applicationContext.getBean(SimpleUrlHandlerMapping.class);
        Properties properties = new Properties();
        properties.setProperty("/{serviceId}/**", "gatewayWebHandler");
        mapping.setMappings(properties);
        mapping.initApplicationContext();
    }
}
