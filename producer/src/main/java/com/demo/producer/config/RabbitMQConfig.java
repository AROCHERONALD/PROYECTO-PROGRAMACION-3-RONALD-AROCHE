package com.demo.producer.config;

import com.rabbitmq.client.ConnectionFactory;

public class RabbitMQConfig {

    public static ConnectionFactory getFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(AppConfig.RABBIT_HOST);
        factory.setPort(AppConfig.RABBIT_PORT);
        factory.setUsername(AppConfig.RABBIT_USER);
        factory.setPassword(AppConfig.RABBIT_PASSWORD);
        return factory;
    }
}