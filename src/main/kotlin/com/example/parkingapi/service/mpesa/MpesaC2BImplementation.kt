package com.example.parkingapi.service.mpesa

import com.example.parkingapi.exception.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MpesaC2BImplementation(
    private val c2BService: C2BService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun c2bRequest(
        c2BPaymentRequest: C2BPaymentRequest,
    ): ApiResponse<PaymentResponse> {
        val bearerToken =
            """Bearer ${MpesaConfiguration.getBearerToken(MpesaUtils.Mpesa_Api_Key, MpesaUtils.Mpesa_Public_Key)}"""
        val c2bRequest = c2BService.c2bRequest(bearerToken = bearerToken, c2BPaymentRequest)
        return if (c2bRequest.isSuccessful) {
            logger.info("${c2bRequest.body()}")
            ApiResponse(
                message = MpesaResponse.SUCCESS.name, response = c2bRequest.body()!!
            )
        } else {
            logger.info(c2bRequest.message())
            ApiResponse(
                message = MpesaResponse.FAILURE.name,
                response = c2bRequest.errorBody()!!.string().toObject<PaymentResponse>()
            )
        }
    }

}