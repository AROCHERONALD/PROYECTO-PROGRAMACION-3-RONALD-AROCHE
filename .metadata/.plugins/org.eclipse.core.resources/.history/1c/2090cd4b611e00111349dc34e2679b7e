package com.demo.consumer.service;

import com.demo.consumer.config.AppConfig;
import com.demo.consumer.config.RabbitMQConfig;
import com.demo.consumer.model.Transaccion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public class RabbitConsumerService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApiPostService apiPostService = new ApiPostService();

    public void iniciar() {
        try {
            Connection connection = RabbitMQConfig.getFactory().newConnection();

            for (String cola : AppConfig.COLAS_BANCOS) {
                Channel channel = connection.createChannel();

                channel.queueDeclare(cola, true, false, false, null);
                channel.basicQos(1);

                System.out.println("Escuchando cola: " + cola);

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    long deliveryTag = delivery.getEnvelope().getDeliveryTag();
                    String mensaje = new String(delivery.getBody(), StandardCharsets.UTF_8);

                    try {
                        System.out.println("Mensaje recibido desde " + cola + ": " + mensaje);

                        Transaccion transaccion = objectMapper.readValue(mensaje, Transaccion.class);

                        boolean enviado = apiPostService.enviarConReintento(transaccion);

                        if (enviado) {
                            channel.basicAck(deliveryTag, false);
                            System.out.println("ACK enviado para " + transaccion.getIdTransaccion());
                        } else {
                            channel.basicNack(deliveryTag, false, true);
                            System.out.println("POST falló. Mensaje reencolado.");
                        }

                    } catch (Exception e) {
                        System.err.println("Error procesando mensaje: " + e.getMessage());
                        channel.basicNack(deliveryTag, false, true);
                    }
                };

                channel.basicConsume(cola, false, deliverCallback, consumerTag -> {
                });
            }

            System.out.println("Consumer activo. Esperando mensajes...");
            new CountDownLatch(1).await();

        } catch (Exception e) {
            System.err.println("Error iniciando Consumer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}