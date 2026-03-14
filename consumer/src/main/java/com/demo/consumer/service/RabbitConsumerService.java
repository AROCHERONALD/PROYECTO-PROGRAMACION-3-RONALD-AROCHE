package com.demo.consumer.service;

import com.demo.consumer.config.AppConfig;
import com.demo.consumer.config.RabbitMQConfig;
import com.demo.consumer.model.Transaccion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.MessageProperties;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class RabbitConsumerService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApiPostService apiPostService = new ApiPostService();

    // Guarda los idTransaccion que ya fueron procesados
    private final Set<String> idsProcesados = ConcurrentHashMap.newKeySet();
    
    public void iniciar() {
        try {
            Connection connection = RabbitMQConfig.getFactory().newConnection();

            // Cola para duplicados
            Channel canalDuplicados = connection.createChannel();
            canalDuplicados.queueDeclare(AppConfig.DUPLICADOS_QUEUE, true, false, false, null);

            for (String cola : AppConfig.COLAS_BANCOS) {
                Channel channel = connection.createChannel();

                channel.queueDeclare(cola, true, false, false, null);
                channel.basicQos(1);

                System.out.println("Escuchando cola: " + cola);

                DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                    long deliveryTag = delivery.getEnvelope().getDeliveryTag();
                    String mensaje = new String(delivery.getBody(), StandardCharsets.UTF_8);
                    
                    try {
                        Transaccion transaccion = objectMapper.readValue(mensaje, Transaccion.class);
                        String idTransaccion = transaccion.getIdTransaccion();

                        System.out.println("Cola atendida: " + cola + " | idTransaccion: " + idTransaccion);

                        // Si ya fue procesada, no va al POST
                        if (idsProcesados.contains(idTransaccion)) {
                            canalDuplicados.basicPublish(
                                    "",
                                    AppConfig.DUPLICADOS_QUEUE,
                                    MessageProperties.PERSISTENT_TEXT_PLAIN,
                                    delivery.getBody()
                            );

                            System.out.println("idTransaccion: " + idTransaccion
                                    + " | estado: DUPLICADA"
                                    + " | cola destino: " + AppConfig.DUPLICADOS_QUEUE);

                            channel.basicAck(deliveryTag, false);
                            return;
                        }
                        
                     // Si es nueva, sigue el flujo normal
                        boolean enviado = apiPostService.enviarConReintento(transaccion);

                        if (enviado) {
                            idsProcesados.add(idTransaccion);

                            System.out.println("idTransaccion: " + idTransaccion
                                    + " | estado: PROCESADA"
                                    + " | cola destino: POST");

                            channel.basicAck(deliveryTag, false);
                            System.out.println("ACK enviado para " + idTransaccion);
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