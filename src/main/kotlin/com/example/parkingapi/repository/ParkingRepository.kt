package com.example.parkingapi.repository

import com.example.parkingapi.model.Parking
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ParkingRepository : CoroutineCrudRepository<Parking, Int> {
    suspend fun findByUtenteId(utenteId: Int): Parking?

    @Modifying
    @Query("delete from parking where utenteid=$1")
    suspend fun removeByUtenteId(utenteId: Int): Int
}