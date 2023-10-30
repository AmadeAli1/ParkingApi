package com.example.parkingapi.configuration

import com.example.parkingapi.websocket.ParkingWebSocketHandler
import com.example.parkingapi.websocket.UtenteWebSocketHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@Configuration
class WebSocketConfiguration(
    private val parkingWebSocketHandler: ParkingWebSocketHandler,
    private val utenteWebSocketHandler: UtenteWebSocketHandler,
) {
    @Bean
    fun handlerMapping(): SimpleUrlHandlerMapping {
        val map = mutableMapOf<String, WebSocketHandler>()
        map["/api/ws/parking"] = parkingWebSocketHandler
        map["/api/ws/utente/{id}"] = utenteWebSocketHandler
        return SimpleUrlHandlerMapping(map, 1)
    }

    @Bean
    fun webSocketHandlerAdapter(): WebSocketHandlerAdapter {
        return WebSocketHandlerAdapter()
    }


}