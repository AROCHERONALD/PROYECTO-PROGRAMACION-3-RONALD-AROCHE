package com.demo.producer.service;

import com.demo.producer.config.AppConfig;
import com.demo.producer.model.LoteTransacciones;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiGetService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiGetService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public LoteTransacciones obtenerLote() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AppConfig.GET_URL))
                .GET()
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Error en GET /transacciones. Status: " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), LoteTransacciones.class);
    }
}