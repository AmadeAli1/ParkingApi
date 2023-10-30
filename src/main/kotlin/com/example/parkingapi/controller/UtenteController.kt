package com.example.parkingapi.controller

import com.example.parkingapi.model.Utente
import com.example.parkingapi.service.UtenteService
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/utente")
@RestController
class UtenteController(
    private val utenteService: UtenteService,
) {

    @GetMapping("/login")
    suspend fun login(
        @RequestParam("email") email: String,
        @RequestParam("password") password: String,
    ): Utente {
        return utenteService.signIn(email, password)
    }

    @PostMapping("/signUp")
    suspend fun signUp(@RequestBody @Valid utente: Utente): Utente {
        return utenteService.signUp(utente)
    }

    @GetMapping("/all")
    suspend fun findAll(): Flow<Utente> {
        return utenteService.findAll()
    }

    @GetMapping("/query")
    suspend fun findAll(
        @RequestParam("nome") nome: String,
    ): Flow<Utente> {
        return utenteService.searchByNome(nome)
    }

    @GetMapping("/findOne")
    suspend fun findUtenteById(
        @RequestParam("id") id: Int,
    ): Utente {
        return utenteService.findUtenteById(id)
    }

}
