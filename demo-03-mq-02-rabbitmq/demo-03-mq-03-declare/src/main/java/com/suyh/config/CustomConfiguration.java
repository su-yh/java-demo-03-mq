package com.suyh.config;

import com.suyh.config.properties.CustomProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author suyh
 * @since 2024-08-09
 */
@Configuration
@EnableConfigurationProperties(CustomProperties.class)
public class CustomConfiguration {
}
