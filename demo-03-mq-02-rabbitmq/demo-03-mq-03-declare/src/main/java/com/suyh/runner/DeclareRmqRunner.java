package com.suyh.runner;

import com.suyh.config.properties.CustomProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author suyh
 * @since 2024-08-09
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DeclareRmqRunner implements ApplicationRunner {
    private final CustomProperties properties;
    private final AmqpAdmin amqpAdmin;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<CustomProperties.ExchangeProperties> topicExchangeList = properties.getTopicExchangeList();
        for (CustomProperties.ExchangeProperties exchangeProperties : topicExchangeList) {
            TopicExchange topicExchange = new TopicExchange(exchangeProperties.getExchangeName());
            amqpAdmin.declareExchange(topicExchange);

            List<CustomProperties.BindingProperties> bindingList = exchangeProperties.getBindingList();
            for (CustomProperties.BindingProperties bindingProperties : bindingList) {
                String queueName = bindingProperties.getQueueName();
                String routingKey = bindingProperties.getRoutingKey();

                Queue queue = new Queue(queueName);
                amqpAdmin.declareQueue(queue);

                Binding binding = BindingBuilder.bind(queue).to(topicExchange).with(routingKey);
                amqpAdmin.declareBinding(binding);
            }
        }
    }
}
