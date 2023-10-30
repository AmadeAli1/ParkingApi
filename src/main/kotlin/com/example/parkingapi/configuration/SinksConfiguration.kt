package com.example.parkingapi.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Sinks

@Configuration
class SinksConfiguration {

    @Bean
    fun sinkAsString(): Sinks.Many<String> {
        return Sinks.many().multicast().directBestEffort()
    }

    @Bean
    fun json(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerModule(JavaTimeModule())
        return mapper
    }

}