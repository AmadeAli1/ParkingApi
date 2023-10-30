package com.example.parkingapi.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("Subscription")
data class Subscription(
    @Id
    @Column("id")
    val id: Int? = null,
    @Column("divida")
    val divida: Double,
    @Column("paymentDateTime")
    val paymentDateTime: LocalDateTime = LocalDateTime.now(),
    @Column("utenteId")
    val utenteId: Int,
)
