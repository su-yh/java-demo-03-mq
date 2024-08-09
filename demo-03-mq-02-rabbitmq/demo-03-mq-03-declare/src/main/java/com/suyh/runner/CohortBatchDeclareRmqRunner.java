package com.suyh.runner;

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

/**
 * @author suyh
 * @since 2024-08-09
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CohortBatchDeclareRmqRunner implements ApplicationRunner {
    public static final String EXCHANGE_NAME = "suyhCohortBatchExchange";

    private final AmqpAdmin amqpAdmin;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        TopicExchange topicExchange = new TopicExchange(EXCHANGE_NAME);
        amqpAdmin.declareExchange(topicExchange);

        {
            Queue queue = new Queue("suyhCohortBatch");
            Binding binding = BindingBuilder.bind(queue).to(topicExchange).with("suyhCohortBatch");
            amqpAdmin.declareQueue(queue);
            amqpAdmin.declareBinding(binding);
        }

        {
            Queue queue = new Queue("suyhCohortRetentionBatch");
            Binding binding = BindingBuilder.bind(queue).to(topicExchange).with("suyhCohortRetentionBatch");
            amqpAdmin.declareQueue(queue);
            amqpAdmin.declareBinding(binding);
        }

    }
}
