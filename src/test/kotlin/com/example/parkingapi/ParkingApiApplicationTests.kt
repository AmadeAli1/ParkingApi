package com.example.parkingapi

import com.example.parkingapi.model.Spot
import com.example.parkingapi.repository.SpotRepository
import com.example.parkingapi.repository.UtenteRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

@SpringBootTest
class ParkingApiApplicationTests @Autowired constructor(
    private val spotRepository: SpotRepository,
    private val utenteRepository: UtenteRepository
) {

    //TODO CHAMAR 1 vez antes de executar o programa!
    @Test
    fun registerSpots() {
        runBlocking {
            repeat(20) {
                spotRepository.save(Spot())
            }
            assert(spotRepository.count().toInt() == 20) {
                println("Spots doesn't saved")
            }
        }
    }

    @Test
    fun calculatePayment() {
        val between = Duration.between(LocalDateTime.now().minusHours(1).minusMinutes(30), LocalDateTime.now())
        val result = between.toMinutes() / 60f
        println("Minutes in Parking = ${between.toMinutes()}")
        println("Minutes % 60 = ${if (result.toInt() == 0) 1 else result}")
    }

    @Test
    fun discountIsAvailableTime() {
        val localTime = LocalTime.now()
        val before = localTime.isBefore(LocalTime.of(6, 0))
        val after = localTime.isAfter(LocalTime.of(23, 0))

        println("Before 06:00  = $before")
        println("After 23:00  = $after")
        println("Exists discount? ${before.or(after)}")
    }

    @Test
    fun findAllByNome(){
        runBlocking {

            utenteRepository.findAllByNomeStartingWith("ute").collect{
                println(it)
            }
        }
    }
}
