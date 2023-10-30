package com.example.parkingapi.websocket

import com.example.parkingapi.service.ParkingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketSession
import reactor.core.publisher.Mono
import java.net.URI

@Component
class UtenteWebSocketHandler(
    private val parkingService: ParkingService,
) : WebSocketHandler {

    override fun handle(session: WebSocketSession): Mono<Void> {
        val id = sessionId(session.handshakeInfo.uri)
        val data = parkingService
            .registerSession(id)
            .map(session::textMessage)
        CoroutineScope(Dispatchers.IO).launch {
            while (session.isOpen) {
                delay(10_000)
            }
            parkingService.sessions.remove(id.toInt())
        }
        return session.send(data)
    }

    fun sessionId(uri: URI): String {
        return uri.toString().split("/").last()
    }
}