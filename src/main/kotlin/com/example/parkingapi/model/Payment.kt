package com.example.parkingapi.model

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.*
import org.hibernate.validator.constraints.Length
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("Payment")
data class Payment(
    @Id
    @Column
    val id: Int? = null,
    @JsonFormat(pattern = "EEEE dd MMM yyyy hh:mm:ss")
    @Column("entranceTime")
    val entranceTime: LocalDateTime,
    @JsonFormat(pattern = "EEEE dd MMM yyyy hh:mm:ss")
    @Column("exitTime")
    val exitTime: LocalDateTime,
    @Column("timeInParking")
    val timeInParking: String,
    @Column("amountPerHour")
    val amountPerHour: Int = AMOUNT_PER_HOUR,
    @Column("amount")
    val amount: Float,
    @Column("discount")
    val discount: Float,
    @Column("utenteId")
    val utenteId: Int,
) {
    companion object {
        const val AMOUNT_PER_HOUR: Int = 20
    }

    data class Mpesa(
        @field:NotNull @field:Size(max = 10, min = 9)
        @field:Pattern(regexp = "^(84|85)[0-9]+$")
        val phoneNumber: String,
        @field:NotNull @field:Positive val utenteId: Int,
    )

}
