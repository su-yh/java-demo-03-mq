package com.suyh.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.RabbitRetryTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateConfigurer;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.stream.Collectors;

/**
 * @author suyh
 * @since 2025-08-08
 */
@Configuration
public class MultiRabbitmqAutoConfiguration {

    // 消费者
    @ConfigurationProperties(prefix = "base.rabbitmq.consumer")
    @Primary
    @Bean
    public RabbitProperties consumerRabbit() {
        return new RabbitProperties();
    }

    @Bean(name = "consumerConnectionFactory")
    @Primary
    public ConnectionFactory consumerConnectionFactory(
            @Qualifier("consumerRabbit") RabbitProperties consumerRabbit) {
        return buildConnectionFactory(consumerRabbit);
    }

    private static ConnectionFactory buildConnectionFactory(RabbitProperties rabbitProperties) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitProperties.getHost());
        connectionFactory.setPort(rabbitProperties.getPort());
        connectionFactory.setUsername(rabbitProperties.getUsername());
        connectionFactory.setPassword(rabbitProperties.getPassword());
        connectionFactory.setVirtualHost(rabbitProperties.getVirtualHost());
        return connectionFactory;
    }

    @Bean("consumerContainerFactory")
    public SimpleRabbitListenerContainerFactory consumerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            @Qualifier("consumerConnectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConcurrentConsumers(10);
        factory.setMaxConcurrentConsumers(20);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        configurer.configure(factory, connectionFactory);
        return factory;
    }

    //  生产者
    @ConfigurationProperties(prefix = "base.rabbitmq.product")
    @Bean
    public RabbitProperties productRabbit() {
        return new RabbitProperties();
    }

    @Bean(name = "productConnectionFactory")
    public ConnectionFactory productConnectionFactory(
            @Qualifier("productRabbit") RabbitProperties productRabbit) {
        return buildConnectionFactory(productRabbit);
    }

    @Bean("productTemplateConfigurer")
    public RabbitTemplateConfigurer productTemplateConfigurer(
            @Qualifier("productRabbit") RabbitProperties properties,
            ObjectProvider<MessageConverter> messageConverter,
            ObjectProvider<RabbitRetryTemplateCustomizer> retryTemplateCustomizers) {
        RabbitTemplateConfigurer configurer = new RabbitTemplateConfigurer(properties);
        configurer.setMessageConverter(messageConverter.getIfUnique());
        configurer.setRetryTemplateCustomizers(retryTemplateCustomizers.orderedStream().collect(Collectors.toList()));
        return configurer;
    }

    @Bean(name = "productRabbitTemplate")
    public RabbitTemplate productRabbitTemplate(
            @Qualifier("productTemplateConfigurer") RabbitTemplateConfigurer configurer,
            @Qualifier("productConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        configurer.configure(rabbitTemplate, connectionFactory);
        return rabbitTemplate;
    }
}
