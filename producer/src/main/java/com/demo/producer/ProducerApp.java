package com.demo.producer;

import com.demo.producer.model.LoteTransacciones;
import com.demo.producer.service.ApiGetService;
import com.demo.producer.service.RabbitProducerService;

public class ProducerApp {

    public static void main(String[] args) {
        try {
            ApiGetService apiGetService = new ApiGetService();
            RabbitProducerService rabbitProducerService = new RabbitProducerService();

            LoteTransacciones lote = apiGetService.obtenerLote();

            System.out.println("Lote recibido: " + lote.getLoteId());
            System.out.println("Cantidad de transacciones: " + lote.getTransacciones().size());

            rabbitProducerService.enviarTodas(lote.getTransacciones());

            System.out.println("Producer finalizado correctamente.");

        } catch (Exception e) {
            System.err.println("Error en Producer: " + e.getMessage());
            e.printStackTrace();
        }
    }
}