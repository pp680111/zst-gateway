package com.zst.configuration;

import com.zst.router.RouterConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(RouterConfiguration.class)
@Configuration
public class WebConfiguration {
}
