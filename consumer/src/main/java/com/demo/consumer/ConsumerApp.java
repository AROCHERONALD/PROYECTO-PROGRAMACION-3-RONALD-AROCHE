package com.demo.consumer;

import com.demo.consumer.service.RabbitConsumerService;

public class ConsumerApp {

    public static void main(String[] args) {
        RabbitConsumerService service = new RabbitConsumerService();
        service.iniciar();
    }
}