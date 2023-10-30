package com.example.parkingapi.service

import com.example.parkingapi.exception.ApiException
import com.example.parkingapi.exception.ApiResponse
import com.example.parkingapi.model.*
import com.example.parkingapi.repository.*
import com.example.parkingapi.service.mpesa.MpesaResponse
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.asFlux
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.ConcurrentHashMap
import java.util.function.Predicate

@Service
class ParkingService(
    private val spotRepository: SpotRepository,
    private val parkingRepository: ParkingRepository,
    private val paymentRepository: PaymentRepository,
    private val utenteRepository: UtenteRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val mpesaService: MpesaService,
    private val sinks: Sinks.Many<String>,
    private val mapper: ObjectMapper,
) {

    val sessions by lazy {
        ConcurrentHashMap<Int, Sinks.Many<String>>()
    }

    fun getSession() {
        parkingRepository.findAll()
            .map {
                it.spot = spotRepository.findByUtenteId(it.utenteId)
                it.spot.status = Spot.Status.ENTRY
                it.utente = utenteRepository.findById(it.utenteId)
                it
            }.asFlux()
            .subscribe {
                sinks.emitNext(mapper.writeValueAsString(it), Sinks.EmitFailureHandler.FAIL_FAST)
            }
    }

    suspend fun parquear(utenteId: Int): Parking {
        val utente =
            utenteRepository.findById(utenteId)
                ?: throw ApiException("Utente credentials not found with id {$utenteId}")

        //TODO verifica se o cliente esta em divida do mes anterior
        if (!checkIfAlreadyPayInMonth(utente)) {
            throw ApiException("Pague a subscricao mensal para poder estacionar!")
        }

        val isClosed =
            Predicate { localTime: LocalTime ->
                (localTime.isBefore(LocalTime.of(6, 0, 0))
                        ||
                        localTime.isAfter(LocalTime.of(23, 0, 0)))
            }.test(LocalTime.now())
        if (isClosed) throw ApiException("Estacionamento Fechado! Volte mais tarde")
        if (spotRepository.existsByUtenteId(utenteId)) throw ApiException("Voce ja tem um carro no estacionamento!")
        val spot = spotRepository.findAll().firstOrNull { spot -> spot.isAvailable }
            ?: throw RuntimeException("Nao existe espaco disponivel")
        spot.utenteId = utenteId
        spot.isAvailable = false
        spot.status = Spot.Status.ENTRY
        var parking: Parking? = null

        try {
            parking = parkingRepository.save(Parking(utenteId))
            val savedSpot = spotRepository.save(spot)
            parking.spot = savedSpot
            parking.utente = utente
            sinks.emitNext(mapper.writeValueAsString(parking), Sinks.EmitFailureHandler.FAIL_FAST)
            return parking
        } catch (e: Exception) {
            if (parking != null) {
                parkingRepository.delete(parking)
            }
            throw ApiException("Ocorreu um erro!")
        }

    }

    //TODO() Falta resolver valor Mensal
    suspend fun valorApagar(utenteId: Int): Payment {
        if (!spotRepository.existsByUtenteId(utenteId)) throw ApiException("Voce nao tem um carro no estacionamento!")
        val parking =
            parkingRepository.findByUtenteId(utenteId)
                ?: throw ApiException("Voce nao tem um carro no estacionamento!")

        val utente = utenteRepository.findById(utenteId) ?: throw ApiException("Utente not found!")
        val exitTime = LocalDateTime.now()
        val between = Duration.between(parking.entranceTime, exitTime)
        val result = between.toMinutes() / 60f
        val amount =
            (if (result.toInt() == 0) Payment.AMOUNT_PER_HOUR * 1 else Payment.AMOUNT_PER_HOUR * result).toInt()

        val localTime = exitTime.toLocalTime()
        val before = localTime.isBefore(LocalTime.of(6, 0))
        val after = localTime.isAfter(LocalTime.of(23, 0))

        //TODO Verifica se pode ha desconto apos as 23h e antes das 6h
        val discount: Float = if (!before.or(after)) {
            amount * utente.paymentType.discount(utente.type)
        } else {
            0.0f
        }
        return Payment(
            utenteId = utenteId,
            entranceTime = parking.entranceTime,
            exitTime = exitTime,
            timeInParking = timeInParking(between),
            amount = amount - discount,
            discount = discount
        )
    }

    suspend fun payment(utenteId: Int): ApiResponse<Unit> {
        val payment = paymentRepository.save(valorApagar(utenteId))
        val parking = parkingRepository.save(Parking(utenteId))
        parkingRepository.removeByUtenteId(utenteId)
        val spot =
            spotRepository.findByUtenteId(utenteId) ?: throw ApiException("Utente spot {$utenteId} not found")
        spot.status = Spot.Status.EXIT
        spot.isAvailable = true
        spot.utenteId = null
        spotRepository.save(spot)
        parking.spot = spot
        val utente = utenteRepository.findById(utenteId) ?: throw ApiException("Utente account not found $utenteId")
        if (utente.paymentType == Utente.PaymentPlan.Monthly) {
            utente.divida += payment.amount
            val utenteUpdated = utenteRepository.save(utente)
            updateUtente(utenteUpdated)
        }
        sinks.emitNext(mapper.writeValueAsString(parking), Sinks.EmitFailureHandler.FAIL_FAST)
        return ApiResponse("Pagamento efectuado com sucesso!")
    }

    suspend fun paymentWithMpesa(mpesa: Payment.Mpesa): ApiResponse<Unit> {
        val utenteId = mpesa.utenteId
        val paymentCalculate = valorApagar(utenteId)
        val c2bTransaction = mpesaService.c2bTransaction(
            amount = paymentCalculate.amount.toDouble(),
            phoneNumber = mpesa.phoneNumber
        )
        if (c2bTransaction.message == MpesaResponse.SUCCESS.name) {
            val payment = paymentRepository.save(paymentCalculate)
            val parking = parkingRepository.save(Parking(utenteId))
            parkingRepository.removeByUtenteId(utenteId)
            val spot =
                spotRepository.findByUtenteId(utenteId) ?: throw ApiException("Utente spot {$utenteId} not found")
            spot.status = Spot.Status.EXIT
            spot.isAvailable = true
            spot.utenteId = null
            spotRepository.save(spot)
            parking.spot = spot
            val utente = utenteRepository.findById(utenteId) ?: throw ApiException("Utente account not found $utenteId")
            if (utente.paymentType == Utente.PaymentPlan.Monthly) {
                utente.divida += payment.amount
                val utenteUpdated = utenteRepository.save(utente)
                updateUtente(utenteUpdated)
            }
            sinks.emitNext(mapper.writeValueAsString(parking), Sinks.EmitFailureHandler.FAIL_FAST)
            return ApiResponse("Pagamento efectuado com sucesso!")
        } else {
            return ApiResponse("Ocorreu um erro ao efectuar o pagamento via mpesa!")
        }
    }


    suspend fun paySubscription(mpesa: Payment.Mpesa): ApiResponse<Unit> {
        val utente = utenteRepository.findById(mpesa.utenteId)
            ?: throw ApiException("Utente account not found ${mpesa.utenteId}")
        return if (utente.divida != 0.0) {
            val transaction = mpesaService.c2bTransaction(
                amount = utente.divida,
                phoneNumber = mpesa.phoneNumber
            )
            if (transaction.message == MpesaResponse.SUCCESS.name) {
                subscriptionRepository.save(entity = Subscription(divida = utente.divida, utenteId = mpesa.utenteId))
                utente.divida = 0.0
                utente.lastSubscription = LocalDateTime.now()
                val save = utenteRepository.save(utente)
                updateUtente(save)
                ApiResponse("Pagamento mensal/divida paga com sucesso!")
            } else {
                ApiResponse("Ocorreu um erro ao efectuar o pagamento via mpesa!")
            }
        } else {
            if (utente.paymentType == Utente.PaymentPlan.Monthly) {
                utente.lastSubscription = LocalDateTime.now()
                utenteRepository.save(utente)
                updateUtente(utente)
            } else {
                throw ApiException("Somente Utente com plano mensal!!!")
            }
            ApiResponse("Plano mensal atualizado!")
        }
    }


    suspend fun getParkingInfo(utenteId: Int): Parking {
        return parkingRepository.findByUtenteId(utenteId) ?: throw ApiException("Parking credentials not found")
    }

    suspend fun findAllParkingWithUtenteId(utenteId: Int): Flow<Payment> {
        return paymentRepository.findAllByUtenteId(utenteId)
    }

    suspend fun findAllParkingPayments(): Flow<Payment> {
        return paymentRepository.findAll()
    }

    private fun timeInParking(duration: Duration): String {
        val milliseconds = duration.toMillis()
        val hours = milliseconds / 1000 / 3600
        val minutes = milliseconds / 1000 % 3600 / 60
        val seconds = milliseconds / 1000 % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }


    fun registerSession(utenteId: String): Flux<String> {
        sessions.putIfAbsent(utenteId.toInt(), Sinks.many().multicast().onBackpressureBuffer())
        val sinks = sessions[utenteId.toInt()]!!
        CoroutineScope(Dispatchers.IO).launch {
            sinks.emitNext(
                mapper.writeValueAsString(utenteRepository.findById(utenteId.toInt())!!),
                Sinks.EmitFailureHandler.FAIL_FAST
            )
        }
        return sinks.asFlux()
    }

    suspend fun updateUtente(utente: Utente) {
        try {
            val sinks = sessions[utente.id]!!
            sinks.emitNext(
                mapper.writeValueAsString(utenteRepository.findById(utente.id)!!),
                Sinks.EmitFailureHandler.FAIL_FAST
            )
        } catch (_: Exception) {
        }
    }


    companion object {
        fun checkIfAlreadyPayInMonth(utente: Utente): Boolean {
            when (utente.paymentType) {
                Utente.PaymentPlan.Daily -> {
                    return true
                }

                Utente.PaymentPlan.Monthly -> {
                    val lastSubscription = utente.lastSubscription
                    val today = LocalDateTime.now()
                    if (lastSubscription.month == today.month) {
                        return true
                    }
                    return utente.divida == 0.0
                }

                else -> return true
            }
        }
    }

}
