package com.suyh.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * @author suyh
 * @since 2024-08-09
 */
@ConfigurationProperties(CustomProperties.PREFIX)
@Validated
@Data
public class CustomProperties {
    public static final String PREFIX = "suyh.declare";

    // key: exchange, value: 该exchange 下面对应的队列名，以及绑定的路由key
    private List<ExchangeProperties> topicExchangeList;

    @Data
    public static class ExchangeProperties {
        private String exchangeName;

        private List<BindingProperties> bindingList;
    }

    @Data
    public static class BindingProperties {
        private String queueName;
        private String routingKey;
    }
}
