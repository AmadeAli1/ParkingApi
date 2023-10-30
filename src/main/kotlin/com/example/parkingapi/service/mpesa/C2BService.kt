package com.example.parkingapi.service.mpesa

import com.example.parkingapi.service.mpesa.C2BPaymentRequest
import com.example.parkingapi.service.mpesa.PaymentResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface C2BService {
    @Headers("Content-Type: application/json", "Origin: developer.mpesa.vm.co.mz")
    @POST("/ipg/v1x/c2bPayment/singleStage/")
    suspend fun c2bRequest(
        @Header("Authorization") bearerToken: String,
        @Body paymentRequest: C2BPaymentRequest,
    ): Response<PaymentResponse>
}