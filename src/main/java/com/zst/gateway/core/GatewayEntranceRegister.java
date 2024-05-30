package com.zst.gateway.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;

import java.util.Properties;

public class GatewayEntranceRegister implements WebHandler, ApplicationContextAware, ApplicationListener<ApplicationReadyEvent> {
    private ApplicationContext applicationContext;

    @Autowired
    private GatewayResponseHandler gatewayResponseHandler;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        SimpleUrlHandlerMapping mapping = applicationContext.getBean(SimpleUrlHandlerMapping.class);
        Properties properties = new Properties();
        properties.setProperty("/**", "gatewayEntranceHandler");
        mapping.setMappings(properties);
        mapping.initApplicationContext();
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange) {
        return gatewayResponseHandler.handle(exchange);
    }
}
