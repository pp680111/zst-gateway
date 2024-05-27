package com.zst.gateway.configuration;

import com.zst.gateway.router.GatewayEntranceRouter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(GatewayEntranceRouter.class)
@Configuration
public class WebConfig {
}
