@file:Suppress("SpringDataRepositoryMethodReturnTypeInspection")

package com.example.parkingapi.repository

import com.example.parkingapi.model.Spot
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.stream.Stream

@Repository
interface SpotRepository : CoroutineCrudRepository<Spot, Int> {
    suspend fun existsByUtenteId(utenteId: Int): Boolean
    suspend fun findByUtenteId(utenteId: Int): Spot?
}
