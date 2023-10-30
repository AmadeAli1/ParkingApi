package com.example.parkingapi.controller

import com.example.parkingapi.exception.ApiResponse
import com.example.parkingapi.model.Parking
import com.example.parkingapi.model.Payment
import com.example.parkingapi.service.ParkingService
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/parking")
@RestController
class ParkingController(
    private val parkingService: ParkingService,
) {

    @GetMapping("/parquear")
    suspend fun parking(
        @RequestParam("utenteId") utenteId: Int,
    ): Parking {
        return parkingService.parquear(utenteId)
    }

    @GetMapping("/payment/detail")
    suspend fun valorAPagar(@RequestParam("utenteId") utenteId: Int): Payment {
        return parkingService.valorApagar(utenteId)
    }


    @GetMapping("/payment/confirm")
    suspend fun payment(@RequestParam("utenteId") utenteId: Int): ApiResponse<Unit> {
        return parkingService.payment(utenteId)
    }

    @GetMapping("/parquear/detail")
    suspend fun getParkingInfo(@RequestParam("utenteId") utenteId: Int): Parking {
        return parkingService.getParkingInfo(utenteId)
    }

    @PostMapping("/payment/mpesa")
    suspend fun paymentWithMpesa(
        @Valid @RequestBody mpesa: Payment.Mpesa
    ): ApiResponse<Unit> {
        println(mpesa)
        return parkingService.paymentWithMpesa(mpesa)
    }

    @GetMapping("/history")
    suspend fun findAllParkingHistory(
        @RequestParam("utenteId") utenteId: Int,
    ): Flow<Payment> {
        return parkingService.findAllParkingWithUtenteId(utenteId)
    }

    @GetMapping("/payment/all")
    suspend fun findAllPayments() = parkingService.findAllParkingPayments()

    @PostMapping("/payment/subscription")
    suspend fun paymentSubscription(
        @Valid @RequestBody mpesa: Payment.Mpesa
    ): ApiResponse<Unit> {
        println("SUB:: $mpesa")
        return parkingService.paySubscription(mpesa)
    }

}