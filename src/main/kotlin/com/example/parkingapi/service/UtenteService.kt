package com.example.parkingapi.service

import com.example.parkingapi.exception.ApiException
import com.example.parkingapi.model.Utente
import com.example.parkingapi.repository.UtenteRepository
import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UtenteService(
    private val utenteRepository: UtenteRepository,
) {

    suspend fun signUp(utente: Utente): Utente {
        if (utenteRepository.existsByEmailIgnoreCase(utente.email)) {
            throw ApiException("Email em uso, tente outro email.")
        }
        if (utente.paymentType == Utente.PaymentPlan.Monthly) {
            utente.lastSubscription = LocalDateTime.now()
        }
        return utenteRepository.save(utente)
    }

    suspend fun signIn(email: String, password: String): Utente {
        val utente = utenteRepository.findByEmailIgnoreCase(email) ?: throw ApiException("Email invalido!")

        if (!utente.password.equals(password)) {
            throw ApiException("Dados invalidos, verifique email/password")
        }
        return utente
    }

    suspend fun findUtenteById(utenteId: Int): Utente {
        return utenteRepository.findById(utenteId) ?: throw ApiException("Utente credentials not found id {$utenteId}")
    }

    suspend fun findAll(): Flow<Utente> {
        return utenteRepository.findAll()
    }

    suspend fun searchByNome(nome: String): Flow<Utente> {
        return utenteRepository.findAllByNomeStartingWith(nome)
    }


}