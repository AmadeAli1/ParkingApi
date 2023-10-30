package com.example.parkingapi.repository

import com.example.parkingapi.model.Payment
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface PaymentRepository : CoroutineCrudRepository<Payment, Int> {

    fun findAllByUtenteId(utenteId: Int): Flow<Payment>

}