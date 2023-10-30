package com.example.parkingapi.websocket

import com.example.parkingapi.service.ParkingService
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks

@Component
class ParkingWebSocketHandler(
    private val sinks: Sinks.Many<String>,
    private val parkingService: ParkingService,
) : WebSocketHandler {
    override fun handle(session: WebSocketSession): Mono<Void> {
        parkingService.getSession()
        val messageFlux =
            sinks
                .asFlux()
                .map(session::textMessage)
        return session.send(messageFlux)
    }

}