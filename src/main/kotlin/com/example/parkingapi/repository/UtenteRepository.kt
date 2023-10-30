@file:Suppress("SpringDataRepositoryMethodReturnTypeInspection")

package com.example.parkingapi.repository

import com.example.parkingapi.model.Utente
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import kotlinx.coroutines.flow.Flow
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface UtenteRepository : CoroutineCrudRepository<Utente, Int> {

    suspend fun existsByEmailIgnoreCase(email: @Email @NotBlank String): Boolean

    suspend fun findByEmailIgnoreCase(email: String): Utente?

    @Query("select * from utente where upper(nome) like upper(concat($1,'%'))")
    fun findAllByNomeStartingWith(nome: String): Flow<Utente>

}