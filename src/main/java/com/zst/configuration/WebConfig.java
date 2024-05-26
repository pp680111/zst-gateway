package com.zst.configuration;

import com.zst.router.GatewayEntranceRouter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(GatewayEntranceRouter.class)
@Configuration
public class WebConfig {
}
