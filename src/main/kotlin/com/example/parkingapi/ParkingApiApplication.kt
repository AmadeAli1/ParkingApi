package com.example.parkingapi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ParkingApiApplication

fun main(args: Array<String>) {
    runApplication<ParkingApiApplication>(*args)
}
