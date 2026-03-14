package com.demo.producer.service;

import com.demo.producer.config.RabbitMQConfig;
import com.demo.producer.model.Transaccion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class RabbitProducerService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void enviarTodas(List<Transaccion> transacciones) {
        try (Connection connection = RabbitMQConfig.getFactory().newConnection();
             Channel channel = connection.createChannel()) {

            for (Transaccion transaccion : transacciones) {
                String nombreCola = transaccion.getBancoDestino();

                channel.queueDeclare(nombreCola, true, false, false, null);

                String mensajeJson = objectMapper.writeValueAsString(transaccion);

                AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                        .contentType("application/json")
                        .deliveryMode(2)
                        .build();

                channel.basicPublish(
                        "",
                        nombreCola,
                        properties,
                        mensajeJson.getBytes(StandardCharsets.UTF_8)
                );

                System.out.println("Enviada transacción " + transaccion.getIdTransaccion()
                        + " a la cola " + nombreCola);
            }

        } catch (Exception e) {
            System.err.println("Error enviando transacciones a RabbitMQ: " + e.getMessage());
            e.printStackTrace();
        }
    }
}