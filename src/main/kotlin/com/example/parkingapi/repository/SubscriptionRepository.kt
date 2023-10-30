package com.example.parkingapi.repository

import com.example.parkingapi.model.Subscription
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SubscriptionRepository:CoroutineCrudRepository<Subscription,Int> {
}