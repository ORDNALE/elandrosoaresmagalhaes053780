package com.elandroapi.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.quarkus.websockets.next.OnClose;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OpenConnections;
import io.quarkus.websockets.next.WebSocket;
import io.quarkus.websockets.next.WebSocketConnection;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@WebSocket(path = "/ws/albums")
public class AlbumWebSocket {

    @OnOpen
    public void onOpen(WebSocketConnection connection) {
        Log.infof("WebSocket conectado: %s", connection.id());
    }

    @OnClose
    public void onClose(WebSocketConnection connection) {
        Log.infof("WebSocket desconectado: %s", connection.id());
    }

    @ApplicationScoped
    public static class Broadcaster {

        @Inject
        OpenConnections connections;

        @Inject
        ObjectMapper objectMapper;

        public void broadcast(AlbumNotificationEvent event) {
            try {
                String message = objectMapper.writeValueAsString(event);
                connections.findByEndpointId(AlbumWebSocket.class.getName())
                        .forEach(conn -> conn.sendTextAndAwait(message));
                Log.infof("Notificação enviada: %s", event.titulo());
            } catch (JsonProcessingException e) {
                Log.errorf("Erro ao serializar notificação: %s", e.getMessage());
            }
        }
    }
}
